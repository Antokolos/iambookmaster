package com.iambookmaster.client.iphone.common;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.iambookmaster.client.iphone.IPhoneCanvas;
import com.iambookmaster.client.iphone.IPhoneViewListenerAdapter;

public class IPhoneTextFileViewer extends IPhoneViewListenerAdapter {
	
	private ClickHandler backHandler;
	private String text;

	public IPhoneTextFileViewer() {
		backHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				back();
			}
		};
	}

	public void show(IPhoneCanvas canvas,String text) {
		this.text = text;
		canvas.setListener(this);
		redraw(canvas);
	}

	public void redraw(IPhoneCanvas canvas) {
		canvas.clear();
		canvas.add(new HTML(text));
		IPhoneButton button = new IPhoneButton("Назад");
		canvas.addClickHandler(button, backHandler);
		canvas.add(button);
		canvas.done();
	}

	public void drawn() {
	}
	
}
