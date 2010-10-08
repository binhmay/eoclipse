package com.metaaps.eoclipse.viewers;

import java.util.ArrayList;
import java.util.List;

import com.metaaps.eoclipse.common.IWorkFlow;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.common.views.IViewerImplementation;
import com.metaaps.eoclipse.common.views.IViewerItem;
import com.metaaps.eoclipse.common.views.IViewers;

public class AllViewers implements IViewers {

	public AllViewers() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IViewerItem findViewer(String viewID) {
		// TODO Auto-generated method stub
		return Viewers.getInstance().findViewer(viewID);
	}

	@Override
	public List<IViewerImplementation> findDataSetsViewers(IDataSets datasets) {
		List<IViewerImplementation> datasetViewers = new ArrayList<IViewerImplementation>(); 
		for(Object obj : Viewers.getInstance().getChildren()) {
			if(obj instanceof IViewerItem) {
				IViewerItem vieweritem = (IViewerItem) obj;
				for(Object viewobj : vieweritem.getChildren()) {
					if(viewobj instanceof IViewerImplementation) {
						IViewerImplementation viewerimp = (IViewerImplementation) viewobj;
						if(viewerimp.getDataSets() == datasets) {
							datasetViewers.add(viewerimp);
						}
						
					}
				}
			}
		}
		return datasetViewers;
	}

}
