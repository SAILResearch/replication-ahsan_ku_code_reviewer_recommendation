package com.sail.java.exam.topic.javaee;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sail.model.TopicExtractorBaseModel;

import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class JavaCDIBeanKnowledge extends TopicExtractorBaseModel {
    public String filePath = "";
    public CompilationUnit cu = null;

    List<String> cdiProduceList = Arrays.asList("javax.enterprise.inject.Produces");
    List<String> cdiDisposeList = Arrays.asList("javax.enterprise.inject.Disposes");
    List<String> cdiSterotypeList = Arrays.asList("javax.enterprise.inject.Stereotype");
    List<String> cdiQualifierList = Arrays.asList("javax.inject.Qualifier");

    public List<String> otherInjectAPI = Arrays.asList("javax.enterprise.inject", "javax.inject.");

    Set<String> usageOfCDIProduce = new HashSet<String>();
    Set<String> usageOfCDIDispose = new HashSet<String>();
    Set<String> usageOfCDISterotype = new HashSet<String>();
    Set<String> usageOfCDIQualifier = new HashSet<String>();
    Set<String> usageOfOtherInjectAPI = new HashSet<String>();

    

    public Set<String> getUsageOfOtherInjectAPI() {
        return usageOfOtherInjectAPI;
    }
    public Set<String> getUsageOfCDIProduce() {
        return usageOfCDIProduce;
    }
    public Set<String> getUsageOfCDIDispose() {
        return usageOfCDIDispose;
    }
    public Set<String> getUsageOfCDISterotype() {
        return usageOfCDISterotype;
    }

    public Set<String> getUsageOfCDIQualifier() {
        return usageOfCDIQualifier;
    }
    
    public JavaCDIBeanKnowledge(String filePath, CompilationUnit cu) {
        this.filePath = filePath;
        this.cu = cu;
    }

    public void checkMatchType(String qualifiedName){
        if (cdiProduceList.contains(qualifiedName)){
            usageOfCDIProduce.add(qualifiedName);
        }else if (cdiDisposeList.contains(qualifiedName)){
            usageOfCDIDispose.add(qualifiedName);
        }else if (cdiSterotypeList.contains(qualifiedName)){
            usageOfCDISterotype.add(qualifiedName);
        }else if (cdiQualifierList.contains(qualifiedName)){
            usageOfCDIQualifier.add(qualifiedName);
        }else{
            for (String apiName : otherInjectAPI){
                if (qualifiedName.contains(apiName)){
                    usageOfOtherInjectAPI.add(qualifiedName);
                }
            }
        }
    }

    @Override
    public void visitTypeDeclaration(TypeDeclaration node) {
        //System.out.println("Type Declaration " + node.toString());
        //System.out.println("Type Declaration From Inside");
        for (int i = 0; i < node.modifiers().size(); i++) {
            IExtendedModifier mod = (IExtendedModifier) node.modifiers().get(i);
            //System.out.println(mod.toString());
            if (mod.isAnnotation()) {
                Expression modExpression = (Expression) mod;
                ITypeBinding modExprBinding = modExpression.resolveTypeBinding();
                if (modExprBinding != null) {
                    String qualifiedName = modExprBinding.getQualifiedName();
                    //System.out.println(modExpression.toString() + " " + qualifiedName);
                    checkMatchType(qualifiedName);
                }
            }
        }
    }

    @Override
    public void visitMethodDeclaration(MethodDeclaration node) {
        Type returnType = node.getReturnType2();
        for (int i = 0; i < node.modifiers().size(); i++) {
            IExtendedModifier mod = (IExtendedModifier) node.modifiers().get(i);
            if (mod.isAnnotation()) {
                Expression modExpression = (Expression) mod;
                ITypeBinding modExprBinding = modExpression.resolveTypeBinding();
                if (modExprBinding != null) {
                    String qualifiedName = modExprBinding.getQualifiedName();
                    //System.out.println(modExpression.toString() + " " + qualifiedName);
                    checkMatchType(qualifiedName);
                }
            }
        }
        for (int i = 0 ; i < node.parameters().size() ; i ++){
            SingleVariableDeclaration parameter = (SingleVariableDeclaration) node.parameters().get(i);
            Type parameterType = parameter.getType();
            if (parameterType != null){
                ITypeBinding parameterTypeBinding = parameterType.resolveBinding();
                if (parameterTypeBinding != null){
                    String qualifiedName = parameterTypeBinding.getQualifiedName();
                    checkMatchType(qualifiedName);
                }
                
            }
        }
        if (returnType != null){
            ITypeBinding returnTypeBinding = returnType.resolveBinding();
            if (returnTypeBinding != null){
                String qualifiedName = returnTypeBinding.getQualifiedName();
                checkMatchType(qualifiedName);
            }
        }
        super.visitMethodDeclaration(node);
    }
    
    @Override
    public void visitAnnotationTypeDeclaration(AnnotationTypeDeclaration node) {
        //System.out.println("Annotation Declaration: " + node.toString());
        super.visitAnnotationTypeDeclaration(node);
    }
    

    public void printInfo(){
        System.out.println("--- Java CDI Bean Knowledge ---");
        System.out.println("Uasge of Java CDI Produce: " + usageOfCDIProduce.size());
        System.out.println("Usage of Java CDI Dispose: " + usageOfCDIDispose.size());
        System.out.println("Usage of Java CDI Sterotype: " + usageOfCDISterotype.size());
        System.out.println("Usage of Java Qualifier: " + usageOfCDIQualifier.size());
        System.out.println("Usage of other inject api: " + usageOfOtherInjectAPI.size());
    }
}
