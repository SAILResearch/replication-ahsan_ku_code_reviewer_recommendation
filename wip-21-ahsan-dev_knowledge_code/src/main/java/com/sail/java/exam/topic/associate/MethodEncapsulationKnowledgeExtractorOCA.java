package com.sail.java.exam.topic.associate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import com.sail.model.JavaClassModel;
import com.sail.model.JavaFieldDeclarationModel;
import com.sail.model.JavaMethodModel;
import com.sail.model.JavaMethodParameterModel;
import com.sail.model.JavaOtherElementModel;
import com.sail.model.TopicExtractorBaseModel;
import com.sail.util.JDTUtil;

public class MethodEncapsulationKnowledgeExtractorOCA extends TopicExtractorBaseModel{
	public String filePath = "";
	public CompilationUnit cu = null;

	ArrayList<JavaFieldDeclarationModel> fieldList = new ArrayList<JavaFieldDeclarationModel>();
	
	ArrayList<JavaClassModel> javaClassInFileList = new ArrayList<JavaClassModel>();
	
	Map<String, ArrayList<JavaMethodModel>> methodList = new HashMap<String, ArrayList<JavaMethodModel>>();

	ArrayList<JavaFieldDeclarationModel> staticVariableDeclaration = new ArrayList<JavaFieldDeclarationModel>();
	ArrayList<JavaOtherElementModel> staticBlocks = new ArrayList<JavaOtherElementModel>();

	ArrayList<JavaOtherElementModel> thisConstructorInvoke = new ArrayList<JavaOtherElementModel>();

	ArrayList<JavaOtherElementModel> classPrivateFieldInitalize = new ArrayList<JavaOtherElementModel>();

	Map<String, ArrayList<JavaMethodModel>> overloadedMethod = new HashMap<String, ArrayList<JavaMethodModel>>();
	public boolean isDifferentAccessModifierIsUsedInMethod;
	public ArrayList<JavaMethodModel> varLengthArughmentMethodList = new ArrayList<JavaMethodModel>();

	Set<String> classPrivateVariableInitializeOtherMethods = new HashSet<String>();
	Set<String> classPrivateVariableInitiaalizeInConstructor = new HashSet<String>();

	ArrayList<JavaOtherElementModel> superClassReferSubClassList = new ArrayList<JavaOtherElementModel>();

	public Set<String> accessModifierFullList = new HashSet<String>();

	boolean classIsFinal = true;
	public boolean isImmutableClass = false;
	

	List<String> invodkedMethodForModifyingObject = Arrays.asList("add", "put");
	ArrayList<JavaOtherElementModel> objectParameterModifiedInsideMethodList = new ArrayList<JavaOtherElementModel>();

	List<String> primitiveTypeList = Arrays.asList("boolean", "byte", "short", "int", "long", "char", "float",
			"double");

	public MethodEncapsulationKnowledgeExtractorOCA(String filePath, CompilationUnit cu) {
		this.filePath = filePath;
		this.cu = cu;
	}

	public Map<String, ArrayList<JavaMethodModel>> getMethodList() {
		return methodList;
	}

	public ArrayList<JavaOtherElementModel> getThisConstructorInvoke() {
		return thisConstructorInvoke;
	}

	public void setThisConstructorInvoke(ArrayList<JavaOtherElementModel> thisConstructorInvoke) {
		this.thisConstructorInvoke = thisConstructorInvoke;
	}

	public ArrayList<JavaFieldDeclarationModel> getStaticVariableDeclaration() {
		return staticVariableDeclaration;
	}

	public void setStaticVariableDeclaration(ArrayList<JavaFieldDeclarationModel> staticVariableDeclaration) {
		this.staticVariableDeclaration = staticVariableDeclaration;
	}

	public ArrayList<JavaOtherElementModel> getStaticBlocks() {
		return staticBlocks;
	}

	public void setStaticBlocks(ArrayList<JavaOtherElementModel> staticBlocks) {
		this.staticBlocks = staticBlocks;
	}

