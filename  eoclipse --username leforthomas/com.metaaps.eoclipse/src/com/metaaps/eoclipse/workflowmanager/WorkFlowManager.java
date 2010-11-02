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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.part.ViewPart;
import org.jdom.Element;

import com.metaaps.eoclipse.Activator;
import com.metaaps.eoclipse.common.IWorkFlow;
import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.datasets.IImports;
import com.metaaps.eoclipse.common.datasets.IReaders;
import com.metaaps.eoclipse.common.processing.IProcesses;
import com.metaaps.eoclipse.common.views.IViewerImplementation;
import com.metaaps.eoclipse.common.views.IViewerItem;
import com.metaaps.eoclipse.common.views.IViewers;

/**
 * @author leforthomas
 * 
 * The top level manager of items. A project is organised in workflows that are themselves managed by the (singleton) work flow manager. See it as a workspace for projects.
 * 
 */
public class WorkFlowManager extends Model {
	
	private static final String CONFIGURER_KEY_FILES = "WorkFlow Files";

	private static final String WORKSPACE_FILES = IWorkFlow.class.getName() + ".Files";

	private CommonNavigator m_navigator = null;

	private String m_initialfilename = null;

	private IWorkbenchWindowConfigurer m_configurer;
	
	private static WorkFlowManager _instance = null;
	
	protected WorkFlowManager() {
	}
	
	public WorkFlow openFile(String filename) throws FileNotFoundException {
    	WorkFlow workflow = new WorkFlow("Initialised");
    	workflow.openFile(filename);
    	addWorkFlow(workflow);
    	//refreshTree();
		return workflow;
	}
	
	public void setConfigurerData(String key, Object value) {
		m_configurer.setData(Activator.PLUGIN_ID + key, value);
	}

	public Object getConfigurerData(String key) {
		return m_configurer.getData(Activator.PLUGIN_ID + key);
	}

	public void refreshTree() {
		if(m_navigator == null) return;
    	// expand the tree
        TreeViewer viewer = (TreeViewer) m_navigator.getCommonViewer();
        viewer.expandAll();
        viewer.refresh();
	}

	public void setNavigator(CommonNavigator navigator)
	{
		m_navigator  = navigator;
	}
	
	public void openWorkSpace() {
		List<Element> workspacefiles = Util.getConfigurationElements(WORKSPACE_FILES);
		if((workspacefiles == null) || (workspacefiles.size() == 0))
			return;
		
		List<Element> files = workspacefiles.get(0).getChildren();
		for(Element fileelement : files) {
			String filename = fileelement.getAttributeValue("name");
			try {
				WorkFlow workflow = openFile(filename);
//				List<Element> views = fileelement.getChildren(IViewerItem.class.getName());
//				for(Element viewelement : views) {
//					String viewid = viewelement.getAttributeValue("viewid");
//					String secondaryid = viewelement.getAttributeValue("secondaryid");
//					IViewerItem viewer = WorkFlowManager.getInstance().getViewers().findViewer(viewid);
//					if(viewer != null) {
//						viewer.Open(workflow, secondaryid);
//					} else {
//						Util.errorMessage("Could not Open View with view ID: " + viewid);
//					}
//				}
			} catch (FileNotFoundException e) {
				Util.errorMessage("Could not find file " + filename);
			}
		}
		
//		// now open the views with their workflow
//		for(Element fileelement : files) {
//			String filename = fileelement.getAttributeValue("name");
//			try {
//				WorkFlow workflow = getWorkflowByFileName(filename);
//				List<Element> views = fileelement.getChildren(IViewerItem.class.getName());
//				for(Element viewelement : views) {
//					String viewid = viewelement.getAttributeValue("viewid");
//					String secondaryid = viewelement.getAttributeValue("secondaryid");
//					IViewerItem viewer = WorkFlowManager.getInstance().getViewers().findViewer(viewid);
//					if(viewer != null) {
//						viewer.Open(workflow, secondaryid);
//					} else {
//						Util.errorMessage("Could not Open View with view ID: " + viewid);
//					}
//				}
//			} catch (Exception e) {
//				Util.errorMessage(e.getMessage());
//			}
//		}
		
	}
	
//	private WorkFlow getWorkflowByFileName(String filename) {
//		for(Object obj : getChildren()) {
//			if(obj instanceof WorkFlow) {
//				WorkFlow workflow = (WorkFlow) obj;
//				if((workflow.getFileName() != null) && workflow.getFileName().contentEquals(filename)) {
//					return workflow;
//				}
//			}
//		}
//		return null;
//	}

	public void saveWorkSpace() {
		Element fileselements = new Element(WORKSPACE_FILES);
		for(Object obj : getChildren())
		{
			if(obj instanceof WorkFlow) {
				WorkFlow workflow = (WorkFlow) obj;
				String filename = workflow.getFileName();
				if((filename == null) || (filename.length() == 0)) {
					if(Util.questionMessage("Unsaved Work Flow", "You are about to leave with an unsaved work flow: " + workflow.getLabel() + "\nDo you want to save the workflow first?"))
					{
						workflow.save(false);
						filename = workflow.getFileName();
					}
				}
				if((filename != null) && (filename.length() != 0)) {
					Element fileelement = new Element("file").setAttribute("name", filename);
//					IDataSets datasets = (IDataSets) Util.scanTreeChidren(IDataSets.class, workflow);
//					List<IViewerImplementation> viewers = WorkFlowManager.getInstance().getViewers().findDataSetsViewers(datasets);
//					for(IViewerImplementation viewer : viewers) {
//						Element viewerelement = new Element(IViewerItem.class.getName());
//						viewerelement.setAttribute("viewid", viewer.getViewid());
//						viewerelement.setAttribute("secondaryid", ((ViewPart)viewer).getViewSite().getSecondaryId());
//						fileelement.addContent(viewerelement);
//					}
					fileselements.addContent(fileelement);
				}
			}
		}
		Util.setConfiguration(fileselements);
	}

	public void addTreeSelectionListener(ISelectionChangedListener listener) {
		((TreeViewer) m_navigator.getCommonViewer()).addSelectionChangedListener(listener);
	}
	
	public static WorkFlowManager getInstance(){
		if(_instance == null){
			_instance = new WorkFlowManager();
		}
		return _instance;
	}

	public void addWorkFlow(WorkFlow workflow) {
		addChild(workflow);
		fireChanged(workflow, Model.ADDED);
	}

	public IProcesses getProcesses() {
		return (IProcesses) Util.getExtensionPointImplementation("com.metaaps.eoclipse.layer2extensions", "Processes");
	}

	public IImports getImports() {
		return (IImports) Util.getExtensionPointImplementation("com.metaaps.eoclipse.layer2extensions", "Imports");
	}

	public IReaders getReaders() {
		return (IReaders) Util.getExtensionPointImplementation("com.metaaps.eoclipse.layer2extensions", "Readers");
	}

	public IViewers getViewers() {
		return (IViewers) Util.getExtensionPointImplementation("com.metaaps.eoclipse.layer2extensions", "Viewers");
	}
	
	public void setConfigurer(IWorkbenchWindowConfigurer windowConfigurer) {
		m_configurer = windowConfigurer;
	}

	public IWorkFlow findWorklowByID(String id) {
		for(Object obj : getChildren()) {
			if(obj instanceof WorkFlow) {
				WorkFlow workflow = (WorkFlow) obj;
				if(workflow.getId().contentEquals(id)) {
					return workflow;
				}
			}
		}
		return null;
	}

}
