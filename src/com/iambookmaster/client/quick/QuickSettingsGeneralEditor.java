package com.iambookmaster.client.quick;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.Images;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.Settings;
import com.iambookmaster.client.common.CompactHorizontalPanel;
import com.iambookmaster.client.common.NumberTextBox;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.SettingsListener;

public class QuickSettingsGeneralEditor extends VerticalPanel implements QuickViewWidget {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	
	private Model model;
	private SettingsListener settingsListener;
	private CheckBox oneWayConnectionsOnly;
	private CheckBox showParagraphNumbers;
	private CheckBox showConnectionNames;
	private Image nextGameVersion;
	private Label gameVersion;
	private NumberTextBox maxSizeX;
	private NumberTextBox maxSizeY;
	private CheckBox addAlchemyToText;
	private CheckBox addModificatorNamesToText;

	private CheckBox showConnectionIDs;
	public QuickSettingsGeneralEditor(Model mod) {
		this.model = mod;
		setSize("100%", "100%");
		Label label = new Label(appConstants.quickSettingsGeneralTitle());
		add(label);
		setCellHeight(label,"1%");
		setCellWidth(label,"100%");
		
		ClickHandler clickListener  = new ClickHandler() {
			public void onClick(ClickEvent event) {
				updateSettings(event.getSource());
			}
		};
		ChangeHandler changeHandler = new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateSettings(event.getSource());
			}
		};
		oneWayConnectionsOnly = new CheckBox(appConstants.quickSettingsOneWayConnections());
		oneWayConnectionsOnly.addClickHandler(clickListener);
		add(oneWayConnectionsOnly);
		setCellHeight(oneWayConnectionsOnly,"1%");
		setCellWidth(oneWayConnectionsOnly,"100%");
		
		showParagraphNumbers = new CheckBox(appConstants.quickSettingsShowParagraphNumbers());
		showParagraphNumbers.addClickHandler(clickListener);
		add(showParagraphNumbers);
		setCellHeight(showParagraphNumbers,"1%");
		setCellWidth(showParagraphNumbers,"100%");
		
		showConnectionIDs = new CheckBox(appConstants.quickSettingsShowConnectionIDs());
		showConnectionIDs.setTitle(appConstants.quickSettingsShowConnectionIDsTitle());
		showConnectionIDs.addClickHandler(clickListener);
		add(showConnectionIDs);
		setCellHeight(showConnectionIDs,"1%");
		setCellWidth(showConnectionIDs,"100%");
		
		showConnectionNames = new CheckBox(appConstants.quickSettingsShowConnectionNames());
		showConnectionNames.setTitle(appConstants.quickSettingsShowConnectionNamesTitle());
		showConnectionNames.addClickHandler(clickListener);
		add(showConnectionNames);
		setCellHeight(showConnectionNames,"1%");
		setCellWidth(showConnectionNames,"100%");
		
		CompactHorizontalPanel horizontalPanel = new CompactHorizontalPanel();
		horizontalPanel.setSpacing(5);
		gameVersion = new Label();
		gameVersion.setStyleName(Styles.BORDER);
		gameVersion.addStyleName(Styles.BOLD);
		horizontalPanel.addText(appConstants.quickSettingsGameVersion(),false);
		horizontalPanel.addCompactWidget(gameVersion);
		nextGameVersion = new Image(Images.ADD_CONNECTION);
		nextGameVersion.setTitle(appConstants.quickSettingsIncreaseVersion());
		nextGameVersion.addClickHandler(clickListener);
		horizontalPanel.addFullWidget(nextGameVersion);
		add(horizontalPanel);
		setCellHeight(horizontalPanel,"1%");
		setCellWidth(horizontalPanel,"100%");
		
		horizontalPanel = new CompactHorizontalPanel();
		horizontalPanel.addText(appConstants.quickSettingsParagraphMapWidth(),false);
		maxSizeX = new NumberTextBox();
		maxSizeX.setTitle(appConstants.quickSettingsParagraphMapTitle());
		maxSizeX.setVisibleLength(6);
		maxSizeX.addChangeHandler(changeHandler);
		maxSizeX.setMaxLength(5);
		horizontalPanel.addFullWidget(maxSizeX);
		add(horizontalPanel);
		setCellHeight(horizontalPanel,"1%");
		setCellWidth(horizontalPanel,"100%");
		
		horizontalPanel = new CompactHorizontalPanel();
		horizontalPanel.addText(appConstants.quickSettingsParagraphMapHeight(),false);
		maxSizeY = new NumberTextBox();
		maxSizeY.setTitle(appConstants.quickSettingsParagraphMapTitle());
		maxSizeY.setVisibleLength(6);
		maxSizeY.setMaxLength(5);
		maxSizeY.addChangeHandler(changeHandler);
		horizontalPanel.addFullWidget(maxSizeY);
		add(horizontalPanel);
		setCellHeight(horizontalPanel,"1%");
		setCellWidth(horizontalPanel,"100%");
		
		addAlchemyToText = new CheckBox(appConstants.quickSettingsAddAlchemyToText());
		addAlchemyToText.setTitle(appConstants.quickSettingsAddAlchemyToTextTitle());
		addAlchemyToText.addClickHandler(clickListener);
		add(addAlchemyToText);
		setCellHeight(addAlchemyToText,"1%");
		setCellWidth(addAlchemyToText,"100%");
		
		addModificatorNamesToText = new CheckBox(appConstants.quickSettingsAddModificatorNamesToText());
		addModificatorNamesToText.setTitle(appConstants.quickSettingsAddModificatorNamesToTextTitle());
		addModificatorNamesToText.addClickHandler(clickListener);
		add(addModificatorNamesToText);
		setCellHeight(addModificatorNamesToText,"1%");
		setCellWidth(addModificatorNamesToText,"100%");
		
		HTML html = new HTML("&nbsp;");
		html.setStyleName(Styles.FILLER);
		add(html);
		setCellHeight(html,"99%");
		setCellWidth(html,"100%");
		
		settingsListener = new SettingsListener(){
			public void settingsWereUpated() {
				update();
			}
		};
		model.addSettingsListener(settingsListener);
		update();
	}
	
	public void update() {
		Settings settings = model.getSettings();
		oneWayConnectionsOnly.setValue(settings.isOneWayConnectionsOnly());
		gameVersion.setText(String.valueOf(settings.getGameVersion()));
		showParagraphNumbers.setValue(settings.isShowParagraphNumbers());
		maxSizeX.setValue(settings.getMaxDimensionX());
		maxSizeY.setValue(settings.getMaxDimensionY());
		addAlchemyToText.setValue(settings.isAddAlchemyToText());
		addModificatorNamesToText.setValue(settings.isAddModificatorNamesToText());
		showConnectionIDs.setValue(settings.isShowConnectionsIDs());
		showConnectionNames.setValue(settings.isShowConnectionNames());
		
	}

	private void updateSettings(Object sender) {
		if (sender==oneWayConnectionsOnly) {
			model.getSettings().setOneWayConnectionsOnly(oneWayConnectionsOnly.getValue());
		} else if (sender==nextGameVersion) {
			int next = model.getSettings().getGameVersion()+1;
			model.getSettings().setGameVersion(next);
			gameVersion.setText(String.valueOf(next));
		} else if (sender==showParagraphNumbers) {
			model.getSettings().setShowParagraphNumbers(showParagraphNumbers.getValue());
		} else if (sender==maxSizeX) {
			model.getSettings().setMaxDimensionX(maxSizeX.getIntegerValue());
		} else if (sender==maxSizeY) {
			model.getSettings().setMaxDimensionY(maxSizeY.getIntegerValue());
		} else if (sender==addAlchemyToText) {
			model.getSettings().setAddAlchemyToText(addAlchemyToText.getValue());
		} else if (sender==addModificatorNamesToText) {
			model.getSettings().setAddModificatorNamesToText(addModificatorNamesToText.getValue());
		} else if (sender==showConnectionIDs) {
			model.getSettings().setShowConnectionsIDs(showConnectionIDs.getValue());
		} else if (sender==showConnectionNames) {
			model.getSettings().setShowConnectionNames(showConnectionNames.getValue());
		}
		
		model.updateSettings(settingsListener);
	}

	public void close() {
		model.removeSettingsListener(settingsListener);
	}

}
