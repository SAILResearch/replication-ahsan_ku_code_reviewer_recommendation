package com.sail.java.exam.topic.associate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import com.sail.java.exam.work.JavaExamTopicExtractor;
import com.sail.model.JavaOtherElementModel;
import com.sail.model.TopicExtractorBaseModel;
import com.sail.util.JDTUtil;

public class DataTypeKnowledgeExtractorOCA extends TopicExtractorBaseModel {
	public String filePath = "";
	public CompilationUnit cu = null;

	Map<String, ArrayList<JavaOtherElementModel>> variableDeclList = new HashMap<String, ArrayList<JavaOtherElementModel>>();

	public Map<String, ArrayList<JavaOtherElementModel>> getVariableDeclList() {
		return variableDeclList;
	}

	public void setVariableDeclList(Map<String, ArrayList<JavaOtherElementModel>> variableDeclList) {
		this.variableDeclList = variableDeclList;
	}

	public DataTypeKnowledgeExtractorOCA(String filePath, CompilationUnit cu) {
		this.filePath = filePath;
		this.cu = cu;
	}

	@Override
	public void visitImportDeclaration(ImportDeclaration node) {
		// TODO Auto-generated method stub
		// System.out.println(filePath +" Import: " + node.getName());
		String javaRootPackageName = JDTUtil.getJavaRootPackage(node.getName().toString());
		if (javaRootPackageName != null) {
			if (!JavaExamTopicExtractor.filesUseJavaPackageList.containsKey(javaRootPackageName)) {
				JavaExamTopicExtractor.filesUseJavaPackageList.put(javaRootPackageName, new HashSet<String>());
			}
			JavaExamTopicExtractor.filesUseJavaPackageList.get(javaRootPackageName).add(filePath);
		}
	}

	@Override
	public void visitVariableDeclarationStatement(VariableDeclarationStatement node) {
		// TODO Auto-generated method stub
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		JavaOtherElementModel ob = new JavaOtherElementModel();
		ob.setStartLine(startLine);
		ob.setEndLine(endLine);

		String varDeclType = JDTUtil.getVariableDeclarationType(node.getType());

		// System.out.println("Var: " + varDeclType + " " + startLine + " " +
		// node.getType() + " " + (node.getType() instanceof PrimitiveType));

		if (!variableDeclList.containsKey(varDeclType)) {
			variableDeclList.put(varDeclType, new ArrayList<JavaOtherElementModel>());
		}
		variableDeclList.get(varDeclType).add(ob);
	}

	public void extractTopic() {
		if (cu == null) {
			System.out.println("Problem in the Compilation Unit initialization...");
			return;
		}
		cu.accept(new ASTVisitor() {

			@Override
			public boolean visit(VariableDeclarationStatement node) {
				return super.visit(node);
			}
		});
	}

	public static void main(String[] args) {
		System.out.println("Program Finishes Successfully");
	}
}
