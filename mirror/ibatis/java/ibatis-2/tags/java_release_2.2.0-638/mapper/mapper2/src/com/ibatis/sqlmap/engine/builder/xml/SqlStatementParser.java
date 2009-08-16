package com.ibatis.sqlmap.engine.builder.xml;

import com.ibatis.common.beans.Probe;
import com.ibatis.common.beans.ProbeFactory;
import com.ibatis.common.resources.Resources;
import com.ibatis.common.xml.NodeletUtils;
import com.ibatis.sqlmap.client.SqlMapException;
import com.ibatis.sqlmap.engine.cache.CacheModel;
import com.ibatis.sqlmap.engine.mapping.parameter.BasicParameterMap;
import com.ibatis.sqlmap.engine.mapping.parameter.InlineParameterMapParser;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMap;
import com.ibatis.sqlmap.engine.mapping.result.AutoResultMap;
import com.ibatis.sqlmap.engine.mapping.result.BasicResultMap;
import com.ibatis.sqlmap.engine.mapping.sql.Sql;
import com.ibatis.sqlmap.engine.mapping.sql.SqlText;
import com.ibatis.sqlmap.engine.mapping.sql.dynamic.DynamicSql;
import com.ibatis.sqlmap.engine.mapping.sql.dynamic.elements.*;
import com.ibatis.sqlmap.engine.mapping.sql.simple.SimpleDynamicSql;
import com.ibatis.sqlmap.engine.mapping.sql.stat.StaticSql;
import com.ibatis.sqlmap.engine.mapping.statement.*;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.sql.ResultSet;
import java.util.*;

public class SqlStatementParser extends BaseParser {

  private static final Probe PROBE = ProbeFactory.getProbe();

  private static final InlineParameterMapParser PARAM_PARSER = new InlineParameterMapParser();

  public SqlStatementParser(Variables vars) {
    super(vars);
  }