	public ArrayList<JavaOtherElementModel> getClassPrivateFieldInitalize() {
		return classPrivateFieldInitalize;
	}

	public void setClassPrivateFieldInitalize(ArrayList<JavaOtherElementModel> classPrivateFieldInitalize) {
		this.classPrivateFieldInitalize = classPrivateFieldInitalize;
	}

	public Map<String, ArrayList<JavaMethodModel>> getOverloadedMethod() {
		return overloadedMethod;
	}

	public void setOverloadedMethod(Map<String, ArrayList<JavaMethodModel>> overloadedMethod) {
		this.overloadedMethod = overloadedMethod;
	}

	public boolean isDifferentAccessModifierIsUsedInMethod() {
		return isDifferentAccessModifierIsUsedInMethod;
	}

	public void setDifferentAccessModifierIsUsedInMethod(boolean isDifferentAccessModifierIsUsedInMethod) {
		this.isDifferentAccessModifierIsUsedInMethod = isDifferentAccessModifierIsUsedInMethod;
	}

	public ArrayList<JavaMethodModel> getVarLengthArughmentMethodList() {
		return varLengthArughmentMethodList;
	}

	public void setVarLengthArughmentMethodList(ArrayList<JavaMethodModel> varLengthArughmentMethodList) {
		this.varLengthArughmentMethodList = varLengthArughmentMethodList;
	}

	public Set<String> getClassPrivateVariableInitializeOtherMethods() {
		return classPrivateVariableInitializeOtherMethods;
	}

	public void setClassPrivateVariableInitializeOtherMethods(Set<String> classPrivateVariableInitializeOtherMethods) {
		this.classPrivateVariableInitializeOtherMethods = classPrivateVariableInitializeOtherMethods;
	}

	public Set<String> getClassPrivateVariableInitiaalizeInConstructor() {
		return classPrivateVariableInitiaalizeInConstructor;
	}

	public void setClassPrivateVariableInitiaalizeInConstructor(
			Set<String> classPrivateVariableInitiaalizeInConstructor) {
		this.classPrivateVariableInitiaalizeInConstructor = classPrivateVariableInitiaalizeInConstructor;
	}

	public boolean isImmutableClass() {
		return isImmutableClass;
	}

	public void setImmutableClass(boolean isImmutableClass) {
		this.isImmutableClass = isImmutableClass;
	}

	public List<String> getInvodkedMethodForModifyingObject() {
		return invodkedMethodForModifyingObject;
	}

	public void setInvodkedMethodForModifyingObject(List<String> invodkedMethodForModifyingObject) {
		this.invodkedMethodForModifyingObject = invodkedMethodForModifyingObject;
	}

	public ArrayList<JavaOtherElementModel> getObjectParameterModifiedInsideMethodList() {
		return objectParameterModifiedInsideMethodList;
	}

	public void setObjectParameterModifiedInsideMethodList(
			ArrayList<JavaOtherElementModel> objectParameterModifiedInsideMethodList) {
		this.objectParameterModifiedInsideMethodList = objectParameterModifiedInsideMethodList;
	}

	public void checkImmutableClass() {
		for(JavaClassModel jModel : javaClassInFileList) {
			if (jModel.isFinal() && jModel.isAllFieldPrivate()
					&& (classPrivateVariableInitiaalizeInConstructor.size() > 0
							|| classPrivateVariableInitializeOtherMethods.size() > 0)) {
				isImmutableClass = true;
			}
		}
	}

	public boolean isPrivateClassFieldAssignment(String varKey) {
		for (JavaFieldDeclarationModel fieldModel : fieldList) {
			String key = fieldModel.getFieldTypeName() + "-" + fieldModel.getFieldName();
			if (key.compareTo(varKey) == 0 && fieldModel.getAccessModifier().compareTo(JDTUtil.PRIVATE) == 0) {
				return true;
			}
		}
		return false;
	}

