package com.iambookmaster.server.beans;
 
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class JPABookVersion {
	 
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key id; 
	@Persistent
	private Key book;
	@Persistent
	private com.google.appengine.api.datastore.Text model;
	@Persistent
	private String versions;
	@Persistent
	private Date date;
	@Persistent
	private Boolean published;
	@Persistent
	private com.google.appengine.api.datastore.Text text;
	@Persistent
	private com.google.appengine.api.datastore.Text html;
	@Persistent
	private com.google.appengine.api.datastore.Text urq;
	
	public boolean getPublished() {
		return published==null ? false : published.booleanValue();
	}
	public void setPublished(Boolean published) {
		this.published = published;
	}
	
	public Key getId() {
		return id;
	}
	
	public Key getBook() {
		return book;
	}

	public void setBook(Key book) {
		this.book = book;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getVersions() {
		return versions;
	}
	public void setVersions(String versions) {
		this.versions = versions;
	}

	public String getLocalModel() {
		if (model==null) {
			return null;
		} else {
			return model.getValue();
		}
	}
	
	public String getLocalText() {
		if (text==null) {
			return null;
		} else {
			return text.getValue();
		}
	}
	
	public String getLocalHtml() {
		if (html==null) {
			return null;
		} else {
			return html.getValue();
		}
	}
	
	public String getLocalURQ() {
		if (urq==null) {
			return null;
		} else {
			return urq.getValue();
		}
	}

	public void clearLocals() {
		model = null;
		text = null;
		html = null;
		urq=null;
	}

}
