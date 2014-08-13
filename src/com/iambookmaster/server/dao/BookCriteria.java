package com.iambookmaster.server.dao;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.iambookmaster.server.beans.JPAUser;

public class BookCriteria {

	private JPAUser user;
	private Key id;
	
	public Key getId() {
		return id;
	}
	public void setId(Key id) {
		this.id = id;
	}
	public void setId(String id) {
		this.id = KeyFactory.stringToKey(id);
	}
	
	public JPAUser getUser() {
		return user;
	}
	public void setUser(JPAUser user) {
		this.user = user;
	}

	public String toString() {
		return super.toString();
	}

}
