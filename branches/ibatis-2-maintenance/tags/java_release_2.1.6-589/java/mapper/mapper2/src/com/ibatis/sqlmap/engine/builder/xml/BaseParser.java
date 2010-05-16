package com.ibatis.sqlmap.engine.builder.xml;

import com.ibatis.common.beans.Probe;
import com.ibatis.common.beans.ProbeFactory;
import com.ibatis.common.resources.Resources;
import com.ibatis.common.exception.NestedRuntimeException;
import com.ibatis.sqlmap.engine.cache.CacheModel;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;
import com.ibatis.sqlmap.engine.mapping.parameter.BasicParameterMap;
import com.ibatis.sqlmap.engine.mapping.result.BasicResultMap;
import com.ibatis.sqlmap.engine.mapping.result.BasicResultMapping;
import com.ibatis.sqlmap.engine.mapping.result.ResultMapping;
import com.ibatis.sqlmap.engine.mapping.result.Discriminator;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;
import com.ibatis.sqlmap.engine.scope.ErrorContext;
import com.ibatis.sqlmap.engine.type.DomTypeMarker;
import com.ibatis.sqlmap.engine.type.TypeHandler;
import com.ibatis.sqlmap.engine.type.TypeHandlerFactory;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public abstract class BaseParser {

  private static final Probe PROBE = ProbeFactory.getProbe();

  protected final Variables vars;

  protected BaseParser(Variables vars) {
    this.vars = vars;
  }

  public TypeHandler resolveTypeHandler(TypeHandlerFactory typeHandlerFactory, Class clazz, String propertyName, String javaType, String jdbcType) {
    return resolveTypeHandler(typeHandlerFactory, clazz, propertyName, javaType, jdbcType, false);
  }

  public TypeHandler resolveTypeHandler(TypeHandlerFactory typeHandlerFactory, Class clazz, String propertyName, String javaType, String jdbcType, boolean useSetterToResolve) {
    TypeHandler handler = null;
    if (clazz == null) {
      // Unknown
      handler = typeHandlerFactory.getUnkownTypeHandler();
    } else if (DomTypeMarker.class.isAssignableFrom(clazz)) {
      // DOM
      handler = typeHandlerFactory.getTypeHandler(String.class, jdbcType);
    } else if (java.util.Map.class.isAssignableFrom(clazz)) {
      // Map
      if (javaType == null) {
        handler = typeHandlerFactory.getUnkownTypeHandler(); //BUG 1012591 - typeHandlerFactory.getTypeHandler(java.lang.Object.class, jdbcType);
      } else {
        try {
          Class javaClass = Resources.classForName(javaType);
          handler = typeHandlerFactory.getTypeHandler(javaClass, jdbcType);
        } catch (Exception e) {
          throw new NestedRuntimeException("Error.  Could not set TypeHandler.  Cause: " + e, e);
        }
      }
    } else if (typeHandlerFactory.getTypeHandler(clazz, jdbcType) != null) {
      // Primitive
      handler = typeHandlerFactory.getTypeHandler(clazz, jdbcType);
    } else {
      // JavaBean
      if (javaType == null) {
        if (useSetterToResolve) {
          Class type = PROBE.getPropertyTypeForSetter(clazz, propertyName);
          handler = typeHandlerFactory.getTypeHandler(type, jdbcType);
        } else {
          Class type = PROBE.getPropertyTypeForGetter(clazz, propertyName);
          handler = typeHandlerFactory.getTypeHandler(type, jdbcType);
        }
      } else {
        try {
          Class javaClass = Resources.classForName(javaType);
          handler = typeHandlerFactory.getTypeHandler(javaClass, jdbcType);
        } catch (Exception e) {
          throw new NestedRuntimeException("Error.  Could not set TypeHandler.  Cause: " + e, e);
        }
      }
    }
    return handler;
  }

  public String applyNamespace(String id) {
    String newId = id;
    if (vars.currentNamespace != null && vars.currentNamespace.length() > 0 && id != null && id.indexOf(".") < 0) {
      newId = vars.currentNamespace + "." + id;
    }
    return newId;
  }

  /**
   * Variables the parser uses.  This "struct" like class is necessary because
   * anonymous inner classes do not have access to non-final member fields of the parent class.
   * This way, we can make the Variables instance final, and use all of its public fields as
   * variables for parsing state.
   */
  protected static class Variables {
    public ErrorContext errorCtx = new ErrorContext();

    public Properties txProps = new Properties();
    public Properties dsProps = new Properties();
    public ErrorContext errorContext = new ErrorContext();
    public Properties properties;

    public XmlConverter sqlMapConv;
    public XmlConverter sqlMapConfigConv;

    public String currentResource = "SQL Map XML Config File";
    public String currentNamespace = null;

    public ExtendedSqlMapClient client;
    public SqlMapExecutorDelegate delegate;
    public TypeHandlerFactory typeHandlerFactory;
    public DataSource dataSource;

    public boolean useStatementNamespaces = false;

    // SQL Map Vars
    public Properties currentProperties;
    public CacheModel currentCacheModel;
    public BasicResultMap currentResultMap;
    public BasicParameterMap currentParameterMap;
    public MappedStatement currentStatement;
    public List parameterMappingList;
    public List resultMappingList;
    public int resultMappingIndex;
    public Map sqlIncludes = new HashMap();
    public Discriminator discriminator;
  }

}
