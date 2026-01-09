package com.sail.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.dom.AnnotatableType;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IntersectionType;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WildcardType;

import com.sail.java.exam.work.JavaExamTopicExtractor;
import com.sail.model.JavaFieldDeclarationModel;
import com.sail.model.JavaMethodModel;
import com.sail.model.JavaMethodParameterModel;

public class JDTUtil {

	public static final String PRIVATE = "Private";
	public static final String PUBLIC = "Public";
	public static final String PROTECTED = "Protected";
	public static final String DEFAULT = "Default";

	public static final String ARITHMETIC_OPERATOR = "ArithmeticOperator";
	public static final String SHIFT_OPERATOR = "ShiftOperator";
	public static final String RELATIONAL_OPERATOR = "RelationalOperator";
	public static final String BITWISE_OPERATOR = "BitwiseOperator";
	public static final String LOGICAL_OPERATOR = "LogicalOperator";
	public static final String TERNARY_OPERATOR = "TernaryOperator";
	public static final String ASSIGNMENT_OPERATOR = "AssignmentOperator";
	public static final String PREFIX_OPERATOR = "PrefixOperator";
	public static final String POSTFIX_OPERATOR = "PostfixOperator";

	public static List<String> arithmeticOperatorList = Arrays.asList("*", "//", "%", "+", "-");
	public static List<String> shiftOperatorList = Arrays.asList("<<", ">>", ">>>");
	public static List<String> relationalOperatorList = Arrays.asList("<", ">", "<=", ">=", "instance of", "==", "!=");
	public static List<String> bitwiseOperatorList = Arrays.asList("&", "|", "^");
	public static List<String> logicalOperatorList = Arrays.asList("&&", "||");
	public static List<String> ternaryOperatorList = Arrays.asList("?:");

	public static List<String> operatorList = Arrays.asList(ARITHMETIC_OPERATOR, RELATIONAL_OPERATOR, BITWISE_OPERATOR,
			LOGICAL_OPERATOR, TERNARY_OPERATOR, ASSIGNMENT_OPERATOR, PREFIX_OPERATOR, POSTFIX_OPERATOR);

	public static List<String> selectedTypeList = Arrays.asList("ArrayType", "ParameterizedType", "PrimitiveType",
			"WildcardType");

	public static List<String> JDTTypeList = Arrays.asList("AnnotatableType", "ArrayType", "IntersectionType",
			"NameQualifiedType", "ParameterizedType", "PrimitiveType", "QualifiedType", "SimpleType", "UnionType",
			"WildcardType");

	// Find the name of an operation
	public static String getOperatorName(String operator) {
		if (arithmeticOperatorList.contains(operator)) {
			return ARITHMETIC_OPERATOR;
		} else if (shiftOperatorList.contains(operator)) {
			return SHIFT_OPERATOR;
		} else if (relationalOperatorList.contains(operator)) {
			return RELATIONAL_OPERATOR;
		} else if (bitwiseOperatorList.contains(operator)) {
			return BITWISE_OPERATOR;
		} else if (logicalOperatorList.contains(operator)) {
			return LOGICAL_OPERATOR;
		} else if (ternaryOperatorList.contains(operator)) {
			return TERNARY_OPERATOR;
		}
		return null;
	}

	// Find the type of a variable declaration
	public static String getVariableDeclarationType(Type nodeType) {
		 if (nodeType instanceof ArrayType) {
			return JDTTypeList.get(1);
		} else if (nodeType instanceof IntersectionType) {
			return JDTTypeList.get(2);
		} else if (nodeType instanceof NameQualifiedType) {
			return JDTTypeList.get(3);
		} else if (nodeType instanceof ParameterizedType) {
			return JDTTypeList.get(4);
		} else if (nodeType instanceof PrimitiveType) {
			return JDTTypeList.get(5);
		} else if (nodeType instanceof QualifiedType) {
			return JDTTypeList.get(6);
		} else if (nodeType instanceof SimpleType) {
			return JDTTypeList.get(7);
		} else if (nodeType instanceof UnionType) {
			return JDTTypeList.get(8);
		} else if (nodeType instanceof WildcardType) {
			return JDTTypeList.get(9);
		}else if (nodeType instanceof AnnotatableType) {
			return JDTTypeList.get(0);
		}
		 
		return "";
	}

