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
package org.apache.ibatis.abator.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.apache.ibatis.abator.ui.plugin.AbatorUIPlugin;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "mpe". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class NewConfigFileWizard extends Wizard implements INewWizard {
	private NewConfigFileWizardPage1 page;
	private ISelection selection;

	/**
	 * Constructor for NewConfigFileWizard.
	 */
	public NewConfigFileWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new NewConfigFileWizardPage1(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		final String containerName = page.getLocation();
		final String fileName = page.getFileName();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, fileName, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish(
		String containerName,
		String fileName,
		IProgressMonitor monitor)
		throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		try {
			InputStream stream = openContentStream();
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e) {
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}
	
	/**
	 * We will initialize file contents with a sample text.
	 */

	private InputStream openContentStream() {
        String lineSeparator = System.getProperty("line.separator"); //$NON-NLS-1$
        if (lineSeparator == null) {
            lineSeparator = "\n"; //$NON-NLS-1$
        }
        
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); //$NON-NLS-1$
		sb.append(lineSeparator);
		sb.append("<!DOCTYPE abatorConfiguration PUBLIC \"-//Apache Software Foundation//DTD Abator for iBATIS Configuration 1.0//EN\""); //$NON-NLS-1$
		sb.append(lineSeparator);
		sb.append("  \"http://ibatis.apache.org/dtd/abator-config_1_0.dtd\">"); //$NON-NLS-1$
		sb.append(lineSeparator);
		sb.append(lineSeparator);
		sb.append("<abatorConfiguration>"); //$NON-NLS-1$
		sb.append(lineSeparator);
		sb.append("  <abatorContext>"); //$NON-NLS-1$

		sb.append("    <!-- TODO: Add Database Connection Information -->"); //$NON-NLS-1$
		sb.append(lineSeparator);
		sb.append("    <jdbcConnection driverClass=\"???\""); //$NON-NLS-1$
		sb.append(lineSeparator);
		sb.append("        connectionURL=\"???\""); //$NON-NLS-1$
		sb.append(lineSeparator);
		sb.append("        userId=\"???\""); //$NON-NLS-1$
		sb.append(lineSeparator);
		sb.append("        password=\"???\">"); //$NON-NLS-1$
		sb.append(lineSeparator);
		sb.append("      <classPathEntry location=\"???\" />"); //$NON-NLS-1$
		sb.append(lineSeparator);
		sb.append("    </jdbcConnection>"); //$NON-NLS-1$
		sb.append(lineSeparator);

		sb.append(lineSeparator);
		sb.append("    <javaModelGenerator targetPackage=\"???\" targetProject=\"???\" />"); //$NON-NLS-1$
		sb.append(lineSeparator);
		sb.append("    <sqlMapGenerator targetPackage=\"???\" targetProject=\"???\" />"); //$NON-NLS-1$
		sb.append(lineSeparator);
		sb.append("    <daoGenerator type=\"IBATIS\" targetPackage=\"???\" targetProject=\"???\" />"); //$NON-NLS-1$
		sb.append(lineSeparator);

		sb.append(lineSeparator);
		sb.append("    <table schema=\"???\" tableName=\"???\">"); //$NON-NLS-1$
		sb.append(lineSeparator);
		sb.append("      <columnOverride column=\"???\" property=\"???\" />"); //$NON-NLS-1$
		sb.append(lineSeparator);
		sb.append("    </table>"); //$NON-NLS-1$
		sb.append(lineSeparator);
		
		sb.append(lineSeparator);
		sb.append("  </abatorContext>"); //$NON-NLS-1$
		sb.append(lineSeparator);
		sb.append("</abatorConfiguration>"); //$NON-NLS-1$
		sb.append(lineSeparator);
		
		return new ByteArrayInputStream(sb.toString().getBytes());
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, AbatorUIPlugin.getPluginId(), IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	public void init(IWorkbench workbench, ISelection selection) {
		this.selection = selection;
	}
}