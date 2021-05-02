package com.spencercjh;

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
}
