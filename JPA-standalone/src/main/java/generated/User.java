package generated;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

//@Entity(name = "user")
//@Table(name = "user")
public class User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private long id;

	@Column(name = "username")
	private String username;

	@ManyToMany
	@JoinTable(name = "Favourite", 
	joinColumns = @JoinColumn(name = "username"), 
	inverseJoinColumns = @JoinColumn(name = "publication_id"))
	protected Set<Publication> favourite_publications;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Set<Publication> getFavourite_publications() {
		return favourite_publications;
	}

	public void setFavourite_publications(Set<Publication> favourite_publications) {
		this.favourite_publications = favourite_publications;
	}

}
