package org.datacleaner.extension.sendjmsmessage;

import java.util.Hashtable;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.directory.InitialDirContext;

public class JmsConnectionFactory {

    // These are the JNDI object names expected.
    public static final String connFactoryToLookFor = "JMSDEMOCF";
    public static final String connFactoryForTopic = "JMSDEMOTopic";
    public static final String connFactoryForQueue = "JMSDEMOQueue";
    public static final String defaultContextFactory = "com.sun.jndi.fscontext.RefFSContextFactory";

    // Class variables
    static Session session = null; // JMS Session
    static Connection connection = null; // JMS Connection
    static String connFactoryType = connFactoryForQueue; // Generic JMS Destination
    static String operationType = "put"; // Program mode
    static String destType = null; // Destination type

    // JNDI Provider URL - may be overridden from the command line.
    static String url = "file:C:\\Users\\joserelda\\Desktop\\MQTEST\\JMSDEMO\\JNDI";

    public static void main(String[] args) {

        InitialDirContext ctx = null;
        Destination myDest = null;
        ConnectionFactory connFactory = null;

        // A single try block is used here to allow us to focus on the JNDI and
        // I/O
        // operations. Production code would be required to have much finer
        // grained
        // exception handling. In any case, always print linked JMS exceptions.
        try {
//            parseArgs(args);

            System.out.println("Lookup initial context");
            Hashtable environment = new Hashtable();
            environment.put(Context.INITIAL_CONTEXT_FACTORY, defaultContextFactory);
            environment.put(Context.PROVIDER_URL, url);
            ctx = new InitialDirContext(environment);

            // Note that the generic Connection Factory works for both queues &
            // topics
            System.out.println("Lookup connection factory " + connFactoryToLookFor);
            connFactory = (ConnectionFactory) ctx.lookup(connFactoryToLookFor);

            System.out.println("Create and start the connection");
            connection = connFactory.createConnection();
            connection.start();

            System.out.println("Create the session");
            boolean transacted = true;
            session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);

            // Note that the generic Destination also works for both queues &
            // topics
            System.out.println("Lookup destination " + connFactoryType);
            myDest = (Destination) ctx.lookup(connFactoryType);

            // Either PUT or GET messages, depending on what was passed in from
            // Cmd Line
            if (operationType == "put") {
                putMsg(myDest);
            } else {
                getMsg(myDest);
            }

            // Clean up session and connection
            session.close();
            session = null;

            connection.close();
            connection = null;

        } catch (JMSException je) {
            System.out.println("caught JMSException: " + je);
            Exception le = je.getLinkedException();
            if (le != null)
                System.out.println("linked exception: " + le);

        } catch (Exception e) {
            System.out.println("Caught exception: " + e);

            // A finally block is a good place to ensure that we don't forget
            // to close the most important JMS objects
        } finally {
            try {
                if (session != null) {
                    System.out.println("Closing Session");
                    session.close();
                }
                if (connection != null) {
                    System.out.println("Closing Connection");
                    connection.close();
                }
            } catch (JMSException je) {
                System.out.println("failed with " + je);
                Exception le = je.getLinkedException();
                if (le != null)
                    System.out.println("linked exception: " + le);
            }
        }
        System.out.print("\nFinished.");
    }

    // ---------------------------------------------------------------------------
    // PUT messages to queue or topic
    // ---------------------------------------------------------------------------
    static void putMsg(Destination myDest)
            throws JMSException, Exception {
        String outString = null;

        // Use generic MessageProducer instead of Queue/Topic Producer
        MessageProducer myProducer = session.createProducer(myDest);

        // Get user input and create messages. Loop until user sends CR.
        System.out.println("\nSending messages to" + myDest.toString() +
                "\nEnter a blank line to quit.\n");
        do {
            byte[] input = new byte[80];
            System.out.print("Enter a message to send: ");
            System.in.read(input);
            outString = (new String(input, 0, input.length)).trim();
            if (outString.length() > 0) {
                TextMessage outMessage = session.createTextMessage();
                outMessage.setText(outString);
                myProducer.send(outMessage);
                session.commit();
            }
        } while (outString.length() > 0);

        myProducer.close();
    }

    // ---------------------------------------------------------------------------
    // Get messages from queue or topic
    // ---------------------------------------------------------------------------
    static void getMsg(Destination myDest)
            throws JMSException, Exception {
        // Use generic MessageConsumer instead of Queue/Topic Consumer
        MessageConsumer myConsumer = session.createConsumer(myDest);

        System.out.println("\nGetting messages from " + myDest.toString() + "\n");

        Message inMessage = null;
        do {
            // The consumer will wait 10 seconds (10,000 milliseconds)
            inMessage = myConsumer.receive(10000);
            if (inMessage instanceof TextMessage) {
                System.out.println("\n" + "Got message: " + ((TextMessage) inMessage).getText());
            }
            session.commit();
        } while (inMessage != null);

        myConsumer.close();
    }

    // ---------------------------------------------------------------------------
    // Parse the command-line arguments
    // ---------------------------------------------------------------------------
    static void parseArgs(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i].toLowerCase();
            if (arg.equals("-url")) {
                if (i + 1 < args.length) {
                    url = args[++i];
                }
            } else if (arg.equals("-pub")) {
                operationType = "put";
                connFactoryType = connFactoryForTopic;
            } else if (arg.equals("-sub")) {
                operationType = "get";
                connFactoryType = connFactoryForTopic;
            } else if (arg.equals("-send")) {
                operationType = "put";
                connFactoryType = connFactoryForQueue;
            } else if (arg.equals("-receive")) {
                operationType = "get";
                connFactoryType = connFactoryForQueue;
            } else
                // Report and discard any unknown command-line options
                System.out.println("Ignoring unknown flag: " + arg);
        }

        if (operationType == null) {
            System.out.println("No mode supplied.  Use -pub, -sub, -send, or -receive option.");
            System.exit(-1);
        }

        System.out.println("Mode = " + operationType);
        System.out.println("JNDI URL = " + url + "\n");
    }

    // End of JMSDemo class
}
