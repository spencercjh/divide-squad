package com.spencercjh.algorithm;

import com.spencercjh.model.Player;
import com.spencercjh.model.Position;
import lombok.AllArgsConstructor;
import lombok.Data;

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
  static double getStats(Player player, Position position) {
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
        return ((player.getOverallStats() + 1.0) / 2.0);
      default:
        throw new RuntimeException();
    }
  }

  /**
   * divide one list of players to two list
   *
   * @param players  player list
   * @param position position enum
   * @return two list. 0th is alpha, 1st is beta
   */
  List<List<Player>> divide(List<Player> players, Position position);

}
