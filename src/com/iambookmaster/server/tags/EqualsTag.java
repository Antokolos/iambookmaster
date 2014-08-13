package com.iambookmaster.server.tags;

import javax.servlet.jsp.JspException;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class EqualsTag extends MyTagSupport {

	private static final long serialVersionUID = 1L;
	
	private String value;
	private String valueProperty;
	private String valueName;
	private String valueScope;
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValueProperty() {
		return valueProperty;
	}

	public void setValueProperty(String valueProperty) {
		this.valueProperty = valueProperty;
	}

	public String getValueName() {
		return valueName;
	}

	public void setValueName(String valueName) {
		this.valueName = valueName;
	}

	public String getValueScope() {
		return valueScope;
	}

	public void setValueScope(String valueScope) {
		this.valueScope = valueScope;
	}

	public int doStartTag() throws JspException {
		if (isEqual()) {
			return EVAL_BODY_INCLUDE;
		} else {
			return SKIP_BODY;
		}
	}

	protected boolean isEqual() throws JspException{
		Object obj = getObjectByNameAndProperty();
		if (obj instanceof Key) {
			if (value != null) {
				return value.equals(KeyFactory.keyToString((Key)obj));
			} else {
				Object val = getObjectByNameAndProperty(true,valueName, valueProperty, valueScope);
				if (val instanceof Key) {
					return obj.equals(val);
				} else {
					throw new JspException(getNameAndProperty(valueName,valueProperty)+" is not Key");
				}
			}
			
		} else if (obj instanceof Boolean) {
			if (value != null) {
				return obj.equals(Boolean.parseBoolean(value));
			} else {
				Object val = getObjectByNameAndProperty(true,valueName, valueProperty, valueScope);
				if (val instanceof Boolean) {
					return obj.equals(val);
				} else {
					throw new JspException(getNameAndProperty(valueName,valueProperty)+" is not boolean");
				}
			}
		} else if (value != null) {
			//compare with String
			if (obj instanceof String) {
				return value.equals(obj);
			} else {
				throw new JspException(getNameAndProperty()+" is not String");
			}
		} else if (obj instanceof String) {
			//compare two String
			Object val = getObjectByNameAndProperty(true,valueName, valueProperty, valueScope);
			if (val instanceof String) {
				return value.equals(obj);
			} else {
				throw new JspException(getNameAndProperty(valueName,valueProperty)+" is not String");
			}
		} else if (obj != null) {
			//all other 
			return obj.equals(getObjectByNameAndProperty(true,valueName, valueProperty, valueScope));
		} else {
			throw new JspException(getNameAndProperty()+" is null");
		}
	}

}
