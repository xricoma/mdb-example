package com.xricoma.examples;

// MDB Sample
// --------------------------
// Using Annotations shows a WebLogic MDB that uses a subscription to a WebLogic JMS queue (from WebLogic Server 10.3.4or later),
// transactionally processes the messages, and forwards the messages to a target destination.

// The MDB connects using JMS connection factory MyCF to receive from queue MyQueue. It forwards the messages to MyTargetDest using
// a connection generated from connection factory MyTargetCF.

// Resource reference pooling note: The MDB uses a resource reference to access MyTargetCF. The resource reference automatically
// enables JMS producer pooling, as described in "Enhanced Support for Using WebLogic JMS with EJBs and Servlets" in Programming JMS
// for Oracle WebLogic Server.

// For a similar sample using topics instead of queues,see Example 10-1, "Sample MDB Using Distributed Topics".

import javax.annotation.Resources;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.*;

@MessageDriven(name = "MyMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "connectionFactoryJndiName", propertyValue = "test-cfgXA"), // MyCF - External JNDI Name XA
        @ActivationConfigProperty(propertyName = "destinationJndiName", propertyValue = "test-dq2") // MyQueue - Ext. JNDI Name
})

@Resources({ @Resource(name = "targetCFRef", mappedName = "test-cfgXA", // MyTargetCF - External JNDI name
        type = javax.jms.ConnectionFactory.class),

        @Resource(name = "targetDestRef", mappedName = "test-dq4", // MyTargetDest - External JNDI name
                type = javax.jms.Destination.class) })

public class MyMDB implements MessageListener {

    // inject a reference to the MDB context
    @Resource
    private MessageDrivenContext mdctx;

    // cache targetCF and targetDest for re-use (performance)
    private ConnectionFactory targetCF;
    private Destination targetDest;

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public void onMessage(Message message) {
        System.out.println("[MyMDB] - Processing new message...");

        // Forward the message to "MyTargetDest" using "MyTargetCF"
        Connection jmsConnection = null;

        try {
            if (targetCF == null) targetCF = (javax.jms.ConnectionFactory) mdctx.lookup("targetCFRef");
            if (targetDest == null) targetDest = (javax.jms.Destination) mdctx.lookup("targetDestRef");

            jmsConnection = targetCF.createConnection();
            Session s = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer mp = s.createProducer(null);

            mp.send(targetDest, message);

            String messageContent = message.getBody(String.class);

            System.out.println("[MyMDB] - Message: [" + message + "]");
            System.out.println("[MyMDB] - Message content: [" + messageContent + "]");
        } catch (JMSException e) {
            System.out.println("Forcing rollback due to exception " + e);
            e.printStackTrace();
            mdctx.setRollbackOnly();
        } finally {
            // Closing a connection automatically returns the connection and its session plus producer to the resource reference pool.
            try { if (jmsConnection != null) jmsConnection.close(); }
            catch (JMSException ignored) { };
        }

        // emulate 1 second of "think" time
        try { Thread.currentThread().sleep(1000); }
        catch (InterruptedException ie)
        {
            Thread.currentThread().interrupt(); // Restore the interrupted status
        }
    }
}