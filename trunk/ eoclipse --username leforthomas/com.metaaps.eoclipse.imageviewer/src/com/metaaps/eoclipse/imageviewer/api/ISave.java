/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.metaaps.eoclipse.imageviewer.api;
/**
 *
 * @author thoorfr
 */
public interface ISave {
    
    public void save(String file, String type, String projection);
    public String[] getFileFormatTypes();

}
