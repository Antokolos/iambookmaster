package com.iambookmaster.server;

import java.io.Serializable;
import java.lang.reflect.Method;

public class LocaleProxy implements java.lang.reflect.InvocationHandler,Serializable{

	private static final long serialVersionUID = 1L;
	private final String locale;
	
	public LocaleProxy(String locale) {
		this.locale = locale;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (args==null || args.length==0) {
			return LocalMessages.getConstant(method.getName(), locale);
		} else {
			return LocalMessages.getMessage(method.getName(), locale,args);
		}
	}
	
}
