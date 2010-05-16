package com.ibatis.sqlmap.engine.builder.xml;

import com.ibatis.common.resources.Resources;
import com.ibatis.common.xml.Nodelet;
import com.ibatis.common.xml.NodeletException;
import com.ibatis.common.xml.NodeletParser;
import com.ibatis.common.xml.NodeletUtils;
import com.ibatis.common.exception.NestedRuntimeException;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;
import com.ibatis.sqlmap.engine.cache.CacheModel;
import com.ibatis.sqlmap.engine.mapping.parameter.BasicParameterMap;
import com.ibatis.sqlmap.engine.mapping.parameter.BasicParameterMapping;
import com.ibatis.sqlmap.engine.mapping.result.*;
import com.ibatis.sqlmap.engine.mapping.statement.*;
import com.ibatis.sqlmap.engine.type.CustomTypeHandler;
import com.ibatis.sqlmap.engine.type.TypeHandler;
import org.w3c.dom.Node;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Iterator;

public class SqlMapParser extends BaseParser {

  private final NodeletParser parser = new NodeletParser();

  public SqlMapParser(Variables vars) {
    super(vars);
    parser.setValidation(true);
    parser.setEntityResolver(new SqlMapClasspathEntityResolver());

    addSqlMapNodelets();
    addSqlNodelets();
    addTypeAliasNodelets();
    addCacheModelNodelets();
    addParameterMapNodelets();
    addResultMapNodelets();
    addStatementNodelets();

  }

  public void parse(Reader reader) throws NodeletException {
    parser.parse(reader);
  }

  private void addSqlMapNodelets() {
    parser.addNodelet("/sqlMap", new Nodelet() {
      public void process(Node node) throws Exception {
        Properties attributes = NodeletUtils.parseAttributes(node, vars.properties);
        vars.currentNamespace = attributes.getProperty("namespace");
      }
    });
    parser.addNodelet("/sqlMap/end()", new Nodelet() {
      public void process(Node node) throws Exception {
        Iterator names = vars.delegate.getResultMapNames();
        while (names.hasNext()) {
          String name = (String)names.next();
          ResultMap rm = vars.delegate.getResultMap(name);
          Discriminator disc = rm.getDiscriminator();
          if (disc != null) {
            disc.bindSubMaps();
          }
        }
      }
    });
  }

  private void addSqlNodelets() {
    parser.addNodelet("/sqlMap/sql", new Nodelet() {
      public void process(Node node) throws Exception {
        Properties attributes = NodeletUtils.parseAttributes(node, vars.properties);
        String id = attributes.getProperty("id");
        if (vars.useStatementNamespaces) {
          id = applyNamespace(id);
        }
        vars.sqlIncludes.put(id, node);
      }
    });
  }

  private void addTypeAliasNodelets() {
    parser.addNodelet("/sqlMap/typeAlias", new Nodelet() {
      public void process(Node node) throws Exception {
        Properties prop = NodeletUtils.parseAttributes(node, vars.properties);
        String alias = prop.getProperty("alias");
        String type = prop.getProperty("type");
        vars.typeHandlerFactory.putTypeAlias(alias, type);
      }
    });
  }

