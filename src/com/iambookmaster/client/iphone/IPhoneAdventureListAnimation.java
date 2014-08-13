package com.iambookmaster.client.iphone;

import com.google.gwt.user.client.ui.Label;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;
import com.iambookmaster.client.locale.AppLocale;

public class IPhoneAdventureListAnimation extends Label {

	private static IPhoneStyles css = IPhoneImages.INSTANCE.css();

//	private Timer timer;
//	private int timeout;
//	private double opasity;
//	private IPhoneTouchListener listener;
//	private AbsolutePanel canvas;
//	private Image finger;

	public IPhoneAdventureListAnimation() {
//		setSize("300px","30px");
		setStyleName(css.adventureListNote());
		setText(AppLocale.getAppConstants().iphoneYouHaveAdventureList());
//		setWordWrap(false);
	}

}
