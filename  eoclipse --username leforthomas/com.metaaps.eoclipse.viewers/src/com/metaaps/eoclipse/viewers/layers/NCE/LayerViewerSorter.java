package com.metaaps.eoclipse.viewers.layers.NCE;

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.metaaps.eoclipse.common.views.ILayer;

public class LayerViewerSorter extends ViewerSorter {

	public LayerViewerSorter() {
		// TODO Auto-generated constructor stub
	}

	public LayerViewerSorter(Collator collator) {
		super(collator);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if((e1 instanceof ILayer) && (e2 instanceof ILayer)) {
			// assumes list is already sorted
			return 0;
		}
		return super.compare(viewer, e1, e2);
	}

}