  private void addCacheModelNodelets() {
    parser.addNodelet("/sqlMap/cacheModel", new Nodelet() {
      public void process(Node node) throws Exception {
        vars.currentCacheModel = new CacheModel();
        vars.currentProperties = new Properties();
      }
    });
    parser.addNodelet("/sqlMap/cacheModel/end()", new Nodelet() {
      public void process(Node node) throws Exception {
        vars.errorCtx.setActivity("building a cache model");

        Properties attributes = NodeletUtils.parseAttributes(node, vars.properties);
        String id = applyNamespace(attributes.getProperty("id"));
        String type = attributes.getProperty("type");
        type = vars.typeHandlerFactory.resolveAlias(type);

        String readOnly = attributes.getProperty("readOnly");
        if (readOnly != null && readOnly.length() > 0) {
          vars.currentCacheModel.setReadOnly("true".equals(readOnly));
        } else {
          vars.currentCacheModel.setReadOnly(true);
        }

        String serialize = attributes.getProperty("serialize");
        if (serialize != null && serialize.length() > 0) {
          vars.currentCacheModel.setSerialize("true".equals(serialize));
        } else {
          vars.currentCacheModel.setSerialize(false);
        }

        vars.errorCtx.setObjectId(id + " cache model");

        vars.errorCtx.setMoreInfo("Check the cache model type.");
        vars.currentCacheModel.setId(id);
        vars.currentCacheModel.setResource(vars.errorCtx.getResource());

        try {
          vars.currentCacheModel.setControllerClassName(type);
        } catch (Exception e) {
          throw new NestedRuntimeException("Error setting Cache Controller Class.  Cause: " + e, e);
        }

        vars.errorCtx.setMoreInfo("Check the cache model configuration.");
        vars.currentCacheModel.configure(vars.currentProperties);

        if (vars.client.getDelegate().isCacheModelsEnabled()) {
          vars.client.getDelegate().addCacheModel(vars.currentCacheModel);
        }

        vars.errorCtx.setMoreInfo(null);
        vars.errorCtx.setObjectId(null);
        vars.currentProperties = null;
        vars.currentCacheModel = null;
      }
    });
    parser.addNodelet("/sqlMap/cacheModel/property", new Nodelet() {
      public void process(Node node) throws Exception {
        vars.errorCtx.setMoreInfo("Check the cache model properties.");
        Properties attributes = NodeletUtils.parseAttributes(node, vars.properties);
        String name = attributes.getProperty("name");
        String value = NodeletUtils.parsePropertyTokens(attributes.getProperty("value"), vars.properties);
        vars.currentProperties.put(name, value);
      }
    });
    parser.addNodelet("/sqlMap/cacheModel/flushOnExecute", new Nodelet() {
      public void process(Node node) throws Exception {
        vars.errorCtx.setMoreInfo("Check the cache model flush on statement elements.");
        Properties childAttributes = NodeletUtils.parseAttributes(node, vars.properties);
        vars.currentCacheModel.addFlushTriggerStatement(childAttributes.getProperty("statement"));
      }
    });
    parser.addNodelet("/sqlMap/cacheModel/flushInterval", new Nodelet() {
      public void process(Node node) throws Exception {
        Properties childAttributes = NodeletUtils.parseAttributes(node, vars.properties);
        long t = 0;
        try {
          vars.errorCtx.setMoreInfo("Check the cache model flush interval.");
          String milliseconds = childAttributes.getProperty("milliseconds");
          String seconds = childAttributes.getProperty("seconds");
          String minutes = childAttributes.getProperty("minutes");
          String hours = childAttributes.getProperty("hours");
          if (milliseconds != null) t += Integer.parseInt(milliseconds);
          if (seconds != null) t += Integer.parseInt(seconds) * 1000;
          if (minutes != null) t += Integer.parseInt(minutes) * 60 * 1000;
          if (hours != null) t += Integer.parseInt(hours) * 60 * 60 * 1000;
          if (t < 1) throw new NestedRuntimeException("A flush interval must specify one or more of milliseconds, seconds, minutes or hours.");
          vars.currentCacheModel.setFlushInterval(t);
        } catch (NumberFormatException e) {
          throw new NestedRuntimeException("Error building cache '" + vars.currentCacheModel.getId() + "' in '" + "resourceNAME" + "'.  Flush interval milliseconds must be a valid long integer value.  Cause: " + e, e);
        }
      }
    });
  }

