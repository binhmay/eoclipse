package com.metaaps.eoclipse.globeviewer.util;

import java.io.File;

import org.w3c.dom.Document;

import gov.nasa.worldwind.BasicFactory;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.cache.FileStore;
import gov.nasa.worldwind.data.DataImportUtil;
import gov.nasa.worldwind.data.TiledImageProducer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.util.WWIO;

public class SurfaceImage implements Renderable {

    private static final int BASE_CACHE_PATH = 0;

	protected static Layer importSurfaceImage(String displayName, Object imageSource, FileStore fileStore)
    {
        // Use the FileStore's install location as the destination for the imported image tiles. The install location
        // is an area in the data file store for permenantly resident data.
        File fileStoreLocation = DataImportUtil.getDefaultImportLocation(fileStore);
        // Create a unique cache name which defines the FileStore path to the imported image.
        String cacheName = BASE_CACHE_PATH + WWIO.stripIllegalFileNameCharacters(displayName);

        // Create a parameter list which defines where the image is imported, and the name associated with it.
        AVList params = new AVListImpl();
        params.setValue(AVKey.FILE_STORE_LOCATION, fileStoreLocation.getAbsolutePath());
        params.setValue(AVKey.DATA_CACHE_NAME, cacheName);
        params.setValue(AVKey.DATASET_NAME, displayName);

        // Create a TiledImageProducer to transforms the source image to a pyramid of images tiles in the World Wind
        // Java cache format.
        TiledImageProducer producer = new TiledImageProducer();
        try
        {
            // Configure the TiledImageProducer with the parameter list and the image source.
            producer.setStoreParameters(params);
            producer.offerDataSource(imageSource, null);
            // Import the source image into the FileStore by converting it to the World Wind Java cache format.
            producer.startProduction();
        }
        catch (Exception e)
        {
            producer.removeProductionState();
            e.printStackTrace();
            return null;
        }

        // Extract the data configuration document from the production results. If production sucessfully completed, the
        // TiledImageProducer should always contain a document in the production results, but we test the results
        // anyway.
        Iterable<?> results = producer.getProductionResults();
        if (results == null || results.iterator() == null || !results.iterator().hasNext())
            return null;

        Object o = results.iterator().next();
        if (o == null || !(o instanceof Document))
            return null;

        // Construct a Layer by passing the data configuration document to a LayerFactory.
        Layer layer = (Layer) BasicFactory.create(AVKey.LAYER_FACTORY, ((Document) o).getDocumentElement());
        layer.setEnabled(true); // TODO: BasicLayerFactory creates layers which are intially disabled
        return layer;
    }

	@Override
	public void render(DrawContext arg0) {
		// TODO Auto-generated method stub
		
	}

}
