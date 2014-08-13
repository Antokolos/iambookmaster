package com.iambookmaster.server.beans;
 
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
public class JPAClob {
	
	public static final int TYPE_MODEL=0;
	public static final int TYPE_TEXT=1; 
	public static final int TYPE_HTML=2;
	public static final int TYPE_URQ=3;
	public static int CLOB_SIZE=1024*800; //800Kb
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
 	private Key id;
	
	@Persistent
	private int type;

	@Persistent
	private int ordering;
	
	@Persistent
	private int version;
	
	@Persistent
	private Key owner;
	
	@Persistent
	private com.google.appengine.api.datastore.Text data;

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getOrder() {
		return ordering;
	}

	public void setOrder(int order) {
		this.ordering = order;
	}

	public Key getOwner() {
		return owner;
	}

	public void setOwner(Key owner) {
		this.owner = owner;
	}

	public com.google.appengine.api.datastore.Text getData() {
		return data;
	}

	public void setData(com.google.appengine.api.datastore.Text data) {
		this.data = data;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	
}