  public MappedStatement parseGeneralStatement(Node node, GeneralStatement statement) {
    vars.errorCtx.setActivity("parsing a mapped statement");

    // get attributes
    Properties attributes = NodeletUtils.parseAttributes(node, vars.currentProperties);
    String id = attributes.getProperty("id");

    if (vars.useStatementNamespaces) {
      id = applyNamespace(id);
    }

    String parameterMapName = applyNamespace(attributes.getProperty("parameterMap"));
    String parameterClassName = attributes.getProperty("parameterClass");
    String resultMapName = attributes.getProperty("resultMap");
    String resultClassName = attributes.getProperty("resultClass");
    String cacheModelName = applyNamespace(attributes.getProperty("cacheModel"));
    String xmlResultName = attributes.getProperty("xmlResultName");
    String resultSetType = attributes.getProperty("resultSetType");
    String fetchSize = attributes.getProperty("fetchSize");
    String allowRemapping = attributes.getProperty("remapResults");
    String timeout = attributes.getProperty("timeout");

    String[] additionalResultMapNames;
    List additionalResultMaps = new ArrayList();

    vars.errorCtx.setObjectId(id + " statement");


    // get parameter and result maps

    vars.errorCtx.setMoreInfo("Check the result map name.");
    //BasicResultMap resultMap = null;
    if (resultMapName != null) {
      additionalResultMapNames = getAllButFirstToken(resultMapName);
      resultMapName = getFirstToken (resultMapName);
      statement.setResultMap((BasicResultMap) vars.client.getDelegate().getResultMap(applyNamespace(resultMapName)));
      for (int i=0; i < additionalResultMapNames.length; i++) {
        statement.addResultMap((BasicResultMap) vars.client.getDelegate().getResultMap(applyNamespace(additionalResultMapNames[i])));
      }
    }

    vars.errorCtx.setMoreInfo("Check the parameter map name.");

    if (parameterMapName != null) {
      statement.setParameterMap((BasicParameterMap) vars.client.getDelegate().getParameterMap(parameterMapName));
    }

    statement.setId(id);
    statement.setResource(vars.errorCtx.getResource());

    if (resultSetType != null) {
      if ("FORWARD_ONLY".equals(resultSetType)) {
        statement.setResultSetType(new Integer(ResultSet.TYPE_FORWARD_ONLY));
      } else if ("SCROLL_INSENSITIVE".equals(resultSetType)) {
        statement.setResultSetType(new Integer(ResultSet.TYPE_SCROLL_INSENSITIVE));
      } else if ("SCROLL_SENSITIVE".equals(resultSetType)) {
        statement.setResultSetType(new Integer(ResultSet.TYPE_SCROLL_SENSITIVE));
      }
    }

    if (fetchSize != null) {
      statement.setFetchSize(new Integer(fetchSize));
    }

    // set parameter class either from attribute or from map (make sure to match)
    ParameterMap parameterMap = statement.getParameterMap();
    if (parameterMap == null) {
      try {
        if (parameterClassName != null) {
          vars.errorCtx.setMoreInfo("Check the parameter class.");
          parameterClassName = vars.typeHandlerFactory.resolveAlias(parameterClassName);
          Class parameterClass = Resources.classForName(parameterClassName);
          statement.setParameterClass(parameterClass);
        }
      } catch (ClassNotFoundException e) {
        throw new SqlMapException("Error.  Could not set parameter class.  Cause: " + e, e);
      }
    } else {
      statement.setParameterClass(parameterMap.getParameterClass());
    }

    // process SQL statement, including inline parameter maps
    vars.errorCtx.setMoreInfo("Check the SQL statement.");
    processSqlStatement(node, statement);

    // set up either null result map or automatic result mapping
    BasicResultMap resultMap = (BasicResultMap)statement.getResultMap();
    if (resultMap == null && resultClassName == null) {
      statement.setResultMap(null);
    } else if (resultMap == null) {
      String firstResultClass = getFirstToken(resultClassName);
      resultMap = buildAutoResultMap(allowRemapping, statement, firstResultClass, xmlResultName);
      statement.setResultMap(resultMap);
      String[] additionalResultClasses = getAllButFirstToken(resultClassName);
      for (int i=0; i<additionalResultClasses.length; i++) {
        statement.addResultMap(buildAutoResultMap(allowRemapping, statement, additionalResultClasses[i],xmlResultName));
      }
      
    }

    statement.setTimeout(vars.defaultStatementTimeout);
    if (timeout != null) {
      try {
        statement.setTimeout(Integer.valueOf(timeout));
      } catch (NumberFormatException e) {
        throw new SqlMapException("Specified timeout value for statement "
            + statement.getId() + " is not a valid integer");
      }
    }

    vars.errorCtx.setMoreInfo(null);
    vars.errorCtx.setObjectId(null);

    statement.setSqlMapClient(vars.client);
    if (cacheModelName != null && cacheModelName.length() > 0 && vars.client.getDelegate().isCacheModelsEnabled()) {
      CacheModel cacheModel = vars.client.getDelegate().getCacheModel(cacheModelName);
      return new CachingStatement(statement, cacheModel);
    } else {
      return statement;
    }

  }

  private BasicResultMap buildAutoResultMap(String allowRemapping, GeneralStatement statement, String firstResultClass, String xmlResultName) {
    BasicResultMap resultMap;
    resultMap = new AutoResultMap(vars.client.getDelegate(), "true".equals(allowRemapping));
    resultMap.setId(statement.getId() + "-AutoResultMap");
    resultMap.setResultClass(resolveClass(firstResultClass));
    resultMap.setXmlName(xmlResultName);
    resultMap.setResource(statement.getResource());
    return resultMap;
  }

  private Class resolveClass(String resultClassName) {
    try {
      if (resultClassName != null) {
        vars.errorCtx.setMoreInfo("Check the result class.");
        return Resources.classForName(vars.typeHandlerFactory.resolveAlias(resultClassName));
      } else {
        return null;
      }
    } catch (ClassNotFoundException e) {
      throw new SqlMapException("Error.  Could not set result class.  Cause: " + e, e);
    }
  }

  private String getFirstToken (String s) {
    return new StringTokenizer(s, ", ", false).nextToken();
  }

  private String[] getAllButFirstToken (String s) {
    List strings = new ArrayList();
    StringTokenizer parser = new StringTokenizer(s, ", ", false);
    parser.nextToken();
    while (parser.hasMoreTokens()) {
      strings.add(parser.nextToken());
    }
    return (String[]) strings.toArray(new String[strings.size()]);
  }

