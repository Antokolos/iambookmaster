package com.iambookmaster.client.quick;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.Settings;
import com.iambookmaster.client.common.ColorPicker;
import com.iambookmaster.client.common.CompactHorizontalPanel;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.SettingsListener;

public class QuickSettingsPlayerEditor extends VerticalPanel implements QuickViewWidget {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	
	private Model model;
	private SettingsListener settingsListener;
	private CheckBox hiddenUsingObjects;
	private ListBox bagStatus;
	private ColorPicker textColor;
	private ColorPicker bagColor;
	private ColorPicker applicationtColor;
	private ColorPicker textBackground;
	private CheckBox disableAudio;
	private CheckBox disableImages;
	private CheckBox showAboutOnStart;
	private CheckBox showModificators;
	private CheckBox showBattleConsole;
	private CheckBox skipMustGoParagraphs;
	private CheckBox hideNonMathedParametercConnections;
	private TextBox feedbackEmail;
	private CheckBox verticalObjects;
	private CheckBox overflowControl;

	private CheckBox nonCommercialOnly;
//	private ListBox showConnectionType;
	
	public QuickSettingsPlayerEditor(Model mod) {
		this.model = mod;
		setSpacing(5);
		setSize("100%", "100%");
		Label label = new Label(appConstants.quickPlayerTitle());
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
		hiddenUsingObjects = new CheckBox(appConstants.quickPlayerHiddenItems());
		hiddenUsingObjects.addClickHandler(clickListener);
		add(hiddenUsingObjects);
		setCellHeight(hiddenUsingObjects,"1%");
		setCellWidth(hiddenUsingObjects,"100%");
		
		skipMustGoParagraphs = new CheckBox(appConstants.quickPlayerSkipMustGoParagraphs());
		skipMustGoParagraphs.setTitle(appConstants.quickPlayerSkipMustGoParagraphsTitle());
		skipMustGoParagraphs.addClickHandler(clickListener);
		add(skipMustGoParagraphs);
		setCellHeight(skipMustGoParagraphs,"1%");
		setCellWidth(skipMustGoParagraphs,"100%");
		
		CompactHorizontalPanel horizontalPanel = new CompactHorizontalPanel();
		bagStatus = new ListBox();
		bagStatus.addChangeHandler(changeHandler);
		bagStatus.addItem(appConstants.quickPlayerShowListAlways());
		bagStatus.addItem(appConstants.quickPlayerShowListPopup());
		bagStatus.addItem(appConstants.quickPlayerShowListNo());
		horizontalPanel.addCompactWidget(bagStatus);
		horizontalPanel.addFullText(appConstants.quickPlayerShowList());
		add(horizontalPanel);
		setCellHeight(horizontalPanel,"1%");
		setCellWidth(horizontalPanel,"100%");

		showModificators = new CheckBox(appConstants.quickPlayerShowModificators());
		showModificators.setTitle(appConstants.quickPlayerShowModificatorsTitle());
		showModificators.addClickHandler(clickListener);
		add(showModificators);
		setCellHeight(showModificators,"1%");
		setCellWidth(showModificators,"100%");
		
		/*Colors*/
		horizontalPanel = new CompactHorizontalPanel();
		textColor = new ColorPicker();
		textColor.addChangeHandler(changeHandler);
		horizontalPanel.addCompactWidget(textColor);
		horizontalPanel.addFullText(appConstants.quickPlayerTextColor());
		add(horizontalPanel);
		setCellHeight(horizontalPanel,"1%");
		setCellWidth(horizontalPanel,"100%");
		
		horizontalPanel = new CompactHorizontalPanel();
		textBackground = new ColorPicker();
		textBackground.addChangeHandler(changeHandler);
		horizontalPanel.addCompactWidget(textBackground);
		horizontalPanel.addFullText(appConstants.quickPlayerTextBackground());
		add(horizontalPanel);
		setCellHeight(horizontalPanel,"1%");
		setCellWidth(horizontalPanel,"100%");
		
		horizontalPanel = new CompactHorizontalPanel();
		bagColor = new ColorPicker();
		bagColor.addChangeHandler(changeHandler);
		horizontalPanel.addCompactWidget(bagColor);
		horizontalPanel.addFullText(appConstants.quickPlayerListBackground());
		add(horizontalPanel);
		setCellHeight(horizontalPanel,"1%");
		setCellWidth(horizontalPanel,"100%");
		
		horizontalPanel = new CompactHorizontalPanel();
		applicationtColor = new ColorPicker();
		applicationtColor.addChangeHandler(changeHandler);
		horizontalPanel.addCompactWidget(applicationtColor);
		horizontalPanel.addFullText(appConstants.quickPlayerApplicationBackground());
		add(horizontalPanel);
		setCellHeight(horizontalPanel,"1%");
		setCellWidth(horizontalPanel,"100%");
		
		disableAudio = new CheckBox(appConstants.quickPlayerDisableAudio());
		disableAudio.addClickHandler(clickListener);
		add(disableAudio);
		setCellHeight(disableAudio,"1%");
		setCellWidth(disableAudio,"100%");
		
		disableImages = new CheckBox(appConstants.quickPlayerDisableImages());
		disableImages.addClickHandler(clickListener);
		add(disableImages);
		setCellHeight(disableImages,"1%");
		setCellWidth(disableImages,"100%");
		
		showAboutOnStart = new CheckBox(appConstants.quickPlayerShowAbout());
		showAboutOnStart.addClickHandler(clickListener);
		add(showAboutOnStart);
		setCellHeight(showAboutOnStart,"1%");
		setCellWidth(showAboutOnStart,"100%");
		
		horizontalPanel = new CompactHorizontalPanel();
		feedbackEmail = new TextBox();
		feedbackEmail.addChangeHandler(changeHandler);
		feedbackEmail.setTitle(appConstants.quickPlayerFeedbackEmailTitle());
		horizontalPanel.addText(appConstants.quickPlayerFeedbackEmail());
		horizontalPanel.addFullWidget(feedbackEmail);
		add(horizontalPanel);
		setCellHeight(horizontalPanel,"1%");
		setCellWidth(horizontalPanel,"100%");
		
		hideNonMathedParametercConnections = new CheckBox(appConstants.quickPlayerHideNonMathedParametersConnections());
		hideNonMathedParametercConnections.setTitle(appConstants.quickPlayerHideNonMathedParametersConnectionsTitle());
		hideNonMathedParametercConnections.addClickHandler(clickListener);
		add(hideNonMathedParametercConnections);
		setCellHeight(hideNonMathedParametercConnections,"1%");
		setCellWidth(hideNonMathedParametercConnections,"100%");
		
		showBattleConsole = new CheckBox(appConstants.quickPlayerShowBattleConsole());
		showBattleConsole.setTitle(appConstants.quickPlayerShowBattleConsoleTitle());
		showBattleConsole.addClickHandler(clickListener);
		add(showBattleConsole);
		setCellHeight(showBattleConsole,"1%");
		setCellWidth(showBattleConsole,"100%");
		
		overflowControl = new CheckBox(appConstants.quickPlayerOverflowControl());
		overflowControl.setTitle(appConstants.quickPlayerOverflowControlTitle());
		overflowControl.addClickHandler(clickListener);
		add(overflowControl);
		setCellHeight(overflowControl,"1%");
		setCellWidth(overflowControl,"100%");
		
		
		
		verticalObjects = new CheckBox(appConstants.quickPlayerVerticalObject());
		verticalObjects.setTitle(appConstants.quickPlayerVerticalObjectTitle());
		verticalObjects.addClickHandler(clickListener);
		add(verticalObjects);
		setCellHeight(verticalObjects,"1%");
		setCellWidth(verticalObjects,"100%");
		
		nonCommercialOnly = new CheckBox(appConstants.quickPlayerNonCommercialOny());
		nonCommercialOnly.setTitle(appConstants.quickPlayerNonCommercialOnyTitle());
		nonCommercialOnly.addClickHandler(clickListener);
		add(nonCommercialOnly);
		setCellHeight(nonCommercialOnly,"1%");
		setCellWidth(nonCommercialOnly,"100%");
		
//		showConnectionType = new ListBox();
//		showConnectionType.addItem(appConstants.quickPlayerConnectionTypeDefault(), "0");
//		showConnectionType.addItem(appConstants.quickPlayerConnectionTypeLastWord(), "1");
//		showConnectionType.addItem(appConstants.quickPlayerConnectionTypeBrackets(), "2");
//		showConnectionType.addItem(appConstants.quickPlayerConnectionTypeName(), "3");
//		showConnectionType.addChangeHandler(changeHandler);
//		showConnectionType.setTitle(appConstants.quickPlayerConnectionsType());
//		horizontalPanel.addText(appConstants.quickPlayerFeedbackEmail());
//		horizontalPanel.addFullWidget(showConnectionType);
//		add(horizontalPanel);
//		setCellHeight(horizontalPanel,"1%");
//		setCellWidth(horizontalPanel,"100%");
		
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
		hiddenUsingObjects.setValue(settings.isHiddenUsingObjects());
		bagStatus.setSelectedIndex(settings.getPlayerListType());
		textColor.setSelectedIndex(settings.getTextColor());
		textBackground.setSelectedIndex(settings.getTextBackground());
		bagColor.setSelectedIndex(settings.getBagColor());
		applicationtColor.setSelectedIndex(settings.getApplicationColor());
		disableAudio.setValue(settings.isDisableAudio());
		disableImages.setValue(settings.isDisableImages());
		showAboutOnStart.setValue(settings.isShowAboutOnStart());
		showModificators.setValue(settings.isShowModificators());
		hideNonMathedParametercConnections.setValue(settings.isHideNonMatchedParameterConnections());
		showBattleConsole.setValue(settings.isShowBattleConsole());
		overflowControl.setValue(settings.isOverflowControl());
		skipMustGoParagraphs.setValue(settings.isSkipMustGoParagraphs());
		verticalObjects.setValue(settings.isVerticalObjects());
		nonCommercialOnly.setValue(settings.isDemoVersion());
//		showConnectionType.setSelectedIndex(settings.getShowConnectionType());
		if (settings.getFeedbackEmail()==null) {
			feedbackEmail.setText("");
		} else {
			feedbackEmail.setText(settings.getFeedbackEmail());
		}
	}

