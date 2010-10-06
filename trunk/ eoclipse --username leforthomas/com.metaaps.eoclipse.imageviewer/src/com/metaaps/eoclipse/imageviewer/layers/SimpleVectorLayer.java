/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metaaps.eoclipse.imageviewer.layers;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.opengl.GL;

import com.metaaps.eoclipse.common.Attributes;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.imageviewer.api.GeoContext;
import com.metaaps.eoclipse.imageviewer.api.GeometricLayer;
import com.metaaps.eoclipse.imageviewer.api.IClickable;
import com.metaaps.eoclipse.imageviewer.api.IImageLayer;
import com.metaaps.eoclipse.imageviewer.api.ILayer;
import com.metaaps.eoclipse.imageviewer.api.ILayerManager;
import com.metaaps.eoclipse.imageviewer.api.IThreshable;
import com.metaaps.eoclipse.imageviewer.api.IVectorLayer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.buffer.BufferParameters;

/**
 *
 * @author thoorfr
 */
public class SimpleVectorLayer implements ILayer, IVectorLayer, IClickable, IThreshable {

    public final static String POINT = GeometricLayer.POINT;
    public final static String POLYGON = GeometricLayer.POLYGON;
    public final static String LINESTRING = GeometricLayer.LINESTRING;
    public final static String MIXED = GeometricLayer.MIXED;
    protected boolean active = true;
    protected IImageLayer parent;
    protected GeometricLayer glayer;
    protected String type;
    protected String name;
    protected float renderWidth = 1;
    protected Color color = new Color(1f, 1f, 1f);
    protected Geometry selectedGeometry;
    private symbol displaysymbol = symbol.point;
    private boolean threshable = false;
    private double minThresh = 0;
    private double maxThresh = 0;
    private double currentThresh = 0;
	private IDataContent m_datacontent;

    public SimpleVectorLayer(String layername, IImageLayer parent, IDataContent datacontent, GeometricLayer layer) {
        this.name = layername;
        this.parent = parent;
        if (layer == null) {
            return;
        }
        m_datacontent = datacontent;
        this.glayer = layer;
        this.type = layer.getGeometryType();
        String test = glayer.getSchema('/');
        if (test.contains("significance")) {
            calculateMaxMinTresh();
            threshable = true;
        }
    }

    public double getMaximumThresh() {
        return maxThresh;
    }

    public double getMinimumThresh() {
        return minThresh;
    }

    public void setThresh(double thresh) {
        currentThresh = thresh;
    }

    public boolean isThreshable() {
        return threshable;
    }

    private void calculateMaxMinTresh() {
        minThresh = Double.MAX_VALUE;
        maxThresh = Double.MIN_VALUE;
        for (Attributes att : glayer.getAttributes()) {
            double temp = new Double("" + att.get("significance"));
            if (temp < minThresh) {
                minThresh = temp;
            }
            if (temp > maxThresh) {
                maxThresh = temp;
            }
        }
        currentThresh = minThresh - 0.01;
    }

    public int[] getHistogram(int numClasses) {
        if (threshable) {
            int[] out = new int[numClasses];
            for (Attributes att : glayer.getAttributes()) {
                double temp = new Double("" + att.get("significance"));
                int classe = (int) ((numClasses - 1) * (temp - minThresh) / (maxThresh - minThresh));
                out[classe]++;
            }
            return out;
        }
        return null;
    }

    public double getThresh() {
        return currentThresh;
    }

    protected GeometricLayer createThresholdedLayer(GeometricLayer layer) {
        GeometricLayer out = layer.clone();
        if (!threshable) {
            return out;
        }
        Vector<Geometry> remove = new Vector<Geometry>();
        for (Geometry geom : Collections.unmodifiableList(out.getGeometries())) {
            if (new Double("" + out.getAttributes(geom).get("significance")) < currentThresh) {
                remove.add(geom);
            }
        }
        for (Geometry geom : remove) {
            out.remove(geom);
        }
        return out;

    }
    
