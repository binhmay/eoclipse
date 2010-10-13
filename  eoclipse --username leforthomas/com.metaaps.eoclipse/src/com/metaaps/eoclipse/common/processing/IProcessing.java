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
package com.metaaps.eoclipse.common.processing;

import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;

import com.metaaps.eoclipse.common.datasets.IDataContent;

/**
 * @author leforthomas
 */
public interface IProcessing {

	String extensionPointID = "com.metaaps.eoclipse.processing";
	String extensionPointName = "Processing";

	IDataContent execute(HashMap<String, Object> parametervalues);

	void setParameters(HashMap<String, Object> parameters);
	
	HashMap<String, Object> getParameters();

	void setMonitor(IProgressMonitor monitor);

}
