package org.sample.guice;

import com.google.inject.AbstractModule;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.apache.log4j.Logger;
import org.postgresql.ds.PGPoolingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public class WebModule extends AbstractModule {

	private static final Logger log = Logger.getLogger(WebModule.class);

	DataSource dataSource;

	SqlSessionFactory sqlSessionFactory;
	SqlSessionManager sqlSessionManager;

	EnvironmentSettings settings = new EnvironmentSettings();

	public WebModule() {
		dataSource = getDataSource();
		myBatisConfiguration();
	}

	@Override
	protected void configure() {
		bind(DataSource.class).toInstance(dataSource);
		bind(SqlSessionFactory.class).toInstance(sqlSessionFactory);
		bind(SqlSessionManager.class).toInstance(sqlSessionManager);

		ResolverUtil<Class> resolverUtil = new ResolverUtil<Class>();
		resolverUtil.find(new ResolverUtil.IsA(Object.class), "org.sample.mybatis.mappers");
		Set<Class<? extends Class>> mapperSet = resolverUtil.getClasses();
		for(Class mapperClass : mapperSet){
		  if(sqlSessionManager.getConfiguration().hasMapper(mapperClass)){
			  bind(mapperClass).toInstance(sqlSessionManager.getMapper(mapperClass));
		  }
		}

	}

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public DataSource getDataSource() {
		PGPoolingDataSource ds = new PGPoolingDataSource();
		ds.setUser(settings.getDatabase().getUser());
		ds.setPassword(settings.getDatabase().getPassword());
		ds.setServerName(settings.getDatabase().getServerName());
		ds.setDatabaseName(settings.getDatabase().getDatabaseName());

		// validate connection pool setup
		if (!validateDataSource(ds)) {
			throw new RuntimeException("Unable to validate data source.");
		}

		return ds;

	}

	Boolean validateDataSource(DataSource ds) {

		Boolean valid = false;

		Connection connection = null;

		try {

			connection = ds.getConnection();

		} catch (SQLException e) {

			// we'll deal with this later
			log.error(e.getLocalizedMessage(), e);

		} finally {

			if (null != connection) try {

				connection.close();

				// if we get here, we could open and close a connection - it's valid
				valid = true;

			} catch (SQLException e) {

				// we'll deal with this later
				log.error(e.getLocalizedMessage(), e);

			}

		}

		return valid;
	}

	void myBatisConfiguration() {

		Environment env = new Environment("org.sample", new JdbcTransactionFactory(), dataSource);

		Configuration config = new Configuration(env);

		// register all beans using short names
		config.getTypeAliasRegistry().registerAliases("org.sample.beans");

		// register all type handlers using short names
		config.getTypeAliasRegistry().registerAliases("org.sample.mybatis.typehandlers", TypeHandler.class);

		// register all the type handlers
		config.getTypeHandlerRegistry().register("org.sample.mybatis.typehandlers");

		// add all the mapper classes
		config.addMappers("org.sample.mybatis.mappers");

		// build a factory
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(config);
		sqlSessionManager = SqlSessionManager.newInstance(sqlSessionFactory);
	}

}
