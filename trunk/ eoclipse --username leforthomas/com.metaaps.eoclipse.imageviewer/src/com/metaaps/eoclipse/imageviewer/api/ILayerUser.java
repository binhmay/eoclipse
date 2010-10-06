/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.metaaps.eoclipse.imageviewer.api;

/**
 *
 * @author thoorfr
 */
public interface ILayerUser {

    public void addListenner(ILayerListener l);
    public void removeListenner(ILayerListener l);

}
