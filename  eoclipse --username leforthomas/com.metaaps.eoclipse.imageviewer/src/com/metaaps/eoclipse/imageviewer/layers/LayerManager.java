/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metaaps.eoclipse.imageviewer.layers;

import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.metaaps.eoclipse.common.Property;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.imageviewer.api.GeoContext;
import com.metaaps.eoclipse.imageviewer.api.IClickable;
import com.metaaps.eoclipse.imageviewer.api.IImageLayer;
import com.metaaps.eoclipse.imageviewer.api.IKeyPressed;
import com.metaaps.eoclipse.imageviewer.api.ILayer;
import com.metaaps.eoclipse.imageviewer.api.ILayerListener;
import com.metaaps.eoclipse.imageviewer.api.ILayerManager;
import com.metaaps.eoclipse.imageviewer.api.ILayerUser;
import com.metaaps.eoclipse.imageviewer.api.IMouseDrag;
import com.metaaps.eoclipse.imageviewer.api.IMouseMove;
import com.vividsolutions.jts.geom.Coordinate;

/**
 *
 * @author thoorfr
 */
public class LayerManager implements ILayerManager, IClickable, IMouseMove, IMouseDrag, IKeyPressed, ILayerUser {

    private boolean active = true;
    ArrayList<ILayerListener> listeners = new ArrayList<ILayerListener>();
    protected List<ILayer> layers = new Vector<ILayer>();
    private String name = "";
    private ILayerManager parent;
    protected String description = "Layer Manager";
    protected Vector<ILayer> remove = new Vector<ILayer>();
    protected Vector<ILayer> add = new Vector<ILayer>();

    public LayerManager(ILayerManager parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
    	return name;
    }

    public void render(final GeoContext context) {
        if (this.add.size() > 0) {
            layers.addAll(add);
            for (ILayer l : add) {
                for (ILayerListener ll : listeners) {
                    ll.layerAdded(l);
                }
            }
            add.clear();
        }
        if (this.remove.size() > 0) {
            layers.removeAll(remove);
            for (ILayer l : remove) {
                for (ILayerListener ll : listeners) {
                    ll.layerRemoved(l);
                }
            }
            remove.clear();
        }
        for (ILayer l : layers) {
            if (l.isActive()) {
                try {
                    l.render(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        for (ILayerListener l : listeners) {
            l.layerClicked(this);
        }
    }

    public ILayerManager getParent() {
        return parent;
    }

    public String getDescription() {
        return description;
    }

    public void mouseClicked(Point imagePosition, int button, GeoContext context) {
        for (ILayer l : layers) {
            if (l.isActive()) {
                if (l instanceof IClickable) {
                    ((IClickable) l).mouseClicked(imagePosition, button, context);
                }
            }
        }
    }

    public void dispose() {
        active = false;
        for (ILayer l : layers) {
            l.dispose();
        }
        layers.clear();
    }

    public void mouseMoved(Point imagePosition, GeoContext context) {
        for (ILayer l : layers) {
            if (l.isActive()) {
                if (l instanceof IMouseMove) {
                    ((IMouseMove) l).mouseMoved(imagePosition, context);
                }
            }
        }
    }

    public void addLayer(ILayer layer) {
        // if we are adding an image layer turn off all the other image active layers
        if (layer instanceof IImageLayer) {
            // look for other image layers active
            for (ILayer il : layers) {
                if (il instanceof IImageLayer) {
                    if (il.isActive()) {
                        il.setActive(false);
                    }
                }
            }
        }
        // now add layer
        this.add.add(layer);
    }

    public void removeLayer(ILayer layer) {
        layer.setActive(false);
        this.remove.add(layer);
    }

    public List<ILayer> getLayers() {
        return new ArrayList<ILayer>(layers);
    }

    public void mouseDragged(Point initPosition, Point imagePosition, int button, GeoContext context) {
        for (ILayer l : layers) {
            if (l.isActive()) {
                if (l instanceof IMouseDrag) {
                    ((IMouseDrag) l).mouseDragged(initPosition, imagePosition, button, context);
                }
            }
        }
    }

    public void keyPressed(KeyEvent evt) {
        for (ILayer l : layers) {
            if (l.isActive()) {
                if (l instanceof IKeyPressed) {
                    ((IKeyPressed) l).keyPressed(evt);
                }
            }
        }
    }

    public void addListenner(ILayerListener l) {
        this.listeners.add(l);
        for (ILayer ll : layers) {
            if (ll instanceof ILayerUser) {
                ((ILayerUser) ll).addListenner(l);
            }
        }
    }

    public void removeListenner(ILayerListener l) {
        this.listeners.remove(l);
        for (ILayer ll : layers) {
            if (ll instanceof ILayerUser) {
                ((ILayerUser) ll).removeListenner(l);
            }
        }
    }

	@Override
	public boolean represents(IDataContent datacontent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Coordinate getCenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getLayerProperty(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLayerProperty(String key, Object obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Property[] getLayerProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void selectionChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selectionDoubleClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}
}
