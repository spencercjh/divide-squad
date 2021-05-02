package com.spencercjh.algorithm.impl;

import com.spencercjh.model.Player;
import com.spencercjh.model.Position;
import com.spencercjh.algorithm.DivideAlgorithm;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class DivideAlgorithmTest {
  @Inject
  private DivideAlgorithm divideAlgorithm;

  @Test
  void divide() {
    List<Player> players = new ArrayList<>();
    players.add(Player.builder()
      .name("球员A")
      .strikerStats(8)
      .build());
    players.add(Player.builder()
      .name("球员B")
      .strikerStats(7)
      .build());
    players.add(Player.builder()
      .name("球员C")
      .strikerStats(10)
      .build());
    players.add(Player.builder()
      .name("球员D")
      .strikerStats(6)
      .build());
    players.add(Player.builder()
      .name("球员E")
      .strikerStats(7)
      .build());
    players.add(Player.builder()
      .name("球员F")
      .strikerStats(7)
      .build());
    players.add(Player.builder()
      .name("球员G")
      .strikerStats(8)
      .build());
    players.add(Player.builder()
      .name("球员H")
      .strikerStats(5)
      .build());
    players.add(Player.builder()
      .name("球员I")
      .strikerStats(1)
      .build());
    final List<java.util.List<Player>> result = divideAlgorithm.divide(players, Position.STRIKER);
    System.out.println("A");
    result.get(0).forEach(System.out::println);
    System.out.println("B");
    result.get(1).forEach(System.out::println);
    assertEquals(5, result.get(0).size());
    assertEquals(4, result.get(1).size());
  }
}
