package com.xricoma.examples;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.DeliveryMode;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("message")
public class MessageSender {

    @Inject
    @JMSConnectionFactory("test-cfg")
    private JMSContext jmsContext;

    @Resource(mappedName = "test-dq1")
    private Queue queue;

    @GET
    @Path("send")
    @Produces(MediaType.TEXT_PLAIN)
    public String sendMessage(@QueryParam("message") String messageContent) {
        System.out.println("[MessageSender] - Message content: [" + messageContent + "]");
        System.out.println("[MessageSender] - Sending new message ...");

        JMSProducer jmsProducer = jmsContext.createProducer();
        jmsProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
        jmsProducer.send(queue, messageContent);

        System.out.println("[MessageSender] - Sended ...");

        return "Message has been sent!";
    }
}
