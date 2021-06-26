package com.spencercjh.service;

import com.google.common.collect.Lists;
import com.spencercjh.algorithm.BaseDivideAlgorithm;
import com.spencercjh.algorithm.DivideAlgorithm;
import com.spencercjh.model.Player;
import com.spencercjh.model.Position;
import com.spencercjh.model.SquadSetResult;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author spencercjh
 */
@Singleton
@Slf4j
public class SquadService {
  @Inject
  private DivideAlgorithm divideAlgorithm;

  public SquadSetResult divideSquad(List<Player> allPlayers, List<List<Player>> playersInDifferentPositions,
                                    boolean needLossCompensation) {
    final SquadSetResult squadSetResult = new SquadSetResult();
    final Set<Player> alphaSet = squadSetResult.getAlphaSet();
    final Set<Player> bravoSet = squadSetResult.getBravoSet();
    // =0 means equal stats, <0 means stats of alpha is bigger, >0 means stats of bravo is bigger
    double deviation = 0;
    // remove the last overall
    for (int i = 0; i < Position.values().length - 1; i++) {
      final List<Player> playersInDifferentPosition = playersInDifferentPositions.get(i);
      if (playersInDifferentPosition.isEmpty()) {
        log.warn("There are no players in {}.", Position.values()[i]);
        continue;
      }
      final Position position = Position.values()[i];
      final List<List<Player>> dividedPlayers = divideAlgorithm.divide(playersInDifferentPosition, position);
      double statsA = BaseDivideAlgorithm.getTotalStatsOfOneSquad(dividedPlayers.get(0));
      double statsB = BaseDivideAlgorithm.getTotalStatsOfOneSquad(dividedPlayers.get(1));
      boolean result = false;
      // TODO support position-ignored mode
      if (!needLossCompensation) {
        result = alphaSet.addAll(dividedPlayers.get(0)) && bravoSet.addAll(dividedPlayers.get(1));
      } else {
        // In the last position comparison, there is no difference between two team
        if (deviation == 0) {
          result = alphaSet.addAll(dividedPlayers.get(0)) && bravoSet.addAll(dividedPlayers.get(1));
        }
        // In the last position comparison, the Bravo team is stronger, assign stronger players to alpha team
        else if (deviation > 0) {
          if (statsA > statsB) {
            result = alphaSet.addAll(dividedPlayers.get(0)) && bravoSet.addAll(dividedPlayers.get(1));
          } else {
            result = alphaSet.addAll(dividedPlayers.get(1)) && bravoSet.addAll(dividedPlayers.get(0));
          }
        }
        // In the last position comparison, the Alpha team is stronger, assign stronger players to Bravo team
        else if (deviation < 0) {
          if (statsA > statsB) {
            result = alphaSet.addAll(dividedPlayers.get(1)) && bravoSet.addAll(dividedPlayers.get(0));
          } else {
            result = alphaSet.addAll(dividedPlayers.get(0)) && bravoSet.addAll(dividedPlayers.get(1));
          }
        }
        deviation = (statsA - statsB) * -1.0;
      }
      if (!result) {
        log.error("Failed when dividing {}", position);
        throw new RuntimeException();
      }
    }
    // TODO: Multi Position DeDuplication
    if (alphaSet.size() + bravoSet.size() != allPlayers.size()) {
      log.error("Failed when dividing, inconsistent totals: {} + {} != {}, There may be duplicate positions", alphaSet.size(), bravoSet.size(),
        allPlayers.size());
      throw new RuntimeException();
    }
    return squadSetResult;
  }

  public List<List<Player>> groupPlayersByPositions(List<Player> allPlayers) {
    final List<Player> strikers = new ArrayList<>();
    final List<Player> middleFields = new ArrayList<>();
    final List<Player> sides = new ArrayList<>();
    final List<Player> halfbacks = new ArrayList<>();
    final List<Player> goalkeepers = new ArrayList<>();

    for (Player player : allPlayers) {
      if (player.getStrikerStats() > 0) {
        strikers.add(player);
      }
      if (player.getMiddleFieldStats() > 0) {
        middleFields.add(player);
      }
      if (player.getSideStats() > 0) {
        sides.add(player);
      }
      if (player.getHalfbackStats() > 0) {
        halfbacks.add(player);
      }
      if (player.getGoalkeeperStats() > 0) {
        goalkeepers.add(player);
      }
    }
    return Lists.newArrayList(strikers, middleFields, sides, halfbacks, goalkeepers);
  }
}
