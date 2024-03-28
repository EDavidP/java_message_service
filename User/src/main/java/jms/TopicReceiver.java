package jms;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class TopicReceiver implements MessageListener {
	private ConnectionFactory connectionFactory;
	private Destination destination;
	private JMSContext context;
	private String user;

	public TopicReceiver(String username) throws NamingException {
		this.connectionFactory = InitialContext.doLookup("jms/RemoteConnectionFactory");
		this.destination = InitialContext.doLookup("jms/topic/allUsersTopic");
		this.context = connectionFactory.createContext("john", "!1secret");
		this.user = username;
	}

	@Override
	public void onMessage(Message msg) {
		ObjectMessage message = (ObjectMessage) msg;

		try {
			String header = message.getStringProperty("header");
			String[] array = header.split("@");
			
			if(array[1].equals("Publication rejected") && this.user.equals(array[0])) {
				System.out.println("\n" + "-".repeat(80) + "\n");
				System.out.println(
						"     Message received: The publication " + (String) message.getObject() + " was rejected.");
				System.out.println("\n" + "-".repeat(80) + "\n");
				System.out.println("\nChoose between the options above:");
				
			} else if(array[1].equals("Publication approved")) {
				System.out.println("\n" + "-".repeat(80) + "\n");
				System.out.println(
						"     Message received: The publication " + (String) message.getObject() + " was approved.");
				System.out.println("\n" + "-".repeat(80) + "\n");
				System.out.println("\nChoose between the options above:");
			}
			
			

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void launch_and_wait() {
		try {
			context.setClientID(user);
			JMSConsumer mc = context.createDurableConsumer((Topic) destination, "mySubscription");
			mc.setMessageListener(this);

		} catch (JMSRuntimeException e) {
			e.printStackTrace();
		}
	}

	public void end() {
		this.context.close();
	}
}
