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
package com.metaaps.eoclipse.imageviewer;

import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import javax.media.opengl.GL;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.glu.GLU;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.metaaps.eoclipse.common.IModelChangeListener;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IGeoRaster;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.datasets.IVectorData;
import com.metaaps.eoclipse.common.views.IViewerImplementation;
import com.metaaps.eoclipse.imageviewer.api.GeoContext;
import com.metaaps.eoclipse.imageviewer.api.GeometricLayer;
import com.metaaps.eoclipse.imageviewer.api.IImageLayer;
import com.metaaps.eoclipse.imageviewer.api.ILayer;
import com.metaaps.eoclipse.imageviewer.api.ILayerManager;
import com.metaaps.eoclipse.imageviewer.api.ILayerUser;
import com.metaaps.eoclipse.imageviewer.layers.FastImageLayer;
import com.metaaps.eoclipse.imageviewer.layers.LayerManager;
import com.metaaps.eoclipse.imageviewer.layers.SimpleVectorLayer;
import com.metaaps.eoclipse.imageviewer.utils.GeoUtils;
import com.metaaps.eoclipse.imageviewer.api.ILayerListener;
import com.metaaps.eoclipse.viewers.util.AbstractViewerImplementation;
import com.vividsolutions.jts.io.ParseException;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ImageViewer extends AbstractViewerImplementation implements ILayerUser, IViewerImplementation, IModelChangeListener {

	private GLCanvas canvas;
	private LayerManager root = new LayerManager(null);
	private GeoContext geocontext=new GeoContext();
	protected int dyy;
	protected int dxx;
	private ArrayList<ILayerListener> listeners=new ArrayList<ILayerListener>();
	
	public ImageViewer() {
		m_name = "Image Viewer";
	}
	
	public void createPartControl(final Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		top.setLayout(new FillLayout());
		GLData data = new GLData();
		data.doubleBuffer = true;
		canvas = new GLCanvas(top, SWT.NONE, data);
		canvas.setCurrent();
		final GLContext context = GLDrawableFactory.getFactory().createExternalGLContext();
		context.makeCurrent();
		GL gl = context.getGL();
		geocontext.initialize(context);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		context.release();
		
		canvas.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				Rectangle bounds = canvas.getBounds();
				canvas.setCurrent();
				context.makeCurrent();
				GL gl = context.getGL ();
				geocontext.setHeight(bounds.height);
				geocontext.setWidth(bounds.width);
				gl.glViewport(0, 0, bounds.width, bounds.height);
		        gl.glMatrixMode(GL.GL_PROJECTION);
		        gl.glLoadIdentity();
		        new GLU().gluOrtho2D(0, 1, 0, 1);
		        gl.glMatrixMode(GL.GL_MODELVIEW);
		        gl.glLoadIdentity();
				context.release();
			}
		});
		
		canvas.addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent e) {
                if (e.stateMask==SWT.SHIFT) {
                    dxx = (int) (geocontext.getZoom() * (e.x - canvas.getBounds().width / 2) / 10);
                    dyy = (int) (geocontext.getZoom() * (e.y - canvas.getBounds().height / 2) / 10);
                    geocontext.setDirty(true);
                } else {
                    Point p = new Point();
                    try {
                        p.x = (int) (geocontext.getX() + e.x * geocontext.getWidth() / canvas.getBounds().width * geocontext.getZoom());
                        p.y = (int) (geocontext.getY() + e.y * geocontext.getHeight() / canvas.getBounds().height * geocontext.getZoom());
                        root.mouseMoved(p, geocontext);
                    } catch (Exception ex) {
                    }
                }
            }

            
        });

		
		 canvas.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseScrolled(MouseEvent e) {
			
	                float zoom = (float) (geocontext.getZoom() * Math.pow(2, -e.count / 20.0));
	                
	                // check mouse position
	                int posX = e.x;
	                int posY = e.y;
	                // calculate the image position of the current position of the mouse
	                int x = (int) (geocontext.getX() + posX * geocontext.getWidth() / canvas.getBounds().width * geocontext.getZoom());
	                int y = (int) (geocontext.getY() + posY * geocontext.getHeight() / canvas.getBounds().height * geocontext.getZoom());
	                geocontext.setZoom(zoom);
	                // translate the image origin to have the same mouse position in the geocontext
	                geocontext.setX((int) (x - posX * geocontext.getWidth() / canvas.getBounds().width * geocontext.getZoom()));
	                geocontext.setY((int) (y - posY * geocontext.getHeight() / canvas.getBounds().height * geocontext.getZoom()));
	                geocontext.setDirty(true);
	            }
	        });
		
		final Runnable core=new Runnable() {
			int count=0;
			long[] times=new long[500];
			public void run() {
				if (!canvas.isDisposed()) {
					if(count==times.length){
						count=0;
						double fps=0;
						for(int i=0;i<times.length-1;i++){
							fps+=(times[i+1]-times[i])/1000.;
						}
						fps/=times.length-1;
						System.out.println(1/fps);
					}else{
						times[count++]=System.currentTimeMillis();
					}
					canvas.setCurrent();
					context.makeCurrent();
					GL gl = context.getGL();
					gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
					if (dxx != 0 || dyy != 0) {
						dxx /= 1.2;
                        dyy /= 1.2;
			            geocontext.setX(geocontext.getX() + dxx);
			            geocontext.setY(geocontext.getY() + dyy);
			        }
					geocontext.initialize(context);
					geocontext.setDirty(true);
					root.render(geocontext);
					canvas.swapBuffers();
					context.release();
					try {
						Thread.sleep(15);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					canvas.getDisplay().asyncExec(this);
				}
			}
		};
		canvas.getDisplay().asyncExec(core);
		
	}
	
	public ILayerManager getRootLayer(){
		return root;
	}
	
	
	@Override
	public void setFocus() {
		canvas.setFocus();
	}
	
	@Override
	public void dispose() {
		canvas.dispose();
		if(m_datasets != null) {
			m_datasets.removeListener(this);
		}
		super.dispose();
	}

	@Override
	public void addListenner(ILayerListener l) {
		this.root.addListenner(l);
	}

	@Override
	public void removeListenner(ILayerListener l) {
		this.root.removeListenner(l);
	}

	@Override
	public void setDataSets(IDataSets datasets) {
		m_datasets = datasets;
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
		datasets.addListener(this);
	}
	
	public void addDataLayer(IDataContent datacontent) throws ParseException {
		IImageLayer iil = null;
		if(datacontent instanceof IGeoRaster) {
			iil = new FastImageLayer(root, (IGeoRaster)datacontent);
			root.addLayer(iil);
			root.render(geocontext);
		}
		if(datacontent instanceof IVectorData) {
			IVectorData vectordata = (IVectorData) datacontent;
			for(ILayer layer : root.getLayers()) {
				if(layer.isActive()) {
					iil = (IImageLayer) layer;
				}
			}
			if(iil != null) {
				IGeoRaster gir = iil.getImage();
				GeometricLayer layer = GeoUtils.createImageProjectedLayer(vectordata, gir, "EPSG:4326");
				iil.addLayer(new SimpleVectorLayer(vectordata.getName(), iil, vectordata, layer));
			}
		}
	}
	
	public void removeDataLayer(IDataContent datacontent) throws ParseException {
		IImageLayer iil = null;
		for(ILayer layer : root.getLayers()) {
			if(layer.isActive()) {
				iil = (IImageLayer) layer;
				if(iil.represents(datacontent)) {
					root.removeLayer(iil);
				}
			}
		}
		for(ILayer layer : iil.getLayers()) {
			// need to find a way to keep track of data content used for layers
			if(layer.represents(datacontent)) {
				iil.removeLayer(layer);
			}
		}
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
	public void selectionChanged(SelectionChangedEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<com.metaaps.eoclipse.common.views.ILayer> getLayers() {
		ArrayList<com.metaaps.eoclipse.common.views.ILayer> layers = new ArrayList<com.metaaps.eoclipse.common.views.ILayer>();
		layers.add(root);
		for(ILayer layer : root.getLayers()) {
			layers.add((com.metaaps.eoclipse.common.views.ILayer)layer);
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

	@Override
	public void refresh() {
		geocontext.setDirty(true);
		root.render(geocontext);
	}
	
	@Override
	public void moveLayer(com.metaaps.eoclipse.common.views.ILayer layer, boolean up) {
		List<ILayer> layers = root.getLayers();
		layers.lastIndexOf(layer);
		int curindex = layers.lastIndexOf(layer);
		if(curindex > 0) {
			layers.remove(curindex);
			if(curindex + 1 < layers.size()) {
				layers.add(curindex + 1, (ILayer) layer);
			} else {
				layers.add((ILayer) layer);
			}
		}
	}
	
}
