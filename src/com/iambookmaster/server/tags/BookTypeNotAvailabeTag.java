package com.iambookmaster.server.tags;

import javax.servlet.jsp.JspException;

@SuppressWarnings("serial")
public class BookTypeNotAvailabeTag extends BookTypeAvailabeTag {

	public int doStartTag() throws JspException {
		if (isTypePresent()) {
			return SKIP_BODY;
		} else {
			return EVAL_BODY_INCLUDE;
		}	
	}


}
