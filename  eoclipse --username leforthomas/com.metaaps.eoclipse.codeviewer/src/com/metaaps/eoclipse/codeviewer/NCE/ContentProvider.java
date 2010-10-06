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
package com.metaaps.eoclipse.codeviewer.NCE;

import java.util.List;

import org.jdom.Element;

import com.metaaps.eoclipse.common.CodeFragment;
import com.metaaps.eoclipse.common.CodeFragment.TYPE;
import com.metaaps.eoclipse.common.Folder;
import com.metaaps.eoclipse.common.ModelChangeListener;
import com.metaaps.eoclipse.common.Property;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.datasets.IImport;
import com.metaaps.eoclipse.common.datasets.IReader;
import com.metaaps.eoclipse.common.processing.IParameter;
import com.metaaps.eoclipse.common.processing.IProcess;

/**
 * @author leforthomas
 */
public class ContentProvider extends ModelChangeListener {

	@Override
	public void dispose() {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof Folder)
		{
			return ((Folder)parentElement).getChildren();
		} else if(parentElement instanceof IDataContent)
		{
			IDataContent data = (IDataContent)parentElement;
			Folder propertiesfolder = new Folder();
			CodeFragment code = data.getCode();
			if((code == null) || (code.getChildren().size() == 0)) {
				return EMPTY_ARRAY;
			}
			CodeFragment.TYPE type = CodeFragment.TYPE.valueOf(code.getAttributeValue("type"));
			propertiesfolder.addChild(new Property("Type", type.toString()));
			if(type == TYPE.SOURCE) {
				Element importelement = code.getChild(IImport.class.getName());
				propertiesfolder.addChild(new Property("URI", importelement.getAttributeValue("URI")));
				Element readerelement = code.getChild(IReader.class.getName());
				if(readerelement != null) {
					propertiesfolder.addChild(new Property("Reader", readerelement.getAttributeValue("name")));
					propertiesfolder.addChild(new Property("Format", readerelement.getAttributeValue("format")));
				}
			}
			if(type == TYPE.GENERATED) {
				Element processelement = code.getChild(IProcess.class.getName());
				propertiesfolder.addChild(new Property("Process", processelement.getAttributeValue("processingclass")));
				List<Element> parameters = processelement.getChildren(IParameter.class.getName());
				for(Element parameter : parameters) {
					propertiesfolder.addChild(new Property("Parameter " + parameter.getAttributeValue("name"), "TBI"));
				}
			}
			return new Object[]{propertiesfolder};
		} else if(parentElement instanceof String)
		{
			return new Object[]{parentElement};
		}
		
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

}

