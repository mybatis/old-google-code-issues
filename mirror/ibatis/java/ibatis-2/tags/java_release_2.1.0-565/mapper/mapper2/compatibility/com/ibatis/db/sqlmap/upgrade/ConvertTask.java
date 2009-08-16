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
package com.ibatis.db.sqlmap.upgrade;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Extends the ant copy task to convert SQL Map files from v.1.x to v.2.x.
 * <p/>
 * Extends Ant version 1.5.4. Changes/extensions marked inline below
 */
public class ConvertTask extends Copy {

  private static final SqlMapXmlConverter CONVERTER = new SqlMapXmlConverter();

  /**
   * Overrides Ant Copy tasks file copy method.
   */
  protected void doFileOperations() {
    if (fileCopyMap.size() > 0) {
      log("Copying " + fileCopyMap.size()
          + " file" + (fileCopyMap.size() == 1 ? "" : "s")
          + " to " + destDir.getAbsolutePath());

      Enumeration e = fileCopyMap.keys();
      while (e.hasMoreElements()) {
        String fromFile = (String) e.nextElement();
        String toFile = (String) fileCopyMap.get(fromFile);

        if (fromFile.equals(toFile)) {
          log("Skipping self-copy of " + fromFile, verbosity);
          continue;
        }

        try {
          log("Copying " + fromFile + " to " + toFile, verbosity);

          FilterSetCollection executionFilters =
              new FilterSetCollection();
          if (filtering) {
            executionFilters
                .addFilterSet(getProject().getGlobalFilterSet());
          }
          for (Enumeration filterEnum = getFilterSets().elements();
               filterEnum.hasMoreElements();) {
            executionFilters
                .addFilterSet((FilterSet) filterEnum.nextElement());
          }

          // --------------------------------------

          File temp = File.createTempFile("sql-map-", "-temp");

          CONVERTER.convertFile(new File(fromFile), temp);

          getFileUtils().copyFile(temp, new File(toFile), executionFilters,
              getFilterChains(), forceOverwrite,
              preserveLastModified, getEncoding(),
              getProject());

          // --------------------------------------

        } catch (IOException ioe) {
          String msg = "Failed to copy " + fromFile + " to " + toFile
              + " due to " + ioe.getMessage();
          File targetFile = new File(toFile);
          if (targetFile.exists() && !targetFile.delete()) {
            msg += " and I couldn't delete the corrupt " + toFile;
          }
          throw new BuildException(msg, ioe, getLocation());
        }
      }
    }

    if (includeEmpty) {
      Enumeration e = dirCopyMap.elements();
      int count = 0;
      while (e.hasMoreElements()) {
        File d = new File((String) e.nextElement());
        if (!d.exists()) {
          if (!d.mkdirs()) {
            log("Unable to create directory "
                + d.getAbsolutePath(), Project.MSG_ERR);
          } else {
            count++;
          }
        }
      }

      if (count > 0) {
        log("Copied " + count +
            " empty director" +
            (count == 1 ? "y" : "ies") +
            " to " + destDir.getAbsolutePath());
      }
    }
  }

}
