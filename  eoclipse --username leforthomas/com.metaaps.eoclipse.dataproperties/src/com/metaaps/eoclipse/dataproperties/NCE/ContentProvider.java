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
package com.metaaps.eoclipse.dataproperties.NCE;

import java.util.HashMap;
import java.util.Iterator;

import com.metaaps.eoclipse.common.Folder;
import com.metaaps.eoclipse.common.ModelChangeListener;
import com.metaaps.eoclipse.common.Property;
import com.metaaps.eoclipse.common.datasets.IDataContent;

/**
 * @author leforthomas
 */
public class ContentProvider extends ModelChangeListener {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof Folder)
		{
			return ((Folder)parentElement).getChildren();
		} else if(parentElement instanceof IDataContent)
		{
			IDataContent data = (IDataContent)parentElement;
			Folder propertiesfolder = new Folder();
			HashMap<String, Object> properties = data.getProperties();
			if(properties == null) {
				return EMPTY_ARRAY;
			}
			Iterator<String> propertykeys = properties.keySet().iterator();
			while(propertykeys.hasNext())
			{
				try {
					String key = (String)propertykeys.next();
					String property = properties.get(key).toString();
					propertiesfolder.addChild(new Property(key, property));
				} catch(NullPointerException e) {
					
				}
			}
			//data.addChild(propertiesfolder);
			return new Object[]{propertiesfolder};
		} else if(parentElement instanceof String)
		{
			return new Object[]{parentElement};
		}
		
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		return getChildren(element).length > 0;
	}

}

