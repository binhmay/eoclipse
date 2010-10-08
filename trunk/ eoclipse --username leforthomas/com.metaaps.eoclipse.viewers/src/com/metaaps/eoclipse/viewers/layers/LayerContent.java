package com.metaaps.eoclipse.viewers.layers;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;

import com.metaaps.eoclipse.viewers.Viewers;
import com.metaaps.eoclipse.workflowmanager.WorkFlowManager;

public class LayerContent extends CommonNavigator {
	
	static public String layerViewID = "com.metaaps.eoclipse.viewers.sidepanel.layers";
	
	public LayerContent() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void createPartControl(Composite aParent) {
		// TODO Auto-generated method stub
		super.createPartControl(aParent);

	}

	@Override
	protected Object getInitialInput() {
		return Viewers.getInstance();
	}

}
