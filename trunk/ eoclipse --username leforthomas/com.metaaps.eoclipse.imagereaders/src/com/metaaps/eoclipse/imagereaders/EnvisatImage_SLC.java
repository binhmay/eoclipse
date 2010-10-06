/*******************************************************************************
 * Copyright (c) 2010 METAAPS SRL(U).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     METAAPS SRL(U) - created by Thomas Lefort - initial API and implementation
 ******************************************************************************/
package com.metaaps.eoclipse.imagereaders;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeocentricCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.operation.MathTransform;


/**
 * Class that warp EnvisatImage class to read specifically SLC rasters
 * Made for performance issues
 * @author thoorfr
 */
public class EnvisatImage_SLC extends EnvisatImage {


    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.629FC100-1F32-9D04-0B03-05ABE3011501]
    // </editor-fold> 
    public EnvisatImage_SLC() {
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.CBB1DB5A-0DEE-6B06-03FB-01D2BC15A031]
    // </editor-fold> 
    @Override
    public boolean initialise() {
         fss = null;
        try {
            if (file == null) {
                return false;
            }
            fss = new RandomAccessFile(file, "r");
            byte[] magicb = new byte[8];
            fss.read(magicb);
            String magic = new String(magicb);
            if (!magic.equals("PRODUCT=")) {
                return false;
            }
            
            SPH = 0;
            String prefix = "";
            String temp;
            while (fss.getFilePointer() < MPH + SPH) {
                temp = fss.readLine();
                if (temp.contains("=")) {
                    String[] spltemp = temp.split("=");

                    if (spltemp[0].equals("SPH_SIZE")) {
                        String bob = spltemp[1];
                        bob = bob.replace("+", "");
                        SPH = Integer.parseInt(bob.replace("<bytes>", ""));
                    }
                    if (spltemp[0].equals("DS_NAME")) {
                        prefix = spltemp[1].replace("\"", "").trim() + "_";
                    }
                    spltemp[1] = spltemp[1].replace("\"", "").replace("\n", "").trim();

                    setMetadata(prefix + spltemp[0], spltemp[1]);
                //System.out.println(prefix + spltemp[0] + "=" + spltemp[1]);

                }
            }
            name = getFilesList()[0]; //String) getMetadata("PRODUCT");
            extractGcps(fss);
            getWidth();
            setMetadata(WIDTH,xSize);
            getHeight();
            setMetadata(HEIGHT,ySize);
            getTimestamp();
            bounds = new Rectangle(0, 0, xSize, ySize);
            geotransform = GeoTransformFactory.createFromGcps(gcps, "EPSG:4326");
            setMetadata(SATELLITE, "ENVISAT");
            setMetadata(SENSOR, "ASAR");
            setMetadata(BEAM, getMetadata("SWATH"));
            setMetadata(TYPE, "ASAR");
            setMetadata(TIMESTAMP_START, getMetadata("SENSING_START"));
            setMetadata(TIMESTAMP_STOP, getMetadata("SENSING_STOP"));
            setMetadata(ENL, String.valueOf(ENLUtil.getFromGeoImageReader(this)));
            setMetadata(HEADING_ANGLE, String.valueOf(this.getImageAzimuth()));
            // get incidence angles from gcps and convert them into radians
            float firstIncidenceangle = (float) (this.gcps.get(0).getAngle());
            float lastIncidenceAngle = (float) (this.gcps.get(this.gcps.size() - 1).getAngle());
            setMetadata(LOOK_DIRECTION, firstIncidenceangle < lastIncidenceAngle ? "RIGHT" : "LEFT");
            setMetadata(INCIDENCE_NEAR, String.valueOf(firstIncidenceangle < lastIncidenceAngle ? firstIncidenceangle : lastIncidenceAngle));
            setMetadata(INCIDENCE_FAR, String.valueOf(firstIncidenceangle > lastIncidenceAngle ? firstIncidenceangle : lastIncidenceAngle));
            setMetadata(POLARISATION, getMetadata("MDS1_TX_RX_POLAR") + ", " + getMetadata("MDS2_TX_RX_POLAR"));
            setMetadata(ORBIT_DIRECTION, getMetadata("PASS"));
            setMetadata(PROCESSOR, getMetadata("SOFTWARE_VER") + " with " + getMetadata("ALGORITHM"));
            //remove the <m> otherwise crashed in detected pixels
            setMetadata(AZIMUTH_SPACING, getMetadata("AZIMUTH_SPACING").toString().replace("<m>", ""));
            setMetadata(RANGE_SPACING, getMetadata("RANGE_SPACING").toString().replace("<m>", ""));
            setMetadata(MODE, getMetadata("SPH_DESCRIPTOR"));
            double xposition = Double.parseDouble(((String)getMetadata("X_POSITION")).replaceAll("<m>", ""));
            double yposition = Double.parseDouble(((String)getMetadata("Y_POSITION")).replaceAll("<m>", ""));
            double zposition = Double.parseDouble(((String)getMetadata("Z_POSITION")).replaceAll("<m>", ""));
            double radialdist = Math.pow(xposition * xposition + yposition * yposition + zposition * zposition, 0.5);
            MathTransform convert;
            double[] latlon = getGeoTransform().getGeoFromPixel(0.0, 0.0, "EPSG:4326");
            double[] position = new double[3];
            convert = CRS.findMathTransform(DefaultGeographicCRS.WGS84, DefaultGeocentricCRS.CARTESIAN);
            convert.transform(latlon, 0, position, 0, 1);
            double earthradial = Math.pow(position[0] * position[0] + position[1] * position[1] + position[2] * position[2], 0.5);
            setMetadata(SATELLITE_ALTITUDE, String.valueOf(radialdist - earthradial));
            double xvelocity = Double.parseDouble(((String)getMetadata("X_VELOCITY")).replaceAll("<m/s>", ""));
            double yvelocity = Double.parseDouble(((String)getMetadata("Y_VELOCITY")).replaceAll("<m/s>", ""));
            double zvelocity = Double.parseDouble(((String)getMetadata("Z_VELOCITY")).replaceAll("<m/s>", ""));
            double velocity = Math.pow(xvelocity * xvelocity + yvelocity * yvelocity + zvelocity * zvelocity, 0.5);
            setMetadata(SATELLITE_SPEED, String.valueOf(velocity));
            setMetadata(SATELLITE_ORBITINCLINATION, "98.5485");
            int offset_processing = Integer.parseInt(((String)getMetadata("MAIN PROCESSING PARAMS ADS_DS_OFFSET")).replace("<bytes>", "").replace("+", ""));
            fss.seek(offset_processing + 703);
            setMetadata(PRF, String.valueOf(fss.readFloat()));
            fss.seek(offset_processing + 987);
            float radarFrequency = fss.readFloat();
            setMetadata(RADAR_WAVELENGTH, String.valueOf(299792457.9 / radarFrequency));
            setMetadata(REVOLUTIONS_PERDAY, String.valueOf(14.32247085));
            fss.seek(offset_processing + 1381);
            setMetadata(K, String.valueOf(fss.readFloat()));

        } catch (Exception ex) {
            Logger.getLogger(EnvisatImage.class.getName()).log(Level.SEVERE, null, ex);
            dispose();
            return false;
        } finally {
        }

        return true;
    }
    

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.51939D82-C149-F3C5-E238-2F946C1E2A47]
    // </editor-fold> 
    @Override
    public int read(int x, int y) {
        int result = 0;
        long temp = 0;
        byte[] pixelByte = new byte[4];
        // System.out.println(this.imageType);
        if (x >= 0 & y >= 0 & x < xSize & y < ySize) {
            try {
                temp = (y * (xOffset + xSize * 4) + xOffset + x * 4);
                fss.seek(temp + offsetBand);
                fss.read(pixelByte, 0, 4);
                byte interm0 = pixelByte[0];
                byte interm1 = pixelByte[1];
                byte interm2 = pixelByte[2];
                byte interm3 = pixelByte[3];
                long real=((interm0) << 8) | (interm1&0xFF);
                long img=((interm2) << 8) | (interm3&0xFF);
                result = (int)Math.sqrt(real*real+img*img);
            } catch (IOException e) {
                Logger.getLogger(EnvisatImage.class.getName()).log(Level.SEVERE, "cannot read pixel (" + x + "," + y + ")", e);
            }
        }
        return result;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.A2952509-283F-287E-993A-1B96B54243CF]
    // </editor-fold> 
    @Override
    public void preloadLineTile(int y, int length) {
        if (y < 0) {
            return;
        }
        preloadedInterval = new int[]{y, y + length};
        int tileOffset = offsetBand + (y * (xSize * 4 + xOffset));
        preloadedData = new byte[(xSize * 4 + xOffset) * length];
        try {
            fss.seek(tileOffset);
            fss.read(preloadedData);
        } catch (IOException e) {
            Logger.getLogger(EnvisatImage.class.getName()).log(Level.SEVERE, "cannot preload the line tile", e);
        }
    }

    
    @Override
    public int[] readTile(int x, int y, int width, int height, int[] tile) {
        Rectangle rect = new Rectangle(x, y, width, height);
        rect = rect.intersection(bounds);
        if (rect.isEmpty()) {
            return tile;
        }
        if (rect.y != preloadedInterval[0] || rect.y + rect.height != preloadedInterval[1]) {
            preloadLineTile(rect.y, rect.height);
        }
        int yOffset = xOffset + 4 * xSize;
        int xinit = rect.x - x;
        int yinit = rect.y - y;
        for (int i = 0; i < rect.height; i++) {
            for (int j = 0; j < rect.width; j++) {
                int temp = i * yOffset + j*4 + 4 * rect.x + xOffset;
                long real=(preloadedData[temp+0] << 8) | (preloadedData[temp+1]&0xff );
                long img=((preloadedData[temp+2]) << 8) | (preloadedData[temp+3]&0xff);
                tile[(i + yinit) * width + j + xinit] = (int)Math.sqrt(real*real+img*img);
            }
        }
        return tile;
    }

   
    @Override
    public int getNumberOfBytes() {
        return 4;
    }
    @Override
    public String getFormat() {
        return getClass().getCanonicalName();
    }

}
