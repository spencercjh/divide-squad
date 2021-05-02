package com.spencercjh.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author spencercjh
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Player {
  @NotBlank
  private String name;
  @Max(value = 10L)
  @Min(value = 0L)
  @NotNull
  private int strikerStats;
  @Max(value = 10L)
  @Min(value = 0L)
  @NotNull
  private int middleFieldStats;
  @Max(value = 10L)
  @Min(value = 0L)
  @NotNull
  private int sideStats;
  @Max(value = 10L)
  @Min(value = 0L)
  @NotNull
  private int halfbackStats;
  @Max(value = 10L)
  @Min(value = 0L)
  @NotNull
  private int goalkeeperStats;
  /**
   * sum of stats / non-zero item count
   */
  private double overallStats;

  public Position position() {
    int[] stats = new int[]{strikerStats, middleFieldStats, sideStats, halfbackStats, goalkeeperStats};
    int max = Integer.MIN_VALUE;
    int ans = -1;
    for (int i = 0; i < stats.length; i++) {
      if (stats[i] > max) {
        max = stats[i];
        ans = i;
      }
    }
    return Position.values()[ans];
  }
}
