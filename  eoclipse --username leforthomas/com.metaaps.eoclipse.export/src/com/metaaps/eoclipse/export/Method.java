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
package com.metaaps.eoclipse.export;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.resource.ImageDescriptor;

import com.metaaps.eoclipse.common.IWorkFlow;
import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.export.IExportItem;

/**
 * @author leforthomas
 * 
 */
public class Method extends Model implements IExportItem {
	
	private IExtension m_extension;
	private ImageDescriptor m_imagedescriptor;
	private String m_name;
	private Object m_executeObj = null;

	public Method(IExtension extension) {
		m_extension = extension;
		IConfigurationElement[] elements =
            extension.getConfigurationElements();
		for(IConfigurationElement element : elements)
		{
			if(element.getName().contentEquals("Method"))
			{
				String iconpath = element.getAttribute("icon");
				m_imagedescriptor = Activator.imageDescriptorFromPlugin(extension.getNamespaceIdentifier(), iconpath);
				m_name = element.getAttribute("name");
				break;
			}
		}
	}
	
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return m_name;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return m_imagedescriptor;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public IExtension getExtension() {
		// TODO Auto-generated method stub
		return m_extension;
	}

}
