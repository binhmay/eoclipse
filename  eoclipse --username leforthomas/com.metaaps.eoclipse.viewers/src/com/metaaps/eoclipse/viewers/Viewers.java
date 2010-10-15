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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.part.ViewPart;

import com.metaaps.eoclipse.common.Folder;
import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.Property;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.views.ILayer;
import com.metaaps.eoclipse.common.views.ILayeredViewer;
import com.metaaps.eoclipse.common.views.IViewerImplementation;
import com.metaaps.eoclipse.common.views.IViewerItem;
import com.metaaps.eoclipse.viewers.layers.LayerContent;
import com.metaaps.eoclipse.viewers.util.EditValueDialog;

/**
 * @author leforthomas
 * 
 * Viewers folder with a set of utilities for binding views and viewers together
 * 
 */
public class Viewers extends Folder implements IRegistryChangeListener, IDoubleClickListener, ISelectionChangedListener {
	
	private static String extensionpoint = "com.metaaps.eoclipse.viewers";
	
	private static ImageDescriptor m_imagedescriptorActionLayerUp = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/arrow-curve-090.png");
	private static ImageDescriptor m_imagedescriptorActionLayerDown = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/arrow-curve-270.png");
	
	private static Viewers m_instance = null;

	private Action m_layerdown;

	private Action m_layerup;

	protected Viewers() {
		IExtensionPoint viewers = Platform.getExtensionRegistry().getExtensionPoint(extensionpoint);
		if(viewers != null)
		{
			IExtension[] extensions = viewers.getExtensions();
			for (IExtension e : extensions) {
				scanForExtension(e);
			}
		}
		
		// add a listener to track for new plugins with the extension
		Platform.getExtensionRegistry().addRegistryChangeListener(this, extensionpoint);
		
	}
	
	public void OpenLayersView() {
		IWorkbenchWindow workbench = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		// check if view has already been opened
		if(getLayerView() == null) {
			// create layer panel
			try {
				IWorkbenchPage activepage = workbench.getActivePage();
				activepage.showView(LayerContent.layerViewID);
				CommonNavigator layerView = getLayerView();
				final CommonViewer layerviewer = layerView.getCommonViewer();
				if(layerviewer != null) {
					// add Listeners
					layerviewer.addDoubleClickListener(this);
					layerviewer.addSelectionChangedListener(this);
					// add ViewPart listeners
					activepage.addPartListener(new IPartListener2() {
						
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
					IToolBarManager toolbarmanager = layerView.getViewSite().getActionBars().getToolBarManager();
					toolbarmanager.add(m_layerup);
					toolbarmanager.add(m_layerdown);
				}
			} catch (PartInitException e) {
				e.printStackTrace();
				Util.errorMessage("Could not open the Layers View");
				return;
			}
		}
		
	}
	
	private void makeActions() {
		CommonNavigator layerView = getLayerView();
		final CommonViewer layerviewer = layerView.getCommonViewer();
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

	protected void removeViewerImp(IViewerImplementation viewerimp) {
		IViewerItem viewer = findViewer(viewerimp.getViewid());
		viewer.removeChild(viewerimp);
		getLayerView().getCommonViewer().refresh(true);
	}

	public LayerContent getLayerView() {
		IWorkbenchWindow workbench = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IViewReference viewreference = workbench.getActivePage().findViewReference(LayerContent.layerViewID);
		if(viewreference == null) return null;
		IViewPart viewpart = viewreference.getView(false);
		return (LayerContent) viewpart;
	}

	private void scanForExtension(IExtension extension) {
		IConfigurationElement[] elements = extension.getConfigurationElements();
		for(IConfigurationElement element : elements)
		{
			if(element.getName().contentEquals("Viewer"))
			{
				Viewer viewer = new Viewer(extension, element);
				addChild(viewer);
			}
			if(element.getName().contentEquals("GenericViewer"))
			{
				String name = element.getAttribute("name");
				String viewid = element.getAttribute("viewid");
				HashMap<String, String> parameters = new HashMap<String, String>();
				parameters.put("com.metaaps.eoclipse.viewers.viewerid", viewid);
				Util.addMenu("menu:com.metaaps.eoclipse.menu.genericviews", Activator.PLUGIN_ID, name, "com.metaaps.eoclipse.viewers.genericviewer", "com.metaaps.eoclipse.viewers.genericviewer.open", parameters);
			}
		}
	}

	public static Viewers getInstance() {
		if(m_instance == null) {
			m_instance = new Viewers();
		}
		
		return m_instance;
	}

	@Override
	public void registryChanged(IRegistryChangeEvent event) {
		IExtensionDelta[] deltas = event.getExtensionDeltas(extensionpoint);
		for (IExtensionDelta delta : deltas) {
			System.out.println("Evaluating extension");
			if (delta.getKind() == IExtensionDelta.ADDED) {
				scanForExtension(delta.getExtension());
				fireChanged(this,Model.ADDED);
			} else {
				IExtension ext = delta.getExtension();
				for(Object obj : m_children)
				{
					Viewer viewer = (Viewer)obj;
					if(viewer.getExtension() == ext)
					{
						m_children.remove(viewer);
						fireChanged(viewer,Model.REMOVED);
					}
				}
			}
		}
		
	}

	public IViewerItem findViewer(String viewID) {
		
		for(Object obj : getChildren()) {
			if(obj instanceof IViewerItem) {
				Viewer viewer = (Viewer) obj;
				if(viewer.getViewID().contentEquals(viewID)) {
					return viewer;
				}
			}
		}
		
		return null;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if(event.getSelection() instanceof IStructuredSelection) {
			Object selection = ((IStructuredSelection)event.getSelection()).getFirstElement();
			if(selection instanceof ILayer) {
				((ILayer) selection).selectionChanged();
			}
		}
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		if(event.getSelection() instanceof IStructuredSelection) {
			Object selection = ((IStructuredSelection)event.getSelection()).getFirstElement();
			if(selection instanceof ViewPart) {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				page.activate((ViewPart) selection);
			}
			if(selection instanceof ILayer) {
				((ILayer) selection).selectionDoubleClick();
			}
			if(selection instanceof Property) {
				Property property = EditValueDialog.open((Property) selection);
				if(property != null) {
					// update the layer
					ILayer layer = (ILayer) Util.scanTreePath((ITreeSelection) event.getSelection(), ILayer.class);
					if(layer != null) {
						layer.setLayerProperty(property.getProperty(), property.getValue());
						layer.refresh();
						IViewerImplementation viewer = (IViewerImplementation) Util.scanTreePath((ITreeSelection) event.getSelection(), IViewerImplementation.class);
						if(viewer != null) {
							viewer.refresh();
						}
						getLayerView().getCommonViewer().refresh(layer);
					}
				}
			}
		}
	}
}
