package com.iambookmaster.client.quick;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.beans.Picture;
import com.iambookmaster.client.beans.Sound;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.ContentListener;
import com.iambookmaster.client.model.Model;

public class QuickViewSoundEditor extends VerticalPanel implements QuickViewWidget {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	
	private Sound object;
	private Model model;
	private ContentListener objectListener;
	private TextBox name;
	private TextBox value;
	private Button preview;
	private Button stop;
	public QuickViewSoundEditor(Model mod, Sound obj) {
		this.model = mod;
		setSize("100%", "100%");
		Label label = new Label(appConstants.quickSoundEditTitle());
		add(label);
		setCellHeight(label,"1%");
		setCellWidth(label,"100%");
		Grid grid = new Grid(2,2);
		grid.setSize("100%", "100%");
		ChangeListener changeListener  = new ChangeListener() {
			public void onChange(Widget sender) {
				updateLocation(sender);
			}
			
		};
		KeyboardListener keyboardListener  = new KeyboardListener() {
			public void onKeyDown(Widget sender, char keyCode, int modifiers) {
			}
			public void onKeyPress(Widget sender, char keyCode, int modifiers) {
				if (keyCode==KeyboardListener.KEY_ENTER) {
					updateLocation(sender);
				} else if (keyCode==KeyboardListener.KEY_ESCAPE) {
					open(object);
				}
			}
			public void onKeyUp(Widget sender, char keyCode, int modifiers) {
			}
		};
		name = new TextBox();
		name.addChangeListener(changeListener);
		name.addKeyboardListener(keyboardListener);
		grid.setWidget(0,0,new Label(appConstants.quickSoundEditName()));
		grid.setWidget(0,1,name);
		value = new TextBox();
		value.addChangeListener(changeListener);
		value.addKeyboardListener(keyboardListener);
		grid.setWidget(1,0,new Label(appConstants.quickSoundEditURL()));
		grid.setWidget(1,1,value);
		add(grid);
		setCellHeight(grid,"1%");
		setCellWidth(grid,"100%");
		ClickListener clickListener = new ClickListener() {
			public void onClick(Widget sender) {
				if (sender==preview) {
					model.playSound(value.getText().trim(),false);
				} else if (sender==stop){
					model.stopSound();
				}
			}
		};
		preview = new Button(appConstants.buttonPlay(),clickListener);
		stop = new Button(appConstants.buttonStop(),clickListener);
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(5);
		horizontalPanel.add(preview);
		horizontalPanel.add(stop);
		add(horizontalPanel);
		setCellHeight(horizontalPanel,"1%");
		setCellWidth(horizontalPanel,"100%");
		
		HTML html = new HTML("&nbsp;");
		add(html);
		setCellHeight(html,"99%");
		setCellWidth(html,"100%");
		
		objectListener = new ContentListener(){
			public void refreshAll() {
				open(QuickViewSoundEditor.this.object);
			}
			public void select(Picture object) {
			}
			public void unselect(Picture object) {
			}
			public void update(Sound object) {
				if (object==QuickViewSoundEditor.this.object) {
					open(QuickViewSoundEditor.this.object);
				}
			}
			public void remove(Picture object) {
			}
			public void addNew(Picture picture) {
			}
			public void addNew(Sound sound) {
			}
			public void remove(Sound sound) {
			}
			public void select(Sound sound) {
			}
			public void unselect(Sound sound) {
			}
			public void update(Picture picture) {
			}
			public void showInfo(Picture picture) {
			}
			public void showInfo(Sound sound) {
			}
		};
		model.addContentListener(objectListener);
		open(obj);
	}

	private void updateLocation(Widget sender) {
		if (sender==name) {
			object.setName(name.getText().trim());
		} else if (sender==value) {
			object.setUrl(value.getText().trim());
		}
		model.updateSound(object, objectListener);
	}

	public void open(Sound object) {
		this.object = object;
		name.setText(object.getName());
		value.setText(object.getUrl());
	}
	
	public void close() {
		model.removeContentListener(objectListener);
	}

}
