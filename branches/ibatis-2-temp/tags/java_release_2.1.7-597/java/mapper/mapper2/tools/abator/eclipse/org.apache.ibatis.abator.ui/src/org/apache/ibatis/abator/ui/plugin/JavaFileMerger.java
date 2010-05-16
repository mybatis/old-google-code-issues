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
package org.apache.ibatis.abator.ui.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.ibatis.abator.api.FullyQualifiedJavaType;
import org.apache.ibatis.abator.api.GeneratedJavaFile;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

/**
 * This class handles the task of merging changes into an existing Java
 * file.
 * 
 * @author Jeff Butler
 */
public class JavaFileMerger {

	private GeneratedJavaFile generatedJavaFile;

	private IFile existingFile;

	private class GatherNewItemsVisitor extends ASTVisitor {
		private List methods;
		private List fields;

		/**
		 * 
		 */
		public GatherNewItemsVisitor() {
			super();
			methods = new ArrayList();
			fields = new ArrayList();
		}

		public boolean visit(FieldDeclaration node) {
			fields.add(node);
			
			return false;
		}
		public boolean visit(MethodDeclaration node) {
			methods.add(node);

			return false;
		}
		
		public List getFields() {
			return fields;
		}
		
		public List getMethods() {
			return methods;
		}
	};
	
	private class ExistingJavaFileVisitor extends ASTVisitor {
		private TypeDeclaration typeDeclaration;
		private CompilationUnit compilationUnit;
		
		/**
		 * 
		 */
		public ExistingJavaFileVisitor(CompilationUnit compilationUnit) {
			super();
			this.compilationUnit = compilationUnit;
		}

		/**
		 * Find the Abator generated fields and delete them
		 */
		public boolean visit(FieldDeclaration node) {
			Javadoc jd = node.getJavadoc();
			if (jd != null) {
				List tags = jd.tags();
				Iterator tagIterator = tags.iterator();
				while (tagIterator.hasNext()) {
					TagElement tag = (TagElement) tagIterator.next();
					String tagName = tag.getTagName();
					if ("@abatorgenerated".equals(tagName)) {
						node.delete();
						break;
					}
				}
			}
			
			return false;
		}
		
		/**
		 * Find the Abator generated methods and delete them
		 */
		public boolean visit(MethodDeclaration node) {
			Javadoc jd = node.getJavadoc();
			if (jd != null) {
				List tags = jd.tags();
				Iterator tagIterator = tags.iterator();
				while (tagIterator.hasNext()) {
					TagElement tag = (TagElement) tagIterator.next();
					String tagName = tag.getTagName();
					if ("@abatorgenerated".equals(tagName)) {
						node.delete();
						break;
					}
				}
			}
			
			return false;
		}
		
        public boolean visit(TypeDeclaration node) {
            // make sure we only pick up the top level public type
            if (node.getParent().equals(compilationUnit)
                    && (node.getModifiers() & Modifier.PUBLIC) > 0) {
                typeDeclaration = node;
                return true;
            } else {
                return false;
            }
        }
        
        public TypeDeclaration getTypeDeclaration() {
            return typeDeclaration;
        }
	};
	
	/**
	 *  
	 */
	public JavaFileMerger(GeneratedJavaFile generatedJavaFile,
			IFile existingFile) {
		super();
		this.generatedJavaFile = generatedJavaFile;
		this.existingFile = existingFile;
	}

