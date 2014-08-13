package com.iambookmaster.server.tags;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.jsp.JspException;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class WriteTag extends MyTagSupport {

	private static final long serialVersionUID = 1L;
	
	private String format;
	
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public int doStartTag() throws JspException {
		Object object = getObjectByNameAndProperty();
		try {
			if (object instanceof String) {
				pageContext.getOut().append((String) object);
			} else if (object instanceof Date && format != null) {
				String id = this.getClass().getName()+format;
				SimpleDateFormat dateFormat = (SimpleDateFormat)pageContext.getAttribute(id);
				if (dateFormat==null) {
					dateFormat = new SimpleDateFormat(format);
					pageContext.setAttribute(id, dateFormat);
				}
				pageContext.getOut().append(dateFormat.format((Date)object));
			} else if (object instanceof Key) {
				pageContext.getOut().append(KeyFactory.keyToString((Key)object));
			} else {
				pageContext.getOut().append(String.valueOf(object));
			}
		} catch (IOException e) {
			throw new JspException(e);
		}
		return SKIP_BODY;
	}

}
