package com.ibatis.sqlmap.engine.mapper;

import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class Canonicalizer {

  public Map buildCanonicalMap(String[] originals) {
    return buildCanonicalMap(originals, null);
  }
  public Map buildCanonicalMap(String[] originals, String parentName) {
    Map map = new HashMap();
    for (int i=0; i < originals.length; i++) {
      map.put(originals[i], originals[i]);
    }
    upperCase(map);
    removeUnderscores(map);
    removePKFK(map);
    removePrefixes(map);
    removeSuffixes(map);
    removePluralization(map);
    removeParentName(map, parentName);
    return map;
  }

  private void removeParentName(Map map, String parentName) {
    if (parentName != null) {
      parentName = parentName.toUpperCase();
      Iterator i = map.keySet().iterator();
      while (i.hasNext()) {
        String original = (String) i.next();
        String canonical = (String) map.get(original);
        if (canonical.startsWith(parentName)) {
          map.put(original, canonical.substring(parentName.length()));
        }
        if (canonical.endsWith(parentName)) {
          map.put(original, canonical.substring(0, canonical.length() - parentName.length()));
        }
      }
    }
  }

  private void upperCase(Map map) {
    Iterator i = map.keySet().iterator();
    while (i.hasNext()) {
      String original = (String) i.next();
      String canonical = (String) map.get(original);
      map.put(original, canonical.toUpperCase());
    }
  }

  private void removePKFK(Map map) {
    Iterator i = map.keySet().iterator();
    while (i.hasNext()) {
      String original = (String) i.next();
      String canonical = (String) map.get(original);
      if (canonical.startsWith("PK")) {
        map.put(original, canonical.substring(2));
      } else if (canonical.startsWith("FK")) {
        map.put(original, canonical.substring(2));
      }
      if (canonical.endsWith("PK")) {
        map.put(original, canonical.substring(0, canonical.length() - 2));
      } else if (canonical.endsWith("FK")) {
        map.put(original, canonical.substring(0, canonical.length() - 2));
      }
    }
  }

  private void removePrefixes(Map map) {
    int prefix = findPrefixLength(map);

    if (prefix > 1) {
      Iterator i = map.keySet().iterator();
      while (i.hasNext()) {
        String original = (String) i.next();
        String canonical = (String) map.get(original);
        map.put(original, canonical.substring(prefix));
      }
    }
  }

  private void removeSuffixes(Map map) {
    int suffix = findSuffixLength(map);

    if (suffix > 1) {
      Iterator i = map.keySet().iterator();
      while (i.hasNext()) {
        String original = (String) i.next();
        String canonical = (String) map.get(original);
        map.put(original, canonical.substring(0, canonical.length() - suffix));
      }
    }
  }

  private int findPrefixLength(Map map) {
    String[] originals = (String[])map.keySet().toArray(new String[map.keySet().size()]);
    char[] samples = ((String) map.get(findShortestString(originals))).toCharArray();
    int prefix = 0;
    for (int i=0; i < samples.length; i++) {
      for (int j=0; j < originals.length; j++) {
        String original = originals[j];
        String canonical = (String) map.get(original);
        if (canonical.charAt(prefix) != samples[i]) {
          return prefix;
        }
      }
      prefix++;
    }
    if (prefix == samples.length) {
      prefix = 0;
    }
    return prefix;
  }

  private int findSuffixLength(Map map) {
    String[] originals = (String[])map.keySet().toArray(new String[map.keySet().size()]);
    char[] samples = ((String) map.get(findShortestString(originals))).toCharArray();
    int suffix = 0;
    for (int i=0; i < samples.length; i++) {
      for (int j=0; j < originals.length; j++) {
        String original = originals[j];
        String canonical = (String) map.get(original);
        if (canonical.charAt(canonical.length() - suffix - 1) != samples[samples.length - i - 1]) {
          return suffix;
        }
      }
      suffix++;
    }
    if (suffix == samples.length) {
      suffix = 0;
    }
    return suffix;
  }

  private String findShortestString(String[] originals) {
    String shortest = originals[0];
    for (int i=0; i < originals.length; i++) {
      if (originals[i].length() < shortest.length()) {
        shortest = originals[i];
      }
    }
    return shortest;
  }

  private void removeUnderscores (Map map) {
    Iterator i = map.keySet().iterator();
    while (i.hasNext()) {
      String original = (String) i.next();
      String canonical = (String) map.get(original);
      map.put(original, removeUnderscoresFromString(canonical));
    }
  }

  private void removePluralization (Map map) {
    Iterator i = map.keySet().iterator();
    while (i.hasNext()) {
      String original = (String) i.next();
      String canonical = (String) map.get(original);
      if (canonical.endsWith("S") || canonical.endsWith("s")) {
        map.put(original, canonical.substring(0, canonical.length() - 1));
      }
    }
  }

  private String removeUnderscoresFromString (String original) {
    StringBuffer canonical = new StringBuffer();
    StringTokenizer parser = new StringTokenizer (original, "_", false);
    while(parser.hasMoreTokens()) {
      canonical.append(parser.nextToken());
    }
    return canonical.toString();
  }

}
