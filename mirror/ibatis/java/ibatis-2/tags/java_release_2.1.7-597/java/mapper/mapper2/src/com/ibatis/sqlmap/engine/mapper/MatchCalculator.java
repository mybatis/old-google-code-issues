package com.ibatis.sqlmap.engine.mapper;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class MatchCalculator {

  private static final int SET_BEGIN = 2;
  private static final int SET_END = 5;
  private static final double MAX_VALUE = 1;

  public MatchCalculator() {
  }

  public double calculateMatch (String first, String second) {

    Set firstSet = buildSets(first);
    Set secondSet = buildSets(second);

    double value1 = compareSets(firstSet, secondSet);
    double value2 = compareSets(secondSet, firstSet);
    return (value1 + value2) / 2;
  }

  private Set buildSets(String string) {
    Set set = new HashSet();
    char[] chars = string.toUpperCase().toCharArray();
    for (int i = SET_BEGIN; i <= SET_END; i++) {
      setsOf(i, chars, set);
    }
    return set;
  }

  private void setsOf(int size, char[] chars, Set set) {
    for (int i=0; i < chars.length - size + 1; i++) {
      char[] group = new char[size];
      for (int j=0; j < group.length; j++) {
        group[j] = chars[i+j];
      }
      set.add(new String(group));
    }
  }

  private double compareSets(Set firstSet, Set secondSet) {
    double value = MAX_VALUE;
    double interval = calculateInterval(firstSet, secondSet);
    Iterator i = firstSet.iterator();
    while (i.hasNext()) {
      String group = (String)i.next();
      if (!secondSet.contains(group)) {
        value -= interval;
      }
    }
    return value;
  }

  private double calculateInterval(Set firstSet, Set secondSet) {
    double interval;
    if (firstSet.size() > secondSet.size()) {
      interval = MAX_VALUE / firstSet.size();
    } else {
      interval = MAX_VALUE / secondSet.size();
    }
    return interval;
  }


}
