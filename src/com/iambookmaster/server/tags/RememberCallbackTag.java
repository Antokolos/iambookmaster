package com.iambookmaster.server.tags;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.jsp.JspException;

import com.iambookmaster.client.common.Base64Coder;
import com.iambookmaster.client.remote.RemotePanel;

@SuppressWarnings("serial")
public class RememberCallbackTag extends MyTagSupport {

	private static final Logger log = Logger.getLogger(RememberCallbackTag.class.getName());
	
	private static final String CALLBACK=RememberCallbackTag.class.getName();
	
	public int doStartTag() throws JspException {
		String callback = pageContext.getRequest().getParameter(RemotePanel.FIELD_CALLBACK);
		if (callback != null) {
			//update in session
			try {
				callback = Base64Coder.decodeString(callback);
				pageContext.getSession().setAttribute(CALLBACK, callback);
			} catch (Exception e) {
				log.log(Level.WARNING,e.getMessage());
			}
		}
		return SKIP_BODY;
	}
	
	protected boolean isCallbackPresent() {
		return pageContext.getSession().getAttribute(CALLBACK) != null;
	}

	protected String getCallback() {
		return (String)pageContext.getSession().getAttribute(CALLBACK);
	}
}
