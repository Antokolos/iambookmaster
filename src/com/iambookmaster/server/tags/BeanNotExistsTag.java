package com.iambookmaster.server.tags;

import javax.servlet.jsp.JspException;

public class BeanNotExistsTag extends BeanExistsTag {

	private static final long serialVersionUID = 1L;
	
	public int doStartTag() throws JspException {
		if (exists()) {
			return SKIP_BODY;
		} else {
			return EVAL_BODY_INCLUDE;
		}
	}

}
