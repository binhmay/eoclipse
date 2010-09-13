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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import com.metaaps.eoclipse.common.IDataSets;
import com.metaaps.eoclipse.common.IWorkFlow;
import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.views.IViewerFactory;
import com.metaaps.eoclipse.common.views.IViewerItem;

public class Viewer extends Model implements IViewerItem {
	
	private IExtension m_extension;
	private ImageDescriptor m_imagedescriptor;
	private String m_name;
	private Object m_executeObj = null;
	private IConfigurationElement m_configuration;

	public Viewer(IExtension extension, IConfigurationElement element) {
		m_extension = extension;
		String iconpath = element.getAttribute("icon");
		m_imagedescriptor = Activator.imageDescriptorFromPlugin(extension.getNamespaceIdentifier(), iconpath);
		m_name = element.getAttribute("name");
		m_configuration = element;
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("com.metaaps.eoclipse.viewers", getFullExtension());
		Util.addPopupMenu("popup:navigatorcontent.popupmenu.viewers", Activator.PLUGIN_ID, getLabel(), "com.metaaps.eoclipse.viewers.vieweritem", "com.metaaps.eoclipse.viewers.viewer", IDataSets.class, parameters);
	}
	
	public void Open(IWorkFlow workflow)
	{
		// TODO Auto-generated method stub
		if(m_executeObj  == null)
		{
			try {
				m_executeObj = (IViewerFactory) m_configuration.createExecutableExtension("Class");
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		if(m_executeObj != null)
		{
			final IWorkFlow fworkflow = workflow;
			final IViewerFactory viewerfactory = (IViewerFactory)m_executeObj;
			Job job = new Job("Opening View " + m_name) {
			    @Override
			    protected IStatus run(final IProgressMonitor monitor) {
			        monitor.beginTask("Starting...", 100);
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							viewerfactory.open(fworkflow);
						}
						
					});
			        monitor.done();
			        return Status.OK_STATUS;
			    }
			    @Override
			    protected void canceling() {
			    	super.canceling();
			    }
			};
			job.schedule();
		}
	}

	@Override
	public String getLabel() {
		return m_name;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return m_imagedescriptor;
	}

	@Override
	public String getId() {
		return null;
	}

	public IExtension getExtension() {
		return m_extension;
	}

	public String getFullExtension() {
		return m_configuration.getContributor().getName() + ":" + m_name;
	}

}
