package com.sail.java.exam.topic.javaee;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sail.model.TopicExtractorBaseModel;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;

public class MessageServiceKnowledge extends TopicExtractorBaseModel {

    public String filePath = "";
    public CompilationUnit cu = null;

    boolean isTransactionEnabled = false;

    public List<String> producerSenderMessageAPIList = Arrays.asList("javax.jms.MessageProducer", "javax.jms.JMSProducer",
            "javax.jms.QueueSender", "javax.jms.MessageConsumer", "javax.jms.QueueReceiver",
            "javax.jms.MessageListener", "javax.jms.Message", "javax.jms.TextMessage", "javax.jms.StreamMessage",
            "javax.jms.MapMessage");

    public List<String> jmsProducerAPIList = Arrays.asList("javax.jms.MessageProducer", "javax.jms.JMSProducer",
            "javax.jms.QueueSender");
    public List<String> jmsConsumerAPIList = Arrays.asList("javax.jms.MessageConsumer", "javax.jms.QueueReceiver",
            "javax.jms.MessageListener");
    public List<String> jmsMessageAPIList = Arrays.asList("javax.jms.Message", "javax.jms.TextMessage",
            "javax.jms.StreamMessage", "javax.jms.MapMessage");

    public String otherJmsAPI = "javax.jms.";

    Set<String> usedProducerAPIList = new HashSet<String>();
    Set<String> usedConsumerAPIList = new HashSet<String>();
    Set<String> usedMessageAPIList = new HashSet<String>();
    Set<String> otherUsedJMSAPIList = new HashSet<String>();



    public Set<String> getUsedProducerAPIList() {
        return usedProducerAPIList;
    }
    public Set<String> getUsedConsumerAPIList() {
        return usedConsumerAPIList;
    }
    public Set<String> getUsedMessageAPIList() {
        return usedMessageAPIList;
    }
    
    public Set<String> getOtherUsedJMSAPIList() {
        return otherUsedJMSAPIList;
    }

    public MessageServiceKnowledge(String filePath, CompilationUnit cu) {
        this.filePath = filePath;
        this.cu = cu;
    }

    public boolean checkIfTransactionIsUsed() {
        return isTransactionEnabled;
    }

    public void extractTopic() {
        if (cu == null) {
            System.out.println("Problem in the Compilation Unit initialization...");
            return;
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
                //System.out.println("Arugment: " + qualifiedName + " Arguments: " + node.arguments().size() + " Name: "
                //        + node.getName());
                        
                if (jmsProducerAPIList.contains(qualifiedName)) {
                    usedProducerAPIList.add(qualifiedName);
                } else if (jmsConsumerAPIList.contains(qualifiedName)) {
                    usedConsumerAPIList.add(qualifiedName);
                } else if (jmsMessageAPIList.contains(qualifiedName)) {
                    usedMessageAPIList.add(qualifiedName);
                }else if (qualifiedName.contains("javax.jms.") && node.getName().toString().contains("Message")){
                    usedMessageAPIList.add(qualifiedName);
                } else if (qualifiedName.contains(otherJmsAPI) && !producerSenderMessageAPIList.contains(qualifiedName)){
                    otherUsedJMSAPIList.add(qualifiedName);
                }
                if (qualifiedName.contains("javax.jms.QueueConnection")
                        && node.getName().toString().contains("createQueueSession")) {
                    if (node.arguments().size() > 0) {
                        Expression firstArgument = (Expression) node.arguments().get(0);
                        if (firstArgument.toString().contains("true")) {
                            isTransactionEnabled = true;
                        }
                    }
                }
            }
        }
        super.visitMethodInvocation(node);
    }

    public void visitMethodDeclaration(MethodDeclaration node) {
        Type returnType = node.getReturnType2();
        for (int i = 0 ; i < node.parameters().size() ; i ++){
            SingleVariableDeclaration parameter = (SingleVariableDeclaration) node.parameters().get(i);
            Type parameterType = parameter.getType();
            if (parameterType != null){
                ITypeBinding parameterTypeBinding = parameterType.resolveBinding();
                if (parameterTypeBinding != null){
                    String qualifiedName = parameterTypeBinding.getQualifiedName();
                    if (jmsProducerAPIList.contains(qualifiedName)) {
                        usedProducerAPIList.add(qualifiedName);
                    } else if (jmsConsumerAPIList.contains(qualifiedName)) {
                        usedConsumerAPIList.add(qualifiedName);
                    } else if (jmsMessageAPIList.contains(qualifiedName)) {
                        usedMessageAPIList.add(qualifiedName);
                    }else if (qualifiedName.contains(otherJmsAPI) && node.getName().toString().contains("Message")){
                        usedMessageAPIList.add(qualifiedName);
                    } else if (qualifiedName.contains(otherJmsAPI) && !producerSenderMessageAPIList.contains(qualifiedName)){
                        otherUsedJMSAPIList.add(qualifiedName);
                    }
                }
            }
        }
        if (returnType != null){
            ITypeBinding returnTypeBinding = returnType.resolveBinding();
            if (returnTypeBinding != null){
                String qualifiedName = returnTypeBinding.getQualifiedName();
                if (jmsProducerAPIList.contains(qualifiedName)) {
                    usedProducerAPIList.add(qualifiedName);
                } else if (jmsConsumerAPIList.contains(qualifiedName)) {
                    usedConsumerAPIList.add(qualifiedName);
                } else if (jmsMessageAPIList.contains(qualifiedName)) {
                    usedMessageAPIList.add(qualifiedName);
                }else if (qualifiedName.contains(otherJmsAPI) && node.getName().toString().contains("Message")){
                    usedMessageAPIList.add(qualifiedName);
                } else if (qualifiedName.contains(otherJmsAPI) && !producerSenderMessageAPIList.contains(qualifiedName)){
                    otherUsedJMSAPIList.add(qualifiedName);
                }
            }
        }
    }
    public void printInfo(){
        System.out.println("----- Message Service Knowledge ----");
        System.out.println("Transaction: " + this.checkIfTransactionIsUsed());
        System.out.println("Message Producer: " + this.getUsedProducerAPIList().size());
        System.out.println("Message Sender: " + this.getUsedConsumerAPIList().size());
        System.out.println("Message: " + this.getUsedMessageAPIList().size());
        System.out.println("Other JMS API: " + this.otherUsedJMSAPIList.size());
    }
}
