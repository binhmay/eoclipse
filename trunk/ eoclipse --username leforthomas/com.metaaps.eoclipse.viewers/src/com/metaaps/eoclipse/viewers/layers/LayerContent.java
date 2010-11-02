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
package com.metaaps.eoclipse.viewers.layers;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

import com.metaaps.eoclipse.common.IModelChangeListener;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.views.ILayer;
import com.metaaps.eoclipse.common.views.ILayeredViewer;
import com.metaaps.eoclipse.common.views.IViewerImplementation;
import com.metaaps.eoclipse.viewers.Activator;
import com.metaaps.eoclipse.viewers.Viewers;

public class LayerContent extends CommonNavigator implements IModelChangeListener {
	
	static public String layerViewID = "com.metaaps.eoclipse.viewers.sidepanel.layers";
	
	private static ImageDescriptor m_imagedescriptorActionLayerUp = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/arrow-curve-090.png");
	private static ImageDescriptor m_imagedescriptorActionLayerDown = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/arrow-curve-270.png");
	
	private Action m_layerdown;

	private Action m_layerup;

	public LayerContent() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void createPartControl(Composite aParent) {
		// TODO Auto-generated method stub
		super.createPartControl(aParent);

		// create layer panel
		try {
			final CommonViewer layerviewer = getCommonViewer();
			if(layerviewer != null) {
				// add Listeners
				layerviewer.addDoubleClickListener(Viewers.getInstance());
				layerviewer.addSelectionChangedListener(Viewers.getInstance());
				// add ViewPart listeners
				getSite().getPage().addPartListener(new IPartListener2() {
					
					@Override
					public void partVisible(IWorkbenchPartReference partRef) {
					}
					
					@Override
					public void partOpened(IWorkbenchPartReference partRef) {
					}
					
					@Override
					public void partInputChanged(IWorkbenchPartReference partRef) {
					}
					
					@Override
					public void partHidden(IWorkbenchPartReference partRef) {
					}
					
					@Override
					public void partDeactivated(IWorkbenchPartReference partRef) {
					}
					
					@Override
					public void partClosed(IWorkbenchPartReference partRef) {
						IWorkbenchPart part = partRef.getPart(false);
						if(part instanceof IViewerImplementation) {
							Viewers.getInstance().removeViewerImp((IViewerImplementation) part);
						}
					}
					
					@Override
					public void partBroughtToTop(IWorkbenchPartReference partRef) {
					}
					
					@Override
					public void partActivated(IWorkbenchPartReference partRef) {
					}
				});
				// add actions to the window
				makeActions();
				IToolBarManager toolbarmanager = getViewSite().getActionBars().getToolBarManager();
				toolbarmanager.add(m_layerup);
				toolbarmanager.add(m_layerdown);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Util.errorMessage("Could not open the Layers View");
		}
	}

	private void makeActions() {
		final CommonViewer layerviewer = getCommonViewer();
		m_layerdown = new Action() {
			public void run() {
				ISelection selection = layerviewer.getSelection();
				Object obj = ((ITreeSelection)selection).getFirstElement();
				if((!selection.isEmpty()) && (obj instanceof ILayer)) {
					ILayer layer = (ILayer) obj;
					ILayeredViewer viewerimp = (ILayeredViewer) Util.scanTreePath((ITreeSelection)selection, IViewerImplementation.class);
					if(viewerimp != null) {
						((ILayeredViewer)viewerimp).moveLayer(layer, false);
						layerviewer.refresh();
					}
				}
			}
		};
		m_layerdown.setText("");
		m_layerdown.setToolTipText("Move Layer Down");
		m_layerdown.setImageDescriptor(m_imagedescriptorActionLayerDown);
		
		m_layerup = new Action() {
			public void run() {
				ISelection selection = layerviewer.getSelection();
				Object obj = ((ITreeSelection)selection).getFirstElement();
				if((!selection.isEmpty()) && (obj instanceof ILayer)) {
					ILayer layer = (ILayer) obj;
					ILayeredViewer viewerimp = (ILayeredViewer) Util.scanTreePath((ITreeSelection)selection, IViewerImplementation.class);
					if(viewerimp != null) {
						((ILayeredViewer)viewerimp).moveLayer(layer, true);
						layerviewer.refresh();
					}
				}
			}
		};
		m_layerup.setText("");
		m_layerup.setToolTipText("Move Layer Down");
		m_layerup.setImageDescriptor(m_imagedescriptorActionLayerUp);
		
	}

	@Override
	protected Object getInitialInput() {
		return Viewers.getInstance();
	}

	// called when layers or viewers have changed and request an update of the layer view
	@Override
	public void modelChanged(Object element, String event) {
		System.out.println("Model Changed " + event + " " + element.toString());
        TreeViewer viewer = getCommonViewer();
        TreePath[] treePaths = viewer.getExpandedTreePaths();
        viewer.refresh();
        viewer.setExpandedTreePaths(treePaths);
		System.out.println("Finished Refresh " + element.toString());
	}

}
