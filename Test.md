We appreciate that uses fill issues with bug reports, feature suggestions or new ideas. This is in fact the basis of the evoution of MyBatis.

To facilitate the adoption of your idea or to demonstrate a failure we would like to ask you to provide unit tests. A test will make life easier for everyone, it will be easier for you to explain the idea or problem and for team members to understand it and take an action.

# Base Unit Test #

Even if you are not familiarized with unit testing you will see that coding a unit test is an easy task.

MyBatis provides a sample base test that serves as a basis for your test. Check it out from here http://mybatis.googlecode.com/svn/trunk/src/test/java/org/apache/ibatis/submitted/basetest/

This test is quite simple, it is composed by the following files:
  * A JUnit test program called BaseTest
  * A POJO called User
  * A Mapper interface called Mapper
  * A mybatis-config file called mybatis-config.xml
  * A mapper XML file called Mapper.xml
  * A database script called CreateDB.sql

This is how it works.
  * BaseTest.setUp() method builds an SqlSessionFactory that uses an in-memory HSQLDB database:
```
Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/basetest/mybatis-config.xml");
sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
reader.close();
```
  * Then it populates the database with the content of CreateDB.sql file:
```
SqlSession session = sqlSessionFactory.openSession();
Connection conn = session.getConnection();
reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/basetest/CreateDB.sql");
ScriptRunner runner = new ScriptRunner(conn);
runner.setErrorLogWriter(null);
runner.runScript(reader);
reader.close();
session.close();
```
  * BaseTest.shouldGetAUser() gets a mapper and uses it to retrieve a User. Then it calls JUnit's method assertEquals to check that the returned data is what was expected:
```
@Test
public void shouldGetAUser() {
	SqlSession sqlSession = sqlSessionFactory.openSession();
	try {
		Mapper mapper = sqlSession.getMapper(Mapper.class);
		User user = mapper.getUser(1);
		Assert.assertEquals("User1", user.getName());
	} finally {
		sqlSession.close();
	}
}
```

# Code your own Test #

Taking base test as a basis these are the modifications you should do to build your own test:
  * Change package name from org.apache.ibatis.submitted.basetest to org.apache.ibatis.submitted.xxx where xxx is your test name
  * Note that you should also change String literals that hold the package in BaseTest#setUp (twice), in mybatis-config and in Mapper.xml.
  * Change database name both in BaseTest and mybatis-config.xml from jdbc:hsqldb:mem:basetest to jdbc:hsqldb:mem:xxx where xxx is your test name.
  * Run the test. It should finish OK.
  * Add your own code.
  * Once you are done, create a patch with all your changes (including the test) and attach it to an issue.