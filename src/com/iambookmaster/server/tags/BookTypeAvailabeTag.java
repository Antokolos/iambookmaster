package com.iambookmaster.server.tags;

import javax.servlet.jsp.JspException;

@SuppressWarnings("serial")
public class BookTypeAvailabeTag extends LoadModelTag {

	public int doStartTag() throws JspException {
		if (isTypePresent()) {
			return EVAL_BODY_INCLUDE;
		} else {
			return SKIP_BODY;
		}	
	}

	@Override
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

}
