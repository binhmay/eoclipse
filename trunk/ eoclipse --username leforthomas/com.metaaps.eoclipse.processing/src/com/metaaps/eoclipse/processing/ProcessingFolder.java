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
import java.util.Arrays;

import org.eclipse.jface.resource.ImageDescriptor;

import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.processing.IProcess;
import com.metaaps.eoclipse.common.processing.IProcessingFolder;

/**
 * @author leforthomas
 * 
 * This class provides a tree item for representing the processing elements available for a given data item
 * This class is useful as it gathers a subset only of all the processing elements available in the processes folder 
 * 
 */
public class ProcessingFolder extends Model implements IProcessingFolder {

	private static ImageDescriptor m_imagedescriptorProcessingFolder = Activator.imageDescriptorFromPlugin("com.metaaps.eoclipse.common", "icons/041.png");

	// create a special instance with filtered data
	public ProcessingFolder(IDataContent data) {
		m_children = new ArrayList<Object>(Arrays.asList(Processes.getInstance().getChildren(data)));
	}
	
	@Override
	public String getLabel() {
		return "Processing Modules Available";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return m_imagedescriptorProcessingFolder;
	}

	@Override
	public IProcess findProcess(String processingplugin, String processingname) {
		for(Object obj : getChildren()) {
			if(obj instanceof Process) {
				Process process = (Process) obj;
				if((process.getPluginName() == processingplugin) && (process.getName() == processingname)) {
					return process;
				}
			}
		}
		return null;
	}

}
