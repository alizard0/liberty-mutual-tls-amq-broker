package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.resource.ResourceException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/consume", name = "ConsumerServlet")
public class ConsumerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ConsumerServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            response.getWriter().append(consumeMessage());
        } catch (NamingException | ResourceException | JMSException e) {
            throw new RuntimeException(e);
        }
    }

    private String consumeMessage() throws NamingException, ResourceException, JMSException {
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

        LOG.info("createConsumer()");
        final MessageConsumer consumer = session.createConsumer(queue);
        LOG.info("receive()");
        final Message received = consumer.receive(5000);

        LOG.info("close()");
        connection.close();

        return received == null ? "null" : received.getBody(String.class);
    }

    private String getJndiPropertyAsString(final String propertyName) throws NamingException {
        Object jndiConstant = new InitialContext().lookup(propertyName);
        return (String) jndiConstant;
    }
}
