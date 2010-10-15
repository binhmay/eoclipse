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

import com.metaaps.eoclipse.common.processing.IProcess;
import com.metaaps.eoclipse.common.processing.IProcesses;

/**
 * @author leforthomas
 *
 * Utility method for retrieving all available processes
 * 
 */
public class AllProcesses implements IProcesses {

	public AllProcesses() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IProcess findProcess(String processingclass) {
		// TODO Auto-generated method stub
		return Processes.getInstance().findProcess(processingclass);
	}
}
