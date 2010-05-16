package com.ibatis.sqlmap.mapper;

import junit.framework.TestCase;
import com.ibatis.sqlmap.engine.mapper.matcher.Canonicalizer;

import java.util.Map;

public class CanonicalizerTest extends TestCase {

  public void testShouldRemoveUnderscoresFromName () {
    Canonicalizer matcher = new Canonicalizer();
    String original = "ACC_FIRST_NAME";
    String expected = "ACCFIRSTNAME";
    Map map = matcher.buildCanonicalMap (new String[]{original});
    String canonical = (String) map.get(original);
    assertEquals(expected, canonical);
  }

  public void testShouldUppercase () {
    Canonicalizer matcher = new Canonicalizer();
    String original = "AccFirstName";
    String expected = "ACCFIRSTNAME";
    Map map = matcher.buildCanonicalMap (new String[]{original});
    String canonical = (String) map.get(original);
    assertEquals(expected, canonical);
  }

  public void testShouldRemovePluralization () {
    Canonicalizer matcher = new Canonicalizer();
    String original = "ACCFIRSTNAMES";
    String expected = "ACCFIRSTNAME";
    Map map = matcher.buildCanonicalMap (new String[]{original});
    String canonical = (String) map.get(original);
    assertEquals(expected, canonical);
  }

  public void testShouldRemovePrefix () {
    Canonicalizer matcher = new Canonicalizer();
    String[] originals = new String[]{"ACC_FIRSTNAME", "ACC_LASTNAME", "ACC_BIRTHDATE", "ACC_LEVEL"};
    String[] expected = new String[]{"FIRSTNAME", "LASTNAME", "BIRTHDATE", "LEVEL"};
    Map map = matcher.buildCanonicalMap (originals);
    for (int i=0; i < originals.length; i++) {
      String canonical = (String) map.get(originals[i]);
      assertEquals(expected[i], canonical);
    }
  }

  public void testShouldRemoveSuffix () {
    Canonicalizer matcher = new Canonicalizer();
    String[] originals = new String[]{"FIRSTNAME_ACC", "LASTNAME_ACC", "BIRTHDATE_ACC", "LEVEL_ACC"};
    String[] expected = new String[]{"FIRSTNAME", "LASTNAME", "BIRTHDATE", "LEVEL"};
    Map map = matcher.buildCanonicalMap (originals);
    for (int i=0; i < originals.length; i++) {
      String canonical = (String) map.get(originals[i]);
      assertEquals(expected[i], canonical);
    }
  }

  public void testShouldRemovePKFK () {
    Canonicalizer matcher = new Canonicalizer();
    String[] originals = new String[]{"PK_FIRSTNAME", "LASTNAME_PK", "BIRTHDATE_FK", "FK_LEVEL"};
    String[] expected = new String[]{"FIRSTNAME", "LASTNAME", "BIRTHDATE", "LEVEL"};
    Map map = matcher.buildCanonicalMap (originals);
    for (int i=0; i < originals.length; i++) {
      String canonical = (String) map.get(originals[i]);
      assertEquals(expected[i], canonical);
    }
  }

  public void testShouldRemoveParentName () {
    Canonicalizer matcher = new Canonicalizer();
    String parentName = "Person";
    String[] originals = new String[]{"Person_ID, FIRSTNAME", "LASTNAME", "BIRTHDATE", "LEVEL_Person"};
    String[] expected = new String[]{"ID, FIRSTNAME", "LASTNAME", "BIRTHDATE", "LEVEL"};
    Map map = matcher.buildCanonicalMap (originals, parentName);
    for (int i=0; i < originals.length; i++) {
      String canonical = (String) map.get(originals[i]);
      assertEquals(expected[i], canonical);
    }
  }

  public void testShouldNotRemovePrefixWhenOnlyColumn () {
    Canonicalizer matcher = new Canonicalizer();
    String[] originals = new String[]{"ACCFIRSTNAME"};
    String[] expected = new String[]{"ACCFIRSTNAME"};
    Map map = matcher.buildCanonicalMap (originals);
    for (int i=0; i < originals.length; i++) {
      String canonical = (String) map.get(originals[i]);
      assertEquals(expected[i], canonical);
    }
  }

  public void testShouldNotRemovePrefixWhenSomePrefixesDoNotMatch () {
    Canonicalizer matcher = new Canonicalizer();
    String[] originals = new String[]{"ACCFIRSTNAME", "ACCLASTNAME", "ACCBIRTHDATE", "XACCLEVEL"};
    String[] expected = new String[]{"ACCFIRSTNAME", "ACCLASTNAME", "ACCBIRTHDATE", "XACCLEVEL"};
    Map map = matcher.buildCanonicalMap (originals);
    for (int i=0; i < originals.length; i++) {
      String canonical = (String) map.get(originals[i]);
      assertEquals(expected[i], canonical);
    }
  }

}
