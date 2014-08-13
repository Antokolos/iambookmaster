package com.iambookmaster.server.tags;

import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

public class EmptyTag extends MyTagSupport {

	private static final long serialVersionUID = 1L;
	
	public int doStartTag() throws JspException {
		if (empty()) {
			return EVAL_BODY_INCLUDE;
		} else {
			return SKIP_BODY;
		}
	}

	@SuppressWarnings("unchecked")
	protected boolean empty() throws JspException{
		Object obj = getObjectByName();
		if (obj instanceof List) {
			return ((List)obj).size()==0;
		} else if (obj instanceof Map) {
			return ((Map)obj).size()==0;
		} else if (obj instanceof String) {
			return ((String)obj).length()==0;
		} else if (obj instanceof Iterable) {
			return ((Iterable)obj).iterator().hasNext()==false;
		} else {
			throw new JspException(getName()+ ", type is not supported");
		}
	}

}
