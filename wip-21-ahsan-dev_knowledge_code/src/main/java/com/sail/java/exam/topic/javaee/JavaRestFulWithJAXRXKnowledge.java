package com.sail.java.exam.topic.javaee;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sail.model.TopicExtractorBaseModel;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class JavaRestFulWithJAXRXKnowledge extends TopicExtractorBaseModel {
    public String filePath = "";
    public CompilationUnit cu = null;

    public JavaRestFulWithJAXRXKnowledge(String filePath, CompilationUnit cu) {
        this.filePath = filePath;
        this.cu = cu;
    }

    public String JAVA_RX_CLIENT = "javax.ws.rs.client";
    List<String> jaxRXServiceAPIList = Arrays.asList("javax.ws.rs.client", "javax.ws.rs.core", "javax.ws.rs");

    Set<String> usageOfJavaRXClient = new HashSet<String>();
    Set<String> usageOfJavaRXService = new HashSet<String>();

    public Set<String> getUsageOfJavaRXClient() {
        return usageOfJavaRXClient;
    }

    public Set<String> getUsageOfJavaRXService() {
        return usageOfJavaRXService;
    }

    public void checkQualifiedName(String qualifiedName){
        if (qualifiedName.contains(JAVA_RX_CLIENT)) {
            usageOfJavaRXClient.add(qualifiedName);
        }
        for (String apiName : jaxRXServiceAPIList){
            if (qualifiedName.contains(apiName)){
                usageOfJavaRXService.add(qualifiedName);
            }
        }
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
                checkQualifiedName(qualifiedName);
                

                /*for (int j = 0 ; j < node.arguments().size() ; j ++ ){
                    Expression arg = (Expression) node.arguments().get(j);
                    ITypeBinding argTypeBinding =  arg.resolveTypeBinding();
                    if (argTypeBinding != null){
                        String argQualifiedName = argTypeBinding.getQualifiedName();
                        System.out.println(arg.toString() + " " + argQualifiedName);
                        if (argQualifiedName.contains(JAVA_RX_CLIENT)){
                            usageOfJavaRXClient.add(qualifiedName);
                        }else if (jaxRXServiceAPIList.contains(argQualifiedName)){
                            usageOfJavaRXService.add(qualifiedName);
                        }
                    }
                }*/
            }
        }
    }

    @Override
    public void visitVariableDeclarationFragment(VariableDeclarationFragment node) {
        int startLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
        IVariableBinding variableBinding = node.resolveBinding();
        if (variableBinding != null) {
            ITypeBinding classBinding = variableBinding.getDeclaringClass();

            if (classBinding != null) {
                String qualifiedClassName = classBinding.getQualifiedName().toString();
                checkQualifiedName(qualifiedClassName);
            }
        }
        super.visitVariableDeclarationFragment(node);
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
                    checkQualifiedName(qualifiedName);
                }
            }
        }
        if (returnType != null){
            ITypeBinding returnTypeBinding = returnType.resolveBinding();
            if (returnTypeBinding != null){
                String qualifiedName = returnTypeBinding.getQualifiedName();
                checkQualifiedName(qualifiedName);
            }
        }
    }

    public void printInfo() {
        System.out.println("--- Java RX REST Knowledge ---");
        System.out.println("Create REST Client: " + usageOfJavaRXClient.size());
        System.out.println("Create REST Service: " + usageOfJavaRXService.size());
    }
}
