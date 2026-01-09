package com.sail.java.exam.topic.professional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import com.sail.model.FunctionalInterfaceModel;
import com.sail.model.TopicExtractorBaseModel;
import com.sail.util.ConstantUtil;
import com.sail.util.JDTUtil;

public class FunctionalInterfaceKnowledgeExtractorOCP extends TopicExtractorBaseModel{

	public String filePath = "";
	public CompilationUnit cu = null;

	Map<String, ArrayList<FunctionalInterfaceModel>> functionalInterfaceDeclList = new HashMap<String, ArrayList<FunctionalInterfaceModel>>();

	public FunctionalInterfaceKnowledgeExtractorOCP(String filePath, CompilationUnit cu) {
		this.filePath = filePath;
		this.cu = cu;
	}
	
	@Override
	public void visitExpressionMethodReference(ExpressionMethodReference node) {
		// TODO Auto-generated method stub

		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());

		Expression methodRefExp = node.getExpression();
		if (methodRefExp != null) {
			ITypeBinding typeBinding = methodRefExp.resolveTypeBinding();
			if (typeBinding != null) {
				

				String fullyQualifiedClassName = typeBinding.getQualifiedName();
				String classNameWithPackage = fullyQualifiedClassName;
	
				String parameterString = "";
				if (fullyQualifiedClassName.contains("<")) {
					classNameWithPackage = fullyQualifiedClassName.substring(0,
							fullyQualifiedClassName.indexOf("<"));
				}
				if (fullyQualifiedClassName.contains("<") && fullyQualifiedClassName.contains(">")) {
					parameterString = fullyQualifiedClassName.substring(fullyQualifiedClassName.indexOf("<"),
							fullyQualifiedClassName.lastIndexOf(">") + 1);
				}
				//System.out.println("EMR: " + typeBinding.getQualifiedName()+ " "+classNameWithPackage+" " + startLine);

				if (classNameWithPackage.contains(ConstantUtil.FUNCTIONAL_INTERFACE_PACKAGE)) {

					String funcInterTypeName = JDTUtil.getFunctionalInterfaceType(classNameWithPackage);
					
					FunctionalInterfaceModel fm = new FunctionalInterfaceModel();

					fm.setStartLine(startLine);
					fm.setEndLine(endLine);
					fm.setNumberOfParameters(typeBinding.getTypeArguments().length);
					fm.setParameters(parameterString);
					fm.setFunctionalInterfaceName(classNameWithPackage);
					fm.setFunctionalInterfaceIdentifyType("expression-method-reference");

					if (!functionalInterfaceDeclList.containsKey(funcInterTypeName)) {
						functionalInterfaceDeclList.put(funcInterTypeName,
								new ArrayList<FunctionalInterfaceModel>());
					}
					functionalInterfaceDeclList.get(funcInterTypeName).add(fm);
				} else {
					ITypeBinding arguBindList[] = typeBinding.getTypeArguments();
					for (ITypeBinding argu : arguBindList) {
						String fullyQualifiedClassNameArgu = argu.getQualifiedName();
						String classNameWithPackageArgu = fullyQualifiedClassName;
						
						String parameterStringArgu = "";
						if (fullyQualifiedClassNameArgu.contains("<")) {
							classNameWithPackageArgu = fullyQualifiedClassNameArgu.substring(0,
									fullyQualifiedClassNameArgu.indexOf("<"));
						}
						if (fullyQualifiedClassNameArgu.contains("<")
								&& fullyQualifiedClassNameArgu.contains(">")) {
							parameterStringArgu = fullyQualifiedClassNameArgu.substring(
									fullyQualifiedClassNameArgu.indexOf("<"),
									fullyQualifiedClassNameArgu.lastIndexOf(">") + 1);
						}

						//System.out.println("CLLL " + classNameWithPackage);
						if (classNameWithPackageArgu.contains(ConstantUtil.FUNCTIONAL_INTERFACE_PACKAGE)) {
							
							String funcInterTypeName = JDTUtil.getFunctionalInterfaceType(classNameWithPackageArgu);
							FunctionalInterfaceModel fm = new FunctionalInterfaceModel();

							//System.out.println("EMR: " + typeBinding.getQualifiedName()+ " "+classNameWithPackageArgu+" " +funcInterTypeName+" "+ startLine);
		
							fm.setStartLine(startLine);
							fm.setEndLine(endLine);
							fm.setNumberOfParameters(typeBinding.getTypeArguments().length);
							fm.setParameters(parameterString);
							fm.setFunctionalInterfaceName(classNameWithPackageArgu);
							fm.setFunctionalInterfaceIdentifyType("expression-method-reference-parameter");

							if (!functionalInterfaceDeclList.containsKey(funcInterTypeName)) {
								functionalInterfaceDeclList.put(funcInterTypeName,
										new ArrayList<FunctionalInterfaceModel>());
							}
							functionalInterfaceDeclList.get(funcInterTypeName).add(fm);
						}
					}
				}
			}
		}
	}


	@Override
	public void visitVariableDeclarationStatement(VariableDeclarationStatement node) {
		// TODO Auto-generated method stub

		ITypeBinding typeBinding = node.getType().resolveBinding();

		if (typeBinding != null) {

			// System.out.println("VarDecl: " + node);

			int startLine = cu.getLineNumber(node.getStartPosition());
			int nodeLength = node.getLength();
			int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

			String fullyQualifiedClassName = typeBinding.getQualifiedName();
			// System.out.println("QUALNAME: " + fullyQualifiedClassName );
			String classNameWithPackage = fullyQualifiedClassName;

			if (fullyQualifiedClassName.contains(".")) {
				classNameWithPackage = fullyQualifiedClassName.substring(0,
						fullyQualifiedClassName.lastIndexOf("."));
			}
			String parameterString = "";
			if (fullyQualifiedClassName.contains("<")) {
				classNameWithPackage = fullyQualifiedClassName.substring(0,
						fullyQualifiedClassName.indexOf("<"));
			}
			if (fullyQualifiedClassName.contains("<") && fullyQualifiedClassName.contains(">")) {
				parameterString = fullyQualifiedClassName.substring(fullyQualifiedClassName.indexOf("<"),
						fullyQualifiedClassName.lastIndexOf(">") + 1);
			}

			if (classNameWithPackage.contains(ConstantUtil.FUNCTIONAL_INTERFACE_PACKAGE)) {

				String funcInterTypeName = JDTUtil.getFunctionalInterfaceType(classNameWithPackage);

				//System.out.println("P: " + classNameWithPackage + " " + funcInterTypeName);

				FunctionalInterfaceModel fm = new FunctionalInterfaceModel();

				fm.setStartLine(startLine);
				fm.setEndLine(endLine);
				fm.setNumberOfParameters(typeBinding.getTypeArguments().length);
				fm.setParameters(parameterString);
				fm.setFunctionalInterfaceName(classNameWithPackage);

				if (!functionalInterfaceDeclList.containsKey(funcInterTypeName)) {
					functionalInterfaceDeclList.put(funcInterTypeName,
							new ArrayList<FunctionalInterfaceModel>());
				}
				functionalInterfaceDeclList.get(funcInterTypeName).add(fm);
			}
		}
	}

	public void extractTopic() throws Exception {
		if (cu == null) {
			System.out.println("Problem in the Compilation Unit initialization...");
			return;
		}
		cu.accept(new ASTVisitor() {

			@Override
			public boolean visit(ExpressionMethodReference node) {
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodRef node) {
				//System.out.println("Method reference: " + node.getName());
				return super.visit(node);
			}

			@Override
			public boolean visit(LambdaExpression node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(VariableDeclarationStatement node) {
				return super.visit(node);
			}
		});

	}

	public Map<String, ArrayList<FunctionalInterfaceModel>> getBuiltInInterface() {
		Map<String, ArrayList<FunctionalInterfaceModel>> ob = new HashMap<String, ArrayList<FunctionalInterfaceModel>>();
		for (String funcIntName : functionalInterfaceDeclList.keySet()) {
			if (funcIntName.equals(ConstantUtil.FUNCTION_FUNCTIONAL_INTERFACE)
					|| funcIntName.equals(ConstantUtil.CONSUMER_FUNCTIONAL_INTERFACE)
					|| funcIntName.equals(ConstantUtil.SUPPLIER_FUNCTIONAL_INTERFACE)
					|| funcIntName.equals(ConstantUtil.PREDICATE_FUNCTIONAL_INTERFACE)) {
				ob.put(funcIntName, functionalInterfaceDeclList.get(funcIntName));

			}
		}
		return ob;
	}

	public ArrayList<FunctionalInterfaceModel> getPrimitiveFunctionalInterface() {
		if (functionalInterfaceDeclList.containsKey(ConstantUtil.PRIMITIVE_FUNCTIONAL_INTERFACE)) {
			return functionalInterfaceDeclList.get(ConstantUtil.PRIMITIVE_FUNCTIONAL_INTERFACE);
		}
		return new ArrayList<FunctionalInterfaceModel>();
	}

	public ArrayList<FunctionalInterfaceModel> getBinaryFunctionalInterface() {
		if (functionalInterfaceDeclList.containsKey(ConstantUtil.BINARY_FUNCTIONAL_INTERFACE)) {
			return functionalInterfaceDeclList.get(ConstantUtil.BINARY_FUNCTIONAL_INTERFACE);
		}
		return new ArrayList<FunctionalInterfaceModel>();
	}

	public ArrayList<FunctionalInterfaceModel> getUnaryFunctionalInterface() {
		if (functionalInterfaceDeclList.containsKey(ConstantUtil.UNARY_FUNCTIONAL_INTERFACE)) {
			return functionalInterfaceDeclList.get(ConstantUtil.UNARY_FUNCTIONAL_INTERFACE);
		}
		return new ArrayList<FunctionalInterfaceModel>();
	}

	public static void main(String[] args) {
		System.out.println("Program finishes successfully..");
	}
}
