package com.spencercjh;

import com.spencercjh.algorithm.EquallyDivideAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlgorithmTest {
  @Test
  void bagAlgorithm() {
    EquallyDivideAlgorithm algorithm = new EquallyDivideAlgorithm();
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
    final List<java.util.List<Player>> result = algorithm.divide(players,
      Position.STRIKER);
    System.out.println("A");
    result.get(0).forEach(System.out::println);
    System.out.println("B");
    result.get(1).forEach(System.out::println);
    assertEquals(5, result.get(0).size());
    assertEquals(4, result.get(1).size());
  }
}
