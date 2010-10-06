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

import com.metaaps.eoclipse.common.ModelChangeListener;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.processing.IProcess;
import com.metaaps.eoclipse.processing.ProcessingFolder;

/**
 * @author leforthomas
 */
public class ContentProvider extends ModelChangeListener {

	@Override
	public void dispose() {

	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof ProcessingFolder)
		{
			return ((ProcessingFolder)parentElement).getChildren();
		} else if(parentElement instanceof IDataContent)
		{
			// do not attach the ProcessingFolder to the workflow structure
			return new Object[]{new ProcessingFolder((IDataContent)parentElement)};
		} else if(parentElement instanceof IProcess)
		{
			// left blank intentionally
			// for a process NCE to display parameters and description of processes
		}
		
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

}

