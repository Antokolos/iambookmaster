package com.iambookmaster.client.iphone.common;

import com.google.gwt.core.client.GWT;

public class IPhoneTouchProvider {
	
	private static final IPhoneTouchProvider provider = GWT.create(IPhoneTouchProvider.class);
	public static boolean noImagesPath() {
		return provider.playerImagesPath()==false;
	}
	
	public String getStartEvent() {
		return "touchstart";
	}

	public String getMoveEvent() {
		return "touchmove";
	}
	public String getEndEvent() {
		return "touchend";
	}

	public String getCancelEvent() {
		return null;
	}

	public boolean playerImagesPath() {
		return false;
	}

	public boolean isIE() {
		return false;
	}
}
