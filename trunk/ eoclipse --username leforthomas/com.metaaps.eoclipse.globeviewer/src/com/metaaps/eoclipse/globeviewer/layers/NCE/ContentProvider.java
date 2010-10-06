package com.metaaps.eoclipse.globeviewer.layers.NCE;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.views.IViewerItem;
import com.metaaps.eoclipse.globeviewer.GlobeViewer;

public class ContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IViewerItem)
		{
			IViewerItem vieweritem = (IViewerItem) parentElement;
			if(vieweritem.getViewID() == GlobeViewer.ID) {
				return new Object[]{};
			}
			return new Object[]{};
		}
		return new Object[]{};
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		return getChildren(element).length > 0;
	}

}
