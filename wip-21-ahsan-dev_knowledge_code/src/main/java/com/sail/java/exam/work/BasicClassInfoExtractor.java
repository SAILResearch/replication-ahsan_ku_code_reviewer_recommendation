package com.sail.java.exam.work;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.sail.model.JavaClassModel;
import com.sail.model.JavaFieldDeclarationModel;
import com.sail.model.JavaMethodModel;
import com.sail.util.JDTUtil;

public class BasicClassInfoExtractor {

	Stack<String> classChainList = new Stack<String>();
	Map<String, JavaClassModel> classList;
	ArrayList<String> anonymousClassNameList = new ArrayList<String>();
	public final String ANONYMOUS = "Anonymous";
	String packageName;
	String className;
	int anonnymousClssIndex = 0;
	public CompilationUnit cu = null;
	
	Map<String,ArrayList<String>> perJDKPackageFileList = new HashMap<String,ArrayList<String>>();
	
	public BasicClassInfoExtractor(CompilationUnit cu, Map<String, JavaClassModel> classList) {
		this.cu = cu;
		this.classList = classList;
	}

	public void printClassInformation() {
		System.out.println("[Class Information]");
		System.out.println("Total classes: " + classList.size());
		for (String className : classList.keySet()) {
			System.out.println(
					"Class Name: " + className + " Total Methods: " + classList.get(className).getMethodList().size());
		}
	}

