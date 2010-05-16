/*
 *  Copyright 2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.ibatis.abator.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ibatis.abator.api.DAOGenerator;
import org.apache.ibatis.abator.api.JavaModelGenerator;
import org.apache.ibatis.abator.api.JavaTypeResolver;
import org.apache.ibatis.abator.api.ProgressCallback;
import org.apache.ibatis.abator.api.SqlMapGenerator;
import org.apache.ibatis.abator.exception.InvalidConfigurationException;
import org.apache.ibatis.abator.exception.UnknownTableException;
import org.apache.ibatis.abator.internal.AbatorObjectFactory;
import org.apache.ibatis.abator.internal.NullProgressCallback;
import org.apache.ibatis.abator.internal.db.ColumnDefinitions;
import org.apache.ibatis.abator.internal.db.ConnectionFactory;
import org.apache.ibatis.abator.internal.db.DatabaseIntrospector;
import org.apache.ibatis.abator.internal.util.StringUtility;

/**
 * @author Jeff Butler
 */
public class AbatorContext {
    private String id;
    
	private JDBCConnectionConfiguration jdbcConnectionConfiguration;

	private SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration;

	private JavaTypeResolverConfiguration javaTypeResolverConfiguration;

	private JavaModelGeneratorConfiguration javaModelGeneratorConfiguration;

	private DAOGeneratorConfiguration daoGeneratorConfiguration;

	private ArrayList tableConfigurations;
	
    /**
     * 
     */
    public AbatorContext() {
        super();
		jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
		sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
		javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
		javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
		daoGeneratorConfiguration = new DAOGeneratorConfiguration();
		tableConfigurations = new ArrayList();
    }

	public void addTableConfiguration(TableConfiguration tc) {
		tableConfigurations.add(tc);
	}

	public JDBCConnectionConfiguration getJdbcConnectionConfiguration() {
		return jdbcConnectionConfiguration;
	}

	public DAOGeneratorConfiguration getDaoGeneratorConfiguration() {
		return daoGeneratorConfiguration;
	}

	public JavaModelGeneratorConfiguration getJavaModelGeneratorConfiguration() {
		return javaModelGeneratorConfiguration;
	}

	public JavaTypeResolverConfiguration getJavaTypeResolverConfiguration() {
		return javaTypeResolverConfiguration;
	}

	public SqlMapGeneratorConfiguration getSqlMapGeneratorConfiguration() {
		return sqlMapGeneratorConfiguration;
	}

	/**
	 * This method does a simple validate, it makes sure that all required
	 * fields have been filled in and that all implementation classes exist and
	 * are of the proper type. It does not do any more complex operations such
	 * as: Validating that database tables exist or Validating that named
	 * columns exist
	 */
	public void validate(List errors) {
		validateJdbcConnectionConfiguration(errors);

		if (!StringUtility.stringHasValue(javaModelGeneratorConfiguration.getTargetProject())) {
			errors.add("Java Model Target Project is Required for context " + id);
		}

		if (!StringUtility.stringHasValue(sqlMapGeneratorConfiguration.getTargetProject())) {
			errors.add("SQL Map Generator Target Project is Required for context " + id);
		}

		if (daoGeneratorConfiguration.isEnabled()) {
			if (!StringUtility.stringHasValue(daoGeneratorConfiguration.getTargetProject())) {
				errors.add("DAO Generator Target Project is Required for context " + id);
			}
		}
		
		if (tableConfigurations.size() == 0) {
			errors.add("No Tables Specified");
		} else {
			for (int i = 0; i < tableConfigurations.size(); i++) {
				TableConfiguration tc = (TableConfiguration) tableConfigurations
						.get(i);

				validateTableConfiguration(tc, errors, i);
			}
		}
	}
	
	private void validateJdbcConnectionConfiguration(List errors) {
		if (!StringUtility.stringHasValue(jdbcConnectionConfiguration
				.getDriverClass())) {
			errors.add("JDBC Driver Class Must Be Specified");
		}

		if (!StringUtility.stringHasValue(jdbcConnectionConfiguration
				.getConnectionURL())) {
			errors.add("JDBC Connection URL Must Be Specified");
		}
	}

