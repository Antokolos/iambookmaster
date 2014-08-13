package com.iambookmaster.server.tags;

import javax.servlet.jsp.JspException;

public class NotEmptyTag extends EmptyTag {

	private static final long serialVersionUID = 1L;
	
	public int doStartTag() throws JspException {
		if (empty()) {
			return SKIP_BODY;
		} else {
			return EVAL_BODY_INCLUDE;
		}
	}

}
