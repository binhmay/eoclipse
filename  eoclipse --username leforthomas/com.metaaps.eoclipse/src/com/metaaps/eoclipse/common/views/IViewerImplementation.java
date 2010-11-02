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
package com.metaaps.eoclipse.common.views;

import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;

import com.metaaps.eoclipse.common.IEvent;
import com.metaaps.eoclipse.common.IModelChangeListener;
import com.metaaps.eoclipse.common.IWorkFlow;
import com.metaaps.eoclipse.common.datasets.IDataSets;

/**
 * @author leforthomas
 * 
 * Interface for all Viewers Implementations
 * 
 */
public interface IViewerImplementation extends IModelChangeListener {

	String getViewid();

	String getName();
	void setName(String name);
	
	// refresh method called when the data has changed
	void refresh();

	void setWorkFlow(IWorkFlow workflow);
	String getWorkFlowID();

}
