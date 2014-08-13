package com.iambookmaster.client.iphone;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.beans.Greeting;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;
import com.iambookmaster.client.model.ContentPlayer;

public class IPhoneGreetingWidgetFactory {
	
	private static final IPhoneStyles css = IPhoneImages.INSTANCE.css();

	public static void create(Greeting greeting, IPhoneCanvas canvas,ContentPlayer player) {
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setWidth("100%");
		Widget result = verticalPanel;
		if (greeting.getImageUrl().length()>0) {
			//with Icon
			HorizontalPanel horizontalPanel = new HorizontalPanel();
			horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			horizontalPanel.setWidth("100%");
			Image image = new Image(greeting.getImageUrl());
			image.setStyleName(css.greetingIcon());
			horizontalPanel.add(image);
			horizontalPanel.setCellWidth(image,"1%");
			horizontalPanel.add(verticalPanel);
			horizontalPanel.setCellWidth(verticalPanel,"99%");
			result = horizontalPanel;
		}
		Label label = new Label(greeting.getName());
		label.setStyleName(css.greetingName());
		verticalPanel.add(label);
		verticalPanel.setCellWidth(label,"100%");
		final String url = greeting.getUrl();
		ClickHandler handler = (url==null || url.trim().length()==0) ? null : new MyClickHandler(url,player);
		if (handler != null) {
			label.addStyleName(css.greetingClickableHyper());
			label.addClickHandler(handler);
			canvas.addClickHandler(label, handler);
		}
		if (greeting.getText().length()>0) {
			label = new Label(greeting.getText());
			label.setStyleName(css.greetingText());
			if (handler != null) {
				label.addStyleName(css.greetingClickable());
				label.addClickHandler(handler);
				canvas.addClickHandler(label, handler);
			}
			verticalPanel.add(label);
			verticalPanel.setCellWidth(label,"100%");
		}
		result.setStyleName(css.greetingPanel());
		canvas.add(result);
	}
	
	public static class MyClickHandler implements ClickHandler {
		
		private String url;
		private ContentPlayer player;
		
		public MyClickHandler(String url, ContentPlayer player) {
			this.url = url;
			this.player = player;
		}

		public void onClick(ClickEvent event) {
			player.openURL(url);
		}
	}

}
