package beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import book.Publication;
import beans.MessageDrivenBean;



@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/adminQueue"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") 
		}, mappedName = "java:jboss/exported/jms/queue/adminQueue")

public class MessageDrivenBeanAdmin implements MessageListener  {
	
	public void MessageListener() throws NamingException{
		
	}

//	@EJB
//	private IEJBPublications managePublications;
	
	@Inject
	private JMSContext context;
	
	private static LinkedHashMap<String, TemporaryTopic> usernameTopicDestinations;

	public void onMessage(Message message) {
		try {
			
			TextMessage msg = (TextMessage) message;
			
			Destination destination = msg.getJMSReplyTo();
			
			ObjectMessage response = context.createObjectMessage();
			
			System.out.println("Message received: " + msg.getText() + "\nSender: " + destination);
			
			if (msg.getText().equals("getHashMap")) {
				
				usernameTopicDestinations = MessageDrivenBean.getUsernameTopicDestinations();
				
				response.setObject(usernameTopicDestinations);
				
				response.setStringProperty("header", "The HashMap is here");
				
				JMSProducer messageProducer = context.createProducer();
				messageProducer.send(destination, response);
				System.out.println("Message sent: " + response.getStringProperty("header"));
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}