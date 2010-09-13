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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.*;

import com.metaaps.eoclipse.Activator;
import com.metaaps.eoclipse.common.IDataSets;
import com.metaaps.eoclipse.common.IModel;
import com.metaaps.eoclipse.common.IWorkFlow;
import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.Util;

/**
 * @author leforthomas
 * 
 * A workflow is the equivalent of a project in EOClipse. Workflows contain data imported or generated. Functionality is added to the workflow following the plugins availability and the type of data used.
 *  
 */
public class WorkFlow extends Model implements IWorkFlow {
	
	private static ImageDescriptor m_imagedescriptor = Activator.imageDescriptorFromPlugin("com.metaaps.eoclipse.common", "icons/reports.png");
	
	private String m_name = "";
	
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
		Element root = new Element("workflow");
		fillDOMElement(root);
		Document doc = new Document(root);
		XMLOutputter output = new XMLOutputter();
		output.output(doc, stream);
	}
	
	@Override
	public void fillDOMElement(Element element) {
		element.setAttribute("name", getLabel());
		for(Object obj : getChildren()) {
			if(obj instanceof IModel) {
				((IModel)obj).fillDOMElement(element);
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
	
	@Override
	public void readDOMElement(Element element) {
		String label = element.getAttribute("name").getValue();
		m_name = label;
		Element datasets = (Element) element.getChild("dataset");
		IDataSets folder = (IDataSets) Util.searchForInterface(IDataSets.class, getChildren());
		if(folder != null) {
			folder.readDOMElement(datasets);
		}
			
	}

}
