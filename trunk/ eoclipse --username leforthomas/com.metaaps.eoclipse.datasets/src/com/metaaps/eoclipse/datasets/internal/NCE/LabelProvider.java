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
package com.metaaps.eoclipse.datasets.internal.NCE;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.datasets.IGeoRaster;
import com.metaaps.eoclipse.common.datasets.IImportMethod;
import com.metaaps.eoclipse.common.datasets.ISourceDataContent;
import com.metaaps.eoclipse.datasets.Activator;
import com.metaaps.eoclipse.datasets.importmethods.ImportFolder;

/**
 * @author leforthomas
 */
public class LabelProvider implements ILabelProvider {

	private static ImageDescriptor[] m_imagedescriptor = new ImageDescriptor[]{
		Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/picture.png"),
		Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/node-select-all.png"),
		Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/picture-generated.png"),
		Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/vector-generated.png")
	};
	
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
		ImageDescriptor imagedescriptor = null;
		if((element instanceof ImportFolder) || (element instanceof IImportMethod))
		{
			imagedescriptor = ((Model)element).getImageDescriptor();
		} else if(element instanceof IDataContent) {
			try {
				boolean isimage = element instanceof IGeoRaster;
				boolean issource = element instanceof ISourceDataContent;
				imagedescriptor = m_imagedescriptor[(isimage ? 0 : 1) + (issource ? 0 : 2)];
			} catch (Exception e) {
			}
		}
		
		return (imagedescriptor != null ? imagedescriptor.createImage() : null);
	}

	@Override
	public String getText(Object element) {
		if((element instanceof ImportFolder) || (element instanceof IImportMethod))
		{
			return ((Model)element).getLabel();
		} else if(element instanceof IDataContent) {
			return ((IDataContent) element).getName();
		}
		
		return null;
	}

}
