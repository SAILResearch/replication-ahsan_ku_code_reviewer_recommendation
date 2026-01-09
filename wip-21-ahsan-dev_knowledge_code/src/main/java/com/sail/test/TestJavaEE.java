package com.sail.test;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestJavaEE extends HttpServlet {
    ConnectionFactory connectionFactory;
    Destination inboundQueue;

    private boolean respondWithError = false;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (respondWithError) {
            resp.sendError(500, "Fake error");
        } else {

        }

        try {
            // get the initial context
            InitialContext ctx = new InitialContext();

            // lookup the queue object
            Queue queue = (Queue) ctx.lookup("queue/queue0");

            // lookup the queue connection factory
            QueueConnectionFactory connFactory = (QueueConnectionFactory) ctx.lookup("queue/connectionFactory");

            JMSContext context = connectionFactory.createContext(JMSContext.SESSION_TRANSACTED);

            // create a queue connection
            QueueConnection queueConn = connFactory.createQueueConnection();

            Session txSession = queueConn.createQueueSession(true, /* transacted session */
                    Session.AUTO_ACKNOWLEDGE /* IGNORED for transacted session */
            );
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void sendMessage(String text) {
        try {
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createProducer(inboundQueue);
            TextMessage textMessage = session.createTextMessage(text);
            messageProducer.send(textMessage);
        } catch (JMSException ex) {
            // . . .
        }
    }

    public void setRespondWithError(boolean respondWithError) {
        this.respondWithError = respondWithError;
        System.out.println("GOT");
    }
}