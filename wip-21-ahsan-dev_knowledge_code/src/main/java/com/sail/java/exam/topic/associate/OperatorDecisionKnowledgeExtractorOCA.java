package com.sail.java.exam.topic.associate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import com.sail.model.JavaOtherElementModel;
import com.sail.model.TopicExtractorBaseModel;
import com.sail.util.JDTUtil;

public class OperatorDecisionKnowledgeExtractorOCA extends TopicExtractorBaseModel{
	public String filePath = "";
	public CompilationUnit cu = null;

	ArrayList<JavaOtherElementModel> ifElseStatement = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> switchStatement = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> ternaryStatement = new ArrayList<JavaOtherElementModel>();
	Map<String, ArrayList<JavaOtherElementModel>> operatorList = new HashMap<String, ArrayList<JavaOtherElementModel>>();

	public ArrayList<JavaOtherElementModel> getIfElseStatement() {
		return ifElseStatement;
	}

	public void setIfElseStatement(ArrayList<JavaOtherElementModel> ifElseStatement) {
		this.ifElseStatement = ifElseStatement;
	}

	public ArrayList<JavaOtherElementModel> getSwitchStatement() {
		return switchStatement;
	}

	public void setSwitchStatement(ArrayList<JavaOtherElementModel> switchStatement) {
		this.switchStatement = switchStatement;
	}

	public ArrayList<JavaOtherElementModel> getTernaryStatement() {
		return ternaryStatement;
	}

	public void setTernaryStatement(ArrayList<JavaOtherElementModel> ternaryStatement) {
		this.ternaryStatement = ternaryStatement;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getOperatorList() {
		return operatorList;
	}

	public void setOperatorList(Map<String, ArrayList<JavaOtherElementModel>> operatorList) {
		this.operatorList = operatorList;
	}

	public OperatorDecisionKnowledgeExtractorOCA(String filePath, CompilationUnit cu) {
		this.filePath = filePath;
		this.cu = cu;
	}

	@Override
	public void visitAssignment(Assignment node) {
		// TODO Auto-generated method stub
		int lineNumber = cu.getLineNumber(node.getStartPosition());
		// System.out.println("[Assignment] " + node);
		String operatorName = JDTUtil.ASSIGNMENT_OPERATOR;
		// System.out.println("Operator " + operatorName);
		// System.out.println("[IF Statement] " + node.getExpression());
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		JavaOtherElementModel ob = new JavaOtherElementModel();
		ob.setStartLine(startLine);
		ob.setEndLine(endLine);

		if (!operatorList.containsKey(operatorName)) {
			operatorList.put(operatorName, new ArrayList<JavaOtherElementModel>());
		}
		operatorList.get(operatorName).add(ob);
	}

	@Override
	public void visitPostfixExpression(PostfixExpression node) {
		// TODO Auto-generated method stub
		int lineNumber = cu.getLineNumber(node.getStartPosition());
		// System.out.println("[Postfix] " + node);
		String operatorName = JDTUtil.POSTFIX_OPERATOR;
		// System.out.println("Operator " + operatorName);
		// System.out.println("[IF Statement] " + node.getExpression());
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		JavaOtherElementModel ob = new JavaOtherElementModel();
		ob.setStartLine(startLine);
		ob.setEndLine(endLine);

		if (!operatorList.containsKey(operatorName)) {
			operatorList.put(operatorName, new ArrayList<JavaOtherElementModel>());
		}
		operatorList.get(operatorName).add(ob);
	}
	
	

	@Override
	public void visitPrefixExpression(PrefixExpression node) {
		// TODO Auto-generated method stub
		int lineNumber = cu.getLineNumber(node.getStartPosition());
		// System.out.println("[Prefix] " + node);
		String operatorName = JDTUtil.PREFIX_OPERATOR;
		// System.out.println("Operator " + operatorName);

		// System.out.println("[IF Statement] " + node.getExpression());
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		JavaOtherElementModel ob = new JavaOtherElementModel();
		ob.setStartLine(startLine);
		ob.setEndLine(endLine);

		if (!operatorList.containsKey(operatorName)) {
			operatorList.put(operatorName, new ArrayList<JavaOtherElementModel>());
		}
		operatorList.get(operatorName).add(ob);
	}

	@Override
	public void visitInfixExpression(InfixExpression node) {
		// TODO Auto-generated method stub
		int lineNumber = cu.getLineNumber(node.getStartPosition());
		//System.out.println("[Infix] " + node);
		String operatorName = JDTUtil.getOperatorName(node.getOperator().toString());

		// System.out.println("[IF Statement] " + node.getExpression());
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		JavaOtherElementModel ob = new JavaOtherElementModel();
		ob.setStartLine(startLine);
		ob.setEndLine(endLine);

		if (!operatorList.containsKey(operatorName)) {
			operatorList.put(operatorName, new ArrayList<JavaOtherElementModel>());
		}
		operatorList.get(operatorName).add(ob);
	}

	@Override
	public void visitIfStatement(IfStatement node) {
		// TODO Auto-generated method stub
		int lineNumber = cu.getLineNumber(node.getStartPosition());
		// System.out.println("[IF Statement] " + node.getExpression());
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		JavaOtherElementModel ob = new JavaOtherElementModel();
		ob.setStartLine(startLine);
		ob.setEndLine(endLine);
		ob.setName("IfElse");
		ifElseStatement.add(ob);
	}

	@Override
	public void visitConditionalExpression(ConditionalExpression node) {
		// TODO Auto-generated method stub
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		JavaOtherElementModel ob = new JavaOtherElementModel();
		ob.setStartLine(startLine);
		ob.setEndLine(endLine);
		ob.setName("Ternary");
		ternaryStatement.add(ob);
	}
	
	@Override
	public void visitSwitchStatement(SwitchStatement node) {
		// TODO Auto-generated method stub
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		JavaOtherElementModel ob = new JavaOtherElementModel();
		ob.setStartLine(startLine);
		ob.setEndLine(endLine);
		ob.setName("Switch");
		switchStatement.add(ob);
	}

	public void extractTopic() {
		if (cu == null) {
			System.out.println("Problem in the Compilation Unit initialization...");
			return;
		}
		cu.accept(new ASTVisitor() {
			@Override
			public boolean visit(Assignment node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(PostfixExpression node) {
				// TODO Auto-generated method stub
				
				return super.visit(node);
			}

			@Override
			public boolean visit(PrefixExpression node) {
				// TODO Auto-generated method stub
				

				return super.visit(node);
			}

			public boolean visit(MethodDeclaration node) {
				int lineNumber = cu.getLineNumber(node.getStartPosition());
				// System.out.println("Method Name: " + node.getName());

				return super.visit(node);
			}

			@Override
			public boolean visit(InfixExpression node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			/*
			 * Conditional statements
			 */

			@Override
			public boolean visit(IfStatement node) {
				return super.visit(node);
			}

			@Override
			public boolean visit(ConditionalExpression node) {
				return super.visit(node);
			}

			@Override
			public boolean visit(SwitchStatement node) {
				// TODO Auto-generated method stub
				// System.out.println("[Switch Statement] " + node.getExpression());
				return super.visit(node);
			}
		});
	}

	public static void main(String[] args) {
		System.out.println("Program Finishes Successfully");
	}
}
