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
package com.metaaps.eoclipse.export.NCE;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.metaaps.eoclipse.common.Folder;
import com.metaaps.eoclipse.export.Activator;
import com.metaaps.eoclipse.export.ExportFolder;
import com.metaaps.eoclipse.export.Method;
import com.metaaps.eoclipse.export.MethodFolder;

/**
 * @author leforthomas
 */
public class LabelProvider implements ILabelProvider {

	private static ImageDescriptor m_imagedescriptorFacilityFolder = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/table-export.png");

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
		if(element instanceof MethodFolder)
		{
			return m_imagedescriptorFacilityFolder.createImage();
		} else if(element instanceof ExportFolder) {
			return m_imagedescriptorFacilityFolder.createImage();
		} else if(element instanceof Method) {
			return ((Method)element).getImageDescriptor().createImage();
		}
		
		return null;
	}

	@Override
	public String getText(Object element) {
		if(element instanceof MethodFolder)
		{
			return "Export Methods Available";
		} else if(element instanceof ExportFolder) {
			return "Export";
		} else if(element instanceof Method) {
			return ((Method)element).getLabel();
		}
		
		return null;
	}

}