	private void updateSettings(Object object) {
		if (object==hiddenUsingObjects) {
			model.getSettings().setHiddenUsingObjects(hiddenUsingObjects.getValue());
		} else if (object==bagStatus) {
			model.getSettings().setPlayerListType(bagStatus.getSelectedIndex());
		} else if (object==textColor) {
			model.getSettings().setTextColor(textColor.getSelectedIndex());
		} else if (object==textBackground) {
			model.getSettings().setTextBackground(textBackground.getSelectedIndex());
		} else if (object==bagColor) {
			model.getSettings().setBagColor(bagColor.getSelectedIndex());
		} else if (object==applicationtColor) {
			model.getSettings().setApplicationColor(applicationtColor.getSelectedIndex());
		} else if (object==disableAudio) {
			model.getSettings().setDisableAudio(disableAudio.getValue());
		} else if (object==disableImages) {
			model.getSettings().setDisableImages(disableImages.getValue());
		} else if (object==showAboutOnStart) {
			model.getSettings().setShowAboutOnStart(showAboutOnStart.getValue());
		} else if (object==feedbackEmail) {
			model.getSettings().setFeedbackEmail(feedbackEmail.getText().trim());
		} else if (object==showModificators) {
			model.getSettings().setShowModificators(showModificators.getValue());
		} else if (object==showBattleConsole) {
			model.getSettings().setShowBattleConsole(showBattleConsole.getValue());
		} else if (object==hideNonMathedParametercConnections) {
			model.getSettings().setHideNonMatchedParameterConnections(hideNonMathedParametercConnections.getValue());
		} else if (object==skipMustGoParagraphs) {
			model.getSettings().setSkipMustGoParagraphs(skipMustGoParagraphs.getValue());
		} else if (object==verticalObjects) {
			model.getSettings().setVerticalObjects(verticalObjects.getValue());
		} else if (object==overflowControl) {
			model.getSettings().setOverflowControl(overflowControl.getValue());
		} else if (object==nonCommercialOnly) {
			model.getSettings().setDemoVersion(nonCommercialOnly.getValue());
//		} else if (object==showConnectionType) {
//			model.getSettings().setShowConnectionType(showConnectionType.getSelectedIndex());
		}  
		model.updateSettings(settingsListener);
	}

	public void close() {
		model.removeSettingsListener(settingsListener);
	}

}
