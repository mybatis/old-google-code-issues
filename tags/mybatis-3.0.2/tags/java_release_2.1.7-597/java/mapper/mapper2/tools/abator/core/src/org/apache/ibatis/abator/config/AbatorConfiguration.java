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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ibatis.abator.exception.InvalidConfigurationException;

public class AbatorConfiguration {
    
    private List abatorContexts;

	public AbatorConfiguration() {
		super();
		abatorContexts = new ArrayList();
	}

	/**
	 * This method does a simple validate, it makes sure that all required
	 * fields have been filled in and that all implementation classes exist and
	 * are of the proper type. It does not do any more complex operations such
	 * as: validating that database tables exist or validating that named
	 * columns exist
	 */
	public void validate() throws InvalidConfigurationException {
		List errors = new ArrayList();
		
		Iterator iter = abatorContexts.iterator();
		while (iter.hasNext()) {
		    AbatorContext abatorContext = (AbatorContext) iter.next();
		    abatorContext.validate(errors);
		}

		if (errors.size() > 0) {
			throw new InvalidConfigurationException(errors);
		}
	}

    public List getAbatorContexts() {
        return abatorContexts;
    }
    
    public void addAbatorContext(AbatorContext abatorContext) {
        abatorContexts.add(abatorContext);
    }
}
