package beans;

import java.util.List;


import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import book.Researcher;


@Stateless
public class EJBResearchers implements IEJBResearchers{
	@PersistenceContext(unitName="testPersistence")
	private EntityManager em;

	public EJBResearchers() {
		
	}
	
//	Obtain all stored data about researchers
	public List<Researcher> getInfotmationAboutResearchers() {
		String jpql = "SELECT r FROM Researcher r";
		TypedQuery<Researcher> typedQuery = em.createQuery(jpql, Researcher.class);
		List<Researcher> researchers = typedQuery.getResultList();
		return researchers;
	}
	
//	Given a researcher name, obtain all associated data
	public List<Researcher> getInfotmationAboutResearchersByName(String Name) {
		String jpql = "SELECT r FROM Researcher r WHERE LOWER(r.name) like :xpto";
		TypedQuery<Researcher> typedQuery = em.createQuery(jpql, Researcher.class);
		typedQuery.setParameter("xpto", "%"+Name.toLowerCase()+"%");
		List<Researcher> researchers = typedQuery.getResultList();
		return researchers;
	}
	
//	Given an interest, obtain all researcher data associated with that interest
	public List<Researcher> getInfotmationAboutResearchersByInterest(String Interest) {
		String jpql = "SELECT r FROM Researcher r WHERE :xpto MEMBER OF r.interest";
		TypedQuery<Researcher> typedQuery = em.createQuery(jpql, Researcher.class);
		typedQuery.setParameter("xpto", Interest);
		List<Researcher> researchers = typedQuery.getResultList();
		return researchers;
	}
}
