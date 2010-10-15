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
package com.metaaps.eoclipse.viewers.layers.NCE;

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.metaaps.eoclipse.common.views.ILayer;

/**
 * @author leforthomas
 * 
 */
public class LayerViewerSorter extends ViewerSorter {

	public LayerViewerSorter() {
		// TODO Auto-generated constructor stub
	}

	public LayerViewerSorter(Collator collator) {
		super(collator);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if((e1 instanceof ILayer) && (e2 instanceof ILayer)) {
			// assumes list is already sorted
			return 0;
		}
		return super.compare(viewer, e1, e2);
	}

}
