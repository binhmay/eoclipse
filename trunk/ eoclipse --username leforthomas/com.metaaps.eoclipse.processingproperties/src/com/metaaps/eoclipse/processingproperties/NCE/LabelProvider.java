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
package com.metaaps.eoclipse.processingproperties.NCE;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.processing.IParameter;
import com.metaaps.eoclipse.processingproperties.Activator;

/**
 * @author leforthomas
 */
public class LabelProvider implements ILabelProvider {

	private static ImageDescriptor m_imagedescriptorparameter = Activator.imageDescriptorFromPlugin("com.metaaps.eoclipse.common", "icons/bookmark.png");
	private static ImageDescriptor m_imagedescriptorproperty = Activator.imageDescriptorFromPlugin("com.metaaps.eoclipse.common", "icons/tag-small.png");

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
		if(element instanceof IParameter)
		{
			return m_imagedescriptorparameter.createImage();
		} else if(element instanceof String)
		{
			return m_imagedescriptorproperty.createImage();
		}
		
		return null;
	}

	@Override
	public String getText(Object element) {
		if(element instanceof IParameter)
		{
			return ((IParameter)element).getName();
		} else if(element instanceof String)
		{
			return "sup: " + (String)element;
		}
		
		return null;
	}

}
