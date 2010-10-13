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
package com.metaaps.eoclipse.common.datasets;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;


/**
 * @author leforthomas
 * 
 * interface implemented by all import methods
 * 
 */
public interface IImport {

	void useOwnImport(IDataSets datasets);

	String getURI();

	void setURI(String uRI);

	File importFile(IProgressMonitor monitor) throws Exception;

}
