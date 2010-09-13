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

import com.metaaps.eoclipse.common.IData;
import com.metaaps.eoclipse.common.IDataSets;
import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.datasets.IImportMethod;
import com.metaaps.eoclipse.datasets.importmethods.ImportFolder;

/**
 * @author leforthomas
 */
public class LabelProvider implements ILabelProvider {

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
		if((element instanceof IDataSets) || (element instanceof IData) || (element instanceof ImportFolder) || (element instanceof IImportMethod))
		{
			ImageDescriptor imagedescriptor = ((Model)element).getImageDescriptor();
			return (imagedescriptor != null ? imagedescriptor.createImage() : null);
		}
		
		return null;
	}

	@Override
	public String getText(Object element) {
		if((element instanceof IDataSets) || (element instanceof IData) || (element instanceof ImportFolder) || (element instanceof IImportMethod))
		{
			return ((Model)element).getLabel();
		}
		
		return null;
	}

}
