package com.sail.java.exam.topic.javaee;

import java.util.HashSet;
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

public class JavaJFSKnowledge extends TopicExtractorBaseModel {
    public String filePath = "";
    public CompilationUnit cu = null;

    public String JAVA_FACE_PACKAGE = "javax.faces";

    public String JAVA_FACE_MODEL_PACKAGE = "javax.faces.model";
    public String JAVA_FACE_VALIDATOR_PACKAGE = "javax.faces.validator";
    public String JAVA_FACE_RENDER_PACKAGE = "javax.faces.render";
    public String JAVA_FACE_LIFECYCLE_PACKAGE = "javax.faces.lifecycle";

    Set<String> usageOfJavaFaceModel = new HashSet<String>();
    Set<String> usageOfJavaFaceValidator = new HashSet<String>();
    Set<String> usageOfJavaFaceRender = new HashSet<String>();
    Set<String> usageOfJavaFaceLifecycle = new HashSet<String>();
    Set<String> usageOfOtherJavaFaceAPI = new HashSet<String>();


    public Set<String> getUsageOfJavaFaceModel() {
        return usageOfJavaFaceModel;
    }

    public Set<String> getUsageOfJavaFaceValidator() {
        return usageOfJavaFaceValidator;
    }


    public Set<String> getUsageOfJavaFaceRender() {
        return usageOfJavaFaceRender;
    }

    public Set<String> getUsageOfJavaFaceLifecycle() {
        return usageOfJavaFaceLifecycle;
    }
    public Set<String> getUsageOfOtherJavaFaceAPI() {
        return usageOfOtherJavaFaceAPI;
    }

    public JavaJFSKnowledge(String filePath, CompilationUnit cu) {
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
                if (qualifiedName.contains(JAVA_FACE_MODEL_PACKAGE)) {
                    usageOfJavaFaceModel.add(qualifiedName);
                } else if (qualifiedName.contains(JAVA_FACE_LIFECYCLE_PACKAGE)) {
                    usageOfJavaFaceLifecycle.add(qualifiedName);
                } else if (qualifiedName.contains(JAVA_FACE_RENDER_PACKAGE)) {
                    usageOfJavaFaceRender.add(qualifiedName);
                } else if (qualifiedName.contains(JAVA_FACE_VALIDATOR_PACKAGE)) {
                    usageOfJavaFaceValidator.add(qualifiedName);
                } else if (qualifiedName.contains(JAVA_FACE_PACKAGE)) {
                    usageOfOtherJavaFaceAPI.add(qualifiedName);
                }
            }
        }
        super.visitMethodInvocation(node);
    }

    @Override
    public void visitVariableDeclarationFragment(VariableDeclarationFragment node) {
        IVariableBinding variableBind = node.resolveBinding();
        if (variableBind != null) {
            ITypeBinding varClassBind = variableBind.getDeclaringClass();
            if (varClassBind != null) {
                String qualifiedName = varClassBind.getQualifiedName();
                if (qualifiedName.contains(JAVA_FACE_MODEL_PACKAGE)) {
                    usageOfJavaFaceModel.add(qualifiedName);
                } else if (qualifiedName.contains(JAVA_FACE_LIFECYCLE_PACKAGE)) {
                    usageOfJavaFaceLifecycle.add(qualifiedName);
                } else if (qualifiedName.contains(JAVA_FACE_RENDER_PACKAGE)) {
                    usageOfJavaFaceRender.add(qualifiedName);
                } else if (qualifiedName.contains(JAVA_FACE_VALIDATOR_PACKAGE)) {
                    usageOfJavaFaceValidator.add(qualifiedName);
                } else if (qualifiedName.contains(JAVA_FACE_PACKAGE)) {
                    usageOfOtherJavaFaceAPI.add(qualifiedName);
                }
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
                    if (qualifiedName.contains(JAVA_FACE_MODEL_PACKAGE)) {
                        usageOfJavaFaceModel.add(qualifiedName);
                    } else if (qualifiedName.contains(JAVA_FACE_LIFECYCLE_PACKAGE)) {
                        usageOfJavaFaceLifecycle.add(qualifiedName);
                    } else if (qualifiedName.contains(JAVA_FACE_RENDER_PACKAGE)) {
                        usageOfJavaFaceRender.add(qualifiedName);
                    } else if (qualifiedName.contains(JAVA_FACE_VALIDATOR_PACKAGE)) {
                        usageOfJavaFaceValidator.add(qualifiedName);
                    } else if (qualifiedName.contains(JAVA_FACE_PACKAGE)) {
                        usageOfOtherJavaFaceAPI.add(qualifiedName);
                    }
                }
            }
        }
        if (returnType != null){
            ITypeBinding returnTypeBinding = returnType.resolveBinding();
            if (returnTypeBinding != null){
                String qualifiedName = returnTypeBinding.getQualifiedName();
                if (qualifiedName.contains(JAVA_FACE_MODEL_PACKAGE)) {
                    usageOfJavaFaceModel.add(qualifiedName);
                } else if (qualifiedName.contains(JAVA_FACE_LIFECYCLE_PACKAGE)) {
                    usageOfJavaFaceLifecycle.add(qualifiedName);
                } else if (qualifiedName.contains(JAVA_FACE_RENDER_PACKAGE)) {
                    usageOfJavaFaceRender.add(qualifiedName);
                } else if (qualifiedName.contains(JAVA_FACE_VALIDATOR_PACKAGE)) {
                    usageOfJavaFaceValidator.add(qualifiedName);
                } else if (qualifiedName.contains(JAVA_FACE_PACKAGE)) {
                    usageOfOtherJavaFaceAPI.add(qualifiedName);
                }
            }
        }
    }
    public void printInfo() {
        System.out.println("--- JavaJFSKnowledge ---");
        System.out.println("Usage of Java Face Model: " + usageOfJavaFaceModel.size());
        System.out.println("Usage of Java Face Lifecycle: " + usageOfJavaFaceLifecycle.size());
        System.out.println("Usage of Java Face Render: " + usageOfJavaFaceRender.size());
        System.out.println("Usage of Java Face Validator: " + usageOfJavaFaceValidator.size());
        System.out.println("Usage of Other Java Face API: " + usageOfOtherJavaFaceAPI.size());
    }
}
