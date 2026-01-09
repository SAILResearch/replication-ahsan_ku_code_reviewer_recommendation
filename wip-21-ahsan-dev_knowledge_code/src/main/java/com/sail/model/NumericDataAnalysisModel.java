package com.sail.model;

import java.util.HashMap;
import java.util.Map;

public class NumericDataAnalysisModel {

	public Map<String,Double> fieldWithPosValueList = new HashMap<String,Double>(); 
	public double totalFile;
	public double changedFile;
	public Map<String, Double> getFieldWithPosValueList() {
		return fieldWithPosValueList;
	}
	public void setFieldWithPosValueList(Map<String, Double> fieldWithPosValueList) {
		this.fieldWithPosValueList = fieldWithPosValueList;
	}
	public double getTotalFile() {
		return totalFile;
	}
	public void setTotalFile(double totalFile) {
		this.totalFile = totalFile;
	}
	public double getChangedFile() {
		return changedFile;
	}
	public void setChangedFile(double changedFile) {
		this.changedFile = changedFile;
	}
}
