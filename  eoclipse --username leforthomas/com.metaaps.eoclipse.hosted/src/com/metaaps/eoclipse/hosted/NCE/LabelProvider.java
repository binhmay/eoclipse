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
package com.metaaps.eoclipse.hosted.NCE;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.metaaps.eoclipse.common.Folder;
import com.metaaps.eoclipse.hosted.Activator;
import com.metaaps.eoclipse.hosted.Facility;

/**
 * @author leforthomas
 */
public class LabelProvider implements ILabelProvider {

	private static ImageDescriptor m_imagedescriptorFacilityFolder = Activator.imageDescriptorFromPlugin("com.metaaps.eoclipse.common", "icons/weather-cloud.png");

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
			return m_imagedescriptorFacilityFolder.createImage();
		} else if(element instanceof Facility) {
			return ((Facility)element).getImageDescriptor().createImage();
		}
		
		return null;
	}

	@Override
	public String getText(Object element) {
		if(element instanceof Folder)
		{
			return "Hosting Facilities";
		} else if(element instanceof Facility) {
			return ((Facility)element).getLabel();
		}
		
		return null;
	}

}
