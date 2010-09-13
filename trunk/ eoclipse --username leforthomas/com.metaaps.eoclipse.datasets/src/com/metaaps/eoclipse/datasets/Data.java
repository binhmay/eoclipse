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

import org.eclipse.jface.resource.ImageDescriptor;
import org.jdom.Element;

import com.metaaps.eoclipse.common.CodeFragment;
import com.metaaps.eoclipse.common.IData;
import com.metaaps.eoclipse.common.IDataContent;
import com.metaaps.eoclipse.common.IModel;
import com.metaaps.eoclipse.common.Model;

/**
 * tree item representation of a data object
 * data is of types vector or raster and source or generated
 * a data contains a datacontent object which provides access to the data itself
 * the datacontent object is provided by the reader and based on the file imported by the import method of choice 
 * @author leforthomas
 */
public class Data extends Model implements IData {

	private static ImageDescriptor[] m_imagedescriptor = new ImageDescriptor[]{
		Activator.imageDescriptorFromPlugin("com.metaaps.eoclipse.common", "icons/picture.png"),
		Activator.imageDescriptorFromPlugin("com.metaaps.eoclipse.common", "icons/node-select-all.png"),
		Activator.imageDescriptorFromPlugin("com.metaaps.eoclipse.common", "icons/picture-generated.png"),
		Activator.imageDescriptorFromPlugin("com.metaaps.eoclipse.common", "icons/vector-generated.png")
	};
	
	private IDataContent m_data;
	
	private String m_label = "";
	
	private int m_dataid;

	public Data(IDataContent datacontent) {
		m_data = datacontent;
	}

	public void setLabel(String label) {
		m_label = label;
	}

	@Override
	public String getLabel() {
		if((m_label == null) || (m_label.length() == 0))
		{
			m_label = m_data.getName();
			if((m_label == null) || (m_label.length() == 0))
			{
				m_label = "Data ";
			}
			m_label = ((DataSets)m_parent).getUniqueName(this, m_label);
		}
		return m_label;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		try {
			return m_imagedescriptor[(m_data.getType().contentEquals("Image") ? 0 : 1) + ((m_data.getCode().getType() == CodeFragment.TYPE.SOURCE) ? 0 : 2)];
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Returns the data item id in the tree
	 * This should be used as a reference to the object for saving/exporting and open/rerun of workflows
	 * NEEDS IMPLEMENTING 
	 *
	 * @return the data item id in the tree
	 */
	@Override
	public String getId() {
		return null;
	}

	@Override
	public IDataContent getDataContent() {
		// TODO Auto-generated method stub
		return m_data;
	}
	
	/**
	 * Saves the data as an xml structure
	 * NEEDS REWORK
	 *
	 * @return xml data
	 */
	@Override
	public void fillDOMElement(Element element) {
		Element data = new Element("data");
		data.setAttribute("name", getLabel());
		data.setAttribute("dataid", (new Integer(m_dataid)).toString());
		data.setAttribute("class", IData.class.getCanonicalName());
		element.addContent(data);
		CodeFragment code = getDataContent().getCode();
		code.fillDOMElement(data);
		for(Object obj : getChildren()) {
			if(obj instanceof IModel) {
				((IModel)obj).fillDOMElement(data);
			}
		}
	}
	
	@Override
	public void readDOMElement(Element element) {
		
	}
	
}
