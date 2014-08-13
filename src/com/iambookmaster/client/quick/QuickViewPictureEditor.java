package com.iambookmaster.client.quick;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.beans.Picture;
import com.iambookmaster.client.beans.Sound;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.ContentListener;
import com.iambookmaster.client.model.Model;

public class QuickViewPictureEditor extends VerticalPanel implements QuickViewWidget {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	private static final AppMessages appMessages = AppLocale.getAppMessages();
	
	private Picture object;
	private Model model;
	private ContentListener objectListener;
	private TextBox name;
	private TextBox url;
	private CheckBox noRepeat;
	private CheckBox filler;
	private CheckBox icon;
	private TextBox bigUrl;

	private TextBox urlSize;

	private TextBox bigUrlSize;
	private Image testImage;
	protected boolean testBigImage;
	
	public QuickViewPictureEditor(Model mod, Picture obj) {
		this.model = mod;
		setSize("100%", "100%");
		Label label = new Label(appConstants.quickImageEditTitle());
		add(label);
		setCellHeight(label,"1%");
		setCellWidth(label,"100%");
		Grid grid = new Grid(5,2);
		grid.setSize("100%", "100%");
		ClickHandler clickListener = new ClickHandler() {
			public void onClick(ClickEvent event) {
				updateLocation(event.getSource());
			}
		};
		ChangeHandler changeListener  = new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateLocation(event.getSource());
			}
			
		};
		KeyDownHandler keyboardListener  = new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode()==KeyCodes.KEY_ENTER) {
					updateLocation(event.getSource());
				} else if (event.getNativeKeyCode()==KeyCodes.KEY_ESCAPE) {
					open(object);
				}
			}
		};
		int row=0;
		name = new TextBox();
		name.addChangeHandler(changeListener);
		name.addKeyDownHandler(keyboardListener);
		grid.setWidget(row,0,new Label(appConstants.quickImageEditName()));
		grid.setWidget(row,1,name);
		
		row++;
		url = new TextBox();
		url.addChangeHandler(changeListener);
		url.addKeyDownHandler(keyboardListener);
		grid.setWidget(row,0,new Label(appConstants.quickImageEditURL()));
		grid.setWidget(row,1,url);
		
		row++;
		urlSize = new TextBox();
		urlSize.setReadOnly(true);
		grid.setWidget(row,0,new Label(appConstants.quickImageEditSize()));
		grid.setWidget(row,1,urlSize);
		
		row++;
		bigUrl = new TextBox();
		bigUrl.addChangeHandler(changeListener);
		bigUrl.addKeyDownHandler(keyboardListener);
		grid.setWidget(row,0,new Label(appConstants.quickImageEditBigURL()));
		grid.setWidget(row,1,bigUrl);
		
		row++;
		bigUrlSize = new TextBox();
		bigUrlSize.setReadOnly(true);
		grid.setWidget(row,0,new Label(appConstants.quickImageEditSize()));
		grid.setWidget(row,1,bigUrlSize);
		
		add(grid);
		setCellHeight(grid,"1%");
		setCellWidth(grid,"100%");
		
		noRepeat=  new CheckBox(appConstants.quickImageEditNoRepeat());
		noRepeat.addClickHandler(clickListener);
		add(noRepeat);
		setCellHeight(noRepeat,"1%");
		setCellWidth(noRepeat,"100%");
		
		filler = new CheckBox(appConstants.quickImageEditFiller());
		filler.addClickHandler(clickListener);
		filler.setTitle(appConstants.quickImageEditFillerTitle());
		add(filler);
		setCellHeight(filler,"1%");
		setCellWidth(filler,"100%");
		
		icon = new CheckBox(appConstants.quickImageEditRoleIcon());
		icon.addClickHandler(clickListener);
		icon.setTitle(appConstants.quickImageEditRoleIconTitle());
		add(icon);
		setCellHeight(icon,"1%");
		setCellWidth(icon,"100%");
		
		Button preview = new Button(appConstants.buttonPreview(),new ClickHandler() {
			public void onClick(ClickEvent event) {
				model.previewURL(url.getText().trim());
			}
		});
		add(preview);
		setCellHeight(preview,"1%");
		setCellWidth(preview,"100%");
		
		FlowPanel panel = new FlowPanel();
		panel.setSize("1px", "1px");
		panel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		panel.getElement().getStyle().setOpacity(0);
		testImage = new Image();
		testImage.addLoadHandler(new LoadHandler() {
			public void onLoad(LoadEvent event) {
				if (testBigImage) {
					object.setBigHeight(testImage.getHeight());
					object.setBigWidht(testImage.getWidth());
					bigUrlSize.setValue(appMessages.imageSize(object.getBigWidht(), object.getBigHeight()));
				} else {
					object.setHeight(testImage.getHeight());
					object.setWidht(testImage.getWidth());
					urlSize.setValue(appMessages.imageSize(object.getWidht(), object.getHeight()));
				}
			}
		});
		testImage.addErrorHandler(new ErrorHandler() {
			public void onError(ErrorEvent event) {
				if (testBigImage) {
					bigUrlSize.setValue(appConstants.quickImageEditWrongURL());
				} else {
					urlSize.setValue(appConstants.quickImageEditWrongURL());
				}
			}
		});
		panel.add(testImage);
		add(panel);
		setCellHeight(panel,"99%");
		setCellWidth(panel,"100%");
		
		
		objectListener = new ContentListener(){
			public void refreshAll() {
				open(QuickViewPictureEditor.this.object);
			}
			public void select(Picture object) {
			}
			public void unselect(Picture object) {
			}
			public void update(Picture object) {
				if (object==QuickViewPictureEditor.this.object) {
					open(QuickViewPictureEditor.this.object);
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
			public void update(Sound sound) {
			}
			public void showInfo(Picture picture) {
			}
			public void showInfo(Sound sound) {
			}
		};
		model.addContentListener(objectListener);
		open(obj);
	}

	private void updateLocation(Object sender) {
		if (sender==name) {
			object.setName(name.getText().trim());
		} else if (sender==url) {
			object.setUrl(url.getText().trim());
			testBigImage=false;
			testImage.setUrl(object.getUrl());
		} else if (sender==bigUrl) {
			object.setBigUrl(bigUrl.getText().trim());
			testBigImage=true;
			testImage.setUrl(object.getBigUrl());
		} else if (sender==noRepeat) {
			object.setNoRepeat(noRepeat.getValue());
		} else if (sender==filler) {
			object.setRole(getRoles());
		} else if (sender==icon) {
			object.setRole(getRoles());
		}
		model.updatePicture(object, objectListener);
	}

	private int getRoles() {
		return (filler.getValue() ? Picture.ROLE_FILLER : 0) +
			   (icon.getValue() ? Picture.ROLE_ICON : 0);
	}

	public void open(Picture object) {
		this.object = object;
		name.setText(object.getName());
		url.setText(object.getUrl());
		bigUrl.setText(object.getBigUrl());
		noRepeat.setValue(object.isNoRepeat());
		urlSize.setText(appMessages.imageSize(object.getWidht(),object.getHeight()));
		bigUrlSize.setText(appMessages.imageSize(object.getBigWidht(),object.getBigHeight()));
		filler.setValue((object.getRole() & Picture.ROLE_FILLER) > 0);
		icon.setValue((object.getRole() & Picture.ROLE_ICON) > 0);
	}
	
	public void close() {
		model.removeContentListener(objectListener);
	}

}
