package com.iambookmaster.server.tags;

import javax.servlet.jsp.JspException;

public class BeanExistsTag extends MyTagSupport {

	private static final long serialVersionUID = 1L;
	
	public int doStartTag() throws JspException {
		if (exists()) {
			return EVAL_BODY_INCLUDE;
		} else {
			return SKIP_BODY;
		}
	}
	
	protected boolean exists() throws JspException{
		return getObjectByName(false)!=null;
	}



}
