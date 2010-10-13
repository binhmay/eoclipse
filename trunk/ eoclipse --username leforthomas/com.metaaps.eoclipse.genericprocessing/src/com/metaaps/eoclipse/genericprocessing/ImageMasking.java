package com.metaaps.eoclipse.genericprocessing;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;

import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.datasets.IGeoRaster;
import com.metaaps.eoclipse.common.datasets.IVectorData;
import com.metaaps.eoclipse.genericprocessing.util.RasterizeVector;
import com.metaaps.eoclipse.processing.AbstractProcessing;
import com.vividsolutions.jts.geom.Coordinate;

public class ImageMasking extends AbstractProcessing {

	@Override
	public IDataContent execute(HashMap<String, Object> parametervalues) {
		
		// get parameters
		final IGeoRaster source = (IGeoRaster) parametervalues.get("Source");
		IVectorData mask = (IVectorData) parametervalues.get("Mask");
		Double buffering = (Double) parametervalues.get("Buffering");
		Integer pixelvalue = (Integer) parametervalues.get("Pixel Value");
		
		IDataContent result = ImageMasking.imagemasking(source, mask, buffering.doubleValue(), pixelvalue.intValue(), m_monitor);
		
		return result;
	}

	public static IDataContent imagemasking(IGeoRaster source, IVectorData mask, double doubleValue, int intValue, IProgressMonitor m_monitor) {
		IDataContent result = (IDataContent) source.clone();
		result.setName("Masked Raster " + source.getName());
		IGeoRaster raster = (IGeoRaster) result;
		int width = raster.getWidth();
		int height = raster.getHeight();
		int increment = 10;
		RasterizeVector maskvector = new RasterizeVector(mask);
		for(int row = 0; row < height; row+= increment) {
			raster.readTile(0, row, width, increment);
			// generate raster mask and set all masked pixels of the raster to the new pixel value
		}
		
		return result;
	}

}
