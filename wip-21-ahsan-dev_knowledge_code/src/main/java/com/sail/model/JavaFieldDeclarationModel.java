package com.sail.model;

public class JavaFieldDeclarationModel extends BasicModel{

	
	public String fieldName;
	public String fieldTypeName;
	public String fieldType;
	public boolean isStatic;
	public String accessModifier;
	public boolean isFinal;
	
	public boolean isStatic() {
		return isStatic;
	}
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	public String getAccessModifier() {
		return accessModifier;
	}
	public void setAccessModifier(String accessModifier) {
		this.accessModifier = accessModifier;
	}
	public boolean isFinal() {
		return isFinal;
	}
	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldTypeName() {
		return fieldTypeName;
	}
	public void setFieldTypeName(String fieldTypeName) {
		this.fieldTypeName = fieldTypeName;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	
	public void summaryFieldInfo() {
		System.out.println("Field Name: " + this.getFieldName());
		System.out.println("Field start line: " + this.getStartLine());
		System.out.println("Field end line: " + this.getEndLine());
		System.out.println("Field Type Name: " + this.getFieldTypeName());
		System.out.println("Field Type: " + this.getFieldType());
		System.out.println("Field isStatic: " + this.isStatic());
		System.out.println("Field isFinal: " + this.isFinal());
		System.out.println("Field access modifier: " + this.getAccessModifier());
		System.out.println("-----------------------");
	}
	
}
