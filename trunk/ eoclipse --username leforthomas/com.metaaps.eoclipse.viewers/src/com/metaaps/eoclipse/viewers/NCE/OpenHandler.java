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
import com.metaaps.eoclipse.viewers.Viewer;

/**
 * @author leforthomas
 * 
 * Handler for the Open Contextual Tree Item Menu
 * 
 */
public class OpenHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
        ITreeSelection currentSelection = (ITreeSelection)HandlerUtil.getCurrentSelection(event);
        Object obj = currentSelection.iterator().next();
        if(obj instanceof Viewer)
        {
            IWorkFlow workflow = (IWorkFlow) Util.scanTreePath(currentSelection, IWorkFlow.class);
        	((Viewer)obj).Open(workflow, null);
        }
        
		return null;
	}

}
