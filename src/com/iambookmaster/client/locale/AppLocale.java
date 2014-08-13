package com.iambookmaster.client.locale;

import com.google.gwt.core.client.GWT;

public class AppLocale {

	private static final AppConstants appConstants = GWT.create(AppConstants.class);
	private static final AppMessages appMessages = GWT.create(AppMessages.class);
	
	public static AppConstants getAppConstants() {
		return appConstants;
	}
	public static AppMessages getAppMessages() {
		return appMessages;
	}
}
