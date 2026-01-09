package com.sail.java.exam.topic.professional;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.sail.model.JavaOtherElementModel;
import com.sail.model.TopicExtractorBaseModel;
import com.sail.util.ConstantUtil;

public class JavaIOFundamentalKnowledgeExtractorOCP extends TopicExtractorBaseModel{

	public String filePath = "";
	public CompilationUnit cu = null;
	
	public JavaIOFundamentalKnowledgeExtractorOCP(String filePath, CompilationUnit cu) {
		this.filePath = filePath;
		this.cu = cu;
	}
	
	ArrayList<JavaOtherElementModel> oldWayUserInteractionProcess = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> newWayUserInteractionProcess = new ArrayList<JavaOtherElementModel>();
	
	ArrayList<JavaOtherElementModel> useSeialization = new ArrayList<JavaOtherElementModel>();
	
	ArrayList<JavaOtherElementModel> byteStreamFileList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> charStreamFileList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> binaryStreamFileList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> otherIOFileList = new ArrayList<JavaOtherElementModel>();
	
	
	
	
	
	@Override
	public void visitTypeDeclaration(TypeDeclaration node) {
		// TODO Auto-generated method stub
		int startLine = cu.getLineNumber(node.getStartPosition());
		int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
		
		ITypeBinding typeBinding = node.resolveBinding();
		if(node.superInterfaceTypes() != null) {
			for(Object typeOb : node.superInterfaceTypes()) {
				if(typeOb instanceof Type) {
					Type tp = (Type) typeOb;
					if(tp.resolveBinding() != null) {
						String fullyQualName = tp.resolveBinding().getQualifiedName();
						if(fullyQualName.contains("java.io.Serializable")) {
							JavaOtherElementModel ob = new JavaOtherElementModel();
							ob.setStartLine(startLine);
							ob.setEndLine(endLine);
							ob.setName(fullyQualName);
							ob.setElementType("implement-interface");
							useSeialization.add(ob);
							//System.out.println("Serialization: " + ob.getName() + " " + ob.getStartLine());
						}
					}
				}
			}
		}
	}
	
	

	@Override
	public void visitClassInstanceCreation(ClassInstanceCreation node) {
		// TODO Auto-generated method stub

		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);
		
