package com.iambookmaster.client.iphone.common;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;

public class IPhoneFlatButton extends Label {
	
	static final IPhoneStyles css = IPhoneImages.INSTANCE.css();
	
	public IPhoneFlatButton(String title) {
		super(title);
		applyStyle();
	}

	public IPhoneFlatButton(String title,ClickHandler handler) {
		super(title);
		addClickHandler(handler);
		applyStyle();
	}

	private void applyStyle() {
		setStyleName(css.flatButton());
	}
}
