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
package com.metaaps.eoclipse.hosted;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.resource.ImageDescriptor;
import com.metaaps.eoclipse.common.IWorkFlow;
import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.hosted.IFacility;
import com.metaaps.eoclipse.common.hosted.IFacilityItem;

public class Facility extends Model implements IFacilityItem {
	
	private IExtension m_extension;
	private ImageDescriptor m_imagedescriptor;
	private String m_name;
	private Object m_executeObj = null;

	public Facility(IExtension extension) {
		m_extension = extension;
		IConfigurationElement[] elements =
            extension.getConfigurationElements();
		for(IConfigurationElement element : elements)
		{
			if(element.getName().contentEquals("Host"))
			{
				String iconpath = element.getAttribute("icon");
				m_imagedescriptor = Activator.imageDescriptorFromPlugin(extension.getNamespaceIdentifier(), iconpath);
				m_name = element.getAttribute("name");
				break;
			}
		}
	}
	
	public void Host(IWorkFlow workflow)
	{
		// TODO Auto-generated method stub
		if(m_executeObj  == null)
		{
			IConfigurationElement[] elements =
	            m_extension.getConfigurationElements();
			for(IConfigurationElement element : elements)
			{
				if(element.getName().contentEquals("Host"))
				{
					try {
						m_executeObj = (IFacility) element.createExecutableExtension("Class");
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}
		if(m_executeObj != null)
		{
			((IFacility)m_executeObj).host(workflow);
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
