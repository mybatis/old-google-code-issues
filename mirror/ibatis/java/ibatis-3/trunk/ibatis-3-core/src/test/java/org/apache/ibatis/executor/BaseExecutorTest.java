package org.apache.ibatis.executor;

import domain.blog.*;
import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import static org.junit.Assert.*;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.*;

public abstract class BaseExecutorTest extends BaseDataTest {
  private final Configuration config;

  protected BaseExecutorTest() {
    config = new Configuration();
    config.setEnhancementEnabled(true);
    config.setLazyLoadingEnabled(true);
    config.setUseGeneratedKeys(false);
    config.setMultipleResultSetsEnabled(true);
    config.setUseColumnLabel(true);
    config.setDefaultStatementTimeout(5000);
  }

  @Test
  public void shouldInsertNewAuthor() throws Exception {
    DataSource ds = createBlogDataSource();
    Connection connection = ds.getConnection();
    Executor executor = createExecutor(new JdbcTransaction(connection, false));
    Author author = new Author(99, "someone", "******", "someone@apache.org", null, Section.NEWS);
    MappedStatement insertStatement = ExecutorTestHelper.prepareInsertAuthorMappedStatement(config);
    MappedStatement selectStatement = ExecutorTestHelper.prepareSelectOneAuthorMappedStatement(config);
    int rows = executor.update(insertStatement, author);
    List<Author> authors = executor.query(selectStatement, 99, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT, Executor.NO_RESULT_HANDLER);
    executor.flushStatements();
    executor.rollback(true);
    assertEquals(1, authors.size());
    assertEquals(author.toString(), authors.get(0).toString());
    assertTrue(1 == rows || BatchExecutor.BATCH_UPDATE_RETURN_VALUE == rows);
  }

  @Test
  public void shouldSelectAllAuthorsAutoMapped() throws Exception {
    DataSource ds = createBlogDataSource();
    Connection connection = ds.getConnection();
    Executor executor = createExecutor(new JdbcTransaction(connection, false));
    MappedStatement selectStatement = ExecutorTestHelper.prepareSelectAllAuthorsAutoMappedStatement(config);
    List<Author> authors = executor.query(selectStatement, null, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT, Executor.NO_RESULT_HANDLER);
    assertEquals(2, authors.size());
    Author author = authors.get(0);
    // id,username, password, email, bio, favourite_section
    // (101,'jim','********','jim@ibatis.apache.org','','NEWS');
    assertEquals(101, author.getId());
    assertEquals("jim", author.getUsername());
    assertEquals("jim@ibatis.apache.org", author.getEmail());
    assertEquals("", author.getBio());
    assertEquals(Section.NEWS, author.getFavouriteSection());
  }

  @Test
  public void shouldInsertNewAuthorWithAutoKey() throws Exception {
    DataSource ds = createBlogDataSource();
    Connection connection = ds.getConnection();
    Executor executor = createExecutor(new JdbcTransaction(connection, false));
    Author author = new Author(-1, "someone", "******", "someone@apache.org", null, Section.NEWS);
    MappedStatement insertStatement = ExecutorTestHelper.prepareInsertAuthorMappedStatementWithAutoKey(config);
    MappedStatement selectStatement = ExecutorTestHelper.prepareSelectOneAuthorMappedStatement(config);
    int rows = executor.update(insertStatement, author);
    assertTrue(rows > 0 || rows == BatchExecutor.BATCH_UPDATE_RETURN_VALUE);
    if (rows == BatchExecutor.BATCH_UPDATE_RETURN_VALUE) {
      executor.flushStatements();
    }
    assertTrue(-1 != author.getId());
    if (author.getId() != BatchExecutor.BATCH_UPDATE_RETURN_VALUE) {
      List<Author> authors = executor.query(selectStatement, author.getId(), Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT, Executor.NO_RESULT_HANDLER);
      executor.rollback(true);
      assertEquals(1, authors.size());
      assertEquals(author.toString(), authors.get(0).toString());
      assertTrue(author.getId() >= 10000);
    }
  }

  @Test
  public void shouldInsertNewAuthorByProc() throws Exception {
    DataSource ds = createBlogDataSource();
    Connection connection = ds.getConnection();
    Executor executor = createExecutor(new JdbcTransaction(connection, false));
    Author author = new Author(97, "someone", "******", "someone@apache.org", null, null);
    MappedStatement insertStatement = ExecutorTestHelper.prepareInsertAuthorProc(config);
    MappedStatement selectStatement = ExecutorTestHelper.prepareSelectOneAuthorMappedStatement(config);
    int rows = executor.update(insertStatement, author);
    List<Author> authors = executor.query(selectStatement, 97, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT, Executor.NO_RESULT_HANDLER);
    executor.flushStatements();
    executor.rollback(true);
    assertEquals(1, authors.size());
    assertEquals(author.toString(), authors.get(0).toString());
  }

