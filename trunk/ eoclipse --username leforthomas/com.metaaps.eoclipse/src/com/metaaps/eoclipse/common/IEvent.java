package com.metaaps.eoclipse.common;

public interface IEvent {

	static final String REMOVED = "REMOVED";
	static final String ADDED = "ADDED";

	void addListener(IModelChangeListener listener);
	
	void removeListener(IModelChangeListener listener);
	
}
