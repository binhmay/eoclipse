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
package com.metaaps.eoclipse.opticalvds;

import java.util.HashMap;

import com.metaaps.eoclipse.common.CodeFragment;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.datasets.VectorData;
import com.metaaps.eoclipse.common.datasets.ProducedVectorDataContent;
import com.metaaps.eoclipse.processing.AbstractProcessing;

public class Processing extends AbstractProcessing {

	public Processing() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IDataContent execute(HashMap<String, Object> parametervalues) {
		System.out.println("Execute Optical VDS Process");
		return new ProducedVectorDataContent() {
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "Optical VDS Results";
			}
			
			@Override
			public String getType() {
				// TODO Auto-generated method stub
				return "Vector";
			}
			
		};
	}

}
