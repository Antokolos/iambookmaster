package com.iambookmaster.client.quick;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.SettingsListener;

public class QuickSettingsBookInfoEditor extends VerticalPanel implements QuickViewWidget {
	private static final AppConstants appConstants = AppLocale.getAppConstants();

	private Model model;
	private SettingsListener settingsListener;
	private TextBox title;
	private TextBox authors;
	private TextArea description;

	private Command resizeDescription;
	public QuickSettingsBookInfoEditor(Model mod) {
		this.model = mod;
		setSize("100%", "100%");
		Label label = new Label(appConstants.quickBookInfoTitle());
		add(label);
		setCellHeight(label,"1%");
		setCellWidth(label,"100%");
		Grid grid = new Grid(2,2);
		grid.setSize("100%", "100%");
		ChangeHandler changeListener  = new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateSettings(event.getSource());
			}
			
		};
		KeyPressHandler keyboardListener  = new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode()==KeyCodes.KEY_ENTER) {
					updateSettings(event.getSource());
				} else if (event.getCharCode()==KeyCodes.KEY_ESCAPE) {
					update();
				}
			}
		};
		title = new TextBox();
		title.setWidth("100%");
		title.addChangeHandler(changeListener);
		title.addKeyPressHandler(keyboardListener);
		grid.setWidget(0,0,new Label(appConstants.quickBookTitle()));
		grid.setWidget(0,1,title);
		grid.getCellFormatter().setWidth(0, 0, "1%");
		authors = new TextBox();
		authors.setWidth("100%");
		authors.addChangeHandler(changeListener);
		authors.addKeyPressHandler(keyboardListener);
		grid.getCellFormatter().setWidth(0, 1, "99%");
		grid.setWidget(1,0,new Label(appConstants.quickBookAuthors()));
		grid.setWidget(1,1,authors);
		add(grid);
		setCellHeight(grid,"1%");
		setCellWidth(grid,"100%");
		label = new Label(appConstants.quickBookDescription());
		add(label);
		setCellHeight(label,"1%");
		setCellWidth(label,"100%");
		description = new TextArea();
		description.setSize("100%", "100%");
		description.addChangeHandler(changeListener);
		add(description);
		setCellHeight(description,"99%");
		setCellWidth(description,"100%");
		
		settingsListener = new SettingsListener(){
			public void settingsWereUpated() {
				update();
			}
		};
		model.addSettingsListener(settingsListener);
		resizeDescription = new Command(){
			public void execute() {
				int h = description.getOffsetHeight();
				if (h<40) {
					description.setVisibleLines(7);
				}
			}
		};
		update();
	}
	
	public void update() {
		title.setText(model.getSettings().getBookTitle());
		authors.setText(model.getSettings().getBookAuthors());
		description.setText(model.getSettings().getBookDescription());
		DeferredCommand.addCommand(resizeDescription);
		
	}

	private void updateSettings(Object object) {
		if (object==title) {
			model.getSettings().setBookTitle(title.getText().trim());
		} else if (object==authors) {
			model.getSettings().setBookAuthors(authors.getText().trim());
		} else if (object==description) {
			model.getSettings().setBookDescription(description.getText().trim());
		}
		model.updateSettings(settingsListener);
	}

	public void close() {
		model.removeSettingsListener(settingsListener);
	}

}
