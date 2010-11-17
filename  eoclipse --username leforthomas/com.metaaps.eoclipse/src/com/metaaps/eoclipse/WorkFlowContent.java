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
package com.metaaps.eoclipse;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import com.metaaps.eoclipse.workflowmanager.WorkFlowManager;

/**
 * @author leforthomas
 */
public class WorkFlowContent extends CommonNavigator {

	public static final String ID = "com.metaaps.eoclipse.navigatorcontent";
	private Composite top;
	
	@Override
	public void createPartControl(Composite aParent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(aParent, "com.metaaps.eoclipse.message"); // NEW
		
		// TODO Auto-generated method stub
		super.createPartControl(aParent);
		
		top = aParent;
		
	}
	
	@Override
	protected Object getInitialInput() {
		WorkFlowManager workflowmanager = WorkFlowManager.getInstance();
		workflowmanager.setNavigator(this);
		return workflowmanager;
	}
	
	@Override
	public void setFocus() {
		if(top != null) {
			top.setFocus();
		}
	}
	
}
