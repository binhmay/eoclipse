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

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class ViewsPerspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String viewID = NavigatorContent.ID;
		// add navigation window for the DataSetsManager
		layout.addStandaloneView(viewID, true, IPageLayout.LEFT, .25f, IPageLayout.ID_EDITOR_AREA);
		layout.setEditorAreaVisible(false);
		
		layout.getViewLayout(viewID).setCloseable(false);
		layout.getViewLayout(viewID).setMoveable(true);
		
		layout.addPerspectiveShortcut("com.metaaps.eoclipse.viewsperspective");
		
	}

}