	public static boolean isStatic(int modifiers) {
		if (Flags.isStatic(modifiers)) {
			return true;
		}
		return false;
	}

	public static boolean isFinal(int modifiers) {
		if (Flags.isFinal(modifiers)) {
			return true;
		}
		return false;
	}

	public static boolean isAbstract(int modifiers) {
		if (Flags.isAbstract(modifiers)) {
			return true;
		}
		return false;
	}

	// Find the used accessmodifier
	public static String getAccessModifier(int modifiers) {
		if (Flags.isPrivate(modifiers)) {
			return PRIVATE;
		} else if (Flags.isPublic(modifiers)) {
			return PUBLIC;
		} else if (Flags.isProtected(modifiers)) {
			return PROTECTED;
		}
		return DEFAULT;
	}

	// Parsing the method declaration and generate a model for this pattern
	public static JavaMethodModel parseRequiredMethodInformation(CompilationUnit cu, MethodDeclaration node) {
		String methodName = node.getName().toString();
		int startLineNumber = cu.getLineNumber(node.getName().getStartPosition());
		int nodeLength = node.getLength();
		int endLineNumber = cu.getLineNumber(node.getStartPosition() + nodeLength);

		JavaMethodModel methodModel = new JavaMethodModel();
		methodModel.setMethodName(methodName);
		methodModel.setStartLine(startLineNumber);
		methodModel.setEndLine(endLineNumber);
		methodModel.setStaticMethod(Flags.isStatic(node.getModifiers()));
		methodModel.setConstructor(node.isConstructor());
		methodModel.setReturnType(node.getReturnType2() == null ? null : node.getReturnType2().toString());
		methodModel.setFinal(JDTUtil.isFinal(node.getModifiers()));
		methodModel.setAbstract(JDTUtil.isAbstract(node.getModifiers()));
		methodModel.setAccessModifierName(getAccessModifier(node.getModifiers()));
		for (Object v : node.parameters()) {
			SingleVariableDeclaration svd = (SingleVariableDeclaration) v;
			JavaMethodParameterModel methodParameter = new JavaMethodParameterModel();
			methodParameter.setParameterTypeName(svd.getType().toString());
			methodParameter.setParameterName(svd.getName().toString());
			methodParameter.setParameterType(JDTUtil.getVariableDeclarationType(svd.getType()));
			methodParameter.setVariableLengthArgument(svd.isVarargs());
			methodModel.getParameterList().add(methodParameter);
		}

		IMethodBinding binding = node.resolveBinding();
		if (binding != null) {
			ITypeBinding type = binding.getDeclaringClass();
			if (type != null) {
				methodModel.setDeclaringClassName(type.getName().toString());
			}
		}
		return methodModel;
	}

	// Parsing the field declaration and creating a model for this pattern
	public static JavaFieldDeclarationModel parseRequiredFieldInformation(CompilationUnit cu, FieldDeclaration node) {
		VariableDeclarationFragment classVariableFragment = (VariableDeclarationFragment) node.fragments().get(0);

		int startLineNumber = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLineNumber = cu.getLineNumber(node.getStartPosition() + nodeLength);

		JavaFieldDeclarationModel fieldModel = new JavaFieldDeclarationModel();
		fieldModel.setFieldName(classVariableFragment.getName().toString());
		fieldModel.setFieldTypeName(node.getType().toString());
		fieldModel.setFieldType(JDTUtil.getVariableDeclarationType(node.getType()));
		fieldModel.setStartLine(startLineNumber);
		fieldModel.setEndLine(endLineNumber);
		fieldModel.setStatic(Flags.isStatic(node.getModifiers()));
		fieldModel.setAccessModifier(getAccessModifier(node.getModifiers()));
		fieldModel.setFinal(Flags.isFinal(node.getModifiers()));

		return fieldModel;
	}
	
