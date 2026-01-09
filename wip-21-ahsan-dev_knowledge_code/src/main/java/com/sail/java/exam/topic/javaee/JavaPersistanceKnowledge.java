package com.sail.java.exam.topic.javaee;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sail.model.TopicExtractorBaseModel;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class JavaPersistanceKnowledge extends TopicExtractorBaseModel{
    public String filePath = "";
    public CompilationUnit cu = null;

    public JavaPersistanceKnowledge (String filePath, CompilationUnit cu) {
        this.filePath = filePath;
        this.cu = cu;
    }

    List<String> persistanceEntityORMAPIList = Arrays.asList("javax.persistence.Entity",
    "javax.persistence.GeneratedValue", "javax.persistence.GenerationType", "javax.persistence.Id",
    "javax.persistence.EntityManager", "javax.persistence.EntityManager");
    
    List<String> jpaDatabaseOperatonAPIList = Arrays.asList("javax.persistence.EntityManager.createQuery",
    "javax.persistence.EntityManager.createNamedQuery", "javax.persistence.EntityManager.createNamedProcedureQuery",
    "javax.persistence.EntityManager.createNativeQuery", "javax.persistence.EntityManager.createStoredProcedureQuery",
    "javax.persistence.EntityManager.merge", "javax.persistence.EntityManager.find");

    List<String> javaJQLStatementList = Arrays.asList("javax.persistence.EntityManager.createQuery",
    "javax.persistence.EntityManager.createNamedQuery", "javax.persistence.EntityManager.createNamedProcedureQuery");

    List<String> JpaTransactionAPIList = Arrays.asList("javax.persistence.EntityTransaction.begin",
    "javax.persisetnce.EntityTransaction.commit", "javax.persistence.EntityTransaction.rollback",
    "javax.persistence.EntityTransaction.isActive", "javax.persistence.EntityTransaction.getRollbackOnly",
    "javax.persistence.EntityTransaction.setRollbackOnly");


    String otherJavaPersistanceAPI = "javax.persistence";

    Set<String> usageOfPersistenceEntityORM = new HashSet<String>();
    Set<String> usageOfPersistenceDatabase = new HashSet<String>();
    Set<String> usageOfPersistenceTransaction = new HashSet<String>();
    Set<String> usageOfJQLStatement = new HashSet<String>();
    Set<String> usageOfOtherJavaPersistence = new HashSet<String>();

    public Set<String> getUsageOfOtherJavaPersistence() {
        return usageOfOtherJavaPersistence;
    }

    public Set<String> getUsageOfPersistenceEntityORM() {
        return usageOfPersistenceEntityORM;
    }

    public Set<String> getUsageOfPersistenceDatabase() {
        return usageOfPersistenceDatabase;
    }

    public Set<String> getUsageOfPersistenceTransaction() {
        return usageOfPersistenceTransaction;
    }


    public Set<String> getUsageOfJQLStatement() {
        return usageOfJQLStatement;
    }

    @Override
    public void visitTypeDeclaration(TypeDeclaration node) {
        for (int i = 0; i < node.modifiers().size(); i++) {
            IExtendedModifier mod = (IExtendedModifier) node.modifiers().get(i);
            if (mod.isAnnotation()) {
                Expression modExpression = (Expression) mod;
                ITypeBinding modExprBinding = modExpression.resolveTypeBinding();
                if (modExprBinding != null) {
                    String qualifiedName = modExprBinding.getQualifiedName();
                    //System.out.println("Annotation: " + mod.toString() + " Binding: " + qualifiedName);
                    if (persistanceEntityORMAPIList.contains(qualifiedName)){
                        usageOfPersistenceEntityORM.add(qualifiedName);
                    }else if (qualifiedName.contains(otherJavaPersistanceAPI)){
                        usageOfOtherJavaPersistence.add(qualifiedName);
                    }
                }
            }
        }
        super.visitTypeDeclaration(node);
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
                if (jpaDatabaseOperatonAPIList.contains(qualifiedNameWithMethod)){
                    usageOfPersistenceDatabase.add(qualifiedNameWithMethod);
                }else if (javaJQLStatementList.contains(qualifiedNameWithMethod)){
                    usageOfJQLStatement.add(qualifiedNameWithMethod);
                }else if (JpaTransactionAPIList.contains(qualifiedNameWithMethod)){
                    usageOfPersistenceTransaction.add(qualifiedNameWithMethod);
                }else if (persistanceEntityORMAPIList.contains(qualifiedName)){
                    usageOfPersistenceEntityORM.add(qualifiedName);
                }else if (qualifiedName.contains(otherJavaPersistanceAPI)){
                    usageOfOtherJavaPersistence.add(qualifiedName);
                }
            }
        }
        super.visitMethodInvocation(node);
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
                        if (persistanceEntityORMAPIList.contains(qualifiedName)){
                            usageOfPersistenceEntityORM.add(qualifiedName);
                        }else if (qualifiedName.contains(otherJavaPersistanceAPI)){
                            usageOfOtherJavaPersistence.add(qualifiedName);
                        }
                    }
                }
            }
        }
        if (returnType != null){
            ITypeBinding returnTypeBinding = returnType.resolveBinding();
            if (returnTypeBinding != null){
                String qualifiedName = returnTypeBinding.getQualifiedName();
                if (persistanceEntityORMAPIList.contains(qualifiedName)){
                    usageOfPersistenceEntityORM.add(qualifiedName);
                }else if (qualifiedName.contains(otherJavaPersistanceAPI)){
                    usageOfOtherJavaPersistence.add(qualifiedName);
                }
            }
        }
    }

    public void printInfo(){
        System.out.println("--- Java Persistence Knowledge ---");
        System.out.println("Usage of Java Entity ORM: " + usageOfPersistenceEntityORM.size());
        System.out.println("Usage of JPA Transaction: " + usageOfPersistenceTransaction.size());
        System.out.println("Usage of JQL Statement: " + usageOfJQLStatement.size());
        System.out.println("Usage of JPA Database: " + usageOfPersistenceDatabase.size());
        System.out.println("Usage of other JPA: " + usageOfOtherJavaPersistence.size());

    }
}
