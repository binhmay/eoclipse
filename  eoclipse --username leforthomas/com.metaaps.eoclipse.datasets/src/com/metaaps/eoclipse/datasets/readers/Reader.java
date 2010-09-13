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
package com.metaaps.eoclipse.datasets.readers;

import java.io.File;
import java.util.ArrayList;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.resource.ImageDescriptor;

import com.metaaps.eoclipse.common.IDataContent;
import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.datasets.IReader;

/**
 * 
 * @author leforthomas
 *
 * Encapsulates the reader extension plug-in, read the extension's parameters' values
 * Provides methods for triggering the wizard and for creating the reader object
 * Extends Model FOR CONVENIENCE ONLY, it is not necessary in this case, the tree item is not even displayed
 * 
 */
public class Reader extends Model implements IReader {

	private IExtension m_extension;
	private String m_name;
	private String m_readertype;
	private IConfigurationElement m_configuration;
	private String[] m_formats = null;
	private String m_format;
	private String m_type;

	public Reader(IExtension extension, IConfigurationElement element) {
		m_extension = extension;
		m_name = element.getAttribute("name");
		m_readertype = element.getAttribute("Type");
		m_configuration = element;
		ArrayList<String> formats = new ArrayList<String>();
		IConfigurationElement[] formatelements = element.getChildren("Format");
		for(IConfigurationElement format : formatelements) {
			formats.add(format.getAttribute("Type"));
		}
		m_formats = (String[]) formats.toArray(new String[0]);
	}

	public IExtension getExtension() {
		return m_extension;
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public String getType() {
		return m_readertype;
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public String[] getFormatsSupported() {
		return m_formats;
	}

	public IDataContent openFile(File file) {
		try {
			IDataContent reader = (IDataContent) m_configuration.createExecutableExtension("Class");
			reader.setFile(file);
			reader.initialise();
			reader.setDataFormat(m_format);
			reader.setType(m_type);
			return reader;
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setType(String type) {
		m_type = type;
	}

	@Override
	public void setDataFormat(String formatname) {
		m_format = formatname;
	}

	@Override
	public String getReader() {
		// TODO Auto-generated method stub
		return m_extension.getUniqueIdentifier();
	}

	@Override
	public String getDataFormat() {
		// TODO Auto-generated method stub
		return m_format;
	}

}
