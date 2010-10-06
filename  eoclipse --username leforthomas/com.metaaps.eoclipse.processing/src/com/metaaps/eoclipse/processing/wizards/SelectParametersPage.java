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
package com.metaaps.eoclipse.processing.wizards;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import com.metaaps.eoclipse.common.IModel;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.processing.Parameter;
import com.metaaps.eoclipse.processing.Process;

public class SelectParametersPage extends WizardPage {
	
	private IDataSets m_datasets;
	private Process m_process;
	private HashMap<String, Object> m_parameters = new HashMap<String, Object>();
	private HashMap<String, Object> m_parametersvalues;

	public SelectParametersPage(IDataContent data, Process process) {
		super("Select Parameters");
		setTitle("Select the processing parameters for " + process.getName());
		setDescription("Specify the extra parameters required for this plugin");
		
		m_process = process;
		m_datasets = (IDataSets) Util.scanTree(IDataSets.class, data);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		
		for(Object obj : m_process.getChildren())
		{
			if(obj instanceof Parameter)
			{
				Parameter parameter = (Parameter)obj;
				String name = parameter.getName();
				Label label = new Label(container, SWT.NULL);
				label.setText(name);
				if(parameter.getType().contentEquals("Value")) {
					Text text = new Text(container, SWT.SINGLE | SWT.BORDER);
					text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					m_parameters.put(name, text);
				} else {
					Combo combo = new Combo(container, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.VERTICAL | SWT.BORDER);
					addParameters(combo, parameter);
					combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					m_parameters.put(name, combo);
				}
			}
		}
		
		// Required to avoid an error in the system
		setControl(container);
		
	}
	
	private void addParameters(Combo combo, Parameter parameter)
	{
		// scan available data in the datasets and find the ones that the parameter support
		for(Object obj : ((IModel)m_datasets).getChildren())
		{
			if(obj instanceof IDataContent)
			{
				IDataContent data = (IDataContent) obj;
				if(parameter.isSupported(data))
				{
					combo.add(data.getLabel());
				}
			}
		}
		// needs implementing
		// on selection, gathers available import methods and 
		combo.add("-Load From Source-");
		combo.select(0);
		combo.redraw();
	}

	public HashMap<String, Object> getParameters() {
		return m_parametersvalues;
	}
	
	@Override
	public void dispose() {
		m_parametersvalues = collectParameters();
	}
	
	public HashMap<String, Object> collectParameters() {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		Iterator<String> keyiterator = m_parameters.keySet().iterator();
		while(keyiterator.hasNext())
		{
			String parametername = keyiterator.next();
			Object parameter = m_parameters.get(parametername);
			if(parameter instanceof Combo) {
				String parametervalue = ((Combo)parameter).getText();
				for(Object obj : ((IModel)m_datasets).getChildren())
				{
					if(obj instanceof IDataContent)
					{
						IDataContent data = (IDataContent) obj;
						if(data.getLabel().contentEquals(parametervalue))
						{
							parameters.put(parametername, data);
							break;
						}
					}
				}
			} else if(parameter instanceof Text) {
				String parametervalue = ((Text)parameter).getText();
				try {
					parameters.put(parametername, new Double(parametervalue));
				} catch(Exception e) {
					
				}
			}
		}
		
		return parameters;
	}
}

