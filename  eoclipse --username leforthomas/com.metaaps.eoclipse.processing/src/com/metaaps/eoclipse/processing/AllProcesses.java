package com.metaaps.eoclipse.processing;

import com.metaaps.eoclipse.common.processing.IProcess;
import com.metaaps.eoclipse.common.processing.IProcesses;

public class AllProcesses implements IProcesses {

	public AllProcesses() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IProcess findProcess(String processingclass) {
		// TODO Auto-generated method stub
		return Processes.getInstance().findProcess(processingclass);
	}
}
