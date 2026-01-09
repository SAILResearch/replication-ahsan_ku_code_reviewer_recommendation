package com.sail.model;

import java.util.ArrayList;

public class JavaMethodModel extends BasicModel{

	
	public String methodName;
	public String returnType;
	public ArrayList<JavaMethodParameterModel> parameterList = new ArrayList<JavaMethodParameterModel>();
	public boolean isConstructor;
	public boolean isStaticMethod;
	public String accessModifierName;
	public boolean isFinal;
	public boolean isAbstract;
	public boolean isOverriddenMethod;
	public boolean isOverrideAnnotation;
	
	public String declaringClassName;
	
	
	
	public String getDeclaringClassName() {
		return declaringClassName;
	}
	public void setDeclaringClassName(String declaringClassName) {
		this.declaringClassName = declaringClassName;
	}
	public boolean isOverriddenMethod() {
		return isOverriddenMethod;
	}
	public void setOverriddenMethod(boolean isOverriddenMethod) {
		this.isOverriddenMethod = isOverriddenMethod;
	}
	public boolean isOverrideAnnotation() {
		return isOverrideAnnotation;
	}
	public void setOverrideAnnotation(boolean isOverrideAnnotation) {
		this.isOverrideAnnotation = isOverrideAnnotation;
	}
	public boolean isAbstract() {
		return isAbstract;
	}
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	public boolean isFinal() {
		return isFinal;
	}
	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public boolean isConstructor() {
		return isConstructor;
	}
	public void setConstructor(boolean isConstructor) {
		this.isConstructor = isConstructor;
	}
	public boolean isStaticMethod() {
		return isStaticMethod;
	}
	public void setStaticMethod(boolean isStaticMethod) {
		this.isStaticMethod = isStaticMethod;
	}
	public String getAccessModifierName() {
		return accessModifierName;
	}
	public void setAccessModifierName(String accessModifierName) {
		this.accessModifierName = accessModifierName;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public ArrayList<JavaMethodParameterModel> getParameterList() {
		return parameterList;
	}
	public void setParameterList(ArrayList<JavaMethodParameterModel> parameterList) {
		this.parameterList = parameterList;
	}
	public void summaryMethodInfo() {
		System.out.println("**********************");
		System.out.println("Method Name: " + this.methodName);
		System.out.println("Method Start Line: " + this.startLine);
		System.out.println("Method End Line: " + this.endLine);
		System.out.println("Is Constructor: " + this.isConstructor);
		System.out.println("Is Static: " + this.isStaticMethod);
		System.out.println("Total Parameters: " + this.getParameterList().size());
		System.out.println("Parameters: ");
		for(JavaMethodParameterModel mpm : this.getParameterList()) {
			mpm.summaryParameterInfo();
		}
		System.out.println("**********************");
	}
	
}
