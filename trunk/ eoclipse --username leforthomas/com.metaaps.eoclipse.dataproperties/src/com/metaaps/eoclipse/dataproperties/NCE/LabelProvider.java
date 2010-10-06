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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.metaaps.eoclipse.common.Folder;
import com.metaaps.eoclipse.common.Property;
import com.metaaps.eoclipse.dataproperties.Activator;

/**
 * @author leforthomas
 */
public class LabelProvider implements ILabelProvider {

	private static ImageDescriptor m_imagedescriptorfolder = Activator.imageDescriptorFromPlugin("com.metaaps.eoclipse.common", "icons/property.png");
	private static ImageDescriptor m_imagedescriptorproperty = Activator.imageDescriptorFromPlugin("com.metaaps.eoclipse.common", "icons/property.png");

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getImage(Object element) {
		if(element instanceof Folder)
		{
			return m_imagedescriptorfolder.createImage();
		} else if(element instanceof Property) {
			return m_imagedescriptorproperty.createImage();
		}
		
		return null;
	}

	@Override
	public String getText(Object element) {
		if(element instanceof Folder)
		{
			return "Properties";
		} else if(element instanceof Property) {
			Property property = (Property)element;
			Object value = property.getValue();
			return property.getProperty() + ": " + (value == null ? "undefined" : value.toString());
		}
		
		return null;
	}

}