    @Override
    public String toString() {
    	return name;
    }

    public static enum symbol {

        point, circle, square, triangle, cross
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void render(GeoContext context) {
        if (!context.isDirty()) {
            return;
        }
        int x = context.getX(), y = context.getY();
        float zoom = context.getZoom(), width = context.getWidth() * zoom, height = context.getHeight() * zoom;
        GL gl = context.getGL();
        float[] c = color.getColorComponents(null);
        gl.glColor3f(c[0], c[1], c[2]);

        if (glayer != null) {
            if (!threshable) {
                if (getType().equalsIgnoreCase(POINT)) {
                    switch (this.displaysymbol) {
                        case point: {
                            gl.glPointSize(this.renderWidth);
                            gl.glBegin(GL.GL_POINTS);
                            for (Geometry temp : glayer.getGeometries()) {
                                Coordinate point = temp.getCoordinate();
                                gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                            }
                            gl.glEnd();
                            gl.glFlush();
                            if (selectedGeometry != null) {
                                gl.glPointSize(this.renderWidth * 2);
                                gl.glBegin(GL.GL_POINTS);
                                Coordinate point = selectedGeometry.getCoordinate();
                                gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                                gl.glEnd();
                                gl.glFlush();
                            }
                        }
                        break;
                        case circle: {
                        }
                        break;
                        case square: {
                            for (Geometry temp : glayer.getGeometries()) {
                                gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 3 : this.renderWidth);
                                Coordinate point = new Coordinate(temp.getCoordinate());
                                point.x = (point.x - x) / width;
                                point.y = 1 - (point.y - y) / height;
                                double rectwidth = 0.01;
                                gl.glBegin(GL.GL_LINE_STRIP);
                                gl.glVertex2d(point.x - rectwidth, point.y - rectwidth);
                                gl.glVertex2d(point.x - rectwidth, point.y + rectwidth);
                                gl.glVertex2d(point.x + rectwidth, point.y + rectwidth);
                                gl.glVertex2d(point.x + rectwidth, point.y - rectwidth);
                                gl.glVertex2d(point.x - rectwidth, point.y - rectwidth);
                                gl.glEnd();
                                gl.glFlush();
                            }
                        }
                        break;
                        case cross: {
                            for (Geometry temp : glayer.getGeometries()) {
                                gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                                Coordinate point = new Coordinate(temp.getCoordinate());
                                point.x = (point.x - x) / width;
                                point.y = 1 - (point.y - y) / height;
                                double rectwidth = 0.01;
                                gl.glBegin(GL.GL_LINE_STRIP);
                                gl.glVertex2d(point.x - rectwidth, point.y);
                                gl.glVertex2d(point.x + rectwidth, point.y);
                                gl.glEnd();
                                gl.glBegin(GL.GL_LINE_STRIP);
                                gl.glVertex2d(point.x, point.y - rectwidth);
                                gl.glVertex2d(point.x, point.y + rectwidth);
                                gl.glEnd();
                                gl.glFlush();
                            }
                        }
                        break;
                        case triangle: {
                            for (Geometry temp : glayer.getGeometries()) {
                                gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                                Coordinate point = new Coordinate(temp.getCoordinate());
                                point.x = (point.x - x) / width;
                                point.y = 1 - (point.y - y) / height;
                                double rectwidth = 0.01;
                                gl.glBegin(GL.GL_LINE_STRIP);
                                gl.glVertex2d(point.x - rectwidth, point.y - rectwidth);
                                gl.glVertex2d(point.x, point.y + rectwidth);
                                gl.glVertex2d(point.x + rectwidth, point.y - rectwidth);
                                gl.glVertex2d(point.x - rectwidth, point.y - rectwidth);
                                gl.glEnd();
                                gl.glFlush();
                            }

                        }
                        break;
                        default: {
                        }
                    }
                } else if (getType().equalsIgnoreCase(POLYGON)) {
                    for (Geometry temp : glayer.getGeometries()) {
                        if (temp.getCoordinates().length < 1) {
                            continue;
                        }
                        gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                        gl.glBegin(GL.GL_LINE_STRIP);
                        Coordinate[] cs = temp.getCoordinates();
                        for (int p = 0; p < cs.length; p++) {
                            gl.glVertex2d((cs[p].x - x) / width, 1 - (cs[p].y - y) / height);
                        }
                        Coordinate point = cs[0];
                        gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                        gl.glEnd();
                        gl.glFlush();
                    }
                } else if (getType().equalsIgnoreCase(LINESTRING)) {
                    for (Geometry temp : glayer.getGeometries()) {
                        if (temp.getCoordinates().length < 1) {
                            continue;
                        }
                        gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                        gl.glBegin(GL.GL_LINE_STRIP);
                        Coordinate[] cs = temp.getCoordinates();
                        for (int p = 0; p < cs.length; p++) {
                            gl.glVertex2d((cs[p].x - x) / width, 1 - (cs[p].y - y) / height);
                        }
                        gl.glEnd();
                        gl.glFlush();
                    }
                } else if (getType().equalsIgnoreCase(MIXED)) {
                    for (Geometry temp : glayer.getGeometries()) {
                        if (temp.getCoordinates().length < 1) {
                            continue;
                        }
                        if (temp instanceof LineString) {
                            gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                            gl.glBegin(GL.GL_LINE_STRIP);
                            for (Coordinate point : temp.getCoordinates()) {
                                gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                            }
                            gl.glEnd();
                            gl.glFlush();
                        } else if (temp instanceof Polygon) {
                            gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                            gl.glBegin(GL.GL_LINE_STRIP);
                            for (Coordinate point : temp.getCoordinates()) {
                                gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                            }
                            gl.glEnd();
                            gl.glFlush();
                        } else if (temp instanceof Point) {
                            gl.glPointSize(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                            gl.glBegin(GL.GL_POINTS);
                            Coordinate point = temp.getCoordinate();
                            gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                            gl.glEnd();
                            gl.glFlush();
                        }
                    }
                }
            } else {
                if (getType().equalsIgnoreCase(POINT)) {
                    switch (this.displaysymbol) {
                        case point: {
                            gl.glPointSize(this.renderWidth);
                            gl.glBegin(GL.GL_POINTS);
                            for (Geometry temp : glayer.getGeometries()) {
                                if (((Double) glayer.getAttributes(temp).get("significance")) > currentThresh) {
                                    Coordinate point = temp.getCoordinate();
                                    gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                                }
                            }
                            gl.glEnd();
                            gl.glFlush();
                            if (selectedGeometry != null) {
                                gl.glPointSize(this.renderWidth * 2);
                                gl.glBegin(GL.GL_POINTS);
                                Coordinate point = selectedGeometry.getCoordinate();
                                gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                                gl.glEnd();
                                gl.glFlush();
                            }
                        }
                        break;
                        case circle: {
                        }
                        break;
                        case square: {
                            for (Geometry temp : glayer.getGeometries()) {
                                if (((Double) glayer.getAttributes(temp).get("significance")) > currentThresh) {
                                    gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 3 : this.renderWidth);
                                    Coordinate point = new Coordinate(temp.getCoordinate());
                                    point.x = (point.x - x) / width;
                                    point.y = 1 - (point.y - y) / height;
                                    double rectwidth = 0.01;
                                    gl.glBegin(GL.GL_LINE_STRIP);
                                    gl.glVertex2d(point.x - rectwidth, point.y - rectwidth);
                                    gl.glVertex2d(point.x - rectwidth, point.y + rectwidth);
                                    gl.glVertex2d(point.x + rectwidth, point.y + rectwidth);
                                    gl.glVertex2d(point.x + rectwidth, point.y - rectwidth);
                                    gl.glVertex2d(point.x - rectwidth, point.y - rectwidth);
                                    gl.glEnd();
                                    gl.glFlush();
                                }
                            }
                        }
                        break;
                        case cross: {
                            for (Geometry temp : glayer.getGeometries()) {
                                if (((Double) glayer.getAttributes(temp).get("significance")) > currentThresh) {
                                    gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                                    Coordinate point = new Coordinate(temp.getCoordinate());
                                    point.x = (point.x - x) / width;
                                    point.y = 1 - (point.y - y) / height;
                                    double rectwidth = 0.01;
                                    gl.glBegin(GL.GL_LINE_STRIP);
                                    gl.glVertex2d(point.x - rectwidth, point.y);
                                    gl.glVertex2d(point.x + rectwidth, point.y);
                                    gl.glEnd();
                                    gl.glBegin(GL.GL_LINE_STRIP);
                                    gl.glVertex2d(point.x, point.y - rectwidth);
                                    gl.glVertex2d(point.x, point.y + rectwidth);
                                    gl.glEnd();
                                    gl.glFlush();
                                }
                            }
                        }
                        break;
                        case triangle: {
                            for (Geometry temp : glayer.getGeometries()) {
                                if (((Double) glayer.getAttributes(temp).get("significance")) > currentThresh) {
                                    gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                                    Coordinate point = new Coordinate(temp.getCoordinate());
                                    point.x = (point.x - x) / width;
                                    point.y = 1 - (point.y - y) / height;
                                    double rectwidth = 0.01;
                                    gl.glBegin(GL.GL_LINE_STRIP);
                                    gl.glVertex2d(point.x - rectwidth, point.y - rectwidth);
                                    gl.glVertex2d(point.x, point.y + rectwidth);
                                    gl.glVertex2d(point.x + rectwidth, point.y - rectwidth);
                                    gl.glVertex2d(point.x - rectwidth, point.y - rectwidth);
                                    gl.glEnd();
                                    gl.glFlush();
                                }
                            }

                        }
                        break;
                        default: {
                        }
                    }
                } else if (getType().equalsIgnoreCase(POLYGON)) {
                    for (Geometry temp : glayer.getGeometries()) {
                        if (((Double) glayer.getAttributes(temp).get("significance")) > currentThresh) {
                            if (temp.getCoordinates().length < 1) {
                                continue;
                            }
                            gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                            gl.glBegin(GL.GL_LINE_STRIP);
                            for (Coordinate point : temp.getCoordinates()) {
                                gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                            }
                            Coordinate point = temp.getCoordinates()[0];
                            gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                            gl.glEnd();
                            gl.glFlush();
                        }
                    }
                } else if (getType().equalsIgnoreCase(LINESTRING)) {
                    for (Geometry temp : glayer.getGeometries()) {
                        if (((Double) glayer.getAttributes(temp).get("significance")) > currentThresh) {
                            if (temp.getCoordinates().length < 1) {
                                continue;
                            }
                            gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                            gl.glBegin(GL.GL_LINE_STRIP);
                            for (Coordinate point : temp.getCoordinates()) {
                                gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                            }
                            gl.glEnd();
                            gl.glFlush();
                        }
                    }
                } else if (getType().equalsIgnoreCase(MIXED)) {
                    for (Geometry temp : glayer.getGeometries()) {
                        if (((Double) glayer.getAttributes(temp).get("significance")) > currentThresh) {
                            if (temp.getCoordinates().length < 1) {
                                continue;
                            }
                            if (temp instanceof LineString) {
                                gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                                gl.glBegin(GL.GL_LINE_STRIP);
                                for (Coordinate point : temp.getCoordinates()) {
                                    gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                                }
                                gl.glEnd();
                                gl.glFlush();
                            } else if (temp instanceof Polygon) {
                                gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                                gl.glBegin(GL.GL_LINE_STRIP);
                                for (Coordinate point : temp.getCoordinates()) {
                                    gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                                }
                                gl.glEnd();
                                gl.glFlush();
                            } else if (temp instanceof Point) {
                                gl.glPointSize(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                                gl.glBegin(GL.GL_POINTS);
                                Coordinate point = temp.getCoordinate();
                                gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                                gl.glEnd();
                                gl.glFlush();
                            }
                        }
                    }
                }
            }
        }

    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isRadio() {
        return false;
    }

    public ILayerManager getParent() {
        return parent;
    }

    public String getDescription() {
        return getName();
    }

    public void dispose() {
        glayer.clear();
        glayer = null;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getWidth() {
        return this.renderWidth;
    }

    public void setWidth(float width) {
        this.renderWidth = width;
    }

    public String[] getFileFormatTypes() {
        return new String[]{"csv", "shapefile"};
    }

    public boolean intersects(int x, int y, int width, int height) {
        try {
            if (getType().equals("point")) {
                return false;
            }
            WKTReader wkt = new WKTReader();
            Geometry geom = wkt.read("POLYGON((" + x + " " + y + "," + (x + width) + " " + y + "," + (x + width) + " " + (y + height) + "," + x + " " + (y + height) + "," + x + " " + y + "))");
            for (Geometry p : glayer.getGeometries()) {
                if (p.intersects(geom)) {
                    return true;
                }
            }
            return false;
        } catch (ParseException ex) {
            Logger.getLogger(SimpleVectorLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void mouseClicked(java.awt.Point imagePosition, int button, GeoContext context) {
        this.selectedGeometry = null;
        GeometryFactory gf = new GeometryFactory();
        Point p = gf.createPoint(new Coordinate(imagePosition.x, imagePosition.y));
        for (Geometry temp : glayer.getGeometries()) {
            if (p.equalsExact(temp, 5 * context.getZoom())) {
                this.selectedGeometry = temp;
            }
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GeometricLayer getGeometriclayer() {
        return glayer;
    }

    public void setGeometriclayer(GeometricLayer glayer) {
        this.glayer = glayer;
    }

    public boolean contains(int x, int y) {
        if (getType().equals(POINT)) {
            return false;
        }
        GeometryFactory gf = new GeometryFactory();
        Point geom = gf.createPoint(new Coordinate(x, y));
        for (Geometry p : glayer.getGeometries()) {
            if (p.contains(geom)) {
                return true;
            }
        }
        return false;
    }

    public boolean includes(int x, int y, int width, int height) {
        try {
            if (getType().equals("point")) {
                return false;
            }
            WKTReader wkt = new WKTReader();
            Geometry geom = wkt.read("POLYGON((" + x + " " + y + "," + (x + width) + " " + y + "," + (x + width) + " " + (y + height) + "," + x + " " + (y + height) + "," + x + " " + y + "))");
            for (Geometry p : glayer.getGeometries()) {
                if (geom.within(p)) {
                    return true;
                }
            }
            return false;
        } catch (ParseException ex) {
            Logger.getLogger(SimpleVectorLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // rasterize the mask clipped with the Rectangle scaled back to full size with an offset onto a BufferedImage
    public BufferedImage rasterize(Rectangle rect, int offsetX, int offsetY, double scalingFactor) {
        // create the buffered image of the size of the Rectangle
        BufferedImage image = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_BYTE_BINARY);
        GeometryFactory gf = new GeometryFactory();
        // define the clipping region in full scale
        Coordinate[] coords = new Coordinate[]{
            new Coordinate((int) (((double) rect.getMinX() / scalingFactor)), (int) (((double) rect.getMinY() / scalingFactor))),
            new Coordinate((int) (((double) rect.getMaxX() / scalingFactor)), (int) (((double) rect.getMinY() / scalingFactor))),
            new Coordinate((int) (((double) rect.getMaxX() / scalingFactor)), (int) (((double) rect.getMaxY() / scalingFactor))),
            new Coordinate((int) (((double) rect.getMinX() / scalingFactor)), (int) (((double) rect.getMaxY() / scalingFactor))),
            new Coordinate((int) (((double) rect.getMinX() / scalingFactor)), (int) (((double) rect.getMinY() / scalingFactor))),};
        Polygon geom = gf.createPolygon(gf.createLinearRing(coords), null);
        Graphics g2d = image.getGraphics();
        g2d.setColor(Color.WHITE);
        for (Geometry p : glayer.getGeometries()) {
            if (p.intersects(geom)) {
                int[] xPoints = new int[p.getNumPoints()];
                int[] yPoints = new int[p.getNumPoints()];
                int i = 0;
                for (Coordinate c : p.getCoordinates()) {
                    xPoints[i] = (int) ((c.x + offsetX) * scalingFactor);
                    yPoints[i++] = (int) ((c.y + offsetY) * scalingFactor);
                }
                g2d.fillPolygon(xPoints, yPoints, p.getNumPoints());
            }
        }
        g2d.dispose();
        return image;
    }

    public Area getShape() {
        Area maskArea = new Area();
        Rectangle rect = new Rectangle(0, 0, parent.getImage().getWidth(), parent.getImage().getHeight());
        GeometryFactory gf = new GeometryFactory();
        Coordinate[] coords = new Coordinate[]{
            new Coordinate((int) rect.getMinX(), (int) rect.getMinY()),
            new Coordinate((int) rect.getMaxX(), (int) rect.getMinY()),
            new Coordinate((int) rect.getMaxX(), (int) rect.getMaxY()),
            new Coordinate((int) rect.getMinX(), (int) rect.getMaxY()),
            new Coordinate((int) rect.getMinX(), (int) rect.getMinY()),};
        Polygon geom = gf.createPolygon(gf.createLinearRing(coords), null);
        for (Geometry p : glayer.getGeometries()) {
            if (p.intersects(geom)) {
                int[] xPoints = new int[p.getNumPoints()];
                int[] yPoints = new int[p.getNumPoints()];
                int i = 0;
                for (Coordinate c : p.getCoordinates()) {
                    xPoints[i] = (int) (c.x);
                    yPoints[i++] = (int) (c.y);
                }
                maskArea.add(new Area(new java.awt.Polygon(xPoints, yPoints, p.getNumPoints())));
            }
        }
        return maskArea;
    }

    public symbol getDisplaysymbol() {
        return displaysymbol;
    }

    public void setDisplaysymbol(symbol displaysymbol) {
        this.displaysymbol = displaysymbol;
    }

    public void buffer(double bufferingDistance) {
        PrecisionModel pm=new PrecisionModel(1);
        GeometryFactory gf = new GeometryFactory(pm);
        for (Geometry geom : new Vector<Geometry>(glayer.getGeometries())) {
            Geometry p = geom.buffer(bufferingDistance, 2, BufferParameters.CAP_SQUARE);
            if (p instanceof Polygon && ((Polygon) p).getNumInteriorRing() > 0) {
                p = gf.createPolygon((LinearRing) ((Polygon) p).getExteriorRing(), null);
            }
            glayer.replace(geom, p);

        }
        // then merge them
        Vector<Geometry> newgeoms = new Vector<Geometry>();
        Vector<Geometry> remove = new Vector<Geometry>();
        Vector<Geometry> parse = new Vector<Geometry>(glayer.getGeometries());


        for (Geometry g : parse) {
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


        glayer.clear();
        // assign new value
        for (Geometry geom : newgeoms) {
            glayer.put(geom);
        }
    }

    public List<Geometry> getGeometries() {
        return glayer.getGeometries();
    }

	@Override
	public boolean represents(IDataContent datacontent) {
		return m_datacontent == datacontent;
	}
}
