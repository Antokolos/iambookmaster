package com.iambookmaster.server.tags;

import javax.jdo.PersistenceManager;
import javax.servlet.jsp.JspException;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.iambookmaster.server.beans.JPAUser;
import com.iambookmaster.server.dao.DAO;

@SuppressWarnings("serial")
public class CurrentUserTag extends MyTagSupport {
	public int doStartTag() throws JspException {
		UserService userService = UserServiceFactory.getUserService();
		if (userService.isUserLoggedIn()==false) {
			throw new JspException("User is not logged in");
		}
		try {
			PersistenceManager em = getPM();
			JPAUser user = DAO.getUsersDAO().findOrCreateUser(em, userService.getCurrentUser());
			//store results
			setObjectByName(user);
			return SKIP_BODY;
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e);
		}
	}

}
