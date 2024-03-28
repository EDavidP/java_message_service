package beans;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remote;

import book.Publication;

@Remote
public interface IEJBPublications {
	
	public String getUsernameById(Long id);

	public ArrayList<String> getPublicationsTitles();
	
	public String getPublicationTitleById(Long id);
	
	public void addPublication(Publication publication);
	
	public ArrayList<String> listPublications();

	public ArrayList<String> listPendingTasks();
	
	public int checkIfPublicationExists(Long id, String state);
	
	public void aproveOrRejectPendingPublication(Long id, String action);
	
	public void removePublication(Long id);
}
