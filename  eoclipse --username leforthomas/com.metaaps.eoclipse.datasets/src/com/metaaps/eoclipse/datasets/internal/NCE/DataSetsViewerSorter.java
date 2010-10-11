package com.metaaps.eoclipse.datasets.internal.NCE;

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.datasets.importmethods.ImportFolder;

public class DataSetsViewerSorter extends ViewerSorter {

	public DataSetsViewerSorter() {
		// TODO Auto-generated constructor stub
	}

	public DataSetsViewerSorter(Collator collator) {
		super(collator);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if((e1 instanceof IDataContent) && (e2 instanceof IDataContent)) {
			return 0;
		}
		if((e1 instanceof IDataContent) && !(e2 instanceof IDataContent)) {
			return -1;
		}
		return super.compare(viewer, e1, e2);
	}

}
