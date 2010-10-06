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
package com.metaaps.eoclipse.imageviewer.utils;

import java.util.List;

import com.metaaps.eoclipse.common.Attributes;
import com.metaaps.eoclipse.common.datasets.IGeoRaster;
import com.metaaps.eoclipse.common.datasets.IGeoTransform;
import com.metaaps.eoclipse.common.datasets.IVectorData;
import com.metaaps.eoclipse.common.datasets.VectorData;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.metaaps.eoclipse.genericprocessing.ImageFrame;
import com.metaaps.eoclipse.genericprocessing.VectorClipping;
import com.metaaps.eoclipse.imageviewer.api.GeometricLayer;

public class GeoUtils {

  /**
  * Modify the GeometricLayer so the layer coordinate system matches the image coordinate system ("pixel" projection).
 * @throws ParseException 
  */
 public static GeometricLayer createImageProjectedLayer(IVectorData vectorreader, IGeoRaster gir, String projection) throws ParseException {
	 
	 // create a layer
	 GeometricLayer geomlayer = new GeometricLayer(GeometricLayer.POLYGON);
	 int margin = 0;
	 double[] x0;
	 double[] x1;
	 double[] x2;
	 double[] x3;
	 IGeoTransform gt = gir.getGeoTransform();
	 x0 = gt.getGeoFromPixel(-margin, -margin, "EPSG:4326");
	 x2 = gt.getGeoFromPixel(margin + gir.getWidth(), margin + gir.getHeight(), "EPSG:4326");
	 x3 = gt.getGeoFromPixel(margin + gir.getWidth(), -margin, "EPSG:4326");
	 x1 = gt.getGeoFromPixel(-margin, margin + gir.getHeight(), "EPSG:4326");
	 double minx = Math.min(x0[0], Math.min(x1[0], Math.min(x2[0], x3[0])));
	 double maxx = Math.max(x0[0], Math.max(x1[0], Math.max(x2[0], x3[0])));
	 double miny = Math.min(x0[1], Math.min(x1[1], Math.min(x2[1], x3[1])));
	 double maxy = Math.max(x0[1], Math.max(x1[1], Math.max(x2[1], x3[1])));
	 
	 String cqlfilter = "BBOX(the_geom," + minx + "," + miny + "," + maxx + "," + maxy + ")";
	
     VectorData vectordata = vectorreader.getVectorData(cqlfilter);
     
	 Polygon imageP = (Polygon) new WKTReader().read("POLYGON((" +
	         x0[0] + " " + x0[1] + "," +
	         x1[0] + " " + x1[1] + "," +
	         x2[0] + " " + x2[1] + "," +
	         x3[0] + " " + x3[1] + "," +
	         x0[0] + " " + x0[1] + "" +
	         "))");
     VectorData clip = new VectorData();
     clip.put(imageP);
     VectorData clipped = VectorClipping.clip(vectordata, clip, null);
     List<Geometry> geometries = clipped.getGeometries();
     List<Attributes> attributes = clipped.getAttributes();
     
	 for(int vcount = 0; vcount < geometries.size(); vcount++) {
		 Geometry geom  = geometries.get(vcount);
		 Attributes attribute  = attributes.get(vcount);
     	 geomlayer.put(geom, attribute);
	 }
	
	 geomlayer.setProjection(null);
     for(Geometry geom : geomlayer.getGeometries()){
         for(Coordinate pos : geom.getCoordinates()){
             double[] temp = gt.getPixelFromGeo(pos.x, pos.y, projection);
             pos.x = temp[0];
             pos.y = temp[1];
         }
     }
     
     return geomlayer;
 }

 /**
  * Modify the GeometricLayer so the layer coordinates system matches the world coordinate system (EPSG projection).
  */
 public static GeometricLayer createWorldProjectedLayer(GeometricLayer positions, IGeoTransform geoTransform, String projection) {
     positions=positions.clone();
     positions.setProjection(projection);
     for(Geometry geom:positions.getGeometries()){
         for(Coordinate pos:geom.getCoordinates()){
             double[] temp=geoTransform.getGeoFromPixel(pos.x, pos.y, projection);
             pos.x=temp[0];
             pos.y=temp[1];
         }
     }
     return positions;
 }


}
