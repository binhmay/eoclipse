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
package com.metaaps.eoclipse.processing.NCE;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.processing.Process;

/**
 * @author leforthomas
 * 
 * Handler for menu item triggering the execution of a processing block
 * 
 */
public class Execute extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
        ITreeSelection currentSelection = (ITreeSelection)HandlerUtil.getCurrentSelection(event);
        Object obj = currentSelection.iterator().next();
        if(obj instanceof Process)
        {
        	// prepare parameters
            IDataContent data = (IDataContent) Util.scanTreePath(currentSelection, IDataContent.class);
        	((Process)obj).execute(data);
        }
        
        return null;
	}

}
