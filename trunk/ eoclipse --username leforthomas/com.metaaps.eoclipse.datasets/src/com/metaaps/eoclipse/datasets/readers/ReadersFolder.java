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
package com.metaaps.eoclipse.datasets.readers;

import java.util.ArrayList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;

import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.datasets.IReader;

/**
 * @author leforthomas
 * 
 * The readers folder is a singleton to list all available readers provided as extension points to the reader extension point
 * It extends the Model class for future provision, it is currently not used
 * 
 */
public class ReadersFolder extends Model implements IRegistryChangeListener {
	
	private static ReadersFolder m_instance = null;

	protected ReadersFolder() {
		// look for extensions
		IExtensionPoint processing = Platform.getExtensionRegistry().getExtensionPoint("com.metaaps.eoclipse.reader");
		if(processing != null)
		{
			IExtension[] extensions = processing.getExtensions();
			for (IExtension e : extensions) {
				scanForExtensions(e);
			}
		}
		
		// add a listener to track for new plugins with the extension
		Platform.getExtensionRegistry().addRegistryChangeListener(this, "com.metaaps.eoclipse.reader");
		
	}
	
	private void scanForExtensions(IExtension e) {
		IConfigurationElement[] elements = e.getConfigurationElements();
		for(IConfigurationElement element : elements)
		{
			if(element.getName().contentEquals("Reader"))
			{
				Reader reader = new Reader(e, element);
				addChild(reader);
			}
		}
	}

	public static ReadersFolder getInstance() {
		if(m_instance == null) {
			m_instance = new ReadersFolder();
		}
		
		return m_instance;
	}

	@Override
	public void registryChanged(IRegistryChangeEvent event) {
		IExtensionDelta[] deltas = event.getExtensionDeltas("com.metaaps.eoclipse.reader");
		for (IExtensionDelta delta : deltas) {
			System.out.println("Evaluating extension");
			if (delta.getKind() == IExtensionDelta.ADDED) {
				scanForExtensions(delta.getExtension());
			} else {
				IExtension ext = delta.getExtension();
				for(Object obj : m_children)
				{
					Reader reader = (Reader)obj;
					if(reader.getExtension() == ext)
					{
						m_children.remove(reader);
					}
				}
			}
		}
		
	}

	public IReader[] getChildrenWithType(String type) {
		ArrayList<IReader> readers = new ArrayList<IReader>();
		
		for(Object child : getChildren()) {
			if(child instanceof IReader) {
				IReader reader = (IReader) child;
				if(reader.getType() == type) {
					readers.add(reader);
				}
			}
		}
		
		return (IReader[]) readers.toArray();
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getId() {
		return null;
	}

}
