/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.metaaps.eoclipse.imageviewer.api;

import com.metaaps.eoclipse.common.datasets.IGeoRaster;

/**
 *
 * @author thoorfr
 */
public interface IImageLayer extends ILayerManager{

    public float getContrast();
    public float getBrightness();
    public void setContrast(float value);
    public void setBrightness(float value);
    public void setMaximum(float value);
    public float getMaximum();
    public void setMinimum(float value);
    public float getMinimum();
    public void setBand(int[] values);
    public int getNumberOfBands();
    public int[] getBands();
    public IGeoRaster getImage();
}
