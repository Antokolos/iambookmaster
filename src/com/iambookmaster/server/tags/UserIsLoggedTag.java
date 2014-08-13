package com.iambookmaster.server.tags;

import javax.servlet.jsp.JspException;

@SuppressWarnings("serial")
public class UserIsLoggedTag extends UserIsNotLoggedTag {

	public int doStartTag() throws JspException {
		if (logged()) {
			return EVAL_BODY_INCLUDE;
		} else {
			return SKIP_BODY;
		}
	}}
