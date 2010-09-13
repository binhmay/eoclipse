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
package com.metaaps.eoclipse.datasets.internal.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import com.metaaps.eoclipse.common.datasets.IImportWizardPage;

/**
 * 
 * @author leforthomas
 * 
 * The Additional Properties Wizard Page enables the user to enter generic additional parameters values such as the name of the data
 * 
 */
public class AdditionalInfoWizardPage extends WizardPage {
	private Text m_nametext;
	private Composite container;
	private String m_name;
	private IImportWizardPage m_importpage;

	public AdditionalInfoWizardPage(IImportWizardPage importpage) {
		super("Set Name and Others...");
		setTitle("Set Name and Others...");
		setDescription("Set Name and Others...");
		m_importpage = importpage;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		
		Label label5 = new Label(container, SWT.NULL);
		label5.setText("Name");

		m_nametext = new Text(container, SWT.BORDER | SWT.SINGLE);
		m_nametext.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(true);
		
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		updateName();
	}
	
	public void updateName() {
		String name = "Data Name";
		try {
			String URI = m_importpage.getImportMethod().getURI();
			name = URI.substring(URI.lastIndexOf('/') + 1, URI.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		m_nametext.setText(name);
	}
	
	public String getName() {
		return m_name;
	}
	
	@Override
	public void dispose() {
		m_name = m_nametext.getText();
		super.dispose();
	}

}

