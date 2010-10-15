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
package com.metaaps.eoclipse.datasets.importmethods;

import com.metaaps.eoclipse.common.datasets.IImportMethod;
import com.metaaps.eoclipse.common.datasets.IImports;

/**
 * @author leforthomas
 * 
 * Utility class for retrieving Import Methods
 * 
 */
public class AllImports implements IImports {

	public AllImports() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IImportMethod findImport(String scheme) {
		for(Object obj : ImportFolder.getInstance().getChildren()) {
			IImportMethod importmethod = (IImportMethod) obj;
			if((scheme.length() > 0) && scheme.contentEquals(importmethod.getURIScheme())) {
				return importmethod;
			}
		}
		
		return null;
	}

}
