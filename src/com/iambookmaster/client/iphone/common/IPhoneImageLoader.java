package com.iambookmaster.client.iphone.common;

import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class IPhoneImageLoader implements LoadHandler,ErrorHandler{

	private int counter;
	private Command command;

	public IPhoneImageLoader(String[] strings, Command command) {
		counter=strings.length;
		this.command = command;
		if (counter==0) {
			command.execute();
		} else {
			for (String url : strings) {
				final Image image = new Image(url);
				image.setVisible(false);
				image.addLoadHandler(this);
				image.addErrorHandler(this);
				RootPanel.get().add(image);
			}
		}
	}

	public void onError(ErrorEvent event) {
		process(event.getSource());
	}

	public void onLoad(LoadEvent event) {
		process(event.getSource());
	}

	private void process(Object source) {
		Image image = (Image)source;
		RootPanel.get().remove(image);
		counter--;
		if (counter==0) {
			//everything is loaded
			command.execute();
		}
	}

}
