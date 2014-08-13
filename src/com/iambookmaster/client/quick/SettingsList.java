package com.iambookmaster.client.quick;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.common.ScrollContainer;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;

public class SettingsList extends ScrollContainer {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	
	public static final int BLOCK_BOOK_INFO = 0;
	public static final int BLOCK_PARAGRAPHS_EDITOR = 1;
	public static final int BLOCK_TEXT_GENERATION = 2;
	public static final int BLOCK_PLAYER_SETTINGS = 3;
	public static final int GREETINGS = 4;
	private VerticalPanel mainPanel;
	private boolean activationNeed=true;
	private SettingsBlock selected;
	private Model model;
	
	public void activate() {
		if (activationNeed) {
			activationNeed = false;
			resetHeight();
		}
	}
	
	public void activateLater() {
		activationNeed = true;
	}
	
	public SettingsList(Model mod) {
		model = mod;
		mainPanel = new VerticalPanel();
		mainPanel.setSize("100%", "100%");
		setScrollWidget(mainPanel);
		mainPanel.setStyleName("settings_list");
		new SettingsBlock(appConstants.quickSettingsBookInfo(),BLOCK_BOOK_INFO);
		new SettingsBlock(appConstants.quickSettingsGeneral(),BLOCK_PARAGRAPHS_EDITOR);
		new SettingsBlock(appConstants.quickSettingsPlayer(),BLOCK_PLAYER_SETTINGS);
		new SettingsBlock(appConstants.quickSettingsGeneration(),BLOCK_TEXT_GENERATION);
		new SettingsBlock(appConstants.quickSettingsGreetings(),GREETINGS);
		//filler
		HTML html = new HTML("&nbsp;");
		html.setStyleName("filler");
		mainPanel.add(html);
		mainPanel.setCellHeight(html,"99%");
		mainPanel.setCellWidth(html,"100%");
		
	}

	public class SettingsBlock extends Label{
		private int code;
		public SettingsBlock(String name, int code) {
			super(name,false);
			this.code = code;
			mainPanel.add(this);
			mainPanel.setCellHeight(this,"1%");
			mainPanel.setCellWidth(this,"100%");
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (selected != SettingsBlock.this) {
						if (selected != null) {
							SettingsBlock old = selected;
							selected = null;
							old.applyStyle();
						}
						selected = SettingsBlock.this;
						applyStyle();
						onSelected();
					}
				}
			});
			applyStyle();
		}
		private void applyStyle() {
			if (selected==this) {
				setStyleName("settings_selected");
			} else {
				setStyleName("settings_normal");
			}
		}
		
	}
	
	public QuickViewWidget getSelectedWidget() {
		if (selected==null) {
			return null;
		} else {
			switch (selected.code) {
			case BLOCK_PARAGRAPHS_EDITOR:
				return new QuickSettingsGeneralEditor(model); 
			case BLOCK_PLAYER_SETTINGS:
				return new QuickSettingsPlayerEditor(model); 
			case BLOCK_TEXT_GENERATION:
				return new QuickSettingsBookGenerationEditor(model); 
			case GREETINGS:
				return new QuickSettingsGreetingsEditor(model); 
			default:
				//case BLOCK_BOOK_INFO:
				return new QuickSettingsBookInfoEditor(model); 
			}
		}
	}

	public void onSelected() {
	}
}
