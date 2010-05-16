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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * @author Jeff Butler
 */
public class GeneratedJavaFile extends GeneratedFile {
	private Set importedTypes;

	private List fields;

	private List methods;

	private boolean javaInterface;

	private FullyQualifiedJavaType type;

	private FullyQualifiedJavaType superClass;

	private Set superInterfaceTypes;

    private String lineSeparator;
    
	/**
	 *  Default constructor
	 */
	public GeneratedJavaFile(FullyQualifiedJavaType type) {
		super();
		this.type = type;
		importedTypes = new HashSet();
		fields = new ArrayList();
		methods = new ArrayList();
		superInterfaceTypes = new HashSet();

        lineSeparator = System.getProperty("line.separator"); //$NON-NLS-1$
        if (lineSeparator == null) {
            lineSeparator = "\n"; //$NON-NLS-1$
        }
	}

	public List getFields() {
		return fields;
	}

	public Set getImportedTypes() {
		return importedTypes;
	}

	public List getMethods() {
		return methods;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.apache.ibatis.abator.api.GeneratedFile#getContent()
	 */
	public String getContent() {
		StringBuffer content = new StringBuffer();

		content.append("package "); //$NON-NLS-1$
		content.append(type.getPackageName());
		content.append(';');
		newLine(content);

		Iterator iter = importedTypes.iterator();
		while (iter.hasNext()) {
		    FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) iter.next();

		    if (fqjt.isExplicitlyImported()
		            && !fqjt.getPackageName().equals(type.getPackageName())) {
		        newLine(content);
		        content.append("import "); //$NON-NLS-1$
		        content.append(fqjt.getFullyQualifiedName());
		        content.append(';');
		    }
		}

        newLine(content);
        newLine(content);
		content.append("public "); //$NON-NLS-1$
		content.append(javaInterface ? "interface " : "class "); //$NON-NLS-1$ //$NON-NLS-2$
		content.append(type.getShortName());
		
		if (superClass != null) {
			content.append(" extends "); //$NON-NLS-1$
			content.append(superClass.getShortName());
		}
		
		if (superInterfaceTypes.size() > 0) {
			content.append(" implements "); //$NON-NLS-1$
			iter = superInterfaceTypes.iterator();
			boolean comma = false; 
			while (iter.hasNext()) {
			    FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) iter.next();
				if (comma) {
					content.append(", "); //$NON-NLS-1$
				}
				
				content.append(fqjt.getShortName());
				
				comma = true;
			}
		}

		content.append(" {"); //$NON-NLS-1$
		
		iter = fields.iterator();
		while (iter.hasNext()) {
	        newLine(content);
	        newLine(content);
			content.append(iter.next());
		}

		iter = methods.iterator();
		while (iter.hasNext()) {
	        newLine(content);
	        newLine(content);
			content.append(iter.next());
		}

        newLine(content);
		content.append('}');
        newLine(content);

		return content.toString();
	}

	public boolean isJavaInterface() {
		return javaInterface;
	}

	public void setJavaInterface(boolean javaInterface) {
		this.javaInterface = javaInterface;
	}

	public Set getSuperInterfaceTypes() {
		return superInterfaceTypes;
	}

	public void addSuperInterfaceType(FullyQualifiedJavaType superInterfaceType) {
        addImportedType(superInterfaceType);
		superInterfaceTypes.add(superInterfaceType);
	}

	/**
	 * The underlying Set does not allow duplicates, so clients do
	 * not need to be concerned with duplicate resolution.
	 * 
	 * @param importedType the type to import.
	 */
	public void addImportedType(FullyQualifiedJavaType importedType) {
	    if (importedType.isExplicitlyImported()
	            && !type.getPackageName().equals(importedType.getPackageName())) {
	        importedTypes.add(importedType);
	    }
	}
	
	public void addField(String field) {
		fields.add(field);
	}
	
	public void addMethod(String method) {
		methods.add(method);
	}
	
    /**
     * @return Returns the superClass.
     */
    public FullyQualifiedJavaType getSuperClass() {
        return superClass;
    }
    /**
     * @param superClass The superClass to set.
     */
    public void setSuperClass(FullyQualifiedJavaType superClass) {
        addImportedType(superClass);
        this.superClass = superClass;
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.GeneratedFile#getFileName()
     */
    public String getFileName() {
        return type.getShortName() + ".java"; //$NON-NLS-1$
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.abator.api.GeneratedFile#getTargetPackage()
     */
    public String getTargetPackage() {
        return type.getPackageName();
    }

    /**
     * Utility method, adds a newline character to a StringBuffer.
     * 
     * @param sb the StringBuffer to be appended to
     */
    public void newLine(StringBuffer sb) {
        sb.append(lineSeparator);
    }
}
