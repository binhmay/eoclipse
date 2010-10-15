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
package com.metaaps.eoclipse.genericimport.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import com.metaaps.eoclipse.common.datasets.IImport;
import com.metaaps.eoclipse.common.datasets.IImportWizardPage;

/**
 * @author leforthomas
 *
 * Wizard Page for the PostGIS import method
 * NOT IMPLEMENTED
 * 
 */
public class PostGISImportWizardPage extends WizardPage implements
		IImportWizardPage {

	public PostGISImportWizardPage(String pageName) {
		super(pageName);
		// TODO Auto-generated constructor stub
	}

	public PostGISImportWizardPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public IImport getImportMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFilter(String filter) {
		// TODO Auto-generated method stub
		
	}

}
