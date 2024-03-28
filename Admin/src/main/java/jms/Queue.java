package jms;

import java.util.ArrayList;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Queue {
	private ConnectionFactory connectionFactory;
	private Destination systemDestination;

	public Queue() throws NamingException {
		this.connectionFactory = InitialContext.doLookup("jms/RemoteConnectionFactory");
		this.systemDestination = InitialContext.doLookup("jms/queue/adminQueue");
	}
	
	public ObjectMessage sendAndReceive(String text) {
		ObjectMessage messageReceived = null;
		try (JMSContext context = connectionFactory.createContext("john", "!1secret");) {

			JMSProducer messageProducer = context.createProducer();
			TextMessage msg = context.createTextMessage();
			Destination tmp = context.createTemporaryQueue();

			msg.setJMSReplyTo(tmp);
			msg.setText(text);
			messageProducer.send(systemDestination, msg);

			JMSConsumer messageConsumer = context.createConsumer(tmp);
			messageReceived = (ObjectMessage) messageConsumer.receive();

			System.out.println("\n" + "-".repeat(80) + "\n");
			System.out.println("     Message received: " + messageReceived.getStringProperty("header"));			
			System.out.println("\n" + "-".repeat(80) + "\n");

			
		} catch (Exception re) {
			re.printStackTrace();
		}

		return messageReceived;
	}


	
}
