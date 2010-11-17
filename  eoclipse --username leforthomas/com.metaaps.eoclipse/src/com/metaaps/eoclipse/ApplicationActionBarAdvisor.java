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

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    private IWorkbenchAction exitAction;
	private IWorkbenchAction aboutAction;
	private IWorkbenchAction showHelpAction;
	private IAction searchHelpAction;
	private IWorkbenchAction dynamicHelpAction;
	private IWorkbenchAction introAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
        exitAction = ActionFactory.QUIT.create(window);
        register(exitAction);

        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);

        showHelpAction = ActionFactory.HELP_CONTENTS.create(window); // NEW
        register(showHelpAction); // NEW

        searchHelpAction = ActionFactory.HELP_SEARCH.create(window); // NEW
        register(searchHelpAction); // NEW

        dynamicHelpAction = ActionFactory.DYNAMIC_HELP.create(window); // NEW
        register(dynamicHelpAction); // NEW
        
        introAction = ActionFactory.INTRO.create(window);
        register(introAction);
        
//        views = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
    }

    protected void fillMenuBar(IMenuManager menuBar) {
    	   MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
    	   // Add a group marker indicating where action set menus will appear.
    	   menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
    	   menuBar.add(helpMenu);

    	   // Help
    	   helpMenu.add(aboutAction);
    	   helpMenu.add(showHelpAction); // NEW
    	   helpMenu.add(searchHelpAction); // NEW
    	   helpMenu.add(dynamicHelpAction); // NEW
    	   helpMenu.add(introAction); // NEW
    }
    
}
