package com.sail.model;

public class StreamModel extends BasicModel{

	public String invokedMethodName;
	public String fullyQualifiedPackageName;
	
	public String getInvokedMethodName() {
		return invokedMethodName;
	}
	public void setInvokedMethodName(String invokedMethodName) {
		this.invokedMethodName = invokedMethodName;
	}
	public String getFullyQualifiedPackageName() {
		return fullyQualifiedPackageName;
	}
	public void setFullyQualifiedPackageName(String fullyQualifiedPackageName) {
		this.fullyQualifiedPackageName = fullyQualifiedPackageName;
	}
	
}
