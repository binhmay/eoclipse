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
package com.metaaps.eoclipse.datasets;

import java.io.File;
import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.jdom.Element;

import com.metaaps.eoclipse.common.CodeFragment;
import com.metaaps.eoclipse.common.IData;
import com.metaaps.eoclipse.common.IDataContent;
import com.metaaps.eoclipse.common.IDataSets;
import com.metaaps.eoclipse.common.IModel;
import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IImport;
import com.metaaps.eoclipse.common.datasets.IImportMethod;
import com.metaaps.eoclipse.common.datasets.IReader;
import com.metaaps.eoclipse.datasets.importmethods.ImportFolder;
import com.metaaps.eoclipse.datasets.readers.ReadersFolder;

/**
 * @author leforthomas
 * 
 * The DataSets is the collection of data items contained in a workflow
 * 
 */
public class DataSets extends Model implements IDataSets {

	private static ImageDescriptor m_imagedescriptor = Activator
			.imageDescriptorFromPlugin("com.metaaps.eoclipse.common",
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

	@Override
	public void addDataContent(IDataContent data, String dataname) {
		Data dataitem = new Data(data);
		dataitem.setLabel(dataname);
		addChild(dataitem);
		fireChanged(data, Model.ADDED);
	}

	@Override
	public void removeDataContent(IData data) {
		removeChild(data);
		fireChanged(data.getDataContent(), Model.REMOVED);
	}

	/**
	 * 
	 * Ensures the name of the data item in the dataset is unique
	 *
	 * @return the unique name
	 */
	public String getUniqueName(Data data, String basename) {
		int count = 0;
		while (!isUniqueName(data, basename, count)) {
			count++;
			if (count > 100) {
				break;
			}
		}

		return (count == 0 ? basename : basename + count);
	}

	public boolean isUniqueName(Data data, String basename, int count) {
		// scan tree to make sure no other data has the same label
		String name = basename + (count > 0 ? count : "");
		for (Object obj : getChildren()) {
			if (obj instanceof Data) {
				if(obj != data)
				{
					if (name.contentEquals(((Data) obj).getLabel())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Saves the data as an xml structure
	 * NEEDS REWORK
	 *
	 * @return xml data
	 */
	@Override
	public void fillDOMElement(Element element) {
		Element dataset = new Element("dataset");
		dataset.setAttribute("class", IDataSets.class.getCanonicalName());
		element.addContent(dataset);
		for(Object obj : getChildren()) {
			if(obj instanceof IModel) {
				((IModel)obj).fillDOMElement(dataset);
			}
		}
	}
	
	/**
	 * Reads the XML data and create the data
	 * NEEDS REWORK
	 *
	 */
	@Override
	public void readDOMElement(Element element) {
		for(Object child : element.getChildren("data")) {
			// get code fragment
			Element code = (Element) ((Element) child).getChild("code");
			CodeFragment.TYPE type = CodeFragment.TYPE.valueOf(code.getAttributeValue("type"));
			if(type == CodeFragment.TYPE.SOURCE) {
				Element properties = (Element) code.getChild("properties");
				// get URI
				String URI = properties.getAttributeValue("URI");
				// look for an import method
				IImport method = null;
				for(Object obj : ImportFolder.getInstance().getChildren()) {
					IImportMethod importmethod = (IImportMethod) obj;
					if((URI.length() > 0) && URI.startsWith(importmethod.getURIExtension())) {
						method = importmethod.getImport();
						method.setURI(URI);
						break;
					}
				}
				// get reader
				String readername = properties.getAttributeValue("reader");
				String dataname = properties.getAttributeValue("data");
				String format = properties.getAttributeValue("format");
				String readertype = properties.getAttributeValue("type");
				IReader reader = null;
				for(Object obj : ReadersFolder.getInstance().getChildren()) {
					IReader childreader = (IReader) obj;
					if(childreader.getName().contentEquals(readername)) {
						reader = childreader;
						break;
					}
				}
				if(reader == null) {
					Util.errorMessage("Could not find reader " + readername);
					return;
				}
				reader.setType(readertype);
				reader.setDataFormat(format);
				
				importDataContent(method, reader, dataname);
			}
			
		}
	}
	
	/**
	 * Import the data into the datasets using the provided import method and reader
	 * Gives the dataname to the data item in the tree
	 *
	 */
	@Override
	public void importDataContent(IImport importmethod, IReader reader, String dataname) {
		// get file first
		File file = importmethod.importFile();
		
		// now open it with the reader
		IDataContent datacontent = reader.openFile(file);
		
		// create code fragment to keep track of how data was created
		HashMap<String, Object> properties = new HashMap<String, Object>();
		// collect properties from import and reader
		properties.put("URI", importmethod.getURI());
		properties.put("reader", reader.getName());
		properties.put("type", reader.getType());
		properties.put("name", dataname);
		properties.put("format", reader.getDataFormat());
		datacontent.setCode(new CodeFragment(CodeFragment.TYPE.SOURCE, 0, properties));
		addDataContent(datacontent, dataname);

	}
	
}
