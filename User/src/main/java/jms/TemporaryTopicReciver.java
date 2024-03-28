package jms;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TemporaryTopic;

public class TemporaryTopicReciver implements MessageListener{
	private ConnectionFactory connectionFactory;
	private Destination destination;
	private JMSContext context;
	private String user;
	
	public TemporaryTopicReciver(String username) throws NamingException {
		this.connectionFactory = InitialContext.doLookup("jms/RemoteConnectionFactory");
		this.destination = InitialContext.doLookup("jms/topic/singleUserTopic");
		this.context = connectionFactory.createContext("john", "!1secret");
		this.user = username;
	}

	@Override
	public void onMessage(Message msg) {
		ObjectMessage message = (ObjectMessage) msg;
		
		try {
			String header = message.getStringProperty("header");
			if (header.equals("Publication rejected") ) {
				System.out.println("\n" + "-".repeat(80) + "\n");
				System.out.println("     Message received: The publication " 
				+ (String)message.getObject() +" was rejected.");
				System.out.println("\n" + "-".repeat(80) + "\n");
				System.out.println("\nChoose between the options above:");
//				
				
			} else if (header.equals("Publication approved") ) {
//				
			}
					

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public TemporaryTopic launch_and_wait() {
		TemporaryTopic tempTopicDestination = null;
		try {
			tempTopicDestination = context.createTemporaryTopic();
			context.createConsumer(tempTopicDestination);
		    
			JMSConsumer mc = context.createConsumer((Topic) tempTopicDestination, "notifyAboutRejectedPublication");
			mc.setMessageListener(this);
			
		} catch (JMSRuntimeException e) {
			e.printStackTrace();
		}
		
		return tempTopicDestination;
	}

	public void end()
	{
		this.context.close();
	}
}
