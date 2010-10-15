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
package com.metaaps.eoclipse.vectordatareader;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.indexed.IndexedShapefileDataStoreFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureType;
import org.geotools.feature.SchemaException;
import org.geotools.feature.SimpleFeatureType;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

import com.metaaps.eoclipse.common.Attributes;
import com.metaaps.eoclipse.common.CodeFragment;
import com.metaaps.eoclipse.common.datasets.DataContent;
import com.metaaps.eoclipse.common.datasets.ISourceDataContent;
import com.metaaps.eoclipse.common.datasets.ITableData;
import com.metaaps.eoclipse.common.datasets.IVectorData;
import com.metaaps.eoclipse.common.datasets.VectorData;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @author leforthomas
 * based on code from fxthoor
 * 
 * ShapeFile Reader
 * 
 */
public class ShapeFileReader extends DataContent implements IVectorData, ISourceDataContent {

	private File m_file;
	private DataStore m_dataStore;

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFile(File file) {
		m_file = file;
	}

	@Override
	public boolean initialise() {
		if(m_file != null) {
	        try {
	        	Map<String, Serializable> connectParameters = new HashMap<String, Serializable>();
	        	connectParameters.put("url", m_file.toURI().toURL());
	            m_dataStore = new ShapefileDataStore(m_file.toURI().toURL());
//	            m_dataStore = DataStoreFinder.getDataStore(connectParameters);
	            if(m_dataStore != null) {
	            	return true;
	            }
            
	        } catch (Exception ex) {
				ex.printStackTrace();
	        }
		}
		
		return false;
	}

	@Override
	public void save(File file) {
	}

    private static String[] createSchema(Collection<PropertyDescriptor> attributeTypes) {
        String[] out = new String[attributeTypes.size()];
        int i = 0;
        for (PropertyDescriptor at : attributeTypes) {
            out[i++] = at.getName().toString();
        }
        return out;
    }

    private static String[] createTypes(Collection<PropertyDescriptor> attributeTypes) {
        String[] out = new String[attributeTypes.size()];
        int i = 0;
        for (PropertyDescriptor at : attributeTypes) {
            out[i++] = at.getType().getBinding().getSimpleName();
        }
        return out;
    }

	@Override
	public VectorData getVectorData(String cqlFilter) {
		VectorData vectordata = new VectorData();
		
		if(m_dataStore != null) {
	        // needs feature name
			try {
				FeatureSource featureSource = m_dataStore.getFeatureSource(getGeometryName());
		        String geomName = featureSource.getSchema().getGeometryDescriptor().getLocalName();
	            FeatureCollection featurecollection = null;
		        if(cqlFilter.length() != 0) {
		            Filter filter = CQL.toFilter(cqlFilter);
		            DefaultQuery query = new DefaultQuery(featureSource.getSchema().getName().getLocalPart(), filter);
		            featurecollection = featureSource.getFeatures(query);
		        } else {
		            featurecollection = featureSource.getFeatures();
		        }

		        if (!featurecollection.isEmpty()) {
			        FeatureIterator fi = featurecollection.features();
		            String[] schema = createSchema(featurecollection.getSchema().getDescriptors());
		            String[] types = createTypes(featurecollection.getSchema().getDescriptors());
			        while (fi.hasNext()) {
			            Feature f = fi.next();
			            try {
			                Attributes at = Attributes.createAttributes(schema, types);
			                for (int i = 0; i < f.getProperties().size(); i++) {
			                    at.set(schema[i], f.getProperty(schema[i]).getValue());
			                }
			                vectordata.put((Geometry) f.getDefaultGeometryProperty().getValue(), at);
	
			            } catch (Exception e) {
							e.printStackTrace();
			            }
			        }
		        }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
		}
		
		return vectordata;
	}
	
	@Override
	public ITableData getTableData() {
		VectorData data = getVectorData("");
		final List<Geometry> geometries = data.getGeometries();
		List<Attributes> attributes = data.getAttributes();
		final String[] schema = data.getSchema();
		final ArrayList<Object[]> attvalues = new ArrayList<Object[]>();
		for(Attributes att : data.getAttributes()) {
			attvalues.add(att.getValues());
		}
		return new ITableData() {
			
			@Override
			public Object[] getRowValues(int rownumber) {
//				Geometry geometry = geometries.get(rownumber);
				return schema;
			}
			
			@Override
			public String[] getColumnNames() {
				return schema;
			}

			@Override
			public Object[] getAllRows() {
				return attvalues.toArray();
			}
		};
	}

	@Override
	public HashMap<String, Object> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGeometryName() {
		String featurename = "";
		if(m_dataStore != null) {
			try {
				featurename = m_dataStore.getTypeNames()[0];
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return featurename;
	}

	@Override
	public String[] getFilesList() {
		return new String[]{m_file.getAbsolutePath()};
	}

	@Override
	public String getDataFormat() {
		// Change to return the exact type of the shapefile data
		return DATA_FORMATS.VECTOR_ALL.toString();
	}

}
