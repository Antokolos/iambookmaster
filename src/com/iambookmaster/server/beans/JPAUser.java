package com.iambookmaster.server.beans;
 
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class JPAUser {
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key id;
	
	@Persistent
    private String email;
	
	@Persistent
	private String nick;
	
	@Persistent
	private Boolean locked;
	
	@Persistent
	private Date lastVisit;
	
	@Persistent
	private com.google.appengine.api.datastore.Text upload;
	
	@Persistent
	private String uploadExternalId;
	
	public Date getLastVisit() {
		return lastVisit;
	}
	public String getUpload() {
		return upload==null ? "": upload.getValue();
	}
	public void setUpload(String upload) {
		if (upload==null) {
			this.upload = null;
		} else {
			this.upload = new Text(upload);
		}
	}
	public String getUploadExternalId() {
		return uploadExternalId;
	}
	public void setUploadExternalId(String uploadExternalId) {
		this.uploadExternalId = uploadExternalId;
	}
	public void setLastVisit(Date lastVisit) {
		this.lastVisit = lastVisit;
	}
	public boolean isLocked() {
		return locked==null ? false : locked.booleanValue();
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Key getId() {
		return id;
	}
    public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
}
