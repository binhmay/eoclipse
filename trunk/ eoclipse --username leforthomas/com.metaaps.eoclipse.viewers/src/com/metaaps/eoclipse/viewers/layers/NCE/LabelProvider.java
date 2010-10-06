package com.metaaps.eoclipse.viewers.layers.NCE;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.metaaps.eoclipse.viewers.Viewer;
import com.metaaps.eoclipse.viewers.Viewers;

public class LabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getImage(Object element) {
		if(element instanceof Viewer) {
			return ((Viewer)element).getImageDescriptor().createImage();
		}
		
		return null;
	}

	@Override
	public String getText(Object element) {
		if(element instanceof Viewer) {
			return ((Viewer)element).getLabel();
		}
		
		return null;
	}

}
