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
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.jdom.Element;

import com.metaaps.eoclipse.common.CodeFragment;
import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.processing.IParameter;
import com.metaaps.eoclipse.common.processing.IProcess;
import com.metaaps.eoclipse.common.processing.IProcessing;
import com.metaaps.eoclipse.processing.wizards.SelectParameterWizard;

/**
 * @author leforthomas
 * 
 * This class provides a bridge to the processing element extending a process extension point
 * 
 */
public class Process extends Model implements IProcess {
	
	private String m_name;
	private ImageDescriptor m_imagedescriptor;
	private IExtension m_extension;
	private IProcessing m_executeObj;
	private IConfigurationElement m_processing;
	private IConfigurationElement m_confelement;

	public Process(IExtension extension, IConfigurationElement element)
	{
		m_extension = extension;
		m_confelement = element;
		m_processing = element;
		String iconpath = element.getAttribute("icon");
		m_imagedescriptor = Activator.imageDescriptorFromPlugin(extension.getNamespaceIdentifier(), iconpath);
		m_name = element.getAttribute("name");
		// get plugin parameters
		IConfigurationElement[] parameters = element.getChildren();
		for(IConfigurationElement parameter : parameters)
		{
			if(parameter.getName().contentEquals("Parameter"))
			{
				ArrayList<String> formats = new ArrayList<String>();
				IConfigurationElement[] parameterformats = parameter.getChildren("Formats")[0].getChildren("Format");
				for(IConfigurationElement format : parameterformats)
				{
					formats.add(format.getAttribute("Format"));
				}
				
				addChild(new Parameter(parameter.getAttribute("name"), parameter.getAttribute("Type"), (parameter.getAttribute("description")  == null ? "" : parameter.getAttribute("description")), formats));
			}
		}
	}
	
	/**
	 * 
	 * Checks if the data content is a supported format for any of this process parameters
	 * 
	 */
	public boolean isSupported(IDataContent data)
	{
		for(Object obj : m_children)
		{
			// check only for first parameter
			Parameter parameter = (Parameter)obj;
			if(parameter.isSupported(data))
			{
				return true;
			}
			return false;
		}
		
		return false;
	}
	
	public void insertResult(IDataContent data, IDataSets dataset)
	{
		if(dataset != null)
		{
			dataset.addDataContent(data, data.getName());
		} else {
			System.out.println("Datasets is null");
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

	/**
	 * 
	 * Runs the processing element, passing all parameters through a hashmap using the extension points names
	 * Monitor progress by creating a job and passing the monitor object to the processing element
	 * 
	 */
	public void execute(IDataContent data) {
		if(m_executeObj == null)
		{
			try {
				m_executeObj = (IProcessing) m_processing.createExecutableExtension("Class");
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Util.errorMessage("Could not instantiate process");
			}
		}
		if(m_executeObj != null)
		{
			// start wizard to get the parameters
			if(data != null)
			{
	    		final HashMap<String, Object> parametervalues;
				// first parameter is the one that triggered the menu, add to list
				parametervalues = new HashMap<String, Object>();
				Parameter uniqueparam = (Parameter) m_children.toArray()[0];
				parametervalues.put(uniqueparam.getName(), data);
				// if more than one parameter required, open the wizard
				if((m_children != null) && (m_children.size() > 1)) {
		    		SelectParameterWizard wizard = new SelectParameterWizard(data, this, parametervalues);
		    		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), wizard);
		    		if(dialog.open() == WizardDialog.CANCEL){
		    			return;
		    		}
//		    		parametervalues = wizard.getParameters();
				}
				final IDataSets datasets = (IDataSets) Util.scanTree(IDataSets.class, data);
				
				Job job = new Job("Performing Processing " + m_name) {
				    @Override
				    protected IStatus run(final IProgressMonitor monitor) {
						final IDataContent fresult = runProcess(parametervalues, null);
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								if(fresult == null) {
									Util.errorMessage("Execution failed");
									return;
								}
					        	insertResult(fresult, datasets);
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
	}

	@Override
	public IDataContent runProcess(HashMap<String, Object> parametervalues, IProgressMonitor monitor) {
		if(m_executeObj == null)
		{
			try {
				m_executeObj = (IProcessing) m_processing.createExecutableExtension("Class");
			} catch (CoreException e) {
				e.printStackTrace();
				return null;
			}
		}
		if(monitor == null) {
			monitor = new NullProgressMonitor();
		}
        monitor.beginTask("Starting...", 100);
		m_executeObj.setMonitor(monitor);
		IDataContent result = m_executeObj.execute(parametervalues);
		if(result != null) {
			CodeFragment code = new CodeFragment(IDataContent.class.getName(), CodeFragment.TYPE.GENERATED);
			code.setAttribute("name", result.getName());
			// dataid is set later
			// set process
			Element processelement = new Element(IProcess.class.getName());
			processelement.setAttribute("processingclass", m_processing.getAttribute("Class"));
			code.addContent(processelement);
			// add process parameters
			// insert all parameters with their code fragments
			for(Object obj : parametervalues.keySet().toArray()) {
				if(obj instanceof String) {
					String paramname = (String) obj;
					Element parameter = new Element(IParameter.class.getName());
					parameter.setAttribute("name", paramname);
					Object paramobj = parametervalues.get(paramname);
					if(paramobj instanceof IDataContent) {
						parameter.addContent((Element)((IDataContent) paramobj).getCode().clone());
					}
					processelement.addContent(parameter);
				}
			}
			result.setCode(code);
		}
		
		return result;
	}

	public IExtension getExtension() {
		return m_extension;
	}
	
	public String getName() {
		return m_name;
	}

	public String getPluginName() {
		return m_confelement.getContributor().getName();
	}

	public String getImplementationClass() {
		return m_processing.getAttribute("Class");
	}

}
