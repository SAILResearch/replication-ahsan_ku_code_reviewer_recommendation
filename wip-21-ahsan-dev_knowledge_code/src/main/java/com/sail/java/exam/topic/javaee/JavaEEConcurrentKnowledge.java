package com.sail.java.exam.topic.javaee;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sail.model.TopicExtractorBaseModel;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class JavaEEConcurrentKnowledge extends TopicExtractorBaseModel{
    
    public String filePath = "";
    public CompilationUnit cu = null;

    public JavaEEConcurrentKnowledge(String filePath, CompilationUnit cu) {
        this.filePath = filePath;
        this.cu = cu;
    }

    public List<String> manageExecutorAPI = Arrays.asList("javax.enterprise.concurrent.ManagedExecutors",
    "javax.enterprise.concurrent.ManagedScheduledExecutorService");

    public String otherConcurrentAPI = "javax.enterprise.concurrent";
    
    Set<String> usageOfJavaManageExecutor = new HashSet<String>();
    Set<String> usageOfOtherJavaEEConcurrent = new HashSet<String>();

    public Set<String> getUsageOfJavaManageExecutor() {
        return usageOfJavaManageExecutor;
    }

    public Set<String> getUsageOfOtherJavaEEConcurrent() {
        return usageOfOtherJavaEEConcurrent;
    }
    
    @Override
    public void visitFieldDeclaration(FieldDeclaration node) {
        Type fieldType = node.getType();
        if (fieldType != null){
            ITypeBinding fieldTypeBinding = fieldType.resolveBinding();
            if (fieldTypeBinding != null){
                String qualifiedName = fieldTypeBinding.getQualifiedName();
                if (manageExecutorAPI.contains(qualifiedName)){
                    usageOfJavaManageExecutor.add(qualifiedName);
                }else if (qualifiedName.contains(otherConcurrentAPI)){
                    usageOfOtherJavaEEConcurrent.add(qualifiedName);
                }
            }
        }
        super.visitFieldDeclaration(node);
    }

    @Override
    public void visitMethodInvocation(MethodInvocation node) {
        int startLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        IMethodBinding methodBinding = node.resolveMethodBinding();
        if (methodBinding != null) {
            ITypeBinding declareClassType = methodBinding.getDeclaringClass();
            if (declareClassType != null) {
                String qualifiedName = declareClassType.getQualifiedName();
                String qualifiedNameWithMethod = qualifiedName + "." + node.getName().toString();
                //System.out.println("MI: " + qualifiedNameWithMethod + "Node: " + node.toString());
                if (manageExecutorAPI.contains(qualifiedName)){
                    usageOfJavaManageExecutor.add(qualifiedName);
                }else if (qualifiedName.contains(otherConcurrentAPI)){
                    usageOfOtherJavaEEConcurrent.add(qualifiedName);
                }
            }
        }
        super.visitMethodInvocation(node);
    }

    @Override
    public void visitTypeDeclaration(TypeDeclaration node) {
        for (int i = 0 ; i < node.superInterfaceTypes().size() ; i ++ ){
            Type superInterfaceType = (Type) node.superInterfaceTypes().get(i);
            //System.out.println("SuperInterface: " + superInterfaceType.toString());
            ITypeBinding superInterTypeBinding = superInterfaceType.resolveBinding();
            if (superInterTypeBinding != null){
                String qualifiedName = superInterTypeBinding.getQualifiedName();
                if (manageExecutorAPI.contains(qualifiedName)){
                    usageOfJavaManageExecutor.add(qualifiedName);
                }else if (qualifiedName.contains(otherConcurrentAPI)){
                    usageOfOtherJavaEEConcurrent.add(qualifiedName);
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
                    if (manageExecutorAPI.contains(qualifiedName)){
                        usageOfJavaManageExecutor.add(qualifiedName);
                    }else if (qualifiedName.contains(otherConcurrentAPI)){
                        usageOfOtherJavaEEConcurrent.add(qualifiedName);
                    }
                }
            }
        }
        if (returnType != null){
            ITypeBinding returnTypeBinding = returnType.resolveBinding();
            if (returnTypeBinding != null){
                String qualifiedName = returnTypeBinding.getQualifiedName();
                if (manageExecutorAPI.contains(qualifiedName)){
                    usageOfJavaManageExecutor.add(qualifiedName);
                }else if (qualifiedName.contains(otherConcurrentAPI)){
                    usageOfOtherJavaEEConcurrent.add(qualifiedName);
                }
            }
        }
    }

    public void printInfo(){
        System.out.println("--- Java EE Concurrent KNowledge ---");
        System.out.println("Usage of Manage Executors: " + usageOfJavaManageExecutor.size());
        System.out.println("Usage of Other Concurrent API: " + usageOfOtherJavaEEConcurrent.size());
    }
}
