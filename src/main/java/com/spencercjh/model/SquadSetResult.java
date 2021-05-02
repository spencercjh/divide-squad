package com.spencercjh.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * @author spencercjh
 */
@Getter
@EqualsAndHashCode
public class SquadSetResult {
  Set<Player> alphaSet;
  Set<Player> bravoSet;

  public SquadSetResult() {
    alphaSet = new HashSet<>();
    bravoSet = new HashSet<>();
  }

  public SquadListResult sortPlayersInSquad() {
    final SquadListResult listResult = new SquadListResult(this);
    listResult.getAlphaSquad().sort(Comparator.comparingInt(p -> p.position().getSortWeight()));
    listResult.getBravoSquad().sort(Comparator.comparingInt(p -> p.position().getSortWeight()));
    return listResult;
  }
}
