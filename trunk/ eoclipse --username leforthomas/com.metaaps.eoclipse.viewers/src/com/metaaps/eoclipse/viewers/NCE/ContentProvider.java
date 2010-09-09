package com.metaaps.eoclipse.viewers.NCE;

import com.metaaps.eoclipse.common.Folder;
import com.metaaps.eoclipse.common.IDataSets;
import com.metaaps.eoclipse.common.IModel;
import com.metaaps.eoclipse.common.IWorkFlow;
import com.metaaps.eoclipse.common.ModelChangeListener;
import com.metaaps.eoclipse.common.Util;
import com.metaaps.eoclipse.viewers.ViewFolder;

/**
 * @author leforthomas
 */
public class ContentProvider extends ModelChangeListener {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof ViewFolder)
		{
			return ((ViewFolder)parentElement).getChildren();
		} else if(parentElement instanceof IDataSets)
		{
			// do not attach the ViewFolder to the workflow structure
			return new Object[]{ViewFolder.getInstance()};
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

}

