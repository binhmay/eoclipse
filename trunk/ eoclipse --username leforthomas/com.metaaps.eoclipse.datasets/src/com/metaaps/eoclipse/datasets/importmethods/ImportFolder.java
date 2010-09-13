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
package com.metaaps.eoclipse.datasets.importmethods;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;

import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.datasets.Activator;

/**
 * @author leforthomas
 * 
 * The import folder is a singleton to list all available import methods provided as extension points to the import method extension point
 * It also extends the Model class to be attached to the CNE as well as provide context menus at the level of the datasets tree item
 * 
 */
public class ImportFolder extends Model implements IRegistryChangeListener {
	
	private static ImageDescriptor m_imagedescriptor = Activator
	.imageDescriptorFromPlugin("com.metaaps.eoclipse.common",
			"icons/disks.png");
	
	private static ImportFolder m_instance = null;

	protected ImportFolder() {
		// look for extensions
		IExtensionPoint processing = Platform.getExtensionRegistry().getExtensionPoint("com.metaaps.eoclipse.import");
		if(processing != null)
		{
			IExtension[] extensions = processing.getExtensions();
			for (IExtension extension : extensions) {
				scanForExtensions(extension);
			}
		}
		
		// add a listener to track for new plugins with the extension
		Platform.getExtensionRegistry().addRegistryChangeListener(this, "com.metaaps.eoclipse.import");
		
	}
	
	private void scanForExtensions(IExtension extension) {
		IConfigurationElement[] elements = extension.getConfigurationElements();
		for(IConfigurationElement element : elements)
		{
			if(element.getName().contentEquals("Import"))
			{
				ImportMethod importmethod = new ImportMethod(extension, element);
				addChild(importmethod);
			}
		}
	}

	public static ImportFolder getInstance() {
		if(m_instance == null) {
			m_instance = new ImportFolder();
		}
		
		return m_instance;
	}

	@Override
	public String getLabel() {
		return "Import Methods";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return m_imagedescriptor;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public void registryChanged(IRegistryChangeEvent event) {
		IExtensionDelta[] deltas = event.getExtensionDeltas("com.metaaps.eoclipse.import");
		for (IExtensionDelta delta : deltas) {
			System.out.println("Evaluating extension");
			if (delta.getKind() == IExtensionDelta.ADDED) {
				scanForExtensions(delta.getExtension());
				fireChanged(this,Model.ADDED);
			} else {
				IExtension ext = delta.getExtension();
				for(Object obj : m_children)
				{
					ImportMethod importmethod = (ImportMethod)obj;
					if(importmethod.getExtension() == ext)
					{
						m_children.remove(importmethod);
						fireChanged(importmethod,Model.REMOVED);
					}
				}
			}
		}
		
	}

}
