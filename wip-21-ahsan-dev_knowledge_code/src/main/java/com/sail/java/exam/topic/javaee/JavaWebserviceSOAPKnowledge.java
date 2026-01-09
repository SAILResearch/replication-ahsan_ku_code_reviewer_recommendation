package com.sail.java.exam.topic.javaee;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sail.model.TopicExtractorBaseModel;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;

public class JavaWebserviceSOAPKnowledge extends TopicExtractorBaseModel{
    public String filePath = "";
    public CompilationUnit cu = null;


    List<String> soapAPIList = Arrays.asList("javax.xml.soap", "javax.xml.ws.soap",
    "javax.xml.ws.handler.soap");

    List<String> marshallUnmarshallAPIList = Arrays.asList(
    "javax.xml.bind.JAXBContext.createMarshaller",
    "javax.xml.bind.JAXBContext.createUnmarshaller", 
    "javax.xml.blind.Marshaller",
    "javax.xml.blind.Marshaller.Listener",
    "javax.xml.blind.Unmarshaller",
    "javax.xml.blind.UnarshallerHandler");

    Set<String> usageOfMarshallUnmarshallList = new HashSet<String>();
    Set<String> creationOfSOAPList = new HashSet<String>();

    public Set<String> getUsageOfMarshallUnmarshallList() {
        return usageOfMarshallUnmarshallList;
    }

    public Set<String> getCreationOfSOAPList() {
        return creationOfSOAPList;
    }
    
    public JavaWebserviceSOAPKnowledge(String filePath, CompilationUnit cu) {
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
                
                //System.out.println("Invoked Method Qualified Name: " + qualifiedNameWithMethod);

                if (marshallUnmarshallAPIList.contains(qualifiedName) || 
                marshallUnmarshallAPIList.contains(qualifiedNameWithMethod)){
                    usageOfMarshallUnmarshallList.add(qualifiedNameWithMethod);
                }

                for (String item : soapAPIList){
                    if (qualifiedName.contains(item)){
                        creationOfSOAPList.add(qualifiedName);
                    }
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
                    String qualifiedName = parameterTypeBinding.getQualifiedName();
                    if (marshallUnmarshallAPIList.contains(qualifiedName)){
                        usageOfMarshallUnmarshallList.add(qualifiedName);
                    }
                    for (String item : soapAPIList){
                        if (qualifiedName.contains(item)){
                            creationOfSOAPList.add(qualifiedName);
                        }
                    }
                }
            }
        }
        if (returnType != null){
            ITypeBinding returnTypeBinding = returnType.resolveBinding();
            if (returnTypeBinding != null){
                String qualifiedName = returnTypeBinding.getQualifiedName();
                for (String item : soapAPIList){
                    if (qualifiedName.contains(item)){
                        creationOfSOAPList.add(qualifiedName);
                    }
                }
            }
        }
    }
    public void printInfo(){
        System.out.println("--- JavaWebserviceSOAPKnowledge ---");
        System.out.println("Creation of SOAP: " + creationOfSOAPList.size());
        System.out.println("Usage of marshall unmarshall: " + usageOfMarshallUnmarshallList.size());

    }
}
