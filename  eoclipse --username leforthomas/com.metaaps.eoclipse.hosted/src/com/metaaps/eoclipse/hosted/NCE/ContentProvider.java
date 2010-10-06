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

import com.metaaps.eoclipse.common.Folder;
import com.metaaps.eoclipse.common.IWorkFlow;
import com.metaaps.eoclipse.common.ModelChangeListener;
import com.metaaps.eoclipse.hosted.FacilityFolder;

/**
 * @author leforthomas
 */
public class ContentProvider extends ModelChangeListener {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof Folder)
		{
			return ((Folder)parentElement).getChildren();
		} else if(parentElement instanceof IWorkFlow)
		{
			FacilityFolder folder = new FacilityFolder();
			return new Object[]{folder};
		}
		
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		return getChildren(element).length > 0;
	}

}

