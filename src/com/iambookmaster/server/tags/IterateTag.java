package com.iambookmaster.server.tags;

import java.util.Iterator;

import javax.servlet.jsp.JspException;

public class IterateTag extends MyTagSupport {

	private static final long serialVersionUID = 1L;
	
	private String item;
	private String itemScope;
	
	public String getItemScope() {
		return itemScope;
	}

	public void setItemScope(String itemScope) {
		this.itemScope = itemScope;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	@SuppressWarnings("unchecked")
	private Iterator iterator;
	@SuppressWarnings("unchecked")
	public int doStartTag() throws JspException {
		Object list = getObjectByName();
		if (list instanceof Iterable) {
			iterator = ((Iterable) list).iterator();
			if (iterator.hasNext()) {
				setObjectByName(iterator.next(),item,itemScope);
				return EVAL_BODY_INCLUDE;
			} else {
				return SKIP_BODY;
			}
		} else {
			throw new JspException(getName()+ " is not Iterable");
		}
	}

    public int doAfterBody() throws JspException {
		if (iterator.hasNext()) {
			setObjectByName(iterator.next(),item,itemScope);
			return EVAL_BODY_AGAIN;
		} else {
			return EVAL_PAGE;
		}
    }
	
}
