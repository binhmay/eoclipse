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

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.processing.IParameter;

/**
 * @author leforthomas
 */
public class Parameter implements IParameter {
	
	private String m_name;
	private ArrayList<String> m_supportedFormats;
	private String m_type;

	public Parameter(String name, String type, ArrayList<String> formats) {
		// TODO Auto-generated constructor stub
		m_name = name;
		m_type = type;
		m_supportedFormats = formats;
	}
	
	public boolean isSupported(IDataContent data) {
		String format = data.getDataFormat();
		String[] formatparts = format.split("_");
		int length = Array.getLength(formatparts);
		for(String supformat : m_supportedFormats)
		{
			String[] supformatparts = supformat.split("_");
			if(Array.getLength(supformatparts) == length) {
				for(int count = 0; count < length; count++) {
					String value = supformatparts[count];
					if(value.contentEquals(formatparts[count]) || value.contentEquals("ALL")) {
						return true;
					}
				}
			}
			
		}
		
		return false;
	}
	
	public String getName() {
		return m_name;
	}

	@Override
	public ArrayList<String> getSupportedFormats() {
		// TODO Auto-generated method stub
		return m_supportedFormats;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return m_type;
	}
	
}
