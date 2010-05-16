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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.abator.api.GeneratedJavaFile;
import org.apache.ibatis.abator.api.ShellCallback;
import org.apache.ibatis.abator.exception.ShellException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

/**
 * @author Jeff Butler
 */
public class EclipseShellCallback implements ShellCallback {
	private Map projects;

	private Map folders;

	/**
	 *  
	 */
	public EclipseShellCallback() {
		super();
		projects = new HashMap();
		folders = new HashMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ibatis.abator.core.api.ShellCallback#getDirectory(java.lang.String,
	 *      java.lang.String, java.util.List)
	 */
	public File getDirectory(String targetProject, String targetPackage,
			List warnings) throws ShellException {
		try {
			IFolder folder = getFolder(targetProject, targetPackage);

			return folder.getRawLocation().toFile();
		} catch (CoreException e) {
			// TODO - improve this exception handling
			throw new ShellException(e.getStatus().getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ibatis.abator.core.api.ShellCallback#mergeJavaFile(java.io.File,
	 *      org.apache.ibatis.abator.core.api.GeneratedJavaFile,
	 *      java.lang.String, java.util.List)
	 */
	public String mergeJavaFile(GeneratedJavaFile newFile, String javadocTag,
			List warnings) throws ShellException {
		try {
			IFolder folder = getFolder(newFile.getTargetProject(), newFile
					.getTargetPackage());

			IFile file = folder.getFile(newFile.getFileName());
			String source;
			if (file.exists()) {
				JavaFileMerger merger = new JavaFileMerger(newFile, file);
				source = merger.getMergedSource();

			} else {
				source = formatJavaSource(newFile.getContent());
			}

			return source;
		} catch (CoreException e) {
			// TODO - improve this exception handling
			throw new ShellException(e.getStatus().getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ibatis.abator.core.api.ShellCallback#refreshProject(java.lang.String)
	 */
	public void refreshProject(String project) {
		try {
			IJavaProject javaProject = getJavaProject(project);
			javaProject.getCorrespondingResource().refreshLocal(
					IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			// ignore
			;
		}
	}

	private String formatJavaSource(String unformattedSource) {
		CodeFormatter formatter = ToolFactory.createCodeFormatter(null);
		TextEdit te = formatter.format(CodeFormatter.K_COMPILATION_UNIT,
				unformattedSource, 0, unformattedSource.length(), 0, null);

		if (te == null) {
			// no edits to make
			return unformattedSource;
		}

		IDocument doc = new Document(unformattedSource);
		String formattedSource;
		try {
			te.apply(doc);
			formattedSource = doc.get();
		} catch (BadLocationException e) {
			formattedSource = unformattedSource;
		}

		return formattedSource;
	}

	private IJavaProject getJavaProject(String javaProjectName)
			throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(javaProjectName);
		IJavaProject javaProject;

		if (project.exists()) {
			if (project.hasNature(JavaCore.NATURE_ID)) {
				javaProject = JavaCore.create(project);
			} else {
				Status status = new Status(IStatus.ERROR, AbatorUIPlugin
						.getPluginId(), IStatus.ERROR, "Project "
						+ javaProjectName + " is not a Java Project", null);

				throw new CoreException(status);
			}
		} else {
			Status status = new Status(IStatus.ERROR, AbatorUIPlugin
					.getPluginId(), IStatus.ERROR, "Project " + javaProjectName
					+ " does not exist", null);

			throw new CoreException(status);
		}

		return javaProject;
	}

	private IFolder getFolder(String targetProject, String targetPackage)
			throws CoreException {
		String key = targetProject + targetPackage;
		IFolder folder = (IFolder) folders.get(key);
		if (folder == null) {
			IJavaProject project = (IJavaProject) projects.get(targetProject);
			if (project == null) {
				project = getJavaProject(targetProject);
				projects.put(targetProject, project);
			}

			IPackageFragmentRoot root = getPackageRoot(project);
			IPackageFragment packageFragment = getPackage(root, targetPackage);

			folder = (IFolder) packageFragment.getCorrespondingResource();

			folders.put(key, folder);
		}

		return folder;
	}

	/**
	 * This method returns the first modifiable package fragment root in the
	 * java project
	 * 
	 * @param javaProject
	 * @return
	 */
	private IPackageFragmentRoot getPackageRoot(IJavaProject javaProject)
			throws CoreException {

		// find the first non-JAR package fragment root
		IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
		IPackageFragmentRoot srcFolder = null;
		for (int i = 0; i < roots.length; i++) {
			if (roots[i].isArchive() || roots[i].isReadOnly()
					|| roots[i].isExternal()) {
				continue;
			} else {
				srcFolder = roots[i];
				break;
			}
		}

		if (srcFolder == null) {
			Status status = new Status(IStatus.ERROR, AbatorUIPlugin
					.getPluginId(), IStatus.ERROR,
					"Cannot find source folder for project "
							+ javaProject.getElementName(), null);
			throw new CoreException(status);
		}

		return srcFolder;
	}

	private IPackageFragment getPackage(IPackageFragmentRoot srcFolder,
			String packageName) throws CoreException {

		IPackageFragment fragment = srcFolder.getPackageFragment(packageName);
		if (!fragment.exists()) {
			fragment = srcFolder.createPackageFragment(packageName, true, null);
		}

		fragment.getCorrespondingResource().refreshLocal(IResource.DEPTH_ONE,
				null);

		return fragment;
	}

	/* (non-Javadoc)
	 * @see org.apache.ibatis.abator.api.ShellCallback#mergeSupported()
	 */
	public boolean mergeSupported() {
		return true;
	}
}
