/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metaaps.eoclipse.imageviewer.layers;


import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.imageviewer.api.GeoContext;
import com.metaaps.eoclipse.imageviewer.api.GeometricLayer;
import com.metaaps.eoclipse.imageviewer.api.IClickable;
import com.metaaps.eoclipse.imageviewer.api.IEditable;
import com.metaaps.eoclipse.imageviewer.api.IImageLayer;
import com.metaaps.eoclipse.imageviewer.api.IKeyPressed;
import com.metaaps.eoclipse.imageviewer.api.IMouseDrag;
import com.metaaps.eoclipse.imageviewer.api.IMouseMove;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.media.opengl.GL;

/**
 *
 * @author thoorfr
 */
public class SimpleEditVectorLayer extends SimpleVectorLayer implements IClickable, IMouseMove, IEditable, IMouseDrag, IKeyPressed {

    private Coordinate editedPoint = null;
    private boolean edit = false;
    private boolean add = false;
    private boolean delete = false;
    private boolean move = false;
    private Vector<Geometry> toBeRemoved = new Vector<Geometry>();
    private GeometryFactory gf;

    public SimpleEditVectorLayer(String layername, IImageLayer parent, IDataContent datacontent, GeometricLayer layer) {
        super(layername, parent, datacontent, layer);
        gf = new GeometryFactory();
    }

    @Override
    public void mouseClicked(Point imagePosition, int button, GeoContext context) {
        if (!this.edit || button != IClickable.BUTTON1) {
            super.mouseClicked(imagePosition, button, context);
            return;
        }
        if (this.move) {
            performMove(imagePosition, context);
        } else if (this.add) {
            performAdd(imagePosition, context);
        } else if (this.delete) {
            performDelete(imagePosition, context);
        }
        super.mouseClicked(imagePosition, button, context);

    }

    @Override
    public void render(GeoContext context) {
        if (toBeRemoved.size() > 0) {
            for (Geometry remove : toBeRemoved) {
                glayer.remove(remove);
            }

            toBeRemoved.clear();
        }

        super.render(context);
        if (!context.isDirty() || glayer == null || !this.edit) {
            return;
        }

        int x = context.getX(), y = context.getY();
        float zoom = context.getZoom(), width = context.getWidth() * zoom, height = context.getHeight() * zoom;
        GL gl = context.getGL();
        if (type.equalsIgnoreCase(POLYGON) || type.equalsIgnoreCase(LINESTRING)) {
            gl.glPointSize(getWidth() * 2);
            gl.glBegin(GL.GL_POINTS);
            for (Geometry temp : glayer.getGeometries()) {
                for (Coordinate point : temp.getCoordinates()) {
                    gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);

                }

            }
            gl.glEnd();
            gl.glFlush();
        }

        if (editedPoint == null) {
            return;
        }