  @Test
  public void shouldInsertNewAuthorUsingSimpleNonPreparedStatements() throws Exception {
    DataSource ds = createBlogDataSource();
    Connection connection = ds.getConnection();
    Executor executor = createExecutor(new JdbcTransaction(connection, false));
    Author author = new Author(99, "someone", "******", "someone@apache.org", null, null);
    MappedStatement insertStatement = ExecutorTestHelper.createInsertAuthorWithIDof99MappedStatement(config);
    MappedStatement selectStatement = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(config);
    int rows = executor.update(insertStatement, null);
    List<Author> authors = executor.query(selectStatement, 99, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT, Executor.NO_RESULT_HANDLER);
    executor.flushStatements();
    executor.rollback(true);
    assertEquals(1, authors.size());
    assertEquals(author.toString(), authors.get(0).toString());
    assertTrue(1 == rows || BatchExecutor.BATCH_UPDATE_RETURN_VALUE == rows);
  }

  @Test
  public void shouldUpdateAuthor() throws Exception {
    DataSource ds = createBlogDataSource();
    Connection connection = ds.getConnection();
    Executor executor = createExecutor(new JdbcTransaction(connection, false));
    Author author = new Author(101, "someone", "******", "someone@apache.org", null, Section.NEWS);
    MappedStatement updateStatement = ExecutorTestHelper.prepareUpdateAuthorMappedStatement(config);
    MappedStatement selectStatement = ExecutorTestHelper.prepareSelectOneAuthorMappedStatement(config);
    int rows = executor.update(updateStatement, author);
    List<Author> authors = executor.query(selectStatement, 101, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT, Executor.NO_RESULT_HANDLER);
    executor.flushStatements();
    executor.rollback(true);
    assertEquals(1, authors.size());
    assertEquals(author.toString(), authors.get(0).toString());
    assertTrue(1 == rows || BatchExecutor.BATCH_UPDATE_RETURN_VALUE == rows);
  }

  @Test
  public void shouldDeleteAuthor() throws Exception {
    DataSource ds = createBlogDataSource();
    Connection connection = ds.getConnection();
    Executor executor = createExecutor(new JdbcTransaction(connection, false));
    Author author = new Author(101, null, null, null, null, null);
    MappedStatement deleteStatement = ExecutorTestHelper.prepareDeleteAuthorMappedStatement(config);
    MappedStatement selectStatement = ExecutorTestHelper.prepareSelectOneAuthorMappedStatement(config);
    int rows = executor.update(deleteStatement, author);
    List<Author> authors = executor.query(selectStatement, 101, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT, Executor.NO_RESULT_HANDLER);
    executor.flushStatements();
    executor.rollback(true);
    assertEquals(0, authors.size());
    assertTrue(1 == rows || BatchExecutor.BATCH_UPDATE_RETURN_VALUE == rows);
  }

  @Test
  public void shouldSelectDiscriminatedProduct() throws Exception {
    DataSource ds = createJPetstoreDataSource();
    Connection connection = ds.getConnection();
    Executor executor = createExecutor(new JdbcTransaction(connection, false));
    MappedStatement selectStatement = ExecutorTestHelper.prepareSelectDiscriminatedProduct(config);
    List<Map> products = executor.query(selectStatement, null, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT, Executor.NO_RESULT_HANDLER);
    connection.rollback();
    assertEquals(16, products.size());
    for (Map m : products) {
      if ("REPTILES".equals(m.get("category"))) {
        assertNull(m.get("name"));
      } else {
        assertNotNull(m.get("name"));
      }
    }
  }

  @Test
  public void shouldSelect10DiscriminatedProducts() throws Exception {
    DataSource ds = createJPetstoreDataSource();
    Connection connection = ds.getConnection();
    Executor executor = createExecutor(new JdbcTransaction(connection, false));
    MappedStatement selectStatement = ExecutorTestHelper.prepareSelectDiscriminatedProduct(config);
    List<Map> products = executor.query(selectStatement, null, 4, 10, Executor.NO_RESULT_HANDLER);
    connection.rollback();
    assertEquals(10, products.size());
    for (Map m : products) {
      if ("REPTILES".equals(m.get("category"))) {
        assertNull(m.get("name"));
      } else {
        assertNotNull(m.get("name"));
      }
    }
  }

  @Test
  public void shouldSelectTwoSetsOfAuthorsViaProc() throws Exception {
    DataSource ds = createBlogDataSource();
    Connection connection = ds.getConnection();
    connection.setAutoCommit(false);
    Executor executor = createExecutor(new JdbcTransaction(connection, false));
    MappedStatement selectStatement = ExecutorTestHelper.prepareSelectTwoSetsOfAuthorsProc(config);
    List<List> authorSets = executor.query(selectStatement, new HashMap() {
      {
        put("id1", 101);
        put("id2", 102);
      }
    }, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT, Executor.NO_RESULT_HANDLER);
    connection.rollback();
    assertEquals(2, authorSets.size());
    for (List authors : authorSets) {
      assertEquals(2, authors.size());
      for (Object author : authors) {
        assertTrue(author instanceof Author);
      }
    }
  }

