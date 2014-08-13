package com.iambookmaster.server.tags;

import javax.servlet.jsp.JspException;

@SuppressWarnings("serial")
public class UserIsAdminTag extends UserIsNotAdminTag {

	public int doStartTag() throws JspException {
		if (admin()) {
			return EVAL_BODY_INCLUDE;
		} else {
			return SKIP_BODY;
		}
	}}
