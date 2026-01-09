package com.sail.test;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

enum MessageTransportType {
    EMAIL, SMS;
}

interface MessageSender {

    void sendMessage();

}

class EmailMessageSender implements MessageSender {

    @Override
    public void sendMessage() {
        System.out.println("Sending email message");
    }

}

class SmsMessageSender implements MessageSender {

    @Override
    public void sendMessage() {
        System.out.println("Sending SMS message");
    }

}

@SessionScoped
class MessageSenderFactory implements Serializable {

    private static final long serialVersionUID = 5269302440619391616L;

    private MessageTransportType messageTransportType;

    @Produces
    public MessageSender getMessageSender() {

        switch (messageTransportType) {

        case EMAIL:
            return new EmailMessageSender();

        case SMS:
        default:
            return new SmsMessageSender();

        }
    }
}

@Qualifier
@Retention(RUNTIME)
@Target({FIELD, TYPE, METHOD})

@interface MessageTransport {

  MessageTransportType value();
	
}

public class TestJavaCDIBean {
    @Inject
    private MessageSender messageSender;
}
