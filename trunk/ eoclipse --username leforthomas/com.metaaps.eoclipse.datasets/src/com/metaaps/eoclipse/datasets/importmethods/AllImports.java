package com.metaaps.eoclipse.datasets.importmethods;

import com.metaaps.eoclipse.common.datasets.IImportMethod;
import com.metaaps.eoclipse.common.datasets.IImports;

public class AllImports implements IImports {

	public AllImports() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IImportMethod findImport(String scheme) {
		for(Object obj : ImportFolder.getInstance().getChildren()) {
			IImportMethod importmethod = (IImportMethod) obj;
			if((scheme.length() > 0) && scheme.contentEquals(importmethod.getURIScheme())) {
				return importmethod;
			}
		}
		
		return null;
	}

}
