package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.resource.ResourceException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/produce", name = "ProducerServlet")
public class ProducerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ProducerServlet.class);
    private static final int NUMBER_OF_DUPLICATED_MESSAGES_SENT = 1000;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            response.getWriter().append(produceMessage());
        } catch (NamingException | ResourceException | JMSException e) {
            throw new RuntimeException(e);
        }
    }

    private String produceMessage() throws NamingException, ResourceException, JMSException {
        String brokerUrl = getJndiPropertyAsString("secureConnectionFactory");
        LOG.info("new ActiveMQResourceAdapter() connection: " + brokerUrl);
        final ActiveMQResourceAdapterConfigurable ra =
                new ActiveMQResourceAdapterConfigurable(brokerUrl);

        LOG.info("getDefaultActiveMQConnectionFactory()");
        final ConnectionFactory connFactory = ra.getDefaultActiveMQConnectionFactory();

        LOG.info("createConnection()");
        final Connection connection = connFactory.createConnection();
        LOG.info("start()");
        connection.start();

        LOG.info("createSession()");
        final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        String queueName = getJndiPropertyAsString("queue");
        LOG.info("createQueue(): " + queueName);
        final Queue queue = session.createQueue(queueName);

        LOG.info("createObjectMessage()");
        final ObjectMessage msg = session.createObjectMessage();


        LOG.info("createProducer()");
        final MessageProducer producer = session.createProducer(queue);

        for (int i=0; i < NUMBER_OF_DUPLICATED_MESSAGES_SENT; i++) {
            LOG.info("send()");
            msg.setObject("ArtemisMQ Rocks! id: " + i);
            producer.send(msg);
        }

        LOG.info("close()");
        connection.close();

        return NUMBER_OF_DUPLICATED_MESSAGES_SENT + " messages produced!";
    }

    private String getJndiPropertyAsString(final String propertyName) throws NamingException {
        Object jndiConstant = new InitialContext().lookup(propertyName);
        return (String) jndiConstant;
    }
}
