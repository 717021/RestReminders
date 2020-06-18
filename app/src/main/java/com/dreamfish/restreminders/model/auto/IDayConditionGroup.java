package com.dreamfish.restreminders.model.auto;

import java.util.List;

public interface IDayConditionGroup {
  List<DayCondition> getDayConditions();
  void setDayConditions(List<DayCondition> newList);
}
