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
package com.ibatis.sqlmap.engine.type;

import com.ibatis.sqlmap.client.extensions.*;

import java.sql.*;

public class BlobTypeHandlerCallback implements TypeHandlerCallback {

  public Object getResult(ResultGetter getter)
      throws SQLException {
    Blob blob = getter.getBlob();
    int size = (int) blob.length();
    return blob.getBytes(1, size);
  }

  public void setParameter(ParameterSetter setter, Object parameter)
      throws SQLException {
    byte[] bytes = (byte[]) parameter;
    setter.setBytes(bytes);
  }

  public Object valueOf(String s) {
    return s;
  }

}
