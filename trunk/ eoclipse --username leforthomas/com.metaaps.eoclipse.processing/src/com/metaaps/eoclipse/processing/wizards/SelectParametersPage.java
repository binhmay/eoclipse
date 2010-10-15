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

/**
 * @author leforthomas
 * 
 * Provides a generic wizard page for selecting the process parameters
 * 
 */
public class SelectParametersPage extends WizardPage {
	
	private IDataSets m_datasets;
	private Process m_process;
	private HashMap<String, Object> m_parameters = new HashMap<String, Object>();
	private HashMap<String, Object> m_parametersvalues;

	public SelectParametersPage(IDataContent data, Process process, HashMap<String, Object> parametervalues) {
		super("Select Parameters");
		setTitle("Select the processing parameters for " + process.getName());
		setDescription("Specify the extra parameters required for this plugin");
		
		m_process = process;
		m_datasets = (IDataSets) Util.scanTree(IDataSets.class, data);
		m_parametersvalues = parametervalues;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		
		String parametername = m_parametersvalues.keySet().iterator().next();
		new Label(container, SWT.NULL | SWT.BOLD).setText("Parameter " + parametername + ":");
		new Label(container, SWT.NULL).setText(((IDataContent) m_parametersvalues.get(parametername)).getLabel());
		
		int counter = 0;
		for(Object obj : m_process.getChildren())
		{
			if(obj instanceof Parameter)
			{
				// skip first parameter
				if(counter++ == 0) continue;
				Parameter parameter = (Parameter)obj;
				String name = parameter.getName();
				String description = parameter.getDescription();
				String fullname = "'" + name + "' " + description;
				new Label(container, SWT.NULL);
				Label label = new Label(container, SWT.NULL | SWT.BOLD);
				if(parameter.getType().contentEquals("Value")) {
					// check type
					String format = parameter.getSupportedFormats().get(0);
					String[] formats = format.split(":");
					String type = formats[0];
					double min = -1;
					double max = -1;
					if((formats.length > 1) && !formats[1].contentEquals("*")) {
						min = Double.parseDouble(formats[1]);
					}
					if((formats.length == 3) && !formats[2].contentEquals("*")) {
						max = Double.parseDouble(formats[2]);
					}
					Text text = new Text(container, SWT.SINGLE | SWT.BORDER);
					text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					if(type.contentEquals("STRING")) {
						label.setText("Parameter " + fullname + "(String " + (min == -1 ? "" : "- min char: " + min) + (max == -1 ? "" : " - max char: " + max) + ")");
					}
					if(type.contentEquals("INTEGER") || type.contentEquals("DOUBLE")) {
						label.setText("Parameter " + fullname + "(" + (type.contentEquals("INTEGER") ? "Integer" : "Double") + (min == -1 ? "" : "- min: " + min) + (max == -1 ? "" : " - max: " + max) + ")");
					}
					m_parameters.put(name, text);
				} else if(parameter.getType().contentEquals("Choice")) {
					label.setText("Parameter " + fullname + ":");
					Combo combo = new Combo(container, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.VERTICAL | SWT.BORDER);
					combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					for(String format : parameter.getSupportedFormats()) {
						combo.add(format);
					}
					m_parameters.put(name, combo);
				} else {
					label.setText("Parameter " + fullname + ":");
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
		collectParameters();
	}
	
	public void collectParameters() {
		
		int counter = 0;
		for(Object obj : m_process.getChildren())
		{
			if(obj instanceof Parameter)
			{
				// skip first parameter
				if(counter++ == 0) continue;
				Parameter parameter = (Parameter)obj;
				String name = parameter.getName();
				Object control = m_parameters.get(name);
				if(parameter.getType().contentEquals("Value")) {
					// check type
					String format = parameter.getSupportedFormats().get(0);
					String[] formats = format.split(":");
					String type = formats[0];
					double min = -1;
					double max = -1;
					if((formats.length > 1) && !formats[1].contentEquals("*")) {
						min = Double.parseDouble(formats[1]);
					}
					if((formats.length == 3) && !formats[2].contentEquals("*")) {
						max = Double.parseDouble(formats[2]);
					}
					String parametervalue = ((Text)control).getText();
					if(type.contentEquals("STRING")) {
						m_parametersvalues.put(name, parametervalue);
					}
					if(type.contentEquals("INTEGER")) {
						m_parametersvalues.put(name, new Integer(parametervalue));
					}
					if(type.contentEquals("DOUBLE")) {
						m_parametersvalues.put(name, new Double(parametervalue));
					}
				} else if(parameter.getType().contentEquals("Choice")) {
					String parametervalue = ((Text)control).getText();
					m_parametersvalues.put(name, parametervalue);
				} else {
					String parametervalue = ((Combo)control).getText();
					for(Object dataobj : ((IModel)m_datasets).getChildren())
					{
						if(dataobj instanceof IDataContent)
						{
							IDataContent data = (IDataContent) dataobj;
							if(data.getLabel().contentEquals(parametervalue))
							{
								m_parametersvalues.put(name, data);
								break;
							}
						}
					}
				}
			}
		}
		
//		Iterator<String> keyiterator = m_parameters.keySet().iterator();
//		while(keyiterator.hasNext())
//		{
//			String parametername = keyiterator.next();
//			Object parameter = m_parameters.get(parametername);
//			if(parameter instanceof Combo) {
//				String parametervalue = ((Combo)parameter).getText();
//				for(Object obj : ((IModel)m_datasets).getChildren())
//				{
//					if(obj instanceof IDataContent)
//					{
//						IDataContent data = (IDataContent) obj;
//						if(data.getLabel().contentEquals(parametervalue))
//						{
//							m_parametersvalues.put(parametername, data);
//							break;
//						}
//					}
//				}
//			} else if(parameter instanceof Text) {
//				String parametervalue = ((Text)parameter).getText();
//				try {
//					m_parametersvalues.put(parametername, new Double(parametervalue));
//				} catch(Exception e) {
//					
//				}
//			}
//		}
	}
	
}

