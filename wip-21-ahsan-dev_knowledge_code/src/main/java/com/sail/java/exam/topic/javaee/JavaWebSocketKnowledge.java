package com.sail.java.exam.topic.javaee;

import java.util.HashSet;
import java.util.Set;

import com.sail.model.TopicExtractorBaseModel;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class JavaWebSocketKnowledge extends TopicExtractorBaseModel {
    public String filePath = "";
    public CompilationUnit cu = null;

    public JavaWebSocketKnowledge(String filePath, CompilationUnit cu) {
        this.filePath = filePath;
        this.cu = cu;
    }

    public String JAVA_WEB_SOCKET_PACKAGE = "javax.websocket";
    public String WEBSOCKET_ENCODER_API = "javax.websocket.Encoder";
    public String WEBSOCKET_DECODER_API = "javax.websocket.Decoder";

    boolean createServerEndPoint = false;
    boolean createClientEndPoint = false;

    Set<String> usageJavaWebSocket = new HashSet<String>();
    Set<String> usageOfWebsocketMessageEncoder = new HashSet<String>();
    Set<String> usageOfWebsocketMessageDecoder = new HashSet<String>();

    
    public Set<String> getUsageJavaWebSocket() {
        return usageJavaWebSocket;
    }
    public Set<String> getUsageOfWebsocketMessageEncoder() {
        return usageOfWebsocketMessageEncoder;
    }

    public Set<String> getUsageOfWebsocketMessageDecoder() {
        return usageOfWebsocketMessageDecoder;
    }

    public void setUsageOfWebsocketMessageDecoder(Set<String> usageOfWebsocketMessageDecoder) {
        this.usageOfWebsocketMessageDecoder = usageOfWebsocketMessageDecoder;
    }

    @Override
    public void visitTypeDeclaration(TypeDeclaration node) {
        // Check it implements Decoder and Encoder Interface of the
        // Javax.websocket.Decoder

        for (int i = 0; i < node.superInterfaceTypes().size(); i++) {
            Type superType = (Type) node.superInterfaceTypes().get(i);
            ITypeBinding superTypeBinding = superType.resolveBinding();
            if (superTypeBinding != null) {
                String qualifiedName = superTypeBinding.getQualifiedName();
                if (qualifiedName.contains(WEBSOCKET_DECODER_API)) {
                    usageOfWebsocketMessageDecoder.add(qualifiedName);
                } else if (qualifiedName.contains(WEBSOCKET_ENCODER_API)) {
                    usageOfWebsocketMessageEncoder.add(qualifiedName);
                }
            }
        }

        for (int i = 0; i < node.modifiers().size(); i++) {
            IExtendedModifier mod = (IExtendedModifier) node.modifiers().get(i);
            if (mod.isAnnotation()) {
                // System.out.println("Annotation: " + mod.toString());
                if (mod.toString().contains("ServerEndpoint")) {
                    createServerEndPoint = true;
                } else if (mod.toString().contains("ClientEndpoint")) {
                    createClientEndPoint = true;
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
                // System.out.println(node.toString() + " " + qualifiedNameWithMethod);
                if (qualifiedName.contains(JAVA_WEB_SOCKET_PACKAGE)) {
                    usageJavaWebSocket.add(qualifiedName);
                } else if (qualifiedName.contains(WEBSOCKET_ENCODER_API)) {
                    System.out.println("GOT ENCODER");
                    usageOfWebsocketMessageEncoder.add(qualifiedName);
                } else if (qualifiedName.contains(WEBSOCKET_DECODER_API)) {
                    System.out.println("GOT DECODER");
                    usageOfWebsocketMessageDecoder.add(qualifiedName);
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
                    if (qualifiedName.contains(JAVA_WEB_SOCKET_PACKAGE)) {
                        usageJavaWebSocket.add(qualifiedName);
                    } else if (qualifiedName.contains(WEBSOCKET_ENCODER_API)) {
                        System.out.println("GOT ENCODER");
                        usageOfWebsocketMessageEncoder.add(qualifiedName);
                    } else if (qualifiedName.contains(WEBSOCKET_DECODER_API)) {
                        System.out.println("GOT DECODER");
                        usageOfWebsocketMessageDecoder.add(qualifiedName);
                    }
                }
            }
        }
        if (returnType != null){
            ITypeBinding returnTypeBinding = returnType.resolveBinding();
            if (returnTypeBinding != null){
                String qualifiedName = returnTypeBinding.getQualifiedName();
                if (qualifiedName.contains(JAVA_WEB_SOCKET_PACKAGE)) {
                    usageJavaWebSocket.add(qualifiedName);
                } else if (qualifiedName.contains(WEBSOCKET_ENCODER_API)) {
                    //System.out.println("GOT ENCODER");
                    usageOfWebsocketMessageEncoder.add(qualifiedName);
                } else if (qualifiedName.contains(WEBSOCKET_DECODER_API)) {
                    //System.out.println("GOT DECODER");
                    usageOfWebsocketMessageDecoder.add(qualifiedName);
                }
            }
        }
    }

    public void printInfo() {
        System.out.println("--- Java Websocket Knowledge ---");
        System.out.println("Create ServerEndpoint: " + createServerEndPoint);
        System.out.println("Create ClientEndpoint: " + createClientEndPoint);
        System.out.println("Usage Java Weboscket API: " + usageJavaWebSocket.size());
        System.out.println("Usage of Encoder: " + usageOfWebsocketMessageEncoder.size());
        System.out.println("Usage of Decoder: " + usageOfWebsocketMessageDecoder.size());
    }
}
