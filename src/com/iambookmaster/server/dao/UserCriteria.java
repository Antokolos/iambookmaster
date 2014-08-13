package com.iambookmaster.server.dao;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class UserCriteria {

	private String name;
	private String email;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
