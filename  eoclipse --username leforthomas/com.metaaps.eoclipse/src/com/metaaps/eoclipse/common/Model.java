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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.eclipse.jface.resource.ImageDescriptor;
import org.jdom.Element;

/**
 * @author leforthomas
 * 
 * an abstract class for the IModel interface
 * 
 */
public abstract class Model implements IModel {
	
	protected ArrayList<Object> m_children = new ArrayList<Object>();
	
	protected ArrayList<IModelChangeListener> listeners = new ArrayList<IModelChangeListener>(); //NullDeltaListener.getSoleInstance();
	
	protected Object m_parent = null;
	
	protected String m_id = null;
	
	public void fireChanged(Object added, String event) {
		System.out.println("fireChanged(" + added + ", " + event + ")");
		for(IModelChangeListener listener : listeners) {
			System.out.println("	@" + listener.toString() + " modelChanged(" + added + ", " + event + ")");
			listener.modelChanged(added, event);
		}
	}

	public void addListener(IModelChangeListener listener) {
		System.out.println(toString() + " addListener(" + listener.toString() + ")");
		// make sure we don't add it twice
		listeners.remove(listener);
		listeners.add(listener);
	}
	
	public void removeListener(IModelChangeListener listener) {
		System.out.println("removeListener(" + listener.toString() + ")");
		listeners.remove(listener);
	}
	
	@Override
	public void addChild(Object obj) {
		m_children.add(obj);
		if(obj instanceof Model)
		{
			((Model)obj).setParent(this);
		}
	}
	
	@Override
	public Object[] getChildren() {
		return m_children.toArray();
	}
	
	@Override
	public void removeChild(Object obj) {
		m_children.remove(obj);
	}
	
	@Override
	public void setParent(Object obj) {
		m_parent = obj;
	}
	
	@Override
	public Object getParent() {
		return m_parent;
	}
	
	/**
	 * Returns the item id in the tree
	 * This should be used as a reference to the object for saving/exporting and open/rerun of workflows
	 *
	 * @return the item id in the tree
	 */
	@Override
	public String getId() {
		return m_id;
	}
	
	public void setId(String id) {
		m_id = id;
	}
	
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
