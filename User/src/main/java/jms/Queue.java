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
		this.systemDestination = InitialContext.doLookup("jms/queue/systemQueue");
	}
	
	public String sendAndReceive(String header, ArrayList<Object> newPublication) {
		String messageReceived = "";
		try (JMSContext context = connectionFactory.createContext("john", "!1secret");) {

			JMSProducer messageProducer = context.createProducer();
			ObjectMessage msg = context.createObjectMessage();
			Destination tmp = context.createTemporaryQueue();

			msg.setJMSReplyTo(tmp);
			msg.setStringProperty("header", header);
			msg.setObject(newPublication);
			messageProducer.send(systemDestination, msg);

			JMSConsumer messageConsumer = context.createConsumer(tmp);
			TextMessage msgReceived = (TextMessage) messageConsumer.receive();

			System.out.println("\n" + "-".repeat(80) + "\n");
			System.out.println("     Message received: " + msgReceived.getStringProperty("header"));			
			System.out.println("\n" + "-".repeat(80) + "\n");

			messageReceived = msgReceived.getText();
		} catch (Exception re) {
			re.printStackTrace();
		}

		return messageReceived;
	}

	public Destination receive() {
		Destination tmp = null;
		try (JMSContext context = connectionFactory.createContext("john", "!1secret");) {

			tmp = context.createTemporaryQueue();

			JMSConsumer messageConsumer = context.createConsumer(tmp);
			ObjectMessage msgReceived = (ObjectMessage) messageConsumer.receive();
			String header = msgReceived.getStringProperty("header");
			if (header.equals("Publication rejected") ) {
				System.out.println("\n" + "-".repeat(80) + "\n");
				System.out.println("     Message received: The publication " 
				+ (String)msgReceived.getObject() +" was rejected.");
				System.out.println("\n" + "-".repeat(80) + "\n");
				System.out.println("\nChoose between the options above:");
				context.close();
				
			} else if (header.equals("Publication approved") ) {
				context.close();
			}
		} catch (Exception re) {
			re.printStackTrace();
		}

		return tmp;
	}
	
}
