package com.metaaps.eoclipse.viewers;

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

}
