package com.sail.java.exam.topic.associate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import com.sail.java.exam.work.OverrideDetector;
import com.sail.model.JavaClassModel;
import com.sail.model.JavaOtherElementModel;
import com.sail.model.TopicExtractorBaseModel;

public class HandlindExceptionKnowledgeExtractorOCA extends TopicExtractorBaseModel{
	public String filePath = "";
	public CompilationUnit cu = null;

	ArrayList<JavaOtherElementModel> tryCatchBlockList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> methodThrowException = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> multipleCatchBlock = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> multipleMethodThrowException = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> otherExceptionCatch = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> widerExceptionType = new ArrayList<JavaOtherElementModel>();

	public HandlindExceptionKnowledgeExtractorOCA(String filePath, CompilationUnit cu) {
		this.filePath = filePath;
		this.cu = cu;
	}

	
	
	public ArrayList<JavaOtherElementModel> getTryCatchBlockList() {
		return tryCatchBlockList;
	}



	public void setTryCatchBlockList(ArrayList<JavaOtherElementModel> tryCatchBlockList) {
		this.tryCatchBlockList = tryCatchBlockList;
	}



	public ArrayList<JavaOtherElementModel> getMethodThrowException() {
		return methodThrowException;
	}



	public void setMethodThrowException(ArrayList<JavaOtherElementModel> methodThrowException) {
		this.methodThrowException = methodThrowException;
	}



	public ArrayList<JavaOtherElementModel> getMultipleCatchBlock() {
		return multipleCatchBlock;
	}
	
	public void setMultipleCatchBlock(ArrayList<JavaOtherElementModel> multipleCatchBlock) {
		this.multipleCatchBlock = multipleCatchBlock;
	}
	
	public ArrayList<JavaOtherElementModel> getMultipleMethodThrowException() {
		return multipleMethodThrowException;
	}



	public void setMultipleMethodThrowException(ArrayList<JavaOtherElementModel> multipleMethodThrowException) {
		this.multipleMethodThrowException = multipleMethodThrowException;
	}



	public ArrayList<JavaOtherElementModel> getOtherExceptionCatch() {
		return otherExceptionCatch;
	}



	public void setOtherExceptionCatch(ArrayList<JavaOtherElementModel> otherExceptionCatch) {
		this.otherExceptionCatch = otherExceptionCatch;
	}



	public ArrayList<JavaOtherElementModel> getWiderExceptionType() {
		return widerExceptionType;
	}



	public void setWiderExceptionType(ArrayList<JavaOtherElementModel> widerExceptionType) {
		this.widerExceptionType = widerExceptionType;
	}

	@Override
	public void visitMethodDeclaration(MethodDeclaration node) {
		// TODO Auto-generated method stub


		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		IMethodBinding methodBinding = node.resolveBinding();

		if (methodBinding != null) {

			ITypeBinding classTypeBinding = methodBinding.getDeclaringClass();
			Set<String> subClassExceptionTypes = new HashSet<String>();

			if (methodBinding.getExceptionTypes().length > 0) {
				JavaOtherElementModel throwElement = new JavaOtherElementModel();
				throwElement.setStartLine(startLine);
				throwElement.setEndLine(startLine);
				throwElement.setName("MethodThrowException");
				methodThrowException.add(throwElement);
			}

			for (ITypeBinding itb : methodBinding.getExceptionTypes()) {
				subClassExceptionTypes.add(itb.getQualifiedName());
				//System.out.println("Subclass Exception: " + itb.getQualifiedName());
			}

			if (methodBinding.getExceptionTypes().length > 1) {
				JavaOtherElementModel multipleThrowsException = new JavaOtherElementModel();
				multipleThrowsException.setStartLine(startLine);
				multipleThrowsException.setEndLine(endLine);
				multipleThrowsException.setName("MultipleThrowException");
				multipleMethodThrowException.add(multipleThrowsException);
			}

			OverrideDetector overrideDetector = OverrideDetector.getInstance();
			IMethodBinding overriddenMethodBinding = overrideDetector.findOverrideInHierarchy(methodBinding,
					classTypeBinding);
			Set<String> superClassThrowException = new HashSet<String>();
			if (overriddenMethodBinding != null) {
				for (ITypeBinding itb : overriddenMethodBinding.getExceptionTypes()) {
					superClassThrowException.add(itb.getQualifiedName());
					//System.out.println("Superclass Exception: " + itb.getQualifiedName());
				}
				if (superClassThrowException.contains("java.lang.Exception")
						&& !subClassExceptionTypes.contains("java.lang.Exception")) {

					JavaOtherElementModel multipleThrowsException = new JavaOtherElementModel();
					multipleThrowsException.setStartLine(startLine);
					multipleThrowsException.setEndLine(endLine);
					multipleThrowsException.setName("WiderExceptionType");
					widerExceptionType.add(multipleThrowsException);

				}
			}

		}
	}

	@Override
	public void visitTryStatement(TryStatement node) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub

		JavaOtherElementModel tryModel = new JavaOtherElementModel();

		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		tryModel.setStartLine(startLine);
		tryModel.setEndLine(endLine);
		tryModel.setName("TryCatchBlock");
		tryCatchBlockList.add(tryModel);

		if (node.catchClauses().size() > 1) {
			multipleCatchBlock.add(tryModel);
		}

		for (int i = 0; i < node.catchClauses().size(); i++) {
			CatchClause catchClause = (CatchClause) node.catchClauses().get(i);
			// System.out.println("TryCatch: " + catchClause.getException());
			if (catchClause.getException() == null) {
				//System.out.println("No Exception");
			} else {
				ITypeBinding variableBinding = catchClause.getException().getType().resolveBinding();

				if (variableBinding != null) {
					String exceptionType = variableBinding.getQualifiedName();
					// System.out.println(
					// "Exception: " + catchClause.getException() + " " +
					// variableBinding.getQualifiedName());
					if (exceptionType.compareTo("java.lang.Exception") != 0) {
						JavaOtherElementModel differentExceptionCatch = new JavaOtherElementModel();
						differentExceptionCatch.setStartLine(startLine);
						differentExceptionCatch.setEndLine(endLine);
						differentExceptionCatch.setName("OtherExceptionCatch");
						otherExceptionCatch.add(differentExceptionCatch);
					}
				}

			}
		}
	}



	public void extractTopic() {
		if (cu == null) {
			System.out.println("Problem in the Compilation Unit initialization...");
			return;
		}
		cu.accept(new ASTVisitor() {

			@Override
			public boolean visit(TryStatement node) {
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodDeclaration node) {
				return super.visit(node);
			}
		});

		/*
		System.out.println("==================================================");
		System.out.println("Try catch block : " + tryCatchBlockList.size());
		System.out.println("Method throw exception: " + methodThrowException.size());
		System.out.println("Multiple catchBlock: " + multipleCatchBlock.size());
		System.out.println("Multiple Method throws: " + multipleMethodThrowException.size());
		System.out.println("Other exception catch: " + otherExceptionCatch.size());
		System.out.println("Wider exception type: " + widerExceptionType.size());*/
	}

	public static void main(String[] args) {
		System.out.println("Program Finishes Successfully");
	}
}
