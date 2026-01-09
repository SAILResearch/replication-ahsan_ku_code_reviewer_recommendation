package com.sail.model;

public class FunctionalInterfaceModel extends BasicModel{

	
	public String parameters;
	public int numberOfParameters;
	public String functionalInterfaceName;
	public String functionalInterfaceIdentifyType;
	
	
	
	public String getFunctionalInterfaceIdentifyType() {
		return functionalInterfaceIdentifyType;
	}

	public void setFunctionalInterfaceIdentifyType(String functionalInterfaceIdentifyType) {
		this.functionalInterfaceIdentifyType = functionalInterfaceIdentifyType;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public int getNumberOfParameters() {
		return numberOfParameters;
	}

	public void setNumberOfParameters(int numberOfParameters) {
		this.numberOfParameters = numberOfParameters;
	}

	public String getFunctionalInterfaceName() {
		return functionalInterfaceName;
	}

	public void setFunctionalInterfaceName(String functionalInterfaceName) {
		this.functionalInterfaceName = functionalInterfaceName;
	}

	public void printInfo() {
		System.out.println("--------------------");
		System.out.println("Start Line: " + this.startLine);
		System.out.println("Name: " + this.functionalInterfaceName);
		System.out.println("Number of parameters: " + this.numberOfParameters);
		System.out.println("Parameter String: " + this.parameters);
		System.out.println("--------------------");
	}
	
	
	
}
