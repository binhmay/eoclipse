package com.metaaps.eoclipse.common.datasets;

import java.io.File;


/**
 * @author leforthomas
 * 
 * interface for data content that has been read from a data source
 * 
 */
public interface ISourceDataContent extends IDataContent {

	// needs changing as it will not work for database connections for instance
	void setFile(File file);
	String[] getFilesList();
	boolean initialise();
	void save(File file);
	
}
