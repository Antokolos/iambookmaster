package com.iambookmaster.server.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.iambookmaster.server.beans.JPAUser;

@SuppressWarnings("serial")
public class UserInfoTag extends MyTagSupport {
	private static final String FIELD_ID="id";
	private static final String FIELD_NAME="name";
	private static final String FIELD_EMAIL="email";
	
	private String item;
	
	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public int doStartTag() throws JspException {
		UserService userService = UserServiceFactory.getUserService();
		try {
			if (userService.isUserLoggedIn()) {
				User usr = userService.getCurrentUser();
				if (FIELD_NAME.equals(item)) {
					pageContext.getOut().append(usr.getNickname());
				} else if (FIELD_EMAIL.equals(item)) {
					pageContext.getOut().append(usr.getEmail());
				} else if (FIELD_ID.equals(item)) {
					Object object = getObjectByName();
					if (object instanceof JPAUser) {
						JPAUser user = (JPAUser) object;
						pageContext.getOut().append(KeyFactory.keyToString(user.getId()));
					} else {
						throw new JspException(getName()+" is not a JPAUser");
					}
				} else {
					throw new JspException("Unknown attribute "+getName());
				}
			} else {
				throw new JspException("User is not logged");
			}
		} catch (IOException e) {
			throw new JspException(e.getMessage(),e);
		}
		return SKIP_BODY;
	}
}