  private void addParameterMapNodelets() {
    parser.addNodelet("/sqlMap/parameterMap/end()", new Nodelet() {
      public void process(Node node) throws Exception {

        vars.currentParameterMap.setParameterMappingList(vars.parameterMappingList);

        vars.client.getDelegate().addParameterMap(vars.currentParameterMap);

        vars.errorCtx.setMoreInfo(null);
        vars.errorCtx.setObjectId(null);
      }
    });
    parser.addNodelet("/sqlMap/parameterMap", new Nodelet() {
      public void process(Node node) throws Exception {
        vars.errorCtx.setActivity("building a parameter map");

        vars.currentParameterMap = new BasicParameterMap(vars.client.getDelegate());

        Properties attributes = NodeletUtils.parseAttributes(node, vars.properties);
        String id = applyNamespace(attributes.getProperty("id"));
        String parameterClassName = attributes.getProperty("class");
        parameterClassName = vars.typeHandlerFactory.resolveAlias(parameterClassName);

        vars.currentParameterMap.setId(id);
        vars.currentParameterMap.setResource(vars.errorCtx.getResource());

        vars.errorCtx.setObjectId(id + " parameter map");

        Class parameterClass = null;
        try {
          vars.errorCtx.setMoreInfo("Check the parameter class.");
          parameterClass = Resources.classForName(parameterClassName);
        } catch (Exception e) {
          //TODO: Why is this commented out?
          //throw new SqlMapException("Error configuring ParameterMap.  Could not set ParameterClass.  Cause: " + e, e);
        }

        vars.currentParameterMap.setParameterClass(parameterClass);

        vars.parameterMappingList = new ArrayList();

        vars.errorCtx.setMoreInfo("Check the parameter mappings.");
      }
    });
    parser.addNodelet("/sqlMap/parameterMap/parameter", new Nodelet() {
      public void process(Node node) throws Exception {
        Properties childAttributes = NodeletUtils.parseAttributes(node, vars.properties);
        String propertyName = childAttributes.getProperty("property");
        String jdbcType = childAttributes.getProperty("jdbcType");
        String type     = childAttributes.getProperty("typeName");
        String javaType = childAttributes.getProperty("javaType");
        String nullValue = childAttributes.getProperty("nullValue");
        String mode = childAttributes.getProperty("mode");
        String callback = childAttributes.getProperty("typeHandler");

        callback = vars.typeHandlerFactory.resolveAlias(callback);
        javaType = vars.typeHandlerFactory.resolveAlias(javaType);

        vars.errorCtx.setObjectId(propertyName + " mapping of the " + vars.currentParameterMap.getId() + " parameter map");

        TypeHandler handler = null;
        if (callback != null) {
          vars.errorCtx.setMoreInfo("Check the parameter mapping typeHandler attribute '" + callback + "' (must be a TypeHandler or TypeHandlerCallback implementation).");
          try {
            Object impl = Resources.classForName(callback).newInstance();
            if (impl instanceof TypeHandlerCallback) {
              handler = new CustomTypeHandler((TypeHandlerCallback) impl);
            } else if (impl instanceof TypeHandler) {
              handler = (TypeHandler) impl;
            } else {
              throw new NestedRuntimeException ("The class '"+callback+"' is not a valid implementation of TypeHandler or TypeHandlerCallback");
            }
          } catch (Exception e) {
            throw new NestedRuntimeException("Error occurred during custom type handler configuration.  Cause: " + e, e);
          }
        } else {
          vars.errorCtx.setMoreInfo("Check the parameter mapping property type or name.");
          handler = resolveTypeHandler(vars.client.getDelegate().getTypeHandlerFactory(), vars.currentParameterMap.getParameterClass(), propertyName, javaType, jdbcType);
        }

        BasicParameterMapping mapping = new BasicParameterMapping();
        mapping.setPropertyName(propertyName);
        mapping.setJdbcTypeName(jdbcType);
        mapping.setTypeName(type);
        mapping.setNullValue(nullValue);
        if (mode != null && mode.length() > 0) {
          mapping.setMode(mode);
        }
        mapping.setTypeHandler(handler);
        try {
          if (javaType != null && javaType.length() > 0) {
            mapping.setJavaType(Class.forName(javaType));
          }
        } catch (ClassNotFoundException e) {
          throw new NestedRuntimeException("Error setting javaType on parameter mapping.  Cause: " + e);
        }

        vars.parameterMappingList.add(mapping);

      }
    });
  }

