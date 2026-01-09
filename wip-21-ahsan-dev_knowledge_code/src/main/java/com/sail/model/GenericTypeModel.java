package com.sail.model;

import java.util.ArrayList;
import java.util.List;

public class GenericTypeModel extends BasicModel{

	public int numberOfParameter;
	public boolean isInterface;
	public List<String> parameterTypeList = new ArrayList<String>();
	public String actionName;
	public String genericInstanceCreationTypeName;
	
	public boolean genericType;

	public int getNumberOfParameter() {
		return numberOfParameter;
	}

	public void setNumberOfParameter(int numberOfParameter) {
		this.numberOfParameter = numberOfParameter;
	}

	public boolean isInterface() {
		return isInterface;
	}

	public void setInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}

	public List<String> getParameterTypeList() {
		return parameterTypeList;
	}

	public void setParameterTypeList(List<String> parameterTypeList) {
		this.parameterTypeList = parameterTypeList;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getGenericInstanceCreationTypeName() {
		return genericInstanceCreationTypeName;
	}

	public void setGenericInstanceCreationTypeName(String genericInstanceCreationTypeName) {
		this.genericInstanceCreationTypeName = genericInstanceCreationTypeName;
	}

	public boolean isGenericType() {
		return genericType;
	}

	public void setGenericType(boolean genericType) {
		this.genericType = genericType;
	}
	
}
