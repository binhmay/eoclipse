/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.metaaps.eoclipse.imagereaders.utils;

import com.metaaps.eoclipse.common.datasets.IGeoRaster;
import com.metaaps.eoclipse.common.datasets.IGeoTransform;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Usefull tools to extract geometry of the image boundaries
 * @author thoorfr
 */
public class GeometryExtractor {
    
    public static Geometry getFrame(IGeoRaster gir){
        try {
            IGeoTransform gt = gir.getGeoTransform();
            double[] x0;
            double[] x1;
            double[] x2;
            double[] x3;
            x0 = gt.getGeoFromPixel(-50, -50, "EPSG:4326");
            x2 = gt.getGeoFromPixel(50 + gir.getWidth(), 50 + gir.getHeight(), "EPSG:4326");
            x3 = gt.getGeoFromPixel(50 + gir.getWidth(), -50, "EPSG:4326");
            x1 = gt.getGeoFromPixel(-50, 50 + gir.getHeight(), "EPSG:4326");
            return new WKTReader().read("POLYGON((" + x0[0] + " " + x0[1] + "," + x1[0] + " " + x1[1] + "," + x2[0] + " " + x2[1] + "," + x3[0] + " " + x3[1] + "," + x0[0] + " " + x0[1] + "" + "))");
        } catch (ParseException ex) {
            Logger.getLogger(GeometryExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
