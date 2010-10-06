/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.metaaps.eoclipse.imagereaders;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import com.metaaps.eoclipse.common.datasets.IGeoRaster;

/**
 * A bunch of useful tools for
 * @author thoorfr
 */
public class GeoImageUtils {
    /**
     * create a quicklook image from the GeoImageReader
     * @param gir the source image
     * @param width the output width
     * @param height the output height
     * @return a BufferedImage representing the quicklook
     */
    public static BufferedImage createOverview(IGeoRaster gir, int width, int height){
        int nPass=gir.getHeight()/256;
        int xstep=gir.getWidth()/width;
        int ystep=gir.getHeight()/height;
        width=gir.getWidth()/xstep;
        height= gir.getHeight()/ystep;
        BufferedImage out=new BufferedImage(width,height, BufferedImage.TYPE_USHORT_GRAY);
        WritableRaster raster=out.getRaster();
        for(int i=0;i<nPass;i++){
            int[] t=gir.readTile(0, i*256, gir.getWidth(), 256);
            for(int x=0;x<width;x++){
                for(int y=i*height/nPass;y<(i+1)*height/nPass;y++){
                    raster.setSample(x, y, 0, t[x*xstep*(y-i*height/nPass)*ystep]);
                }
            }
        }
        return out;
    }
}
