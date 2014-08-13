package com.iambookmaster.client;

import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.common.FileExchangeClient;
import com.iambookmaster.client.common.MaskPanel;
import com.iambookmaster.client.editor.ModelPersist;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.player.ContentPlayerImpl;

public class Master implements EntryPoint {

	public void onModuleLoad() {
		if (GWT.isScript()) {
			GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				private long last;
				public void onUncaughtException(Throwable e) {
					e.printStackTrace();
					MaskPanel.hide();
					long curr = new Date().getTime();
					if (curr-last<5000) {
						last = curr;
						return;
					}
					last = curr;
					Window.alert(e.getMessage());
				}
			});
		}
		History.fireCurrentHistoryState();
		Model model = new ModelPersist(AppLocale.getAppConstants(),AppLocale.getAppMessages());
		ContentPlayerImpl player = new ContentPlayerImpl();
		model.setContentPlayer(player);
		final VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setSize("100%", "100%");
		mainPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		final MasterPanel masterPanel = new MasterPanel(model);
		mainPanel.add(masterPanel);
		mainPanel.setCellWidth(masterPanel,"100%");
		mainPanel.setCellHeight(masterPanel,"100%");
		RootPanel rootPanel = RootPanel.get();
		mainPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		mainPanel.getElement().getStyle().setTop(0,Unit.PX);
		mainPanel.getElement().getStyle().setLeft(0,Unit.PX);
		rootPanel.setSize("100%", "100%");
		rootPanel.add(mainPanel);
		Window.addWindowClosingHandler(new ClosingHandler(){
			public void onWindowClosing(ClosingEvent event) {
				event.setMessage(AppLocale.getAppConstants().lostAllUnsavedAlert());
			}
			
		});
		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				masterPanel.onResize();
			}
		});
		FileExchangeClient.init(false);
	}

}
