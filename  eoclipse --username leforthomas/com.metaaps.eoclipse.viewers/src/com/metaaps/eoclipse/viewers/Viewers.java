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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.metaaps.eoclipse.common.Folder;
import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.views.IViewerItem;
import com.metaaps.eoclipse.viewers.layers.LayerContent;

public class Viewers extends Folder implements IRegistryChangeListener {
	
	private static String extensionpoint = "com.metaaps.eoclipse.viewers";
	
	private static Viewers m_instance = null;

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
		// check if view has already been opened
		IWorkbenchWindow workbench = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IViewReference viewreference = workbench.getActivePage().findViewReference(LayerContent.layerViewID);
		if(viewreference == null) {
			// create layer panel
			try {
				workbench.getActivePage().showView(LayerContent.layerViewID);
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Util.errorMessage("Could not open the Layers View");
				return;
			}
		}
		
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
			Viewer viewer = (Viewer) obj;
			if(viewer.getViewID().contentEquals(viewID)) {
				return viewer;
			}
		}
		
		return null;
	}
}
