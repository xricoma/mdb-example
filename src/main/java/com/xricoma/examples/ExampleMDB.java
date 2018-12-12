package com.xricoma.examples;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

@MessageDriven(activationConfig =  {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destinationJNDIName", propertyValue="test-dq1"),
        @ActivationConfigProperty(propertyName = "connectionFactoryJndiName", propertyValue = "test-cfg"),
        @ActivationConfigProperty(propertyName = "dispatch-Policy", propertyValue = "test-wm-mdb") })

public class ExampleMDB implements MessageListener
{
    @Resource
    private MessageDrivenContext context;

    @Override
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public void onMessage(Message message) {
        System.out.println("[ExampleMDB] - Processing new message...");

        try {
            String messageContent = message.getBody(String.class);

            System.out.println("[ExampleMDB] - Message: [" + message + "]");
            System.out.println("[ExampleMDB] - Message content: [" + messageContent + "]");
        } catch (JMSException e)
        {
            context.setRollbackOnly();
        }
    }
}
