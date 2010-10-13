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
package com.metaaps.eoclipse.common.views;

import java.awt.Color;
import java.util.HashMap;

import com.metaaps.eoclipse.common.Property;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geomgraph.Position;

public interface ILayer { //extends Layer { <- TBD
	
	boolean represents(IDataContent datacontent);
	
	String getName();
	void setName(String name);
	
    boolean isActive();
    void setActive(boolean active);

	Object getLayerProperty(String key);
	Property[] getLayerProperties();
	void setLayerProperty(String key, Object obj);
	
	Coordinate getCenter();

	// to implement if the layer should react on selection changes
	void selectionChanged();
	// to implement if the layer should react on selection double click
	void selectionDoubleClick();

	void refresh();

}
