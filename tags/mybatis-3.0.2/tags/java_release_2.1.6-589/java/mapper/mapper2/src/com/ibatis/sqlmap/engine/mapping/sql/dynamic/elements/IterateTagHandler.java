/*
 *  Copyright 2004 Clinton Begin
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
package com.ibatis.sqlmap.engine.mapping.sql.dynamic.elements;

import com.ibatis.common.beans.Probe;
import com.ibatis.common.beans.ProbeFactory;

public class IterateTagHandler extends BaseTagHandler {

  private static final Probe PROBE = ProbeFactory.getProbe();

  public int doStartFragment(SqlTagContext ctx, SqlTag tag, Object parameterObject) {
    IterateContext iterate = (IterateContext) ctx.getAttribute(tag);
    if (iterate == null) {
      
      ctx.pushRemoveFirstPrependMarker(tag);
      
      Object collection;
      String prop = tag.getPropertyAttr();
      if (prop != null) {
        collection = PROBE.getObject(parameterObject, prop);
      } else {
        collection = parameterObject;
      }
      iterate = new IterateContext(collection,tag);
      
      iterate.setProperty( null == prop ? "" : prop );
      
      ctx.setAttribute(tag, iterate);
      ctx.pushIterateContext(iterate);
    }
    if (iterate != null && iterate.hasNext()) {
      return INCLUDE_BODY;
    } else {
      return SKIP_BODY;
    }
  }

  public int doEndFragment(SqlTagContext ctx, SqlTag tag, Object parameterObject, StringBuffer bodyContent) {
    IterateContext iterate = (IterateContext) ctx.getAttribute(tag);

    if (iterate.hasNext() || iterate.isFinal()) {
      
      if(iterate.isAllowNext()) {
        iterate.next();
      }

      //iteratePropertyReplace(bodyContent, iterate);

      if (iterate.isFirst()) {
        String open = tag.getOpenAttr();
        if (open != null) {
          bodyContent.insert(0, open);
        }
      }

      if (!iterate.isFirst()) {
        if(!bodyContent.toString().trim().equals("")) {
          String conj = tag.getConjunctionAttr();
          if (conj != null) {
            bodyContent.insert(0,conj);
          }
        }
      }

      if (iterate.isLast()) {
        String close = tag.getCloseAttr();
        if (close != null) {
          bodyContent.append(close);
        }
      }
      
      iterate.setAllowNext(true);
      if(iterate.isFinal()) {
        return super.doEndFragment(ctx,tag,parameterObject,bodyContent);
      } else {
        return REPEAT_BODY;
      }

    } else {
      return super.doEndFragment(ctx,tag,parameterObject,bodyContent);
    }
  }

  public void doPrepend(SqlTagContext ctx, SqlTag tag, Object parameterObject, StringBuffer bodyContent) {
    IterateContext iterate = (IterateContext) ctx.getAttribute(tag);
    if (iterate.isFirst()) {
      super.doPrepend(ctx, tag, parameterObject, bodyContent);
    }
  }

  public boolean isPostParseRequired() {
    return true;
  }

}

