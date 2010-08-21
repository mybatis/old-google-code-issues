package org.apache.ibatis.submitted.substitution_in_annots;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.hsqldb.jdbcDriver;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SubstitutionInAnnotsTest {

  protected static SqlSessionFactory sqlSessionFactory;

  @BeforeClass
  public static void setUp() throws Exception {
    DriverManager.registerDriver(new jdbcDriver());
   	Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:annots", "sa", "");
   	Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/substitution_in_annots/CreateDB.sql");
    ScriptRunner runner = new ScriptRunner(c);
    runner.setLogWriter(null);
    runner.setErrorLogWriter(new PrintWriter(System.err));
    runner.runScript(reader);
    c.commit();
    reader.close();

    Configuration configuration = new Configuration();
    Environment environment = new Environment("test", new JdbcTransactionFactory(), new UnpooledDataSource(jdbcDriver.class.getName(), "jdbc:hsqldb:mem:annots", null));
    configuration.setEnvironment(environment);
    
    configuration.addMapper(SubstitutionInAnnotsMapper.class);
    
    sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
  }

  @Test
  public void testSubstitutionWithXml() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {
      SubstitutionInAnnotsMapper mapper = sqlSession.getMapper(SubstitutionInAnnotsMapper.class);
      assertEquals("Barney", mapper.getPersonNameByIdWithXml(4));
    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void testSubstitutionWithAnnotsValue() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {
      SubstitutionInAnnotsMapper mapper = sqlSession.getMapper(SubstitutionInAnnotsMapper.class);
      assertEquals("Barney", mapper.getPersonNameByIdWithAnnotsValue(4));
    } finally {
      sqlSession.close();
    }
  }
  
  @Test
  public void testSubstitutionWithAnnotsParameter() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {
      SubstitutionInAnnotsMapper mapper = sqlSession.getMapper(SubstitutionInAnnotsMapper.class);
      assertEquals("Barney", mapper.getPersonNameByIdWithAnnotsParameter(4));
    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void testSubstitutionWithAnnotsParamAnnot() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {
      SubstitutionInAnnotsMapper mapper = sqlSession.getMapper(SubstitutionInAnnotsMapper.class);
      assertEquals("Barney", mapper.getPersonNameByIdWithAnnotsParamAnnot(4));
    } finally {
      sqlSession.close();
    }
  }

}
