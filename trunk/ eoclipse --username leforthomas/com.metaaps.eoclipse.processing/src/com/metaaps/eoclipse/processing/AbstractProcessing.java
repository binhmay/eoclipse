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
package com.metaaps.eoclipse.processing;

import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;

import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.processing.IProcessing;

/**
 * 
 * @author leforthomas
 * 
 * Provides an abstract class implementation of IProcessing for the processing elements
 * 
 */
public abstract class AbstractProcessing implements IProcessing {

	private HashMap<String, Object> m_parameters;
	protected IProgressMonitor m_monitor;

	@Override
	public void setParameters(HashMap<String, Object> parameters) {
		m_parameters = parameters;
	}

	@Override
	public HashMap<String, Object> getParameters() {
		return m_parameters;
	}

//	@Override
//	public IDataContent execute(HashMap<String, Object> parametervalues) {
//		return null;
//	}

	@Override
	public void setMonitor(IProgressMonitor monitor) {
		m_monitor = monitor;
	}

}
