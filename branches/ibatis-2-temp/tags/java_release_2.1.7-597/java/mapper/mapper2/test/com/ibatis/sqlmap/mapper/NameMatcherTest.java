package com.ibatis.sqlmap.mapper;

import com.ibatis.sqlmap.engine.mapper.NameMatcher;
import junit.framework.TestCase;

import java.util.Map;

public class NameMatcherTest extends TestCase {

  public void testShouldCorrectlyMatchMostOfTheseFairlyComplexNamePairs() {
    String[] properties = new String[]{"id", "username", "orderDate", "shipAddress1", "shipAddress2", "shipCity", "shipState", "shipZip", "shipCountry", "billAddress1", "billAddress2", "billCity", "billState", "billZip", "billCountry", "courier", "totalPrice", "billToFirstName", "billToLastName", "shipToFirstName", "shipToLastName", "creditCard", "expiryDate", "cardType", "locale", "status"};
    String[] fields = new String[]{"orderid", "userid", "orderdate", "shipaddr1", "shipaddr2", "shipcity", "shipstate", "shipzip", "shipcountry", "billaddr1", "billaddr2", "billcity", "billstate", "billzip", "billcountry", "courier", "totalprice", "billtofirstname", "billtolastname", "shiptofirstname", "shiptolastname", "creditcard", "exprdate", "cardtype", "locale"};
    Map map = new NameMatcher().matchNames(fields, properties);

    // unmatched
    assertNull(map.get("orderid"));

    // match collision, userid matches id better than orderid
    assertEquals("id", map.get("userid"));

    // good matches
    assertEquals("cardType", map.get("cardtype"));
    assertEquals("creditCard", map.get("creditcard"));
    assertEquals("expiryDate", map.get("exprdate"));

    assertEquals("totalPrice", map.get("totalprice"));
    assertEquals("courier", map.get("courier"));
    assertEquals("orderDate", map.get("orderdate"));
    assertEquals("locale", map.get("locale"));

    assertEquals("billToFirstName", map.get("billtofirstname"));
    assertEquals("billToLastName", map.get("billtolastname"));
    assertEquals("billAddress1", map.get("billaddr1"));
    assertEquals("billAddress2", map.get("billaddr2"));
    assertEquals("billCity", map.get("billcity"));
    assertEquals("billState", map.get("billstate"));
    assertEquals("billCountry", map.get("billcountry"));
    assertEquals("billZip", map.get("billzip"));

    assertEquals("shipToFirstName", map.get("shiptofirstname"));
    assertEquals("shipToLastName", map.get("shiptolastname"));
    assertEquals("shipAddress1", map.get("shipaddr1"));
    assertEquals("shipAddress2", map.get("shipaddr2"));
    assertEquals("shipCountry", map.get("shipcountry"));
    assertEquals("shipZip", map.get("shipzip"));
    assertEquals("shipCity", map.get("shipcity"));
    assertEquals("shipState", map.get("shipstate"));
  }

}


