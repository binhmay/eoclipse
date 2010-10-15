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
 * Get a file from a local file system
 * 
 */
public class LocalFile implements IImport {

	private String m_uriextension = "file://";
	private String m_filename;

	public LocalFile() {
	}
	
	@Override
	public File importFile(IProgressMonitor monitor) throws Exception {
		if((m_filename == null) || (m_filename.length() == 0)) {
			throw new Exception("File Name Not Set");
		}
		return new File(m_filename);
	}
	
	public void setFilename(String filename) {
		m_filename = filename;
	}

	@Override
	public void useOwnImport(IDataSets datasets) {
	}

	@Override
	public String getURI() {
		return m_uriextension + m_filename;
	}

	@Override
	public void setURI(String URI) {
		m_filename = URI.replaceFirst(m_uriextension, "");
	}

}
