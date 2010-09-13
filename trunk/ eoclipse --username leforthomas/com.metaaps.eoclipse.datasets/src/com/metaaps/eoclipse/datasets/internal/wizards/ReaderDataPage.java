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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import com.metaaps.eoclipse.common.datasets.IReader;
import com.metaaps.eoclipse.datasets.readers.ReadersFolder;

/**
 * 
 * @author leforthomas
 * 
 * The data reader wizard page
 * The user should choose the file type, the file format and the reader available
 * All these fields are populated dynamically based on the plug-ins available
 * 
 */
public class ReaderDataPage extends WizardPage {
	private Composite container;
	private Combo m_formats;
	private Combo m_types;
	private Combo m_reader;
	private IReader m_datareader = null;

	public ReaderDataPage() {
		super("Select the type of data to open");
		setTitle("Select the type of data to open");
		setDescription("Select the type of data to open");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		
		Label label1 = new Label(container, SWT.NULL);
		label1.setText("Data Type");
		
		m_types = new Combo(container, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.VERTICAL | SWT.BORDER);
		m_types.setItems(new String[]{"Image", "Vector"});
		m_types.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_types.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateReaders();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		new Label(container, SWT.NULL);

		Label label2 = new Label(container, SWT.NULL);
		label2.setText("Reader");

		m_reader = new Combo(container, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.VERTICAL | SWT.BORDER);
		m_reader.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_reader.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateFormats();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		new Label(container, SWT.NULL);
		
		Label label3 = new Label(container, SWT.NULL);
		label3.setText("Data Format");

		m_formats = new Combo(container, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.VERTICAL | SWT.BORDER);
		m_formats.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(container, SWT.NULL);
		
		m_types.select(0);
		updateReaders();

		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(true);
		
	}
	
	private void updateReaders()
	{
		String type = m_types.getText();
		m_reader.setItems(new String[]{});
		ReadersFolder readerfolder = ReadersFolder.getInstance();
		Object[] readers = readerfolder.getChildren();
		for(Object reader : readers) {
			if(((IReader)reader).getType().contentEquals(type)) {
				m_reader.add(((IReader)reader).getName());
			}
		}
		m_reader.redraw();
		m_reader.select(0);
		updateFormats();
	}

	private void updateFormats()
	{
		String type = m_types.getText();
		String readername = m_reader.getText();
		m_formats.setItems(new String[]{});
		ReadersFolder readerfolder = ReadersFolder.getInstance();
		Object[] readers = readerfolder.getChildren();
		for(Object reader : readers) {
			if(((IReader)reader).getType().contentEquals(type) && ((IReader)reader).getName().contentEquals(readername)) {
				for(String format : ((IReader)reader).getFormatsSupported()) {
					m_formats.add(format);
				}
				break;
			}
		}
		m_formats.redraw();
		m_formats.select(0);
	}

	public IReader getReader() {
		
		return m_datareader;
	}
	
	@Override
	public void dispose() {
		String readername = m_reader.getText();
		String formatname = m_formats.getText();
		String type = m_types.getText();
		
		ReadersFolder readerfolder = ReadersFolder.getInstance();
		Object[] readers = readerfolder.getChildren();
		for(Object reader : readers) {
			if(((IReader)reader).getType().contentEquals(type) && ((IReader)reader).getName().contentEquals(readername)) {
				m_datareader = ((IReader)reader);
				break;
			}
		}
		m_datareader.setType(type);
		m_datareader.setDataFormat(formatname);
		super.dispose();
	}
}

