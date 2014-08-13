package com.iambookmaster.client.quick;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Picture;
import com.iambookmaster.client.common.NumberTextBox;
import com.iambookmaster.client.editor.PicturesListBox;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ObjectListener;

public class QuickViewObjectEditor extends VerticalPanel implements QuickViewWidget {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	
	private ObjectBean object;
	private Model model;
	private ObjectListener objectListener;
	private TextBox name;
	private NumberTextBox number;
	private TextArea description;
	private TextArea comments;
	private CheckBox uncoutable;
	private PicturesListBox image;

	public QuickViewObjectEditor(Model mod, ObjectBean obj) {
		this.model = mod;
		setSize("100%", "100%");
		Label label = new Label(appConstants.quickItemTitle());
		add(label);
		setCellHeight(label,"1%");
		setCellWidth(label,"100%");
		Grid grid = new Grid(4,2);
		grid.setSize("100%", "100%");
		ChangeHandler changeListener  = new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateLocation(event.getSource());
			}
			
		};
		ClickHandler clickHandler  = new ClickHandler() {
			public void onClick(ClickEvent event) {
				updateLocation(event.getSource());
			}
			
		};
		KeyPressHandler keyboardListener  = new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode()==KeyCodes.KEY_ENTER) {
					updateLocation(event.getSource());
				} else if (event.getCharCode()==KeyCodes.KEY_ESCAPE) {
					open(object);
				}
			}
		};
		name = new TextBox();
		name.addChangeHandler(changeListener);
		name.addKeyPressHandler(keyboardListener);
		grid.setWidget(0,0,new Label(appConstants.quickItemName()));
		grid.setWidget(0,1,name);
		number = new NumberTextBox();
		number.addChangeHandler(changeListener);
		number.addKeyPressHandler(keyboardListener);
		grid.setWidget(1,0,new Label(appConstants.qucikItemSecretKey()));
		grid.setWidget(1,1,number);
		
		uncoutable = new CheckBox();
		uncoutable.addClickHandler(clickHandler);
		uncoutable.setTitle(appConstants.quickSettingsMultyObjectsTitle());
		grid.setWidget(2,0,new Label(appConstants.quickSettingsMultyObjects()));
		grid.setWidget(2,1,uncoutable);
		
		image = new PicturesListBox(model,Picture.ROLE_ICON);
		image.addChangeHandler(changeListener);
		image.setTitle(appConstants.quickObjectImageTitle());
		grid.setWidget(3,0,new Label(appConstants.quickObjectImage()));
		grid.setWidget(3,1,image);
		
		add(grid);
		setCellHeight(grid,"1%");
		setCellWidth(grid,"100%");
		label = new Label(appConstants.quickItemMasterDescription());
		add(label);
		setCellHeight(label,"1%");
		setCellWidth(label,"100%");
		comments = new TextArea();
		comments.setTitle(appConstants.quickItemPlayerNeverSee());
		comments.setSize("100%","100%");
		comments.addChangeHandler(changeListener);
		add(comments);
		setCellHeight(comments,"50");
		setCellWidth(comments,"100%");
		
		label = new Label(appConstants.quickItemPlayerMissuse());
		add(label);
		setCellHeight(label,"1%");
		setCellWidth(label,"100%");
		description = new TextArea();
		description.addChangeHandler(changeListener);
		description.setTitle(appConstants.qucikItemPlayerMissuseTitle());
		description.setSize("100%","100%");
		add(description);
		setCellHeight(description,"50%");
		setCellWidth(description,"100%");
		
		objectListener = new ObjectListener(){
			public void refreshAll() {
				open(QuickViewObjectEditor.this.object);
			}
			public void select(ObjectBean object) {
			}
			public void unselect(ObjectBean object) {
			}
			public void update(ObjectBean object) {
				if (object==QuickViewObjectEditor.this.object) {
					open(QuickViewObjectEditor.this.object);
				}
			}
			public void addNewObject(ObjectBean object) {
			}
			public void remove(ObjectBean object) {
			}
			public void showInfo(ObjectBean object) {
			}
		};
		model.addObjectsListener(objectListener);
		open(obj);
	}

	private void updateLocation(Object sender) {
		if (sender==name) {
			object.setName(name.getText().trim());
		} else if (sender==number) {
			object.setKey(number.getIntegerValue());
		} else if (sender==comments) {
			object.setMasterComments(comments.getText().trim());
		} else if (sender==description) {
			object.setDescription(description.getText().trim());
		} else if (sender==uncoutable) {
			object.setUncountable(uncoutable.getValue());
		} else if (sender==image) {
			object.setIcon(image.getSelectedPicture());
		}
		model.updateObject(object, objectListener);
	}

	public void open(ObjectBean object) {
		this.object = object;
		name.setText(object.getName());
		number.setValue(object.getKey());
		description.setText(object.getDescription());
		comments.setText(object.getMasterComments());
		uncoutable.setValue(object.isUncountable());
		image.setSelectedPicture(object.getIcon());
	}
	
	public void close() {
		model.removeObjectsListener(objectListener);
	}

}
