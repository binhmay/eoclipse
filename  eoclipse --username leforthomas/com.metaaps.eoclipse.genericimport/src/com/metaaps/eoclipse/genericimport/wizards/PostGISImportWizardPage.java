package com.metaaps.eoclipse.genericimport.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import com.metaaps.eoclipse.common.datasets.IImport;
import com.metaaps.eoclipse.common.datasets.IImportWizardPage;

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
