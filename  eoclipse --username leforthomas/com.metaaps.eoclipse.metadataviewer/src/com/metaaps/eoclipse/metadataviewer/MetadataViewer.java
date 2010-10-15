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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
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
import org.eclipse.swt.layout.GridData;
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
import com.metaaps.eoclipse.workflowmanager.WorkFlowManager;

/**
 * @author leforthomas
 *
 * The actual view implementation
 * Displays all returned meta data as a table
 * 
 */
public class MetadataViewer extends ViewPart implements ISelectionChangedListener, IModelChangeListener {
	
	private TableViewer m_tableviewer;
	private IDataContent m_currentdata;
	private Label m_selectlabel;
	private Composite m_parent;

	@Override
	public void createPartControl(Composite parent) {
		
	    GridLayout compositeLayout = new GridLayout(1, true);
	    parent.setLayout(compositeLayout);
	    
	    m_parent = parent;
	    
	    m_selectlabel = new Label(m_parent, SWT.NULL);
		m_selectlabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    m_tableviewer = new TableViewer(m_parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
	    
		Table table = m_tableviewer.getTable();
	    table.setVisible(false);
	    
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		//gridData.horizontalSpan = 3;
		table.setLayoutData(gridData);	
	    
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
				if(inputElement instanceof ITableData) {
					return ((ITableData) inputElement).getAllRows();
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
			public String getColumnText(Object elements, int columnIndex) {
				Object[] fields = (Object[]) elements;
				if(columnIndex < fields.length) {
					Object element = fields[columnIndex];
					if(element instanceof String) {
						return (String) element;
					} else if(element instanceof Boolean) {
						return ((Boolean) element).toString();
					}
					return element.toString();
				}
				return "undefined";
			}
	    });
	    
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

	private void generateTable() {
		
		Table table = m_tableviewer.getTable();
		table.setVisible(false);
		
	    if(m_currentdata == null) {
		    m_selectlabel.setText("No Data Element Selected, select an element first");
		    m_selectlabel.redraw();
		    m_parent.redraw();
	    	return;
	    }
	    
	    ITableData tabledata = m_currentdata.getTableData();
	    if(tabledata == null) {
		    m_selectlabel.setText("Element Selected '" + m_currentdata.getLabel() + "' does not provide metadata.");
		    m_selectlabel.redraw();
		    m_parent.redraw();
	    	return;
	    }
	    
	    m_selectlabel.setText("Element '" + m_currentdata.getLabel() + "' meta data table:");
	    
	    // remove previous table columns
	    ArrayList<TableColumn> columns = new ArrayList<TableColumn>(Arrays.asList(table.getColumns()));
	    for(TableColumn column : columns) {
	    	column.dispose();
	    }
	    
	    for(String columnname : tabledata.getColumnNames()) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(columnname);
			column.setWidth(100);
			column.setResizable(true);
			column.setMoveable(true);
	    }
	    
	    table.setVisible(true);
	    
	    m_tableviewer.setInput(tabledata);
	    
	    m_selectlabel.update();
	    table.update();
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

}
