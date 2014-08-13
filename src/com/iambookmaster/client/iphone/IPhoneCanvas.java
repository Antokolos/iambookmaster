package com.iambookmaster.client.iphone;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;

public interface IPhoneCanvas {
	
	void clear();
	
	void clearWithAnimation(boolean leftToRight);
	
	void setImage(String url);

	void setImage(ImageResource resource);
	
	void addSprite(String url,int x,int y);
	
	void addSprite(ImageResource resource,int x,int y);

	void add(Widget widget);

	void done();
	
	void setListener(IPhoneViewListener listener);

	void setBackgroundImage(String url);
	
	void disableAudio();

	void setAudio(boolean allowAudio);

	void setPageOrientation(boolean leftPage);
	
	void changePageOrientation();
	
	HandlerRegistration addClickHandler(Widget widget, ClickHandler handler);

	void removeWidget(Widget widget);

	boolean isVertical();

	boolean isBottomVisible();

	void scrollPageDown();

	int getClientWidth();

}
