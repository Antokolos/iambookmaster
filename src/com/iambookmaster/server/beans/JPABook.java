package com.iambookmaster.server.beans;
 
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
 
@PersistenceCapable(identityType = IdentityType.APPLICATION)
/**
 * Game-Book
 */
public class JPABook {
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
 	private Key id;
	@Persistent
	private String name;
	@Persistent
	private Key owner;
//	private long ownerUserId; 
	@Persistent
	private com.google.appengine.api.datastore.Text model;
	@Persistent
	private String authors;
	@Persistent
	private String externalId;
	@Persistent
	private String version;
	@Persistent
	private String description;
	@Persistent
	private Boolean locked;
	@Persistent
	private Date lastUpdate;
	@Persistent
	private Boolean published;
	@Persistent
	private com.google.appengine.api.datastore.Text text;
	@Persistent
	private com.google.appengine.api.datastore.Text html;
	@Persistent
	private com.google.appengine.api.datastore.Text urq;
	
	public boolean isPublished() {
		return published==null ? false : published.booleanValue();
	}
	public void setPublished(Boolean published) {
		this.published = published;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public boolean isLocked() {
		return locked==null ? false : locked.booleanValue();
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	public String getAuthors() {
		return authors;
	}
	public void setAuthors(String authors) {
		this.authors = authors;
	}
	public Key getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Key getOwner() {
		return owner;
	}
	public void setOwner(Key owner) {
		this.owner = owner;
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
