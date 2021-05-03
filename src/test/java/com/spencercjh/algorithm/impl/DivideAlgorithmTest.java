package com.spencercjh.algorithm.impl;

import com.spencercjh.algorithm.DivideAlgorithm;
import com.spencercjh.model.Player;
import com.spencercjh.model.Position;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@MicronautTest
class DivideAlgorithmTest {
  @Inject
  private DivideAlgorithm divideAlgorithm;

  @Test
  void divide() {
    final List<Player> players = new ArrayList<>();
    players.add(Player.builder()
      .name("Player A")
      .strikerStats(8)
      .build());
    players.add(Player.builder()
      .name("Player B")
      .strikerStats(7)
      .build());
    players.add(Player.builder()
      .name("Player C")
      .strikerStats(10)
      .build());
    players.add(Player.builder()
      .name("Player D")
      .strikerStats(6)
      .build());
    players.add(Player.builder()
      .name("Player E")
      .strikerStats(7)
      .build());
    players.add(Player.builder()
      .name("Player F")
      .strikerStats(7)
      .build());
    players.add(Player.builder()
      .name("Player G")
      .strikerStats(8)
      .build());
    players.add(Player.builder()
      .name("Player H")
      .strikerStats(5)
      .build());
    players.add(Player.builder()
      .name("Player I")
      .strikerStats(1)
      .build());
    players.add(Player.builder()
      .name("Player J")
      .strikerStats(7)
      .build());
    final List<java.util.List<Player>> result = divideAlgorithm.divide(players, Position.STRIKER);
    System.out.println("A");
    result.get(0).forEach(System.out::println);
    System.out.println("B");
    result.get(1).forEach(System.out::println);
    assertEquals(5, result.get(0).size());
    assertEquals(5, result.get(1).size());
  }

  @Test
  void shuffleTest() {
    final Set<Player> previous = needShuffle();
    for (int i = 1; i < 5; i++) {
      assertNotEquals(previous, needShuffle());
    }
  }

  private Set<Player> needShuffle() {
    final List<Player> players = new ArrayList<>();
    players.add(Player.builder()
      .name("Player A")
      .strikerStats(8)
      .build());
    players.add(Player.builder()
      .name("Player B")
      .strikerStats(8)
      .build());
    players.add(Player.builder()
      .name("Player C")
      .strikerStats(8)
      .build());
    players.add(Player.builder()
      .name("Player D")
      .strikerStats(8)
      .build());
    players.add(Player.builder()
      .name("Player E")
      .strikerStats(8)
      .build());
    players.add(Player.builder()
      .name("Player F")
      .strikerStats(8)
      .build());
    players.add(Player.builder()
      .name("Player G")
      .strikerStats(8)
      .build());
    players.add(Player.builder()
      .name("Player H")
      .strikerStats(8)
      .build());
    players.add(Player.builder()
      .name("Player I")
      .strikerStats(8)
      .build());
    players.add(Player.builder()
      .name("Player J")
      .strikerStats(8)
      .build());
    final List<List<Player>> result = divideAlgorithm.divide(players, Position.STRIKER);
    System.out.println("A");
    result.get(0).forEach(System.out::println);
    System.out.println("B");
    result.get(1).forEach(System.out::println);
    assertEquals(5, result.get(0).size());
    assertEquals(5, result.get(1).size());
    return new HashSet<>(result.get(0));
  }
}
