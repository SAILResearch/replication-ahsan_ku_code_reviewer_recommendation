package com.sail.java.exam.topic.associate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import com.sail.model.JavaOtherElementModel;
import com.sail.model.TopicExtractorBaseModel;

public class LoopKnowledgeExtractorOCA extends TopicExtractorBaseModel{
	public String filePath = "";
	public CompilationUnit cu = null;

	ArrayList<JavaOtherElementModel> doWhileList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> forList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> whileList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> continueList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> breakList = new ArrayList<JavaOtherElementModel>();

	public ArrayList<JavaOtherElementModel> getDoWhileList() {
		return doWhileList;
	}

	public void setDoWhileList(ArrayList<JavaOtherElementModel> doWhileList) {
		this.doWhileList = doWhileList;
	}

	public ArrayList<JavaOtherElementModel> getForList() {
		return forList;
	}

	public void setForList(ArrayList<JavaOtherElementModel> forList) {
		this.forList = forList;
	}

	public ArrayList<JavaOtherElementModel> getWhileList() {
		return whileList;
	}

	public void setWhileList(ArrayList<JavaOtherElementModel> whileList) {
		this.whileList = whileList;
	}

	public ArrayList<JavaOtherElementModel> getContinueList() {
		return continueList;
	}

	public void setContinueList(ArrayList<JavaOtherElementModel> continueList) {
		this.continueList = continueList;
	}

	public ArrayList<JavaOtherElementModel> getBreakList() {
		return breakList;
	}

	public void setBreakList(ArrayList<JavaOtherElementModel> breakList) {
		this.breakList = breakList;
	}

	public LoopKnowledgeExtractorOCA(String filePath, CompilationUnit cu) {
		this.filePath = filePath;
		this.cu = cu;
	}

	@Override
	public void visitDoStatement(DoStatement node) {
		// TODO Auto-generated method stub
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		JavaOtherElementModel ob = new JavaOtherElementModel();
		ob.setStartLine(startLine);
		ob.setEndLine(endLine);
		ob.setName("DoWhile");
		doWhileList.add(ob);
		// System.out.println("[Do/while Statement] " + node);
	}

	@Override
	public void visitForStatement(ForStatement node) {
		// TODO Auto-generated method stub
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		JavaOtherElementModel ob = new JavaOtherElementModel();
		ob.setStartLine(startLine);
		ob.setEndLine(endLine);
		ob.setName("ForStatement");
		forList.add(ob);
	}

	@Override
	public void visitWhileStatement(WhileStatement node) {
		// TODO Auto-generated method stub
		// System.out.println("[While Statement] " + node);
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		JavaOtherElementModel ob = new JavaOtherElementModel();
		ob.setStartLine(startLine);
		ob.setEndLine(endLine);
		ob.setName("whileStatement");
		whileList.add(ob);
	}

	
	@Override
	public void visitBreakStatement(BreakStatement node) {
		// TODO Auto-generated method stub
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		JavaOtherElementModel ob = new JavaOtherElementModel();
		ob.setStartLine(startLine);
		ob.setEndLine(endLine);
		ob.setName("BreakStatement");
		breakList.add(ob);
		// System.out.println("[Break Statement] " + node);
	}

	@Override
	public void visitContinueStatement(ContinueStatement node) {
		// TODO Auto-generated method stub
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		JavaOtherElementModel ob = new JavaOtherElementModel();
		ob.setStartLine(startLine);
		ob.setEndLine(endLine);
		ob.setName("continueStatement");
		continueList.add(ob);
		// System.out.println("[Continue Statement] " + node);
	}

	public void extractTopic() {
		if (cu == null) {
			System.out.println("Problem in the Compilation Unit initialization...");
			return;
		}
		cu.accept(new ASTVisitor() {
			@Override
			public boolean visit(DoStatement node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(ForStatement node) {
				// TODO Auto-generated method stub
				
				return super.visit(node);
			}

			@Override
			public boolean visit(WhileStatement node) {
				// TODO Auto-generated method stub
				
				return super.visit(node);
			}

			@Override
			public boolean visit(BreakStatement node) {
				// TODO Auto-generated method stub
				
				return super.visit(node);
			}

			@Override
			public boolean visit(ContinueStatement node) {
				// TODO Auto-generated method stub
				
				return super.visit(node);
			}
		});
	}

	public static void main(String[] args) {
		System.out.println("Program Finishes Successfully");
	}
}
