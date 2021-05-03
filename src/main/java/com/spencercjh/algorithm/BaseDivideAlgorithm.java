package com.spencercjh.algorithm;

import com.spencercjh.model.Player;
import com.spencercjh.model.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author spencercjh
 */
@Slf4j
public abstract class BaseDivideAlgorithm implements DivideAlgorithm {
  protected List<List<Player>> mapSquadPlayerToPlayer(List<Player> allPlayers, Position position,
                                                      List<SquadPlayer> alpha,
                                                      List<SquadPlayer> bravo) {
    final List<List<Player>> answer = new ArrayList<>(2);
    answer.add(alpha.stream().map(squadPlayer -> allPlayers.get(squadPlayer.getNumber() - 1))
      .collect(Collectors.toList()));
    log.debug("The total {} stats of team A: {}", position, alpha.stream()
      .map(SquadPlayer::getStats)
      .mapToInt(Integer::intValue)
      .sum());
    answer.add(bravo.stream().map(squadPlayer -> allPlayers.get(squadPlayer.getNumber() - 1))
      .collect(Collectors.toList()));
    log.debug("The total {} stats of team B: {}", position, bravo.stream()
      .map(SquadPlayer::getStats)
      .mapToInt(Integer::intValue)
      .sum());
    return answer;
  }


  /**
   * shuffle players of the position in two squad
   *
   * @param alpha    alpha squad
   * @param bravo    bravo squad
   * @param position position
   * @return two list. 0th is alpha, 1st is beta
   */
  abstract protected List<List<SquadPlayer>> shuffle(List<SquadPlayer> alpha, List<SquadPlayer> bravo, Position position);

  @Data
  @AllArgsConstructor
  @Accessors(chain = true)
  public static
  class SquadPlayer {
    /**
     * begin from 1
     */
    int number;
    int stats;
    /**
     * alpha true, bravo false
     */
    boolean alpha;
  }
}
