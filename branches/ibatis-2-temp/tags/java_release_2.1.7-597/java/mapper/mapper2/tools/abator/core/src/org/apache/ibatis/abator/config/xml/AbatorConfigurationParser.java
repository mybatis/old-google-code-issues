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
package org.apache.ibatis.abator.config.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.ibatis.abator.config.AbatorConfiguration;
import org.apache.ibatis.abator.config.AbatorContext;
import org.apache.ibatis.abator.config.ColumnOverride;
import org.apache.ibatis.abator.config.DAOGeneratorConfiguration;
import org.apache.ibatis.abator.config.JDBCConnectionConfiguration;
import org.apache.ibatis.abator.config.JavaModelGeneratorConfiguration;
import org.apache.ibatis.abator.config.JavaTypeResolverConfiguration;
import org.apache.ibatis.abator.config.PropertyHolder;
import org.apache.ibatis.abator.config.SqlMapGeneratorConfiguration;
import org.apache.ibatis.abator.config.TableConfiguration;
import org.apache.ibatis.abator.exception.GenerationRuntimeException;
import org.apache.ibatis.abator.exception.XMLParserException;
import org.apache.ibatis.abator.internal.db.DatabaseDialects;
import org.apache.ibatis.abator.internal.java.DAOGeneratorGenericConstructorInjectionImpl;
import org.apache.ibatis.abator.internal.java.DAOGeneratorGenericSetterInjectionImpl;
import org.apache.ibatis.abator.internal.java.DAOGeneratorIbatisImpl;
import org.apache.ibatis.abator.internal.java.DAOGeneratorSpringImpl;
import org.apache.ibatis.abator.internal.java.JavaModelGeneratorDefaultImpl;
import org.apache.ibatis.abator.internal.sqlmap.SqlMapGeneratorDefaultImpl;
import org.apache.ibatis.abator.internal.types.JavaTypeResolverDefaultImpl;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Jeff Butler
 */
public class AbatorConfigurationParser {
	private List warnings;

	private List parseErrors;

	/**
	 *  
	 */
	public AbatorConfigurationParser(List warnings) {
		super();
		this.warnings = warnings;
		parseErrors = new ArrayList();
	}

	public AbatorConfiguration parseAbatorConfiguration(
			File inputFile) throws XMLParserException {
		parseErrors.clear();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new ParserEntityResolver());

			ParserErrorHandler handler = new ParserErrorHandler(warnings,
					parseErrors);
			builder.setErrorHandler(handler);

			Document document;
			try {
				document = builder.parse(inputFile);
			} catch (SAXParseException e) {
				throw new XMLParserException(parseErrors);
			} catch (SAXException e) {
				if (e.getException() == null) {
					throw new GenerationRuntimeException("SAXException", e);
				} else {
					throw new GenerationRuntimeException("SAXException", e
							.getException());
				}
			}

			if (parseErrors.size() > 0) {
				throw new XMLParserException(parseErrors);
			}

