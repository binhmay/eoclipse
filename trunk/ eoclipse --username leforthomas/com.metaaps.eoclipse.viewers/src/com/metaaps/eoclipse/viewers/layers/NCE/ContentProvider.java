package com.metaaps.eoclipse.viewers.layers.NCE;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.metaaps.eoclipse.common.ModelChangeListener;
import com.metaaps.eoclipse.common.datasets.IDataSets;
import com.metaaps.eoclipse.viewers.Viewers;

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
			return ((Viewers)parentElement).getChildren();
		} else if(parentElement instanceof Viewer)
		{
			return new Object[]{};
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
