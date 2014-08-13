package com.iambookmaster.server.tags;

import javax.servlet.jsp.JspException;

import com.iambookmaster.server.dao.UserCriteria;

@SuppressWarnings("serial")
public class UserCriteriaTag extends MyTagSupport {

	private String value;
	private String valueName;
	private String valueProperty;
	private String valueScope;
	
	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public String getValueName() {
		return valueName;
	}


	public void setValueName(String valueName) {
		this.valueName = valueName;
	}


	public String getValueProperty() {
		return valueProperty;
	}


	public void setValueProperty(String valueProperty) {
		this.valueProperty = valueProperty;
	}


	public String getValueScope() {
		return valueScope;
	}


	public void setValueScope(String valueScope) {
		this.valueScope = valueScope;
	}


	public int doStartTag() throws JspException {
		UserCriteria criteria;
		Object object = getObjectByName(false,getName());
		if (object instanceof UserCriteria) {
			//already exists
			criteria = (UserCriteria) object;
		} else if (object==null){
			//create
			criteria = new UserCriteria();
			setObjectByName(criteria);
		} else {
			//error
			throw new JspException(getName()+" is not UserCriteria");
		}
		try {
			if (value != null) {
				setPropertyByName(criteria,getProperty(),value);
			} else {
				Object value = getObjectByNameAndProperty(true,valueName,valueProperty,valueScope);
				setPropertyByName(criteria,getProperty(),value);
			}
		} catch (IllegalArgumentException e) {
			throw new JspException(e);
		}
		return SKIP_BODY;
	}





}
