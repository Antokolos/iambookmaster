package com.iambookmaster.server.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class LogoutURLTag extends TagSupport {

	public int doStartTag() throws JspException {
		UserService userService = UserServiceFactory.getUserService();
		try {
			pageContext.getOut().append(userService.createLogoutURL(((HttpServletRequest)pageContext.getRequest()).getRequestURI()));
		} catch (IOException e) {
			throw new JspException(e.getMessage(),e);
		}
		return SKIP_BODY;
	}
}
