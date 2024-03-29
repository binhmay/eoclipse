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
package com.metaaps.eoclipse.processing.NCE;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.processing.IProcess;
import com.metaaps.eoclipse.common.processing.IProcessingFolder;

/**
 * @author leforthomas
 */
public class LabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {

	}

	@Override
	public Image getImage(Object element) {
		if((element instanceof IProcessingFolder) || (element instanceof IProcess))
		{
			ImageDescriptor imagedescriptor = ((Model)element).getImageDescriptor();
			return (imagedescriptor != null ? imagedescriptor.createImage() : null);
		}

		
		return null;
	}

	@Override
	public String getText(Object element) {
		if((element instanceof IProcessingFolder) || (element instanceof IProcess))
		{
			return ((Model)element).getLabel();
		}
		
		return null;
	}

}