  private void processSqlStatement(Node n, GeneralStatement statement) {
    vars.errorCtx.setActivity("processing an SQL statement");

    boolean isDynamic = false;
    DynamicSql dynamic = new DynamicSql(vars.client.getDelegate());
    StringBuffer sqlBuffer = new StringBuffer();

    isDynamic = parseDynamicTags(n, dynamic, sqlBuffer, isDynamic, false);
    if (statement instanceof InsertStatement) {
      InsertStatement insertStatement = ((InsertStatement) statement);
      SelectKeyStatement selectKeyStatement = findAndParseSelectKeyStatement(n, statement);
      insertStatement.setSelectKeyStatement(selectKeyStatement);
    }

    String sqlStatement = sqlBuffer.toString();
    if (isDynamic) {
      statement.setSql(dynamic);
    } else {
      applyInlineParameterMap(statement, sqlStatement);
    }

  }

  private boolean parseDynamicTags(Node node, DynamicParent dynamic, StringBuffer sqlBuffer, boolean isDynamic, boolean postParseRequired) {
    vars.errorCtx.setActivity("parsing dynamic SQL tags");

    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      String nodeName = child.getNodeName();
      if (child.getNodeType() == Node.CDATA_SECTION_NODE
          || child.getNodeType() == Node.TEXT_NODE) {

        String data = ((CharacterData) child).getData();
        data = NodeletUtils.parsePropertyTokens(data, vars.properties);

        SqlText sqlText;

        if (postParseRequired) {
          sqlText = new SqlText();
          sqlText.setPostParseRequired(postParseRequired);
          sqlText.setText(data);
        } else {
          sqlText = PARAM_PARSER.parseInlineParameterMap(vars.client.getDelegate().getTypeHandlerFactory(), data, null);
          sqlText.setPostParseRequired(postParseRequired);
        }

        dynamic.addChild(sqlText);

        sqlBuffer.append(data);
      } else if ("include".equals(nodeName)) {
        Properties attributes = NodeletUtils.parseAttributes(child, vars.properties);
        String refid = (String) attributes.get("refid");
        Node includeNode = (Node) vars.sqlIncludes.get(refid);
        if (includeNode == null) {
          String nsrefid = applyNamespace(refid);
          includeNode = (Node) vars.sqlIncludes.get(nsrefid);
          if (includeNode == null) {
            throw new RuntimeException("Could not find SQL statement to include with refid '" + refid + "'");
          }
        }
        isDynamic = parseDynamicTags(includeNode, dynamic, sqlBuffer, isDynamic, false);
      } else {
        vars.errorCtx.setMoreInfo("Check the dynamic tags.");

        SqlTagHandler handler = SqlTagHandlerFactory.getSqlTagHandler(nodeName);
        if (handler != null) {
          isDynamic = true;

          SqlTag tag = new SqlTag();
          tag.setName(nodeName);
          tag.setHandler(handler);

          Properties attributes = NodeletUtils.parseAttributes(child, vars.properties);

          tag.setPrependAttr(attributes.getProperty("prepend"));
          tag.setPropertyAttr(attributes.getProperty("property"));
          tag.setRemoveFirstPrepend(attributes.getProperty("removeFirstPrepend"));

          tag.setOpenAttr(attributes.getProperty("open"));
          tag.setCloseAttr(attributes.getProperty("close"));

          tag.setComparePropertyAttr(attributes.getProperty("compareProperty"));
          tag.setCompareValueAttr(attributes.getProperty("compareValue"));
          tag.setConjunctionAttr(attributes.getProperty("conjunction"));

          // an iterate ancestor requires a post parse

          if(dynamic instanceof SqlTag) {
            SqlTag parentSqlTag = (SqlTag)dynamic;
            if(parentSqlTag.isPostParseRequired() ||
               tag.getHandler() instanceof IterateTagHandler) {
              tag.setPostParseRequired(true);
            }
          } else if (dynamic instanceof DynamicSql) {
              if(tag.getHandler() instanceof IterateTagHandler) {
                tag.setPostParseRequired(true);
              }
          }

          dynamic.addChild(tag);

          if (child.hasChildNodes()) {
            isDynamic = parseDynamicTags(child, tag, sqlBuffer, isDynamic, tag.isPostParseRequired());
          }
        }
      }
    }
    vars.errorCtx.setMoreInfo(null);
    return isDynamic;
  }

  private SelectKeyStatement findAndParseSelectKeyStatement(Node n, GeneralStatement insertStatement) {
    vars.errorCtx.setActivity("parsing select key tags");

    SelectKeyStatement selectKeyStatement = null;

    boolean foundTextFirst = false;
    boolean hasType = false;

    NodeList children = n.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child.getNodeType() == Node.CDATA_SECTION_NODE
          || child.getNodeType() == Node.TEXT_NODE) {
        String data = ((CharacterData) child).getData();
        if (data.trim().length() > 0) {
          foundTextFirst = true;
        }
      } else if (child.getNodeType() == Node.ELEMENT_NODE
          && "selectKey".equals(child.getNodeName())) {
        selectKeyStatement = new SelectKeyStatement();
        hasType = parseSelectKey(child, insertStatement, selectKeyStatement);
        break;
      }
    }
    if (selectKeyStatement != null && !hasType) {
      selectKeyStatement.setAfter(foundTextFirst);
    }
    vars.errorCtx.setMoreInfo(null);
    return selectKeyStatement;
  }

  /**
   * 
   * @param node
   * @param insertStatement
   * @param selectKeyStatement
   * @return true is the type (pre or post) was set from the configuration
   *   false if the type (pre or post) should be inferred from the position
   *   of the element in the text (the legacy behavior)
   */
  private boolean parseSelectKey(Node node, GeneralStatement insertStatement, SelectKeyStatement selectKeyStatement) {
    vars.errorCtx.setActivity("parsing a select key");

    // get attributes
    Properties attributes = NodeletUtils.parseAttributes(node, vars.properties);
    String keyPropName = attributes.getProperty("keyProperty");
    String resultClassName = attributes.getProperty("resultClass");
    resultClassName = vars.typeHandlerFactory.resolveAlias(resultClassName);
    Class resultClass = null;

    // get parameter and result maps
    selectKeyStatement.setSqlMapClient(vars.client);

    selectKeyStatement.setId(insertStatement.getId() + "-SelectKey");
    selectKeyStatement.setResource(vars.errorCtx.getResource());
    selectKeyStatement.setKeyProperty(keyPropName);

    // process the type (pre or post) attribute
    boolean hasType;
    String type = attributes.getProperty("type");
    if (type == null) {
      hasType = false;
    } else {
      hasType = true;
      selectKeyStatement.setAfter("post".equals(type));
    }

    try {
      if (resultClassName != null) {
        vars.errorCtx.setMoreInfo("Check the select key result class.");
        resultClass = Resources.classForName(resultClassName);
      } else {
        Class parameterClass = insertStatement.getParameterClass();
        if (keyPropName != null && parameterClass != null) {
          resultClass = PROBE.getPropertyTypeForSetter(parameterClass, selectKeyStatement.getKeyProperty());
        }
      }
    } catch (ClassNotFoundException e) {
      throw new SqlMapException("Error.  Could not set result class.  Cause: " + e, e);
    }

    if (resultClass == null) {
      resultClass = Object.class;
    }

    // process SQL statement, including inline parameter maps
    vars.errorCtx.setMoreInfo("Check the select key SQL statement.");
    processSqlStatement(node, selectKeyStatement);

    BasicResultMap resultMap;
    resultMap = new AutoResultMap(vars.client.getDelegate(), false);
    resultMap.setId(selectKeyStatement.getId() + "-AutoResultMap");
    resultMap.setResultClass(resultClass);
    resultMap.setResource(selectKeyStatement.getResource());
    selectKeyStatement.setResultMap(resultMap);

    vars.errorCtx.setMoreInfo(null);
    return hasType;
  }

  private void applyInlineParameterMap(GeneralStatement statement, String sqlStatement) {
    String newSql = sqlStatement;

    vars.errorCtx.setActivity("building an inline parameter map");

    ParameterMap parameterMap = statement.getParameterMap();

    vars.errorCtx.setMoreInfo("Check the inline parameters.");
    if (parameterMap == null) {

      BasicParameterMap map;
      map = new BasicParameterMap(vars.client.getDelegate());

      map.setId(statement.getId() + "-InlineParameterMap");
      map.setParameterClass(statement.getParameterClass());
      map.setResource(statement.getResource());
      statement.setParameterMap(map);

      SqlText sqlText = PARAM_PARSER.parseInlineParameterMap(vars.client.getDelegate().getTypeHandlerFactory(), newSql, statement.getParameterClass());
      newSql = sqlText.getText();
      List mappingList = Arrays.asList(sqlText.getParameterMappings());

      map.setParameterMappingList(mappingList);

    }

    Sql sql = null;
    if (SimpleDynamicSql.isSimpleDynamicSql(newSql)) {
      sql = new SimpleDynamicSql(vars.client.getDelegate(), newSql);
    } else {
      sql = new StaticSql(newSql);
    }
    statement.setSql(sql);

  }

}
