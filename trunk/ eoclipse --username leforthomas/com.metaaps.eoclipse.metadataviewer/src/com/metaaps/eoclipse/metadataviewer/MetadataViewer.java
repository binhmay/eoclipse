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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ViewPart;

import com.metaaps.eoclipse.common.IModelChangeListener;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.datasets.ITableData;
import com.metaaps.eoclipse.common.views.IViewerImplementation;

public class MetadataViewer extends ViewPart implements IViewerImplementation, IModelChangeListener {
	
	static String ID = "com.metaaps.eoclipse.eoclipsepropertiesviews.metadataviewer";
	private TableViewer m_viewer;
	private IDataContent m_currentdata;
	private Table m_table;

	public MetadataViewer() {
	}

	@Override
	public void createPartControl(Composite parent) {
		
	    FillLayout compositeLayout = new FillLayout();
	    parent.setLayout(compositeLayout);

	    m_table = new Table(parent, SWT.FULL_SELECTION);
	    m_viewer = new TableViewer(m_table);
	    
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
		
	    m_table.removeAll();
	    m_viewer.refresh();

	    if(m_currentdata == null) return;
	    
	    ITableData tabledata = m_currentdata.getTableData();
	    if(tabledata == null) return;
	    
	    TableLayout layout = new TableLayout();
	    for(String columnname : tabledata.getColumnNames()) {
	    	layout.addColumnData(null);
	    }
	    m_table.setLayout(layout);
	    
	    for(String columnname : tabledata.getColumnNames()) {
	    	layout.addColumnData(null);
		    TableColumn nameColumn = new TableColumn(m_table, SWT.LEFT);
		    nameColumn.setText(columnname);
	    }
	    m_table.setHeaderVisible(true);
	    
	    m_viewer.setColumnProperties(tabledata.getColumnNames());
	    m_viewer.setUseHashlookup(true);
	    
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
				if(inputElement instanceof ITableData) {
					return ((ITableData) inputElement).getRowValues(0);
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
				if(element instanceof String) {
					return (String) element;
				} else if(element instanceof Boolean) {
					return ((Boolean) element).toString();
				}
				return null;
			}
	    });
	    
	    m_viewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
			}
		});
	    
	    m_viewer.setCellModifier(new ICellModifier() {
		      public boolean canModify(Object element, String property) {
		    	  return false;
		      }

		      public Object getValue(Object element, String property) {
				return null;
		      }

		      public void modify(Object element, String property, Object value) {
		        m_viewer.refresh();
		      }
		    });

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
        	generateTable();
        }
		
	}

}
