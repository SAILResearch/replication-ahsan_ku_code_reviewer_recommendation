package com.sail.java.exam.work;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
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

import com.sail.model.TopicExtractorBaseModel;

public class TopicExtractorSourceCodeVisitor {

	public String filePath = "";
	public CompilationUnit cu = null;
	ArrayList<TopicExtractorBaseModel> analyzedTopicExtractorList = null;
	
	public TopicExtractorSourceCodeVisitor(String filePath, CompilationUnit cu,
			ArrayList<TopicExtractorBaseModel> analyzedTopicExtractorList) {
		this.filePath = filePath;
		this.cu = cu;
		this.analyzedTopicExtractorList = analyzedTopicExtractorList;
	}
	
	public void parseJavaSourceCode() {
		if (cu == null) {
			System.out.println("Problem in the Compilation Unit initialization...");
			return;
		}
		cu.accept(new ASTVisitor() {

			@Override
			public boolean visit(VariableDeclarationStatement node) {
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitVariableDeclarationStatement(node);
				}
				return super.visit(node);
			}

			
			@Override
			public boolean visit(VariableDeclarationFragment node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitVariableDeclarationFragment(node);
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(Assignment node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitAssignment(node);
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(PostfixExpression node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitPostfixExpression(node);
				}
				return super.visit(node);
			}
			
			
			@Override
			public boolean visit(InfixExpression node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitInfixExpression(node);
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(PrefixExpression node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitPrefixExpression(node);
				}
				return super.visit(node);
			}

			
			@Override
			public boolean visit(Initializer node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitInitializer(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(MethodDeclaration node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitMethodDeclaration(node);
				}
				return super.visit(node);
			}

			
			@Override
			public boolean visit(MethodInvocation node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitMethodInvocation(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(IfStatement node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitIfStatement(node);
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(ConditionalExpression node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitConditionalExpression(node);
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(SwitchStatement node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitSwitchStatement(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(ArrayAccess node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitArrayAccess(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(DoStatement node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitDoStatement(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(ForStatement node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitForStatement(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(WhileStatement node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitWhileStatement(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(BreakStatement node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitBreakStatement(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(ContinueStatement node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitContinueStatement(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(TypeDeclaration node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitTypeDeclaration(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(ConstructorInvocation node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}


			@Override
			public boolean visit(SuperConstructorInvocation node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitSuperConstructorInvocation(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(SuperFieldAccess node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitSuperFieldAccess(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(SuperMethodInvocation node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitSuperMethodInvocation(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(SuperMethodReference node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitSuperMethodReference(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(TryStatement node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitTryStatement(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(EnumDeclaration node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitEnumDeclaration(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(AnonymousClassDeclaration node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitAnonymousClassDeclaration(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(ClassInstanceCreation node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitClassInstanceCreation(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(FieldDeclaration node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitFieldDeclaration(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(ExpressionMethodReference node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitExpressionMethodReference(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(MethodRef node) {
				// TODO Auto-generated method stub
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitMethodRef(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(LambdaExpression node) {
				// TODO Auto-generated method stub
				
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitLambdaExpression(node);
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(TypeDeclarationStatement node) {
				//System.out.println("NODE: " + node.toString());
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitTypeDeclarationStatement(node);
				}
				return super.visit(node);
			}
			
			@Override
			public boolean visit(AnnotationTypeDeclaration node) {
				//System.out.println("Annotation TYpe Declaration");
				for(TopicExtractorBaseModel ob : analyzedTopicExtractorList) {
					ob.visitAnnotationTypeDeclaration(node);
				}
				return super.visit(node);
			}
			
		});
	}
}