	public boolean isObjectTypeMethodInvokedFromParameter(ArrayList<JavaMethodParameterModel> parameters,
			String candParaName) {
		for (JavaMethodParameterModel parameter : parameters) {
			if (parameter.getParameterType().compareTo("ParameterizedType") == 0) {
				if (parameter.getParameterName().compareTo(candParaName) == 0) {
					return true;
				}
			}
		}
		return false;
	}


	@Override
	public void visitVariableDeclarationStatement(VariableDeclarationStatement node) {
		// TODO Auto-generated method stub


		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		if (JDTUtil.isStatic(node.getModifiers())) {
			JavaFieldDeclarationModel staticVariable = new JavaFieldDeclarationModel();
			staticVariable.setStartLine(startLine);
			staticVariable.setEndLine(endLine);
			staticVariable.setFieldType(JDTUtil.getVariableDeclarationType(node.getType()));
			staticVariable.setFieldTypeName(node.getType().toString());
			staticVariableDeclaration.add(staticVariable);
		}

		boolean isSuperReferSub = false;
		for (Object nodeOb : node.fragments()) {
			if (nodeOb instanceof VariableDeclarationFragment) {
				VariableDeclarationFragment vFrag = (VariableDeclarationFragment) nodeOb;
				if (vFrag.resolveBinding() != null) {
					if (vFrag.resolveBinding().getType() != null) {
						Expression initializationExp = vFrag.getInitializer();
						if(initializationExp != null) {
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
									break;
								}
							}
						}
					}

				}
			}
		}
	}

	@Override
	public void visitAssignment(Assignment node) {
		// TODO Auto-generated method stub
		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());

		String assignmentVariable = node.getLeftHandSide().toString();
		
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
			// ob.printInfo();
		}
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration node) {
		// TODO Auto-generated method stub
		ITypeBinding typeBinding = node.resolveBinding();
		if(typeBinding != null) {
			IVariableBinding variableBinding[] = typeBinding.getDeclaredFields();
			for (IVariableBinding var : variableBinding) {
				if (var.getType() != null) {
					JavaFieldDeclarationModel fieldModel = new JavaFieldDeclarationModel();
					fieldModel.setFieldName(var.getName());
					fieldModel.setFieldTypeName(var.getType().getQualifiedName());
					fieldModel.setStatic(Flags.isStatic(var.getModifiers()));
					fieldModel.setAccessModifier(JDTUtil.getAccessModifier(var.getModifiers()));
					fieldModel.setFinal(Flags.isFinal(var.getModifiers()));
					//fieldModel.summaryFieldInfo();
					fieldList.add(fieldModel);
					if(!node.isInterface()){
						String classFullName = typeBinding.getQualifiedName();
						JavaClassModel cModel = new JavaClassModel();
						cModel.getClassFieldList().put(fieldModel.getFieldName(), fieldModel);
						cModel.setAbstract(JDTUtil.isAbstract(node.getModifiers()));
						cModel.setFinal(JDTUtil.isFinal(node.getModifiers()));
						javaClassInFileList.add(cModel);
					}
				}
			}
		}
	}

	
	
	@Override
	public void visitConstructorInvocation(ConstructorInvocation node) {
		// TODO Auto-generated method stub
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		IMethodBinding methodBinding = node.resolveConstructorBinding();

		if (node.toString().contains("this(")) {
			JavaOtherElementModel ob = new JavaOtherElementModel();
			ob.setStartLine(startLine);
			ob.setEndLine(endLine);
			ob.setName("ThisConstructorInvocation");
			thisConstructorInvoke.add(ob);
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

		node.accept(new ASTVisitor() {
			@Override
			public boolean visit(MethodInvocation node) {
				if (node.getExpression() != null) {
					String callerName = node.getExpression().toString();
					boolean result = isObjectTypeMethodInvokedFromParameter(methodModel.getParameterList(),
							callerName);
					if (invodkedMethodForModifyingObject.contains(node.getName().toString().trim()) && result) {
						int startLineNumber = cu.getLineNumber(node.getStartPosition());
						int nodeLength = node.getLength();
						int endLineNumber = cu.getLineNumber(node.getStartPosition() + nodeLength);

						JavaOtherElementModel ob = new JavaOtherElementModel();
						ob.setStartLine(startLineNumber);
						ob.setEndLine(endLineNumber);
						ob.setName("ParameterObjectModified");
						objectParameterModifiedInsideMethodList.add(ob);
					}
				}
				return super.visit(node);
			}
			@Override
			public boolean visit(Assignment node) {
				String assignmentVariable = node.getLeftHandSide().toString();
				if (assignmentVariable.contains(".")) {
					assignmentVariable = assignmentVariable.substring(assignmentVariable.lastIndexOf(".") + 1);
				}
				ITypeBinding leftOperandBinding = node.getLeftHandSide().resolveTypeBinding();

				if (leftOperandBinding != null) {
					String varKey = leftOperandBinding.getQualifiedName().toString() + "-" + assignmentVariable;
					boolean result = isPrivateClassFieldAssignment(varKey);
					if (result) {
						if (!methodModel.isConstructor()) {
							classPrivateVariableInitializeOtherMethods.add(varKey);
						} else {
							classPrivateVariableInitiaalizeInConstructor.add(varKey);
						}
					}
				}
				return super.visit(node);
			}
		});
	}

	
	@Override
	public void visitMethodInvocation(MethodInvocation node) {
		// TODO Auto-generated method stub

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
				//ob.printInfo();
			}
		}
	}

	
	@Override
	public void visitInitializer(Initializer node) {
		// TODO Auto-generated method stub
		int startLineNumber = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLineNumber = cu.getLineNumber(node.getStartPosition() + nodeLength);

		if (JDTUtil.isStatic(node.getModifiers())) {
			JavaOtherElementModel staticBlockInitilizer = new JavaOtherElementModel();
			staticBlockInitilizer.setStartLine(startLineNumber);
			staticBlockInitilizer.setEndLine(endLineNumber);
			staticBlockInitilizer.setName("StaticBlock");
			staticBlocks.add(staticBlockInitilizer);
		}
	}

	public void extractTopic() {
		if (cu == null) {
			System.out.println("Problem in the Compilation Unit initialization...");
			return;
		}
		cu.accept(new ASTVisitor() {			
			@Override
			public boolean visit(TypeDeclaration node) {
				return super.visit(node);
			}

			@Override
			public boolean visit(ConstructorInvocation node) {
				// TODO Auto-generated method stub
				
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodDeclaration node) {
				return super.visit(node);
			}

			@Override
			public boolean visit(Assignment node) {
				
				return super.visit(node);
			}

			@Override
			public boolean visit(VariableDeclarationStatement node) {
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodInvocation node) {
				return super.visit(node);
			}

			@Override
			public boolean visit(Initializer node) {
				// TODO Auto-generated method stub
					return super.visit(node);
			}
		});

		checkImmutableClass();
		getInfoFromMethod();
	}

	public void getInfoFromMethod() {
		Set<String> usedAccessModifierList = new HashSet<String>();
		for (String methodName : methodList.keySet()) {
			if (methodList.get(methodName).size() > 0) {
				overloadedMethod.put(methodName, methodList.get(methodName));
			}
			for (JavaMethodModel method : methodList.get(methodName)) {
				usedAccessModifierList.add(method.getAccessModifierName());

				
				if (method.getAccessModifierName().compareTo(JDTUtil.DEFAULT) != 0){
					accessModifierFullList.add(method.getAccessModifierName());
				}
				

				for (JavaMethodParameterModel parameter : method.getParameterList()) {
					if (parameter.isVariableLengthArgument()) {
						varLengthArughmentMethodList.add(method);
						break;
					}
				}
			}
		}
		if (usedAccessModifierList.size() > 0) {
			this.isDifferentAccessModifierIsUsedInMethod = true;
		}
	}

	public static void main(String[] args) {
		System.out.println("Program Finishes Successfully");
	}
}
