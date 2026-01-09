package com.sail.java.exam.topic.professional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.sail.java.exam.work.OverrideDetector;
import com.sail.model.EnumModel;
import com.sail.model.JavaMethodModel;
import com.sail.model.JavaOtherElementModel;
import com.sail.model.TopicExtractorBaseModel;
import com.sail.util.JDTUtil;

public class AdvancedClassDesignKnowledgeExtractorOCP extends TopicExtractorBaseModel{
	public String filePath = "";
	public CompilationUnit cu = null;

	Map<String, EnumModel> enumDecList = new HashMap<String, EnumModel>();
	ArrayList<JavaOtherElementModel> innerClassList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> localClassList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> anonymousClassList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaMethodModel> overridedMethodList = new ArrayList<JavaMethodModel>();
	
	public AdvancedClassDesignKnowledgeExtractorOCP(String filePath, CompilationUnit cu) {
		this.filePath = filePath;
		this.cu = cu;
	}
	
	
	
	@Override
	public void visitEnumDeclaration(EnumDeclaration node) {

		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);

		// TODO Auto-generated method stub
		// System.out.println("Enum Declaration: " + node.toString());

		ITypeBinding typeBinding = node.resolveBinding();

		if(typeBinding != null) {
			IMethodBinding methodBindList[] = typeBinding.getDeclaredMethods();

			String enumName = typeBinding.getQualifiedName();

			if (!enumDecList.containsKey(enumName)) {
				enumDecList.put(enumName, new EnumModel());
			}

			enumDecList.get(enumName).setStartLine(startLine);
			enumDecList.get(enumName).setEndLine(endLine);
			enumDecList.get(enumName).setEnumName(enumName);

			IVariableBinding varBinndList[] = typeBinding.getDeclaredFields();
			for (IVariableBinding varBind : varBinndList) {
				// System.out.println("VarBinding: " + varBind.getName());
				if (varBind.isEnumConstant()) {
					enumDecList.get(enumName).getEnumConstants().add(varBind.getName());
				} else {
					enumDecList.get(enumName).getEnumFields().add(varBind.getName());
				}
			}

			if (methodBindList.length > 2) {
				for (IMethodBinding mBinding : methodBindList) {
					if (mBinding.isConstructor()) {
						// System.out.println("Enum constructor: " + mBinding.getName());
						enumDecList.get(enumName).setHasEnumConstructor(true);
					} else {
						// System.out.println("Normal enum method: " + mBinding.getName());
						enumDecList.get(enumName).getEnumMethods().add(mBinding.getName());
					}
				}
			}
			
			//System.out.println("Enum Decl: " + enumDecList.size());
		}
	}



	@Override
	public void visitTypeDeclaration(TypeDeclaration node) {
		// TODO Auto-generated method stub
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);
		
		if(!node.isPackageMemberTypeDeclaration()) {
			JavaOtherElementModel ob = new JavaOtherElementModel();
			ob.setStartLine(startLine);
			ob.setEndLine(endLine);
			ob.setName(node.getName().toString());
			
			if(node.isLocalTypeDeclaration()) {
				// Local inner classes
				ob.setElementType("local-inner-class");
				//System.out.println("LOCAL: " + node.getName());
				localClassList.add(ob);
			}else if (node.isMemberTypeDeclaration()) {
				// member inner classes
				ob.setElementType("inner-class");
				innerClassList.add(ob);
			}
			
		}
	}

	@Override
	public void visitAnonymousClassDeclaration(AnonymousClassDeclaration node) {
		// TODO Auto-generated method stub
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);
		
		JavaOtherElementModel ob = new JavaOtherElementModel();
		ob.setStartLine(startLine);
		ob.setEndLine(endLine);
		ob.setElementType("anonymous-class");
		anonymousClassList.add(ob);
	}



	@Override
	public void visitMethodDeclaration(MethodDeclaration node) {
		// TODO Auto-generated method stub

		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
		
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



	public void extractTopic() throws Exception {
		if (cu == null) {
			System.out.println("Problem in the Compilation Unit initialization...");
			return;
		}
		cu.accept(new ASTVisitor() {

			@Override
			public boolean visit(EnumDeclaration node) {
				return super.visit(node);
			}

			@Override
			public boolean visit(TypeDeclaration node) {
				return super.visit(node);
			}
			
			public boolean visit(AnonymousClassDeclaration node) {
			    return true;
			}

			@Override
			public boolean visit(MethodDeclaration node) {
				
				return super.visit(node);
			}
		});

		/*for (String enumName : enumDecList.keySet()) {
			EnumModel enModel = enumDecList.get(enumName);
			System.out.println("EnumName: " + enModel.getEnumName());
			System.out.println("EnumConstructor: " + enModel.isHasEnumConstructor());
			System.out.println("EnumConstants: " + enModel.isHasEnumConstants());
			System.out.println("EnumMethods: " + enModel.isHasEnumMethods());
			System.out.println("EnumFields: " + enModel.isHasEnumFields());
			System.out.println("---------------");
		}*/

	}
	public Map<String, EnumModel> getEnumDecList() {
		return enumDecList;
	}
	public void setEnumDecList(Map<String, EnumModel> enumDecList) {
		this.enumDecList = enumDecList;
	}
	public ArrayList<JavaOtherElementModel> getInnerClassList() {
		return innerClassList;
	}
	public void setInnerClassList(ArrayList<JavaOtherElementModel> innerClassList) {
		this.innerClassList = innerClassList;
	}
	public ArrayList<JavaOtherElementModel> getLocalClassList() {
		return localClassList;
	}
	public void setLocalClassList(ArrayList<JavaOtherElementModel> localClassList) {
		this.localClassList = localClassList;
	}
	public ArrayList<JavaOtherElementModel> getAnonymousClassList() {
		return anonymousClassList;
	}
	public void setAnonymousClassList(ArrayList<JavaOtherElementModel> anonymousClassList) {
		this.anonymousClassList = anonymousClassList;
	}
	public ArrayList<JavaMethodModel> getOverridedMethodList() {
		return overridedMethodList;
	}
	public void setOverridedMethodList(ArrayList<JavaMethodModel> overridedMethodList) {
		this.overridedMethodList = overridedMethodList;
	}
	public static void main(String[] args) {
		System.out.println("Program finishes successfully");
	}
}
