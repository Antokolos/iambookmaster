package com.iambookmaster.server.tags;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.jsp.JspException;

import com.iambookmaster.server.beans.JPABook;
import com.iambookmaster.server.beans.JPABookVersion;
import com.iambookmaster.server.dao.DAO;

@SuppressWarnings("serial")
public class SelectBooksVersionsTag extends MyTagSupport {
	
	private String book;
	public String getBook() {
		return book;
	}
	public void setBook(String book) {
		this.book = book;
	}
	public String getBookScope() {
		return bookScope;
	}
	public void setBookScope(String bookScope) {
		this.bookScope = bookScope;
	}
	private String bookScope;
	public int doStartTag() throws JspException {
		try {
			PersistenceManager em = getPM();
			Object object = getObjectByNameAndProperty(true,book,null,bookScope);
			if (object instanceof JPABook) {
				JPABook book = (JPABook)object;
				List<JPABookVersion> list = DAO.getBookDAO().selectBookVersions(em, book);
				setObjectByName(list);
			} else {
				throw new JspException(getName()+" is not JPABook");
			}
			return SKIP_BODY;
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e);
		}
	}

}
