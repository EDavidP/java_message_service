package jpaprimer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import generated.Publication;
import generated.Researcher;
import generated.Scholar;

public class JPA {
	private static File xmlFile = new File("scholar.xml");
	private static File xmlSchema = new File("scholarSchema.xsd");
	private static Scholar scholar;

	public static void main(String[] args) throws JAXBException, IOException, TransformerException{
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Scholar.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			scholar = (Scholar) jaxbUnmarshaller.unmarshal(xmlFile);

			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema scholarSchema = sf.newSchema(xmlSchema);
			jaxbUnmarshaller.setSchema(scholarSchema);

			System.out.println("The XML file was imported and validated with success.");
		} catch (JAXBException | SAXException e) {
			System.out.println("There was an error in the importation or validation of the XML file.");
			 e.printStackTrace();
		}

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("TestPersistence");
		EntityManager em = emf.createEntityManager();
		EntityTransaction trx = em.getTransaction();

		List<Researcher> researchers = scholar.getResearcher();
		List<Publication> publications = new ArrayList<>();
		for (int i=0; i<researchers.size();i++) {
			List<Publication> articlesOfTheAuthor = researchers.get(i).getPublication();
			publications.addAll(articlesOfTheAuthor);
		}
		
//		for (int i=0; i<researchers.size();i++) {
//			System.out.println(researchers.get(i).getName());
//		}
		
		trx.begin();
		for (Researcher researcher : researchers) {
			em.persist(researcher);
		}
		trx.commit();

		trx.begin();
		for (Publication article : publications) {
			em.persist(article);
		}
		trx.commit();

		System.out.print("\nList of authors loaded to the database: \n");
		for (Researcher researcher : researchers) {
			System.out.println("--> " + researcher.getName());
		}

		System.out.print("\nList of articles loaded to the database: \n");
		for (Publication article : publications) {
			System.out.println("--> " + article.getTitle());
		}

		em.close();
		emf.close();
	}
}
