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
package com.metaaps.eoclipse.globeviewer.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwind.render.Renderable;
import java.awt.Color;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author thoorfr
 * 
 * modified for eoclipse by leforthomas
 * 
 */
public class PlaceMark implements Renderable{

    private GlobeAnnotation annotation;
    private Polyline bounds;
    private boolean annotationVisible = false;

    public PlaceMark(Polyline bounds, GlobeAnnotation annotation) {
        this.bounds = bounds;
        this.annotation = annotation;
        this.annotation.getAttributes().setBackgroundColor(Color.BLACK);
        this.annotation.setDelegateOwner(this);
        this.annotationVisible = false;
    }
    
    public static PlaceMark create(String text, Geometry geometry, Color color) {
        if (geometry instanceof Point) {
            List<Position> lp = new Vector<Position>();
            double x=((Point)geometry).getX();
            double y=((Point)geometry).getY();
            for (int i=0;i<2;i++) {
                Position ll = new Position(Angle.fromDegreesLatitude(y+ (i%2)*0.005), Angle.fromDegreesLongitude(x+ ((i+1)%2)*0.005), 0);
                lp.add(ll);
                ll = new Position(Angle.fromDegreesLatitude(y+ (i%2)*0.005), Angle.fromDegreesLongitude(x+ (i%2)*0.005), 0);
                lp.add(ll);
            }
            Polyline pol = new Polyline(lp);
            pol.setClosed(true);
            pol.setColor(color);
            Point c = (Point) geometry;
            Position centre = new Position(Angle.fromDegreesLatitude(c.getY()), Angle.fromDegreesLongitude(c.getX()), 0);
            return new PlaceMark(pol,new GlobeAnnotation(text, centre));
        } else {
            List<Position> lp = new Vector<Position>();
            for (Coordinate c : geometry.getCoordinates()) {
                Position ll = new Position(Angle.fromDegreesLatitude(c.y), Angle.fromDegreesLongitude(c.x), 0);
                lp.add(ll);
            }
            Polyline pol = new Polyline(lp);
            pol.setColor(color);
            if (lp.size() > 2 && geometry instanceof Polygon) {
                //pol.setClosed(true);
            }
            Point c = geometry.getCentroid();
            Position centre = new Position(Angle.fromDegreesLatitude(c.getY()), Angle.fromDegreesLongitude(c.getX()), 0);
            return new PlaceMark(pol, new GlobeAnnotation(text, centre));
        }
    }

    public boolean getAnnotationVisible() {
        return annotationVisible;
    }

    public void setAnnotationVisible(boolean value) {
        this.annotationVisible = value;
    }

    public void render(DrawContext dc) {
        bounds.render(dc);
        if (this.annotationVisible) {
            annotation.render(dc);
        }
    }
    
    public void changeColor(Color color) {
    	this.bounds.setColor(color);
    }
    
    public Color getColor() {
    	return this.bounds.getColor();
    }
    
    public Position getCenter() {
    	return this.bounds.getReferencePosition();
    }

}