  @Test
  public void shouldSelectAuthorViaOutParams() throws Exception {
    DataSource ds = createBlogDataSource();
    Connection connection = ds.getConnection();
    connection.setAutoCommit(false);
    Executor executor = createExecutor(new JdbcTransaction(connection, false));
    MappedStatement selectStatement = ExecutorTestHelper.prepareSelectAuthorViaOutParams(config);
    Author author = new Author(102, null, null, null, null, null);
    executor.query(selectStatement, author, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT, Executor.NO_RESULT_HANDLER);
    connection.rollback();

    assertEquals("sally", author.getUsername());
    assertEquals("********", author.getPassword());
    assertEquals("sally@ibatis.apache.org", author.getEmail());
    assertEquals(null, author.getBio());
  }

  @Test
  public void shouldFetchPostsForBlog() throws Exception {
    DataSource ds = createBlogDataSource();
    Connection connection = ds.getConnection();
    Executor executor = createExecutor(new JdbcTransaction(connection, false));
    MappedStatement selectBlog = ExecutorTestHelper.prepareComplexSelectBlogMappedStatement(config);
    MappedStatement selectPosts = ExecutorTestHelper.prepareSelectPostsForBlogMappedStatement(config);
    config.addMappedStatement(selectBlog);
    config.addMappedStatement(selectPosts);
    List<Post> posts = executor.query(selectPosts, 1, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT, Executor.NO_RESULT_HANDLER);
    executor.flushStatements();
    assertEquals(2, posts.size());
    assertNotNull(posts.get(1).getBlog());
    assertEquals(1, posts.get(1).getBlog().getId());
    executor.rollback(true);
  }

  @Test
  public void shouldFetchOneOrphanedPostWithNoBlog() throws Exception {
    DataSource ds = createBlogDataSource();
    Connection connection = ds.getConnection();
    Executor executor = createExecutor(new JdbcTransaction(connection, false));
    MappedStatement selectBlog = ExecutorTestHelper.prepareComplexSelectBlogMappedStatement(config);
    MappedStatement selectPost = ExecutorTestHelper.prepareSelectPostMappedStatement(config);
    config.addMappedStatement(selectBlog);
    config.addMappedStatement(selectPost);
    List<Post> posts = executor.query(selectPost, 5, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT, Executor.NO_RESULT_HANDLER);
    executor.flushStatements();
    executor.rollback(true);
    assertEquals(1, posts.size());
    Post post = posts.get(0);
    assertNull(post.getBlog());
  }

  @Test
  public void shouldFetchPostWithBlogWithCompositeKey() throws Exception {
    DataSource ds = createBlogDataSource();
    Connection connection = ds.getConnection();
    Executor executor = createExecutor(new JdbcTransaction(connection, false));
    MappedStatement selectBlog = ExecutorTestHelper.prepareSelectBlogByIdAndAuthor(config);
    MappedStatement selectPost = ExecutorTestHelper.prepareSelectPostWithBlogByAuthorMappedStatement(config);
    config.addMappedStatement(selectBlog);
    config.addMappedStatement(selectPost);
    List<Post> posts = executor.query(selectPost, 2, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT, Executor.NO_RESULT_HANDLER);
    executor.flushStatements();
    assertEquals(1, posts.size());
    Post post = posts.get(0);
    assertNotNull(post.getBlog());
    assertEquals(101, post.getBlog().getAuthor().getId());
    executor.rollback(true);
  }


  @Test
  public void shouldFetchComplexBlogs() throws Exception {
    DataSource ds = createBlogDataSource();
    Connection connection = ds.getConnection();
    try {
      Executor executor = createExecutor(new JdbcTransaction(connection, false));
      MappedStatement selectBlog = ExecutorTestHelper.prepareComplexSelectBlogMappedStatement(config);
      MappedStatement selectPosts = ExecutorTestHelper.prepareSelectPostsForBlogMappedStatement(config);
      config.addMappedStatement(selectBlog);
      config.addMappedStatement(selectPosts);
      config.setLazyLoadingEnabled(false);
      List<Blog> blogs = executor.query(selectBlog, 1, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT, Executor.NO_RESULT_HANDLER);
      executor.flushStatements();
      assertEquals(1, blogs.size());
      assertEquals(2, blogs.get(0).getPosts().size());
      assertEquals(1, blogs.get(0).getPosts().get(1).getBlog().getPosts().get(1).getBlog().getId());
      executor.rollback(true);
    } finally {
      config.setLazyLoadingEnabled(true);
    }
  }

  @Test
  public void shouldMapConstructorResults() throws Exception {
    DataSource ds = createBlogDataSource();
    Connection connection = ds.getConnection();
    Executor executor = createExecutor(new JdbcTransaction(connection, false));
    MappedStatement selectStatement = ExecutorTestHelper.prepareSelectOneAuthorMappedStatementWithConstructorResults(config);
    List<Author> authors = executor.query(selectStatement, 102, Executor.NO_ROW_OFFSET, Executor.NO_ROW_LIMIT, Executor.NO_RESULT_HANDLER);
    executor.flushStatements();
    executor.rollback(true);
    assertEquals(1, authors.size());

    Author author = authors.get(0);
    assertEquals(102, author.getId());
  }

  protected abstract Executor createExecutor(Transaction transaction);

}
