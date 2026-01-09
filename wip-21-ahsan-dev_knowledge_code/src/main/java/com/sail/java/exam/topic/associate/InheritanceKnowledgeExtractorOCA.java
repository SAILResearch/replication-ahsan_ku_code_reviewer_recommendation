package com.sail.java.exam.topic.associate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.Expression;
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
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import com.sail.model.JavaClassModel;
import com.sail.model.JavaMethodModel;
import com.sail.model.JavaOtherElementModel;
import com.sail.model.TopicExtractorBaseModel;
import com.sail.util.JDTUtil;

public class InheritanceKnowledgeExtractorOCA extends TopicExtractorBaseModel{
	public String filePath = "";
	public CompilationUnit cu = null;
	

	Map<String, ArrayList<JavaMethodModel>> methodList = new HashMap<String, ArrayList<JavaMethodModel>>();

	ArrayList<JavaOtherElementModel> superClassReferSubClass = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> superClassReferSubClassWithCasting = new ArrayList<JavaOtherElementModel>();

	ArrayList<JavaOtherElementModel> parameterizedPolymorphism = new ArrayList<JavaOtherElementModel>();

	ArrayList<JavaOtherElementModel> superAccess = new ArrayList<JavaOtherElementModel>();

	List<String> primitiveTypeList = Arrays.asList("boolean", "byte", "short", "int", "long", "char", "float",
			"double");

	public boolean isInterface;

	public Set<String> interfaceCount = new HashSet<String>();
	public Set<String> abstractClassCount = new HashSet<String>();
	
	Set<String> allDefinedClassQualifiedName;

	public Set<String> invokedMethodList = new HashSet<String>();
	public Set<String> superClassList = new HashSet<String>();
	
	public ArrayList<JavaOtherElementModel> getSuperClassReferSubClass() {
		return superClassReferSubClass;
	}

	public void setSuperClassReferSubClass(ArrayList<JavaOtherElementModel> superClassReferSubClass) {
		this.superClassReferSubClass = superClassReferSubClass;
	}

	public ArrayList<JavaOtherElementModel> getSuperClassReferSubClassWithCasting() {
		return superClassReferSubClassWithCasting;
	}

	public void setSuperClassReferSubClassWithCasting(ArrayList<JavaOtherElementModel> superClassReferSubClassWithCasting) {
		this.superClassReferSubClassWithCasting = superClassReferSubClassWithCasting;
	}

	public ArrayList<JavaOtherElementModel> getParameterizedPolymorphism() {
		return parameterizedPolymorphism;
	}

	public void setParameterizedPolymorphism(ArrayList<JavaOtherElementModel> parameterizedPolymorphism) {
		this.parameterizedPolymorphism = parameterizedPolymorphism;
	}

	public ArrayList<JavaOtherElementModel> getSuperAccess() {
		return superAccess;
	}

	public void setSuperAccess(ArrayList<JavaOtherElementModel> superAccess) {
		this.superAccess = superAccess;
	}

	public boolean hasUserDefinedConstructor() {
		for (String methodName : methodList.keySet()) {
			if (methodList.get(methodName).get(0).isConstructor) {
				return true;
			}
		}
		return false;
	}

	public void findOverridedMethod() {
		for (String methodName : methodList.keySet()) {
			//System.out.println("Method: " + methodName + " " + methodList.get(methodName).size());
			if (methodList.get(methodName).size() > 1) {
				//System.out.println("Overrided Method ");
				for (JavaMethodModel methodModel : methodList.get(methodName)) {
					methodModel.summaryMethodInfo();
				}
			}
		}
	}

	public InheritanceKnowledgeExtractorOCA(String filePath, CompilationUnit cu,
			Set<String> allDefinedClassQualifiedName) {
		this.filePath = filePath;
		this.cu = cu;
		this.allDefinedClassQualifiedName = allDefinedClassQualifiedName;
	}

	@Override
	public void visitAssignment(Assignment node) {
		// TODO Auto-generated method stub

		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());

		String assignmentVariable = node.getLeftHandSide().toString();

