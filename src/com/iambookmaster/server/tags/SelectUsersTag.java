package com.iambookmaster.server.tags;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.jsp.JspException;

import com.iambookmaster.server.TransactionInViewFilter;
import com.iambookmaster.server.beans.JPAUser;
import com.iambookmaster.server.dao.DAO;
import com.iambookmaster.server.dao.UserCriteria;

@SuppressWarnings("serial")
public class SelectUsersTag extends MyTagSupport {
	private String criteria;
	private String criteriaScope;
	
	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public String getCriteriaScope() {
		return criteriaScope;
	}

	public void setCriteriaScope(String criteriaScope) {
		this.criteriaScope = criteriaScope;
	}
	
	public int doStartTag() throws JspException {
		PersistenceManager em = TransactionInViewFilter.getEM(pageContext.getRequest());
		UserCriteria criteria;
		try {
			if (getCriteria()==null) {
				//all users
				criteria = new UserCriteria();
			} else {
				Object object = getObjectByNameAndProperty(true, getCriteria(),null,criteriaScope);
				if (object instanceof UserCriteria) {
					criteria = (UserCriteria) object;
				} else {
					throw new JspException(getName()+" is not UserCriteria");
				}
			}
			List<JPAUser> users = DAO.getUsersDAO().selectUsers(em,criteria);
			setObjectByName(users);
			//store results
			return SKIP_BODY;
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e);
		}
	}

}
