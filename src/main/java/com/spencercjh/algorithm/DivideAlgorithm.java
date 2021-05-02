package com.spencercjh.algorithm;

import com.spencercjh.Player;
import com.spencercjh.Position;

import java.util.List;

/**
 * @author spencercjh
 */
public interface DivideAlgorithm {
  /**
   * Take out the corresponding stats according to the position
   *
   * @param player   player
   * @param position position enum
   * @return round down stats
   */
  static int getStats(Player player, Position position) {
    switch (position) {
      case STRIKER:
        return player.getStrikerStats();
      case MIDDLE_FIELD:
        return player.getMiddleFieldStats();
      case SIDE:
        return player.getSideStats();
      case HALFBACK:
        return player.getHalfbackStats();
      case GOALKEEPER:
        return player.getGoalkeeperStats();
      case OVERALL:
        return (int) ((player.getOverallStats() + 1) / 2);
      default:
        throw new RuntimeException();
    }
  }

  /**
   * @param players  player list
   * @param position position enum
   * @return two list. 0th is alpha, 1st is beta
   */
  List<List<Player>> divide(List<Player> players, Position position);

  class SquadPlayer {
    /**
     * begin from 1
     */
    int number;
    int stats;
    /**
     * alpha true, beta false
     */
    boolean team = false;
  }

}
