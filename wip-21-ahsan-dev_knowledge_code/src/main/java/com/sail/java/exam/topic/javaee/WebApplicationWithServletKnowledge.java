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
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class WebApplicationWithServletKnowledge extends TopicExtractorBaseModel{
    public String filePath = "";
    public CompilationUnit cu = null;

    public String otherServletAPI = "javax.servlet";
    public String JAVAX_SERVLET_PACKAGE = "javax.servlet.http";

    List<String> servletListenerList = Arrays.asList("javax.servlet.ServletContextAttributeListener",
    "javax.servlet.ServletContextListener", "javax.servlet.annotation.WebListener");

    List<String> servletCookieAPIList = Arrays.asList("javax.servlet.http.Cookie");
    List<String> servletHeaderAPIList = Arrays.asList("javax.servlet.http.HttpServletRequest.getHeader",
    "javax.servlet.http.HttpServletRequest.getHeaders",
    "javax.servlet.http.HttpServletRequest.getDateHeader", 
    "javax.servlet.http.HttpServletRequest.getHeaderNames",
    "javax.servlet.http.HttpServletResponse.addHader",
    "javax.servlet.http.HttpServletResponse.addIntHader",
    "javax.servlet.http.HttpServletResponse.setHeader",
    "javax.servlet.http.HttpServletResponse.setIntHader");

    Set<String> usageOfJavaServletHTTP = new HashSet<String>();
    Set<String> usageServletLifeCycle = new HashSet<String>();
    Set<String> usageOfCookie = new HashSet<String>();
    Set<String> usageOfHeaderAPI = new HashSet<String>();
    Set<String> usageOfOtherServletAPI = new HashSet<String>();


    public Set<String> getUsageOfOtherServletAPI() {
        return usageOfOtherServletAPI;
    }
    public Set<String> getUsageOfJavaServletHTTP() {
        return usageOfJavaServletHTTP;
    }
    public Set<String> getUsageServletLifeCycle() {
        return usageServletLifeCycle;
    }
    public Set<String> getUsageOfCookie() {
        return usageOfCookie;
    }
    public Set<String> getUsageOfHeaderAPI() {
        return usageOfHeaderAPI;
    }

    public WebApplicationWithServletKnowledge(String filePath, CompilationUnit cu) {
        this.filePath = filePath;
        this.cu = cu;
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

                //System.out.println("Invocation: " + qualifiedNameWithMethod + " Node: " + node.toString());

                if (qualifiedName.contains(JAVAX_SERVLET_PACKAGE)){
                    usageOfJavaServletHTTP.add(qualifiedName);
                }
                else if (servletCookieAPIList.contains(qualifiedName)){
                    usageOfCookie.add(qualifiedName);
                } else if (qualifiedName.contains(otherServletAPI)){
                    usageOfOtherServletAPI.add(qualifiedName);
                }
                if (servletHeaderAPIList.contains(qualifiedNameWithMethod)){
                    usageOfHeaderAPI.add(qualifiedNameWithMethod);
                }
            }
        }
        super.visitMethodInvocation(node);
    }
    
    @Override
    public void visitTypeDeclaration(TypeDeclaration node) {
        int startLine = cu.getLineNumber(node.getStartPosition());
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());

        for (int i = 0 ; i < node.superInterfaceTypes().size() ; i ++ ){
            Type superInterfaceType = (Type) node.superInterfaceTypes().get(i);
            ITypeBinding superInterfaceTypeBind = superInterfaceType.resolveBinding();
            if (superInterfaceTypeBind != null){
                String superInterfaceQualifiedName = superInterfaceTypeBind.getQualifiedName();
                if (servletListenerList.contains(superInterfaceQualifiedName)){
                    usageServletLifeCycle.add(superInterfaceQualifiedName);
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
                    if (parameterTypeBinding != null){
                        String qualifiedName = parameterTypeBinding.getQualifiedName();
                        //System.out.println("Qualified Name: "+ qualifiedName);
                        if (qualifiedName.contains(JAVAX_SERVLET_PACKAGE)){
                            usageOfJavaServletHTTP.add(qualifiedName);
                        }
                        else if (servletCookieAPIList.contains(qualifiedName)){
                            usageOfCookie.add(qualifiedName);
                        }else if (qualifiedName.contains(otherServletAPI)){
                            usageOfOtherServletAPI.add(qualifiedName);
                        }
                    }
                }
            }
            if (returnType != null){
                ITypeBinding returnTypeBinding = returnType.resolveBinding();
                if (returnTypeBinding != null){
                    String qualifiedName = returnTypeBinding.getQualifiedName();
                    if (qualifiedName.contains(JAVAX_SERVLET_PACKAGE)){
                        usageOfJavaServletHTTP.add(qualifiedName);
                    }
                    else if (servletCookieAPIList.contains(qualifiedName)){
                        usageOfCookie.add(qualifiedName);
                    }else if (qualifiedName.contains(otherServletAPI)){
                        usageOfOtherServletAPI.add(qualifiedName);
                    }
                }
            }
        }
        super.visitMethodDeclaration(node);
    }

    public void printInfo(){
        System.out.println("--- Java Servlet Knowledge Unit ---");
        System.out.println("Creation of Java Servlet: " + usageOfJavaServletHTTP.size());
        System.out.println("Usage of Servlet Lifecycle: " + usageServletLifeCycle.size());
        System.out.println("Usage of Servlet Header: " + usageOfHeaderAPI.size());
        System.out.println("Other Servlet API: " + usageOfOtherServletAPI.size());
    }
}
