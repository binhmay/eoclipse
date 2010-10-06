/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.metaaps.eoclipse.imageviewer.layers.image;

import com.metaaps.eoclipse.common.datasets.IGeoRaster;

/**
 *
 * @author thoorfr
 */
public class ImagePool {
    private IGeoRaster[] images;
    private int index=0;
    private boolean[] locked;

    public ImagePool(IGeoRaster gir,int poolsize) {
        images=new IGeoRaster[poolsize];
        for(int i=0;i<poolsize;i++){
            images[i]=gir.clone();
        }
        locked=new boolean[poolsize];
    }
    
    public IGeoRaster get(){
        if(index>images.length-1) index=0;
        if(locked[index]) return get(0);
        else{
            locked[index]=true;
            return images[index++];
        }
    }
    
    public void release(IGeoRaster gir){
        for(int i=0;i<images.length;i++){
            if(gir==images[i]){
                locked[i]=false;
            }
        }
    }
    
    public void dispose(){
        for(int i=0;i<images.length;i++){
            images[i].dispose();
            images[i]=null;
        }
    }

    private IGeoRaster get(int i) {
        if(i==images.length) return null;
        if(index>images.length-1) index=0;
        if(locked[index]) return get(i+1);
        else{
            locked[index]=true; 
            return images[index++];
        }
    }
    
    
}
