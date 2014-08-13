package com.iambookmaster.server.tags;

import javax.servlet.jsp.JspException;

public class EqualsTrueTag extends MyTagSupport {

	private static final long serialVersionUID = 1L;
	
	public int doStartTag() throws JspException {
		if (isEqual()) {
			return EVAL_BODY_INCLUDE;
		} else {
			return SKIP_BODY;
		}
	}

	protected boolean isEqual() throws JspException{
		Object obj = getObjectByNameAndProperty();
		if (obj instanceof Boolean) {
			return (Boolean)obj; 
		} else if (obj==null) {
			throw new JspException(getNameAndProperty()+" is null");
		} else {
			throw new JspException(getNameAndProperty()+ " is "+obj.getClass().getName());
		}
	}

}
