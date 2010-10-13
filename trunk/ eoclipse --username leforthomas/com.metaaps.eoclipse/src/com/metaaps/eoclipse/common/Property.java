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
package com.metaaps.eoclipse.common;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author leforthomas
 * 
 * A convenience class for implementing property elements
 * 
 */
public class Property extends Model {
	
	private Object m_value;
	private String m_property;

	public Property(String property, Object value)
	{
		m_property = property;
		m_value = value;
	}
	
	public Object getValue() {
		return m_value;
	}
	
	public String getProperty() {
		return m_property;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setValue(Object value) {
		m_value = value;
	}
	
}
