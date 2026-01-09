package com.sail.model;

public class JavaOtherElementModel extends BasicModel{
	public String name;
	public String elementType;
	public String declClassType;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getElementType() {
		return elementType;
	}
	public void setElementType(String elementType) {
		this.elementType = elementType;
	}
	
	public String getDeclClassType() {
		return declClassType;
	}
	public void setDeclClassType(String declClassType) {
		this.declClassType = declClassType;
	}
	public void printInfo() {
		System.out.println("Start: " + this.getStartLine());
		System.out.println("Name: " + this.getName());
		System.out.println("Elementtype: " + this.getElementType());
		System.out.println("------------------");
	}
	
}
