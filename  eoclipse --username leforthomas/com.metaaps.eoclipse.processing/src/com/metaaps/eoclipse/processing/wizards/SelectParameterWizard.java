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

import org.eclipse.jface.wizard.Wizard;

import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.processing.Process;

public class SelectParameterWizard extends Wizard {

	private SelectParametersPage m_parameterspage;
	private IDataContent m_data = null;
	private Process m_process;
	private HashMap<String, Object> m_parametervalues;

	public SelectParameterWizard(IDataContent data, Process process, HashMap<String, Object> parametervalues) {
		super();
		setNeedsProgressMonitor(true);
		m_data  = data;
		m_process = process;
		m_parametervalues = parametervalues;
	}

	@Override
	public void addPages() {
		m_parameterspage = new SelectParametersPage(m_data, m_process, m_parametervalues);
		addPage(m_parameterspage);
	}
	
	public HashMap<String, Object> getParameters() {
		return m_parameterspage.getParameters();
	}

	@Override
	public boolean performFinish() {
		return true;
	}
}
