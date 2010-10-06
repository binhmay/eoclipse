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

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class ViewsPerspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String viewID = WorkFlowContent.ID;
		// add navigation window for the DataSetsManager
		IFolderLayout sidefolder = layout.createFolder("com.metaaps.eoclipse.sidefolder", IPageLayout.LEFT, 0.25f, IPageLayout.ID_EDITOR_AREA);
		sidefolder.addView(viewID);
		layout.setEditorAreaVisible(false);
		
		layout.getViewLayout(viewID).setCloseable(false);
		layout.getViewLayout(viewID).setMoveable(true);
		
		layout.addPerspectiveShortcut("com.metaaps.eoclipse.viewsperspective");
		
	}

}