	public void extractClassInfo() {
		if (cu == null) {
			System.out.println("Problem in the Compilation Unit initialization...");
			return;
		}
		cu.accept(new ASTVisitor() {

			
			
			
			@Override
			public boolean visit(ClassInstanceCreation node) {
				// TODO Auto-generated method stub
				AnonymousClassDeclaration anonymousClassDeclarationNode = node.getAnonymousClassDeclaration();
				int startLine = cu.getLineNumber(node.getStartPosition());
				int nodeLength = node.getLength();
				int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

				if (anonymousClassDeclarationNode != null) {
					// Anonymous class
					JavaClassModel classModel = new JavaClassModel();
					ITypeBinding typeBinding = node.getType().resolveBinding();

					classModel.setClassName(classChainList.peek() + "." + ANONYMOUS + anonnymousClssIndex);
					classModel.setAnonymousClass(true);
					classModel.setStartLine(startLine);
					classModel.setEndLine(endLine);

					if (typeBinding != null) {
						if (typeBinding.getQualifiedName().toString().trim().length() > 0) {
							classModel.setSuperClassQualifiedName(typeBinding.getQualifiedName().toString());
						}
					} else {
						//System.out.println("Problem in type binding for Anonymous Class");
					}
					// System.out.println("Anonymous Class: " + classModel.getClassName());
					anonymousClassNameList.add(classModel.getClassName());
					classList.put(classModel.getClassName(), classModel);
					++anonnymousClssIndex;
					classChainList.add(classModel.getClassName());

					//System.out.println("Start: " + startLine + " Anonymous Type: " + classModel.getClassName() + " "
					//		+ classModel.getSuperClassQualifiedName());

				}

				return super.visit(node);
			}

			@Override
			public boolean visit(AnonymousClassDeclaration node) {
				// TODO Auto-generated method stub

				return super.visit(node);
			}

			@Override
			public void endVisit(ClassInstanceCreation node) {
				// TODO Auto-generated method stub
				AnonymousClassDeclaration anonymousClassDeclarationNode = node.getAnonymousClassDeclaration();
				if (anonymousClassDeclarationNode != null) {
					classChainList.pop();
				}
				super.endVisit(node);
			}

			@Override
			public boolean visit(TypeDeclaration node) {

				// TODO Auto-generated method stub
				// System.out.println("[Type Declaration] " + node.getName() + " " +
				// node.getSuperclassType());

				int startLine = cu.getLineNumber(node.getStartPosition());
				int nodeLength = node.getLength();
				int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

				JavaClassModel classModel = new JavaClassModel();
				classModel.setPackageName(packageName);
				classModel.setAccessModifier(JDTUtil.getAccessModifier(node.getModifiers()));
				classModel.setStatic(JDTUtil.isStatic(node.getModifiers()));
				classModel.setFinal(JDTUtil.isFinal(node.getModifiers()));
				classModel.setInterface(node.isInterface());

				// Getting the class name. sometime the resolvebinding returns null.
				String className = "";
				if (node.resolveBinding() == null) {
					className = node.getName().toString();
				} else {
					className = node.resolveBinding().getQualifiedName().toString();
				}

				classModel.setStartLine(startLine);
				classModel.setEndLine(endLine);
				classModel.setClassName(className);

				if (node.getSuperclassType() != null) {
					if (node.getSuperclassType().resolveBinding() != null) {
						String superClassQualifiedName = node.getSuperclassType().resolveBinding().getQualifiedName();
						classModel.setChildClass(true);
						classModel.setSuperClassQualifiedName(superClassQualifiedName);
					} else {
						String superClassQualifiedName = node.getSuperclassType().toString();
						classModel.setChildClass(true);
						classModel.setSuperClassQualifiedName(superClassQualifiedName);
						// System.out.println("CHECKING: " + node.getSuperclassType().toString());
					}
				}
				// System.out.println("[Classs] " + classModel.getClassName() + " Super class: "
				// + classModel.getSuperClassQualifiedName());
				classList.put(classModel.getClassName(), classModel);
				classChainList.add(classModel.getClassName());
				return super.visit(node);
			}

			@Override
			public void endVisit(TypeDeclaration node) {
				// TODO Auto-generated method stub
				classChainList.pop();
				super.endVisit(node);
			}

			@Override
			public boolean visit(EnumDeclaration node) {
				// TODO Auto-generated method stub
				int startLine = cu.getLineNumber(node.getStartPosition());
				int nodeLength = node.getLength();
				int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);
				String className = node.getName().toString();
				if (node.resolveBinding() != null) {
					className = node.resolveBinding().getQualifiedName();
				}
				JavaClassModel classModel = new JavaClassModel();
				classModel.setPackageName(packageName);
				classModel.setAccessModifier(JDTUtil.getAccessModifier(node.getModifiers()));
				classModel.setStatic(JDTUtil.isStatic(node.getModifiers()));
				classModel.setFinal(JDTUtil.isFinal(node.getModifiers()));
				classModel.setClassName(className);
				classModel.setStartLine(startLine);
				classModel.setEndLine(endLine);
				classList.put(className, classModel);
				classChainList.add(classModel.getClassName());
				return super.visit(node);
			}

			@Override
			public void endVisit(EnumDeclaration node) {
				// TODO Auto-generated method stub
				classChainList.pop();
				super.endVisit(node);
			}

			@Override
			public boolean visit(FieldDeclaration node) {
				ITypeBinding typeBind = node.getType().resolveBinding();
				
				JavaFieldDeclarationModel fieldDeclModel = JDTUtil.parseRequiredFieldInformation(cu, node);
				String className = classChainList.peek();
				classList.get(className).getClassFieldList().put(fieldDeclModel.getFieldName(), fieldDeclModel);
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodDeclaration node) {

				// TODO Auto-generated method stub
				IMethodBinding methodBinding = node.resolveBinding();
				String fullyQualifiedMethodClassName = "";

				JavaMethodModel methodModel = JDTUtil.parseRequiredMethodInformation(cu, node);

				// System.out.println("Method " + node.getName() + " " +
				// methodModel.getStartLine() + " C: "
				// + fullyQualifiedMethodClassName + " P: " + classChainList.peek());

				fullyQualifiedMethodClassName = classChainList.peek();

				/*
				 * if (fullyQualifiedMethodClassName == null) {
				 * System.out.println("Null value"); }
				 */
				// System.out.println("Method: " + methodModel.getMethodName() + " Class: " +
				// fullyQualifiedMethodClassName
				// + " " + fullyQualifiedMethodClassName.length());

				String methodKey = fullyQualifiedMethodClassName + "-" + methodModel.getMethodName();

				if (!classList.get(fullyQualifiedMethodClassName).getMethodList().containsKey(methodKey)) {
					classList.get(fullyQualifiedMethodClassName).getMethodList().put(methodKey,
							new ArrayList<JavaMethodModel>());
				}
				classList.get(fullyQualifiedMethodClassName).getMethodList().get(methodKey).add(methodModel);
				return super.visit(node);
			}

			@Override
			public boolean visit(PackageDeclaration node) {
				// TODO Auto-generated method stub
				packageName = node.getName().toString();
				//System.out.println("Package name: " + packageName);
				return super.visit(node);
			}
		});
	}
}
