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

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;

import com.metaaps.eoclipse.common.Folder;
import com.metaaps.eoclipse.common.Model;

public class ViewFolder extends Folder implements IRegistryChangeListener {
	
	private static String extensionpoint = "com.metaaps.eoclipse.viewers";
	
	private static ViewFolder m_instance = null;

	protected ViewFolder() {
		IExtensionPoint viewers = Platform.getExtensionRegistry().getExtensionPoint(extensionpoint);
		if(viewers != null)
		{
			IExtension[] extensions = viewers.getExtensions();
			for (IExtension e : extensions) {
				Viewer viewer = new Viewer(e);
				addChild(viewer);
			}
		}
		
		// add a listener to track for new plugins with the extension
		Platform.getExtensionRegistry().addRegistryChangeListener(this, extensionpoint);
		
	}

	public static ViewFolder getInstance() {
		if(m_instance == null) {
			m_instance = new ViewFolder();
		}
		
		return m_instance;
	}

	@Override
	public void registryChanged(IRegistryChangeEvent event) {
		IExtensionDelta[] deltas = event.getExtensionDeltas(extensionpoint);
		for (IExtensionDelta delta : deltas) {
			System.out.println("Evaluating extension");
			if (delta.getKind() == IExtensionDelta.ADDED) {
				Viewer viewer = new Viewer(delta.getExtension());
				addChild(viewer);
				fireChanged(viewer,Model.ADDED);
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

}
