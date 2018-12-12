package com.xricoma.examples;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationJNDIName", propertyValue = "test-dq3"),
        @ActivationConfigProperty(propertyName = "connectionFactoryJndiName", propertyValue = "test-cfg") })

public class ExampleMDB_JMSReplyTo implements MessageListener {

    @Resource
    private MessageDrivenContext context;

    @Resource(name = "test-cfg")
    private ConnectionFactory cf;

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public void onMessage(final Message message) {
        Connection connection = null;

        System.out.println("[ExampleMDB_JMSReplyTo] - Processing new message...");

        try {
            connection = cf.createConnection();

            Destination replyTo = message.getJMSReplyTo();
            if (replyTo != null) {
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer producer = session.createProducer(replyTo);
                String replyText = ((TextMessage) message).getText();

                Message reply = session.createTextMessage(replyText);
                reply.setJMSCorrelationID(message.getJMSMessageID());
                producer.send(reply);
            }
            String messageContent = message.getBody(String.class);
            System.out.println("[ExampleMDB_JMSReplyTo] - Message: [" + message + "]");
            System.out.println("[ExampleMDB_JMSReplyTo] - Message content: [" + messageContent + "]");
        } catch (Exception e) {
            context.setRollbackOnly();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}