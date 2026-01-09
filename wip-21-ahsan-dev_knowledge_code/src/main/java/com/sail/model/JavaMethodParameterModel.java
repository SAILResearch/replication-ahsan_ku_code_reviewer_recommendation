package com.sail.model;

public class JavaMethodParameterModel extends BasicModel{

	public String parameterName;
	public String parameterTypeName;
	public String parameterType;
	public boolean isVariableLengthArgument;
	
	public String getParameterName() {
		return parameterName;
	}
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	public String getParameterTypeName() {
		return parameterTypeName;
	}
	public void setParameterTypeName(String parameterTypeName) {
		this.parameterTypeName = parameterTypeName;
	}
	public String getParameterType() {
		return parameterType;
	}
	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}
	public boolean isVariableLengthArgument() {
		return isVariableLengthArgument;
	}
	public void setVariableLengthArgument(boolean isVariableLengthArgument) {
		this.isVariableLengthArgument = isVariableLengthArgument;
	}
	
	public void summaryParameterInfo() {
		System.out.println("Parameter Name: " + this.getParameterName());
		System.out.println("Parameter Type Name: " + this.getParameterTypeName());
		System.out.println("Parameter Type: " + this.getParameterType());
		System.out.println("Is Parameter Varargs: " + this.isVariableLengthArgument());
	}
}
