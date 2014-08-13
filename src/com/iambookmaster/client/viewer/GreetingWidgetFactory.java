package com.iambookmaster.client.viewer;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.beans.Greeting;
import com.iambookmaster.client.player.PlayerStyles;

public class GreetingWidgetFactory {

	public static Widget create(Greeting greeting) {
		if (greeting.getImageUrl().length()>0) {
			//with Icon
			HorizontalPanel horizontalPanel = new HorizontalPanel();
			horizontalPanel.setStyleName(PlayerStyles.GREETING);
			horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			horizontalPanel.setSize("100%", "100%");
			if (greeting.getUrl().length()>0) {
				HTML html = new HTML("<a href=\""+removeHTML(greeting.getUrl())+"\"><img src=\""+removeHTML(greeting.getImageUrl())+"\"/></a>");
				html.setStyleName(PlayerStyles.GREETING_ICON);
				horizontalPanel.add(html);
				horizontalPanel.setCellWidth(html,"1%");
				horizontalPanel.setCellHeight(html,"100%");
			} else {
				Image image = new Image(greeting.getImageUrl());
				image.setStyleName(PlayerStyles.GREETING_ICON);
				horizontalPanel.add(image);
				horizontalPanel.setCellWidth(image,"1%");
				horizontalPanel.setCellHeight(image,"100%");
			}
			Widget panel = getNonIconWidget(greeting);
			horizontalPanel.add(panel);
			horizontalPanel.setCellWidth(panel,"99%");
			horizontalPanel.setCellHeight(panel,"100%");
			return horizontalPanel;
		} else {
			Widget panel = getNonIconWidget(greeting);
			panel.setStyleName(PlayerStyles.GREETING);
			return panel;
		}
	}

	private static Widget getNonIconWidget(Greeting greeting) {
		VerticalPanel panel = new VerticalPanel();
		panel.setSize("100%", "100%");
		Widget label;
		if (greeting.getUrl().length()>0) {
			//url
			label = new HTML("<a href=\""+removeHTML(greeting.getUrl())+"\" target=\"_blank\">"+removeHTML(greeting.getName())+"</a>");
		} else {
			//just name
			label = new Label(greeting.getName());
		}
		label.setStyleName(PlayerStyles.GREETING_NAME);
		panel.add(label);
		panel.setCellWidth(label,"100%");
		if (greeting.getText().length()>0) {
			panel.setCellHeight(label,"1%");
			label = new Label(greeting.getText());
			label.setStyleName(PlayerStyles.GREETING_TEXT);
			panel.add(label);
			panel.setCellHeight(label,"99%");
			panel.setCellWidth(label,"100%");
		} else {
			panel.setCellHeight(label,"100%");
		}
		return panel;
	}

	private static String removeHTML(String url) {
		return url.replace('<', ' ').replace('>', ' ');
	}

}
