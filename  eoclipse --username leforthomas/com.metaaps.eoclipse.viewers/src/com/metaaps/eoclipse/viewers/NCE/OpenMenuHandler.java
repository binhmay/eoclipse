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
package com.metaaps.eoclipse.viewers.NCE;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.metaaps.eoclipse.common.IWorkFlow;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.viewers.Viewer;
import com.metaaps.eoclipse.viewers.Viewers;


/**
 * @author leforthomas
 * 
 * Handler for the DataSets Tree Item Viewer Contextual Menu
 * 
 */
public class OpenMenuHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
        ITreeSelection currentSelection = (ITreeSelection)HandlerUtil.getCurrentSelection(event);
        Object obj = currentSelection.iterator().next();
        
        if(obj instanceof IDataSets)
        {
	    	Viewer viewer = null;
			try {
				String extension = event.getParameter("com.metaaps.eoclipse.viewers");
				for(Object viewerobj : Viewers.getInstance().getChildren()) {
					if(viewerobj instanceof Viewer) {
						if(((Viewer) viewerobj).getFullExtension().contentEquals(extension)) {
							viewer = (Viewer) viewerobj;
							break;
						}
					}
				}
	            IWorkFlow workflow = (IWorkFlow) Util.scanTreePath(currentSelection, IWorkFlow.class);
				viewer.Open(workflow);
			} catch (Exception e) {
				Util.errorMessage("Could not activate Import Method");
				e.printStackTrace();
			}
        }
		return null;
	}

}
