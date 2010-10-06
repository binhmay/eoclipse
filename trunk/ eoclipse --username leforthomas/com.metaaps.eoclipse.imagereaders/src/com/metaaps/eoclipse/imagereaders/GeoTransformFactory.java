/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metaaps.eoclipse.imagereaders;

import java.awt.geom.AffineTransform;
import java.util.List;

import com.metaaps.eoclipse.common.datasets.GCP;
import com.metaaps.eoclipse.common.datasets.IGeoTransform;

/**
 * Simple class that contains factory methods that return the appropriate
 * implementation of the GeoTransform interface
 * @author thoorfr
 */
public class GeoTransformFactory {

    public static IGeoTransform getFromAffineTransform(AffineTransform atpix2geo, String wktGeoProj) {
        return new AffineGeoTransform(atpix2geo, wktGeoProj);
    }
    
    public static IGeoTransform createFromGcps(List<GCP> gcps, String wktGeoProj) {
        return new GcpsGeoTransform(gcps, wktGeoProj);
    }
}


