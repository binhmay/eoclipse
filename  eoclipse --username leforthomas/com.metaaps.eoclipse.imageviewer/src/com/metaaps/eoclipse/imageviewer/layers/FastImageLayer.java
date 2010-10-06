/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metaaps.eoclipse.imageviewer.layers;

import com.metaaps.eoclipse.common.datasets.IDataContent;
import com.metaaps.eoclipse.common.datasets.IGeoRaster;
import com.metaaps.eoclipse.common.datasets.ISatelliteMetadata;
import com.metaaps.eoclipse.common.datasets.ISourceDataContent;
import com.metaaps.eoclipse.imageviewer.Activator;
import com.metaaps.eoclipse.imageviewer.api.GeoContext;
import com.metaaps.eoclipse.imageviewer.api.IImageLayer;
import com.metaaps.eoclipse.imageviewer.api.ILayerManager;
import com.metaaps.eoclipse.imageviewer.layers.image.CacheManager;
import com.metaaps.eoclipse.imageviewer.layers.image.ImagePool;
import com.metaaps.eoclipse.imageviewer.layers.image.TextureCacheManager;
import com.metaaps.eoclipse.imageviewer.layers.image.TiledBufferedImage;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;


/**
 *
 * @author thoorfr
 */
public class FastImageLayer extends LayerManager implements IImageLayer {

    private IGeoRaster eoir;
    private HashMap<String, Float> contrast = new HashMap<String, Float>();
    private float brightness = 0;
    private int[] bands;
    private int xpadding;
    private int ypadding;
    private TextureCacheManager tcm;
    private ExecutorService pool;
    private int poolSize = 4;
    private Vector<String> submitedTiles;
    private ImagePool imagePool;
    private List<Future<Object[]>> futures = new Vector<Future<Object[]>>();
    private int mylevel = -1;
    private RescaleOp rescale = new RescaleOp(1f, brightness, null);
    private boolean disposed = false;
    private int maxCut = 1;
    private int il = 0;
    private int currentSize = 0;
    private int curlevel;
    private int levels;
    private boolean torescale = false;
    private int maxlevels;
    private int[] nat;
    private int maxnumberoftiles = 7;
    private int countGC = 0;
    static String MaxNumberOfTiles = "Max Number Of Tiles";


    public FastImageLayer(ILayerManager parent, IGeoRaster eoir) {
        super(parent);
        this.eoir = eoir;
        poolSize = 1;
        pool = Executors.newFixedThreadPool(poolSize);
        submitedTiles = new Vector<String>();
        imagePool = new ImagePool(eoir, poolSize);
        String temp = getCacheFileName().replace("\\", "/");
        setName(temp.substring(temp.lastIndexOf("/") + 1));
        description = eoir.getName();
        bands = new int[1];
        bands[0] = 0;
        levels = (int) (Math.sqrt(Math.max(eoir.getWidth() / 256., eoir.getHeight() / 256.))) + 1;
        maxlevels = (int) (Math.sqrt(Math.max(eoir.getWidth() / 512., eoir.getHeight() / 512.))) + 1;
        curlevel = levels;
        xpadding = (((1 << levels) << 8) - eoir.getWidth()) / 2;
        ypadding = (((1 << levels) << 8) - eoir.getHeight()) / 2;
        int maxBuffer = 64;
        tcm = new TextureCacheManager(maxBuffer);
        setInitialContrast();
        maxnumberoftiles = 7;
    }

    private String getCacheFileName() {
    	if(eoir instanceof ISourceDataContent) {
    		return ((ISourceDataContent)eoir).getFilesList()[0];
    	}
		return eoir.getName();
	}

	@Override
    public String getDescription() {
        if ((eoir instanceof ISatelliteMetadata) && (((ISatelliteMetadata)eoir).getMetadata() != null)) {
            return eoir.getDescription();
        } else {
            return eoir.getName();
        }
    }

