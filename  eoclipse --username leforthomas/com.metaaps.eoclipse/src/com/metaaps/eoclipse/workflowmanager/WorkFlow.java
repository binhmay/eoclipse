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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.*;

import com.metaaps.eoclipse.Activator;
import com.metaaps.eoclipse.common.CodeFragment;
import com.metaaps.eoclipse.common.IModel;
import com.metaaps.eoclipse.common.IWorkFlow;
import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.datasets.IImport;
import com.metaaps.eoclipse.common.datasets.IImportMethod;
import com.metaaps.eoclipse.common.datasets.IImports;
import com.metaaps.eoclipse.common.datasets.IReader;
import com.metaaps.eoclipse.common.datasets.IReaders;
import com.metaaps.eoclipse.common.export.IExportItem;
import com.metaaps.eoclipse.common.processing.IParameter;
import com.metaaps.eoclipse.common.processing.IProcess;
import com.metaaps.eoclipse.common.processing.IProcesses;
import com.metaaps.eoclipse.common.views.IViewerItem;
import com.metaaps.eoclipse.common.views.IViewers;

/**
 * @author leforthomas
 * 
 * A workflow is the equivalent of a project in EOClipse. Workflows contain data imported or generated. Functionality is added to the workflow following the plugins availability and the type of data used.
 *  
 */
public class WorkFlow extends Model implements IWorkFlow {
	
	private static ImageDescriptor m_imagedescriptor = Activator.imageDescriptorFromPlugin("com.metaaps.eoclipse.common", "icons/reports.png");
	
	private String m_name = "";

	private String m_filename = "";
	
	public WorkFlow(String name){
		m_name = name;
	}
	
	public String getName(){
		return m_name;
	}
	
	@Override
	public String getLabel() {
		return getName();
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return m_imagedescriptor;
	}

	@Override
	public String getId() {
		return "Workflow";
	}
	
	public void writeToStream(OutputStream stream) throws IOException {
		Element root = new Element(IWorkFlow.class.getName());
		fillDOMElement(root);
		Document doc = new Document(root);
		XMLOutputter output = new XMLOutputter();
		output.output(doc, stream);
	}
	
	public void fillDOMElement(Element element) {
		element.setAttribute("name", getLabel());
		for(Object obj : getChildren()) {
			if(obj instanceof IDataSets) {
				Element dataset = new Element(IDataSets.class.getName());
				element.addContent(dataset);
				for(Object dataobj : ((IDataSets) obj).getChildren()) {
					if(dataobj instanceof IDataContent) {
						CodeFragment code = ((IDataContent)dataobj).getCode();
						dataset.addContent(code);
					}
				}
			}
		}
	}
	
