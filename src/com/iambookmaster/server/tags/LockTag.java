package com.iambookmaster.server.tags;

import javax.jdo.PersistenceManager;
import javax.servlet.jsp.JspException;

import com.iambookmaster.server.beans.JPABook;
import com.iambookmaster.server.beans.JPAUser;
import com.iambookmaster.server.dao.DAO;

@SuppressWarnings("serial")
public class LockTag extends MyTagSupport {
	
	private String lock;
	
	public String getLock() {
		return lock;
	}

	public void setLock(String lock) {
		this.lock = lock;
	}

	public int doStartTag() throws JspException {
		PersistenceManager em = getPM();
		try {
			Object object = getObjectByName();
			boolean lock = Boolean.parseBoolean(getLock());
			if (object instanceof JPAUser) {
				DAO.getUsersDAO().lock(em,(JPAUser) object,lock);
			} else if (object instanceof JPABook) {
				DAO.getBookDAO().lock(em,(JPABook) object,lock);
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
