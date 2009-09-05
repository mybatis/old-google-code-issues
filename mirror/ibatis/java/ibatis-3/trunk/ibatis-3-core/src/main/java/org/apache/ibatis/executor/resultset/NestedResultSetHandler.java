package org.apache.ibatis.executor.resultset;

import static org.apache.ibatis.executor.resultset.NoValue.*;

import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.Configuration;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.ResultSet;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

public class NestedResultSetHandler {

  private final ObjectFactory objectFactory;
  private final TypeHandlerRegistry typeHandlerRegistry;
  private final MappedStatement mappedStatement;
  private final Map nestedResultObjects;

  private CacheKey currentNestedKey;
  private final DiscriminatorHandler discriminatorHandler;
  private DefaultResultSetHandler resultSetHandler;

  public NestedResultSetHandler(Configuration configuration, MappedStatement mappedStatement, DefaultResultSetHandler resultSetHandler) {
    this.objectFactory = configuration.getObjectFactory();
    this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
    this.mappedStatement = mappedStatement;
    this.nestedResultObjects = new HashMap();
    this.discriminatorHandler = new DiscriminatorHandler(mappedStatement);
    this.resultSetHandler = resultSetHandler;
  }

  public Object processNestedJoinResults(ResultSet rs, List<ResultMapping> resultMappings, Object resultObject) {
    CacheKey previousKey = currentNestedKey;
    try {
      currentNestedKey = createUniqueResultKey(resultMappings, resultObject, currentNestedKey);
      if (nestedResultObjects.containsKey(currentNestedKey)) {
        // Unique key is already known, so get the existing result object and process additional results.
        resultObject = NO_VALUE;
      } else if (currentNestedKey != null) {
        // Unique key is NOT known, so create a new result object and then process additional results.
        nestedResultObjects.put(currentNestedKey, resultObject);
      }
      Object knownResultObject = nestedResultObjects.get(currentNestedKey);
      if (knownResultObject != null && knownResultObject != NO_VALUE) {
        for (ResultMapping resultMapping : resultMappings) {
          Configuration configuration = mappedStatement.getConfiguration();
          String nestedResultMapId = resultMapping.getNestedResultMapId();
          if (nestedResultMapId != null) {
            ResultMap nestedResultMap = configuration.getResultMap(nestedResultMapId);
            try {

              // get the discriminated submap if it exists
              nestedResultMap = discriminatorHandler.resolveSubMap(rs, nestedResultMap);

              Class type = resultMapping.getJavaType();
              String propertyName = resultMapping.getProperty();

              MetaObject metaObject = MetaObject.forObject(knownResultObject);
              Object propertyValue = metaObject.getValue(propertyName);
              if (propertyValue == null) {
                if (type == null) {
                  type = metaObject.getSetterType(propertyName);
                }

                try {
                  // create the object if is it a Collection.  If not a Collection
                  // then we will just set the property to the object created
                  // in processing the nested result map
                  if (Collection.class.isAssignableFrom(type)) {
                    propertyValue = objectFactory.create(type);
                    metaObject.setValue(propertyName, propertyValue);
                  }
                } catch (Exception e) {
                  throw new ExecutorException("Error instantiating collection property for result '" + resultMapping.getProperty() + "'.  Cause: " + e, e);
                }
              }

              Reference<Boolean> foundValues = new Reference(false);
              Object nestedResultObject = resultSetHandler.loadResultObject(rs, nestedResultMap, foundValues);
              if (nestedResultObject != null && nestedResultObject != NO_VALUE) {
                if (propertyValue != null && propertyValue instanceof Collection) {
                  if (foundValues.get()) {
                    ((Collection) propertyValue).add(nestedResultObject);
                  }
                } else {
                  metaObject.setValue(propertyName, nestedResultObject);
                }
              }
            } catch (Exception e) {
              throw new ExecutorException("Error getting nested result map values for '" + resultMapping.getProperty() + "'.  Cause: " + e, e);
            }
          }
        }
      }
    } finally {
      currentNestedKey = previousKey;
    }
    return resultObject;
  }

  private CacheKey createUniqueResultKey(List<ResultMapping> resultMappings, Object resultObject, CacheKey parentCacheKey) {
    if (resultObject == null) {
      return null;
    } else {
      MetaObject metaResultObject = MetaObject.forObject(resultObject);
      CacheKey cacheKey = new CacheKey();
      cacheKey.update(parentCacheKey);
      boolean updated = false;
      if (typeHandlerRegistry.hasTypeHandler(resultObject.getClass())) {
        cacheKey.update(resultObject);
      } else {
        for (ResultMapping resultMapping : resultMappings) {
          if (resultMapping.getNestedQueryId() == null) {
            String propName = resultMapping.getProperty();
            if (propName != null) {
              cacheKey.update(metaResultObject.getValue(propName));
              updated = true;
            }
          }
        }
      }
      return updated ? cacheKey : null;
    }
  }


  public void reset() {
    nestedResultObjects.clear();
    currentNestedKey = null;
  }
}
