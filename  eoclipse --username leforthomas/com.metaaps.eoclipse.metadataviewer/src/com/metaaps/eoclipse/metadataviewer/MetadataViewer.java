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
package com.metaaps.eoclipse.metadataviewer;

import java.util.List;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ViewPart;

import com.metaaps.eoclipse.common.IModelChangeListener;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.datasets.ITableData;
import com.metaaps.eoclipse.common.views.ILayer;
import com.metaaps.eoclipse.common.views.IViewerImplementation;
import com.metaaps.eoclipse.viewers.util.AbstractViewerImplementation;

public class MetadataViewer extends AbstractViewerImplementation implements IViewerImplementation, IModelChangeListener {
	
	private TableViewer m_tableviewer;
	private IDataContent m_currentdata;
	private Label m_selectlabel;
	private Composite m_parent;

	public MetadataViewer() {
		m_name = "Meta Data Viewer";
	}

	@Override
	public void createPartControl(Composite parent) {
		
	    GridLayout compositeLayout = new GridLayout(1, true);
	    parent.setLayout(compositeLayout);
	    
	    m_parent = parent;
	    
	    generateTable();
	    
//	    m_tableviewer.addDoubleClickListener(new IDoubleClickListener() {
//			
//			@Override
//			public void doubleClick(DoubleClickEvent event) {
//			}
//		});
//	    
//	    m_tableviewer.setCellModifier(new ICellModifier() {
//		      public boolean canModify(Object element, String property) {
//		    	  return false;
//		      }
//
//		      public Object getValue(Object element, String property) {
//				return null;
//		      }
//
//		      public void modify(Object element, String property, Object value) {
//		        m_tableviewer.refresh();
//		      }
//		    });

	    generateTable();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDataSets(IDataSets datasets) {
		generateTable();
	}

	private void generateTable() {
		
		if(m_tableviewer != null) {
			m_tableviewer.getTable().dispose();
		}
		if(m_selectlabel != null) {
			m_selectlabel.dispose();
		}
		
	    if(m_currentdata == null) {
		    m_selectlabel = new Label(m_parent, SWT.FULL_SELECTION);
		    m_selectlabel.setText("No Data Element Selected, select an element first");
		    m_parent.redraw();
	    	return;
	    }
	    
	    ITableData tabledata = m_currentdata.getTableData();
	    if(tabledata == null) {
		    m_selectlabel = new Label(m_parent, SWT.FULL_SELECTION);
		    m_selectlabel.setText("Element Selected '" + m_currentdata.getLabel() + "' does not provide metadata.");
		    m_selectlabel.redraw();
		    m_parent.redraw();
	    	return;
	    }
	    
	    m_tableviewer = new TableViewer(m_parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
	    
		Table table = m_tableviewer.getTable();
	    table.setVisible(true);
	    
	    for(String columnname : tabledata.getColumnNames()) {
			TableViewerColumn column = new TableViewerColumn(m_tableviewer, SWT.NONE);
			column.getColumn().setText(columnname);
			column.getColumn().setWidth(100);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
	    }
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);
	    
	    m_tableviewer.setContentProvider(new IStructuredContentProvider() {
			
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
				if(inputElement instanceof IDataContent) {
					return new Object[]{((IDataContent) inputElement).getTableData()};
				} else if(inputElement instanceof ITableData) {
					return new Object[]{((ITableData) inputElement).getRowValues(0)};
				}
				return null;
			}
		});

	    m_tableviewer.setLabelProvider(new ITableLabelProvider() {

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
				if(element instanceof String) {
					return (String) element;
				} else if(element instanceof Boolean) {
					return ((Boolean) element).toString();
				}
				return element.toString();
			}
	    });
	    
	    m_tableviewer.setInput(m_currentdata);
	    
	    m_tableviewer.getTable().redraw();
	    m_parent.redraw();
	    
//		    m_viewer.setCellEditors(
//		    		new CellEditor[] { new TextCellEditor(table), new TextCellEditor(table),
//		    					new ComboBoxCellEditor(table, new String[]{VISIBLE, HIDDEN}, SWT.READ_ONLY) }
//		    		);

	}

	@Override
	public void modelChanged(Object element, String event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
        IStructuredSelection currentSelection = (IStructuredSelection) event.getSelection();
        Object obj = currentSelection.getFirstElement();
        if(obj instanceof IDataContent)
        {
        	m_currentdata = (IDataContent) obj;
        } else {
        	m_currentdata = null;
        }
    	generateTable();
	}

	@Override
	public List<ILayer> getLayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return m_name;
	}

	@Override
	public void setName(String name) {
		m_name  = name;
	}

}
