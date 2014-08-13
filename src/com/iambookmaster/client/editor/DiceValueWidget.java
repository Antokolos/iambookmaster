package com.iambookmaster.client.editor;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.DiceValue;
import com.iambookmaster.client.common.NumberTextBox;
import com.iambookmaster.client.common.SpanLabel;

public class DiceValueWidget {

	private static final int DEFAULT_VALUE = 3;
	private NumberTextBox constant;
	private NumberTextBox n;
	private ListBox size;
	private Label sign;
	private DiceValue diceValue;
	private ArrayList<ChangeHandler> handlers = new ArrayList<ChangeHandler>();
	
	public DiceValueWidget(ComplexPanel panel) {
		ChangeHandler handler = new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				if (event.getSource()==constant) {
					diceValue.setConstant(constant.getIntegerValue());
				} else if (event.getSource()==n) {
					diceValue.setN(n.getIntegerValue());
				} else if (event.getSource()==size) {
					try {
						diceValue.setSize(Integer.parseInt(size.getValue(size.getSelectedIndex())));
					} catch (NumberFormatException e) {
					}
				}
				updateControls();
				for (ChangeHandler handler : handlers) {
					handler.onChange(null);
				}
			}
		};
		BlurHandler blurHandler = new BlurHandler() {
			public void onBlur(BlurEvent event) {
				if (event.getSource()==constant) {
					diceValue.setConstant(constant.getIntegerValue());
				} else if (event.getSource()==n) {
					diceValue.setN(n.getIntegerValue());
				}
				updateControls();
				for (ChangeHandler handler : handlers) {
					handler.onChange(null);
				}
			}
		};
		constant = new NumberTextBox();
		constant.setStyleName(Styles.SMALL_TEXT);
		constant.setVisibleLength(4);
		constant.setMaxLength(4);
		constant.addBlurHandler(blurHandler);
		constant.setWidth("27px");
		panel.add(constant);
		sign = new SpanLabel("+");
		panel.add(sign);
		n = new NumberTextBox();
		n.setVisibleLength(2);
		n.setMaxLength(2);
		n.addBlurHandler(blurHandler);
		n.setStyleName(Styles.SMALL_TEXT);
		n.setWidth("27px");
		panel.add(n);
		panel.add(new SpanLabel("D"));
		size = new ListBox();
		size.setWidth("40px");
		size.setStyleName(Styles.SMALL_TEXT);
		size.addItem("0");
		size.addItem("2");
		size.addItem("4");
		size.addItem("6");
		size.addItem("8");
		size.addItem("10");
		size.addItem("12");
		size.addItem("14");
		size.addItem("16");
		size.addItem("20");
		size.addItem("24");
		size.addItem("30");
		size.addItem("34");
		size.addItem("50");
		size.addItem("100");
		size.addChangeHandler(handler);
		size.setSelectedIndex(DEFAULT_VALUE);
		panel.add(size);
	}
	
	public void setEnabled(boolean enabled) {
		constant.setEnabled(enabled);
		n.setEnabled(enabled);
		size.setEnabled(enabled);
	}

	public void apply(DiceValue diceValue) {
		if (diceValue==null) {
			diceValue = new DiceValue();
		}
		this.diceValue = diceValue;
		constant.setValue(diceValue.getConstant());
		n.setValue(diceValue.getN());
		size.setSelectedIndex(DEFAULT_VALUE);
		String sz = String.valueOf(diceValue.getSize());
		for (int i = 0; i < size.getItemCount(); i++) {
			if (size.getItemText(i).endsWith(sz)) {
				size.setSelectedIndex(i);
				break;
			}
		}
		updateControls();
		
	}

	private void updateControls() {
		if (diceValue.getN()<0 && diceValue.getSize()>0) {
			sign.setText("");
		} else {
			sign.setText("+");
		}
	}

	public void addChangeHandler(ChangeHandler changeHandler) {
		if (handlers.contains(changeHandler)==false) {
			handlers.add(changeHandler);
		}
	}
	
	public void removeChangeHandler(ChangeHandler changeHandler) {
		handlers.remove(changeHandler);
	}
	
	public DiceValue getDiceValue() {
		return diceValue;
	}

}
