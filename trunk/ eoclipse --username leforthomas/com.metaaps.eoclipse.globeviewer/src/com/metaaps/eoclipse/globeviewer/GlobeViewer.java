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
import com.metaaps.eoclipse.common.views.IViewerImplementation;
import com.metaaps.eoclipse.globeviewer.layers.LayerTableViewerControl;

public class GlobeViewer extends ViewPart implements IViewerImplementation, IModelChangeListener {

	public static String ID = "com.metaaps.eoclipse.eoclipsegisviews.globeviewer";
	private GlobeViewerControl m_globeviewercontrol;
	private LayerTableViewerControl m_layertableviewer;
	private IDataSets m_datasets;
	private String m_name = "Globe Viewer";

	public GlobeViewer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		
//        SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
//        sashForm.setSize(10, 50);
		m_globeviewercontrol = new GlobeViewerControl(parent); //sashForm);

//		m_layertableviewer = new LayerTableViewerControl(sashForm);
		
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
	}
	
	private void removeDataLayer(IDataContent datacontent) throws ParseException {
		m_globeviewercontrol.removeLayer(datacontent);
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
		// update table too
//		m_layertableviewer.update();
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		// TODO Auto-generated method stub
		
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
	}

}
