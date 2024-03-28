package beans;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.LocalBean; 
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import book.Publication;
import utils.StringAlignUtils;

@Stateless
@LocalBean
public class EJBPublications implements IEJBPublications{

	@PersistenceContext(unitName="testPersistence")
	private EntityManager em;

	public EJBPublications() {
		
	}
	
	public String getUsernameById(Long id) {
		String jpql = "SELECT p FROM Publication p WHERE p.id=:xpto";
		TypedQuery<Publication> typedQuery = em.createQuery(jpql, Publication.class);
		typedQuery.setParameter("xpto", id);
		List<Publication> publication = typedQuery.getResultList();
		String publicationUsername = publication.get(0).getAddedBy();
		return publicationUsername;
	}
	
	public ArrayList<String> getPublicationsTitles() {
		String jpql = "SELECT p FROM Publication p where p.state= 'valid'";
		TypedQuery<Publication> typedQuery = em.createQuery(jpql, Publication.class);
		List<Publication> publications = typedQuery.getResultList();
		ArrayList<String> titles = new ArrayList<String>();
		for (Publication publication : publications) {
			titles.add(publication.getTitle());
		}
		return titles;
	}
	
	public String getPublicationTitleById(Long id) {
		String jpql = "SELECT p FROM Publication p WHERE p.id=:xpto";
		TypedQuery<Publication> typedQuery = em.createQuery(jpql, Publication.class);
		typedQuery.setParameter("xpto", id);
		List<Publication> publication = typedQuery.getResultList();
		String publicationTitle = publication.get(0).getTitle();
		return publicationTitle;
	}
	
	public void addPublication(Publication publication) {
		em.persist(publication);
	}
	
	public ArrayList<String> listPublications() {
		String jpql = "SELECT p FROM Publication p where p.state= 'valid'";
		TypedQuery<Publication> typedQuery = em.createQuery(jpql, Publication.class);
		List<Publication> publications = typedQuery.getResultList();
		return publicationsListToString(publications);
		
	}
	
	public ArrayList<String> listPendingTasks() {
		String jpql = "SELECT p FROM Publication p where p.state= 'pending'";
		TypedQuery<Publication> typedQuery = em.createQuery(jpql, Publication.class);
		List<Publication> pendingPublications = typedQuery.getResultList();
		return publicationsListToString(pendingPublications);
		
	}
	
	public int checkIfPublicationExists(Long id, String state) {
		String jpql = "SELECT p FROM Publication p WHERE p.id=:xpto1 and p.state=:xpto2";
		TypedQuery<Publication> typedQuery = em.createQuery(jpql, Publication.class);
		typedQuery.setParameter("xpto1", id).setParameter("xpto2", state);
		List<Publication> publications = typedQuery.getResultList();
		return publications.size();
	}
	
	public void aproveOrRejectPendingPublication(Long id, String action) {
		if (action.equals("approve")) {
			Publication publication = em.find(Publication.class, id);
			publication.setState("valid");
			
		} else if (action.equals("remove")) {
			Publication publication = em.find(Publication.class, id);
			publication.setState("invalid");
			em.remove(publication);
		}
	}
	
	public void removePublication(Long id) {
		Publication publication = em.find(Publication.class, id);
		publication.setState("invalid");
		em.remove(publication);
	}

	private static ArrayList<String> publicationsListToString(List<Publication> publicationsList) {
		
		StringAlignUtils util80 = new StringAlignUtils(100, "LEFT");
		StringAlignUtils util60 = new StringAlignUtils(80, "LEFT");
		StringAlignUtils util20 = new StringAlignUtils(20, "RIGHT");

		ArrayList<String> publications = new ArrayList<String>();
		
		for (Publication publication : publicationsList) {

			String str = "";

			str = str + "\n" + util80.format(publication.getTitle());
			str = str + "\n" + util20.format("ID:  ") + util60.format(publication.getId());
			str = str + "\n" + util20.format("Authors:  ");
			for (int i = 0; i < publication.getAuthors().size(); i++) {
				str = "\n" + str + publication.getAuthors().get(i) + ",";
			}
			str = str + "\n" + util60.format(str);
			str = str + "\n" + util20.format("Publication Date:  ") + util60.format(publication.getPublicationDate());
			str = str + "\n" + util20.format("Newsletter:  ") + util60.format(publication.getNewsletter());
			str = str + "\n" + util20.format("Edition:  ") + util60.format(publication.getEdition());
			str = str + "\n" + util20.format("Pages:  ") + util60.format(publication.getPages());
			str = str + "\n" + util20.format("Total citations:  ") + util60.format(publication.getTotalCitation());
			str = str + "\n";
			str = str + ".".repeat(100) + "\n";
			publications.add(str);
		}
		return publications;
	}

}
