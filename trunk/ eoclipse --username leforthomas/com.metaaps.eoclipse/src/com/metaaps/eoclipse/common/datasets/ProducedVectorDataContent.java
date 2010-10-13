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
package com.metaaps.eoclipse.common.datasets;

import java.util.HashMap;

/**
 * @author leforthomas
 * 
 * Abstract Class representing a produced vector data
 * The geometry filtering needs implementing
 * 
 */
public abstract class ProducedVectorDataContent extends DataContent implements
		IVectorData, IProducedDataContent {

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Object> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGeometryName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VectorData getVectorData(String cqlFilter) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
