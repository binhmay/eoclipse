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
package com.metaaps.eoclipse.workflowmanager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.navigator.CommonNavigator;

import com.metaaps.eoclipse.common.Model;

/**
 * @author leforthomas
 * 
 * The top level manager of items. A project is organised in workflows that are themselves managed by the (singleton) work flow manager. See it as a workspace for projects.
 * 
 */
public class WorkFlowManager extends Model {
	
	private CommonNavigator m_navigator = null;

	private String m_initialfilename = null;
	
	private static WorkFlowManager _instance = null;
	
	protected WorkFlowManager() {
	}
	
	public void openFile(String filename) throws FileNotFoundException {
		FileInputStream fileinputstream = new FileInputStream(filename);
    	WorkFlow workflow = new WorkFlow("Initialised");
    	addWorkFlow(workflow);
    	refreshTree();
		workflow.readFromStream(fileinputstream);
	}
	
	public void refreshTree() {
    	// expand the tree
        TreeViewer viewer = (TreeViewer) m_navigator.getCommonViewer();
        viewer.expandAll();
        viewer.refresh();
	}

	public void setNavigator(CommonNavigator navigator)
	{
		m_navigator  = navigator;
		if(m_initialfilename != null) {
			try {
				openFile(m_initialfilename);
				return;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static WorkFlowManager getInstance(){
		if(_instance == null){
			_instance = new WorkFlowManager();
		}
		return _instance;
	}

	@Override
	public String getLabel() {
		return "WorkFlows";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getId() {
		return null;
	}

	public void addWorkFlow(WorkFlow workflow) {
		addChild(workflow);
		fireChanged(workflow, Model.ADDED);
	}

	public void setInitialFile(String filename) {
		m_initialfilename = filename;
		if(m_navigator != null) {
			try {
				openFile(m_initialfilename);
				return;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
}
