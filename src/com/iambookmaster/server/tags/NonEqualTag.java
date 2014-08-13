package com.iambookmaster.server.tags;

import javax.servlet.jsp.JspException;

public class NonEqualTag extends EqualsTag {

	private static final long serialVersionUID = 1L;
	
	public int doStartTag() throws JspException {
		if (isEqual()) {
			return SKIP_BODY;
		} else {
			return EVAL_BODY_INCLUDE;
		}
	}

}
