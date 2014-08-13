package com.iambookmaster.client.player;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RadioButton;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;

public class FeedbackLevel extends Grid {
	private static AppConstants appConstants = AppLocale.getAppConstants();
	private static int COUNTER;
	private int value;
	public FeedbackLevel() {
		super(2,5);
		setCellSpacing(0);
		setCellPadding(0);
		String id = "FeedbackLevel"+String.valueOf(++COUNTER);
		addItem(-2,PlayImages.FACE_BAD,appConstants.FeedBackBad(),0,id);
		addItem(-1,PlayImages.FACE_SAD,appConstants.FeedBackSad(),1,id);
		addItem(0,PlayImages.FACE_NORMAL,appConstants.FeedBackNormal(),2,id);
		addItem(1,PlayImages.FACE_GOOD,appConstants.FeedBackGood(),3,id);
		addItem(2,PlayImages.FACE_BEST,appConstants.FeedBackBest(),4,id);
	}
	
	private void addItem(final int val, String imageSrc, String title, int col,String id) {
		final RadioButton button = new RadioButton(id);
		if (col==2) {
			button.setValue(true);
		}
		button.setTitle(title);
		ClickHandler handler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				value = val;
				button.setValue(true);
				if (changeHandler != null) {
					changeHandler.onChange(null);
				}
			}
		};
		button.addClickHandler(handler);
		Image image = new Image(imageSrc);
		image.setStyleName(PlayerStyles.CLICKABLE);
		image.setTitle(title);
		image.addClickHandler(handler);
		setWidget(0, col, image);
		getCellFormatter().setHorizontalAlignment(0, col, HasHorizontalAlignment.ALIGN_CENTER);
		setWidget(1, col, button);
		getCellFormatter().setHorizontalAlignment(1, col, HasHorizontalAlignment.ALIGN_CENTER);
	}

	public int getValue() {
		return value;
	}

	private ChangeHandler changeHandler;
	public void addChangeHandler(ChangeHandler changeHandler) {
		if (this.changeHandler==null) {
			this.changeHandler = changeHandler;
		} else {
			throw new IllegalArgumentException();
		}
	}
	public void removeChangeHandler(ChangeHandler changeHandler) {
		if (this.changeHandler==null) {
			throw new IllegalArgumentException();
		} else {
			this.changeHandler = null;
		}
	}
	
}
