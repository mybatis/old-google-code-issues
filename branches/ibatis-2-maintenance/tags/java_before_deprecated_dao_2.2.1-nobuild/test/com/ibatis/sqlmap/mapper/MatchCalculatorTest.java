package com.ibatis.sqlmap.mapper;

import junit.framework.TestCase;
import com.ibatis.sqlmap.engine.mapper.matcher.MatchCalculator;

public class MatchCalculatorTest extends TestCase {

  public void testWillNotFindMatchesCorrectlyWithoutMoreContext() {
    assertTrue(isMatch("firstName", "LASTNAME"));
    assertFalse(isMatch("lastName", "ACC_LAST_NAME"));
    assertFalse(isMatch("lastName", "ACC_LAST_NAME_FK"));
  }

  public void testShouldFindMatchesWithinAGivenThreshold() {
    assertTrue(isMatch("firstName", "FIRSTNAME"));
    assertTrue(isMatch("firstName", "ACC_FIRSTNAME"));
    assertTrue(isMatch("firstName", "ACC_FIRST_NAME"));
    assertTrue(isMatch("firstName", "ACC_FIRST_NAME_FK"));
    assertTrue(isMatch("id", "id"));

    assertTrue(isMatch("lastName", "LASTNAME"));
    assertTrue(isMatch("lastName", "ACC_LASTNAME"));

    assertFalse(isMatch("firstName", "ACC_LASTNAME"));
    assertFalse(isMatch("firstName", "ACC_LAST_NAME"));
    assertFalse(isMatch("firstName", "ACC_LAST_NAME_FK"));
    assertFalse(isMatch("id", "PersonId"));
  }

  private boolean isMatch(String first, String second) {
    double matchBar = 0.55;
    return new MatchCalculator().calculateMatch(first, second) >= matchBar;
  }


}
