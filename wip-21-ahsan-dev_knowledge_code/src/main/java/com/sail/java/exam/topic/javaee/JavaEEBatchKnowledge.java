package com.sail.java.exam.topic.javaee;

import java.util.HashSet;
import java.util.Set;

import com.sail.model.TopicExtractorBaseModel;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class JavaEEBatchKnowledge extends TopicExtractorBaseModel{
    
    public String filePath = "";
    public CompilationUnit cu = null;

    public JavaEEBatchKnowledge(String filePath, CompilationUnit cu) {
        this.filePath = filePath;
        this.cu = cu;
    }

    public String JAVA_BATCH_API = "javax.batch";

    Set<String> usageOfJavaBatchAPI = new HashSet<String>();

    public Set<String> getUsageOfJavaBatchAPI() {
        return usageOfJavaBatchAPI;
    }

    @Override
    public void visitTypeDeclaration(TypeDeclaration node) {
        for (int i = 0 ; i < node.superInterfaceTypes().size() ; i ++ ){
            Type superInterfaceType = (Type) node.superInterfaceTypes().get(i);
            //System.out.println("SuperInterface: " + superInterfaceType.toString());
            ITypeBinding superInterTypeBinding = superInterfaceType.resolveBinding();
            if (superInterTypeBinding != null){
                String qualifiedName = superInterTypeBinding.getQualifiedName();
                if (qualifiedName.startsWith(JAVA_BATCH_API)){
                    usageOfJavaBatchAPI.add(qualifiedName);
                }
            }
        }
        super.visitTypeDeclaration(node);
    }
    @Override
    public void visitMethodDeclaration(MethodDeclaration node) {
        Type returnType = node.getReturnType2();
        for (int i = 0 ; i < node.parameters().size() ; i ++){
            SingleVariableDeclaration parameter = (SingleVariableDeclaration) node.parameters().get(i);
            Type parameterType = parameter.getType();
            if (parameterType != null){
                ITypeBinding parameterTypeBinding = parameterType.resolveBinding();
                if (parameterTypeBinding != null){
                    String qualifiedName = parameterTypeBinding.getQualifiedName();
                    if (qualifiedName.startsWith(JAVA_BATCH_API)){
                        usageOfJavaBatchAPI.add(qualifiedName);
                    }
                }
                
            }
        }
        if (returnType != null){
            ITypeBinding returnTypeBinding = returnType.resolveBinding();
            if (returnTypeBinding != null){
                String qualifiedName = returnTypeBinding.getQualifiedName();
                if (qualifiedName.startsWith(JAVA_BATCH_API)){
                    usageOfJavaBatchAPI.add(qualifiedName);
                }
            }
        }
    }
    public void printInfo(){
        System.out.println("--- Java Batch Knowledge ---");
        System.out.println("Usage of Java Batch API: " + usageOfJavaBatchAPI.size());
    }
}
