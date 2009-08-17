package org.apache.ibatis.binding;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;

import domain.blog.*;

public class IbatisConfig {

  public SqlSessionFactory getSqlSessionFactory() {
    try {
      DataSource dataSource = BaseDataTest.createBlogDataSource();
      BaseDataTest.runScript(dataSource, BaseDataTest.BLOG_DDL);
      BaseDataTest.runScript(dataSource, BaseDataTest.BLOG_DATA);
      TransactionFactory transactionFactory = new JdbcTransactionFactory();
      Environment environment = new Environment("Production", transactionFactory, dataSource);
      Configuration configuration = new Configuration(environment);
      configuration.setLazyLoadingEnabled(true);
      configuration.setEnhancementEnabled(true);
      configuration.getTypeAliasRegistry().registerAlias(Blog.class);
      configuration.getTypeAliasRegistry().registerAlias(Post.class);
      configuration.getTypeAliasRegistry().registerAlias(Author.class);
      configuration.addMapper(BoundBlogMapper.class);
      configuration.addMapper(BoundAuthorMapper.class);
      return new SqlSessionFactoryBuilder().build(configuration);
    } catch (Exception e) {
      throw new RuntimeException("Error initializing SqlSessionFactory. Cause: " + e, e);
    }
  }

}
