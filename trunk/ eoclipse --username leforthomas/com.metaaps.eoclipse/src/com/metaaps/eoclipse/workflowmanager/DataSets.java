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
package com.metaaps.eoclipse.workflowmanager;

import java.io.File;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.jdom.Element;

import com.metaaps.eoclipse.Activator;
import com.metaaps.eoclipse.common.CodeFragment;
import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.datasets.IImport;
import com.metaaps.eoclipse.common.datasets.IReader;

/**
 * @author leforthomas
 * 
 * The DataSets is the collection of data items contained in a workflow
 * 
 */
public class DataSets extends Model implements IDataSets {

	private static ImageDescriptor m_imagedescriptor = Activator
			.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
					"icons/report-paper.png");
	
	public DataSets() {
	}

	@Override
	public String getLabel() {
		return "Data Sets";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return m_imagedescriptor;
	}

	@Override
	public String getId() {
		return "datasets";
	}

	public void addDataContent(IDataContent data, String dataname, String dataid) {
		// check name is unique first
		dataname = getUniqueName(data, dataname);
		data.setName(dataname);
		data.setDataId(dataid);
		addChild(data);
		fireChanged(data, Model.ADDED);
	}

	@Override
	public void addDataContent(IDataContent data, String dataname) {
		addDataContent(data, dataname, generateDataId());
	}

	private String generateDataId() {
		return "data" + UUID.randomUUID().toString();
	}

	@Override
	public void removeDataContent(IDataContent data) {
		removeChild(data);
		fireChanged(data, Model.REMOVED);
	}

	/**
	 * 
	 * Ensures the name of the data item in the dataset is unique
	 *
	 * @return the unique name
	 */
	public String getUniqueName(IDataContent data, String basename) {
		if((basename == null) || basename.contentEquals("")) {
			basename = "Data";
		}
		int count = 0;
		while (!isUniqueName(data, basename, count)) {
			count++;
			if (count > 100) {
				break;
			}
		}

		return (count == 0 ? basename : basename + count);
	}

	public boolean isUniqueName(IDataContent data, String basename, int count) {
		// scan tree to make sure no other data has the same label
		String name = basename + (count > 0 ? count : "");
		for (Object obj : getChildren()) {
			if (obj instanceof IDataContent) {
				if(obj != data)
				{
					if (name.contentEquals(((IDataContent) obj).getName())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public IDataContent findDataFromId(String parameterid) {
		for(Object obj : getChildren()) {
			if(obj instanceof IDataContent) {
				IDataContent datacontent = (IDataContent) obj;
				if(datacontent.getDataId().contentEquals(parameterid)) {
					return datacontent;
				}
			}
		}
		return null;
	}

	@Override
	public void importDataContent(IImport importmethod, IReader reader, String dataname) {
		importDataContent(importmethod, reader, dataname, generateDataId());
	}
		
	/**
	 * Import the data into the datasets using the provided import method and reader
	 * Gives the dataname to the data item in the tree
	 *
	 */
	public void importDataContent(IImport importmethod, IReader reader, String dataname, final String dataid) {
		
		final IImport fimportmethod = importmethod;
		final IReader freader = reader;
		final String fdataname = dataname;
		
		Job job = new Job("Loading Data") {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
		        monitor.beginTask("Get File...", 100);
				// get file first
				File file;
				try {
					file = fimportmethod.importFile(monitor);
			        monitor.beginTask("Read File...", 100);
					// now open it with the reader
					IDataContent datacontent = freader.openFile(file);
					
					CodeFragment code = new CodeFragment(IDataContent.class.getName(), CodeFragment.TYPE.SOURCE);
					Element importcode = new Element(IImport.class.getName());
					importcode.setAttribute("URI", fimportmethod.getURI());
					code.addContent(importcode);
					Element readercode = new Element(IReader.class.getName());
					readercode.setAttribute("name", freader.getName());
					readercode.setAttribute("format", freader.getDataFormat());
					readercode.setAttribute("datatype", freader.getType());
					code.addContent(readercode);
					datacontent.setCode(code);
					
					final IDataContent fdatacontent = datacontent;
					
					Display.getDefault().asyncExec(new Runnable() {
	
						@Override
						public void run() {
							addDataContent(fdatacontent, fdataname, dataid);
					    }
					});
			        monitor.done();
			        return Status.OK_STATUS;
				} catch (final Exception e) {
					Display.getDefault().asyncExec(new Runnable() {
						
						@Override
						public void run() {
							Util.errorMessage("Failed to Import File: " + e.getMessage());
					    }
					});
			        return Status.CANCEL_STATUS;
				}
				
			}
			
		    @Override
		    protected void canceling() {
		    	super.canceling();
		    }
		};
		job.setUser(true);
		job.schedule();
	}
	
}
