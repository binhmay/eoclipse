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
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.metaaps.eoclipse.common.Attributes;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.datasets.IVectorData;
import com.metaaps.eoclipse.common.datasets.VectorData;
import com.metaaps.eoclipse.common.datasets.ProducedVectorDataContent;
import com.metaaps.eoclipse.processing.AbstractProcessing;
import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 * @author leforthomas
 * 
 * Returns the input VectorData object ("Source") clipped by the input VectorData object ("Clip") as a VectorData object
 * 
 */
public class VectorClipping extends AbstractProcessing {

	public VectorClipping() {
	}
	
	@Override
	public IDataContent execute(HashMap<String, Object> parametervalues) {
		
		// get parameters
		IVectorData source = (IVectorData) parametervalues.get("Source");
		IVectorData clip = (IVectorData) parametervalues.get("Clip");
		
		// generate buffering
		final VectorData clippedData = clip(source.getVectorData(""), clip.getVectorData(""), m_monitor);
		
		// return result
		return new ProducedVectorDataContent() {
			
			@Override
			public String getName() {
				return "Clipped Vector Data";
			}
			
			@Override
			public VectorData getVectorData(String cqlFilter) {
				return clippedData;
			}
		};
		
	}

	static public VectorData clip(VectorData source, VectorData clip, IProgressMonitor monitor) {
		
		VectorData clipped = new VectorData();
		
		List<Geometry> geometries = source.getGeometries();
		List<Attributes> attributes = source.getAttributes();
		List<Geometry> clipgeometries = clip.getGeometries();
		
		if(monitor != null) {
	    	monitor.setTaskName("Clipping Geometries...");
	    	monitor.worked(20);
		}
    	
		for(int vcount = 0; vcount < geometries.size(); vcount++) {
			 if(monitor != null) {
		    	if(monitor.isCanceled()) {return null;}
			 }
			 Geometry geom  = geometries.get(vcount);
			 Attributes attribute  = attributes.get(vcount);
			 for(Geometry clipgeom : clipgeometries) {
				 Geometry geometry = geom.intersection(clipgeom);
			     for (int i = 0; i < geometry.getNumGeometries(); i++) {
			         if (!geometry.getGeometryN(i).isEmpty()) {
			         	clipped.put(geometry.getGeometryN(i), attribute);
			         }
			     }
			 }
		}
				 
		return clipped;
		
	}

}
