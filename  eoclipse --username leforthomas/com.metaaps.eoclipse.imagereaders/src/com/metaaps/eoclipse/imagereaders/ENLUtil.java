/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metaaps.eoclipse.imagereaders;

import java.util.Hashtable;

import com.metaaps.eoclipse.common.datasets.ISatelliteMetadata;
import com.metaaps.eoclipse.common.datasets.ISARMetadata;

/**
 * Convenience class to calculate the ENL (Equivalent Number of Looks)
 * from a SarImageReader when the meta data does not contain it.
 * @author thoorfr
 */
public class ENLUtil {

    private static Hashtable<String, String> envisatENLMap = new Hashtable<String, String>();
    private static float[][] radarsatENLArray = {
        {4.5f, 4}, // mode s(tandard)
        {1, 1}, // mode f(ine)
        {4.5f, 4}, // mode n(arrow)
        {4.7f, 3.8f} // product scn
    };

    static {
        envisatENLMap.put("IS1", "1.8");
        envisatENLMap.put("IS2", "2.1");
        envisatENLMap.put("IS3", "2.6");
        envisatENLMap.put("IS4", "3.1");
        envisatENLMap.put("IS5", "3.6");
        envisatENLMap.put("IS6", "4.1");
        envisatENLMap.put("IS7", "4.6");
        envisatENLMap.put("WS", "1.8");
    }

    public static String whatIsThis() {
        return "ENL = the Equivalent Number of Looks";
    }

    public static float getFromGeoImageReader(SARImageReader gir) {
        float enl = 1;
        if (gir instanceof Radarsat1Image) {
            int ENLColumn = 0, ENLLine = 0;
            String processor = (String) gir.getMetadata().get(ISatelliteMetadata.PROCESSOR);
            String mode = "" + ((String)gir.getMetadata().get(ISARMetadata.MODE));
            if (processor.charAt(0) == 'K' || processor.charAt(0) == 'S') {
                ENLColumn = 0;
            } else {
                ENLColumn = 1;
            }
            if (mode.equalsIgnoreCase("s")) {
                ENLLine = 0;
            }
            if (mode.equalsIgnoreCase("f")) {
                ENLLine = 1;
            }
            if (mode.equalsIgnoreCase("w")) {
                ENLLine = 2;
            }
            if (((String)gir.getMetadata().get(ISARMetadata.PRODUCT)).equalsIgnoreCase("scn")) {
                ENLLine = 3;
            }
            enl = radarsatENLArray[ENLLine][ENLColumn];
        } else if (gir instanceof EnvisatImage) {
            String swath = (String) gir.getMetadata().get("SWATH");
            enl = Float.parseFloat(envisatENLMap.get(swath));
        }
        return enl;
    }
}
