package com.sail.java.exam.topic.professional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import com.sail.model.JavaFieldDeclarationModel;
import com.sail.model.JavaOtherElementModel;
import com.sail.util.JDTUtil;

public class TopicExtractorProfessionalTest {

	public String filePath = "";
	public CompilationUnit cu = null;

	public TopicExtractorProfessionalTest(String filePath, CompilationUnit cu) {
		this.filePath = filePath;
		this.cu = cu;
	}
	Map<String,ArrayList<String>> filesUseJavaPackageList = new HashMap<String,ArrayList<String>>();
	
	ArrayList<JavaFieldDeclarationModel> fieldList = new ArrayList<JavaFieldDeclarationModel>();
	ArrayList<JavaOtherElementModel> superClassReferSubClassList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> abstractMethodList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> abstractClassDeclList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> interfaceClassDeclList = new ArrayList<JavaOtherElementModel>();

	public void extractTopic() throws Exception {
		if (cu == null) {
			System.out.println("Problem in the Compilation Unit initialization...");
			return;
		}
		
		cu.accept(new ASTVisitor() {
			
		
			@Override
			public boolean visit(FieldDeclaration node) {
				
				return super.visit(node);
			}

			@Override
			public boolean visit(ForStatement node) {
				// TODO Auto-generated method stub
				int startLine = cu.getLineNumber(node.getStartPosition());
				int nodeLength = node.getLength();
				int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);
				
				//System.out.println("For Loop: " + node.toString());
				return super.visit(node);
			}

			@Override
			public boolean visit(ImportDeclaration node) {
				String importPackageName = node.getName().toString();
				String javaRootPackageName = JDTUtil.getJavaRootPackage(importPackageName);
				//System.out.println(importPackageName + " " + javaRootPackageName);
				if(javaRootPackageName != null) {
					if(!filesUseJavaPackageList.containsKey(javaRootPackageName)) {
						filesUseJavaPackageList.put(javaRootPackageName, new ArrayList<String>());
					}
					filesUseJavaPackageList.get(javaRootPackageName).add(filePath);
				}
				
				return super.visit(node);
			}

			@Override
			public boolean visit(TypeDeclaration node) {
				//System.out.println("DECL: " + node.getName());
				// TODO Auto-generated method stub
				int startLine = cu.getLineNumber(node.getStartPosition());
				int nodeLength = node.getLength();
				int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

				boolean abstractMethod = JDTUtil.isAbstract(node.getModifiers());
				boolean isInterface = node.isInterface();
				
				ITypeBinding typeBinding = node.resolveBinding();
				IVariableBinding variableBinding[] = typeBinding.getDeclaredFields();
				fieldList = new ArrayList<JavaFieldDeclarationModel>();
				for(IVariableBinding var : variableBinding) {
					if(var.getType() != null) {
						JavaFieldDeclarationModel fieldModel = new JavaFieldDeclarationModel();
						fieldModel.setFieldName(var.getName());
						fieldModel.setFieldTypeName(var.getType().getQualifiedName());
						fieldModel.setStatic(Flags.isStatic(var.getModifiers()));
						fieldModel.setAccessModifier(JDTUtil.getAccessModifier(var.getModifiers()));
						fieldModel.setFinal(Flags.isFinal(node.getModifiers())); 
						fieldModel.summaryFieldInfo();
						fieldList.add(fieldModel);
					}
				}
				
				if (typeBinding != null) {
					if (abstractMethod) {
						JavaOtherElementModel ob = new JavaOtherElementModel();
						ob.setStartLine(startLine);
						ob.setEndLine(endLine);
						ob.setName(node.getName().toString());
						ob.setElementType("abstract-class-declaration");
						abstractClassDeclList.add(ob);
					} else if (isInterface) {
						JavaOtherElementModel ob = new JavaOtherElementModel();
						ob.setStartLine(startLine);
						ob.setEndLine(endLine);
						ob.setName(node.getName().toString());
						ob.setElementType("interface-declaration");
						interfaceClassDeclList.add(ob);
					}
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodDeclaration node) {

				int startLine = cu.getLineNumber(node.getStartPosition());
				int nodeLength = node.getLength();
				int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

				boolean abstractMethod = JDTUtil.isAbstract(node.getModifiers());

				IMethodBinding methodBind = node.resolveBinding();
				ITypeBinding typeBinding = methodBind.getDeclaringClass();

				if (methodBind != null && typeBinding != null) {
					if (abstractMethod) {
						JavaOtherElementModel ob = new JavaOtherElementModel();
						ob.setStartLine(startLine);
						ob.setEndLine(endLine);
						ob.setName(node.getName().toString());
						ob.setElementType("abstract-method-declaration");
						ob.setDeclClassType(typeBinding.getQualifiedName());
						abstractMethodList.add(ob);
					}
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(VariableDeclarationStatement node) {

				int startLine = cu.getLineNumber(node.getStartPosition());
				int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
				boolean isSuperReferSub = false;
				for (Object nodeOb : node.fragments()) {
					if (nodeOb instanceof VariableDeclarationFragment) {
						VariableDeclarationFragment vFrag = (VariableDeclarationFragment) nodeOb;
						if (vFrag.resolveBinding() != null) {
							if (vFrag.resolveBinding().getType() != null) {
								Expression initializationExp = vFrag.getInitializer();
								String varQualName = vFrag.resolveBinding().getType().getQualifiedName();
								if (initializationExp.resolveTypeBinding() != null) {
									ITypeBinding temp = initializationExp.resolveTypeBinding();
									temp = temp.getSuperclass();
									while (temp != null) {
										String initiQualName = temp.getQualifiedName();
										if (varQualName.compareTo(initiQualName) == 0) {
											isSuperReferSub = true;
											break;
										} else {
											temp = temp.getSuperclass();
										}
									}
									if (isSuperReferSub) {
										JavaOtherElementModel ob = new JavaOtherElementModel();
										ob.setStartLine(startLine);
										ob.setEndLine(endLine);
										ob.setName(varQualName);
										ob.setElementType("super-class-refer-subclass-instance");
										superClassReferSubClassList.add(ob);
										ob.printInfo();
										break;
									}
								}
							}

						}
					}
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(Assignment node) {
				int startLine = cu.getLineNumber(node.getStartPosition());
				int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());

				String assignmentVariable = node.getLeftHandSide().toString();
				//System.out.println("Assignment: " + assignmentVariable);

				if (assignmentVariable.contains(".")) {
					assignmentVariable = assignmentVariable.substring(assignmentVariable.lastIndexOf(".") + 1);
				}
				ITypeBinding leftOperandBinding = node.getLeftHandSide().resolveTypeBinding();
				boolean isSuperReferSub = false;
				if (leftOperandBinding != null) {
					Expression rightHandExpression = node.getRightHandSide();
					if (rightHandExpression instanceof SimpleName) {
						ITypeBinding rightOperandBinding = rightHandExpression.resolveTypeBinding();
						if (rightOperandBinding != null) {
							ITypeBinding temp = rightOperandBinding;
							temp = temp.getSuperclass();
							while (temp != null) {
								if (leftOperandBinding.getQualifiedName().toString()
										.compareTo(temp.getQualifiedName().toString()) == 0) {
									isSuperReferSub = true;
									break;
								} else {
									temp = temp.getSuperclass();
								}
							}
						}
					}
				}
				if (isSuperReferSub) {
					JavaOtherElementModel ob = new JavaOtherElementModel();
					ob.setStartLine(startLine);
					ob.setEndLine(endLine);
					ob.setName(assignmentVariable);
					ob.setElementType("super-class-refer-subclass");
					superClassReferSubClassList.add(ob);
					ob.printInfo();
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodInvocation node) {
				int startLine = cu.getLineNumber(node.getStartPosition());
				int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());

				IMethodBinding methodBinding = node.resolveMethodBinding();
				ITypeBinding typeBinding = node.resolveTypeBinding();
				boolean isSuperReferSub = false;

				if (methodBinding != null && typeBinding != null) {
					List<Expression> expressionList = node.arguments();
					ITypeBinding paramBindingList[] = methodBinding.getParameterTypes();
					
					int paramLength = expressionList.size();
					
					if(methodBinding.isVarargs()) {
						paramLength = Math.min(expressionList.size(), paramBindingList.length);
					}
					
					//System.out.println("MI: " + node.getName());
					
					//System.out.println("Expressions: " + expressionList.size());
					//System.out.println("Parameters: " + paramBindingList.length);
					
					for (int i = 0; i < paramLength; i++) {
						ITypeBinding expBinding = expressionList.get(i).resolveTypeBinding();
						if (expBinding != null && paramBindingList[i] != null) {
							ITypeBinding temp = expBinding;
							temp = temp.getSuperclass();
							while (temp != null) {
								if (paramBindingList[i].getQualifiedName().toString()
										.compareTo(temp.getQualifiedName().toString()) == 0) {
									isSuperReferSub = true;
									break;
								} else {
									temp = temp.getSuperclass();
								}
							}
						}
					}
					
					if (isSuperReferSub) {
						JavaOtherElementModel ob = new JavaOtherElementModel();
						ob.setStartLine(startLine);
						ob.setEndLine(endLine);
						ob.setName("Method");
						ob.setElementType("super-class-refer-subclass-method");
						superClassReferSubClassList.add(ob);
						ob.printInfo();
					}
				}
				return super.visit(node);
			}
		});
		
		for(String packageName : filesUseJavaPackageList.keySet()) {
			System.out.println(packageName +" " + filesUseJavaPackageList.get(packageName).size());
		}
	}
	
}
