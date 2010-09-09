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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ViewPart;

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

	public Viewer(IExtension extension) {
		m_extension = extension;
		IConfigurationElement[] elements =
            extension.getConfigurationElements();
		for(IConfigurationElement element : elements)
		{
			if(element.getName().contentEquals("Viewer"))
			{
				String iconpath = element.getAttribute("icon");
				m_imagedescriptor = Activator.imageDescriptorFromPlugin(extension.getNamespaceIdentifier(), iconpath);
				m_name = element.getAttribute("name");
				Util.addPopupMenu("popup:navigatorcontent.popupmenu.viewers", Activator.PLUGIN_ID, getLabel(), "com.metaaps.eoclipse.viewers.vieweritem", "com.metaaps.eoclipse.viewers.viewer", IDataSets.class, null);
				break;
			}
		}
	}
	
	public void Open(IWorkFlow workflow)
	{
		// TODO Auto-generated method stub
		if(m_executeObj  == null)
		{
			IConfigurationElement[] elements =
	            m_extension.getConfigurationElements();
			for(IConfigurationElement element : elements)
			{
				if(element.getName().contentEquals("Viewer"))
				{
					try {
						m_executeObj = (IViewerFactory) element.createExecutableExtension("Class");
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
			final IWorkFlow fworkflow = workflow;
			final IViewerFactory viewerfactory = (IViewerFactory)m_executeObj;
//			IRunnableWithProgress runnable = new IRunnableWithProgress() {
//				
//				@Override
//				public void run(IProgressMonitor monitor) throws InvocationTargetException,
//						InterruptedException {
//					viewerfactory.open(fworkflow);
//				}
//			};
			ISafeRunnable runnable = new ISafeRunnable() {
				private String m_resultFormat;

				@Override
				public void handleException(Throwable exception) {
					System.out.println("Exception in client");
				}
	
				@Override
				public void run() throws Exception {
						viewerfactory.open(fworkflow);
					}
				};
			SafeRunner.run(runnable);
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
