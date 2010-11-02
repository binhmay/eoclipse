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

import java.util.ArrayList;
import java.util.Arrays;

import com.metaaps.eoclipse.common.IModel;
import com.metaaps.eoclipse.common.IWorkFlow;
import com.metaaps.eoclipse.common.ModelChangeListener;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.datasets.importmethods.ImportFolder;
import com.metaaps.eoclipse.workflowmanager.DataSets;

/**
 * @author leforthomas
 */
public class ContentProvider extends ModelChangeListener {
	
	static private final Object[] EMPTY_ARRAY = {};

	@Override
	public void dispose() {

	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IDataSets) {
			IDataSets dataset = (IDataSets) parentElement;
			ArrayList<Object> children = new ArrayList<Object>(Arrays.asList(dataset.getChildren()));
			children.add(ImportFolder.getInstance());
			return children.toArray();
		} if(parentElement instanceof ImportFolder)
		{
			return ((IModel)parentElement).getChildren();
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
