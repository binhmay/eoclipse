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
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.datasets.IImport;
import com.metaaps.eoclipse.common.datasets.IImportMethod;
import com.metaaps.eoclipse.common.datasets.IImportWizardPage;
import com.metaaps.eoclipse.datasets.internal.wizards.ImportDataWizard;

/**
 * 
 * @author leforthomas
 * 
 * Handler for importing a data source through the import method tree item
 * Provides also the static method importdata to be reused in other contexts, eg @ImportHandler
 * 
 */
public class NewHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
        ITreeSelection currentSelection = (ITreeSelection)HandlerUtil.getCurrentSelection(event);
        Object obj = currentSelection.iterator().next();
        if(obj instanceof IImportMethod)
        {
        	IDataSets datasets = (IDataSets) Util.scanTreePath(currentSelection, IDataSets.class);
        	IImportMethod importmethod = (IImportMethod) obj;
        	importData(importmethod, datasets);
        }
        
        return null;
	}
	
	static public void importData(IImportMethod importmethod, IDataSets datasets) {
    	if(importmethod.useWizard()) {
        	// create wizard to collect reader and file
    		WizardPage importpage = importmethod.getImportWizardPage();
    		ImportDataWizard wizard = new ImportDataWizard((IImportWizardPage) importpage);
    		WizardDialog dialog = new WizardDialog(new Shell(), wizard);
    		if(dialog.open() == WizardDialog.OK) {
            	datasets.importDataContent(wizard.getImportMethod(), wizard.getReader(), wizard.getDataName());
    		}
    	} else {
        	IImport method = importmethod.getImport();
    		method.useOwnImport(datasets);
    	}
	}

}
