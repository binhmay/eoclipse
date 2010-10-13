package com.metaaps.eoclipse.common.views;

import java.util.List;

import com.metaaps.eoclipse.common.IWorkFlow;
import com.metaaps.eoclipse.common.datasets.IDataSets;

/**
 * 
 * Interface used for retrieving all available viewers
 * 
 * @author leforthomas
 */
public interface IViewers {

	IViewerItem findViewer(String viewID);
	
	List<IViewerImplementation> findDataSetsViewers(IDataSets datasets);
}
