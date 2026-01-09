package com.sail.test;

import java.io.IOException;
import java.util.Enumeration;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/endpoint")
public class TestJavaWebSocket {
    
    //private static PushTimeService pst;
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("onOpen::" + session.getId());        
    }
    @OnClose
    public void onClose(Session session) {
        System.out.println("onClose::" +  session.getId());
    }
    
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("onMessage::From=" + session.getId() + " Message=" + message);
        
        try {
            session.getBasicRemote().sendText("Hello Client " + session.getId() + "!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @OnError
    public void onError(Throwable t) {
        System.out.println("onError::" + t.getMessage());
    }

 class MessageA implements Message{
     public MessageA(){
         System.out.println("Constructor");
     }

    @Override
    public void acknowledge() throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clearBody() throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clearProperties() throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public <T> T getBody(Class<T> arg0) throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getBooleanProperty(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public byte getByteProperty(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getDoubleProperty(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float getFloatProperty(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getIntProperty(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getJMSCorrelationID() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getJMSDeliveryMode() throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getJMSDeliveryTime() throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Destination getJMSDestination() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getJMSExpiration() throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getJMSMessageID() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getJMSPriority() throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean getJMSRedelivered() throws JMSException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Destination getJMSReplyTo() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getJMSTimestamp() throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getJMSType() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getLongProperty(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Object getObjectProperty(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Enumeration getPropertyNames() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public short getShortProperty(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getStringProperty(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isBodyAssignableTo(Class arg0) throws JMSException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean propertyExists(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setBooleanProperty(String arg0, boolean arg1) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setByteProperty(String arg0, byte arg1) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setDoubleProperty(String arg0, double arg1) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setFloatProperty(String arg0, float arg1) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setIntProperty(String arg0, int arg1) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setJMSCorrelationID(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setJMSCorrelationIDAsBytes(byte[] arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setJMSDeliveryMode(int arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setJMSDeliveryTime(long arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setJMSDestination(Destination arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setJMSExpiration(long arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setJMSMessageID(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setJMSPriority(int arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setJMSRedelivered(boolean arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setJMSReplyTo(Destination arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setJMSTimestamp(long arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setJMSType(String arg0) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setLongProperty(String arg0, long arg1) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setObjectProperty(String arg0, Object arg1) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setShortProperty(String arg0, short arg1) throws JMSException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setStringProperty(String arg0, String arg1) throws JMSException {
        // TODO Auto-generated method stub
        
    }
 }
 class MessageATextEncoder implements Encoder.Text<MessageA> {
        @Override
        public void init(EndpointConfig ec) { }
        @Override
        public void destroy() { }
        @Override
        public String encode(MessageA msgA) throws EncodeException {
           // Access msgA's properties and convert to JSON text...
           return "msgAJsonString";
        }
     }

     class MessageTextDecoder implements Decoder.Text<Message> {
        @Override
        public void init(EndpointConfig ec) { }
        @Override
        public void destroy() { }
        @Override
        public Message decode(String string) throws DecodeException {
           // Read message...
           if ( true)
              return new MessageA();
           else
              return new MessageA();
        }
        @Override
        public boolean willDecode(String string) {
           // Determine if the message can be converted into either a
           // MessageA object or a MessageB object...
           return true;
        }
     }
}