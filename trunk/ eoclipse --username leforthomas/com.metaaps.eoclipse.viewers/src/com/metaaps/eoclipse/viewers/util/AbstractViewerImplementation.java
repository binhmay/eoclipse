package com.metaaps.eoclipse.viewers.util;

import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.views.ILayer;
import com.metaaps.eoclipse.common.views.IViewerImplementation;

public abstract class AbstractViewerImplementation extends ViewPart implements
		IViewerImplementation {
	
	protected String m_viewID = "";
	
	protected String m_name;

	protected IDataSets m_datasets;

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setViewid(String viewID) {
		m_viewID = viewID;
	}

	@Override
	public String getViewid() {
		return m_viewID;
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
	public IDataSets getDataSets() {
		return m_datasets;
	}

	@Override
	public void setDataSets(IDataSets datasets) {
		m_datasets = datasets;
	}
	
	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void moveLayer(ILayer layer, boolean up) {
		// TODO Auto-generated method stub
		
	}

}
