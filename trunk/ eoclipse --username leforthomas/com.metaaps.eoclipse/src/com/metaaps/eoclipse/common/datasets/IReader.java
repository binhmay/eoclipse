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


/**
 * @author leforthomas
 * 
 * interface to be implemented by all data readers plugins implementations
 * 
 */
public interface IReader {

	String getType();

	String getName();

	String[] getFormatsSupported();

	public IDataContent openFile(File file);

	void setType(String type);

	void setDataFormat(String formatname);

	String getDataFormat();

	String getReader();

	String getFilter();
	
}
