/*******************************************************************************
 * Copyright (c) 2010 METAAPS SRL(U).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     METAAPS SRL(U) - initial API and implementation
 ******************************************************************************/
package com.metaaps.eoclipse.globeviewer.layers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;

import com.metaaps.eoclipse.common.Property;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.views.ILayer;
import com.metaaps.eoclipse.globeviewer.util.PlaceMark;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author leforthomas
 *
 * Provides a eoclipse layer implementation by extending the WWJ one
 * Contains the WWJ layer as well as its datacontent
 * 
 */
public class GlobeViewerLayer extends RenderableLayer implements ILayer {
	
	private IDataContent m_datacontent;

	public GlobeViewerLayer(IDataContent datacontent) {
		super();
		m_datacontent = datacontent;
	}
	
	public boolean represents(IDataContent datacontent) {
		return m_datacontent == datacontent;
	}
	
	public Color getLayerColor() {
		PlaceMark item = (PlaceMark) getRenderables().iterator().next();
		return item.getColor();
	}

	public void setLayerColor(Color color) {
		PlaceMark item = (PlaceMark) getRenderables().iterator().next();
		item.changeColor(color);
	}

	public Coordinate getCenter() {
		PlaceMark item = (PlaceMark) getRenderables().iterator().next();
		Position center = item.getCenter();
		return new Coordinate(center.getLatitude().getRadians(), center.getLongitude().getRadians());
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setActive(boolean active) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getLayerProperty(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property[] getLayerProperties() {
		ArrayList<Property> properties = new ArrayList<Property>();
		properties.add(new Property("color", getLayerColor()));
		return properties.toArray(new Property[properties.size()]);
	}

	@Override
	public void setLayerProperty(String key, Object obj) {
		// TODO Auto-generated method stub
		
	}

}