  private void addResultMapNodelets() {
    parser.addNodelet("/sqlMap/resultMap/end()", new Nodelet() {
      public void process(Node node) throws Exception {
        vars.currentResultMap.setResultMappingList(vars.resultMappingList);

        vars.currentResultMap.setDiscriminator(vars.discriminator);
        vars.discriminator = null;
        
        vars.client.getDelegate().addResultMap(vars.currentResultMap);

        vars.errorCtx.setMoreInfo(null);

        vars.errorCtx.setObjectId(null);
      }
    });
    parser.addNodelet("/sqlMap/resultMap", new Nodelet() {
      public void process(Node node) throws Exception {
        vars.errorCtx.setActivity("building a result map");

        vars.currentResultMap = new BasicResultMap(vars.client.getDelegate());

        Properties attributes = NodeletUtils.parseAttributes(node, vars.properties);
        String id = applyNamespace(attributes.getProperty("id"));
        String resultClassName = attributes.getProperty("class");
        String extended = applyNamespace(attributes.getProperty("extends"));
        String xmlName = attributes.getProperty("xmlName");
        String groupBy = attributes.getProperty("groupBy");
        resultClassName = vars.typeHandlerFactory.resolveAlias(resultClassName);

        vars.errorCtx.setObjectId(id + " result map");

        vars.currentResultMap.setId(id);
        vars.currentResultMap.setXmlName(xmlName);
        vars.currentResultMap.setResource(vars.errorCtx.getResource());

        if (groupBy != null && groupBy.length() > 0) {
          StringTokenizer parser = new StringTokenizer(groupBy, ", ", false);
          while (parser.hasMoreTokens()) {
            vars.currentResultMap.addGroupByProperty(parser.nextToken());
          }
        }

        Class resultClass = null;
        try {
          vars.errorCtx.setMoreInfo("Check the result class.");
          resultClass = Resources.classForName(resultClassName);
        } catch (Exception e) {
          throw new NestedRuntimeException("Error configuring Result.  Could not set ResultClass.  Cause: " + e, e);

        }

        vars.currentResultMap.setResultClass(resultClass);

        vars.resultMappingList = new ArrayList();

        vars.errorCtx.setMoreInfo("Check the extended result map.");
        if (extended != null) {
          BasicResultMap extendedResultMap = (BasicResultMap) vars.client.getDelegate().getResultMap(extended);
          ResultMapping[] resultMappings = extendedResultMap.getResultMappings();
          for (int i = 0; i < resultMappings.length; i++) {
            vars.resultMappingList.add(resultMappings[i]);
          }
          if (groupBy == null || groupBy.length() == 0) {
            if (extendedResultMap.hasGroupBy()) {
              Iterator i = extendedResultMap.groupByProps();
              while (i.hasNext()) {
                vars.currentResultMap.addGroupByProperty((String) i.next());
              }
            }
          }
        }

        vars.errorCtx.setMoreInfo("Check the result mappings.");
        vars.resultMappingIndex = vars.resultMappingList.size();

      }
    });
    parser.addNodelet("/sqlMap/resultMap/result", new Nodelet() {
      public void process(Node node) throws Exception {
        Properties childAttributes = NodeletUtils.parseAttributes(node, vars.properties);
        String propertyName = childAttributes.getProperty("property");
        String nullValue = childAttributes.getProperty("nullValue");
        String jdbcType = childAttributes.getProperty("jdbcType");
        String javaType = childAttributes.getProperty("javaType");
        String columnName = childAttributes.getProperty("column");
        String columnIndex = childAttributes.getProperty("columnIndex");
        String statementName = childAttributes.getProperty("select");
        String resultMapName = childAttributes.getProperty("resultMap");
        String callback = childAttributes.getProperty("typeHandler");

        callback = vars.typeHandlerFactory.resolveAlias(callback);
        javaType = vars.typeHandlerFactory.resolveAlias(javaType);

        vars.errorCtx.setObjectId(propertyName + " mapping of the " + vars.currentResultMap.getId() + " result map");

        TypeHandler handler = null;
        if (callback != null) {
          vars.errorCtx.setMoreInfo("Check the result mapping typeHandler attribute '" + callback + "' (must be a TypeHandler or TypeHandlerCallback implementation).");
          try {
            Object impl = Resources.classForName(callback).newInstance();
            if (impl instanceof TypeHandlerCallback) {
              handler = new CustomTypeHandler((TypeHandlerCallback) impl);
            } else if (impl instanceof TypeHandler) {
              handler = (TypeHandler) impl;
            } else {
              throw new NestedRuntimeException ("The class '"+callback+"' is not a valid implementation of TypeHandler or TypeHandlerCallback");
            }
          } catch (Exception e) {
            throw new NestedRuntimeException("Error occurred during custom type handler configuration.  Cause: " + e, e);
          }
        } else {
          vars.errorCtx.setMoreInfo("Check the result mapping property type or name.");
          handler = resolveTypeHandler(vars.client.getDelegate().getTypeHandlerFactory(), vars.currentResultMap.getResultClass(), propertyName, javaType, jdbcType, true);
        }


        BasicResultMapping mapping = new BasicResultMapping();
        mapping.setPropertyName(propertyName);
        mapping.setColumnName(columnName);
        mapping.setJdbcTypeName(jdbcType);
        mapping.setTypeHandler(handler);
        mapping.setNullValue(nullValue);
        mapping.setStatementName(statementName);
        mapping.setNestedResultMapName(resultMapName);

        if (resultMapName != null && resultMapName.length() > 0) {
          vars.currentResultMap.addNestedResultMappings(mapping);
        }

        try {
          if (javaType != null && javaType.length() > 0) {
            mapping.setJavaType(Class.forName(javaType));
          }
        } catch (ClassNotFoundException e) {
          throw new NestedRuntimeException("Error setting javaType on result mapping.  Cause: " + e);
        }

        if (columnIndex != null && columnIndex.length() > 0) {
          mapping.setColumnIndex(Integer.parseInt(columnIndex));
        } else {
          vars.resultMappingIndex++;
          mapping.setColumnIndex(vars.resultMappingIndex);
        }

        vars.resultMappingList.add(mapping);
      }
    });

    parser.addNodelet("/sqlMap/resultMap/discriminator/subMap", new Nodelet() {
      public void process(Node node) throws Exception {
        if (vars.discriminator == null) {
          throw new NestedRuntimeException ("The discriminator is null, but somehow a subMap was reached.  This is a bug.");
        }
        Properties childAttributes = NodeletUtils.parseAttributes(node, vars.properties);
        String value = childAttributes.getProperty("value");
        String resultMap = childAttributes.getProperty("resultMap");
        vars.discriminator.addSubMap(value, applyNamespace(resultMap));
      }
    });

    parser.addNodelet("/sqlMap/resultMap/discriminator", new Nodelet() {
      public void process(Node node) throws Exception {
        Properties childAttributes = NodeletUtils.parseAttributes(node, vars.properties);
        String nullValue = childAttributes.getProperty("nullValue");
        String jdbcType = childAttributes.getProperty("jdbcType");
        String javaType = childAttributes.getProperty("javaType");
        String columnName = childAttributes.getProperty("column");
        String columnIndex = childAttributes.getProperty("columnIndex");
        String callback = childAttributes.getProperty("typeHandler");

        callback = vars.typeHandlerFactory.resolveAlias(callback);
        javaType = vars.typeHandlerFactory.resolveAlias(javaType);

        TypeHandler handler = null;
        if (callback != null) {
          vars.errorCtx.setMoreInfo("Check the result mapping typeHandler attribute '" + callback + "' (must be a TypeHandlerCallback implementation).");
          try {
            Object impl = Resources.classForName(callback).newInstance();
            if (impl instanceof TypeHandlerCallback) {
              handler = new CustomTypeHandler((TypeHandlerCallback) impl);
            } else if (impl instanceof TypeHandler) {
              handler = (TypeHandler) impl;
            } else {
              throw new NestedRuntimeException ("The class '' is not a valid implementation of TypeHandler or TypeHandlerCallback");
            }
          } catch (Exception e) {
            throw new NestedRuntimeException("Error occurred during custom type handler configuration.  Cause: " + e, e);
          }
        } else {
          vars.errorCtx.setMoreInfo("Check the result mapping property type or name.");
          handler = resolveTypeHandler(vars.client.getDelegate().getTypeHandlerFactory(), vars.currentResultMap.getResultClass(), "", javaType, jdbcType, true);
        }

        BasicResultMapping mapping = new BasicResultMapping();
        mapping.setColumnName(columnName);
        mapping.setJdbcTypeName(jdbcType);
        mapping.setTypeHandler(handler);
        mapping.setNullValue(nullValue);

        try {
          if (javaType != null && javaType.length() > 0) {
            mapping.setJavaType(Class.forName(javaType));
          }
        } catch (ClassNotFoundException e) {
          throw new NestedRuntimeException("Error setting javaType on result mapping.  Cause: " + e);
        }

        if (columnIndex != null && columnIndex.length() > 0) {
          mapping.setColumnIndex(Integer.parseInt(columnIndex));
        }

        vars.discriminator = new Discriminator (vars.delegate, mapping);
      }
    });
  }

