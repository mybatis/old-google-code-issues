package com.ibatis.sqlmap.engine.mapper;

import java.util.*;

public class NameMatcher {

  public Map matchNames(String[] properties, String[] fields) {
    Map propertyMap = invertMap(buildCanonicalMap(properties));
    Map fieldMap = invertMap(buildCanonicalMap(fields));

    String[] canonicalProperties = setToStringArray(propertyMap.keySet());
    String[] canonicalFields = setToStringArray(fieldMap.keySet());

    // consider building in both directions
    // consider tracking which fields have already been mapped to avoid duplicate mappings
    List matchList = buildMatchList(canonicalProperties, canonicalFields);

    Map matchedNames = new HashMap();
    Iterator matches = matchList.iterator();
    while(matches.hasNext()) {
      Match match = (Match)matches.next();
      String property = (String) propertyMap.get(match.getProperty());
      String field = (String) fieldMap.get(match.getField());
      matchedNames.put(property, field);
    }
    return matchedNames;
  }

  private Map buildCanonicalMap(String[] properties) {
    return new Canonicalizer().buildCanonicalMap(properties);
  }

  private String[] setToStringArray(Set set) {
    return (String[]) set.toArray(new String[set.size()]);
  }

  private List buildMatchList(String[] canonicalProperties, String[] canonicalFields) {
    List matchList = new LinkedList();
    MatchCalculator calc = new MatchCalculator();
    for (int i = 0; i < canonicalProperties.length; i++) {
      for (int j = 0; j < canonicalFields.length; j++) {
        String prop = canonicalProperties[i];
        String field = canonicalFields[j];
        double score = calc.calculateMatch(prop, field);
        Match match = new Match(prop, field, score);
        matchList.add(match);
      }
    }
    sortMatches(matchList);
    removeDuplicatesAndLowScores (matchList);
    return matchList;
  }

  private void removeDuplicatesAndLowScores(List matchList) {
    Set usedProperties = new HashSet();
    Set usedFields = new HashSet();
    Iterator i = matchList.iterator();
    while (i.hasNext()) {
      Match m = (Match)i.next();
      if (usedProperties.contains(m.getProperty()) || usedFields.contains(m.getField()) || m.getMatchScore() < 0.40) {
        i.remove();
      } else {
        usedProperties.add(m.getProperty());
        usedFields.add(m.getField());
      }
    }

  }

  private void sortMatches(List matchList) {
    Collections.sort(matchList, new Comparator () {
      public int compare(Object o1, Object o2) {
        Match m1 = (Match) o1;
        Match m2 = (Match) o2;
        return m1.getMatchScore() < m2.getMatchScore() ? 1 : m1.getMatchScore() > m2.getMatchScore() ? -1 : 0;
      }
    });
  }

  private Map invertMap(Map original) {
    Map inverse = new HashMap();
    Iterator keys = original.keySet().iterator();
    while (keys.hasNext()) {
      Object key = keys.next();
      Object value = original.get(key);
      inverse.put(value, key);
    }
    return inverse;
  }
}
