package com.iambookmaster.client.iphone;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.iphone.common.IPhoneButton;
import com.iambookmaster.client.iphone.common.IPhoneTouchListener;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;
import com.iambookmaster.client.locale.AppLocale;

public class IPhoneMessage extends PopupPanel {

	private static final IPhoneStyles css = IPhoneImages.INSTANCE.css();
	
	private static IPhoneMessage instance = new IPhoneMessage();
	
	private HTML html;

	private Timer timer;

	private int timeout;

	private double opasity;
	private IPhoneTouchListener listener;

	private IPhoneMessage() {
		super(false,true);
		listener = new IPhoneTouchListener(){
			@Override
			public boolean event(JavaScriptObject source, String name, int[] x, int[] y, JavaScriptObject target) {
				return superEvent(source, name, x, y, target);
			}
			
		};
		listener.addListener(getElement(),IPhoneTouchListener.TOUCHSTART,false);
		int size = Math.min(Window.getClientHeight(), Window.getClientWidth());
		setSize(IPhoneViewerOldBook.toPixels(size-size/4), IPhoneViewerOldBook.toPixels(size/2));
		setStyleName("iambm_log");
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setSize("100%", "100%");
		verticalPanel.setSpacing(5);
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		html = new HTML();
		verticalPanel.add(html);
		verticalPanel.setCellWidth(html,"100%");
		verticalPanel.setCellHeight(html,"99%");
		
		IPhoneButton label = new IPhoneButton(AppLocale.getAppConstants().iphoneCloseMessage());
		ClickHandler handler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (timer!=null) {
					timer.cancel();
					timer = null;
				}
				hide();
			}
		};
		label.addClickHandler(handler);
		listener.addClickHandler(label, handler);
//		label.setStyleName(css.stateSelection());
		verticalPanel.add(label);
		verticalPanel.setCellWidth(label,"100%");
		setWidget(verticalPanel);
	}

	public static void showMessage(String message) {
		instance.html.setHTML(message);
		instance.start();
	}

	private void start() {
		timeout=0;
		opasity=1;
		getElement().getStyle().setOpacity(opasity);
		if (timer==null) {
			timer = new Timer(){
				@Override
				public void run() {
					timeout++;
					if (timeout>40) {
						if (opasity>0) {
							opasity = opasity - 0.1;
							getElement().getStyle().setOpacity(opasity);
						} else {
							timer = null;
							cancel();
							hide();
						}
					}
				}
			};
			timer.scheduleRepeating(100);
			show();
			center();
		}
	}
}
