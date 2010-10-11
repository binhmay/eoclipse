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

import java.net.URI;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import com.metaaps.eoclipse.common.datasets.IImportWizardPage;
import com.metaaps.eoclipse.common.datasets.IReader;
import com.metaaps.eoclipse.datasets.readers.Reader;
import com.metaaps.eoclipse.datasets.readers.ReadersFolder;

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
	private Combo m_readerlist;
	private IReader m_reader;
	private Combo m_formats;

	public AdditionalInfoWizardPage(IImportWizardPage importpage) {
		super("Set Name and Change Default Reader");
		setTitle("Set Name and Change Default Reader");
		setDescription("Set Name and Change Default Reader");
		m_importpage = importpage;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		
		new Label(container, SWT.NULL).setText("Name");
		m_nametext = new Text(container, SWT.BORDER | SWT.SINGLE);
		m_nametext.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(container, SWT.NULL).setText("Reader to Use:");
		m_readerlist = new Combo(container, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.VERTICAL | SWT.BORDER);
		m_readerlist.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_readerlist.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateFormats();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		new Label(container, SWT.NULL).setText("Format to Use:");
		m_formats = new Combo(container, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.VERTICAL | SWT.BORDER);
		m_formats.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(container, SWT.NULL);
		
		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(false);
		
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		updateValues(visible);
		setPageComplete(true);
	}
	
	private void updateValues(boolean visible) {
		if(visible) {
			updateName();
			updateReader();
			updateFormats();
		} else {
			m_name = m_nametext.getText();
			m_reader = ReadersFolder.getInstance().findReaderWithName(m_readerlist.getText());
			if(m_reader != null) {
				m_reader.setDataFormat(m_formats.getText());
			}
		}
	}

	private void updateReader() {
		m_readerlist.setItems(new String[]{});
		String URI = m_importpage.getImportMethod().getURI();
		String filter = "*" + URI.substring(URI.lastIndexOf("."), URI.length());
		for(IReader reader : ReadersFolder.getInstance().findReadersWithFilter(filter)) {
			m_readerlist.add(reader.getName());
		}
		m_readerlist.redraw();
		m_readerlist.select(0);
	}

	private void updateFormats()
	{
		String readername = m_readerlist.getText();
		m_formats.setItems(new String[]{});
		ReadersFolder readerfolder = ReadersFolder.getInstance();
		Object[] readers = readerfolder.getChildren();
		for(Object reader : readers) {
			if(((IReader)reader).getName().contentEquals(readername)) {
				for(String format : ((IReader)reader).getFormatsSupported()) {
					m_formats.add(format);
				}
				break;
			}
		}
		m_formats.redraw();
		m_formats.select(0);
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
		updateValues(false);
		super.dispose();
	}

	public IReader getReader() {
		return m_reader;
	}

}

