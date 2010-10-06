/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.metaaps.eoclipse.imageviewer.api;


/**
 *
 * @author thoorfr
 */
public interface ILayer extends com.metaaps.eoclipse.common.views.ILayer {
    public String getName();
    public void setName(String name);
    public void render(GeoContext context);
    public boolean isActive();
    public void setActive(boolean active);
    public ILayerManager getParent();
    public void dispose();
}
