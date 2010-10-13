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
package com.metaaps.eoclipse.common;

import java.io.Serializable;
import java.util.ArrayList;

import org.jdom.Element;

/**
 * @author leforthomas
 * 
 * IModel is the interface implemented by all workflow elements
 * 
 */
public interface IModel extends IDescriptor, IEvent {
	
	void addChild(Object obj);
	
	Object[] getChildren();
	
	void removeChild(Object obj);
	
	void setParent(Object obj);
	
	Object getParent();

}
