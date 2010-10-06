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

import java.util.ArrayList;
import java.util.Arrays;

import com.metaaps.eoclipse.common.ModelChangeListener;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.export.ExportFolder;
import com.metaaps.eoclipse.export.MethodFolder;

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
		if(parentElement instanceof MethodFolder)
		{
			return MethodFolder.getInstance().getChildren();
		} else if(parentElement instanceof ExportFolder) {
			ArrayList<Object> children = new ArrayList<Object>(Arrays.asList(((ExportFolder)parentElement).getChildren()));
			children.add(MethodFolder.getInstance());
			return children.toArray();
		} else if(parentElement instanceof IDataContent)
		{
			// check if export folder has not been created
			Object exportFolder = Util.searchForInterface(ExportFolder.class, ((IDataContent)parentElement).getChildren());
			if(exportFolder == null) {
				exportFolder = new ExportFolder();
				((IDataContent)parentElement).addChild(exportFolder);
				((ExportFolder)exportFolder).addListener(this);
			}
			return new Object[]{exportFolder};
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

