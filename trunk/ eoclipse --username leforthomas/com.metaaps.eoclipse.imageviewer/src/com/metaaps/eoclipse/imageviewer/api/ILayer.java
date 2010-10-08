/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.metaaps.eoclipse.imageviewer.api;

import org.opengis.geometry.coordinate.Position;

/**
 *
 * @author thoorfr
 * 
 * modified for EOClipse by leforthomas
 * 
 */
public interface ILayer extends com.metaaps.eoclipse.common.views.ILayer {
    public void render(GeoContext context);
    public ILayerManager getParent();
    public void dispose();
}
