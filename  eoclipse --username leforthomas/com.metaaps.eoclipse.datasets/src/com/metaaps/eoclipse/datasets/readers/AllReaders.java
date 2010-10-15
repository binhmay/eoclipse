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
package com.metaaps.eoclipse.datasets.readers;

import com.metaaps.eoclipse.common.datasets.IReader;
import com.metaaps.eoclipse.common.datasets.IReaders;

/**
 * @author leforthomas
 * 
 * Utility class for retrieving Readers
 * 
 */
public class AllReaders implements IReaders {

	public AllReaders() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IReader findReaderWithName(String readername) {
		for(Object obj : ReadersFolder.getInstance().getChildren()) {
			IReader childreader = (IReader) obj;
			if(childreader.getName().contentEquals(readername)) {
				return childreader;
			}
		}
		
		return null;
	}

}
