/*******************************************************************************
 * Copyright (c) 2010 METAAPS SRL(U).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     METAAPS SRL(U) - initial API and implementation
 ******************************************************************************/
package com.metaaps.eoclipse.globeviewer;

import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.metaaps.eoclipse.common.IModelChangeListener;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.views.ILayer;
import com.metaaps.eoclipse.common.views.ILayeredViewer;
import com.metaaps.eoclipse.common.views.IViewerImplementation;
import com.metaaps.eoclipse.globeviewer.layers.GlobeViewerLayer;
import com.metaaps.eoclipse.viewers.util.AbstractViewerImplementation;

/**
 * @author leforthomas
 *
 * The View itself
 * 
 */
public class GlobeViewer extends AbstractViewerImplementation implements IViewerImplementation, ILayeredViewer {

	private GlobeViewerControl m_globeviewercontrol;

	public GlobeViewer() {
		m_name = "Globe Viewer";
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		m_globeviewercontrol = new GlobeViewerControl(parent); //sashForm);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void dispose() {
		m_datasets.removeChild(this);
		super.dispose();
	}

	public void setDataSets(IDataSets datasets) {
		// scan datasets for all data
		for(Object obj : datasets.getChildren()) {
			if(obj instanceof IDataContent) {
				IDataContent datacontent = (IDataContent) obj;
				try {
					addDataLayer(datacontent);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// update table too
//		m_layertableviewer.setGlobeViewerControl(m_globeviewercontrol);
		// add datasets listener
		datasets.addListener(this);
		m_datasets = datasets;
	}
	
	private void addDataLayer(IDataContent datacontent) throws ParseException {
		m_globeviewercontrol.addLayer(datacontent);
		// tell listeners we have changed
		fireChanged(datacontent, ADDED);
	}
	
	private void removeDataLayer(IDataContent datacontent) throws ParseException {
		m_globeviewercontrol.removeLayer(datacontent);
		// tell listeners we have changed
		fireChanged(datacontent, REMOVED);
	}
	
	@Override
	public void modelChanged(Object element, String event) {
		if(element instanceof IDataContent) {
			try {
				IDataContent datacontent = (IDataContent) element;
				if(event == com.metaaps.eoclipse.common.Model.ADDED) {
					addDataLayer(datacontent);
				} else if(event == com.metaaps.eoclipse.common.Model.REMOVED) {
					removeDataLayer(datacontent);
				}
			} catch (ParseException e) {
				Util.errorMessage("Could not remove data layer.");
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<ILayer> getLayers() {
		ArrayList<ILayer> layers = new ArrayList<ILayer>();
		for(Layer layer : m_globeviewercontrol.getLayers()) {
			if(layer instanceof ILayer) {
				layers.add((ILayer) layer);
			}
		}
		
		return layers;
	}

	@Override
	public String getName() {
		return m_name;
	}
	
	@Override
	public void setName(String name) {
		m_name = name;
		setPartName("Globe Viewer - " + name);
	}

	@Override
	public void refresh() {
		m_globeviewercontrol.world.redraw();
	}
	
	@Override
	public void moveLayer(ILayer layer, boolean up) {
		LayerList layerlist = m_globeviewercontrol.getLayers();
		int curindex = layerlist.indexOf((GlobeViewerLayer) layer, 0);
		if(curindex > -1 ) {
			layerlist.remove(curindex);
			if(up) {
				if(curindex > 0) {
					layerlist.add(curindex - 1, (GlobeViewerLayer) layer);
				}
			} else {
				if(curindex + 1 < layerlist.size()) {
					layerlist.add(curindex + 1, (GlobeViewerLayer) layer);
				} else {
					layerlist.add((GlobeViewerLayer) layer);
				}
			}
		}
	}

}
