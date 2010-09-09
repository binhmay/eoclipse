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
package com.metaaps.eoclipse.viewers.NCE;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.metaaps.eoclipse.common.views.IViewerFactory;
import com.metaaps.eoclipse.common.views.IViewerItem;
import com.metaaps.eoclipse.viewers.Activator;
import com.metaaps.eoclipse.viewers.ViewFolder;
import com.metaaps.eoclipse.viewers.Viewer;

/**
 * @author leforthomas
 */
public class LabelProvider implements ILabelProvider {

	private static ImageDescriptor m_imagedescriptorViewFolder = Activator.imageDescriptorFromPlugin("com.metaaps.eoclipse.common", "icons/monitor-window-3d.png");

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
		if(element instanceof ViewFolder)
		{
			return m_imagedescriptorViewFolder.createImage();
		} else if(element instanceof Viewer) {
			return ((Viewer)element).getImageDescriptor().createImage();
		}
		
		return null;
	}

	@Override
	public String getText(Object element) {
		if(element instanceof ViewFolder)
		{
			return "Available Views";
		} else if(element instanceof Viewer) {
			return ((Viewer)element).getLabel();
		}
		
		return null;
	}

}
