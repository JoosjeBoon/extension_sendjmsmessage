package org.datacleaner.extension.sendjmsmessage;

public class JmsQueueSender {

//    private JmsTemplate jmsTemplate;
//    private Queue queue;
//
//    public JmsQueueSender(ConnectionFactory cf) {
//        this.jmsTemplate = new JmsTemplate(cf);
//    }
//
//    public void setQueue(Queue queue) {
//        this.queue = queue;
//    }
//
//    public boolean simpleSend() {
//        this.jmsTemplate.send(this.queue, new MessageCreator() {
//            public Message createMessage(Session session) throws JMSException {
//                return session.createTextMessage("hello queue world");
//            }
//        });
//        return true;
//    }
}
