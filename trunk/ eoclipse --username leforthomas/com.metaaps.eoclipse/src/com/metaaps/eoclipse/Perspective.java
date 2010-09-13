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


/**
 * @author leforthomas
 */
public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String viewID = NavigatorContent.ID;
		// add navigation window for the DataSetsManager
		layout.addStandaloneView(viewID, true, IPageLayout.LEFT, .25f, IPageLayout.ID_EDITOR_AREA);
		IFolderLayout propertiesviewsfolder = layout.createFolder("com.metaaps.eoclipse.propertiesviewers", IPageLayout.BOTTOM, 0.75f, IPageLayout.ID_EDITOR_AREA);
		propertiesviewsfolder.addPlaceholder("*.eoclipsepropertiesviews.*:*");
		IFolderLayout gisviewsfolder = layout.createFolder("com.metaaps.eoclipse.gisviewers", IPageLayout.TOP, 0.75f, IPageLayout.ID_EDITOR_AREA);
		gisviewsfolder.addPlaceholder("*.eoclipsegisviews.*:*");
		layout.setEditorAreaVisible(false);
		
		layout.getViewLayout(viewID).setCloseable(false);
		layout.getViewLayout(viewID).setMoveable(false);
		
		layout.addPerspectiveShortcut("com.metaaps.eoclipse.perspective");
		
	}
}
