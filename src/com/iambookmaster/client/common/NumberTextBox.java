package com.iambookmaster.client.common;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ChangeListenerCollection;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class NumberTextBox extends TextBox {

	private int min = Integer.MIN_VALUE;
	private int max = Integer.MAX_VALUE;
	private ChangeListenerCollection changeListenerCollection;
	public NumberTextBox() {
		super.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				getIntegerValue();
				if (changeListenerCollection != null) {
					changeListenerCollection.fireChange(NumberTextBox.this);
				}
			}
		});
	}
	
	@Override
	public void addChangeListener(ChangeListener listener) {
		if (changeListenerCollection==null) {
			changeListenerCollection = new ChangeListenerCollection();
		}
		changeListenerCollection.add(listener);
	}

	@Override
	public void removeChangeListener(ChangeListener listener) {
		if (changeListenerCollection!=null) {
			changeListenerCollection.remove(listener);
		}
	}

	public void setRange(int min, int max) {
		this.min = min;
		this.max = max;
	}

	public int getIntegerValue() {
		int val;
		try {
			val = Integer.parseInt(getText());
		} catch (NumberFormatException e) {
			setText("0");
			val = 0;
		}
		if (val<min) {
			val = min;
			setText(String.valueOf(min));
		}
		if (val>max) {
			val = max;
			setText(String.valueOf(max));
		}
		return val;
	}

	public void setValue(int value) {
		setText(String.valueOf(value));
		
	}

}
