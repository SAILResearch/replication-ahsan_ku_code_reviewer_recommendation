package com.sail.java.exam.topic.professional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import com.sail.model.JavaOtherElementModel;
import com.sail.model.TopicExtractorBaseModel;
import com.sail.util.ConstantUtil;
import com.sail.util.TextUtil;

public class LocalizationKnowledgeExctractorOCP extends TopicExtractorBaseModel {
	public String filePath = "";
	public CompilationUnit cu = null;

	Map<String, ArrayList<JavaOtherElementModel>> javaLocaleList = new HashMap<String, ArrayList<JavaOtherElementModel>>();
	Map<String, ArrayList<JavaOtherElementModel>> javaResourceBuilderList = new HashMap<String, ArrayList<JavaOtherElementModel>>();
	Map<String, ArrayList<JavaOtherElementModel>> javaPropertyAccessList = new HashMap<String, ArrayList<JavaOtherElementModel>>();
	
	
	public LocalizationKnowledgeExctractorOCP(String filePath, CompilationUnit cu) {
		this.cu = cu;
		this.filePath = filePath;
	}

	public void visitMethodInvocation(MethodInvocation node) {
		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
		IMethodBinding methodBinding = node.resolveMethodBinding();
		if (methodBinding != null) {
			ITypeBinding declareClassType = methodBinding.getDeclaringClass();
			// System.out.println("Declaring class: " + declareClassType.);
			if (declareClassType != null) {
				String qualifiedName = declareClassType.getQualifiedName();
				if (qualifiedName.contains("<")) {
					qualifiedName = qualifiedName.substring(0, qualifiedName.indexOf("<"));
				}
				String fullMethodName = qualifiedName + TextUtil.DOT_SEPERATOR + node.getName();
				//System.out.println("MI: " + qualifiedName + " " + node.getName() + " " + fullMethodName);
				JavaOtherElementModel ob = new JavaOtherElementModel();
				ob.setStartLine(startLine);
				ob.setEndLine(endLine);
				ob.setElementType("method-invocation");
				ob.setName(node.getName().toString());

				if (qualifiedName.compareTo(ConstantUtil.JAVA_LOCALE) == 0) {
					if (!javaLocaleList.containsKey(node.getName())) {
						javaLocaleList.put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaLocaleList.get(node.getName().toString()).add(ob);

				}
				if (qualifiedName.compareTo(ConstantUtil.PROPERTY_API) == 0) {
					if (!javaPropertyAccessList.containsKey(node.getName())) {
						javaPropertyAccessList.put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaPropertyAccessList.get(node.getName().toString()).add(ob);
				}
				
				if (qualifiedName.compareTo(ConstantUtil.JAVA_RESOURCE_BUILDER) == 0) {
					if (!javaResourceBuilderList.containsKey(node.getName())) {
						javaResourceBuilderList.put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaResourceBuilderList.get(node.getName().toString()).add(ob);
				}
				
			}
		}
	}

	public void visitVariableDeclarationStatement(VariableDeclarationStatement node) {
		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
		if (node.getType() != null) {
			ITypeBinding typeBinding = node.getType().resolveBinding();
			if (typeBinding != null) {
				JavaOtherElementModel ob = new JavaOtherElementModel();
				ob.setStartLine(startLine);
				ob.setEndLine(endLine);
				ob.setElementType("callable-instance-creation");
				ob.setName(node.toString());
				String qualifiedName = typeBinding.getQualifiedName();
				if (qualifiedName.contains("<")) {
					qualifiedName = qualifiedName.substring(0, qualifiedName.indexOf("<"));
				}
				if (qualifiedName.compareTo(ConstantUtil.JAVA_LOCALE) == 0) {
					if (!javaLocaleList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						javaLocaleList.put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaLocaleList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);

				}
				if (qualifiedName.compareTo(ConstantUtil.PROPERTY_API) == 0) {
					if (!javaPropertyAccessList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						javaPropertyAccessList.put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaPropertyAccessList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
				}
				if (qualifiedName.compareTo(ConstantUtil.JAVA_RESOURCE_BUILDER) == 0) {
					if (!javaResourceBuilderList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						javaResourceBuilderList.put(ConstantUtil.CONSTRUCTOR.toString(),
								new ArrayList<JavaOtherElementModel>());
					}
					javaResourceBuilderList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
				}
			}
		}
	}

	public void visitFieldDeclaration(FieldDeclaration node) {
		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
		if (node.getType() != null) {
			ITypeBinding typeBinding = node.getType().resolveBinding();
			if (typeBinding != null) {
				String fullyQualifiedName = typeBinding.getQualifiedName();
				JavaOtherElementModel ob = new JavaOtherElementModel();
				ob.setStartLine(startLine);
				ob.setEndLine(endLine);
				ob.setElementType("callable-instance-creation");
				ob.setName(node.toString());
				String qualifiedName = typeBinding.getQualifiedName();
				if (qualifiedName.contains("<")) {
					qualifiedName = fullyQualifiedName.substring(0, fullyQualifiedName.indexOf("<"));
				}
				if (qualifiedName.compareTo(ConstantUtil.JAVA_LOCALE) == 0) {
					if (!javaLocaleList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						javaLocaleList.put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaLocaleList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);

				}
				if (qualifiedName.compareTo(ConstantUtil.PROPERTY_API) == 0) {
					if (!javaPropertyAccessList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						javaPropertyAccessList.put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaPropertyAccessList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
				}
				if (qualifiedName.compareTo(ConstantUtil.JAVA_RESOURCE_BUILDER) == 0) {
					if (!javaResourceBuilderList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						javaResourceBuilderList.put(ConstantUtil.CONSTRUCTOR.toString(),
								new ArrayList<JavaOtherElementModel>());
					}
					javaResourceBuilderList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
				}

			}
		}
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public CompilationUnit getCu() {
		return cu;
	}

	public void setCu(CompilationUnit cu) {
		this.cu = cu;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getJavaLocaleList() {
		return javaLocaleList;
	}

	public void setJavaLocaleList(Map<String, ArrayList<JavaOtherElementModel>> javaLocaleList) {
		this.javaLocaleList = javaLocaleList;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getJavaResourceBuilderList() {
		return javaResourceBuilderList;
	}

	public void setJavaResourceBuilderList(Map<String, ArrayList<JavaOtherElementModel>> javaResourceBuilderList) {
		this.javaResourceBuilderList = javaResourceBuilderList;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getJavaPropertyAccessList() {
		return javaPropertyAccessList;
	}

	public void setJavaPropertyAccessList(Map<String, ArrayList<JavaOtherElementModel>> javaPropertyAccessList) {
		this.javaPropertyAccessList = javaPropertyAccessList;
	}
	
}