	// Find the implementation of generic interface
	public static String getGenericInterfaceName(String candString) {
		
		for(int i = 0 ; i < ConstantUtil.listImplementedClassList.size() ; i ++ ) {
			if(ConstantUtil.listImplementedClassList.get(i).compareTo(candString) == 0) {
				return ConstantUtil.LIST_INTERFACE_FULL_NAME;
			}
		}
		
		for(int i = 0 ; i < ConstantUtil.setImplementedClassList.size() ; i ++ ) {
			if(ConstantUtil.setImplementedClassList.get(i).compareTo(candString) == 0) {
				return ConstantUtil.SET_INTERFACE_FULL_NAME;
			}
		}
		
		for(int i = 0 ; i < ConstantUtil.mapIMplementedClassList.size() ; i ++ ) {
			if(ConstantUtil.mapIMplementedClassList.get(i).compareTo(candString) == 0) {
				return ConstantUtil.MAP_INTERFACE_FULL_NAME;
			}
		}
		
		for(int i = 0 ; i < ConstantUtil.dequeImplementedClassList.size() ; i ++ ) {
			if(ConstantUtil.dequeImplementedClassList.get(i).compareTo(candString) == 0) {
				return ConstantUtil.DEQUE_INTERFACE_FULL_NAME;
			}
		}
		
		return candString;
	}
	
	
	// Find the appropriate functional interface (primitive, binary, unary) or
	// Function, predicate, consumer and supplier
	public static String getFunctionalInterfaceType(String fullyQualifiedName) {
		for(String funInterName : ConstantUtil.primitiveVersionFunctionalInterfaces) {
			if(fullyQualifiedName.compareTo(funInterName) == 0) {
				return ConstantUtil.PRIMITIVE_FUNCTIONAL_INTERFACE;
			}
		}
		
		for(String funInterName : ConstantUtil.binaryVersionFunctionalInterfaces) {
			if(fullyQualifiedName.compareTo(funInterName) == 0) {
				return ConstantUtil.BINARY_FUNCTIONAL_INTERFACE;
			}
		}
		for(String funInterName : ConstantUtil.unaryOperatorFunctionalInterface) {
			if(fullyQualifiedName.compareTo(funInterName) == 0) {
				return ConstantUtil.UNARY_FUNCTIONAL_INTERFACE;
			}
		}
		
		if(fullyQualifiedName.compareTo("java.util.function.Function") == 0) {
			return ConstantUtil.FUNCTION_FUNCTIONAL_INTERFACE;
		}
		if(fullyQualifiedName.compareTo("java.util.function.Predicate") == 0) {
			return ConstantUtil.PREDICATE_FUNCTIONAL_INTERFACE;
		}
		if(fullyQualifiedName.compareTo("java.util.function.Supplier") == 0) {
			return ConstantUtil.SUPPLIER_FUNCTIONAL_INTERFACE;
		}
		if(fullyQualifiedName.compareTo("java.util.function.Consumer") == 0) {
			return ConstantUtil.CONSUMER_FUNCTIONAL_INTERFACE;
		}
		return "Others";	
	}
	
	public static boolean isAllFieldPrivate(ArrayList<JavaFieldDeclarationModel> fieldList) {
		for (JavaFieldDeclarationModel field : fieldList) {
			if (field.getAccessModifier().compareTo(JDTUtil.PRIVATE) != 0) {
				return false;
			}
		}
		return true;
	}
	public static String getJavaRootPackage(String importPackageName) {
		String temp = importPackageName;
		if(importPackageName.contains(".")) {
			temp = importPackageName.substring(0, importPackageName.lastIndexOf("."));
		}
		while(temp.contains(".")) {
			//System.out.println("T : " + temp);
			if(JavaExamTopicExtractor.java8PackageList.contains(temp)) {
				return JavaExamTopicExtractor.java8PackageList.get(JavaExamTopicExtractor.java8PackageList.indexOf(temp));
			}
			temp = temp.substring(0, temp.lastIndexOf("."));
		}
		return null;
		
	}
	
}
