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

public class JDBCKnowledgeExtractorOCP extends TopicExtractorBaseModel{
	public String filePath = "";
	public CompilationUnit cu = null;
	
	public Map<String,ArrayList<JavaOtherElementModel>> sqlConnectionList = new HashMap<String,ArrayList<JavaOtherElementModel>>();
	public Map<String,ArrayList<JavaOtherElementModel>> sqlStatementList = new HashMap<String,ArrayList<JavaOtherElementModel>>();
	public Map<String,ArrayList<JavaOtherElementModel>> driverManagerList = new HashMap<String,ArrayList<JavaOtherElementModel>>();
	public Map<String,ArrayList<JavaOtherElementModel>> sqlResultSetList = new HashMap<String,ArrayList<JavaOtherElementModel>>();
	
	public JDBCKnowledgeExtractorOCP (String filePath, CompilationUnit cu) {
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
				
				if(qualifiedName.compareTo(ConstantUtil.DRIVER_MANAGER) == 0) {
					if(!driverManagerList.containsKey(node.getName())) {
						driverManagerList.put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
					}
					driverManagerList.get(node.getName().toString()).add(ob);
				}
				if(qualifiedName.compareTo(ConstantUtil.SQL_CONNECTION) == 0) {
					if(!sqlConnectionList.containsKey(node.getName())) {
						sqlConnectionList.put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
					}
					sqlConnectionList.get(node.getName().toString()).add(ob);
				}
				if(qualifiedName.compareTo(ConstantUtil.SQL_STATEMENT) == 0) {
					if(!sqlStatementList.containsKey(node.getName())) {
						sqlStatementList.put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
					}
					sqlStatementList.get(node.getName().toString()).add(ob);
				}
				if(qualifiedName.compareTo(ConstantUtil.SQL_RESULTSET) == 0) {
					if(!sqlResultSetList.containsKey(node.getName())) {
						sqlResultSetList.put(node.getName().toString(), new ArrayList<JavaOtherElementModel>());
					}
					sqlResultSetList.get(node.getName().toString()).add(ob);
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
				if(qualifiedName.compareTo(ConstantUtil.DRIVER_MANAGER) == 0) {
					if(!driverManagerList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						driverManagerList.put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
					}
					driverManagerList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
				}
				if(qualifiedName.compareTo(ConstantUtil.SQL_CONNECTION) == 0) {
					if(!sqlConnectionList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						sqlConnectionList.put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
					}
					sqlConnectionList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
				}
				if(qualifiedName.compareTo(ConstantUtil.SQL_STATEMENT) == 0) {
					if(!sqlStatementList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						sqlStatementList.put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
					}
					sqlStatementList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
				}
				if(qualifiedName.compareTo(ConstantUtil.SQL_RESULTSET) == 0) {
					if(!sqlResultSetList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						sqlResultSetList.put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
					}
					sqlResultSetList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
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
				if(qualifiedName.compareTo(ConstantUtil.DRIVER_MANAGER) == 0) {
					if(!driverManagerList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						driverManagerList.put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
					}
					driverManagerList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
				}
				if(qualifiedName.compareTo(ConstantUtil.SQL_CONNECTION) == 0) {
					if(!sqlConnectionList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						sqlConnectionList.put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
					}
					sqlConnectionList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
				}
				if(qualifiedName.compareTo(ConstantUtil.SQL_STATEMENT) == 0) {
					if(!sqlStatementList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						sqlStatementList.put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
					}
					sqlStatementList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
				}
				if(qualifiedName.compareTo(ConstantUtil.SQL_RESULTSET) == 0) {
					if(!sqlResultSetList.containsKey(ConstantUtil.CONSTRUCTOR)) {
						sqlResultSetList.put(ConstantUtil.CONSTRUCTOR.toString(), new ArrayList<JavaOtherElementModel>());
					}
					sqlResultSetList.get(ConstantUtil.CONSTRUCTOR.toString()).add(ob);
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

	public Map<String, ArrayList<JavaOtherElementModel>> getSqlConnectionList() {
		return sqlConnectionList;
	}

	public void setSqlConnectionList(Map<String, ArrayList<JavaOtherElementModel>> sqlConnectionList) {
		this.sqlConnectionList = sqlConnectionList;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getSqlStatementList() {
		return sqlStatementList;
	}

	public void setSqlStatementList(Map<String, ArrayList<JavaOtherElementModel>> sqlStatementList) {
		this.sqlStatementList = sqlStatementList;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getDriverManagerList() {
		return driverManagerList;
	}

	public void setDriverManagerList(Map<String, ArrayList<JavaOtherElementModel>> driverManagerList) {
		this.driverManagerList = driverManagerList;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getSqlResultSetList() {
		return sqlResultSetList;
	}

	public void setSqlResultSetList(Map<String, ArrayList<JavaOtherElementModel>> sqlResultSetList) {
		this.sqlResultSetList = sqlResultSetList;
	}
}
