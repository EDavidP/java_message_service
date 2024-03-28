package jms;


import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.jms.ObjectMessage;
import javax.jms.TemporaryTopic;

public class AdminProducer {

	private ConnectionFactory connectionFactory;
	private Destination singleUserTopic;
	private Destination allUsersDestination;

	public AdminProducer() throws NamingException {
		this.connectionFactory = InitialContext.doLookup("jms/RemoteConnectionFactory");
		this.singleUserTopic = InitialContext.doLookup("jms/topic/singleUserTopic");
		this.allUsersDestination = InitialContext.doLookup("jms/topic/allUsersTopic");
	}

	public void notifyAllUsers(String header, String title, String username) {
		try (JMSContext context = connectionFactory.createContext("john", "!1secret");) {
			JMSProducer messageProducer = context.createProducer();

			ObjectMessage msg = context.createObjectMessage();
			
			msg.setStringProperty("header", username+"@"+header);
			msg.setObject(title);
			messageProducer.send(allUsersDestination, msg);
		} catch (Exception re) {
			re.printStackTrace();
		}
	}

	public void notifyUser(String header, String title, TemporaryTopic tmp) {
		try (JMSContext context = connectionFactory.createContext("john", "!1secret");) {
			JMSProducer messageProducer = context.createProducer();

			ObjectMessage msg = context.createObjectMessage();

			msg.setStringProperty("header", header);
			msg.setObject(title);
			
			messageProducer.send(tmp, msg);
		} catch (Exception re) {
			re.printStackTrace();
		}
	}
}