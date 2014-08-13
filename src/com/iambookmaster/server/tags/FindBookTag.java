package com.iambookmaster.server.tags;

import javax.jdo.PersistenceManager;
import javax.servlet.jsp.JspException;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.iambookmaster.server.TransactionInViewFilter;
import com.iambookmaster.server.beans.JPABook;
import com.iambookmaster.server.beans.JPAUser;
import com.iambookmaster.server.dao.BookCriteria;
import com.iambookmaster.server.dao.DAO;

@SuppressWarnings("serial")
public class FindBookTag extends MyTagSupport {
	private String criteria;
	private String criteriaScope;
	
	public String getCriteriaScope() {
		return criteriaScope;
	}

	public void setCriteriaScope(String criteriaScope) {
		this.criteriaScope = criteriaScope;
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public int doStartTag() throws JspException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (userService.isUserLoggedIn()==false) {
			throw new JspException("User is not logged in");
		}
		PersistenceManager em = TransactionInViewFilter.getEM(pageContext.getRequest());
		BookCriteria criteria;
		try {
			if (getCriteria()==null) {
				//by current user
				JPAUser userDB = DAO.getUsersDAO().findUserByEmail(em, user.getEmail());
				criteria = new BookCriteria();
				criteria.setUser(userDB);
			} else {
				Object object = getObjectByNameAndProperty(true, getCriteria(),null,criteriaScope);
				if (object instanceof BookCriteria) {
					criteria = (BookCriteria) object;
				} else {
					throw new JspException(getName()+" is not BookCriteria");
				}
			}
			JPABook book = DAO.getBookDAO().findBook(em,criteria);
			setObjectByName(book,criteriaScope);
			//store results
			return SKIP_BODY;
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e);
		}
	}

}
