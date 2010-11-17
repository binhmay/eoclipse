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
package com.metaaps.eoclipse.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.filter.ElementFilter;

import com.metaaps.eoclipse.common.datasets.IDataContent;

/**
 * @author leforthomas
 * 
 * CodeFragment is the full code block that will enable a data element to be regenerated
 * In other words a code fragment represents an IDataContent and the instructions to use to generate it
 * 
 */
public final class CodeFragment extends Element {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static enum TYPE {SOURCE, GENERATED};
	
	public CodeFragment(String name, TYPE type) {
		super(name);
		setAttribute("type", type.toString());
	}
	
	public static List<Element> getDataElementsOfType(TYPE type, Element currentNode) {
		List<Element> result = new ArrayList<Element>();
		Iterator dataelements = currentNode.getDescendants(new ElementFilter(IDataContent.class.getName()));
		while(dataelements.hasNext()) {
			Element element = (Element) dataelements.next();
			if(element.getAttributeValue("type").contentEquals(type.toString())) {
				String dataid = element.getAttributeValue("dataid");
				boolean exists = false;
				for(Element dataelement : result) {
					if(dataelement.getAttributeValue("dataid").contentEquals(dataid)) {
						exists = true;
					}
				}
				if(exists == false) {
					result.add(element);
				}
			}
		}
		
		return result;
	}
}
