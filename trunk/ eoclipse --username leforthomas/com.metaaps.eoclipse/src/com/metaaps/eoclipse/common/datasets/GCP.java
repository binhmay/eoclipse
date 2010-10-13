/*******************************************************************************
 * Copyright (c) 2010 METAAPS SRL(U).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     METAAPS SRL(U) - created by Thomas Lefort - initial API and implementation
 ******************************************************************************/
package com.metaaps.eoclipse.common.datasets;

/**
 * A simple Bean class to register gcps. the Angle and the Zgeo coordinate (altitude) are facultative
 * @author thoorfr
 */
public class GCP {

    private float angle;

    private double xpix;

    private double ypix;

    private double xgeo;

    private double ygeo;

    private double zgeo;

    public GCP(double xpix, double ypix, double xgeo, double ygeo){
        this.xgeo=xgeo;
        this.xpix=xpix;
        this.ygeo=ygeo;
        this.ypix=ypix;
    }

    public GCP(){
        
    }

    public double getXgeo () {
        return xgeo;
    }

    public void setXgeo (double val) {
        this.xgeo = val;
    }

    public float getAngle () {
        return angle;
    }

    public void setAngle (float val) {
        this.angle = val;
    }

    public double getXpix () {
        return xpix;
    }

    public void setXpix (double val) {
        this.xpix = val;
    }

    public double getYgeo () {
        return ygeo;
    }

    public void setYgeo (double val) {
        this.ygeo = val;
    }

    public double getYpix () {
        return ypix;
    }

    public void setYpix (double val) {
        this.ypix = val;
    }

    public double getZgeo () {
        return zgeo;
    }

    public void setZgeo (double val) {
        this.zgeo = val;
    }

}

