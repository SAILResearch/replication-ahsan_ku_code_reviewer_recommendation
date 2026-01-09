package com.sail.model;

import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public abstract class TopicExtractorBaseModel {

	public void visitImportDeclaration(ImportDeclaration node){}
	public void visitVariableDeclarationStatement(VariableDeclarationStatement node){}
	
	
	public void visitAssignment(Assignment node){}
	public void visitPostfixExpression(PostfixExpression node){}
	public void visitPrefixExpression(PrefixExpression node){}
	
	public void visitInfixExpression(InfixExpression node){}
	public void visitIfStatement (IfStatement node){}
	public void visitConditionalExpression(ConditionalExpression node){}
	public void visitSwitchStatement(SwitchStatement node){}
	public void visitVariableDeclarationFragment(VariableDeclarationFragment node){}
	
	public void visitDoStatement(DoStatement node){}
	public void visitForStatement(ForStatement node){}
	public void visitWhileStatement(WhileStatement node){}
	public void visitBreakStatement(BreakStatement node){}
	public void visitContinueStatement(ContinueStatement node){}
	
	public void visitTypeDeclaration(TypeDeclaration node){}
	public void visitConstructorInvocation(ConstructorInvocation node){}
	public void visitMethodDeclaration(MethodDeclaration node){}
	public void visitMethodInvocation(MethodInvocation node){}
	public void visitInitializer(Initializer node){}
	
	public void visitSuperConstructorInvocation(SuperConstructorInvocation node){}
	public void visitSuperFieldAccess(SuperFieldAccess node){}
	public void visitSuperMethodInvocation(SuperMethodInvocation node){}
	public void visitSuperMethodReference(SuperMethodReference node){}
	
	public void visitTryStatement(TryStatement node){}
	
	public void visitEnumDeclaration(EnumDeclaration node) {}
	public void visitAnonymousClassDeclaration(AnonymousClassDeclaration node) {}
	public void visitClassInstanceCreation(ClassInstanceCreation node){}
	
	public void visitFieldDeclaration(FieldDeclaration node) {}
	public void visitExpressionMethodReference(ExpressionMethodReference node) {}
	public void visitArrayAccess(ArrayAccess node) {}
	public void visitMethodRef(MethodRef node) {}
	public void visitLambdaExpression(LambdaExpression node) {}
	public void visitTypeDeclarationStatement(TypeDeclarationStatement node) {}
	
	public void visitAnnotationTypeDeclaration(AnnotationTypeDeclaration node){}
}
