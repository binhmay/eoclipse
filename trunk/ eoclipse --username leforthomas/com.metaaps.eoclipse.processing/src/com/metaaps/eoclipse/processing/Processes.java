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
package com.metaaps.eoclipse.processing;

import java.util.ArrayList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;
import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.processing.IProcess;
import com.metaaps.eoclipse.common.processing.IProcessing;

/**
 * @author leforthomas
 * 
 * This class is a singleton and collects all plug-ins extending the process extension point
 * 
 */
public class Processes extends Model implements IRegistryChangeListener {

	private static Processes m_instance;
	
	private static String extensionpoint = IProcessing.extensionPointID;
	
	protected Processes() {
		IExtensionPoint processing = Platform.getExtensionRegistry().getExtensionPoint(extensionpoint);
		if(processing != null)
		{
			scanExtensionPoint(processing);
		}
		
		// add a listener to track for new plugins with the extension
		Platform.getExtensionRegistry().addRegistryChangeListener(this, extensionpoint);
		
	}
	
	private void scanExtensionPoint(IExtensionPoint processing) {
		IExtension[] extensions = processing.getExtensions();
		for (IExtension e : extensions) {
			IConfigurationElement[] elements = e.getConfigurationElements();
			for(IConfigurationElement element : elements)
			{
				if(element.getName().contentEquals(IProcessing.extensionPointName))
				{
					Process process = new Process(e, element);
					addChild(process);
				}
			}
		}
	}

	public static Processes getInstance() {
		if(m_instance == null) {
			m_instance = new Processes();
		}
		
		return m_instance;
	}
	
	public Object[] getChildren(IDataContent data) {
		ArrayList<Process> children = new ArrayList<Process>();
		for(Object obj : m_children)
		{
			Process process = (Process) obj;
			if(process.isSupported(data))
			{
				children.add(process);
			}
		}
		
		return children.toArray();
	}

	@Override
	public void registryChanged(IRegistryChangeEvent event) {
		IExtensionDelta[] deltas = event.getExtensionDeltas(extensionpoint);
		for (IExtensionDelta delta : deltas) {
			System.out.println("Evaluating extension");
			if (delta.getKind() == IExtensionDelta.ADDED) {
				scanExtensionPoint(delta.getExtensionPoint());
				fireChanged(this, Model.ADDED);
			} else {
				IExtension ext = delta.getExtension();
				for(Object obj : m_children)
				{
					Process process = (Process)obj;
					if(process.getExtension() == ext)
					{
						m_children.remove(process);
						fireChanged(process,Model.REMOVED);
					}
				}
			}
		}
		
	}

	public IProcess findProcess(String processingclass) {
		
		for(Object obj : getChildren()) {
			Process process = (Process) obj;
			if(process.getImplementationClass().contentEquals(processingclass)) {
				return process;
			}
		}
		
		return null;
	}

}
