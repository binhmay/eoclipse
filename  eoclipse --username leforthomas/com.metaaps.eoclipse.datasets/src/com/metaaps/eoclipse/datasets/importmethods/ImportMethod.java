/*******************************************************************************
 * Copyright (c) 2010 METAAPS SRL(U).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     METAAPS SRL(U) - created by Thomas Lefort - initial API and implementation
 ******************************************************************************/
package com.metaaps.eoclipse.datasets.importmethods;

import java.util.HashMap;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import com.metaaps.eoclipse.common.IDataSets;
import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IImport;
import com.metaaps.eoclipse.common.datasets.IImportMethod;
import com.metaaps.eoclipse.datasets.Activator;

/**
 * @author leforthomas
 * 
 * Encapsulates the import method extension plug-in, read the extension's parameters' values
 * Provides methods for triggering the wizard and for creating the import
 * Extends Model FOR CONVENIENCE ONLY, it is not necessary in this case
 * 
 */
public class ImportMethod extends Model implements IImportMethod {

	private IExtension m_extension;
	private ImageDescriptor m_imagedescriptor;
	private String m_name;
	private IConfigurationElement m_configuration;
	private boolean m_useWizard;
	private String m_uriextension = "";

	public ImportMethod(IExtension extension, IConfigurationElement element)
	{
		m_extension = extension;
		String iconpath = element.getAttribute("icon");
		m_imagedescriptor = Activator.imageDescriptorFromPlugin(extension.getNamespaceIdentifier(), iconpath);
		m_name = element.getAttribute("name");
		m_uriextension = element.getAttribute("uriExtension");
		m_useWizard = element.getAttribute("useWizard").contentEquals("true");
		m_configuration = element;
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("com.metaaps.eoclipse.datasets.importmethod", getFullExtension());
		Util.addPopupMenu("popup:navigatorcontent.popupmenu.import", "import method", m_name, "com.metaaps.eoclipse.datasets.importitem", "com.metaaps.eoclipse.datasets.newimport", IDataSets.class, parameters);
	}
	
	public String getFullExtension() {
		return m_configuration.getContributor().getName() + ":" + m_name;
	}

	@Override
	public String getLabel() {
		return m_name;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return m_imagedescriptor;
	}

	@Override
	public String getId() {
		return null;
	}

	public IExtension getExtension() {
		return m_extension;
	}
	
	@Override
	public IImport getImport() {
		IImport method;
		try {
			method = (IImport) m_configuration.createExecutableExtension("Class");
			return method;
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public boolean useWizard() {
		return m_useWizard;
	}

	@Override
	public String getURIExtension() {
		return m_uriextension;
	}

	@Override
	public WizardPage getImportWizardPage() {
		try {
			return (WizardPage) m_configuration.createExecutableExtension("wizardPage");
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
