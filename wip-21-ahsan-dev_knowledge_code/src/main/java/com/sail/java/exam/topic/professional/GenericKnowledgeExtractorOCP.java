package com.sail.java.exam.topic.professional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.sail.java.exam.work.OverrideDetector;
import com.sail.model.GenericTypeModel;
import com.sail.model.JavaMethodModel;
import com.sail.model.JavaOtherElementModel;
import com.sail.model.TopicExtractorBaseModel;
import com.sail.util.ConstantUtil;
import com.sail.util.JDTUtil;

public class GenericKnowledgeExtractorOCP extends TopicExtractorBaseModel{
	public String filePath = "";
	public CompilationUnit cu = null;
	
	Map<String,GenericTypeModel> declaredGenericClassList = new HashMap<String,GenericTypeModel>();
	Map<String,ArrayList<JavaOtherElementModel>> allClassInstanceInfo = new HashMap<String,ArrayList<JavaOtherElementModel>>();
	//Map<String,ArrayList<String>> methodsInvokedOfallClasses = new HashMap<String,ArrayList<String>>();
	
	ArrayList<JavaOtherElementModel> interfaceList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaMethodModel> overridedMethodList = new ArrayList<JavaMethodModel>();
	ArrayList<JavaOtherElementModel> anonymousClassDelcList = new ArrayList<JavaOtherElementModel>();
	
	Map<String,ArrayList<JavaOtherElementModel>> methodsInvokedOfallClasses = new HashMap<String,ArrayList<JavaOtherElementModel>>();
	
	
	public GenericKnowledgeExtractorOCP(String filePath, CompilationUnit cu) {
		this.filePath = filePath;
		this.cu = cu;
	}
	
	
	
	
	@Override
	public void visitTypeDeclaration(TypeDeclaration node) {
		// TODO Auto-generated method stub

		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);
		
		boolean isInterface = node.isInterface();
		ITypeBinding typeBinding = node.resolveBinding();
		
