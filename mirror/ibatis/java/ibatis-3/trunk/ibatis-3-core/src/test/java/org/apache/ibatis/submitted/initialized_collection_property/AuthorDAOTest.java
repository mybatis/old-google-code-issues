package org.apache.ibatis.submitted.initialized_collection_property;

import org.apache.ibatis.submitted.initialized_collection_property.test.*;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.io.Reader;
import java.sql.Connection;

public class AuthorDAOTest {


  @Test
  public void testGetMessageForEmptyDatabase() throws Exception {
    final String resource = "org/apache/ibatis/submitted/initialized_collection_property/ibatis-config.xml";
    Reader reader = Resources.getResourceAsReader(resource);
    SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);

    SqlSession session = factory.openSession();

    Connection conn = session.getConnection();
    ScriptRunner runner = new ScriptRunner(conn);
    reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/initialized_collection_property/db/create.sql");
    runner.runScript(reader);
    session.close();

    AuthorDAO dao = new AuthorDAO(factory);
    List<Author> authors = dao.getAuthors();
    assertEquals(1, authors.size());
  }

}
