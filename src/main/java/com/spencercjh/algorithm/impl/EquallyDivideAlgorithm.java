package com.spencercjh.algorithm.impl;

import com.spencercjh.model.Player;
import com.spencercjh.model.Position;
import com.spencercjh.algorithm.DivideAlgorithm;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import static com.spencercjh.algorithm.DivideAlgorithm.getStats;

/**
 * reference: https://blog.csdn.net/crayondeng/article/details/16338639?utm_medium=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7EBlogCommendFromMachineLearnPai2%7Edefault-1.control&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7EBlogCommendFromMachineLearnPai2%7Edefault-1.control
 *
 * @author spencercjh
 */
@Singleton
@Named("EquallyDivide")
@Slf4j
public class EquallyDivideAlgorithm implements DivideAlgorithm {
  private static final int MAX_STATS_SUM = 1000;

  @Override
  public List<List<Player>> divide(List<Player> players, Position position) {
    final int n = players.size();
    final SquadPlayer[] squadPlayers = new SquadPlayer[n + 1];
    int sum = 0;
    for (int i = 1; i < squadPlayers.length; i++) {
      final Player player = players.get(i - 1);
      squadPlayers[i] = new SquadPlayer(i, getStats(player, position), false);
      sum += squadPlayers[i].getStats();
    }
    final int average = sum / 2;
    final int[][] dp = new int[n + 1][MAX_STATS_SUM + 1];
    for (int i = 1; i <= n; i++) {
      for (int j = 1; j <= average; j++) {
        dp[i][j] = dp[i - 1][j];
        if (squadPlayers[i].getStats() <= j) {
          dp[i][j] = Math.max(dp[i][j], dp[i - 1][j - squadPlayers[i].getStats()] + squadPlayers[i].getStats());
        }
      }
    }
    int remainSpace = average;
    for (int i = n; i >= 1; i--) {
      if (remainSpace >= squadPlayers[i].getStats()) {
        if ((dp[i][remainSpace] - dp[i - 1][remainSpace - squadPlayers[i].getStats()]) == squadPlayers[i].getStats()) {
          squadPlayers[i].setAlpha(true);
          remainSpace = remainSpace - squadPlayers[i].getStats();
        }
      }
    }

    final PriorityQueue<SquadPlayer> alpha = new PriorityQueue<>(Comparator.comparingInt(o -> o.getStats()));
    final PriorityQueue<SquadPlayer> bravo = new PriorityQueue<>(Comparator.comparingInt(o -> o.getStats()));

    for (SquadPlayer squadPlayer : squadPlayers) {
      if (squadPlayer == null) {
        continue;
      }
      if (squadPlayer.isAlpha()) {
        alpha.offer(squadPlayer);
      } else {
        bravo.offer(squadPlayer);
      }
    }

    int countA = alpha.size();
    int countB = bravo.size();

    while (Math.abs(countA - countB) > 1) {
      if (countA > countB) {
        bravo.offer(alpha.poll());
        countA--;
        countB++;
      } else if (countA < countB) {
        alpha.offer(bravo.poll());
        countA++;
        countB--;
      }
    }
    final List<List<Player>> answer = new ArrayList<>(2);
    answer.add(alpha.stream().map(squadPlayer -> players.get(squadPlayer.getNumber() - 1))
      .collect(Collectors.toList()));
    log.debug("The total {} stats of team A: {}", position, alpha.stream()
      .map(SquadPlayer::getStats)
      .mapToInt(Integer::intValue)
      .sum());
    answer.add(bravo.stream().map(squadPlayer -> players.get(squadPlayer.getNumber() - 1))
      .collect(Collectors.toList()));
    log.debug("The total {} stats of team B: {}", position, bravo.stream()
      .map(SquadPlayer::getStats)
      .mapToInt(Integer::intValue)
      .sum());
    return answer;
  }
}
