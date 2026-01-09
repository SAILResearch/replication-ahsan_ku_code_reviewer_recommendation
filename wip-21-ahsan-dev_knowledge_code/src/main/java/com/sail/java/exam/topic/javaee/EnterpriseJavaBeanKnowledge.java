package com.sail.java.exam.topic.javaee;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sail.model.TopicExtractorBaseModel;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class EnterpriseJavaBeanKnowledge extends TopicExtractorBaseModel {
    public String filePath = "";
    public CompilationUnit cu = null;

    List<String> ejbSessionAPIList = Arrays.asList("javax.ejb.SessionBean", "javax.ejb.SessionContext",
            "javax.ejb.SessionSynchronization", "javax.ejb.Stateful", "javax.ejb.StatefulTimeout",
            "javax.ejb.Stateless", "javax.ejb.Asynchronous");

    List<String> ejbLifeCycleAPIList = Arrays.asList("javax.ejb.PostActivate", "javax.ejb.PrePassivate");

    List<String> ejbTransactionAPIList = Arrays.asList("javax.ejb.TransactionManagement",
            "javax.ejb.TransactionManagementType", "javax.ejb.TransactionAttribute");

    List<String> ejbTimerAPIList = Arrays.asList("javax.ejb.Timer", "javax.ejb.TimerHandle", "javax.ejb.TimerService",
            "javax.ejb.Timeout", "javax.ejb.StatefulTimeout", "javax.ejb.Schedule", "javax.ejb.Schedules",
            "javax.ejb.AccessTimeout");

    Set<String> usageOfJavaBeanTimerList = new HashSet<String>();
    Set<String> usageOfJavaBeanTransaction = new HashSet<String>();
    Set<String> usageOfJavaBeanSession = new HashSet<String>();
    Set<String> usageOfJavaBeanLifeCycle = new HashSet<String>();
    Set<String> usageOfOtherJavaBean = new HashSet<String>();

    public Set<String> getUsageOfJavaBeanTimerList() {
        return usageOfJavaBeanTimerList;
    }
    public Set<String> getUsageOfJavaBeanTransaction() {
        return usageOfJavaBeanTransaction;
    }
    public Set<String> getUsageOfJavaBeanSession() {
        return usageOfJavaBeanSession;
    }
    public Set<String> getUsageOfJavaBeanLifeCycle() {
        return usageOfJavaBeanLifeCycle;
    }
    public Set<String> getUsageOfOtherJavaBean() {
        return usageOfOtherJavaBean;
    }
    
    public EnterpriseJavaBeanKnowledge(String filePath, CompilationUnit cu) {
        this.filePath = filePath;
        this.cu = cu;
    }

    public void checkQualifiedName(String qualifiedName){
        if (ejbTimerAPIList.contains(qualifiedName)) {
            usageOfJavaBeanTimerList.add(qualifiedName);
        }
        else if (ejbTransactionAPIList.contains(qualifiedName)) {
            usageOfJavaBeanTransaction.add(qualifiedName);
        }
        else if (ejbSessionAPIList.contains(qualifiedName)) {
            usageOfJavaBeanSession.add(qualifiedName);
        }
        else if (ejbLifeCycleAPIList.contains(qualifiedName)) {
            usageOfJavaBeanLifeCycle.add(qualifiedName);
        }
        else if (qualifiedName.contains("javax.ejb.")){
            usageOfOtherJavaBean.add(qualifiedName);
        }
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
                    checkQualifiedName(qualifiedName);
                    
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
                checkQualifiedName(qualifiedName);
            }
        }
        super.visitMethodInvocation(node);
    }

    @Override
    public void visitFieldDeclaration(FieldDeclaration node) {
        Type fieldType = node.getType();
        if (fieldType != null){
            ITypeBinding fieldTypeBinding = fieldType.resolveBinding();
            if (fieldTypeBinding != null){
                String qualifiedName = fieldTypeBinding.getQualifiedName();
                checkQualifiedName(qualifiedName);
            }
           
        }
        super.visitFieldDeclaration(node);
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
        System.out.println("--- Java Enterprise Bean Knowledge ---");
        System.out.println("Usage of EJB Timer: " + usageOfJavaBeanTimerList.size());
        System.out.println("Usage of EJB Transaction: " + usageOfJavaBeanTransaction.size());
        System.out.println("Usage of EJB Session: " + usageOfJavaBeanSession.size());
        System.out.println("Usage of EJB LifeCycle: " + usageOfJavaBeanLifeCycle.size());
        System.out.println("Usage of other EJB: " + usageOfOtherJavaBean.size());
    }
}