		ITypeBinding leftOperandBinding = node.getLeftHandSide().resolveTypeBinding();

		if (leftOperandBinding != null) {
			// Assignment
			boolean isSuperClassInstanceReferSubClass = false;

			if (node.getRightHandSide() instanceof ClassInstanceCreation) {
				ITypeBinding rightOperandBinding = node.getRightHandSide().resolveTypeBinding();

				if (rightOperandBinding != null) {
					ITypeBinding temp = rightOperandBinding;
					while (temp.getSuperclass() != null) {
						//System.out.println(leftOperandBinding.getQualifiedName().toString() + " "
						//		+ temp.getQualifiedName().toString());
						if (leftOperandBinding.getQualifiedName().toString()
								.compareTo(temp.getQualifiedName().toString()) == 0) {
							isSuperClassInstanceReferSubClass = true;
							JavaOtherElementModel ob = new JavaOtherElementModel();
							ob.setStartLine(startLine);
							ob.setEndLine(endLine);
							ob.setName("SuperClassReferSubClass");
							superClassReferSubClass.add(ob);

							break;
						} else {
							temp = temp.getSuperclass();
						}
					}
				//	System.out.println("Superclass refer SubClass: " + isSuperClassInstanceReferSubClass);
				}
			}

			if (node.getRightHandSide() instanceof CastExpression) {
				CastExpression castExpression = (CastExpression) node.getRightHandSide();
				if (castExpression != null) {
					if (castExpression.getExpression() instanceof ClassInstanceCreation) {
						ITypeBinding binding = castExpression.resolveTypeBinding();
						if (binding != null) {
							while (binding.getSuperclass() != null) {
								if (leftOperandBinding.getQualifiedName().toString()
										.compareTo(binding.getQualifiedName().toString()) == 0) {

									isSuperClassInstanceReferSubClass = true;
									JavaOtherElementModel ob = new JavaOtherElementModel();
									ob.setStartLine(startLine);
									ob.setEndLine(endLine);
									ob.setName("SuperClassReferSubClassWithCast");
									superClassReferSubClassWithCasting.add(ob);
									superClassReferSubClass.add(ob);
									break;
								}
								binding = binding.getSuperclass();
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void visitMethodDeclaration(MethodDeclaration node) {
		// TODO Auto-generated method stub
		JavaMethodModel methodModel = JDTUtil.parseRequiredMethodInformation(cu, node);

		if (!methodList.containsKey(methodModel.getMethodName())) {
			methodList.put(methodModel.getMethodName(), new ArrayList<JavaMethodModel>());
		}
		methodList.get(methodModel.getMethodName()).add(methodModel);
	}

	@Override
	public void visitMethodInvocation(MethodInvocation node) {
		
		// TODO Auto-generated method stub
		IMethodBinding methodBinding = node.resolveMethodBinding();

		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());

		//System.out.println("MI: " + node.getName());
		
		if (methodBinding != null) {
			if (allDefinedClassQualifiedName.contains(methodBinding.getDeclaringClass().getQualifiedName())) {
				invokedMethodList.add(methodBinding.getDeclaringClass().getQualifiedName());
				//System.out.println("Invoked Method Class: " + methodBinding.getDeclaringClass().getQualifiedName() + " " +  node.getName());
			}
			
			for (int i = 0; i < node.arguments().size(); i++) {
				Expression methodArguType = (Expression) node.arguments().get(i);
				ITypeBinding arguTypeBinding = methodArguType.resolveTypeBinding();

				if (arguTypeBinding != null) {
					if (!allDefinedClassQualifiedName.contains(arguTypeBinding.getQualifiedName())) {
						continue;
					}
					invokedMethodList.add(methodBinding.getDeclaringClass().getQualifiedName());
					
					
					if (!primitiveTypeList.contains(arguTypeBinding.getQualifiedName())) {
						if(i >= methodBinding.getParameterTypes().length) {
							continue;
						}
						ITypeBinding parameterType = methodBinding.getParameterTypes()[i];
						while (arguTypeBinding.getSuperclass() != null) {

							if (parameterType.getQualifiedName().toString()
									.compareTo(arguTypeBinding.getQualifiedName().toString()) == 0) {

								JavaOtherElementModel ob = new JavaOtherElementModel();
								ob.setStartLine(startLine);
								ob.setEndLine(endLine);
								ob.setName("ParameterizedPolymorphism");
								parameterizedPolymorphism.add(ob);
								break;
							}
							arguTypeBinding = arguTypeBinding.getSuperclass();
						}
					}
				}
			}
		}
	}

	@Override
	public void visitSuperConstructorInvocation(SuperConstructorInvocation node) {
		// TODO Auto-generated method stub
		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());

		JavaOtherElementModel ob = new JavaOtherElementModel();
		ob.setStartLine(startLine);
		ob.setEndLine(endLine);
		ob.setName("SuperConstructorInvocation");

		superAccess.add(ob);

	}

	
	@Override
	public void visitSuperFieldAccess(SuperFieldAccess node) {
		// TODO Auto-generated method stub
		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());

		JavaOtherElementModel ob = new JavaOtherElementModel();
		ob.setStartLine(startLine);
		ob.setEndLine(endLine);
		ob.setName("SuperFieldAccess");
		superAccess.add(ob);
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration node) {
		
		if(node.isInterface()) {
			isInterface = true;
			interfaceCount.add(node.getName().toString());
		}
		if(node.getSuperclassType() != null) {
			if(node.getSuperclassType().resolveBinding() != null) {
				String superClassName = node.getSuperclassType().resolveBinding().getQualifiedName();
				superClassList.add(superClassName);
			}
		}
		super.visitTypeDeclaration(node);
	}

	@Override
	public void visitSuperMethodInvocation(SuperMethodInvocation node) {
		// TODO Auto-generated method stub
		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());

		JavaOtherElementModel ob = new JavaOtherElementModel();
		ob.setStartLine(startLine);
		ob.setEndLine(endLine);
		ob.setName("SuperMethodInvocation");
		superAccess.add(ob);
	}

	@Override
	public void visitSuperMethodReference(SuperMethodReference node) {
		// TODO Auto-generated method stub
		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());

		JavaOtherElementModel ob = new JavaOtherElementModel();
		ob.setStartLine(startLine);
		ob.setEndLine(endLine);
		ob.setName("SuperMethodReference");
		superAccess.add(ob);
	}

	public void extractTopic() {
		
		if (cu == null) {
			System.out.println("Problem in the Compilation Unit initialization...");
			return;
		}
		
		cu.accept(new ASTVisitor() {
			@Override
			public void endVisit(TypeDeclaration node) {
				
				super.endVisit(node);
			}

			@Override
			public boolean visit(SuperConstructorInvocation node) {
				// TODO Auto-generated method stub
				
				return super.visit(node);
			}

			@Override
			public boolean visit(SuperFieldAccess node) {
				// TODO Auto-generated method stub
				

				return super.visit(node);
			}

			@Override
			public boolean visit(SuperMethodInvocation node) {
				// TODO Auto-generated method stub
				

				return super.visit(node);
			}

			@Override
			public boolean visit(SuperMethodReference node) {
				// TODO Auto-generated method stub

				return super.visit(node);
			}

			@Override
			public boolean visit(MethodInvocation node) {
				return true;
			}

			@Override
			public boolean visit(MethodDeclaration node) {
				
				return super.visit(node);
			}

			@Override
			public boolean visit(Assignment node) {
				return super.visit(node);
			}
		});
	}

	public boolean isAbstractClass() {
		boolean flag = false;
		for (String methodName : methodList.keySet()) {
			for (JavaMethodModel methodModel : methodList.get(methodName)) {
				if (methodModel.isAbstract()) {
					flag = true;
				}
				String methodClassName = methodModel.getDeclaringClassName().trim();
				if(methodClassName.length() > 0){
					abstractClassCount.add(methodClassName);
				}
			}
		}
		return flag;
	}

	public boolean isInterface() {
		return this.isInterface;
	}

	public static void main(String[] args) {
		System.out.println("Program Finishes Successfully");
	}
}
