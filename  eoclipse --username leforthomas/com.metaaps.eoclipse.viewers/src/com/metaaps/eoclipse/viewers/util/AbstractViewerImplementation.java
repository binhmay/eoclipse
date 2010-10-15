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
package com.metaaps.eoclipse.viewers.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.metaaps.eoclipse.common.IEvent;
import com.metaaps.eoclipse.common.IModelChangeListener;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.views.ILayer;
import com.metaaps.eoclipse.common.views.IViewerImplementation;

/**
 * @author leforthomas
 * 
 * Abstract Implementation for the IViewerImplementation interface
 * 
 */
public abstract class AbstractViewerImplementation extends ViewPart implements IViewerImplementation {
	
	protected String m_viewID = "";
	
	protected String m_name;

	protected IDataSets m_datasets;

	// for event listeners on the viewer
	protected ArrayList<IModelChangeListener> m_listeners = new ArrayList<IModelChangeListener>(); //NullDeltaListener.getSoleInstance();
	
	@Override
	public void setViewid(String viewID) {
		m_viewID = viewID;
	}

	@Override
	public String getViewid() {
		return m_viewID;
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public void setName(String name) {
		m_name = name;
	}

	@Override
	public IDataSets getDataSets() {
		return m_datasets;
	}

	@Override
	public void setDataSets(IDataSets datasets) {
		m_datasets = datasets;
	}
	
	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}
	
	public void fireChanged(Object added, String event) {
		for(IModelChangeListener listener : m_listeners) {
			listener.modelChanged(added, event);
		}
	}

	public void addListener(IModelChangeListener listener) {
		m_listeners.add(listener);
	}
	
	public void removeListener(IModelChangeListener listener) {
		m_listeners.remove(listener);
	}
	
}
