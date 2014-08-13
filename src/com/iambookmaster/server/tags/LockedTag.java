package com.iambookmaster.server.tags;

import javax.servlet.jsp.JspException;

import com.iambookmaster.server.beans.JPABook;
import com.iambookmaster.server.beans.JPAUser;

public class LockedTag extends MyTagSupport {

	private static final long serialVersionUID = 1L;
	
	public int doStartTag() throws JspException {
		if (isLocked()) {
			return EVAL_BODY_INCLUDE;
		} else {
			return SKIP_BODY;
		}
	}

	protected boolean isLocked() throws JspException{
		Object obj = getObjectByName();
		if (obj instanceof JPABook) {
			return ((JPABook)obj).isLocked(); 
		} else if (obj instanceof JPAUser) {
			return ((JPAUser)obj).isLocked(); 
		} else {
			throw new JspException(getName()+ ", type is not supported");
		}
	}
}
