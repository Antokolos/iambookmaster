package com.iambookmaster.server.tags;

import javax.jdo.PersistenceManager;
import javax.servlet.jsp.JspException;

import com.iambookmaster.server.beans.JPABook;
import com.iambookmaster.server.beans.JPAUser;
import com.iambookmaster.server.dao.DAO;

@SuppressWarnings("serial")
public class RemoveTag extends MyTagSupport {
	
	public int doStartTag() throws JspException {
		PersistenceManager em = getPM();
		try {
			Object object = getObjectByName();
			if (object instanceof JPAUser) {
				DAO.getUsersDAO().remove(em,(JPAUser) object);
			} else if (object instanceof JPABook) {
				DAO.getBookDAO().remove(em,(JPABook) object);
			} else {
				throw new JspException(getName()+" is not JPABook or JPAUser");
			}
			return SKIP_BODY;
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e);
		}
	}

}