  private void addStatementNodelets() {
    parser.addNodelet("/sqlMap/statement", new Nodelet() {
      public void process(Node node) throws Exception {
        vars.currentStatement = new SqlStatementParser(vars).parseGeneralStatement(node, new GeneralStatement());
        vars.delegate.addMappedStatement(vars.currentStatement);
      }
    });
    parser.addNodelet("/sqlMap/insert", new Nodelet() {
      public void process(Node node) throws Exception {
        vars.currentStatement = new SqlStatementParser(vars).parseGeneralStatement(node, new InsertStatement());
        vars.delegate.addMappedStatement(vars.currentStatement);
      }
    });
    parser.addNodelet("/sqlMap/update", new Nodelet() {
      public void process(Node node) throws Exception {
        vars.currentStatement = new SqlStatementParser(vars).parseGeneralStatement(node, new UpdateStatement());
        vars.delegate.addMappedStatement(vars.currentStatement);
      }
    });
    parser.addNodelet("/sqlMap/delete", new Nodelet() {
      public void process(Node node) throws Exception {
        vars.currentStatement = new SqlStatementParser(vars).parseGeneralStatement(node, new DeleteStatement());
        vars.delegate.addMappedStatement(vars.currentStatement);
      }
    });
    parser.addNodelet("/sqlMap/select", new Nodelet() {
      public void process(Node node) throws Exception {
        vars.currentStatement = new SqlStatementParser(vars).parseGeneralStatement(node, new SelectStatement());
        vars.delegate.addMappedStatement(vars.currentStatement);
      }
    });
    parser.addNodelet("/sqlMap/procedure", new Nodelet() {
      public void process(Node node) throws Exception {
        vars.currentStatement = new SqlStatementParser(vars).parseGeneralStatement(node, new ProcedureStatement());
        vars.delegate.addMappedStatement(vars.currentStatement);
      }
    });
  }


}
