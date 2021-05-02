package com.spencercjh.model;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author spencercjh
 */
@Getter
@EqualsAndHashCode
public class SquadListResult {
  private final List<Player> alphaSquad;
  private final List<Player> bravoSquad;

  public SquadListResult(SquadSetResult squadSetResult) {
    alphaSquad = new ArrayList<>(squadSetResult.alphaSet);
    bravoSquad = new ArrayList<>(squadSetResult.bravoSet);
  }

  public Map<String, List<Player>> toMap() {
    return ImmutableMap.<String, List<Player>>builder()
      .put("Alpha", alphaSquad)
      .put("Bravo", bravoSquad)
      .build();
  }
}
