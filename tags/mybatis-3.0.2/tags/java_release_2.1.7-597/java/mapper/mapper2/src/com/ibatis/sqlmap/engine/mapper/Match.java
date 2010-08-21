package com.ibatis.sqlmap.engine.mapper;

public class Match {

  private String property;
  private String field;
  private double matchScore;

  public Match(String property, String field, double matchScore) {
    this.property = property;
    this.field = field;
    this.matchScore = matchScore;
  }

  public String getProperty() {
    return property;
  }

  public String getField() {
    return field;
  }

  public double getMatchScore() {
    return matchScore;
  }


  public String toString() {
    return property + " => " + field + " ("+matchScore+")";
  }

}
