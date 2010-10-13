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

import com.metaaps.eoclipse.common.IModel;

/**
 * @author leforthomas
 * 
 */
public interface IDataSets extends IModel {

	void addDataContent(IDataContent data, String dataname);
	
	void addDataContent(IDataContent result, String dataname, String dataid);

	void importDataContent(IImport importmethod, IReader reader, String string);

	void importDataContent(IImport method, IReader reader, String dataname, String dataid);

	void removeDataContent(IDataContent data);

	IDataContent findDataFromId(String parameterid);

}
