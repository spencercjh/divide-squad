package com.spencercjh.model;

import lombok.Getter;

/**
 * @author spencercjh
 */
@SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
@Getter
public enum Position {
  STRIKER(0),
  MIDDLE_FIELD(1),
  SIDE(2),
  HALFBACK(3),
  GOALKEEPER(4),
  OVERALL(5);

  private final int sortWeight;

  Position(int sortWeight) {
    this.sortWeight = sortWeight;
  }


}
