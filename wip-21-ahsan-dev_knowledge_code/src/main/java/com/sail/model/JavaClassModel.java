package com.sail.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sail.util.JDTUtil;

public class JavaClassModel extends BasicModel{

	public String packageName;
	public String classFilePath;
	public String className;
	public boolean isFinal;
	public boolean isStatic;
	public String accessModifier;
	public boolean isChildClass;
	public boolean isInterface;
	public boolean isAbstract;
	public boolean isAnonymousClass;
	public String superClassQualifiedName;
	public boolean isEnum;

	Map<String, JavaFieldDeclarationModel> classFieldList;
	Map<String, ArrayList<JavaMethodModel>> methodList; // Key = fullyqualifiedclassname + methodName, avlue = list of
														// JavaMethodModel

	public JavaClassModel() {
		classFieldList = new HashMap<String, JavaFieldDeclarationModel>();
		methodList = new HashMap<String, ArrayList<JavaMethodModel>>();
	}

	public boolean isEnum() {
		return isEnum;
	}

	public void setEnum(boolean isEnum) {
		this.isEnum = isEnum;
	}

	public String getSuperClassQualifiedName() {
		return superClassQualifiedName;
	}

	public void setSuperClassQualifiedName(String superClassQualifiedName) {
		this.superClassQualifiedName = superClassQualifiedName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean isAnonymousClass() {
		return isAnonymousClass;
	}

	public void setAnonymousClass(boolean isAnonymousClass) {
		this.isAnonymousClass = isAnonymousClass;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

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

	public String getClassFilePath() {
		return classFilePath;
	}

	public void setClassFilePath(String classFilePath) {
		this.classFilePath = classFilePath;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isChildClass() {
		return isChildClass;
	}

	public void setChildClass(boolean isChildClass) {
		this.isChildClass = isChildClass;
	}

	public boolean isInterface() {
		return isInterface;
	}

	public void setInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public Map<String, JavaFieldDeclarationModel> getClassFieldList() {
		return classFieldList;
	}

	public void setClassFieldList(Map<String, JavaFieldDeclarationModel> classFieldList) {
		this.classFieldList = classFieldList;
	}

	public Map<String, ArrayList<JavaMethodModel>> getMethodList() {
		return methodList;
	}

	public void setMethodList(Map<String, ArrayList<JavaMethodModel>> methodList) {
		this.methodList = methodList;
	}
	
	public int totalDeclaredMethods() {
		int total = 0 ;
		for(String methodName : methodList.keySet()) {
			total += methodList.get(methodName).size();
		}
		return total;
	}

	public boolean isAllFieldPrivate() {
		for (String fieldName : this.getClassFieldList().keySet()) {
			if (this.getClassFieldList().get(fieldName).getAccessModifier().compareTo(JDTUtil.PRIVATE) != 0) {
				return false;
			}
		}
		return true;
	}

}
