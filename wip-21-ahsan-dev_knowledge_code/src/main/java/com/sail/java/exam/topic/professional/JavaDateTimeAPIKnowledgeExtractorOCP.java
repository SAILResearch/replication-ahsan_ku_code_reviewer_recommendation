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

public class JavaDateTimeAPIKnowledgeExtractorOCP extends TopicExtractorBaseModel {
	
	public String filePath = "";
	public CompilationUnit cu = null;
	
	Map<String, ArrayList<JavaOtherElementModel>> javaDateTimeFormatterList = new HashMap<String, ArrayList<JavaOtherElementModel>>();
	Map<String, ArrayList<JavaOtherElementModel>> javaLocalDateList = new HashMap<String, ArrayList<JavaOtherElementModel>>();

	Map<String, ArrayList<JavaOtherElementModel>> javaTimeInstantList = new HashMap<String, ArrayList<JavaOtherElementModel>>();
	Map<String, ArrayList<JavaOtherElementModel>> javaTimePeriodList = new HashMap<String, ArrayList<JavaOtherElementModel>>();
	Map<String, ArrayList<JavaOtherElementModel>> javaTimeDurationList = new HashMap<String, ArrayList<JavaOtherElementModel>>();

	public JavaDateTimeAPIKnowledgeExtractorOCP(String filePath, CompilationUnit cu) {
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
				//System.out.println("MI: " + qualifiedName + " " + node.getName() + " " + fullMethodName);
				JavaOtherElementModel ob = new JavaOtherElementModel();
				ob.setStartLine(startLine);
				ob.setEndLine(endLine);
				ob.setElementType("method-invocation");
				ob.setName(node.getName().toString());

				if (qualifiedName.compareTo(ConstantUtil.JAVA_DATE_TIME_FORMATTER) == 0) {
					if (!javaDateTimeFormatterList.containsKey(node.getName())) {
						javaDateTimeFormatterList.put(node.getName().toString(),
								new ArrayList<JavaOtherElementModel>());
					}
					javaDateTimeFormatterList.get(node.getName().toString()).add(ob);

				}

				if (qualifiedName.compareTo(ConstantUtil.JAVA_TIME_INSTANT) == 0) {
					if (!javaTimeInstantList.containsKey(node.getName())) {
						javaTimeInstantList.put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaTimeInstantList.get(node.getName().toString()).add(ob);

				}
				if (qualifiedName.compareTo(ConstantUtil.JAVA_TIME_PERIOD) == 0) {
					if (!javaTimePeriodList.containsKey(node.getName())) {
						javaTimePeriodList.put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaTimePeriodList.get(node.getName().toString()).add(ob);

				}
				if (qualifiedName.compareTo(ConstantUtil.JAVA_TIME_DURATION) == 0) {
					if (!javaTimeDurationList.containsKey(node.getName())) {
						javaTimeDurationList.put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
					}
					javaTimeDurationList.get(node.getName().toString()).add(ob);

				}

				for (String dateAPI : ConstantUtil.DATE_TIME_API) {
					if (qualifiedName.compareTo(dateAPI) == 0) {
						if (!javaLocalDateList.containsKey(fullMethodName)) {
							javaLocalDateList.put(fullMethodName, new ArrayList<JavaOtherElementModel>());
						}
						javaLocalDateList.get(fullMethodName.toString()).add(ob);
					}

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

				if (qualifiedName.compareTo(ConstantUtil.JAVA_DATE_TIME_FORMATTER) == 0) {
					if (!javaDateTimeFormatterList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						javaDateTimeFormatterList.put(ConstantUtil.CONSTRUCTOR.toString(),
								new ArrayList<JavaOtherElementModel>());
					}
					javaDateTimeFormatterList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);

				}
				for (String dateAPI : ConstantUtil.DATE_TIME_API) {
					if (qualifiedName.compareTo(dateAPI) == 0) {
						if (!javaLocalDateList.containsKey(ConstantUtil.CONSTRUCTOR)) {
							javaLocalDateList.put(ConstantUtil.CONSTRUCTOR, new ArrayList<JavaOtherElementModel>());
						}
						javaLocalDateList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
					}
				}

				if (qualifiedName.compareTo(ConstantUtil.JAVA_TIME_INSTANT) == 0) {
					if (!javaTimeInstantList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						javaTimeInstantList.put(ConstantUtil.CONSTRUCTOR.toString(),
								new ArrayList<JavaOtherElementModel>());
					}
					javaTimeInstantList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);

				}
				if (qualifiedName.compareTo(ConstantUtil.JAVA_TIME_PERIOD) == 0) {
					if (!javaTimePeriodList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						javaTimePeriodList.put(ConstantUtil.CONSTRUCTOR.toString(),
								new ArrayList<JavaOtherElementModel>());
					}
					javaTimePeriodList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);

				}
				if (qualifiedName.compareTo(ConstantUtil.JAVA_TIME_DURATION) == 0) {
					if (!javaTimeDurationList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						javaTimeDurationList.put(ConstantUtil.CONSTRUCTOR.toString(),
								new ArrayList<JavaOtherElementModel>());
					}
					javaTimeDurationList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);

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
				if (qualifiedName.compareTo(ConstantUtil.JAVA_DATE_TIME_FORMATTER) == 0) {
					if (!javaDateTimeFormatterList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						javaDateTimeFormatterList.put(ConstantUtil.CONSTRUCTOR.toString(),
								new ArrayList<JavaOtherElementModel>());
					}
					javaDateTimeFormatterList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);

				}
				for (String dateAPI : ConstantUtil.DATE_TIME_API) {
					if (qualifiedName.compareTo(dateAPI) == 0) {
						if (!javaLocalDateList.containsKey(ConstantUtil.CONSTRUCTOR)) {
							javaLocalDateList.put(ConstantUtil.CONSTRUCTOR, new ArrayList<JavaOtherElementModel>());
						}
						javaLocalDateList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
					}
				}

