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

import java.util.HashMap;

import com.metaaps.eoclipse.common.CodeFragment;
import com.metaaps.eoclipse.common.IModel;

/**
 * @author leforthomas
 * 
 * represents a data content
 * 
 */
public interface IDataContent extends IModel {
	
	public static enum TYPE {IMAGE, VECTOR};
	public static String IMAGE = "Image"; 
	public static String VECTOR = "Vector";
	
//	// redundant with the plugins extension attribute Type
//	String getType();
//	void setType(String type);
	// code specifying how the data content was generated
	CodeFragment getCode();
	void setCode(CodeFragment codeFragment);
	// data id management methods
	String getDataId();
	void setDataId(String dataid);
	// format of the data
	String getDataFormat();
	void setDataFormat(String format);
	// name, description and properties of data
	String getName();
	void setName(String name);
	String getDescription();
	// set of key value properties
	HashMap<String, Object> getProperties();
	// viewable and editable data values in a table format
	ITableData getTableData();

}
