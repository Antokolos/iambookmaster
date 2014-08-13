package com.iambookmaster.server.tags;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.jsp.JspException;

import com.google.appengine.api.datastore.KeyFactory;
import com.iambookmaster.server.beans.JPABook;
import com.iambookmaster.server.beans.JPAUser;

@SuppressWarnings("serial")
public class BookInfoTag extends MyTagSupport {
	
	private static final String FIELD_OWNER_NAME = "ownerName";
	private static final String FIELD_OWNER_EMAIL = "ownerEmail";
	private static final String FIELD_OWNER_ID = "ownerId";
	private static final Object FIELD_ID = "id";
	private String item;
	
	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public int doStartTag() throws JspException {
		Object object = getObjectByName();
		if (object instanceof JPABook) {
			JPABook book = (JPABook) object;
			PersistenceManager em = getPM();
			try {
				if (item.equals(FIELD_OWNER_NAME)) {
					JPAUser user = em.getObjectById(JPAUser.class,book.getOwner());
					pageContext.getOut().append(user.getNick());
				} else if (item.equals(FIELD_OWNER_EMAIL)) {
					JPAUser user = em.getObjectById(JPAUser.class,book.getOwner());
					pageContext.getOut().append(user.getEmail());
				} else if (item.equals(FIELD_ID)) {
					pageContext.getOut().append(KeyFactory.keyToString(book.getId()));
				} else if (item.equals(FIELD_OWNER_ID)) {
					pageContext.getOut().append(KeyFactory.keyToString(book.getOwner()));
				} else {
					throw new JspException("Unknown field "+item);
				}
			} catch (IOException e) {
				throw new JspException(e);
			}
		} else {
			throw new JspException(getName()+" is not a JPABook");
		}
		return SKIP_BODY;
	}
}
