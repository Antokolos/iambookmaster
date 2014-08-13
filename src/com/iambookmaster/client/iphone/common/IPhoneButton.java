package com.iambookmaster.client.iphone.common;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;

public class IPhoneButton extends Button implements HasSubElement{

	private final static String[]colors = {"c12b33","48ae31","b57c40","f7c50f","6d8898"};

	public IPhoneButton(String title,ClickHandler handler) {
		this(title);
		addClickHandler(handler);
	}
	
//	@Override
//	public HandlerRegistration addClickHandler(ClickHandler handler) {
//		if (getElement().getInnerHTML().startsWith("<div>")) {
//			//add listenter to the number too
//			Element element = DOM.getFirstChild(getElement());
//			DOM.setEventListener(element, new EventListener() {
//				public void onBrowserEvent(Event event) {
//					Window.alert(event.getType());
//				}
//			});
//			DOM.sinkEvents(element, Event.ONCLICK);
//		}
//		return super.addClickHandler(handler);
//	}


	public IPhoneButton(String title) {
		super("<div>"+title+"</div>");
		setStyleName("btnCommon");
	}

	public IPhoneButton(int counter, ClickHandler handler) {
		this(counter,null,handler);
	}

	public IPhoneButton(int counter, String title, ClickHandler handler) {
		super("<div><div>"+counter+"</div>"+(title==null ? "" : title)+"</div>");
		setColor(counter);
		addClickHandler(handler);
//		setStyleName("btnCommon");
	}

	public Element getSubElement() {
		return DOM.getFirstChild(getElement());
	}
	
	public void setColor(int counter) {
		counter = counter % colors.length +1;
		setStyleName("btnGame btnGame"+counter);
	}
	
	public static String getColorValue(int counter) {
		return colors[counter % colors.length];
	}
	
}
