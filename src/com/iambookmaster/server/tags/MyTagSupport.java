package com.iambookmaster.server.tags;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.iambookmaster.server.LocalMessages;
import com.iambookmaster.server.TransactionInViewFilter;

/**
 * Shared methods for I am Book-Master Tag library
 */
@SuppressWarnings("serial")
public abstract class MyTagSupport extends TagSupport {

	private static final Logger log = Logger.getLogger(MyTagSupport.class.getName());

	private static final Object[] EMPTY_PARAMS = new Object[0];
	
	private String name;
	private String scope;
	private String locale;
	private String property;
	
	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected String getNameAndProperty() {
		return getNameAndProperty(getName(),getProperty());
	}
	/**
	 * @return bean from a scope by name and property (if property is null - just name)
	 * @throws JspException
	 */
	protected Object getObjectByName() throws JspException {
        return getObjectByNameAndProperty(true,name,null,scope);
	}

	/**
	 * @return bean from a scope by name and property (if property is null - just name)
	 * @throws JspException
	 */
	protected Object getObjectByNameAndProperty() throws JspException {
        return getObjectByNameAndProperty(true,name,property,scope);
	}

	/**
	 * @return bean from a scope by name and property (if property is null - just name)
	 * @throws JspException
	 */
	protected String getStringByName() throws JspException {
        try {
			return (String)getObjectByName(true,name);
		} catch (ClassCastException e) {
			throw new JspException(getName()+" is not java.lang.String");
		}
	}

	/**
	 * @param critical 
	 * @return bean from a scope by name and property (if property is null - just name)
	 * @throws JspException
	 */
	protected String getStringValue(String name, boolean critical) throws JspException {
        try {
			return (String)getObjectByNameAndProperty(critical,name,null,scope);
		} catch (ClassCastException e) {
			throw new JspException(getName()+" is not java.lang.String");
		}
	}

	/**
	 * @param critical true - calls exception if no value exists 
	 * @return value of name+propery pair
	 * @throws JspException
	 */
	protected Object getObjectByName(boolean critical) throws JspException {
        return getObjectByNameAndProperty(critical,name,property,scope);
	}

	/**
	 * @param critical true - calls exception if no value exists 
	 * @return value of name+propery pair
	 * @throws JspException
	 */
	protected String getStringByName(boolean critical) throws JspException {
        try {
			return (String)getObjectByNameAndProperty(critical,name,null,scope);
		} catch (ClassCastException e) {
			throw new JspException(getName()+" is not java.lang.String");
		}

	}

	/**
	 * @return bean from a scope by name and property (if property is null - just name)
	 * @throws JspException
	 */
	protected Object getObjectByName(boolean critical,String name) throws JspException {
		return getObjectByNameAndProperty(critical,name,null,scope);
	}
	/**
	 * @return bean from a scope by name and property (if property is null - just name)
	 * @throws JspException
	 */
	protected Object getObjectByNameAndProperty(boolean critical,String name,String property,String scope) throws JspException {
		if (name==null) {
        	if (critical) {
        		throw new JspException("name of a critical bean is null");
        	} else {
        		return null;
        	}
		}
		Object bean;
		if (scope==null) {
			//all scope
			bean = pageContext.getAttribute(name);
			if (bean==null) {
				bean = pageContext.getRequest().getAttribute(name);
			}
			if (bean==null) {
				bean = pageContext.getSession().getAttribute(name);
			}
			if (bean==null) {
	        	bean = pageContext.getRequest().getParameter(name);
			}
		} else if ("page".equals(scope)) {
			bean = pageContext.getAttribute(name);
		} else if ("request".equals(scope)) {
			bean = pageContext.getRequest().getAttribute(name);
		} else if ("session".equals(scope)) {
			bean = pageContext.getSession().getAttribute(name);
		} else if ("parameters".equals(scope)) {
			bean = pageContext.getRequest().getParameter(name);
		} else {
    		throw new JspException("unknown scope "+scope);
		}
		if (bean != null && property != null) {
			String getter = property.substring(0,1).toUpperCase()+property.substring(1);
			String gettIs = "is"+getter;
			getter = "get"+getter;
			try {
				try {
					Method method = bean.getClass().getDeclaredMethod(getter);
					bean = method.invoke(bean,EMPTY_PARAMS);
				} catch (NoSuchMethodException e) {
					//check for is
					Method method = bean.getClass().getDeclaredMethod(gettIs);
					bean = method.invoke(bean,EMPTY_PARAMS);
				}
			} catch (NoSuchMethodException e1) {
				throw new JspException(getNameAndProperty(name,property)+" does not have getter "+getter+"() or "+gettIs+"()");
			} catch (Exception e) {
				e.printStackTrace();
				log.log(Level.SEVERE,e.toString());
        		throw new JspException("Cannot get "+getNameAndProperty(name,property)+" "+e.toString());
			}
		}
        if (bean == null) {
        	if (critical) {
        		throw new JspException(getNameAndProperty(name,property)+" is null");
        	} else {
        		return null;
        	}
        }
        return bean;
	}
	
	protected void setPropertyByName(Object bean,String property, Object value) throws JspException{
		String setter = "set"+property.substring(0,1).toUpperCase()+property.substring(1);
		try {
			Method method = bean.getClass().getDeclaredMethod(setter,value.getClass());
			bean = method.invoke(bean,value);
		} catch (NoSuchMethodException e) {
    		throw new JspException(getNameAndProperty(name,property)+" does not have setter "+setter+"("+value.getClass().getName()+")");
		} catch (Exception e) {
			e.printStackTrace();
			log.log(Level.SEVERE,e.toString());
    		throw new JspException("Cannot set property "+property+": "+e.toString());
		}
	}
	
	protected String getNameAndProperty(String name, String property) {
		if (property==null) {
			return name;
		} else {
			return name+"."+property;
		}
	}

	public void setObjectByName(Object object) throws JspException{
		setObjectByName(object,getName(),scope);
	}
	
	public void setObjectByName(Object object,String name) throws JspException{
		setObjectByName(object,name,scope);
	}
	
	public void setObjectByName(Object object,String name,String scope) throws JspException{
		if (scope==null || "page".equals(scope)) {
			//page scope
			pageContext.setAttribute(name,object);
		} else if ("request".equals(scope)) {
			pageContext.getRequest().setAttribute(name,object);
		} else if ("session".equals(scope)) {
			pageContext.getSession().setAttribute(name,object);
		} else {
    		throw new JspException("unknown scope "+scope);
		}
	}

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}
	
	public String getLocale() {
		if (locale==null) {
			return LocalMessages.getLocale(pageContext);
		} else {
			return locale;
		}
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	protected PersistenceManager getPM() {
		return TransactionInViewFilter.getEM(pageContext.getRequest());
	}
}
