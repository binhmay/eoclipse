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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.metaaps.eoclipse.common.IEvent;
import com.metaaps.eoclipse.common.IModelChangeListener;
import com.metaaps.eoclipse.common.IWorkFlow;
import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.views.ILayer;
import com.metaaps.eoclipse.common.views.IViewerImplementation;
import com.metaaps.eoclipse.viewers.Viewers;
import com.metaaps.eoclipse.workflowmanager.WorkFlow;
import com.metaaps.eoclipse.workflowmanager.WorkFlowManager;

/**
 * @author leforthomas
 * 
 * Abstract Implementation for the IViewerImplementation interface
 * 
 */
public abstract class AbstractViewerImplementation extends ViewPart implements IViewerImplementation {
	
	private static final String WORKFLOWID = "com.metaaps.eoclipse.WORKFLOWID";

	protected String m_name;

	protected IDataSets m_datasets = null;
	
	private String workFlowID;

	public final String getWorkFlowID() {
		return workFlowID;
	}

	public final void setWorkFlow(IWorkFlow workflow) {
		if(m_datasets == null) {
			this.workFlowID = workflow.getId();
			// get IDataSets
			IDataSets datasets = workflow.getDataSets();
			setDataSets(datasets);
			datasets.addListener(this);
			setName(workflow.getLabel());
		}
	}

	// for event listeners on the viewer
	protected ArrayList<IModelChangeListener> m_listeners = new ArrayList<IModelChangeListener>(); //NullDeltaListener.getSoleInstance();

	public AbstractViewerImplementation() {
	}
	
	@Override
	public String getViewid() {
		return getViewSite().getId();
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public void setName(String name) {
		m_name = name;
	}

	protected final IDataSets getDataSets() {
		return m_datasets;
	}

	protected void setDataSets(IDataSets datasets) {
		m_datasets = datasets;
	}
	
	public final void fireChanged(Object added, String event) {
		for(IModelChangeListener listener : m_listeners) {
			listener.modelChanged(added, event);
		}
	}

	public final void addListener(IModelChangeListener listener) {
		m_listeners.add(listener);
	}
	
	public final void removeListener(IModelChangeListener listener) {
		m_listeners.remove(listener);
	}
	
	@Override
	public void saveState(IMemento memento) {
		memento.createChild(WORKFLOWID, workFlowID);
		super.saveState(memento);
	}
	
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		// TODO Auto-generated method stub
		super.init(site, memento);
		// use memento to find the datasets allocated
		if(memento != null) {
			IMemento workflowid = memento.getChild(WORKFLOWID);
			if(workflowid != null) {
				workFlowID = workflowid.getID();
			}
			
		}
	}
	
	protected void registerView() {
		final IViewerImplementation viewimp = this;
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				Viewers.getInstance().registerView(viewimp);
				// look for workflow
				if(workFlowID != null) {
					IWorkFlow workflow = WorkFlowManager.getInstance().findWorklowByID(workFlowID);
					if(workflow != null) {
						setWorkFlow(workflow);
					}
				}
			}
		});
	}
	
}
