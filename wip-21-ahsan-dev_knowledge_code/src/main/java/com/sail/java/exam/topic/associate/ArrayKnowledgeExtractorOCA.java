package com.sail.java.exam.topic.associate;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import com.sail.model.JavaOtherElementModel;
import com.sail.model.TopicExtractorBaseModel;

public class ArrayKnowledgeExtractorOCA extends TopicExtractorBaseModel{
	public String filePath = "";
	public CompilationUnit cu = null;

	ArrayList<JavaOtherElementModel> oneDimArrayList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> twoDimArrayList = new ArrayList<JavaOtherElementModel>();

	public ArrayList<JavaOtherElementModel> getOneDimArrayList() {
		return oneDimArrayList;
	}

	public void setOneDimArrayList(ArrayList<JavaOtherElementModel> oneDimArrayList) {
		this.oneDimArrayList = oneDimArrayList;
	}

	public ArrayList<JavaOtherElementModel> getTwoDimArrayList() {
		return twoDimArrayList;
	}

	public void setTwoDimArrayList(ArrayList<JavaOtherElementModel> twoDimArrayList) {
		this.twoDimArrayList = twoDimArrayList;
	}

	public ArrayKnowledgeExtractorOCA(String filePath, CompilationUnit cu) {
		this.filePath = filePath;
		this.cu = cu;
	}

	@Override
	public void visitVariableDeclarationFragment(VariableDeclarationFragment node) {
		// TODO Auto-generated method stub
		if (node.getInitializer() instanceof ArrayCreation) {
			// TODO Auto-generated method stub
			int startLine = cu.getLineNumber(node.getStartPosition());
			int nodeLength = node.getLength();
			int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

			JavaOtherElementModel ob = new JavaOtherElementModel();
			ob.setStartLine(startLine);
			ob.setEndLine(endLine);

			if (node.getExtraDimensions() == 0) {
				ob.setName("OneDimArray");
				oneDimArrayList.add(ob);
			} else {
				ob.setName("MultiDimArray");
				twoDimArrayList.add(ob);
			}
			// System.out.println("[Array Fragment] " + node.getExtraDimensions() +" " +
			// node.getName() + node.getExtraDimensions());
		}
	}

	public void extractTopic() {
		if (cu == null) {
			System.out.println("Problem in the Compilation Unit initialization...");
			return;
		}
		cu.accept(new ASTVisitor() {
			@Override
			public boolean visit(VariableDeclarationFragment node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}
			@Override
			public boolean visit(ArrayAccess node) {
				int lineNumber = cu.getLineNumber(node.getStartPosition());
				// System.out.println("[Array Access] Line " + lineNumber + " " +
				// node.getArray());
				ITypeBinding arrayBinding = node.resolveTypeBinding();
				return super.visit(node);
			}
		});
	}

	public static void main(String[] args) {
		System.out.println("Program Finishes Successfully");
	}
}
