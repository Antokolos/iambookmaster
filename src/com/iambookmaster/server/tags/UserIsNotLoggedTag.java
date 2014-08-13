package com.iambookmaster.server.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UserIsNotLoggedTag extends TagSupport {

	private static final long serialVersionUID = 1L;
	
	private static final String STATUS = "com.iambookmaster.server.tags.UserIsNotLoggedTag";

	public int doStartTag() throws JspException {
		if (logged()) {
			return SKIP_BODY;
		} else {
			return EVAL_BODY_INCLUDE;
		}
	}
	
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}
	
	protected boolean logged() {
		Boolean status = (Boolean)pageContext.getAttribute(STATUS);
		if (status == null) {
			//no status - define it
			UserService userService = UserServiceFactory.getUserService();
			status = userService.isUserLoggedIn();
			pageContext.setAttribute(STATUS, status);
		}
		return status.booleanValue();
	}



}
