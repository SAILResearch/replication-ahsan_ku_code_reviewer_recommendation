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
import org.eclipse.jdt.internal.core.JavaProject;

import com.sail.model.JavaOtherElementModel;
import com.sail.model.TopicExtractorBaseModel;
import com.sail.util.ConstantUtil;
import com.sail.util.TextUtil;

public class JavaStringProcessingKnowledgeExtractorOCP extends TopicExtractorBaseModel{
	
	public String filePath = "";
	public CompilationUnit cu = null;
	
	
	Map<String, ArrayList<JavaOtherElementModel>> javaStringAPIUsage = new HashMap<String, ArrayList<JavaOtherElementModel>>();
	
	
	Map<String, ArrayList<JavaOtherElementModel>> javaStringPattern = new HashMap<String, ArrayList<JavaOtherElementModel>>();
	Map<String, ArrayList<JavaOtherElementModel>> javaStringMatcher = new HashMap<String, ArrayList<JavaOtherElementModel>>();
	
	Map<String, ArrayList<JavaOtherElementModel>> javaStringFormatter = new HashMap<String, ArrayList<JavaOtherElementModel>>();
	Map<String, ArrayList<JavaOtherElementModel>> javaDecimalFormatter = new HashMap<String, ArrayList<JavaOtherElementModel>>();
	
	
	public JavaStringProcessingKnowledgeExtractorOCP(String filePath, CompilationUnit cu) {
		this.filePath = filePath;
		this.cu = cu;
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
				// System.out.println("MI: " + qualifiedName + " " + node.getName() + " " +
				// fullMethodName);
				JavaOtherElementModel ob = new JavaOtherElementModel();
				ob.setStartLine(startLine);
				ob.setEndLine(endLine);
				ob.setElementType("method-invocation");
				ob.setName(node.getName().toString());

				if(qualifiedName.compareTo(ConstantUtil.JAVA_PATTERN_CLASS) == 0) {
					if(!javaStringPattern.containsKey(node.getName().toString())) {
						javaStringPattern.put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaStringPattern.get(node.getName().toString()).add(ob);
				}
				if(qualifiedName.compareTo(ConstantUtil.JAVA_MATCHER_CLASS) == 0) {
					if(!javaStringMatcher.containsKey(node.getName().toString())) {
						javaStringMatcher.put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaStringMatcher.get(node.getName().toString()).add(ob);
				}
				
				if(qualifiedName.compareTo(ConstantUtil.DECIMAL_FORMATTER) == 0) {
					if(!javaDecimalFormatter.containsKey(node.getName().toString())) {
						javaDecimalFormatter.put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaDecimalFormatter.get(node.getName().toString()).add(ob);
				}
				if(qualifiedName.compareTo(ConstantUtil.STRING_CLASS) == 0 && node.getName().toString().compareTo("format") == 0) {
					if(!javaStringFormatter.containsKey(node.getName().toString())) {
						javaStringFormatter.put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaStringFormatter.get(node.getName().toString()).add(ob);
				}
				
				if(qualifiedName.compareTo(ConstantUtil.STRING_CLASS) == 0) {
					if(!javaStringAPIUsage.containsKey(node.getName().toString())) {
						javaStringAPIUsage.put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaStringAPIUsage.get(node.getName().toString()).add(ob);
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
				if(qualifiedName.compareTo(ConstantUtil.JAVA_PATTERN_CLASS) == 0) {
					if(!javaStringPattern.containsKey(ConstantUtil.CONSTRUCTOR.toString())) {
						javaStringPattern.put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaStringPattern.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
				}
				if(qualifiedName.compareTo(ConstantUtil.JAVA_MATCHER_CLASS) == 0) {
					if(!javaStringMatcher.containsKey(ConstantUtil.CONSTRUCTOR.toString())) {
						javaStringMatcher.put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaStringMatcher.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
				}
				
				if(qualifiedName.compareTo(ConstantUtil.DECIMAL_FORMATTER) == 0) {
					if(!javaDecimalFormatter.containsKey(ConstantUtil.CONSTRUCTOR.toString())) {
						javaDecimalFormatter.put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaDecimalFormatter.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
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
				if(qualifiedName.compareTo(ConstantUtil.JAVA_PATTERN_CLASS) == 0) {
					if(!javaStringPattern.containsKey(ConstantUtil.CONSTRUCTOR.toString())) {
						javaStringPattern.put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaStringPattern.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
				}
				if(qualifiedName.compareTo(ConstantUtil.JAVA_MATCHER_CLASS) == 0) {
					if(!javaStringMatcher.containsKey(ConstantUtil.CONSTRUCTOR.toString())) {
						javaStringMatcher.put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaStringMatcher.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
				}
				
				if(qualifiedName.compareTo(ConstantUtil.DECIMAL_FORMATTER) == 0) {
					if(!javaDecimalFormatter.containsKey(ConstantUtil.CONSTRUCTOR.toString())) {
						javaDecimalFormatter.put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaDecimalFormatter.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
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

	public Map<String, ArrayList<JavaOtherElementModel>> getJavaStringAPIUsage() {
		return javaStringAPIUsage;
	}

	public void setJavaStringAPIUsage(Map<String, ArrayList<JavaOtherElementModel>> javaStringAPIUsage) {
		this.javaStringAPIUsage = javaStringAPIUsage;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getJavaStringPattern() {
		return javaStringPattern;
	}

	public void setJavaStringPattern(Map<String, ArrayList<JavaOtherElementModel>> javaStringPattern) {
		this.javaStringPattern = javaStringPattern;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getJavaStringMatcher() {
		return javaStringMatcher;
	}

	public void setJavaStringMatcher(Map<String, ArrayList<JavaOtherElementModel>> javaStringMatcher) {
		this.javaStringMatcher = javaStringMatcher;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getJavaStringFormatter() {
		return javaStringFormatter;
	}

	public void setJavaStringFormatter(Map<String, ArrayList<JavaOtherElementModel>> javaStringFormatter) {
		this.javaStringFormatter = javaStringFormatter;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getJavaDecimalFormatter() {
		return javaDecimalFormatter;
	}

	public void setJavaDecimalFormatter(Map<String, ArrayList<JavaOtherElementModel>> javaDecimalFormatter) {
		this.javaDecimalFormatter = javaDecimalFormatter;
	}
}
