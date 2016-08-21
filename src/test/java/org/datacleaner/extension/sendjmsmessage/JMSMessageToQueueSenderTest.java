package org.datacleaner.extension.sendjmsmessage;

public class JMSMessageToQueueSenderTest {

//    BrokerService broker;
//    MessageConsumer consumer;
//    Connection connection;
//    Session session;
//
//    //not to interfere with a running instance on the default port 61616
//    String brokerUrl = "tcp://localhost:61617";
//    String queueName = "some.queue.name";
//
//    @Before
//    public void prepare() throws Exception {
//        // broker
//        broker = new BrokerService();
//        TransportConnector connector = new TransportConnector();
//        connector.setUri(new URI(brokerUrl));
//        broker.addConnector(connector);
//        broker.start();
//
//        // consumer
//        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
//        connection = connectionFactory.createConnection();
//        connection.start();
//        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//        Destination destination = session.createQueue(queueName);
//        consumer = session.createConsumer(destination);
//    }
//
//    @After
//    public void cleanup() throws Exception {
//        broker.stop();
//        consumer.close();
//        session.close();
//        connection.close();
//    }
//
//    @Test
//    public void testGetOutputColumns() throws Exception {
//        // producer
//        JMSMessageToQueueSender sender = new JMSMessageToQueueSender(brokerUrl, queueName);
//        String messageBody = "hello";
//        String correlationId = "someId";
//
//        SendMessageToJMSQueueResult result = sender.sendMessage(brokerUrl, queueName, messageBody, correlationId);
//        assertEquals(true, result.isSuccessful());
//
//        Message message = consumer.receive(1000);
//        assertEquals(messageBody, ((TextMessage) message).getText());
//        assertEquals(correlationId, ((TextMessage) message).getJMSCorrelationID());
//    }

}
