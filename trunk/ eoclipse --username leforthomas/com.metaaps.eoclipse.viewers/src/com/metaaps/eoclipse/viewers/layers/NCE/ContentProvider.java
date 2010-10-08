package com.metaaps.eoclipse.viewers.layers.NCE;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.metaaps.eoclipse.common.ModelChangeListener;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.views.ILayer;
import com.metaaps.eoclipse.common.views.IViewerImplementation;
import com.metaaps.eoclipse.common.views.IViewerItem;
import com.metaaps.eoclipse.viewers.Viewers;
import com.metaaps.eoclipse.workflowmanager.WorkFlow;
import com.metaaps.eoclipse.workflowmanager.WorkFlowManager;

public class ContentProvider extends ModelChangeListener {

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof Viewers)
		{
			// display only the ones which are opened
			ArrayList<IViewerItem> vieweritems = new ArrayList<IViewerItem>();
			for(Object obj : ((Viewers)parentElement).getChildren()) {
				if(obj instanceof IViewerItem) {
					IViewerItem vieweritem = (IViewerItem) obj;
					if((vieweritem.getChildren() != null) && (vieweritem.getChildren().length > 0)) {
						vieweritems.add(vieweritem);
					}
				}
			}
			return vieweritems.toArray();
		} else if(parentElement instanceof IViewerItem)
		{
			return ((IViewerItem) parentElement).getChildren();
		} else if(parentElement instanceof IViewerImplementation)
		{
			return ((IViewerImplementation) parentElement).getLayers().toArray();
		} else if(parentElement instanceof ILayer)
		{
			return ((ILayer) parentElement).getLayerProperties();
		}
		
		return EMPTY_ARRAY;
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

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
