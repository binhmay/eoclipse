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

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

import com.metaaps.eoclipse.common.IWorkFlow;
import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.views.ILayeredViewer;
import com.metaaps.eoclipse.common.views.IViewerImplementation;
import com.metaaps.eoclipse.common.views.IViewerItem;
import com.metaaps.eoclipse.viewers.layers.LayerContent;
import com.metaaps.eoclipse.workflowmanager.WorkFlowManager;

/**
 * @author leforthomas
 * 
 * A Viewer Tree Item instantiating views when required
 * 
 */
public class Viewer extends Model implements IViewerItem {
	
	private IExtension m_extension;
	private ImageDescriptor m_imagedescriptor;
	private String m_name;
	private IConfigurationElement m_configuration;
	private String m_viewid;
	private int m_viewCounter = 0;

	public Viewer(IExtension extension, IConfigurationElement element) {
		m_extension = extension;
		String iconpath = element.getAttribute("icon");
		m_imagedescriptor = Activator.imageDescriptorFromPlugin(extension.getNamespaceIdentifier(), iconpath);
		m_name = element.getAttribute("name");
		m_viewid = element.getAttribute("viewid");
		m_configuration = element;
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("com.metaaps.eoclipse.viewers", getFullExtension());
		Util.addPopupMenu("popup:navigatorcontent.popupmenu.viewers", Activator.PLUGIN_ID, getLabel(), "com.metaaps.eoclipse.viewers.vieweritem", "com.metaaps.eoclipse.viewers.viewer", IDataSets.class, parameters);
	}
	
	@Override
	public void Open(final IWorkFlow workflow)
	{
		IWorkbenchWindow workbench = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		try {
			workbench.getActivePage().showView(m_viewid, new Integer(m_viewCounter).toString(), IWorkbenchPage.VIEW_CREATE);
		} catch (PartInitException e) {
			e.printStackTrace();
			Util.errorMessage("Could not open the View");
			return;
		}
		// open layers view if not already opened
		Viewers.getInstance().OpenLayersView();
		// find datasets from the workflow
		final IDataSets datasets = (IDataSets) Util.searchForInterface(IDataSets.class, workflow.getChildren());
		if(datasets != null) {
			final IViewPart viewer = workbench.getActivePage().findViewReference(m_viewid, new Integer(m_viewCounter).toString()).getView(false);
			if(viewer != null) {
				workbench.getActivePage().activate(viewer);
				final Viewer vieweritem = this;
				IWorkbenchSiteProgressService siteService = (IWorkbenchSiteProgressService)viewer.getSite().getAdapter(IWorkbenchSiteProgressService.class);
				siteService.schedule(new Job("Opening Window") {
										@Override
										protected IStatus run(IProgressMonitor monitor) {
									        monitor.beginTask("Adding Layers...", 100);
											Display.getDefault().syncExec(new Runnable() {
						
												@Override
												public void run() {
													LayerContent layerview = Viewers.getInstance().getLayerView();
													IViewerImplementation viewerimp = (IViewerImplementation)viewer;
													viewerimp.setDataSets(datasets);
													viewerimp.setViewid(m_viewid);
													viewerimp.setName(workflow.getLabel());
													CommonViewer layertreeview = layerview.getCommonViewer();
													vieweritem.addChild(viewer);
													layertreeview.refresh();
													// listen to selection changes in the Tree
													if(viewer instanceof ISelectionChangedListener) {
														WorkFlowManager.getInstance().addTreeSelectionListener((ISelectionChangedListener)viewer);
													}
													// Layer View listens for changes in the viewer implementation
													if(viewer instanceof ILayeredViewer) {
														((ILayeredViewer)viewerimp).addListener(layerview);
													}
												}
												
											});
									        monitor.done();
									        return Status.OK_STATUS;
										}
									},
								0 /* now */,
								true /* use the half-busy cursor in the part */);

			}
		}
		m_viewCounter++;
		
	}

	@Override
	public String getLabel() {
		return m_name;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return m_imagedescriptor;
	}

	@Override
	public String getId() {
		return null;
	}

	public IExtension getExtension() {
		return m_extension;
	}

	public String getFullExtension() {
		return m_configuration.getContributor().getName() + ":" + m_name;
	}

	@Override
	public String getViewID() {
		// TODO Auto-generated method stub
		return m_viewid;
	}
	
//	@Override
//	public Object[] getChildren() {
//		// look for views that are opened
//		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		page.findViewReference(getViewID());
//	}

}
