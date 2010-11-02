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
package com.metaaps.eoclipse.viewers;

import java.util.ArrayList;
import java.util.List;

import com.metaaps.eoclipse.common.IWorkFlow;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.views.IViewerImplementation;
import com.metaaps.eoclipse.common.views.IViewerItem;
import com.metaaps.eoclipse.common.views.IViewers;

/**
 * @author leforthomas
 * 
 * Utility Class for retrieving a viewer
 * 
 */
public class AllViewers implements IViewers {

	public AllViewers() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IViewerItem findViewer(String viewID) {
		// TODO Auto-generated method stub
		return Viewers.getInstance().findViewer(viewID);
	}

}