		if(node.superInterfaceTypes() != null) {
			for(Object typeOb : node.superInterfaceTypes()) {
				if(typeOb instanceof Type) {
					Type tp = (Type) typeOb;
					if(tp.resolveBinding() != null) {
						String fullayQualName = tp.resolveBinding().getQualifiedName();
						//System.out.println("Implement Interface: " + fullayQualName);
						JavaOtherElementModel ob = new JavaOtherElementModel();
						ob.setStartLine(startLine);
						ob.setEndLine(endLine);
						ob.setName(fullayQualName);
						ob.setElementType("implement-interface");
						interfaceList.add(ob);
					}
				}
			}
		}
		// <TypeParameter> this means it is generic class
		if (node.typeParameters().size() > 0) {
			if(typeBinding != null) {
				String qualitifiedTypeName = typeBinding.getQualifiedName();
				GenericTypeModel ob = new GenericTypeModel();
				ob.setStartLine(startLine);
				ob.setEndLine(endLine);
				ob.setNumberOfParameter(node.typeParameters().size());
				if(isInterface) {
					ob.setInterface(true);
					ob.setActionName(ConstantUtil.INTERFACE_DECLARATION);
				}else {
					ob.setInterface(false);
					ob.setActionName(ConstantUtil.CLASS_DECLARATION);
				}
				declaredGenericClassList.put(qualitifiedTypeName,ob);
				//System.out.println("Got Declaration" + qualitifiedTypeName);
			}
			
		}
	}




	@Override
	public void visitClassInstanceCreation(ClassInstanceCreation node) {
		// TODO Auto-generated method stub

		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);
		
		ITypeBinding typeBinding = node.getType().resolveBinding();
		
		if(typeBinding != null) {
			ITypeBinding typeDeclaration = typeBinding.getTypeDeclaration();
			if(typeDeclaration != null) {
				String declarationFullyQualifiedName = typeDeclaration.getQualifiedName().toString();
				// This will make sure which type of class has been created..

				String genInterfaceName = JDTUtil.getGenericInterfaceName(declarationFullyQualifiedName);

				//System.out.println("Class_Instance " + declarationFullyQualifiedName + " Interface: " + genInterfaceName);
				
				JavaOtherElementModel ob = new JavaOtherElementModel();
				ob.setStartLine(startLine);
				ob.setEndLine(endLine);
				ob.setElementType(ConstantUtil.CLASS_INSTANCE_CREATION);
				
				if(!allClassInstanceInfo.containsKey(genInterfaceName)) {
					allClassInstanceInfo.put(genInterfaceName, new ArrayList<JavaOtherElementModel>());
				}
				allClassInstanceInfo.get(genInterfaceName).add(ob);
			}
			
		}
	}

	@Override
	public void visitFieldDeclaration(FieldDeclaration node) {
		// TODO Auto-generated method stub
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);
		
		ITypeBinding typeBinding = node.getType().resolveBinding();
		
		//System.out.println("FieldDeclaration: " + typeBinding.getQualifiedName());
	}




	@Override
	public void visitMethodDeclaration(MethodDeclaration node) {
		// TODO Auto-generated method stub

		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);
		
		IMethodBinding methodBinding = node.resolveBinding();
	
		if(methodBinding != null) {
			ITypeBinding typebinding = methodBinding.getDeclaringClass();
			if(typebinding != null) {
				OverrideDetector overDetector = OverrideDetector.getInstance();
				IMethodBinding overridenMethodBind = overDetector.findOverrideInHierarchy(methodBinding, typebinding);
				if(overridenMethodBind != null) {
					
					JavaMethodModel mm = JDTUtil.parseRequiredMethodInformation(cu, node);
					mm.setOverriddenMethod(true);
					mm.setStartLine(startLine);
					mm.setEndLine(endLine);
					
					if(overridenMethodBind.getDeclaringClass() != null) {
						mm.setDeclaringClassName(overridenMethodBind.getDeclaringClass().getQualifiedName());
					}else {
						mm.setDeclaringClassName("");
					}
				//	mm.setMethodName(overridenMethodBind.get);
					IAnnotationBinding annotBinding[] = methodBinding.getAnnotations();
					for(IAnnotationBinding annotation : annotBinding) {
						if(annotation.getName().toString().compareTo("Override") == 0) {
							mm.setOverrideAnnotation(true);
						}
					}
					overridedMethodList.add(mm);
					//System.out.println("Overriden Method: " + overridenMethodBind.getName() + " " + overridenMethodBind.getDeclaringClass().getQualifiedName());
				}
			}
		}
	}




	@Override
	public void visitAnonymousClassDeclaration(AnonymousClassDeclaration node) {
		// TODO Auto-generated method stub

		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);
		
		ITypeBinding typeBinding = node.resolveBinding();
		
		if(typeBinding != null) {
			JavaOtherElementModel ob = new JavaOtherElementModel();
			ob.setStartLine(startLine);
			ob.setEndLine(endLine);
			ob.setName(typeBinding.getQualifiedName().toString());
			ob.setElementType("anonymous-class");
			anonymousClassDelcList.add(ob);
			
			//System.out.println("Anonymous Class : " + ob.getName());
		}
	}
	



	@Override
	public void visitMethodInvocation(MethodInvocation node) {
		// TODO Auto-generated method stub

		
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);
		
		IMethodBinding methodBinding = node.resolveMethodBinding();
		if(methodBinding != null) {
			ITypeBinding typeBinding = methodBinding.getDeclaringClass();
			if(typeBinding != null) {
				
				String fullyQualifiedClassName = typeBinding.getQualifiedName();
				
				// java.util.List<java.util.String> --> java.util.List
				if(fullyQualifiedClassName.contains("<")) {
					fullyQualifiedClassName = fullyQualifiedClassName.substring(0, fullyQualifiedClassName.indexOf("<"));	
				}
				if(!methodsInvokedOfallClasses.containsKey(fullyQualifiedClassName)) {
					methodsInvokedOfallClasses.put(fullyQualifiedClassName, new ArrayList<JavaOtherElementModel>());
				}
				
				JavaOtherElementModel ob = new JavaOtherElementModel();
				ob.setStartLine(startLine);
				ob.setEndLine(endLine);
				ob.setName(fullyQualifiedClassName);
				ob.setElementType("method-invokation");
				
				methodsInvokedOfallClasses.get(fullyQualifiedClassName).add(ob);
				
				//System.out.println("Method invokation " + node.getName() + " " + fullyQualifiedClassName);
			}
		}
	}




	public void extractTopic() throws Exception {
		if (cu == null) {
			System.out.println("Problem in the Compilation Unit initialization...");
			return;
		}
		cu.accept(new ASTVisitor() {
			
			// To collect all the generic class declaration.....
			@Override
			public boolean visit(TypeDeclaration node) {
				return super.visit(node);
			}
			
			// collect all the creation of the class instance..
			@Override
			public boolean visit(ClassInstanceCreation node) {
				return super.visit(node);
			}
			
			@Override
			public boolean visit(FieldDeclaration node) {
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodDeclaration node) {
				return super.visit(node);
			}

			
			
			@Override
			public boolean visit(AnonymousClassDeclaration node) {
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodInvocation node) {
				return super.visit(node);
			}
		});
		
		
		/*
		// Summary Printing for checking....
		System.out.println("Total identified genericType " + allClassInstanceInfo.size());
		for(String s : declaredGenericClassList.keySet()) {
			System.out.println("Generic class : " + s + " " + declaredGenericClassList.get(s).getNumberOfParameter());
		}
		
		for(String methodInfokedClassName : methodsInvokedOfallClasses.keySet()) {
			for(String methodName : methodsInvokedOfallClasses.get(methodInfokedClassName)) {
				System.out.println(methodInfokedClassName + " -> " + methodName);
			}
		}*/
	}

	public static String LIST_INTERFACE_FULL_NAME = "java.util.List";
	public static String MAP_INTERFACE_FULL_NAME = "java.util.Map";
	public static String SET_INTERFACE_FULL_NAME = "java.util.Set";
	public static String DEQUE_INTERFACE_FULL_NAME = "java.util.Deque";
	
	public Map<String,ArrayList<JavaOtherElementModel>> getCreationFourGenInterface(){
		Map<String,ArrayList<JavaOtherElementModel>> ob = new HashMap<String,ArrayList<JavaOtherElementModel>>();
		for(String name : allClassInstanceInfo.keySet()) {
			if(name.equals(LIST_INTERFACE_FULL_NAME) || 
					name.equals(MAP_INTERFACE_FULL_NAME) ||
					name.equals(SET_INTERFACE_FULL_NAME) ||
					name.equals(DEQUE_INTERFACE_FULL_NAME)) {
				ob.put(name, allClassInstanceInfo.get(name));
			}
		}
		return ob;
	}
	
	public ArrayList<JavaMethodModel> getCompareToMethodCall() {
		ArrayList<JavaMethodModel> ob = new ArrayList<JavaMethodModel>();
		for(JavaMethodModel mm : overridedMethodList) {
			//System.out.println("IM: " + mm.getMethodName());
			if(mm.getMethodName().equals("compareTo") && mm.getDeclaringClassName().contains("java.lang.Comparable")) {
				ob.add(mm);
			}
		}
		boolean implementComparable = false;
		for(JavaOtherElementModel javaOtherModel : interfaceList) {
			if(javaOtherModel.getName().contains("java.lang.Comparable")) {
				implementComparable = true;
			}
		}
		if(implementComparable == false) {
			return new ArrayList<JavaMethodModel>();
		}
		// Return ob if True
		return ob;
	}
	
	public ArrayList<JavaMethodModel> getCreationOfComparator() {
		ArrayList<JavaMethodModel> ob = new ArrayList<JavaMethodModel>();
		int total = 0;
		for(JavaMethodModel mm : overridedMethodList) {
			//System.out.println("IM: " + mm.getMethodName() +" " + mm.getDeclaringClassName());
			if(mm.getMethodName().equals("compare") && mm.getDeclaringClassName().contains("java.util.Comparator")) {
				ob.add(mm);
			}
		}
		//System.out.println("implement comparator: " + ob.size());
		return ob;
	}
	
	
	public Map<String, GenericTypeModel> getDeclaredGenericClassList() {
		return declaredGenericClassList;
	}

	public void setDeclaredGenericClassList(Map<String, GenericTypeModel> declaredGenericClassList) {
		this.declaredGenericClassList = declaredGenericClassList;
	}

	public static void main(String[] args) {
		System.out.println("Program finishes successfully");
	}
}
