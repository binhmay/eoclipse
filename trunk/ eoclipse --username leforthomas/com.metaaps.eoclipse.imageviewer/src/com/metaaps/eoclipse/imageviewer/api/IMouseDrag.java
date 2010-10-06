/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.metaaps.eoclipse.imageviewer.api;

import java.awt.Point;

/**
 *
 * @author thoorfr
 */
public interface IMouseDrag {
    
    public void mouseDragged(Point initPosition, Point imagePosition, int button, GeoContext context);

}
