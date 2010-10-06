/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.metaaps.eoclipse.imageviewer.api;

import java.util.List;

/**
 *
 * @author thoorfr
 */
public interface IAction {

    public String getName();
    public String getDescription();
    /**
     * Gets the path to access the action from the menubar
     * Should be of the form "Tools|Action|myAction|"
     * @return
     */
    public String getPath();
    public boolean execute(String[] args);
    public List<Argument> getArgumentTypes();
}
