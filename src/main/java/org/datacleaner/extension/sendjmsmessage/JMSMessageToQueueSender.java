package org.datacleaner.extension.sendjmsmessage;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.activemq.pool.PooledConnectionFactory;

import javax.jms.QueueConnectionFactory;
/**
 * Implementation for sending JMS messages to a queue.
 * 
 */
public class JMSMessageToQueueSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(JMSMessageToQueueSender.class);
    private ProducerTemplate producerTemplate;
    private CamelContext camelContext;

    /**
     * Constructor
     * 
     * @param broker
     * @param queueName
     * @param brokerType
     * @throws Exception
     */
    public JMSMessageToQueueSender(String broker, String queueName, String brokerType) throws Exception {
        camelContext = new DefaultCamelContext();


        if (brokerType.equals("Terracotta Universal Messaging")) {
            new JMSUniversalMessaging(broker, queueName).invoke();
        } else {
            new JMSApacheMQ(broker, queueName).invoke();
        }
    }

    /**
     * Send a message to a JMS queue.
     * 
     * @param brokerUrl
     * @param jmsQueueName
     * @param messageBody
     * @param correlationId
     * @return
     */
    public SendMessageToJMSQueueResult sendMessage(String brokerUrl, String jmsQueueName, String messageBody, String correlationId) {
        LOGGER.debug("Broker URL {}, jmsQueueName {}, messageBody {}, correlationId {}", brokerUrl, jmsQueueName, messageBody, correlationId);
        producerTemplate.sendBodyAndHeader("direct:start", messageBody, "JMSCorrelationID", correlationId);
        return SendMessageToJMSQueueResult.success();
    }

    /**
     * Close the camel related resources.
     * 
     * @throws Exception
     */
    public void close() throws Exception {
        camelContext.stop();
    }

    private class JMSUniversalMessaging {
        private String broker;
        private String queueName;

        public JMSUniversalMessaging(String broker, String queueName) {
            this.broker = broker;
            this.queueName = queueName;
        }

        public void invoke() throws Exception {
            QueueConnectionFactory qcf = new com.pcbsys.nirvana.nJMS.QueueConnectionFactoryImpl(broker);
            final StringBuilder toEndpointUrl = new StringBuilder("jms:queue:").append(queueName);
            LOGGER.debug("connectionFactory {}", qcf);
            // Note we can explicit name the component
            camelContext.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(qcf));
            camelContext.addRoutes(new RouteBuilder() {
                public void configure() {
                    from("direct:start").to(toEndpointUrl.toString());
                }
            });
            producerTemplate = camelContext.createProducerTemplate();
            camelContext.start();
        }
    }

    private class JMSApacheMQ {
        private String broker;
        private String queueName;

        public JMSApacheMQ(String broker, String queueName) {
            this.broker = broker;
            this.queueName = queueName;
        }

        public void invoke() throws Exception {
            PooledConnectionFactory connectionFactory = new PooledConnectionFactory(broker);
            connectionFactory.setMaxConnections(10);
            final StringBuilder toEndpointUrl = new StringBuilder("jms:queue:").append(queueName);
            LOGGER.debug("connectionFactory {}", connectionFactory);
            // Note we can explicit name the component
            camelContext.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
            camelContext.addRoutes(new RouteBuilder() {
                public void configure() {
                    from("direct:start").to(toEndpointUrl.toString());
                }
            });
            producerTemplate = camelContext.createProducerTemplate();
            camelContext.start();
        }
    }
}