package com.spencercjh.service;

import com.google.common.collect.Lists;
import com.spencercjh.model.Player;
import com.spencercjh.model.Position;
import com.spencercjh.model.SquadSetResult;
import com.spencercjh.algorithm.DivideAlgorithm;
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

  public SquadSetResult divideSquad(List<Player> allPlayers, List<List<Player>> playersInDifferentPositions) {
    final SquadSetResult squadSetResult = new SquadSetResult();
    final Set<Player> alphaSet = squadSetResult.getAlphaSet();
    final Set<Player> bravoSet = squadSetResult.getBravoSet();
    // remove the last overall
    for (int i = 0; i < Position.values().length - 1; i++) {
      final List<Player> playersInDifferentPosition = playersInDifferentPositions.get(i);
      final Position position = Position.values()[i];
      final List<List<Player>> dividedPlayers = divideAlgorithm.divide(playersInDifferentPosition, position);
      boolean result = alphaSet.addAll(dividedPlayers.get(0)) && bravoSet.addAll(dividedPlayers.get(1));
      if (!result) {
        log.error("Failed when dividing {}", position);
        throw new RuntimeException();
      }
    }
    // TODO: Multi Position DeDuplication
    if (alphaSet.size() + bravoSet.size() != allPlayers.size()) {
      log.error("Failed when dividing, inconsistent totals: {} + {} != {}", alphaSet.size(), bravoSet.size(),
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