    @Override
    //displays the tiles on screen
    public void render(GeoContext context) {
        if (torescale) {
            torescale = false;
            tcm.clear();
        }
        if (!context.isDirty()) {
            super.render(context);
            return;
        }
        updateFutures();
        float zoom = context.getZoom();
        int width = context.getWidth(), height = context.getHeight();
        int x = context.getX(), y = context.getY();
        GL gl = context.getGL();
        gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
        if (zoom >= 1) {
            curlevel = (int) Math.sqrt(zoom + 1);
            //cycle to avoid the black area when zooming in/out and tiles not in memory
            for (int lll = maxlevels; lll > curlevel - 1; lll--) {
                if (lll > maxlevels) {
                    break;
                }

                lll += il;
                if (lll < 0) {
                    continue;
                }
                if (this.mylevel != curlevel) {
                    System.out.println(mylevel + "->" + curlevel);
                    this.mylevel = curlevel;
                    pool.shutdown();
                    pool = Executors.newFixedThreadPool(poolSize);
                }
                int xx = (int) (x + xpadding);
                int yy = (int) (y + ypadding);
                int w0 = xx / ((1 << lll) << 8);
                //max tiles to compute time by time
                int max = maxnumberoftiles; //for the computation of the array is important to keep this number odd
                int w1 = w0 + max;
                int h0 = yy / ((1 << lll) << 8);
                int h1 = h0 + max;
                int array[][] = new int[max][max];//contain the weight for reading tile sequence, 0 most important
                for (int i = 0; i <= max / 2; i++) {
                    int k = max - i - 1;
                    for (int j = 0; j < max / 2; j++) {
                        array[i][j] = k--;
                    }
                    for (int j = max / 2; j < max; j++) {
                        array[i][j] = k++;
                    }
                }
                for (int i = max / 2 + 1; i < max; i++) {
                    int k = i;
                    for (int j = 0; j < max / 2; j++) {
                        array[i][j] = k--;
                    }
                    for (int j = max / 2; j < max; j++) {
                        array[i][j] = k++;
                    }
                }

                // System.out.println(w0+";"+h0);

                final String initfile = getCacheFileName() + "/" + (int) lll + "/"+(eoir instanceof TiledBufferedImage?((TiledBufferedImage)eoir).getDescription()+"/":"");
                
                //int count = 0;
                //long time=System.currentTimeMillis();
                //AG loads the different tiles, starting from the center (k=0)
                for (int k = 0; k < max; k++) {
                    for (int j = 0; j < max; j++) {
                        if (j + h0 < 0) {
                            continue;
                        }
                        for (int i = 0; i < max; i++) {
                            if (i + w0 < 0) {
                                continue;
                            }
                            if (array[i][j] == k) {
                                //start reading tiles in center of the image and go through the borders
                                float ymin = (float) (((j + h0) * 256d * (1 << lll) - yy) / (height * zoom));
                                float ymax = (float) (((j + h0 + 1) * 256d * (1 << lll) - yy) / (height * zoom));
                                float xmin = (float) (((i + w0) * 256d * (1 << lll) - xx) / (1d * width * zoom));
                                float xmax = (float) (((i + w0 + 1) * 256d * (1 << lll) - xx) / (1d * width * zoom));

                                //check if the tile is in or out, if is not visible then is not loaded
                                if (ymin > 1 || ymax < 0) {
                                    continue;
                                }
                                if (xmin > 1 || xmax < 0) {
                                    continue;
                                }
                                String file = initfile + getBandFolder(bands) + "/" + (i + w0) + "_" + (j + h0) + ".png";

                                //checked if the tile is already in memory or in cache, otherwise required it
                                if (!tryMemoryCache(gl, file, xmin, xmax, ymin, ymax)) {
                                    if (!tryFileCache(gl, file, lll, (i + w0), (j + h0), xmin, xmax, ymin, ymax)) {
                                        if (curlevel == 0 && lll == 0) {
                                            addTileToQueue(initfile, lll, (i + w0), (j + h0));
                                        } else if (curlevel == lll) {
                                            addTileToQueue(initfile, lll, (i + w0), (j + h0));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (zoom > 0) {
            curlevel = 0;
            int xx = (int) (x + xpadding);
            int yy = (int) (y + ypadding);
            int w0 = xx / 256;
            //max tiles to compute time by time
            int max = maxnumberoftiles; //for the computation of the array is important to keep this number odd
            int w1 = w0 + max;
            int h0 = yy / 256;
            int h1 = h0 + max;
            final String initfile = getCacheFileName() + "/0/"+(eoir instanceof TiledBufferedImage?((TiledBufferedImage)eoir).getDescription()+"/":"");
            for (int j = 0; j < max; j++) {
                if (j + h0 < 0) {
                    continue;
                }
                for (int i = 0; i < max; i++) {
                    if (i + w0 < 0) {
                        continue;
                    }
                    //start reading tiles in center of the image and go through the borders
                    float ymin = (float) (((j + h0) * 256d - yy) / (height * zoom));
                    float ymax = (float) (((j + h0 + 1) * 256d - yy) / (height * zoom));
                    float xmin = (float) (((i + w0) * 256d - xx) / (1d * width * zoom));
                    float xmax = (float) (((i + w0 + 1) * 256d - xx) / (1d * width * zoom));

                    //check if the tile is in or out, if is not visible then is not loaded
                    if (ymin > 1 || ymax < 0) {
                        continue;
                    }
                    if (xmin > 1 || xmax < 0) {
                        continue;
                    }
                    String file = initfile + getBandFolder(bands) + "/" + (i + w0) + "_" + (j + h0) + ".png";

                    //checked if the tile is already in memory or in cache, otherwise required it
                    if (!tryMemoryCache(gl, file, xmin, xmax, ymin, ymax)) {
                        if (!tryFileCache(gl, file, 0, (i + w0), (j + h0), xmin, xmax, ymin, ymax)) {
                            addTileToQueue(initfile, 0, (i + w0), (j + h0));
                        }
                    }
                }
            }

        }
        displayDownloading(futures.size());
        super.render(context);

        if (this.disposed) {

            disposeSync();
        }

    }

    private void displayDownloading(int size) {
        if (currentSize != size) {

            currentSize = size;
        }

    }

    private void setInitialContrast() {
        eoir.setBand(bands[0]);

        // very rough calculation of a possible suitable contrast value
        int[] data = eoir.readTile(eoir.getWidth() / 2 - 100, eoir.getHeight() / 2 - 100, 200, 200);
        float average = 0;
        for (int i = 0; i < data.length; i++) {
            average = average + data[i];
        }

        average = average / data.length;
        setContrast((1 << (8 * eoir.getNumberOfBytes())) / 5 / average);
    }

    //search for tiles in the file cache
    private boolean tryFileCache(GL gl, String file, int level, int i, int j, float xmin, float xmax, float ymin, float ymax) {
        if (CacheManager.getInstance().contains(file) & !submitedTiles.contains(level + " " + getBandFolder(bands) + " " + i + " " + j)) {
            try {
                BufferedImage temp = ImageIO.read(CacheManager.getInstance().newFile(file));
                if (temp == null) {
                    return false;
                }

                if (temp.getColorModel().getNumComponents() == 1) {
                    temp = rescale.filter(temp, rescale.createCompatibleDestImage(temp, temp.getColorModel()));
                }
                Texture t = TextureIO.newTexture(temp, false);
                tcm.add(file, t);
                bindTexture(gl, t, xmin, xmax, ymin, ymax);
                return true;
            } catch (Exception ex) {
                Logger.getLogger(FastImageLayer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        return false;
    }

    //search for the tiles on memory
    private boolean tryMemoryCache(GL gl, String file, float xmin, float xmax, float ymin, float ymax) {
        Texture t = tcm.getTexture(file);        
        if (t != null) {
            bindTexture(gl, t, xmin, xmax, ymin, ymax);
            return true;
        }
        return false;
    }

    private void bindTexture(GL gl, Texture texture, float xmin, float xmax, float ymin, float ymax) {
        texture.enable();
        texture.bind();
        TextureCoords coords = texture.getImageTexCoords();
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(coords.left(), coords.top());
        gl.glVertex2f(xmin, 1 - ymin);
        gl.glTexCoord2f(coords.right(), coords.top());
        gl.glVertex2f(xmax, 1 - ymin);
        gl.glTexCoord2f(coords.right(), coords.bottom());
        gl.glVertex2f(xmax, 1 - ymax);
        gl.glTexCoord2f(coords.left(), coords.bottom());
        gl.glVertex2f(xmin, 1 - ymax);
        gl.glEnd();
        texture.disable();
    }

    private BufferedImage createImage(IGeoRaster eoir, int x, int y, int width, int height, float zoom) {
        BufferedImage bufferedImage = new BufferedImage(width, height, eoir.getType(bands.length == 1));

        //System.out.println(zoom);
        WritableRaster raster = bufferedImage.getRaster();

        // Put the pixels on the raster.
        if (bands.length == 1) {
            int band = bands[0];
            eoir.setBand(band);
            nat = eoir.readAndDecimateTile(x, y, (int) (width * zoom), (int) (height * zoom), width, height, true);
            raster.setPixels(0, 0, width, height, nat);

        } else {
            int b = 0;
            for (int band : bands) {
                eoir.setBand(band);
                nat = eoir.readAndDecimateTile(x, y, (int) (width * zoom), (int) (height * zoom), width, height, true);
                ///if (zoom == 1) {
                if(maxCut>1){
                    for(int tt=0;tt<nat.length;tt++){
                        nat[tt]/=maxCut;
                    }
                }
                raster.setSamples(0, 0, width, height, b, nat);
                /**
                 * for (int h = 0; h < height; h++) {
                 *   int temp = h * width;
                 *   for (int w = 0; w < width; w++) {
                 *      raster.setSample(w, h, b, nat[temp + w]);
                 *   }
                 * }
                 **/
                /**
                 * } else {
                 *       for (int h = 0; h < height; h++) {
                 *          int temp = (int) (h * zoom) * (int) (width * zoom);
                 *         for (int w = 0; w < width; w++) {
                 *            try {
                 *               raster.setSample(w, h, b, nat[temp + (int) (w * zoom)]);
                 *          } catch (Exception e) {
                 *               }
                 *          }
                 *      }
                 *  }
                 **/
                b++;
                if (b > raster.getNumBands()) {
                    break;
                }
            }

        }
        return bufferedImage;
    }

    private String getBandFolder(int[] band) {
        String out = "";
        if (band.length == 1) {
            out += band[0];
        } else if (band.length > 1) {
            out += band[0];
            for (int i = 1; i < band.length; i++) {
                out += "_" + band[i];
            }
        }

        return out;
    }

    public void addTileToQueue(final String initfile, final int level, final int i, final int j) {
        if (!submitedTiles.contains(level + " " + getBandFolder(bands) + " " + i + " " + j)) {
            submitedTiles.add(level + " " + getBandFolder(bands) + " " + i + " " + j);
            futures.add(0, pool.submit(new Callable<Object[]>() {

                public Object[] call() {
                    final File f = CacheManager.getInstance().newFile(initfile + getBandFolder(bands) + "/" + i + "_" + j + ".png");
                    if (f == null) {
                        return new Object[]{initfile + getBandFolder(bands) + "/" + i + "_" + j + ".png", level + " " + i + " " + j, null};
                    }

                    IGeoRaster eoir2 = imagePool.get();
                    if (eoir2 == null) {
                        return new Object[]{f.getAbsolutePath(), level + " " + getBandFolder(bands) + " " + i + " " + j, null};
                    }

                    try {
                        //eoir2.setBand(getBands()[0]);
                        //int[] t = eoir2.readAndDecimateTile(i * (1 << level) * 256 - xpadding, j * (1 << level) * 256 - ypadding, (1 << level) * 256, (1 << level) * 256, 256, 256, true);
                        //System.out.println(f.getAbsoluteFile());
                        final BufferedImage out = createImage(eoir2, i * (1 << level) * 256 - xpadding, j * (1 << level) * 256 - ypadding, 256, 256, (1 << level));
                        imagePool.release(eoir2);
                        ImageIO.write(out, "png", f);
                        return new Object[]{f.getAbsolutePath(), level + " " + getBandFolder(bands) + " " + i + " " + j, out};
                    } catch (Exception ex) {
                        imagePool.release(eoir2);
                        Logger.getLogger(FastImageLayer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return new Object[]{f.getAbsolutePath(), level + " " + getBandFolder(bands) + " " + i + " " + j, null};
                }
            }));
        }

    }

    private void updateFutures() {
        Vector<Future<Object[]>> remove1 = new Vector<Future<Object[]>>();
        for (Future<Object[]> f : futures) {
            if (f.isDone() || f.isCancelled()) {
                remove1.add(f);
                try {
                    Object[] o = f.get();
                    submitedTiles.remove(o[1]);
                    if (o[2] != null) {
                        tcm.add((String) o[0], TextureIO.newTexture((BufferedImage) o[2], false));
                    }


                } catch (Exception ex) {
                    Logger.getLogger(FastImageLayer.class.getName()).log(Level.SEVERE, null, ex);
                    //mcm.clear();
                    //tcm.clear();
                }
            }

        }
        futures.removeAll(remove1);
    }

    public void setContrast(float value) {
        contrast.put(createBandsString(bands), value);
        torescale = true;
        rescale = new RescaleOp(value, brightness, null);
    }

    private String createBandsString(int[] b) {
        String out = "";
        for (int i = 0; i < b.length; i++) {
            out += b[i] + ",";
        }
        return out;
    }

    public void setBrightness(float value) {
        this.brightness = value;
        torescale = true;
        rescale = new RescaleOp(getContrast(), brightness, null);
    }

    public float getContrast() {
        return contrast.get(createBandsString(bands)) == null ? 1 : contrast.get(createBandsString(bands));
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBand(int[] values) {
        if (futures.size() > 0) {
            return;
        }

//      only colored geotiff make sense to have several bands displayed
//        if (eoir instanceof GeotiffImage) {
            this.bands = values;
//        } else {
//            this.bands = new int[]{values[0]};
//        }
        if (contrast.get(createBandsString(bands)) == null) {
            setInitialContrast();
        } else {
            rescale = new RescaleOp(contrast.get(createBandsString(bands)), brightness, null);
        }
    }

    public int getNumberOfBands() {
        return eoir.getNBand();
    }

    public int[] getBands() {
        return bands;
    }

    @Override
    public void dispose() {
        disposed = true;
        pool.shutdownNow();
        pool = null;
        super.dispose();
        eoir.dispose();
        eoir = null;
        tcm.clear();
        tcm = null;
        submitedTiles.clear();
        submitedTiles = null;
        imagePool.dispose();
        imagePool = null;
    }

    private void disposeSync() {

        pool.shutdownNow();
        pool = null;
        super.dispose();
        eoir.dispose();
        eoir = null;
        tcm.clear();
        tcm = null;
        submitedTiles.clear();
        submitedTiles = null;
        imagePool.dispose();
        imagePool = null;

    }

    public IGeoRaster getImage() {
        return eoir;
    }

    public void setMaximum(float value) {
        maxCut = (int) value;
    }

    public float getMaximum() {
        return maxCut;
    }

	@Override
	public void setMinimum(float value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getMinimum() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public boolean represents(IDataContent datacontent) {
		return eoir == datacontent;
	}

}
