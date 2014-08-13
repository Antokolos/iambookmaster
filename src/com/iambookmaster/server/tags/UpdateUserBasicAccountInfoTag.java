package com.iambookmaster.server.tags;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.jsp.JspException;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.iambookmaster.server.beans.JPAUser;
import com.iambookmaster.server.dao.DAO;

@SuppressWarnings("serial")
public class UpdateUserBasicAccountInfoTag extends MyTagSupport {

	private static final Logger log = Logger.getLogger(UpdateUserBasicAccountInfoTag.class.getName());
	
	public int doStartTag() throws JspException {
		UserService userService = UserServiceFactory.getUserService();
		if (userService.isUserLoggedIn()==false) {
			throw new JspException("User is not logged in");
		}
		User usr = userService.getCurrentUser();
		PersistenceManager em = getPM();
		try {
			JPAUser dbUser = DAO.getUsersDAO().findOrCreateUser(em, usr);
			//update last time of visit
			dbUser.setLastVisit(new Date());
		} catch (Exception e) {
			e.printStackTrace();
			log.log(Level.SEVERE,e.getMessage());
			log.log(Level.SEVERE,e.getStackTrace().toString());
			throw new JspException(e);
			
		}
		return SKIP_BODY;
	}

}
