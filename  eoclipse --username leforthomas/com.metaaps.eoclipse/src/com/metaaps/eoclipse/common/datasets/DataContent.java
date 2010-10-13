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

import com.metaaps.eoclipse.common.CodeFragment;
import com.metaaps.eoclipse.common.Model;

public abstract class DataContent extends Model implements IDataContent {
	
	protected static enum DATA_FORMATS {
		VECTOR_ALL,
		VECTOR_POLYGON,
		VECTOR_POLYLINE,
		VECTOR_POINT
	};

	private CodeFragment m_code = null;
	private String m_format = "";
	private String m_name = "";
	private String m_dataid;

	@Override
	public CodeFragment getCode() {
		return m_code;
	}

	@Override
	public void setCode(CodeFragment code) {
		// TODO Auto-generated method stub
		if(code != null)
		{
			m_code = code;
		}

	}
	
	@Override
	public void setDataFormat(String format) {
		if(format != null)
		{
			m_format = format;
		}
	}
	
	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public void setName(String name) {
		getCode().setAttribute("name", name);
		m_name = name;
	}
	
	@Override
	public String getLabel() {
		return getName();
	}
	
	@Override
	public String getDataId() {
		return m_dataid;
	}
	
	@Override
	public void setDataId(String dataid) {
		getCode().setAttribute("dataid", dataid);
		m_dataid = dataid;
	}
	
	@Override
	public ITableData getTableData() {
		// TODO Auto-generated method stub
		return null;
	}
}
