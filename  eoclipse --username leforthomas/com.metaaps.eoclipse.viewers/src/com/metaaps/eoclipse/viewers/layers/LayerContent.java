package com.metaaps.eoclipse.viewers.layers;

import org.eclipse.ui.navigator.CommonNavigator;

import com.metaaps.eoclipse.viewers.Viewers;
import com.metaaps.eoclipse.workflowmanager.WorkFlowManager;

public class LayerContent extends CommonNavigator {
	
	static public String layerViewID = "com.metaaps.eoclipse.viewers.sidepanel.layers";

	public LayerContent() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected Object getInitialInput() {
		return Viewers.getInstance();
	}

}
