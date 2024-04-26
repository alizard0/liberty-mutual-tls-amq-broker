package com.example;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.ra.ActiveMQResourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveMQResourceAdapterConfigurable extends ActiveMQResourceAdapter {
    /** UID */
    private static final long serialVersionUID = -6171434152622982474L;
    private static final Logger LOG = LoggerFactory.getLogger(ActiveMQResourceAdapterConfigurable.class);

    private final String connectionParameters;

    public ActiveMQResourceAdapterConfigurable(final String connectionParameters) {
        this.connectionParameters = connectionParameters;
    }

    @Override
    protected void setup() throws ActiveMQException {
        // Establecemos las propiedades
        setConnectorClassName("org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory");
        LOG.info(connectionParameters);
        setConnectionParameters(connectionParameters);

        super.setup();
    }
}