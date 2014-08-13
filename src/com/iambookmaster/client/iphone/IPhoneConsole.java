package com.iambookmaster.client.iphone;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class IPhoneConsole extends PopupPanel implements ClickHandler{

	private static IPhoneConsole instance = new IPhoneConsole();
	
	private Button close;
	private Button clear;
	private HTML view;
	private StringBuilder log;
	private CheckBox storeClicks;

	private IPhoneConsole() {
		log = new StringBuilder();
		maxSize();
		setStyleName("iambm_log");
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setSize("100%", "100%");
		close = new Button("Close",this);
		clear = new Button("Clear",this);
		storeClicks = new CheckBox("Events");
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(close);
		horizontalPanel.add(clear);
		horizontalPanel.add(storeClicks);
		horizontalPanel.setWidth("100%");
		verticalPanel.add(horizontalPanel);
		view = new HTML();
		verticalPanel.add(view);
		setWidget(verticalPanel);
	}

	public void onClick(ClickEvent event) {
		if (event.getSource()==close) {
			hide();
		} else if (event.getSource()==clear) {
			log.setLength(0);
			hide();
		}
	}
	
	public void show() {
		maxSize();
		view.setHTML(log.toString());
		super.show();
	}
	
	private void maxSize() {
		setSize(IPhoneViewerOldBook.toPixels(Window.getClientWidth()), IPhoneViewerOldBook.toPixels(Window.getClientHeight()));
	}

	public static void showLog() {
		if (instance.isShowing()==false) {
			instance.show();
		}
	}
	
	public static void addMessage(Object... messages) {
		if (instance.storeClicks.getValue()==false) {
			return;
		}
		add(messages);
	}

	private static void add(Object... messages) {
		for (Object object : messages) {
			instance.log.append(object);
		}
		instance.log.append("<br/>");
	}
	public static void showError(Throwable throwable) {
		if (throwable instanceof NullPointerException) {
			add("NullPointerException");
		} else {
			add(throwable.getMessage());
		}
		StackTraceElement[] stack = throwable.getStackTrace();
		for (StackTraceElement stackTraceElement : stack) {
			add(stackTraceElement.getFileName(),' ',stackTraceElement.getLineNumber());
		}
		instance.show();
	}
}
