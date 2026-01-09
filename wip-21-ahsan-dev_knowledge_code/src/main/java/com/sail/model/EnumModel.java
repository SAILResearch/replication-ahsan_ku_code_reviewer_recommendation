package com.sail.model;

import java.util.ArrayList;
import java.util.List;

public class EnumModel extends BasicModel{
	public String enumName;
	public boolean hasEnumConstructor;
	public boolean hasEnumConstants;
	public boolean hasEnumFields;
	public boolean hasEnumMethods;
	
	public List<String> enumConstants = new ArrayList<String>();
	public List<String> enumMethods = new ArrayList<String>();
	public List<String> enumFields = new ArrayList<String>();
	
	public String getEnumName() {
		return enumName;
	}
	public void setEnumName(String enumName) {
		this.enumName = enumName;
	}
	public boolean isHasEnumConstructor() {
		return hasEnumConstructor;
	}
	public boolean isHasEnumConstants() {
		return (enumConstants.size() > 0);
	}
	
	public boolean isHasEnumFields() {
		return (enumFields.size() > 0);
	}
	public List<String> getEnumConstants() {
		return enumConstants;
	}
	public void setEnumConstants(List<String> enumConstants) {
		this.enumConstants = enumConstants;
	}
	public List<String> getEnumMethods() {
		return enumMethods;
	}
	public void setEnumMethods(List<String> enumMethods) {
		this.enumMethods = enumMethods;
	}
	public List<String> getEnumFields() {
		return enumFields;
	}
	public void setEnumFields(List<String> enumFields) {
		this.enumFields = enumFields;
	}
	public void setHasEnumConstructor(boolean hasEnumConstructor) {
		this.hasEnumConstructor = hasEnumConstructor;
	}
	public boolean isHasEnumMethods() {
		return (this.enumMethods.size() > 0);
	}
	
}