        float[] c = getColor().brighter().getColorComponents(null);
        gl.glColor3f(c[0], c[1], c[2]);
        gl.glPointSize(this.getWidth() * 3);
        gl.glBegin(GL.GL_POINTS);
        gl.glVertex2d((editedPoint.x - x) / width, 1 - (editedPoint.y - y) / height);
        gl.glEnd();
        gl.glFlush();
    }

    public void mouseMoved(Point imagePosition, GeoContext context) {
        if (this.selectedGeometry == null || this.editedPoint == null) {
            return;
        } else {
            editedPoint.x = imagePosition.x;
            editedPoint.y = imagePosition.y;
        }

    }

    public void setEditable(boolean editable) {
        this.edit = editable;
        if (!this.edit) {
            editedPoint = null;
            updateActions();
        }

    }

    public boolean isEditable() {
        return this.edit;
    }

    public void setAddAction(boolean add) {
        if (add) {
            updateActions();
        }

        this.add = add;
    }

    public void setDeleteAction(boolean delete) {
        if (delete) {
            updateActions();
        }

        this.delete = delete;

    }

    public boolean isAddAction() {
        return this.add;
    }

    public boolean isDeleteAction() {
        return this.delete;
    }

    public void setMoveAction(boolean move) {
        if (move) {
            updateActions();
        }

        this.move = move;
    }

    public boolean isMoveAction() {
        return this.move;
    }

    private double computeDistance(Coordinate point1, Coordinate point2, Coordinate point3, Coordinate point4) {
        return point1.distance(point2) +
                point2.distance(point3) +
                point3.distance(point4);
    }

    private void performAdd(Point imagePosition, GeoContext context) {
        if (type.equals(POINT)) {
            selectedGeometry = gf.createPoint(new Coordinate(imagePosition.x, imagePosition.y));
            glayer.put(selectedGeometry);
        } else if (type.equals(POLYGON) || type.equals(LINESTRING)) {
            if (selectedGeometry == null && (glayer.getGeometries().size() != 0)) {
                super.mouseClicked(imagePosition, BUTTON1, context);
                return;
            }
            if (glayer.getGeometries().size() == 0 | selectedGeometry == null) {
                try {
                    double step = 5 * context.getZoom();
                    Coordinate initend = new Coordinate(imagePosition.x - step, imagePosition.y - step);
                    if (type.equals(POLYGON)) {
                        selectedGeometry = gf.createPolygon(gf.createLinearRing(new Coordinate[]{initend, new Coordinate(imagePosition.x - step, imagePosition.y + step), new Coordinate(imagePosition.x + step, imagePosition.y + step), initend}), null);
                    } else {
                        selectedGeometry = gf.createLineString(new Coordinate[]{initend, new Coordinate(imagePosition.x - step, imagePosition.y + step)});
                    }
                    glayer.put(selectedGeometry);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            Coordinate closestPoint = null;
            Geometry currGeom = selectedGeometry;
            if (currGeom == null) {
                return;
            }
            int index = -1;
            double dist = Double.MAX_VALUE;
            if (type.equals(POLYGON)) {
                Coordinate[] polygon = ((Polygon) currGeom).getExteriorRing().getCoordinates();
                for (int i = 0; i < polygon.length - 1; i++) {
                    Coordinate point = polygon[i];
                    if ((imagePosition.x - point.x) * (imagePosition.x - point.x) + (imagePosition.y - point.y) * (imagePosition.y - point.y) < dist) {
                        closestPoint = point;
                        index = i;
                        dist = (imagePosition.x - point.x) * (imagePosition.x - point.x) + (imagePosition.y - point.y) * (imagePosition.y - point.y);
                    }

                }
                editedPoint = new Coordinate(imagePosition.x, imagePosition.y);
                int indexAfter = index + 1;
                if (indexAfter == polygon.length) {
                    indexAfter = 0;
                }
                int indexBefore = index - 1;
                if (indexBefore < 0) {
                    indexBefore = polygon.length - 1;
                }
                Coordinate after = polygon[indexAfter];
                Coordinate before = polygon[indexBefore];
                double distance1 = computeDistance(before, editedPoint, closestPoint, after);
                double distance2 = computeDistance(before, closestPoint, editedPoint, after);
                if (distance1 < distance2) {
                    Coordinate[] newC = new Coordinate[polygon.length + 1];
                    for (int i = 0; i < index; i++) {
                        newC[i] = polygon[i];
                    }
                    newC[index] = editedPoint;
                    for (int i = index + 1; i < newC.length; i++) {
                        newC[i] = polygon[i - 1];
                    }
                    //closing the ring
                    newC[newC.length - 1] = newC[0];
                    Geometry newGeom = gf.createPolygon(gf.createLinearRing(newC), null);
                    glayer.replace(currGeom, newGeom);
                    selectedGeometry = newGeom;
                } else {
                    Coordinate[] newC = new Coordinate[polygon.length + 1];
                    for (int i = 0; i < index; i++) {
                        newC[i] = polygon[i];
                    }
                    newC[index] = editedPoint;
                    for (int i = index + 1; i < newC.length; i++) {
                        newC[i] = polygon[i - 1];
                    }
                    //closing the ring
                    newC[newC.length - 1] = newC[0];
                    Geometry newGeom = gf.createPolygon(gf.createLinearRing(newC), null);
                    glayer.replace(currGeom, newGeom);
                    selectedGeometry = newGeom;
                }
            }

            if (type.equals(POINT)) {
                glayer.put(gf.createPoint(editedPoint));
                editedPoint = null;
                return;
            }


            editedPoint = null;
        }
    }

    private void performDelete(Point imagePosition, GeoContext context) {
        if (selectedGeometry == null) {
            super.mouseClicked(imagePosition, BUTTON1, context);
            return;
        }
        if (this.editedPoint == null) {
            com.vividsolutions.jts.geom.Point p = gf.createPoint(new Coordinate(imagePosition.x, imagePosition.y));
            if (type.equals(POINT)) {
                if (p.isWithinDistance(selectedGeometry, 5 * context.getZoom())) {
                    toBeRemoved.add(selectedGeometry);
                }
                selectedGeometry = null;
            } else if (type.equals(POLYGON)) {
                Coordinate[] polygon = ((Polygon) selectedGeometry).getExteriorRing().getCoordinates();
                for (Coordinate point : polygon) {
                    if (p.isWithinDistance(gf.createPoint(point), 5 * context.getZoom())) {
                        if (polygon.length == 4) {
                            toBeRemoved.add(selectedGeometry);
                        } else {
                            Coordinate[] newC = new Coordinate[polygon.length - 1];
                            int jump = 0;
                            for (int i = 0; i < polygon.length; i++) {
                                if (polygon[i] == point) {
                                    jump = 1;
                                } else {
                                    newC[i - jump] = polygon[i];
                                }
                            }
                            //closing the ring
                            newC[newC.length - 1] = newC[0];
                            Geometry newGeom = gf.createPolygon(gf.createLinearRing(newC), null);
                            glayer.replace(selectedGeometry, newGeom);
                            selectedGeometry = newGeom;
                        }
                        break;
                    }
                }
            }
        }
    }

    private void performMove(Point imagePosition, GeoContext context) {
        if (selectedGeometry == null) {
            super.mouseClicked(imagePosition, IClickable.BUTTON1, context);

            if (this.editedPoint == null && selectedGeometry != null) {
                if (type.equals(POINT)) {
                    this.editedPoint = selectedGeometry.getCoordinate();
                } else if (type.equals(POLYGON)) {
                    LineString ls = ((Polygon) selectedGeometry).getExteriorRing();
                    for (int i = 0; i < ls.getNumPoints(); i++) {
                        Coordinate point = ls.getCoordinateN(i);
                        if (Math.abs(imagePosition.x - point.x) < 5 * context.getZoom() && Math.abs(imagePosition.y - point.y) < 5 * context.getZoom()) {
                            this.editedPoint = point;
                            break;
                        }
                    }
                }
                if (this.editedPoint == null) {
                    selectedGeometry.geometryChanged();
                    selectedGeometry = null;
                }
            } else {
                if (selectedGeometry != null) {
                    selectedGeometry.geometryChanged();
                }
                this.editedPoint = null;
            }
        } else {
            selectedGeometry.geometryChanged();
            editedPoint = null;
            selectedGeometry = null;
        }
    }

    private void updateActions() {
        this.selectedGeometry = null;
        this.move = false;
        this.add = false;
        this.delete = false;
        this.editedPoint = null;
    }

    public void mouseDragged(final Point initPosition, final Point imagePosition, int button, GeoContext context) {
        if (selectedGeometry == null | !this.edit | this.move | this.add | this.delete) {
            return;
        }
        if (!selectedGeometry.contains(gf.createPoint(new Coordinate(initPosition.x, initPosition.y)))) {
            selectedGeometry = null;
            return;
        }
        if (type.equals(POLYGON) || type.equals(LINESTRING)) {
            selectedGeometry.apply(new CoordinateSequenceFilter() {

                public void filter(CoordinateSequence seq, int i) {
                    if (i == seq.size() - 1) {
                        if (seq.getCoordinate(i) == seq.getCoordinate(0)) {
                            return;
                        }
                    }
                    seq.getCoordinate(i).x += imagePosition.x - initPosition.x;
                    seq.getCoordinate(i).y += imagePosition.y - initPosition.y;
                }

                public boolean isDone() {
                    return false;
                }

                public boolean isGeometryChanged() {
                    return true;
                }
            });
        }

    }

    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            if (selectedGeometry != null) {
                toBeRemoved.add(selectedGeometry);
                selectedGeometry = null;
            }
        }
    }
}
