package com.iambookmaster.client.quick;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.common.CompactHorizontalPanel;
import com.iambookmaster.client.common.NumberTextBox;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.SettingsListener;

public class QuickSettingsBookGenerationEditor extends VerticalPanel implements QuickViewWidget {
	
	private static final AppConstants appConstants = AppLocale.getAppConstants();
	private Model model;
	private SettingsListener settingsListener;
	private CheckBox closeParagraphControl;
	private ListBox fineKeys;
	private NumberTextBox closeLenght;
	private NumberTextBox maxAttempt;
	public QuickSettingsBookGenerationEditor(Model mod) {
		this.model = mod;
		setSize("100%", "100%");
		Label label = new Label(appConstants.quickTextBookGeneration());
		add(label);
		setCellHeight(label,"1%");
		setCellWidth(label,"100%");
		
		ClickListener clickListener = new ClickListener() {
			public void onClick(Widget sender) {
				if (sender==closeParagraphControl) {
					if (closeParagraphControl.isChecked()) {
						closeLenght.setValue(1);
						model.getSettings().setMinimalSeparation(1);
					} else {
						model.getSettings().setMinimalSeparation(0);
						closeLenght.setValue(0);
					}
				}
				model.updateSettings(null);
			}
			
		};
		
		ChangeListener changeListener = new ChangeListener() {
			public void onChange(Widget sender) {
				if (sender==closeLenght) {
					int len = closeLenght.getIntegerValue();
					model.getSettings().setMinimalSeparation(len);
					if (len==0) {
						model.getSettings().setMinimalSeparation(0);
						closeParagraphControl.setChecked(false); 
					} else {
						model.getSettings().setMinimalSeparation(len);
						closeParagraphControl.setChecked(true); 
					}
				} else if (sender==maxAttempt) {
					int len = closeLenght.getIntegerValue();
					model.getSettings().setMaxAttemptCount(len);
				} else if (sender==fineKeys) {
					model.getSettings().setFineSecretKeys(Integer.parseInt(fineKeys.getValue(fineKeys.getSelectedIndex())));
				}
				model.updateSettings(settingsListener);
			}
		};
		//control how close nearest paragraphs
		closeParagraphControl = new CheckBox(appConstants.quickNearestParagraphControl());
		closeParagraphControl.addClickListener(clickListener);
		add(closeParagraphControl);
		setCellHeight(closeParagraphControl,"1%");
		setCellWidth(closeParagraphControl,"100%");
		
		closeLenght = new NumberTextBox();
		closeLenght.setVisibleLength(2);
		closeLenght.setMaxLength(1);
		closeLenght.setRange(0,9);
		closeLenght.addChangeListener(changeListener);
		CompactHorizontalPanel panel = new CompactHorizontalPanel();
		panel.addCompactWidget(closeLenght);
		panel.addFullText(appConstants.quickNearestParagraphDistance());
		add(panel);
		setCellHeight(panel,"1%");
		setCellWidth(panel,"100%");
		
		//control quantity of attempts
		maxAttempt = new NumberTextBox();
		maxAttempt.setVisibleLength(3);
		maxAttempt.setMaxLength(2);
		maxAttempt.setRange(1,99);
		maxAttempt.addChangeListener(changeListener);
		panel = new CompactHorizontalPanel();
		panel.addCompactWidget(maxAttempt);
		panel.addFullText(appConstants.quickMaxQuantityOfGenerationAttempts());
		add(panel);
		setCellHeight(panel,"1%");
		setCellWidth(panel,"100%");
		
		//fine of secret keys
		panel = new CompactHorizontalPanel();
		fineKeys = new ListBox();
		fineKeys.addChangeListener(changeListener);
		fineKeys.addItem(appConstants.quickSmartSecretKeyAny(), "0");
		fineKeys.addItem(appConstants.quickSmartSecretKey5(), "5");
		fineKeys.addItem(appConstants.quickSmartSecretKey10(), "10");
		fineKeys.setTitle(appConstants.quickSmartSecretKeyTitle());
		panel.addCompactWidget(fineKeys);
		panel.addFullText(appConstants.quickSmartSecretKey());
		add(panel);
		setCellHeight(panel,"1%");
		setCellWidth(panel,"100%");
		
		//filler
		HTML html = new HTML("&nbsp;");
		html.setStyleName("filler");
		add(html);
		setCellHeight(html,"100%");
		setCellWidth(html,"99%");
		//fill data
		applySettings();

		
		settingsListener = new SettingsListener(){
			public void settingsWereUpated() {
				applySettings();
			}
		};
		model.addSettingsListener(settingsListener);
		applySettings();
	}
	
	private void applySettings() {
		com.iambookmaster.client.beans.Settings settings = model.getSettings();
		closeLenght.setValue(settings.getMinimalSeparation());
		if (settings.getMinimalSeparation()==0) {
			closeParagraphControl.setChecked(false);
		} else {
			closeParagraphControl.setChecked(true);
		}
		maxAttempt.setValue(settings.getMaxAttemptCount());
		switch (settings.getFineSecretKeys()) {
		case 5:
			fineKeys.setSelectedIndex(1);
			break;
		case 10:
			fineKeys.setSelectedIndex(2);
			break;
		default:
			fineKeys.setSelectedIndex(0);
		}
	}


	public void close() {
		model.removeSettingsListener(settingsListener);
	}

	
}
