/*
 *  Copyright 2006 The Apache Software Foundation
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
package org.apache.ibatis.abator.api;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ibatis.abator.config.AbatorConfiguration;
import org.apache.ibatis.abator.config.xml.AbatorConfigurationParser;
import org.apache.ibatis.abator.exception.InvalidConfigurationException;
import org.apache.ibatis.abator.exception.XMLParserException;
import org.apache.ibatis.abator.internal.DefaultShellCallback;
import org.apache.ibatis.abator.internal.util.messages.Messages;

/**
 * This class allows Abator to be run from the command line.
 * 
 * @author Jeff Butler
 */
public class AbatorRunner {

	public static void main(String[] args) {
		if (args.length != 2) {
			usage();
			return;
		}
		
		String configfile = args[0];
		boolean overwrite = "true".equalsIgnoreCase(args[1]); //$NON-NLS-1$
		
        List warnings = new ArrayList();
        
        File configurationFile = new File(configfile);
        if (!configurationFile.exists()) {
            writeLine(Messages.getString("RuntimeError.1", configfile)); //$NON-NLS-1$
            return;
        }

        try {
            AbatorConfigurationParser cp = new AbatorConfigurationParser(
                warnings);
            AbatorConfiguration config = cp.parseAbatorConfiguration(configurationFile);
            
            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            
            Abator abator = new Abator(config, callback, warnings);
            
            abator.generate(null);
            
        } catch (XMLParserException e) {
        	writeLine(Messages.getString("Progress.3")); //$NON-NLS-1$
        	writeLine();
            List errors = e.getErrors();
            Iterator iter = errors.iterator();
            while (iter.hasNext()) {
                writeLine((String) iter.next());
            }
            
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            // ignore (will never happen with the DefaultShellCallback)
            ;
        }
        
        Iterator iter = warnings.iterator();
        while (iter.hasNext()) {
            writeLine((String) iter.next());
        }
        
        if (warnings.size() == 0) {
        	writeLine(Messages.getString("Progress.4")); //$NON-NLS-1$
        } else {
        	writeLine();
        	writeLine(Messages.getString("Progress.5")); //$NON-NLS-1$
        }
	}
	
	private static void usage() {
		writeLine(Messages.getString("Usage.0")); //$NON-NLS-1$
		writeLine(Messages.getString("Usage.1")); //$NON-NLS-1$
		writeLine(Messages.getString("Usage.2")); //$NON-NLS-1$
		writeLine(Messages.getString("Usage.3")); //$NON-NLS-1$
		writeLine(Messages.getString("Usage.4")); //$NON-NLS-1$
        writeLine(Messages.getString("Usage.5")); //$NON-NLS-1$
	}
	
	private static void writeLine(String message) {
		System.out.println(message);
	}

	private static void writeLine() {
		System.out.println();
	}
}
