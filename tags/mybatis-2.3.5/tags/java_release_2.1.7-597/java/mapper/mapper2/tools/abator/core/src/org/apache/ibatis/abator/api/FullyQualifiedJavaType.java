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
package org.apache.ibatis.abator.api;

/**
 * @author Jeff Butler
 */
public class FullyQualifiedJavaType {
    private String shortName;
    private String fullyQualifiedName;
    private boolean explicitlyImported;
    private String packageName;
    private boolean primitive;
    private String wrapperClass;

    /**
     * 
     */
    public FullyQualifiedJavaType(String fullyQualifiedName) {
        super();
        this.fullyQualifiedName = fullyQualifiedName;
        
		int lastIndex = fullyQualifiedName.lastIndexOf('.');
		if (lastIndex == -1) {
		    shortName = fullyQualifiedName;
		    explicitlyImported = false;
		    packageName = "";
		    
		    if ("byte".equals(fullyQualifiedName)) {
		        primitive = true;
		        wrapperClass = "Byte";
		    } else if ("short".equals(fullyQualifiedName)) {
		        primitive = true;
		        wrapperClass = "Short";
		    } else if ("int".equals(fullyQualifiedName)) {
		        primitive = true;
		        wrapperClass = "Integer";
		    } else if ("long".equals(fullyQualifiedName)) {
		        primitive = true;
		        wrapperClass = "Long";
		    } else if ("char".equals(fullyQualifiedName)) {
		        primitive = true;
		        wrapperClass = "Character";
		    } else if ("float".equals(fullyQualifiedName)) {
		        primitive = true;
		        wrapperClass = "Float";
		    } else if ("double".equals(fullyQualifiedName)) {
		        primitive = true;
		        wrapperClass = "Double";
		    } else if ("boolean".equals(fullyQualifiedName)) {
		        primitive = true;
		        wrapperClass = "Boolean";
		    } else {
		        primitive = false;
		        wrapperClass = null;
		    }
		} else {
		    shortName = fullyQualifiedName.substring(lastIndex + 1);
			packageName = fullyQualifiedName.substring(0, lastIndex);
			if ("java.lang".equals(packageName)) { //$NON-NLS-1$
			    explicitlyImported = false;
			} else {
			    explicitlyImported = true;
			}
		}
    }
    
    /**
     * @return Returns the explicitlyImported.
     */
    public boolean isExplicitlyImported() {
        return explicitlyImported;
    }
    /**
     * @return Returns the fullyQualifiedName.
     */
    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }
    /**
     * @return Returns the packageName.
     */
    public String getPackageName() {
        return packageName;
    }
    /**
     * @return Returns the shortName.
     */
    public String getShortName() {
        return shortName;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof FullyQualifiedJavaType)) {
			return false;
		}

		FullyQualifiedJavaType other = (FullyQualifiedJavaType) obj;
		
        return fullyQualifiedName.equals(other.fullyQualifiedName);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return fullyQualifiedName.hashCode();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return fullyQualifiedName;
    }
    
    /**
     * @return Returns the primitive.
     */
    public boolean isPrimitive() {
        return primitive;
    }
    
    /**
     * @return Returns the wrapperClass.
     */
    public String getWrapperClass() {
        return wrapperClass;
    }
}
