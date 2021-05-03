package com.spencercjh.algorithm.impl;

import com.spencercjh.algorithm.BaseDivideAlgorithm;
import com.spencercjh.model.Player;
import com.spencercjh.model.Position;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.*;

import static com.spencercjh.algorithm.DivideAlgorithm.getStats;

/**
 * reference: https://blog.csdn.net/crayondeng/article/details/16338639?utm_medium=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7EBlogCommendFromMachineLearnPai2%7Edefault-1.control&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7EBlogCommendFromMachineLearnPai2%7Edefault-1.control
 *
 * @author spencercjh
 */
@Singleton
@Named("EquallyDivide")
@Slf4j
public class EquallyDivideAlgorithm extends BaseDivideAlgorithm {
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

    final PriorityQueue<SquadPlayer> alpha = new PriorityQueue<>(Comparator.comparingInt(SquadPlayer::getStats));
    final PriorityQueue<SquadPlayer> bravo = new PriorityQueue<>(Comparator.comparingInt(SquadPlayer::getStats));

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
    final List<List<SquadPlayer>> shuffleResult = shuffle(new ArrayList<>(alpha), new ArrayList<>(bravo),
      position);
    return mapSquadPlayerToPlayer(players, position, shuffleResult.get(0), shuffleResult.get(1));
  }

  @Override
  protected List<List<SquadPlayer>> shuffle(List<SquadPlayer> alpha, List<SquadPlayer> bravo, Position position) {
    final Map<Integer, List<SquadPlayer>> alphaStatsPlayersMap = new HashMap<>();
    for (SquadPlayer squadPlayer : alpha) {
      final List<SquadPlayer> playersWithSpecialStats = alphaStatsPlayersMap.getOrDefault(squadPlayer.getStats(), new ArrayList<>());
      playersWithSpecialStats.add(squadPlayer);
      alphaStatsPlayersMap.put(squadPlayer.getStats(), playersWithSpecialStats);
    }
    final Map<Integer, List<SquadPlayer>> bravoStatsPlayersMap = new HashMap<>();
    final Map<Integer, Boolean> isBravoPlayersVisited = new HashMap<>();
    for (SquadPlayer squadPlayer : bravo) {
      final List<SquadPlayer> playersWithSpecialStats = bravoStatsPlayersMap.getOrDefault(squadPlayer.getStats(), new ArrayList<>());
      playersWithSpecialStats.add(squadPlayer);
      bravoStatsPlayersMap.put(squadPlayer.getStats(), playersWithSpecialStats);
      isBravoPlayersVisited.put(squadPlayer.getStats(), false);
    }
    final List<List<SquadPlayer>> allSquadPlayers = new ArrayList<>();
    final List<SquadPlayer> newAlpha = new ArrayList<>();
    allSquadPlayers.add(newAlpha);
    final List<SquadPlayer> newBravo = new ArrayList<>();
    allSquadPlayers.add(newBravo);
    for (Map.Entry<Integer, List<SquadPlayer>> entry : alphaStatsPlayersMap.entrySet()) {
      isBravoPlayersVisited.put(entry.getKey(), true);
      final int alphaSquadPlayersWithSpecialStatsCount = entry.getValue().size();
      final List<SquadPlayer> bravoSquadPlayersWithSpecialStats = bravoStatsPlayersMap.getOrDefault(entry.getKey(), new ArrayList<>());
      final int bravoSquadPlayersWithSpecialStatsCount = bravoSquadPlayersWithSpecialStats.size();
      if (bravoSquadPlayersWithSpecialStatsCount != alphaSquadPlayersWithSpecialStatsCount) {
        newAlpha.addAll(entry.getValue());
        newBravo.addAll(bravoSquadPlayersWithSpecialStats);
        log.debug("The amount of two squad players with {} stats isn't same, can't shuffle", entry.getKey());
        continue;
      }
      log.debug("During dividing Position: {}, there are {} × 2 players' stats: {} that are same. They can be shuffled", position,
        alphaSquadPlayersWithSpecialStatsCount, entry.getKey());
      final List<SquadPlayer> toShufflePlayers = new ArrayList<>(entry.getValue());
      toShufflePlayers.addAll(bravoSquadPlayersWithSpecialStats);
      Collections.shuffle(toShufflePlayers);
      for (int i = 0; i < toShufflePlayers.size(); i++) {
        final SquadPlayer toShuffle = toShufflePlayers.get(i);
        if (i < toShufflePlayers.size() / 2) {
          newAlpha.add(toShuffle.setAlpha(true));
        } else {
          newBravo.add(toShuffle.setAlpha(false));
        }
      }
    }
    for (Map.Entry<Integer, Boolean> entry : isBravoPlayersVisited.entrySet()) {
      if (!entry.getValue()) {
        newBravo.addAll(bravoStatsPlayersMap.get(entry.getKey()));
      }
    }
    return allSquadPlayers;
  }
}
