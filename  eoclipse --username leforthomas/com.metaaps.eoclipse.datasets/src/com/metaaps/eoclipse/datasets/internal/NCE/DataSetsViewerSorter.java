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

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.datasets.importmethods.ImportFolder;

/**
 * @author leforthomas
 */
public class DataSetsViewerSorter extends ViewerSorter {

	public DataSetsViewerSorter() {
		// TODO Auto-generated constructor stub
	}

	public DataSetsViewerSorter(Collator collator) {
		super(collator);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if((e1 instanceof IDataContent) && (e2 instanceof IDataContent)) {
			return 0;
		}
		if((e1 instanceof IDataContent) && !(e2 instanceof IDataContent)) {
			return -1;
		}
		return super.compare(viewer, e1, e2);
	}

}
