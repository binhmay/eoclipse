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
import com.metaaps.eoclipse.common.datasets.IDataSets;

/**
 * @author leforthomas
 * 
 * Interface for all Viewers Implementations
 * Viewers listen to the WorkSpace selection changes
 * Viewers provide Events when they change, eg layers are added or removed. This is useful for the layer view to update.
 * 
 */
public interface IViewerImplementation extends ISelectionChangedListener, IEvent {

	void setViewid(String viewID);
	String getViewid();

	String getName();
	void setName(String name);
	
	IDataSets getDataSets();
	void setDataSets(IDataSets datasets);
	
	void refresh();

	// move all layer stuff into one interface
	List<ILayer> getLayers();
	
	void moveLayer(ILayer layer, boolean up);

}
