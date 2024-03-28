package Main;


import java.util.ArrayList;
import java.util.Scanner;
import java.time.Year;

import javax.jms.TemporaryTopic;
import javax.naming.NamingException;

import utils.StringAlignUtils;

import jms.Queue;
import jms.TopicReceiver;
import jms.TemporaryTopicReciver;


public class UserApp {

	private static Scanner scan = new Scanner(System.in);
	private static Queue queue;
	private static TopicReceiver topicReceiver;
	private static TemporaryTopicReciver temporaryTopicReciver;
	
	private static StringAlignUtils util80 = new StringAlignUtils(80, "CENTER");
	
	public static void main(String[] args) throws NamingException {

		System.out.println("Insert your username: ");
		String username = scan.nextLine();
		topicReceiver = new TopicReceiver(username);
		queue = new Queue();
		
		topicReceiver.launch_and_wait(); // to receive notifications 
		
		
		
		
		boolean exit = false;

		while (!exit) {
			String option = getOptionMenu();
			switch (option) {
			case "1":
				getPublicationsTitles();
				exit = leave();
				break;
			case "2":
				addPublication(username);
				exit = leave();
				break;
            case "3":
				exit = true;
				topicReceiver.end();
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
			topicReceiver.end();
		}
		return exit;
	}

	private static String getOptionMenu() {
		StringAlignUtils util80 = new StringAlignUtils(80, "CENTER");
		StringAlignUtils util70 = new StringAlignUtils(70, "LEFT");
		StringAlignUtils util10 = new StringAlignUtils(5, "LEFT");

		System.out.println("-".repeat(80));
		System.out.println(util80.format("Researcher - IS Google Scholar"));
		System.out.println("-".repeat(80) + "\n");
		System.out.println(util80.format("Choose between the options below:") + "\n");
		System.out.println(util10.format("1)") + util70.format("List all publication titles in the system."));
		System.out.println(util10.format("2)") + util70.format("Add a new publication."));
		System.out.println(util10.format("3)") + util70.format("Exit."));
		System.out.println("\n" + "-".repeat(80) + "\n");
		System.out.print("Option: ");
		String option = scan.nextLine();
		System.out.println("\n" + "-".repeat(80) + "\n");
		return option;
	}

	private static void getPublicationsTitles() {
		
		String response = queue.sendAndReceive("getPublicationsTitles", null);
		
		String[] titles = response.split(String.valueOf("@"));
		
		if (titles.length == 0) {
			System.out.println("\nThere are no publications stored.");
		} else {
			System.out.println(util80.format("Publications"));
			System.out.println("-".repeat(80) + "\n");
			for (int i=0; i<titles.length; i++) {
				System.out.println("-->" + titles[i] + "\n\n");
			}
		}
	}

	private static void addPublication(String username) throws NamingException {
		ArrayList<Object> newPublication = new ArrayList<Object>();
		
		temporaryTopicReciver = new TemporaryTopicReciver(username);
		TemporaryTopic tempTopicDestination = temporaryTopicReciver.launch_and_wait(); // to receive notifications about rejected publications
	
		System.out.println("Insert information about the new publication.");

		boolean validTitle = false;
        while (validTitle == false) {
			System.out.print("Title: ");
			String title = scan.nextLine();
			
			if (title == null) {
				System.out.println("Try again. You need to insert a valid value for the title.\n");
			} 
			else {
				validTitle = true;
				newPublication.add(title);
			}

			
		}

		boolean validYear = false;
		String publicationDate = "";
		while (validYear == false) {
			System.out.print("Year (yyyy): ");
			publicationDate = scan.nextLine();

			try {
				int year = Integer.parseInt(publicationDate);
				publicationDate = Integer.toString(year);

				if (publicationDate.length() == 4 && year <= Year.now().getValue()) {
					validYear = true;
				} 
				else {
					System.out.println("Invalid input.\n");
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input.\n");
			}
		}

		newPublication.add(publicationDate);

		boolean validAuthor = false;
		String authors = "";
		while (validAuthor == false) {
			System.out.print("Author: ");
			String author = scan.nextLine();

			if (!author.equals("")) {
				validAuthor = true;
				authors = author;

				boolean finish = false;
				while (finish == false) {
					System.out.print("Another author (press enter to finish): ");
					author = scan.nextLine();

					if (author.equals("")) {
						finish = true;
					}
					authors = authors + ";" + author;
				}
			} else {
				System.out.println("This field is mandatory. You have to add at least one author.\n");
			}
		}
		newPublication.add(authors);
		

		boolean validNewsletter = false;
        while (validNewsletter == false) {
			System.out.print("Newsletter: ");
			String newsletter = scan.nextLine();

			if (newsletter == null) {
				System.out.println("Try again. You need to insert a valid string for the newsletter.\n");
			} 
			else {
				validNewsletter = true;
				newPublication.add(newsletter);
			}
		}

		boolean validEdition = false;
        while (validEdition == false) {
			System.out.print("Edition: ");
			String edition = scan.nextLine();

			if (edition == null) {
				System.out.println("Try again. You need to insert a valid string for the edition.\n");
			} 
			else {
				validEdition = true;
				newPublication.add(edition);
			}
		}

		boolean validPages = false;
		while (validPages == false) {
			System.out.print("Pages: ");
			String pages = scan.nextLine();

			if (pages == null) {
				System.out.println("Try again. You need to insert a valid value for the pages.\n");
			} 
			else {
				validPages = true;
				newPublication.add(pages);
			}
		}

		boolean validCitation = false;
		while (validCitation == false) {
			System.out.print("Number of citations: ");
			String citation = scan.nextLine();

			try {
				int citationInteger = Integer.parseInt(citation);
				publicationDate = Integer.toString(citationInteger);
				validCitation = true;
				newPublication.add(citation);
				
			} catch (NumberFormatException e) {
				System.out.println("Invalid input.\n");
			}
		}
		newPublication.add(username);
		newPublication.add(tempTopicDestination);

		queue.sendAndReceive("addPublication", newPublication);


	}


}