		ITypeBinding typeBinding = node.resolveTypeBinding();
		if(typeBinding != null){
			
			String qualifiedNameInstance = typeBinding.getQualifiedName();
			
			if(ConstantUtil.byteStreamAPIList.contains(qualifiedNameInstance)) {
				JavaOtherElementModel jModel = new JavaOtherElementModel();
				jModel.setStartLine(startLine);
				jModel.setEndLine(endLine);
				jModel.setName(qualifiedNameInstance);
				
				if(qualifiedNameInstance.contains("Input")) {
					jModel.setElementType("byte-stream-file-read-api-instance-creation");
				}else {
					jModel.setElementType("byte-stream-file-write-api-instance-creation");
				}
				
				byteStreamFileList.add(jModel);
			}else if (ConstantUtil.charStreamAPIList.contains(qualifiedNameInstance)) {
				JavaOtherElementModel  jModel = new JavaOtherElementModel();
				jModel.setStartLine(startLine);
				jModel.setEndLine(endLine); 
				jModel.setName(qualifiedNameInstance);
				if(qualifiedNameInstance.contains("Reader")) {
					jModel.setElementType("char-stream-file-read-api-instance-creation");
				}else {
					jModel.setElementType("char-stream-file-write-api-instance-creation");
				}
				charStreamFileList.add(jModel);
			}else if (ConstantUtil.binaryStreamAPIList.contains(qualifiedNameInstance)) {
				JavaOtherElementModel jModel = new JavaOtherElementModel();
				jModel.setStartLine(startLine);
				jModel.setEndLine(endLine);
				jModel.setName(qualifiedNameInstance);
				jModel.setElementType("binary-stream-file-api-instance-creation");
				binaryStreamFileList.add(jModel);
			} else if (qualifiedNameInstance.startsWith("java.io")) {
				JavaOtherElementModel jModel = new JavaOtherElementModel();
				jModel.setStartLine(startLine);
				jModel.setEndLine(endLine);
				jModel.setName(qualifiedNameInstance);
				jModel.setElementType("other-file-api");
				otherIOFileList.add(jModel);
			}
			
			List<Expression> argumentList = node.arguments();
			for(Expression e : argumentList){
				ITypeBinding expBind = e.resolveTypeBinding();
				if(expBind != null) {
					String fullName = expBind.getQualifiedName() + ConstantUtil.PACKAGE_SPLITTER + e.toString();
					if(fullName.compareTo(ConstantUtil.JAVA_OLD_PACKAGE_USER_INTERACTION) == 0) {
						JavaOtherElementModel ob = new JavaOtherElementModel();
						ob.setStartLine(startLine);
						ob.setEndLine(endLine);
						ob.setName(fullName);
						ob.setElementType("old-user-ineteraction");
						oldWayUserInteractionProcess.add(ob);
					}
				}
			}
		}
	}


	@Override
	public void visitMethodInvocation(MethodInvocation node) {
		// TODO Auto-generated method stub
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);
		
		ITypeBinding typeBinding = node.resolveTypeBinding();
		if(typeBinding != null && node.getExpression() != null) {
			ITypeBinding expBinding = node.getExpression().resolveTypeBinding();
			if(expBinding != null) {
				String fullExpName = expBinding.getQualifiedName();
				String fullInvokedMethod = typeBinding.getQualifiedName() + ConstantUtil.PACKAGE_SPLITTER + node.getName();
	
				if(fullExpName.compareTo(ConstantUtil.JAVA_NEW_CLASS_USER_INTERACTION) == 0
						&& fullInvokedMethod.compareTo(ConstantUtil.JAVA_NEW_METHOD_USER_INTERACTION) == 0) {
					
					JavaOtherElementModel ob = new JavaOtherElementModel();
					ob.setStartLine(startLine);
					ob.setEndLine(endLine);
					ob.setName(fullExpName);
					ob.setElementType("new-user-ineteraction");
					newWayUserInteractionProcess.add(ob);							
				}	
				
				if(fullExpName.compareTo(ConstantUtil.JAVA_OLD_PACKAGE_USER_INTERACTION) == 0) {
					JavaOtherElementModel ob = new JavaOtherElementModel();
					ob.setStartLine(startLine);
					ob.setEndLine(endLine);
					ob.setName(fullExpName);
					ob.setElementType("old-user-ineteraction");
					oldWayUserInteractionProcess.add(ob);
				}
			}
			String qualifiedNameInstance = typeBinding.getQualifiedName();
			if(ConstantUtil.byteStreamAPIList.contains(qualifiedNameInstance)) {
				JavaOtherElementModel jModel = new JavaOtherElementModel();
				jModel.setStartLine(startLine);
				jModel.setEndLine(endLine);
				jModel.setName(qualifiedNameInstance);
				
				if(qualifiedNameInstance.contains("Input")) {
					jModel.setElementType("byte-stream-file-read-api-method-invoke");
				}else {
					jModel.setElementType("byte-stream-file-write-api-method-invoke");
				}
				byteStreamFileList.add(jModel);
			}else if (ConstantUtil.charStreamAPIList.contains(qualifiedNameInstance)) {
				JavaOtherElementModel  jModel = new JavaOtherElementModel();
				jModel.setStartLine(startLine);
				jModel.setEndLine(endLine); 
				jModel.setName(qualifiedNameInstance);
				if(qualifiedNameInstance.contains("Reader")) {
					jModel.setElementType("char-stream-file-read-api-method-invoke");
				}else {
					jModel.setElementType("char-stream-file-write-api-method-invoke");
				}
				charStreamFileList.add(jModel);
			}else if (ConstantUtil.binaryStreamAPIList.contains(qualifiedNameInstance)) {
				JavaOtherElementModel jModel = new JavaOtherElementModel();
				jModel.setStartLine(startLine);
				jModel.setEndLine(endLine);
				jModel.setName(qualifiedNameInstance);
				jModel.setElementType("binary-stream-file-api-method-invoke");
				binaryStreamFileList.add(jModel);
			} else if (qualifiedNameInstance.startsWith("java.io")) {
				JavaOtherElementModel jModel = new JavaOtherElementModel();
				jModel.setStartLine(startLine);
				jModel.setEndLine(endLine);
				jModel.setName(qualifiedNameInstance);
				jModel.setElementType("other-file-api-method-invoke");
				otherIOFileList.add(jModel);
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
			public void endVisit(TypeDeclaration node) {
				super.endVisit(node);
			}

			@Override
			public boolean visit(ClassInstanceCreation node) {
				return super.visit(node);
			}

			@Override
			public void endVisit(MethodInvocation node) {
				super.endVisit(node);
			}
		});
	}

	public ArrayList<JavaOtherElementModel> getOldWayUserInteractionProcess() {
		return oldWayUserInteractionProcess;
	}

	public void setOldWayUserInteractionProcess(ArrayList<JavaOtherElementModel> oldWayUserInteractionProcess) {
		this.oldWayUserInteractionProcess = oldWayUserInteractionProcess;
	}

	public ArrayList<JavaOtherElementModel> getNewWayUserInteractionProcess() {
		return newWayUserInteractionProcess;
	}

	public void setNewWayUserInteractionProcess(ArrayList<JavaOtherElementModel> newWayUserInteractionProcess) {
		this.newWayUserInteractionProcess = newWayUserInteractionProcess;
	}

	public ArrayList<JavaOtherElementModel> getUseSeialization() {
		return useSeialization;
	}

	public void setUseSeialization(ArrayList<JavaOtherElementModel> useSeialization) {
		this.useSeialization = useSeialization;
	}

	public ArrayList<JavaOtherElementModel> getByteStreamFileList() {
		return byteStreamFileList;
	}

	public void setByteStreamFileList(ArrayList<JavaOtherElementModel> byteStreamFileList) {
		this.byteStreamFileList = byteStreamFileList;
	}

	public ArrayList<JavaOtherElementModel> getCharStreamFileList() {
		return charStreamFileList;
	}

	public void setCharStreamFileList(ArrayList<JavaOtherElementModel> charStreamFileList) {
		this.charStreamFileList = charStreamFileList;
	}

	public ArrayList<JavaOtherElementModel> getBinaryStreamFileList() {
		return binaryStreamFileList;
	}

	public void setBinaryStreamFileList(ArrayList<JavaOtherElementModel> binaryStreamFileList) {
		this.binaryStreamFileList = binaryStreamFileList;
	}

	public ArrayList<JavaOtherElementModel> getOtherIOFileList() {
		return otherIOFileList;
	}

	public void setOtherIOFileList(ArrayList<JavaOtherElementModel> otherIOFileList) {
		this.otherIOFileList = otherIOFileList;
	}
	
}
