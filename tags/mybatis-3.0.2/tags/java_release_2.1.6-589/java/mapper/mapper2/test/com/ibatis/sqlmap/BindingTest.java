package com.ibatis.sqlmap;

import testdomain.DocumentMapper;
import testdomain.Document;

import java.util.List;
import java.util.Map;

import com.ibatis.common.util.PaginatedList;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;

public class BindingTest  extends BaseSqlMapTest {


   protected void setUp() throws Exception {
    initSqlMap("com/ibatis/sqlmap/maps/SqlMapConfig.xml", null);
    initScript("scripts/docs-init.sql");
  }

  private Object getMapper(Class c) {
    return ((SqlMapClientImpl)sqlMap).getMapper(c);
  }

  public void testSelectListStatement () throws Exception {

    DocumentMapper dm = (DocumentMapper) getMapper(DocumentMapper.class);
    List list = dm.getDocuments();

    assertNotNull(list);
    assertEquals(6, list.size());
  }

  public void testSelectSubListStatement () throws Exception {

    DocumentMapper dm = (DocumentMapper) getMapper(DocumentMapper.class);

    List list = dm.getDocuments(null, 2, 2);

    assertNotNull(list);
    assertEquals(2, list.size());
  }

  public void testSelectPaginatedListStatement () throws Exception {

    DocumentMapper dm = (DocumentMapper) getMapper(DocumentMapper.class);

    PaginatedList list = dm.getDocuments(null, 3);

    assertNotNull(list);
    assertEquals(3, list.size());
  }

  public void testSelectMapStatement () throws Exception {

    DocumentMapper dm = (DocumentMapper) getMapper(DocumentMapper.class);

    Map map = dm.getDocuments(null, "id");

    assertNotNull(map);
    assertEquals(6, map.size());
  }

  public void testSelectMapWithValueStatement () throws Exception {

    DocumentMapper dm = (DocumentMapper) getMapper(DocumentMapper.class);

    Map map = dm.getDocuments(null, "id", "title");

    assertNotNull(map);
    assertEquals(6, map.size());
  }

  public void testSelectObjectStatement () throws Exception {

    DocumentMapper dm = (DocumentMapper) getMapper(DocumentMapper.class);

    Document doc = dm.getDocument(1);

    assertNotNull(doc);
    assertEquals(1, doc.getId());
  }

  public void testInsertStatement () throws Exception {

    DocumentMapper dm = (DocumentMapper) getMapper(DocumentMapper.class);

    Document doc = dm.getDocument(1);
    doc.setId(99);
    dm.insertDocument(doc);
    doc = dm.getDocument(99);

    assertNotNull(doc);
    assertEquals(99, doc.getId());
  }

  public void testUpdateStatement () throws Exception {

    DocumentMapper dm = (DocumentMapper) getMapper(DocumentMapper.class);

    Document doc = dm.getDocument(1);
    doc.setTitle("blah");
    dm.updateDocument(doc);
    doc = dm.getDocument(1);

    assertNotNull(doc);
    assertEquals("blah", doc.getTitle());
  }

  public void testDeleteStatement () throws Exception {

    DocumentMapper dm = (DocumentMapper) getMapper(DocumentMapper.class);

    Document doc = dm.getDocument(1);
    dm.deleteDocument(doc);
    doc = dm.getDocument(1);

    assertNull(doc);
  }

}