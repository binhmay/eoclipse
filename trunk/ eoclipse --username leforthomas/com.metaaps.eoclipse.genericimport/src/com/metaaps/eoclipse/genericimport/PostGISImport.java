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
package com.metaaps.eoclipse.genericimport;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.datasets.IImport;

/**
 * @author leforthomas
 *
 * Get data from a PostGresql/PostGIS data base
 * NOT IMPLEMENTED
 * 
 */
public class PostGISImport implements IImport {

	public PostGISImport() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public File importFile(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void useOwnImport(IDataSets datasets) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setURI(String uRI) {
		// TODO Auto-generated method stub

	}

}
