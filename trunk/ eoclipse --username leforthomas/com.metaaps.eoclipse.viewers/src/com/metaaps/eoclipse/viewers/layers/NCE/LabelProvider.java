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
package com.metaaps.eoclipse.viewers.layers.NCE;

import java.awt.Color;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.metaaps.eoclipse.common.Property;
import com.metaaps.eoclipse.common.views.ILayer;
import com.metaaps.eoclipse.common.views.IViewerImplementation;
import com.metaaps.eoclipse.viewers.Activator;
import com.metaaps.eoclipse.viewers.Viewer;
import com.metaaps.eoclipse.viewers.util.EditValueDialog;

/**
 * @author leforthomas
 * 
 */
public class LabelProvider implements ILabelProvider {

	private static ImageDescriptor m_imagedescriptorLayerManager = Activator.imageDescriptorFromPlugin("com.metaaps.eoclipse.viewers", "icons/layers.png");
	private static ImageDescriptor m_imagedescriptorLayer = Activator.imageDescriptorFromPlugin("com.metaaps.eoclipse.viewers", "icons/layer.png");
	private static ImageDescriptor m_imagedescriptorProperty = Activator.imageDescriptorFromPlugin("com.metaaps.eoclipse.viewers", "icons/property.png");
	
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
		if(element instanceof Viewer) {
			return ((Viewer)element).getImageDescriptor().createImage();
		} else if(element instanceof IViewerImplementation) {
			return m_imagedescriptorLayerManager.createImage();
		} else if(element instanceof ILayer) {
			return m_imagedescriptorLayer.createImage();
		} else if(element instanceof Property) {
			return m_imagedescriptorProperty.createImage();
		}
		
		return null;
	}

	@Override
	public String getText(Object element) {
		if(element instanceof Viewer) {
			return ((Viewer)element).getLabel();
		} else if(element instanceof IViewerImplementation) {
			return ((IViewerImplementation) element).getName();
		} else if(element instanceof ILayer) {
			return ((ILayer) element).getName();
		} else if(element instanceof Property) {
			Property property = (Property) element;
			return property.getProperty() + ':' + EditValueDialog.format(property.getValue());
		}
		
		return null;
	}

}