	private void validateTableConfiguration(TableConfiguration tc, List errors,
			int listPosition) {
		if (!StringUtility.stringHasValue(tc.getTable().getTableName())) {
			errors.add("Missing table name in table configuration at index "
					+ listPosition);
		}

		if (tc.getGeneratedKey().isConfigured()
				&& !StringUtility.stringHasValue(tc.getGeneratedKey()
						.getSqlStatement())) {
	        errors
				.add("SQL Statement is required if a generated key is specified in table configuration for table "
						+ tc.getTable().getFullyQualifiedTableName());
		}
	}

	/**
	 * Generate iBATIS artifacts based on the configuration specified in the
	 * constructor.  This method is long running.
	 * 
	 * @param callback a progress callback if progress information is desired, or <code>null</code>
	 * @param generatedJavaFiles any Java file generated from this method will be added to the List
	 *                            The objects will be of type GeneratedJavaFile.
	 * @param generatedXmlFiles any XML file generated from this method will be added to the List.
	 *                            The objects will be of type GeneratedXMLFile.
	 * @param warnings any warning generated from this method will be added to the List.  Warnings
	 *                   are always Strings.
	 * 
	 * @throws InvalidConfigurationException if some configuration error prevents
	 *                                       continuation
	 * @throws SQLException if some error arrises while introspecting the specified
	 *                      database tables.
	 * 
	 * @throws InterruptedException if the progress callback reports a cancel
	 */
	public void generateFiles(ProgressCallback callback, List generatedJavaFiles, List generatedXmlFiles, List warnings) throws InvalidConfigurationException,
			SQLException, InterruptedException {
	    
	    if (callback == null) {
	        callback = new NullProgressCallback();
	    }
	    
	    JavaTypeResolver javaTypeResolver = AbatorObjectFactory.createJavaTypeResolver(javaTypeResolverConfiguration);
	    JavaModelGenerator javaModelGenerator = AbatorObjectFactory.createJavaModelGenerator(javaModelGeneratorConfiguration);
	    SqlMapGenerator sqlMapGenerator = AbatorObjectFactory.createSqlMapGenerator(sqlMapGeneratorConfiguration, javaModelGenerator);
	    DAOGenerator daoGenerator = AbatorObjectFactory.createDAOGenerator(daoGeneratorConfiguration, javaModelGenerator, sqlMapGenerator);

		Connection connection = null;
		
		try {
			callback.startSubTask("Connecting to the Database");
			connection = getConnection();
			
			Iterator iter = tableConfigurations.iterator();
			while (iter.hasNext()) {
				TableConfiguration tc = (TableConfiguration) iter.next();
				if (!tc.areAnyStatementsEnabled()) {
				    StringBuffer sb = new StringBuffer();
				    sb.append("There are no statements enabled for table ");
				    sb.append(tc.getTable().getFullyQualifiedTableName());
				    sb.append(", table will be ignored.");
				    warnings.add(sb.toString());
				    continue;
				}
				
				String tableName = tc.getTable().getFullyQualifiedTableName();

				ColumnDefinitions columnDefinitions;
				try {
					callback.startSubTask("Introspecting table " + tableName);
					columnDefinitions  = DatabaseIntrospector.generateColumnDefinitions(connection, tc, javaTypeResolver, warnings);
					callback.checkCancel();
				} catch (UnknownTableException e) {
					StringBuffer sb = new StringBuffer();
					sb.append("Table ");
					sb.append(tc.getTable().getFullyQualifiedTableName());
					sb.append(" does not exist, or contains only LOB fields");

					warnings.add(sb.toString());
					continue;
				}

				if (daoGenerator != null) {
				    generatedJavaFiles.addAll(daoGenerator.getGeneratedJavaFiles(columnDefinitions, tc, callback));
				}
				generatedJavaFiles.addAll(javaModelGenerator.getGeneratedJavaFiles(columnDefinitions, tc, callback));
				generatedXmlFiles.addAll(sqlMapGenerator.getGeneratedXMLFiles(columnDefinitions, tc, callback));
			}
		} finally {
			closeConnection(connection);
			callback.finished();
		}
	}
	
	public int getTotalSteps() {
	    int steps = 0;
	    
	    steps++;  // connect to database
	    steps += tableConfigurations.size() * 8;
	    
	    return steps;
	}

	private Connection getConnection() throws SQLException {
		Connection connection = ConnectionFactory.getInstance().
				getConnection(jdbcConnectionConfiguration);

		return connection;
	}

	private void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// ignore
				;
			}
		}
	}
	
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
}
