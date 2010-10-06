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
package com.metaaps.eoclipse.workflowmanager.NCE;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.metaaps.eoclipse.workflowmanager.WorkFlow;
import com.metaaps.eoclipse.workflowmanager.WorkFlowManager;

/**
 * @author leforthomas
 * Handler to create a new workflow in the tree.
 */
public class NewWorkFlow extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
        InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),
                "", "Provide a name for your Work Flow", "New WorkFlow", new IInputValidator() {
					
					@Override
					public String isValid(String newText) {
						if(newText.length() == 0) return "You need to specifiy a Name!";
						return null;
					}
		});
        if (dlg.open() == Window.OK) {
    		WorkFlowManager.getInstance().addWorkFlow(new WorkFlow(dlg.getValue()));
        }
		
		return null;
	}

}
