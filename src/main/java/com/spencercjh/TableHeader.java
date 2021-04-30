package com.spencercjh;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author spencercjh
 */
@Getter
@AllArgsConstructor
public enum TableHeader {
  /**
   * 姓名
   */
  NAME(0),
  /**
   * 前锋
   */
  STRIKER_STATS(1),
  /**
   * 中场
   */
  MIDDLE_FIELD_STATS(2),
  /**
   * 边路
   */
  SIDE_STATS(3),
  /**
   * 中卫
   */
  HALFBACK_STATS(4),
  /**
   * 门将
   */
  GOALKEEPER_STATS(5);

  /**
   * 下标
   */
  private final int index;
}
