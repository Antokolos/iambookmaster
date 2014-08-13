package com.iambookmaster.client.iphone.common;

public class IPhoneTouchProviderNormal extends IPhoneTouchProvider {

	public String getStartEvent() {
		return "mousedown";
	}

	public String getMoveEvent() {
		return "mousemove";
	}
	public String getEndEvent() {
		return "mouseup";
	}

	public String getCancelEvent() {
		return "mouseout";
	}

	public boolean playerImagesPath() {
		return true;
	}
}
