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
package com.metaaps.eoclipse.datasets.internal.NCE;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.datasets.importmethods.ImportFolder;
import com.metaaps.eoclipse.datasets.importmethods.ImportMethod;

/**
 * 
 * @author leforthomas
 * 
 * Handler for importing a data source through the context menu
 * 
 */
public class ImportHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
        ITreeSelection currentSelection = (ITreeSelection)HandlerUtil.getCurrentSelection(event);
        Object obj = currentSelection.iterator().next();
        
        if(obj instanceof IDataSets)
        {
        	IDataSets datasets = (IDataSets) obj;
	    	ImportMethod importmethod = null;
			try {
				String extension = event.getParameter("com.metaaps.eoclipse.datasets.importmethod");
				for(Object impobj : ImportFolder.getInstance().getChildren()) {
					if(impobj instanceof ImportMethod) {
						if(((ImportMethod) impobj).getFullExtension().contentEquals(extension)) {
							importmethod = (ImportMethod) impobj;
							break;
						}
					}
				}
				NewHandler.importData(importmethod, datasets);
			} catch (Exception e) {
				Util.errorMessage("Could not activate Import Method");
				e.printStackTrace();
			}
        }
		return null;
	}

}
