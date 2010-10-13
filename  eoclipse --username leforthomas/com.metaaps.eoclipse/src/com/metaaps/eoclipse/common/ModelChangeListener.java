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
package com.metaaps.eoclipse.common;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author leforthomas
 * 
 * class to be extended by all ContentProvider classes of eoclipse
 * 
 */
public abstract class ModelChangeListener implements IModelChangeListener, ITreeContentProvider {

    protected static final Object[] EMPTY_ARRAY = {};
    protected Viewer _viewer;
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		_viewer = viewer;

	}

	@Override
	public void modelChanged(Object element, String event) {
		System.out.println("Model Changed " + event + " " + element.toString());
        TreeViewer viewer = (TreeViewer) _viewer;
        TreePath[] treePaths = viewer.getExpandedTreePaths();
        viewer.refresh();
        viewer.setExpandedTreePaths(treePaths);
		System.out.println("Finished Refresh " + element.toString());
	}

}
