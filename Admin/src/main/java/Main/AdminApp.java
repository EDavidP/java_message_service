package Main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.jms.TemporaryTopic;

import beans.IEJBPublications;
import jms.AdminProducer;
import jms.Queue;
import utils.StringAlignUtils;

public class AdminApp {

	private static IEJBPublications managePublications;
	private static AdminProducer adminProducer;
	private static Scanner scan = new Scanner(System.in);
	private static Queue queue;
	
	private static StringAlignUtils util80 = new StringAlignUtils(80, "CENTER");
	private static StringAlignUtils util70 = new StringAlignUtils(70, "LEFT");
	private static StringAlignUtils util10 = new StringAlignUtils(5, "LEFT");

	public static void main(String[] args) throws NamingException, JMSException {

//		String username = "Admin";
//		topicReceiver = new TopicReceiver(username);
		
//		topicReceiver.launch_and_wait(); // to receive notifications 
		managePublications = InitialContext.doLookup("ear/ejbs/EJBPublications!beans.IEJBPublications");
		adminProducer = new AdminProducer();
		queue = new Queue();
		boolean exit = false;

		while (!exit) {
			String option = getOptionMenu();
			switch (option) {
			case "1":
				showPublicationsTitles();
				exit = leave();
				break;
			case "2":
				showPendingTasks();
				exit = leave();
				break;
			case "3":
				removePublication();
				exit = leave();
				break;
			case "4":
				exit = true;
				break;
			default:
				System.out.println("Invalid option. Please try again: ");
				exit = false;
			}
		}
	}

	private static boolean leave() {
		System.out.println("\n" + "-".repeat(80) + "\n");
		System.out.println("Do you want to leave? \nPress 'y' to leave or any key to go to the menu.");
		String leave = scan.nextLine();
		boolean exit = false;
		if (leave.contains("y")) {
			exit = true;
		}
		return exit;
	}

	private static String getOptionMenu() {

		System.out.println("-".repeat(80));
		System.out.println(util80.format("Admin - IS Google Scholar"));
		System.out.println("-".repeat(80) + "\n");
		System.out.println(util80.format("Choose between the options below:") + "\n");
		System.out.println(util10.format("1)") + util70.format("List all publication titles in the system."));
		System.out.println(util10.format("2)") + util70.format("List all pending tasks."));
		System.out.println(util10.format("3)") + util70.format("Remove a given publication."));
		System.out.println(util10.format("4)") + util70.format("Exit."));
		System.out.println("\n" + "-".repeat(80) + "\n");
		System.out.print("Option: ");
		String option = scan.nextLine();
		System.out.println("\n" + "-".repeat(80) + "\n");
		return option;
	}

	private static void showPublicationsTitles() {
		ArrayList<String> titles = managePublications.getPublicationsTitles();
		if (titles.size() == 0) {
			System.out.println("\nThere are no publications stored.");
		} else {
			System.out.println(util80.format("Publications"));
			System.out.println("-".repeat(80) + "\n");
			for (String title : titles) {
				System.out.println("--> " + title + "\n");
			}
		}
	}

	private static void showPendingTasks() throws JMSException {
		ArrayList<String> pendingPublications = managePublications.listPendingTasks();
		if (pendingPublications.size() == 0) {
			System.out.println("\nCurrently there are no pending tasks.");
		} else {
			System.out.println("\nPublications");
			for (String pendingPublication : pendingPublications) {
				System.out.println(pendingPublication);
			}
			managePendigTasks();
		}
	}

	private static void managePendigTasks() throws JMSException {
		System.out.println("\nInsert the ID of the publication you want to manage: ");
		
		String str = scan.nextLine();
		str = isInteger(str);
		int id = Integer.parseInt(str);
		int size = managePublications.checkIfPublicationExists((long) id, "pending");

		if (size == 0) {
			System.out.println("There are no publications pending approval with the given ID.");
		} else {
			String publicationTitle = managePublications.getPublicationTitleById((long) id);
			
			boolean exit = false;
			while (!exit) {
				System.out.println("Do you wish to approve or reject the addition of one of the pending publications?\n"
						+ "Press 1 to approve, 2 to reject: ");
				String username = "";
				String option = scan.nextLine();
				switch (option) {
				case "1":
					username = managePublications.getUsernameById((long) id);
					managePublications.aproveOrRejectPendingPublication((long) id, "approve");
					adminProducer.notifyAllUsers("Publication approved", publicationTitle, username);
					System.out.println("Publication approved.");
					exit = true;
					break;
				case "2":
					username = managePublications.getUsernameById((long) id);
					managePublications.aproveOrRejectPendingPublication((long) id, "remove");
//					ObjectMessage msg = queue.sendAndReceive("getHashMap");
//				
//					@SuppressWarnings("unchecked") LinkedHashMap<String, TemporaryTopic> usernameTopicDestinations = (LinkedHashMap<String, TemporaryTopic>) msg.getObject();
//					adminProducer.notifyUser("Publication rejected",publicationTitle, usernameTopicDestinations.get(username));
					adminProducer.notifyAllUsers("Publication rejected", publicationTitle, username);
					System.out.println("Publication rejected.");
					exit = true;
					break;
				default:
					System.out.println("Invalid option. Please try again: \n");
					exit = false;
				}
			}
		}
	}

	private static void removePublication() {
		ArrayList<String> publications = managePublications.listPublications();
		
		if (publications.size() == 0) {
			System.out.println("There are no publications in the data base.");
		} else {
			for (String publication : publications) {
				System.out.println(publication);
			}
			
			System.out.println("\nInsert the ID of the publication you would like to remove: ");
			
			String str = scan.nextLine();
			str = isInteger(str);
			int id = Integer.parseInt(str);
			int size = managePublications.checkIfPublicationExists((long) id, "valid");
			if (size == 0) {
				System.out.println("There are no publications with the given ID.");
			} else {
				managePublications.removePublication((long) id);
				System.out.println("Publication removed.");
			}
		}
		
	}

	private static String isInteger(String str) {
		try {
			Integer.parseInt(str);
			return str;
		} catch (NumberFormatException e) {
			System.out.println("\nInvalid input, try again: ");
			String id = scan.nextLine();
			id = isInteger(id);
			return id;
		}
	}

}