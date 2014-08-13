package com.iambookmaster.client.common;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.player.PlayImages;
/**
 * About panel
 * @author ggadyatskiy
 */
public class MaskPanel {
	
	private static MaskPanelPopup instance=new MaskPanelPopup();
	
	//singleton
	private MaskPanel() {
	}
	
	public static void show() {
		if (instance.isShowing()==false) {
			instance.title.setText(AppLocale.getAppConstants().maskProgress());
			instance.centerAndShow();
		}
	}
	public static void show(String title) {
		instance.title.setText(title);
		if (instance.isShowing()==false) {
			instance.centerAndShow();
		}
	}
	
	public static boolean isShown() {
		return instance.isShowing();
	}
	
	public static void setText(String text) {
		instance.title.setText(text);
	}
	
	public static void hide() {
		if (instance.isShowing()) {
			instance.hide();
		}
	}
	
	public static class MaskPanelPopup extends PopupPanel {
		private Label title;
		private MaskPanelPopup() {
			setStyleName("maskPanel");
			VerticalPanel panel = new VerticalPanel();
			panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			panel.setSize("100%", "100%");
			setWidget(panel);
			HorizontalPanel horizontalPanel = new HorizontalPanel();
			horizontalPanel.setStyleName("maskPanelIn");
			panel.add(horizontalPanel);
			panel.setCellWidth(horizontalPanel,"1%");
			panel.setCellHeight(horizontalPanel,"1%");
			Image image = new Image(PlayImages.PROGRESS);
			image.setTitle(AppLocale.getAppConstants().maskClickToClose());
			image.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					hide();
				}
			});
			horizontalPanel.add(image);
			title = new Label();
			title.setWordWrap(false);
			horizontalPanel.add(title);
		}
		
		private void centerAndShow() {
			setSize(String.valueOf(Window.getClientWidth())+"px", String.valueOf(Window.getClientHeight())+"px");
			setPopupPosition(0,0);
			show();
		}
	}

}
