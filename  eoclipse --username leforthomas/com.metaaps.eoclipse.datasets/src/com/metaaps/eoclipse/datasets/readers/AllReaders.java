package com.metaaps.eoclipse.datasets.readers;

import com.metaaps.eoclipse.common.datasets.IReader;
import com.metaaps.eoclipse.common.datasets.IReaders;

public class AllReaders implements IReaders {

	public AllReaders() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IReader findReader(String readername) {
		for(Object obj : ReadersFolder.getInstance().getChildren()) {
			IReader childreader = (IReader) obj;
			if(childreader.getName().contentEquals(readername)) {
				return childreader;
			}
		}
		
		return null;
	}

}
