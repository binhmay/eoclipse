package com.metaaps.eoclipse.common.datasets;

public interface ITableData {
	String[] getColumnNames();
	Object[] getRowValues(int rownumber);
}
