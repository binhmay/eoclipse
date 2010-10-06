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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.datasets.IDataSets;

/**
 * @author leforthomas
 * 
 * Handler for deleting a data item
 * 
 */
public class DeleteHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
        IStructuredSelection currentSelection = (IStructuredSelection)HandlerUtil.getCurrentSelection(event);
        Object obj = currentSelection.iterator().next();
        if(obj instanceof IDataContent)
        {
        	IDataContent data = (IDataContent) obj;
        	IDataSets datasets = (IDataSets) data.getParent();
    		datasets.removeDataContent(data);
        }
        
        return null;
	}

}
