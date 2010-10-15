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

import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.workflowmanager.WorkFlowManager;

/**
 * @author leforthomas
 * 
 * Handler for the Views open menu
 * 
 */
public class OpenGenericViewerHandler extends AbstractHandler implements
		IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String viewerid = event.getParameter("com.metaaps.eoclipse.viewers.viewerid");
		IWorkbenchWindow workbench = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		try {
			workbench.getActivePage().showView(viewerid);
		} catch (PartInitException e) {
			e.printStackTrace();
			Util.errorMessage("Could not open the View");
		}
		
		IViewPart viewer = workbench.getActivePage().findViewReference(viewerid).getView(false);
		if(viewer instanceof ISelectionChangedListener) {
			WorkFlowManager.getInstance().addTreeSelectionListener((ISelectionChangedListener)viewer);
		}
		
		return null;
	}

}
