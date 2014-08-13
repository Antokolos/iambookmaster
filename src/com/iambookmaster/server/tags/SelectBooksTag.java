package com.iambookmaster.server.tags;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.jsp.JspException;

import com.iambookmaster.server.TransactionInViewFilter;
import com.iambookmaster.server.beans.JPABook;
import com.iambookmaster.server.dao.BookCriteria;
import com.iambookmaster.server.dao.DAO;

@SuppressWarnings("serial")
public class SelectBooksTag extends MyTagSupport {
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
		BookCriteria crit;
		try {
			if (criteria==null) {
				//all
				crit = new BookCriteria();
			} else {
				//some
				Object object = getObjectByNameAndProperty(true, criteria,null,criteriaScope);
				if (object instanceof BookCriteria) {
					crit = (BookCriteria) object;
				} else {
					throw new JspException(getName()+" is not BookCriteria");
				}
			}
			List<JPABook> books = DAO.getBookDAO().selectBooks(em,crit);
			setObjectByName(books);
			//store results
			return SKIP_BODY;
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e);
		}
	}

}