				if (qualifiedName.compareTo(ConstantUtil.JAVA_TIME_INSTANT) == 0) {
					if (!javaTimeInstantList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						javaTimeInstantList.put(ConstantUtil.CONSTRUCTOR.toString(),
								new ArrayList<JavaOtherElementModel>());
					}
					javaTimeInstantList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);

				}
				if (qualifiedName.compareTo(ConstantUtil.JAVA_TIME_PERIOD) == 0) {
					if (!javaTimePeriodList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						javaTimePeriodList.put(ConstantUtil.CONSTRUCTOR.toString(),
								new ArrayList<JavaOtherElementModel>());
					}
					javaTimePeriodList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);

				}
				if (qualifiedName.compareTo(ConstantUtil.JAVA_TIME_DURATION) == 0) {
					if (!javaTimeDurationList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						javaTimeDurationList.put(ConstantUtil.CONSTRUCTOR.toString(),
								new ArrayList<JavaOtherElementModel>());
					}
					javaTimeDurationList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);

				}

			}
		}
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getJavaDateTimeFormatterList() {
		return javaDateTimeFormatterList;
	}

	public void setJavaDateTimeFormatterList(Map<String, ArrayList<JavaOtherElementModel>> javaDateTimeFormatterList) {
		this.javaDateTimeFormatterList = javaDateTimeFormatterList;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getJavaLocalDateList() {
		return javaLocalDateList;
	}

	public void setJavaLocalDateList(Map<String, ArrayList<JavaOtherElementModel>> javaLocalDateList) {
		this.javaLocalDateList = javaLocalDateList;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getJavaTimeInstantList() {
		return javaTimeInstantList;
	}

	public void setJavaTimeInstantList(Map<String, ArrayList<JavaOtherElementModel>> javaTimeInstantList) {
		this.javaTimeInstantList = javaTimeInstantList;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getJavaTimePeriodList() {
		return javaTimePeriodList;
	}

	public void setJavaTimePeriodList(Map<String, ArrayList<JavaOtherElementModel>> javaTimePeriodList) {
		this.javaTimePeriodList = javaTimePeriodList;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getJavaTimeDurationList() {
		return javaTimeDurationList;
	}

	public void setJavaTimeDurationList(Map<String, ArrayList<JavaOtherElementModel>> javaTimeDurationList) {
		this.javaTimeDurationList = javaTimeDurationList;
	}
	
}
