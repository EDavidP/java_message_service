package beans;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remote;

import book.Researcher;

@Remote
public interface IEJBResearchers {
	public List<Researcher> getInfotmationAboutResearchers();

	public List<Researcher> getInfotmationAboutResearchersByName(String name);

	public List<Researcher> getInfotmationAboutResearchersByInterest(String interest);
}