	public void readFromStream(InputStream stream) {
        Document doc = null;

        SAXBuilder sb = new SAXBuilder();

        try {
            doc = sb.build(stream);
            readDOMElement(doc.getRootElement());
        }
        catch (JDOMException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
	private void collectSourceDataFromDOM(Element code, IDataSets dataset) throws Exception {
			// get code fragment
			CodeFragment.TYPE type = CodeFragment.TYPE.valueOf(code.getAttributeValue("type"));
			if(type == CodeFragment.TYPE.SOURCE) {
				String dataname = code.getAttributeValue("name");
				String dataid = code.getAttributeValue("dataid");
				// check import method and reader
				Element importelement = code.getChild(IImport.class.getName());
				Element readerelement = code.getChild(IReader.class.getName());
				// get URI
				String URI = importelement.getAttributeValue("URI");
				String scheme = (new URI(URI)).getScheme();
				// look for a suitable import method
				IImports allimports = WorkFlowManager.getInstance().getImports();
				IImportMethod importmethod = allimports.findImport(scheme);
				if(importmethod == null) {
					Util.errorMessage("Could not find suitable import for scheme '" + scheme + "'");
					return;
				}
				IImport method = importmethod.getImport();
				method.setURI(URI);
				// get reader
				String readername = readerelement.getAttributeValue("name");
				String format = readerelement.getAttributeValue("format");
				String readertype = readerelement.getAttributeValue("datatype");
				IReaders allreaders = WorkFlowManager.getInstance().getReaders();
				IReader reader = allreaders.findReaderWithName(readername);
				if(reader == null) {
					Util.errorMessage("Could not find reader " + readername);
					return;
				}
				reader.setType(readertype);
				reader.setDataFormat(format);
				reader.setType(readertype);
				
				dataset.importDataContent(method, reader, dataname, dataid);
			}
				
	}
	
	/**
	 * Reads the XML data and create the data
	 *
	 */
	public void readDOMDataElement(Element element, IDataSets datasets) {
		// start with source data
		for(Object child : CodeFragment.getDataElementsOfType(CodeFragment.TYPE.SOURCE, element)) {
			Element code = (Element) child;
			try {
				collectSourceDataFromDOM(code, datasets);
			} catch (Exception e) {
				e.printStackTrace();
				Util.errorMessage("Failed to load source " + code.getAttributeValue("name") + ", error " + e.getMessage());
			}
		}
		
		// process the data
		for(Object child : CodeFragment.getDataElementsOfType(CodeFragment.TYPE.GENERATED, element)) {
			Element code = (Element) child;
			try {
				processDataFromDOM(code, datasets);
			} catch (Exception e) {
				e.printStackTrace();
				Util.errorMessage("Failed to run process " + e.getMessage());
			}
		}
		
	}
	
	private void processDataFromDOM(final Element code, final IDataSets datasets) {
		Job job = new Job("Performing Processing") {
		    @Override
		    protected IStatus run(final IProgressMonitor monitor) {
	    		HashMap<String, Object> parametersvalues = new HashMap<String, Object>();
				// list parameters
	    		Element processelement = code.getChild(IProcess.class.getName());
				// find processing extension point element
				String processingclass = processelement.getAttributeValue("processingclass");
				List<Element> parameters = processelement.getChildren(IParameter.class.getName());
				try {
					for(Element parameter : parameters) {
						String paramname = parameter.getAttributeValue("name");
						List<Element> parametervalues = parameter.getChildren();
						if(parametervalues.size() != 1) {
							throw new Exception(parametervalues.size() + " parameter values for " + paramname + ", should always be one");
						}
						Element parameterelement = parametervalues.get(0);
						String parameterid = parameterelement.getAttributeValue("dataid");
						IDataContent parameterdata = null;
						// search until data is available
						while(parameterdata == null) {
							parameterdata = datasets.findDataFromId(parameterid);
							if(monitor.isCanceled()) {
								return Status.CANCEL_STATUS;
							}
							Thread.sleep(500);
						};
						parametersvalues.put(paramname, (IDataContent) parameterdata);
					}
					IProcesses allprocesses = WorkFlowManager.getInstance().getProcesses();
					IProcess process = allprocesses.findProcess(processingclass);
					// needs to be a blocking call, eg until the process is finished
					final IDataContent result = process.runProcess(parametersvalues, null);
					final String dataname = code.getAttributeValue("name");
					final String dataid = code.getAttributeValue("dataid");
					Display.getDefault().asyncExec(new Runnable() {
	
						@Override
						public void run() {
							datasets.addDataContent(result, dataname, dataid);
						}
						
					});
			        monitor.done();
			        return Status.OK_STATUS;
				} catch (InterruptedException e) {
					e.printStackTrace();
			        monitor.done();
			        return Status.CANCEL_STATUS;
				} catch (Exception e) {
					e.printStackTrace();
			        monitor.done();
			        return Status.CANCEL_STATUS;
				}
		    }
		    @Override
		    protected void canceling() {
		    	super.canceling();
		    }
		};
		job.schedule();
	}

	public void readDOMElement(Element element) {
		String label = element.getAttribute("name").getValue();
		m_name = label;
		Element datasetselements = (Element) element.getChild(IDataSets.class.getName());
		IDataSets datasets = (IDataSets) Util.searchForInterface(IDataSets.class, getChildren());
		if(datasets != null) {
			readDOMDataElement(datasetselements, datasets);
		}
			
		Element viewselements = (Element) element.getChild(IViewerItem.class.getName());
		if(viewselements != null) {
			readDOMViewsElement(viewselements);
		}
			
		Element exportselements = (Element) element.getChild(IExportItem.class.getName());
		if(exportselements != null) {
			readDOMViewsElement(viewselements);
		}
			
	}

	private void readDOMViewsElement(Element viewselements) {
//		List<Element> views = viewselements.getChildren(IViewerItem.class.getName());
//		for(Element viewelement : views) {
//			String viewid = viewelement.getAttributeValue("viewid");
//			IViewerItem viewer = WorkFlowManager.getInstance().getViewers().findViewer(viewid);
//			viewer.Open(this);
//		}
	}

	static public WorkFlow openFile(String filename) throws FileNotFoundException {
		FileInputStream fileinputstream = new FileInputStream(filename);
    	WorkFlow workflow = new WorkFlow("Initialised");
    	workflow.setFileName(filename);
    	WorkFlowManager.getInstance().addWorkFlow(workflow);
    	WorkFlowManager.getInstance().refreshTree();
		workflow.readFromStream(fileinputstream);
		return workflow;
	}

	private void setFileName(String filename) {
		m_filename = filename;
	}
	
	public String getFileName() {
		return m_filename;
	}

	public void save(boolean forcenew) {
		String filename = "";
		if((m_filename.length() == 0) || forcenew) {
			FileDialog dialog = new FileDialog(new Shell(), SWT.SAVE);
			if(dialog.open() == null) {
				return;
			}
			filename = dialog.getFilterPath() + "/" + dialog.getFileName();
		} else {
			filename = m_filename;
		}
		FileOutputStream fileoutputstream;
		try {
			fileoutputstream = new FileOutputStream(filename);
			writeToStream(fileoutputstream);
			m_filename = filename;
			Element element = new Element("com.metaaps.eoclipse.workflow.filename");
			element.setAttribute("filename", m_filename);
			Util.setConfiguration(element, "filename");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Util.errorMessage("Could not access file");
		} catch (IOException e) {
			e.printStackTrace();
			Util.errorMessage("Could not save file");
		}
	}
	
}
