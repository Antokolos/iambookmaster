package com.iambookmaster.server.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import com.iambookmaster.server.LocalMessages;

public class MessageTag extends MyTagSupport {

	private static final long serialVersionUID = 1L;
	
	private String value;
	private String arg0;
	private String arg0Name;
	private String arg0Property;
	private String arg1;
	private String arg1Name;
	private String arg1Property;
	private String arg2;
	private String arg2Name;
	private String arg2Property;
	private String arg3;
	private String arg3Name;
	private String arg3Property;
	
	public String getArg0() {
		return arg0;
	}

	public void setArg0(String arg0) {
		this.arg0 = arg0;
	}

	public String getArg0Name() {
		return arg0Name;
	}

	public void setArg0Name(String arg0Name) {
		this.arg0Name = arg0Name;
	}

	public String getArg0Property() {
		return arg0Property;
	}

	public void setArg0Property(String arg0Property) {
		this.arg0Property = arg0Property;
	}

	public String getArg1() {
		return arg1;
	}

	public void setArg1(String arg1) {
		this.arg1 = arg1;
	}

	public String getArg1Name() {
		return arg1Name;
	}

	public void setArg1Name(String arg1Name) {
		this.arg1Name = arg1Name;
	}

	public String getArg1Property() {
		return arg1Property;
	}

	public void setArg1Property(String arg1Property) {
		this.arg1Property = arg1Property;
	}

	public String getArg2() {
		return arg2;
	}

	public void setArg2(String arg2) {
		this.arg2 = arg2;
	}

	public String getArg2Name() {
		return arg2Name;
	}

	public void setArg2Name(String arg2Name) {
		this.arg2Name = arg2Name;
	}

	public String getArg2Property() {
		return arg2Property;
	}

	public void setArg2Property(String arg2Property) {
		this.arg2Property = arg2Property;
	}

	public String getArg3() {
		return arg3;
	}

	public void setArg3(String arg3) {
		this.arg3 = arg3;
	}

	public String getArg3Name() {
		return arg3Name;
	}

	public void setArg3Name(String arg3Name) {
		this.arg3Name = arg3Name;
	}

	public String getArg3Property() {
		return arg3Property;
	}

	public void setArg3Property(String arg3Property) {
		this.arg3Property = arg3Property;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	
	public int doStartTag() throws JspException {
		String key;
		if (value==null) {
			Object object = getObjectByNameAndProperty();
			if (object instanceof String) {
				key = (String)object;
			} else {
				throw new JspException(getNameAndProperty()+" is not String");
			}
		} else {
			key = value;
		}
		String val;
		if (arg0==null && arg0Name==null) {
			val = LocalMessages.getConstant(key,getLocale());
		} else if (arg1==null && arg1Name==null) {
			//1 argument
			val = LocalMessages.getMessage(key, getLocale(), getArgValue(arg0,arg0Name,arg0Property));
		} else if (arg2==null && arg2Name==null) {
			//2 arguments
			val = LocalMessages.getMessage(key, getLocale(), getArgValue(arg0,arg0Name,arg0Property),
					getArgValue(arg1,arg1Name,arg1Property));
		} else if (arg3==null && arg3Name==null) {
			//3 arguments
			val = LocalMessages.getMessage(key, getLocale(), getArgValue(arg0,arg0Name,arg0Property),
					getArgValue(arg1,arg1Name,arg1Property),
					getArgValue(arg2,arg2Name,arg2Property));
		} else {
			//4 arguments
			val = LocalMessages.getMessage(key, getLocale(), getArgValue(arg0,arg0Name,arg0Property),
					getArgValue(arg1,arg1Name,arg1Property),
					getArgValue(arg2,arg2Name,arg2Property),
					getArgValue(arg3,arg3Name,arg3Property));
		}
		
		try {
			pageContext.getOut().append(val);
		} catch (IOException e) {
			throw new JspException(e);
		}
		return SKIP_BODY;
	}

	private String getArgValue(String argValue, String name, String property) throws JspException {
		if (argValue==null) {
			Object obj = getObjectByNameAndProperty(true, name, property, null);
			if (obj instanceof String) {
				return (String) obj;
			} else {
				throw new JspException(getNameAndProperty(name, property)+" is not String");
			}
		} else {
			return argValue;
		}
	}

}
