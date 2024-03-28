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

import javax.ejb.EJB;


@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/systemQueue"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") 
		}, mappedName = "java:jboss/exported/jms/queue/systemQueue")

public class MessageDrivenBean implements MessageListener  {
	
	public void MessageListener() throws NamingException{
		
	}

	@EJB
	private IEJBPublications managePublications;
	
	@Inject
	private JMSContext context;
	
	private static LinkedHashMap<String, TemporaryTopic> usernameTopicDestinations = new LinkedHashMap<String, TemporaryTopic>();
	private static int hashmapSize = 0;
//	private static MDBProducer producer = new MDBProducer();
	
	
	@SuppressWarnings("unchecked")
	public void onMessage(Message message) {
		try {
			
			ObjectMessage msg = (ObjectMessage) message;
			
			String header = msg.getStringProperty("header");
			Destination destination = msg.getJMSReplyTo();
			
			TextMessage response = context.createTextMessage();
			
			System.out.println("Message received: " + header + "\nSender: " + destination);
			
			if (header.equals("getPublicationsTitles")) {
				List<String> listOfTitles = managePublications.getPublicationsTitles();
				String titles = "";
				for (String title : listOfTitles) {
					titles = titles + title + String.valueOf("@");
				}
				
				
				if (listOfTitles.size() == 0) {
					response.setText("There are no publications stored.");
				} else {
					response.setText(titles);
					
				}
				response.setStringProperty("header", "Publication titles loaded.");
				
				JMSProducer messageProducer = context.createProducer();
				messageProducer.send(destination, response);
				System.out.println("Message sent: " + response.getText());
			}
			else if (header.equals("addPublication")) {
				
				
				ArrayList<Object> body = (ArrayList<Object>) msg.getObject();
				
				String title = (String) body.get(0);
				String publicationDate = (String) body.get(1);
				String authors = (String) body.get(2);	
				String newsletter = (String) body.get(3);
				String edition = (String) body.get(4);
				String pages = (String) body.get(5);
				String totalCitation = (String) body.get(6);
				String username = (String) body.get(7);
				TemporaryTopic tempTopicDestination = (TemporaryTopic) body.get(8);
				
				Publication publication = new Publication();
				
				publication.setTitle(title);
				String[] author = authors.split(";");
				ArrayList<String> listOfAuthors = new ArrayList<>();
				for (int i = 0; i < author.length; i++) {
					listOfAuthors.add(author[i]);
					}
				publication.setAuthors(listOfAuthors);
				publication.setPublicationDate(Integer.parseInt(publicationDate));
				publication.setNewsletter(newsletter);
				publication.setEdition(edition);
				publication.setPages(pages);
				publication.setTotalCitation(Integer.parseInt(totalCitation));
				publication.setState("pending");
				publication.setAddedBy(username);
				
				managePublications.addPublication(publication);
				
//				Aqui quando o hashmap mudar de tamnhano enviar mais uma vez para o admin
				usernameTopicDestinations.put(username, tempTopicDestination);
//				if (hashmapSize<usernameTopicDestinations.size()) {
//					hashmapSize = hashmapSize + usernameTopicDestinations.size();
//					producer.notifyAdmin("The Hashmap was updated",usernameTopicDestinations);
//				}
				
				response.setText("Publication waiting for Admin aproval.");
				response.setStringProperty("header", "Publication waiting for Admin aproval.");
				
				JMSProducer messageProducer = context.createProducer();
				messageProducer.send(destination, response);
				System.out.println("Message sent: " + response.getText());
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}


	public static LinkedHashMap<String, TemporaryTopic> getUsernameTopicDestinations() {
		return usernameTopicDestinations;
	}


	public static void setUsernameTopicDestinations(LinkedHashMap<String, TemporaryTopic> usernameTopicDestinations) {
		MessageDrivenBean.usernameTopicDestinations = usernameTopicDestinations;
	}
	
	
}