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

import gov.nasa.worldwind.avlist.AVKey;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;


import com.metaaps.eoclipse.common.Attributes;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.datasets.IGeoRaster;
import com.metaaps.eoclipse.common.datasets.IVectorData;
import com.metaaps.eoclipse.common.datasets.VectorData;
import com.metaaps.eoclipse.common.views.IGISControl;
import com.metaaps.eoclipse.common.views.ILayer;
import com.metaaps.eoclipse.genericprocessing.ImageFrame;
import com.metaaps.eoclipse.globeviewer.layers.GlobeViewerLayer;
import com.metaaps.eoclipse.globeviewer.util.PlaceMark;
import com.vividsolutions.jts.geom.Geometry;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.render.Renderable;

/**
 * @author leforthomas
 *
 * Provides a 3D map control based on World Wind Java to be inserted in a window layout 
 * 
 */
public class GlobeViewerControl implements IGISControl {
	
	private Frame worldFrame;
	WorldWindowGLCanvas world = new WorldWindowGLCanvas();

	public GlobeViewerControl(Composite parent) {
		Composite container = new Composite(parent, SWT.EMBEDDED);
		//parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		// Swing Frame and Panel
		worldFrame = SWT_AWT.new_Frame(container);
		worldFrame.setLayout(new java.awt.BorderLayout());

		// Add the WWJ 3D OpenGL Canvas to the Swing Frame
		Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
		world.setModel(m);
		worldFrame.add(world, BorderLayout.CENTER);
		
		// add listener
		world.addSelectListener(new SelectListener() {
			
			@Override
			public void selected(SelectEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	@Override
	public ILayer addLayer(IDataContent datacontent) {
        GlobeViewerLayer layer = new GlobeViewerLayer(datacontent);
        layer.setName(datacontent.getName());
        layer.setPickEnabled(true);

		if(datacontent instanceof IGeoRaster) {
			VectorData frame;
			try {
				frame = ImageFrame.frame((IGeoRaster) datacontent);
		        for (Geometry geom : frame.getGeometries()) {
		        	PlaceMark item = PlaceMark.create("", geom, Color.BLUE);
					layer.addRenderable(item);
		        }
			} catch (com.vividsolutions.jts.io.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(datacontent instanceof IVectorData) {
			VectorData vectordata = ((IVectorData) datacontent).getVectorData("");
	        List<Renderable> renderables = new Vector<Renderable>();
	        if(vectordata.getGeometries() == null) {
	        	Util.errorMessage("Cannot add Empty Layer '" + datacontent.getName() + "'");
	        }
	        for (Geometry geom : vectordata.getGeometries()) {
	            Attributes att = vectordata.getAttributes(geom);
	            renderables.add(PlaceMark.create(att.toString(), geom, Color.CYAN));
	        }
	        layer.addRenderables(renderables);
		}
		world.getModel().getLayers().add(layer);
		
		return null;
	}
	
	@Override
	public void removeLayer(IDataContent datacontent) {
		// look through layers to find the one corresponding to the datacontent
		for(Layer layer : world.getModel().getLayers()) {
			if(layer instanceof GlobeViewerLayer) {
				if(((GlobeViewerLayer) layer).represents(datacontent)) {
					world.getModel().getLayers().remove(layer);
				}
			}
		}
	}
	
	public LayerList getLayers() {
		return world.getModel().getLayers();
	}
	
	public void flyTo (Position latlon) 
	{
	   View view       = world.getView();
	   Globe globe = world.getModel().getGlobe();
	   double elevation = view.getCurrentEyePosition().elevation;
	   view.goTo(new Position(latlon.latitude, latlon.longitude, elevation), elevation);
	   
//	   view.applyStateIterator(FlyToOrbitViewAnimator.createFlyToOrbitViewAnimator(
//			   (OrbitView)view
//	           , world.getCurrentPosition()
//	           , latlon      // bbox
//	           , Angle.ZERO   // Heading
//	           , Angle.ZERO   // Pitch
//	           , Angle.ZERO   // Heading
//	           , Angle.ZERO   // Pitch
//	           , 1.0		  // current zoom
//	           , 1.0		  // final zoom
//	           , 1000		  // time to move
//	           , true )       // end on surface
//	           );
	}
	
}
