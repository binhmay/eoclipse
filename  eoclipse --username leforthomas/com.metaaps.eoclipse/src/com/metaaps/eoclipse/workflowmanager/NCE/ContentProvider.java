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

import com.metaaps.eoclipse.common.Model;
import com.metaaps.eoclipse.common.ModelChangeListener;
import com.metaaps.eoclipse.workflowmanager.DataSets;
import com.metaaps.eoclipse.workflowmanager.WorkFlow;
import com.metaaps.eoclipse.workflowmanager.WorkFlowManager;

/**
 * @author leforthomas
 */
public class ContentProvider extends ModelChangeListener {

    private static final Object[] NO_CHILDREN = {};
    
    public ContentProvider() {
    	WorkFlowManager.getInstance().addListener(this);
	}
    
	@Override
	public void dispose() {

	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
        Object[] children = NO_CHILDREN;
		if(parentElement instanceof WorkFlowManager)
		{
            children = WorkFlowManager.getInstance().getChildren();
        } else if((parentElement instanceof WorkFlow) || (parentElement instanceof DataSets)) {
        	children = ((Model) parentElement).getChildren();
        	((Model) parentElement).addListener(this);
        }

        return children;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

}
