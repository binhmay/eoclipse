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
package com.metaaps.eoclipse.genericprocessing;

import java.util.HashMap;

import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.datasets.IGeoRaster;
import com.metaaps.eoclipse.common.datasets.VectorData;
import com.metaaps.eoclipse.common.datasets.ProducedVectorDataContent;
import com.metaaps.eoclipse.processing.AbstractProcessing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * 
 * @author leforthomas
 * 
 * Returns the frame, or boundaries, of the input ImageData object ("Source") as a VectorData object
 * 
 */
public class ImageFrame extends AbstractProcessing {

	@Override
	public IDataContent execute(HashMap<String, Object> parametervalues) {
		
		// get parameters
		final IGeoRaster source = (IGeoRaster) parametervalues.get("Source");
		
		// generate buffering
		try {
			
			final VectorData imageframe = frame(source);
			
			// return result
			return new ProducedVectorDataContent() {
				
				@Override
				public String getName() {
					return "Image Frame on " + source.getName();
				}
				
				@Override
				public VectorData getVectorData(String cqlFilter) {
					return imageframe;
				}
				
				@Override
				public String getDataFormat() {
					return DATA_FORMATS.VECTOR_POLYGON.toString();
				}
			};
		} catch(ParseException exception) {
			return null;
		}
		
	}

	static public VectorData frame(IGeoRaster source) throws ParseException {
		VectorData frame = new VectorData();
		String polystr = "POLYGON((";
        double[] firstlatlon = null;
		for(double[] latlon : source.getFrameLatLon()) {
	        polystr += latlon[0] + " " + latlon[1] + ",";
			if(firstlatlon == null) {
	        	firstlatlon = latlon;
	        }
		}
		polystr += firstlatlon[0] + " " + firstlatlon[1];
		polystr += "))";
		Polygon imageframe = (Polygon) new WKTReader().read(polystr);
		frame.put(imageframe);
		
		return frame;
	}

}
