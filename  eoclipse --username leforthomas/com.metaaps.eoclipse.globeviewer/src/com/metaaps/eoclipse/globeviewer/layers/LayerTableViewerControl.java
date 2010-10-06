package com.metaaps.eoclipse.globeviewer.layers;

import java.awt.Color;

import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.views.ILayer;
import com.metaaps.eoclipse.globeviewer.GlobeViewerControl;

public class LayerTableViewerControl extends Composite {

	protected static final String VISIBLE = "Visible";
	protected static final String HIDDEN = "Hidden";

	private static final String NAME = "Name";
	private static final String COLOUR = "Colour";
	private static final String VISIBILITY = "Visibility";

	  private TableViewer m_viewer;
	private GlobeViewerControl m_control;

	  public LayerTableViewerControl(Composite parent) {
	    super(parent, SWT.NULL);

	    FillLayout compositeLayout = new FillLayout();
	    setLayout(compositeLayout);

	    final Table table = new Table(this, SWT.FULL_SELECTION);
	    m_viewer = new TableViewer(table);

	    TableLayout layout = new TableLayout();
	    layout.addColumnData(new ColumnWeightData(70, 75, false));
	    layout.addColumnData(new ColumnWeightData(15, 75, false));
	    layout.addColumnData(new ColumnWeightData(15, 75, false));
	    table.setLayout(layout);

	    TableColumn nameColumn = new TableColumn(table, SWT.LEFT);
	    nameColumn.setText("Layer Name");
	    TableColumn colColumn = new TableColumn(table, SWT.LEFT);
	    colColumn.setText("Colour");
	    TableColumn visColumn = new TableColumn(table, SWT.LEFT);
	    visColumn.setText("Visible");
	    table.setHeaderVisible(false);
	    
	    m_viewer.setColumnProperties(new String[] {NAME, COLOUR, VISIBILITY});
	    m_viewer.setUseHashlookup(true);
	    
	    m_viewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				GlobeViewerLayer layer = (GlobeViewerLayer) (((IStructuredSelection) event.getSelection()).getFirstElement());
//				m_control.flyTo(layer.getCenter());
			}
		});
	    
	    m_viewer.setContentProvider(new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void dispose() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Object[] getElements(Object inputElement) {
				if(inputElement instanceof LayerList) {
					return ((LayerList) inputElement).getLayersByClass(GlobeViewerLayer.class).toArray();
				}
				return null;
			}
		});

	    m_viewer.setLabelProvider(new ITableLabelProvider() {

			@Override
			public void addListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getColumnText(Object element, int columnIndex) {
				if(element instanceof ILayer) {
					GlobeViewerLayer layer = ((GlobeViewerLayer) element);
					switch(columnIndex) {
						case 0: {
							return layer.getName();
						}
						case 1: {
							return "#" + Integer.toHexString(((GlobeViewerLayer) element).getLayerColor().hashCode()).toUpperCase();
						}
						case 2: {
							return (layer.isEnabled() ? VISIBLE : HIDDEN);
						}
						default:
							break;
					}
				}
				return null;
			}
	    });
	    
	    m_viewer.setCellModifier(new ICellModifier() {
		      public boolean canModify(Object element, String property) {
		    	  if(property.contentEquals(VISIBILITY) || property.contentEquals(COLOUR)) {
		    		  return true;
		    	  }
		    	  return false;
		      }

		      public Object getValue(Object element, String property) {
				if (property.contentEquals(VISIBILITY)) {
					return ((RenderableLayer) element).isEnabled() ? 0 : 1;
				}
				if (property.contentEquals(COLOUR)) {
					return "#" + Integer.toHexString(((GlobeViewerLayer) element).getLayerColor().hashCode()).toUpperCase();
				}
				return null;
		      }

		      public void modify(Object element, String property, Object value) {
		        TableItem tableItem = (TableItem) element;
		        RenderableLayer layer = (RenderableLayer) tableItem.getData();
		        if (property.contentEquals(VISIBILITY)) {
	        	  layer.setEnabled(((Integer) value).intValue() == 0);
		        }
				if (property.contentEquals(COLOUR)) {
					try {
						((GlobeViewerLayer) layer).setLayerColor(new Color(Integer.decode((String) value), true));
					} catch(Exception e) {
						Util.errorMessage(e.getMessage());
					}
				}

		        m_viewer.refresh(layer);
		      }
		    });

		    m_viewer.setCellEditors(new CellEditor[] { new TextCellEditor(table), new TextCellEditor(table),
		        new ComboBoxCellEditor(table, new String[]{VISIBLE, HIDDEN}, SWT.READ_ONLY) });

	  }

		private void setLayers(LayerList layers) {
		    m_viewer.setInput(layers);
		    m_viewer.refresh();
		}

		public void setGlobeViewerControl(GlobeViewerControl control) {
			m_control = control;
			setLayers(control.getLayers());
		}

	}