	public String getMergedSource() throws CoreException {
		ASTParser astParser = ASTParser.newParser(AST.JLS2);

		ICompilationUnit icu = JavaCore.createCompilationUnitFrom(existingFile);
		IDocument document = new Document(icu.getSource());

		// delete Abator generated stuff, and collect imports
		astParser.setSource(icu);
		CompilationUnit cu = (CompilationUnit) astParser.createAST(null);
		AST ast = cu.getAST();
		
		ExistingJavaFileVisitor visitor = new ExistingJavaFileVisitor(cu);

		cu.recordModifications();
		cu.accept(visitor);
		
		TypeDeclaration typeDeclaration = visitor.getTypeDeclaration();
		if (typeDeclaration == null) {
			Status status = new Status(IStatus.ERROR, AbatorUIPlugin
					.getPluginId(), IStatus.ERROR,
					"No public types defined in the file " + existingFile.getName(), null);
			throw new CoreException(status);
		}

		// reconcile the superinterfaces
		List newSuperInterfaces = getNewSuperInterfaces(typeDeclaration.superInterfaces());
		Iterator iter = newSuperInterfaces.iterator();
		while (iter.hasNext()) {
		    FullyQualifiedJavaType newSuperInterface = (FullyQualifiedJavaType) iter.next();
		    typeDeclaration.superInterfaces().add(ast.newSimpleName(newSuperInterface.getShortName()));
		}
		
		// set the superclass
		if (generatedJavaFile.getSuperClass() != null) {
		    typeDeclaration.setSuperclass(ast.newSimpleName(generatedJavaFile.getSuperClass().getShortName()));
		} else {
		    typeDeclaration.setSuperclass(null);
		}
		
		// interface or class?
		if (generatedJavaFile.isJavaInterface()) {
		    typeDeclaration.setInterface(true);
		} else {
		    typeDeclaration.setInterface(false);
		}
		
		// reconcile the imports
		List newImports = getNewImports(cu.imports());
		iter = newImports.iterator();
		while (iter.hasNext()) {
		    String[] newImport = (String[]) iter.next();
		    ImportDeclaration newImportDeclaration = ast.newImportDeclaration();
		    newImportDeclaration.setName(ast.newName(newImport));
		    cu.imports().add(newImportDeclaration);
		}

		TextEdit textEdit = cu.rewrite(document, null);
		try {
			textEdit.apply(document);
		} catch (BadLocationException e) {
			Status status = new Status(IStatus.ERROR, AbatorUIPlugin
					.getPluginId(), IStatus.ERROR,
					"BadLocationException removing prior fields and methods", e);
			throw new CoreException(status);
		}

		// regenerate the CompilationUnit to reflect all the deletes
		astParser.setSource(document.get().toCharArray());
		CompilationUnit strippedCu = (CompilationUnit) astParser
				.createAST(null);

		// find the top level public type declaration
		TypeDeclaration topLevelType = null;
	    iter = strippedCu.types().iterator();
	    while (iter.hasNext()) {
	        TypeDeclaration td = (TypeDeclaration) iter.next();
	        if (td.getParent().equals(strippedCu)
	                && (td.getModifiers() & Modifier.PUBLIC) > 0) {
	            topLevelType = td;
	            break;
	        }
	    }
		    
		// Now parse all the new fields and methods, then gather the new
		// methods and fields with a visitor
		astParser.setSource(generatedJavaFile.getContent().toCharArray());
		CompilationUnit newCu = (CompilationUnit) astParser.createAST(null);

		GatherNewItemsVisitor newVisitor = new GatherNewItemsVisitor();

		newCu.accept(newVisitor);

		// now add all the new methods and fields to the existing
		// CompilationUnit with a ListRewrite
		ASTRewrite rewrite = ASTRewrite.create(topLevelType.getRoot().getAST());
		ListRewrite listRewrite = rewrite.getListRewrite(topLevelType,
				TypeDeclaration.BODY_DECLARATIONS_PROPERTY);

		iter = newVisitor.getFields().iterator();
		int i = 0;
		while (iter.hasNext()) {
			listRewrite.insertAt((ASTNode) iter.next(), i++, null);
		}

		iter = newVisitor.getMethods().iterator();
		while (iter.hasNext()) {
			listRewrite.insertAt((ASTNode) iter.next(), i++, null);
		}

		textEdit = rewrite.rewriteAST(document, null);
		try {
			textEdit.apply(document);
		} catch (BadLocationException e) {
			Status status = new Status(IStatus.ERROR, AbatorUIPlugin
				.getPluginId(), IStatus.ERROR,
				"BadLocationException adding new fields and methods", e);
			throw new CoreException(status);
		}
		
		String newSource = document.get();
		return newSource;
	}
	
	private List getNewSuperInterfaces(List existingInterfaces) {
	    List answer = new ArrayList();
	    
	    Iterator newInterfaces = generatedJavaFile.getSuperInterfaceTypes().iterator();
	    while (newInterfaces.hasNext()) {
	        FullyQualifiedJavaType newInterface = (FullyQualifiedJavaType) newInterfaces.next();
		    Iterator iter = existingInterfaces.iterator();
		    boolean found = false;
		    while (iter.hasNext()) {
		        Name name = (Name) iter.next();
		        if (name.isSimpleName()) {
		            if (((SimpleName) name).getIdentifier().equals(newInterface.getShortName())) {
		                found = true;
		            }
		        } else {
		            if (((QualifiedName) name).getName().getIdentifier().equals(newInterface.getShortName())) {
		                found = true;
		            }
		        }
		    }
		    
		    if (!found) {
		        answer.add(newInterface);
		    }
	    }
	    
	    return answer;
	}

	private List getNewImports(List existingImports) {
	    List answer = new ArrayList();
	    
	    Iterator newImports = generatedJavaFile.getImportedTypes().iterator();
	    while (newImports.hasNext()) {
	        FullyQualifiedJavaType fqjt = (FullyQualifiedJavaType) newImports.next();
	        
		    Iterator iter = existingImports.iterator();
		    boolean found = false;
		    while (iter.hasNext()) {
		        ImportDeclaration existingImport = (ImportDeclaration) iter.next();
		        if (existingImport.getName().getFullyQualifiedName().equals(fqjt.getFullyQualifiedName())) {
		            found = true;
		        }
		    }
		    
		    if (!found) {
		        answer.add(parseName(fqjt.getFullyQualifiedName()));
		    }
	    }
	    
	    return answer;
	}
	
	private String[] parseName(String name) {
	    StringTokenizer st = new StringTokenizer(name, ".");
	    
	    String[] answer = new String[st.countTokens()];
	    
	    int i = 0;
	    while (st.hasMoreTokens()) {
	        answer[i++] = st.nextToken();
	    }
	    
	    return answer;
	}
}