			NodeList nodeList = document.getChildNodes();
			AbatorConfiguration gc = null;
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				if (node.getNodeType() == 1
						&& "abatorConfiguration".equals(node.getNodeName())) { //$NON-NLS-1$
					gc = parseAbatorConfiguration(node);
					break;
				}
			}

			if (parseErrors.size() > 0) {
				throw new XMLParserException(parseErrors);
			}

			return gc;
		} catch (ParserConfigurationException e) {
			throw new GenerationRuntimeException(
					"ParserConfigurationException", e);
		} catch (IOException e) {
			throw new GenerationRuntimeException("IOException", e);
		}
	}

	private AbatorConfiguration parseAbatorConfiguration(Node node) {

		AbatorConfiguration abatorConfiguration = new AbatorConfiguration();

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

			if (childNode.getNodeType() != 1) {
				continue;
			}

			if ("abatorContext".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseAbatorContext(abatorConfiguration, childNode);
			}
		}

		return abatorConfiguration;
	}

	private void parseAbatorContext(AbatorConfiguration abatorConfiguration, Node node) {
	    
	    AbatorContext abatorContext = new AbatorContext();
	    
	    abatorConfiguration.addAbatorContext(abatorContext);

		NamedNodeMap nnm = node.getAttributes();
		Node attribute = nnm.getNamedItem("id"); //$NON-NLS-1$
		if (attribute != null) {
		    abatorContext.setId(attribute.getNodeValue());
		}
		
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

			if (childNode.getNodeType() != 1) {
				continue;
			}

			if ("jdbcConnection".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseJdbcConnection(abatorContext, childNode);
			} else if ("javaModelGenerator".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseJavaModelGenerator(abatorContext, childNode);
			} else if ("javaTypeResolver".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseJavaTypeResolver(abatorContext, childNode);
			} else if ("sqlMapGenerator".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseSqlMapGenerator(abatorContext, childNode);
			} else if ("daoGenerator".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseDaoGenerator(abatorContext, childNode);
			} else if ("table".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseTable(abatorContext, childNode);
			}
		}
	}
	
	private void parseSqlMapGenerator(AbatorContext abatorContext, Node node) {
		SqlMapGeneratorConfiguration sqlMapGenerator = abatorContext
				.getSqlMapGeneratorConfiguration();

		NamedNodeMap nnm = node.getAttributes();

		Node attribute = nnm.getNamedItem("type"); //$NON-NLS-1$
		if (attribute != null) {
			if ("DEFAULT".equalsIgnoreCase(attribute.getNodeValue())) { //$NON-NLS-1$
				sqlMapGenerator.setType(SqlMapGeneratorDefaultImpl.class
						.getName());
			} else {
				sqlMapGenerator.setType(attribute.getNodeValue());
			}
		}

		attribute = nnm.getNamedItem("targetPackage"); //$NON-NLS-1$
		sqlMapGenerator.setTargetPackage(attribute.getNodeValue());

		attribute = nnm.getNamedItem("targetProject"); //$NON-NLS-1$
		sqlMapGenerator.setTargetProject(attribute.getNodeValue());

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

			if (childNode.getNodeType() != 1) {
				continue;
			}

			if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseProperty(sqlMapGenerator, childNode);
			}
		}
	}

	private void parseTable(AbatorContext abatorContext, Node node) {
		TableConfiguration tc = new TableConfiguration();
		abatorContext.addTableConfiguration(tc);

		NamedNodeMap nnm = node.getAttributes();

		Node attribute = nnm.getNamedItem("catalog"); //$NON-NLS-1$
		if (attribute != null) {
			tc.getTable().setCatalog(attribute.getNodeValue());
		}

		attribute = nnm.getNamedItem("schema"); //$NON-NLS-1$
		if (attribute != null) {
			tc.getTable().setSchema(attribute.getNodeValue());
		}

		attribute = nnm.getNamedItem("domainObjectName"); //$NON-NLS-1$
		if (attribute != null) {
			tc.getTable().setDomainObjectName(attribute.getNodeValue());
		}

		attribute = nnm.getNamedItem("tableName"); //$NON-NLS-1$
		tc.getTable().setTableName(attribute.getNodeValue());

		attribute = nnm.getNamedItem("enableInsert"); //$NON-NLS-1$
		if (attribute != null) {
			tc.setInsertStatementEnabled("true" //$NON-NLS-1$
					.equals(attribute.getNodeValue()));
		}

		attribute = nnm.getNamedItem("enableSelectByPrimaryKey"); //$NON-NLS-1$
		if (attribute != null) {
			tc.setSelectByPrimaryKeyStatementEnabled("true".equals(attribute //$NON-NLS-1$
					.getNodeValue()));
		}

		attribute = nnm.getNamedItem("enableSelectByExample"); //$NON-NLS-1$
		if (attribute != null) {
			tc.setSelectByExampleStatementEnabled("true".equals(attribute //$NON-NLS-1$
					.getNodeValue()));
		}

		attribute = nnm.getNamedItem("enableUpdateByPrimaryKey"); //$NON-NLS-1$
		if (attribute != null) {
			tc.setUpdateByPrimaryKeyStatementEnabled("true".equals(attribute //$NON-NLS-1$
					.getNodeValue()));
		}

		attribute = nnm.getNamedItem("enableDeleteByPrimaryKey"); //$NON-NLS-1$
		if (attribute != null) {
			tc.setDeleteByPrimaryKeyStatementEnabled("true".equals(attribute //$NON-NLS-1$
					.getNodeValue()));
		}

		attribute = nnm.getNamedItem("enableDeleteByExample"); //$NON-NLS-1$
		if (attribute != null) {
			tc.setDeleteByExampleStatementEnabled("true".equals(attribute //$NON-NLS-1$
					.getNodeValue()));
		}

		attribute = nnm.getNamedItem("selectByPrimaryKeyQueryId"); //$NON-NLS-1$
		if (attribute != null) {
			tc.setSelectByPrimaryKeyQueryId(attribute.getNodeValue());
		}

		attribute = nnm.getNamedItem("selectByExampleQueryId"); //$NON-NLS-1$
		if (attribute != null) {
			tc.setSelectByExampleQueryId(attribute.getNodeValue());
		}

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

			if (childNode.getNodeType() != 1) {
				continue;
			}

			if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseProperty(tc, childNode);
			} else if ("columnOverride".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseColumnOverride(tc, childNode);
			} else if ("ignoreColumn".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseIgnoreColumn(tc, childNode);
			} else if ("generatedKey".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseGeneratedKey(tc, childNode);
			}
		}
	}

	private void parseColumnOverride(TableConfiguration tc, Node node) {
		NamedNodeMap nnm = node.getAttributes();

		ColumnOverride co = new ColumnOverride();

		Node attribute = nnm.getNamedItem("column"); //$NON-NLS-1$
		co.setColumnName(attribute.getNodeValue());

		tc.addColumnOverride(co);

		attribute = nnm.getNamedItem("property"); //$NON-NLS-1$
		if (attribute != null) {
			co.setJavaProperty(attribute.getNodeValue());
		}

		attribute = nnm.getNamedItem("javaType"); //$NON-NLS-1$
		if (attribute != null) {
			co.setJavaType(attribute.getNodeValue());
		}

		attribute = nnm.getNamedItem("jdbcType"); //$NON-NLS-1$
		if (attribute != null) {
			co.setJdbcType(attribute.getNodeValue());
		}
	}

	private void parseGeneratedKey(TableConfiguration tc, Node node) {
		NamedNodeMap nnm = node.getAttributes();

		Node attribute = nnm.getNamedItem("column"); //$NON-NLS-1$
		tc.getGeneratedKey().setColumn(attribute.getNodeValue());

		attribute = nnm.getNamedItem("identity"); //$NON-NLS-1$
		tc.getGeneratedKey().setIdentity("true".equalsIgnoreCase(attribute.getNodeValue())); //$NON-NLS-1$

		attribute = nnm.getNamedItem("sqlStatement"); //$NON-NLS-1$
		String value = attribute.getNodeValue();
		if ("DB2".equalsIgnoreCase(value)) { //$NON-NLS-1$
			tc.getGeneratedKey().setSqlStatement(
					DatabaseDialects.getIdentityClause(DatabaseDialects.DB2));
			if (!tc.getGeneratedKey().isIdentity()) {
			    warnings.add("You specified \"DB2\" as the generated key SQL Statement, but did not specify that the column is an identity column in table configuration "
			            + tc.getTable().getFullyQualifiedTableName());
			}
		} else if ("MySQL".equalsIgnoreCase(value)) { //$NON-NLS-1$
			tc.getGeneratedKey().setSqlStatement(
					DatabaseDialects.getIdentityClause(DatabaseDialects.MYSQL));
			if (!tc.getGeneratedKey().isIdentity()) {
			    warnings.add("You specified \"MySql\" as the generated key SQL Statement, but did not specify that the column is an identity column in table configuration "
			            + tc.getTable().getFullyQualifiedTableName());
			}
		} else if ("SqlServer".equalsIgnoreCase(value)) { //$NON-NLS-1$
			tc.getGeneratedKey().setSqlStatement(
					DatabaseDialects
							.getIdentityClause(DatabaseDialects.SQLSERVER));
			if (!tc.getGeneratedKey().isIdentity()) {
			    warnings.add("You specified \"SqlServer\" as the generated key SQL Statement, but did not specify that the column is an identity column in table configuration "
			            + tc.getTable().getFullyQualifiedTableName());
			}
		} else if ("Cloudscape".equalsIgnoreCase(value)) { //$NON-NLS-1$
			tc.getGeneratedKey().setSqlStatement(
					DatabaseDialects
							.getIdentityClause(DatabaseDialects.CLOUDSCAPE));
			if (!tc.getGeneratedKey().isIdentity()) {
			    warnings.add("You specified \"Cloudscape\" as the generated key SQL Statement, but did not specify that the column is an identity column in table configuration "
			            + tc.getTable().getFullyQualifiedTableName());
			}
		} else if ("Derby".equalsIgnoreCase(value)) { //$NON-NLS-1$
			tc.getGeneratedKey().setSqlStatement(
					DatabaseDialects
							.getIdentityClause(DatabaseDialects.DERBY));
			if (!tc.getGeneratedKey().isIdentity()) {
			    warnings.add("You specified \"Derby\" as the generated key SQL Statement, but did not specify that the column is an identity column in table configuration "
			            + tc.getTable().getFullyQualifiedTableName());
			}
		} else if ("HSQLDB".equalsIgnoreCase(value)) { //$NON-NLS-1$
			tc.getGeneratedKey().setSqlStatement(
					DatabaseDialects
							.getIdentityClause(DatabaseDialects.HSQLDB));
			if (!tc.getGeneratedKey().isIdentity()) {
			    warnings.add("You specified \"HSQLDB\" as the generated key SQL Statement, but did not specify that the column is an identity column in table configuration "
			            + tc.getTable().getFullyQualifiedTableName());
			}
		} else {
			tc.getGeneratedKey().setSqlStatement(value);
		}
	}

	private void parseIgnoreColumn(TableConfiguration tc, Node node) {
		NamedNodeMap nnm = node.getAttributes();

		Node attribute = nnm.getNamedItem("column"); //$NON-NLS-1$
		tc.addIgnoredColumn(attribute.getNodeValue());
	}

	private void parseJavaTypeResolver(AbatorContext abatorContext,
			Node node) {
		JavaTypeResolverConfiguration javaTypeResolverConfiguration = abatorContext
				.getJavaTypeResolverConfiguration();

		NamedNodeMap nnm = node.getAttributes();

		Node attribute = nnm.getNamedItem("type"); //$NON-NLS-1$
		if (attribute != null) {
			if ("DEFAULT".equalsIgnoreCase(attribute.getNodeValue())) { //$NON-NLS-1$
				javaTypeResolverConfiguration
						.setType(JavaTypeResolverDefaultImpl.class.getName());
			} else {
				javaTypeResolverConfiguration.setType(attribute.getNodeValue());
			}
		}

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

			if (childNode.getNodeType() != 1) {
				continue;
			}

			if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseProperty(javaTypeResolverConfiguration, childNode);
			}
		}
	}

	private void parseJavaModelGenerator(AbatorContext abatorContext,
			Node node) {
		JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = abatorContext
				.getJavaModelGeneratorConfiguration();

		NamedNodeMap nnm = node.getAttributes();

		Node attribute = nnm.getNamedItem("type"); //$NON-NLS-1$
		if (attribute != null) {
			if ("DEFAULT".equalsIgnoreCase(attribute.getNodeValue())) { //$NON-NLS-1$
				javaModelGeneratorConfiguration
						.setType(JavaModelGeneratorDefaultImpl.class.getName());
			} else {
				javaModelGeneratorConfiguration.setType(attribute
						.getNodeValue());
			}
		}

		attribute = nnm.getNamedItem("targetPackage"); //$NON-NLS-1$
		javaModelGeneratorConfiguration
				.setTargetPackage(attribute.getNodeValue());

		attribute = nnm.getNamedItem("targetProject"); //$NON-NLS-1$
		javaModelGeneratorConfiguration.setTargetProject(attribute
				.getNodeValue());

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

			if (childNode.getNodeType() != 1) {
				continue;
			}

			if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseProperty(javaModelGeneratorConfiguration, childNode);
			}
		}
	}

	private void parseDaoGenerator(AbatorContext abatorContext, Node node) {
		DAOGeneratorConfiguration daoGeneratorConfiguration = abatorContext
				.getDaoGeneratorConfiguration();
		daoGeneratorConfiguration.setEnabled(true);

		NamedNodeMap nnm = node.getAttributes();

		Node attribute = nnm.getNamedItem("type"); //$NON-NLS-1$
		String type = attribute.getNodeValue();
		if ("IBATIS".equalsIgnoreCase(type)) { //$NON-NLS-1$
			daoGeneratorConfiguration.setType(DAOGeneratorIbatisImpl.class
					.getName());
		} else if ("SPRING".equalsIgnoreCase(type)) { //$NON-NLS-1$
			daoGeneratorConfiguration.setType(DAOGeneratorSpringImpl.class
					.getName());
		} else if ("GENERIC-CI".equalsIgnoreCase(type)) { //$NON-NLS-1$
			daoGeneratorConfiguration
					.setType(DAOGeneratorGenericConstructorInjectionImpl.class
							.getName());
		} else if ("GENERIC-SI".equalsIgnoreCase(type)) { //$NON-NLS-1$
			daoGeneratorConfiguration
					.setType(DAOGeneratorGenericSetterInjectionImpl.class
							.getName());
		} else {
			daoGeneratorConfiguration.setType(type);
		}

		attribute = nnm.getNamedItem("targetPackage"); //$NON-NLS-1$
		daoGeneratorConfiguration.setTargetPackage(attribute.getNodeValue());

		attribute = nnm.getNamedItem("targetProject"); //$NON-NLS-1$
		daoGeneratorConfiguration.setTargetProject(attribute.getNodeValue());

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

			if (childNode.getNodeType() != 1) {
				continue;
			}

			if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseProperty(daoGeneratorConfiguration, childNode);
			}
		}
	}

	private void parseJdbcConnection(AbatorContext abatorContext, Node node) {
		JDBCConnectionConfiguration jdbcConnection = abatorContext
				.getJdbcConnectionConfiguration();

		NamedNodeMap nnm = node.getAttributes();

		Node attribute = nnm.getNamedItem("driverClass"); //$NON-NLS-1$
		jdbcConnection.setDriverClass(attribute.getNodeValue());

		attribute = nnm.getNamedItem("connectionURL"); //$NON-NLS-1$
		jdbcConnection.setConnectionURL(attribute.getNodeValue());

		attribute = nnm.getNamedItem("userId"); //$NON-NLS-1$
		if (attribute != null) {
			jdbcConnection.setUserId(attribute.getNodeValue());
		}

		attribute = nnm.getNamedItem("password"); //$NON-NLS-1$
		if (attribute != null) {
			jdbcConnection.setPassword(attribute.getNodeValue());
		}

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

			if (childNode.getNodeType() != 1) {
				continue;
			}

			if ("classPathEntry".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseClassPathEntry(jdbcConnection, childNode);
			} else if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseProperty(jdbcConnection, childNode);
			}
		}
	}

	private void parseClassPathEntry(
			JDBCConnectionConfiguration jdbcConnectionConfiguration, Node node) {
		NamedNodeMap nnm = node.getAttributes();

		jdbcConnectionConfiguration.addClasspathEntry(nnm.getNamedItem(
				"location").getNodeValue()); //$NON-NLS-1$
	}

	private void parseProperty(PropertyHolder propertyHolder, Node node) {
		NamedNodeMap nnm = node.getAttributes();

		String name = nnm.getNamedItem("name").getNodeValue(); //$NON-NLS-1$
		String value = nnm.getNamedItem("value").getNodeValue(); //$NON-NLS-1$

		propertyHolder.addProperty(name, value);
	}
}
