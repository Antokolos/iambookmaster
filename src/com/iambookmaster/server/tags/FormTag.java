package com.iambookmaster.server.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class FormTag extends MyTagSupport {

	private static final long serialVersionUID = 1L;
	
	private String action;
	private String method;
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter writer = pageContext.getOut();
			writer.append("<form ");
			if (getName() != null) {
				writer.append("name=\"");
				writer.append(getName());
				writer.append("\" ");
			}
			//method
			writer.append("method=\"");
			if (method != null) {
				writer.append(method);
			} else {
				writer.append("GET");
			}
			writer.append("\" action=\"");
//			String req = ((HttpServletRequest)pageContext.getRequest()).getRequestURL().toString();
//			pageContext.getOut().append(req.substring(0,req.lastIndexOf("/")));
//			writer.append('/');
			writer.append(action);
			writer.append("\">\n");
		} catch (IOException e) {
			throw new JspException(e);
		}
		return EVAL_BODY_INCLUDE;
	}

    public int doAfterBody() throws JspException {
		try {
			pageContext.getOut().append("</form>");
		} catch (IOException e) {
			throw new JspException(e);
	}
		return EVAL_PAGE;
    }
	
}
