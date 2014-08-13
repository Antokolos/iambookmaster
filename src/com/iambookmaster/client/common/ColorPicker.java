package com.iambookmaster.client.common;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ListBox;
import com.iambookmaster.client.locale.AppLocale;

public class ColorPicker extends ListBox {

	public ColorPicker() {
		addItem(AppLocale.getAppConstants().colorDefault());
		for (int i = 1; i < ColorProvider.colors.length; i++) {
			addItem(" ");
		}
		addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event) {
				ColorPicker.this.setFocus(false);
			}
		});
	}
	
	public ColorPicker(ChangeHandler changeListener) {
		this();
		addChangeHandler(changeListener);
	}
	
	public void insertItem(String item, String value, int index) {
		super.insertItem(item, value, index);
		if (index<0) {
			index = getItemCount()-1;
		}
		if (index>0 && index<ColorProvider.colors.length) {
			DOM.setStyleAttribute(DOM.getChild(getElement(), index), "backgroundColor", ColorProvider.colors[index]);
		}
	}
}
