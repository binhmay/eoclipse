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
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;

import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.datasets.IVectorData;
import com.metaaps.eoclipse.common.datasets.VectorData;
import com.metaaps.eoclipse.common.datasets.ProducedVectorDataContent;
import com.metaaps.eoclipse.processing.AbstractProcessing;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.operation.buffer.BufferParameters;

/**
 * 
 * @author leforthomas
 * 
 * Returns the input VectorData object ("Source") buffered by the specified distance ("Size") in meters as a VectorData object
 * 
 */
public class VectorBuffering extends AbstractProcessing {
	
	@Override
	public IDataContent execute(HashMap<String, Object> parametervalues) {
		
		// get parameters
		IVectorData source = (IVectorData) parametervalues.get("Source");
		Double size = (Double) parametervalues.get("Size");
		
		// generate buffering
		final VectorData bufferedData = buffer(source.getVectorData(""), size.doubleValue(), m_monitor);
		
		// return result
		return (bufferedData == null ? null :
					new ProducedVectorDataContent() {
					
					@Override
					public String getName() {
						return "Buffered Vector Data";
					}
					
					@Override
					public VectorData getVectorData(String cqlFilter) {
						return bufferedData;
					}
				});
		
	}
	
    static public VectorData buffer(VectorData source, double bufferingDistance, IProgressMonitor monitor) {
    	
    	monitor.setTaskName("Cloning Data...");
    	monitor.worked(20);
    	VectorData data = source.clone();
    	
    	monitor.setTaskName("Buffering Geometries...");
    	monitor.worked(40);
    	if(monitor.isCanceled()) {return null;}
    	
        PrecisionModel pm=new PrecisionModel(1);
        GeometryFactory gf = new GeometryFactory(pm);
        for (Geometry geom : new Vector<Geometry>(data.getGeometries())) {
        	if(monitor.isCanceled()) {return null;}
            Geometry p = geom.buffer(bufferingDistance, 2, BufferParameters.CAP_SQUARE);
            if (p instanceof Polygon && ((Polygon) p).getNumInteriorRing() > 0) {
                p = gf.createPolygon((LinearRing) ((Polygon) p).getExteriorRing(), null);
            }
            data.replace(geom, p);

        }
        // then merge them
        Vector<Geometry> newgeoms = new Vector<Geometry>();
        Vector<Geometry> remove = new Vector<Geometry>();
        Vector<Geometry> parse = new Vector<Geometry>(data.getGeometries());

    	monitor.setTaskName("Aggregating Geometries...");
    	monitor.worked(60);
    	
        for (Geometry g : parse) {
        	if(monitor.isCanceled()) {return null;}
            boolean isnew = true;
            remove.clear();
            for (Geometry newg : newgeoms) {
                if (newg.contains(g)) {
                    isnew = false;
                    break;
                } else if (g.contains(newg)) {
                    remove.add(newg);
                } else if (newg.intersects(g)) {
                    g = g.union(newg).buffer(0);
                    remove.add(newg);
                }
            }
            if (isnew) {
                newgeoms.add(g);
            }
            newgeoms.removeAll(remove);
        }


    	monitor.setTaskName("Copying data...");
    	monitor.worked(80);
    	if(monitor.isCanceled()) {return null;}
    	
        data.clear();
        // assign new value
        for (Geometry geom : newgeoms) {
            data.put(geom);
        }
        
        return data;
    }

}
