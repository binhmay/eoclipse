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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IImport;
import com.metaaps.eoclipse.common.datasets.IImportWizardPage;
import com.metaaps.eoclipse.genericimport.LocalFile;

public class LocalFileImportWizardPage extends WizardPage implements IImportWizardPage {
	protected static final String CONFIGURATION_NAME = "com.metaaps.eoclipse.genericimport.localfile";
	private Label URI;
	private Composite container;
	private String m_uri;

	public LocalFileImportWizardPage() {
		super("Import Data Source From Local Disks");
		setTitle("Select a Data Source From Local Files");
		setDescription("Select a Data Source to Include in the Work Flow");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		
		Label label4 = new Label(container, SWT.NULL);
		label4.setText("Data URI");

		URI = new Label(container, SWT.BORDER | SWT.SINGLE);
		URI.setText("");
		URI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button fileBtn = new Button(container, SWT.BORDER);
		fileBtn.setText("Choose");
		
		fileBtn.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog filePicker = new FileDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
				filePicker.setFilterPath(Util.getUniqueConfigurationElementValue(CONFIGURATION_NAME, "filterpath"));
				filePicker.open();
				String path = filePicker.getFilterPath();
				URI.setText(path + "/" + filePicker.getFileName());
				Util.setUniqueConfigurationElementValue(CONFIGURATION_NAME, "filterpath", path);
				setPageComplete(true);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(false);
		
	}
	
	@Override
	public void dispose() {
		m_uri = URI.getText();
		super.dispose();
	}

	@Override
	public IImport getImportMethod() {
		LocalFile method = new LocalFile();
		if(m_uri == null) {
			m_uri = URI.getText();
		}
		method.setURI(m_uri);
		
		return method;
	}
	
}

