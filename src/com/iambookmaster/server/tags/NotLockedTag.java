package com.iambookmaster.server.tags;

import javax.servlet.jsp.JspException;

public class NotLockedTag extends LockedTag {

	private static final long serialVersionUID = 1L;
	
	public int doStartTag() throws JspException {
		if (isLocked()) {
			return SKIP_BODY;
		} else {
			return EVAL_BODY_INCLUDE;
		}
	}

}
