package com.sail.java.exam.topic.professional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import com.sail.model.JavaOtherElementModel;
import com.sail.model.TopicExtractorBaseModel;
import com.sail.util.ConstantUtil;

public class JavaNIOKnowledgeExtractorOCP extends TopicExtractorBaseModel{

	public String filePath = "";
	public CompilationUnit cu = null;
	
	public JavaNIOKnowledgeExtractorOCP(String filePath, CompilationUnit cu) {
		this.filePath = filePath;
		this.cu = cu;
	}
	
	ArrayList<JavaOtherElementModel> methodCallPathList = new ArrayList<JavaOtherElementModel>();
	ArrayList<JavaOtherElementModel> methodCallFileList = new ArrayList<JavaOtherElementModel>();
	Map<String,ArrayList<JavaOtherElementModel>> fileBasicAttributeList = new HashMap<String,ArrayList<JavaOtherElementModel>>();
	
	
	
	
	@Override
	public void visitMethodInvocation(MethodInvocation node) {
		// TODO Auto-generated method stub

		
		int startLine = cu.getLineNumber(node.getStartPosition());
		int nodeLength = node.getLength();
		int endLine = cu.getLineNumber(node.getStartPosition() + nodeLength);
		
		ITypeBinding typeBinding = node.resolveTypeBinding();
		IMethodBinding methodBinding = node.resolveMethodBinding();
		
		Expression e = node.getExpression();
		
		
		if(typeBinding != null && methodBinding != null && e != null) {
			if(e.resolveTypeBinding() != null) {
				String receiverFullName = e.resolveTypeBinding().getQualifiedName();
				//System.out.println(receiverFullName + " " + node.getName());
				if(typeBinding.getQualifiedName().compareTo("java.nio.file.Path") == 0) {
					if(receiverFullName.compareTo(ConstantUtil.JAVA_NIO_PATH) == 0){
						JavaOtherElementModel ob = new JavaOtherElementModel();
						ob.setStartLine(startLine);
						ob.setEndLine(endLine);
						ob.setName(node.getName().toString());
						ob.setElementType("java-nio-path-method-call");
						methodCallPathList.add(ob);
						//System.out.println("NIO: TypeBind: " + receiverFullName + " " + node.getName());
						
					}
					else if(receiverFullName.compareTo(ConstantUtil.JAVA_NIO_FILE) == 0){
						// different operation on file and directories...
						JavaOtherElementModel ob = new JavaOtherElementModel();
						ob.setStartLine(startLine);
						ob.setEndLine(endLine);
						ob.setName(node.getName().toString());
						ob.setElementType("java-nio-file-method-call");
						methodCallFileList.add(ob);
						//System.out.println("NIO: TypeBind: " + receiverFullName + " " + node.getName());
						
					}
				}
				if(receiverFullName.contains("java.nio.file.attribute")){
					
					JavaOtherElementModel ob = new JavaOtherElementModel();
					ob.setStartLine(startLine);
					ob.setEndLine(endLine);
					ob.setName(node.getName().toString());
					ob.setElementType("java-nio-file-attribute-call");
					
					if(!fileBasicAttributeList.containsKey(receiverFullName)) {
						fileBasicAttributeList.put(receiverFullName, new ArrayList<JavaOtherElementModel>());
					}
					fileBasicAttributeList.get(receiverFullName).add(ob);
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
			public boolean visit(MethodInvocation node) {
				return super.visit(node);
			}
		});
		
		/*for(String s : fileBasicAttributeList.keySet()) {
			System.out.println("Package: " + s);
			for(JavaOtherElementModel ob : fileBasicAttributeList.get(s)) {
				ob.printInfo();
			}
		}*/
	}

	public ArrayList<JavaOtherElementModel> getMethodCallPathList() {
		return methodCallPathList;
	}

	public void setMethodCallPathList(ArrayList<JavaOtherElementModel> methodCallPathList) {
		this.methodCallPathList = methodCallPathList;
	}

	public ArrayList<JavaOtherElementModel> getMethodCallFileList() {
		return methodCallFileList;
	}

	public void setMethodCallFileList(ArrayList<JavaOtherElementModel> methodCallFileList) {
		this.methodCallFileList = methodCallFileList;
	}

	public Map<String, ArrayList<JavaOtherElementModel>> getFileBasicAttributeList() {
		return fileBasicAttributeList;
	}

	public void setFileBasicAttributeList(Map<String, ArrayList<JavaOtherElementModel>> fileBasicAttributeList) {
		this.fileBasicAttributeList = fileBasicAttributeList;
	}
	
}
