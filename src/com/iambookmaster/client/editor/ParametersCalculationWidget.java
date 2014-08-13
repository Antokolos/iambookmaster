package com.iambookmaster.client.editor;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.Images;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.beans.ParametersCalculation;
import com.iambookmaster.client.common.SpanLabel;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;

public class ParametersCalculationWidget extends VerticalPanel {

	private AppConstants appConstants = AppLocale.getAppConstants();
	
	private Model model;
	private String name;
	private DiceValueWidget constant;
	private SimpleParameterListBox parameterSelection;
	private ParametersCalculation calculation;
	private Image addParameter;
	private ArrayList<ChangeHandler> handlers = new ArrayList<ChangeHandler>();
	private FlowPanel flowPanel;
	private FlowPanel addPanel;
	private CheckBox overflowControl;

	private Image minusParameter;
	public ParametersCalculationWidget(String title, Model mod,boolean npcOnly,boolean overflowControlControl) {
		setSize("100%", "100%");
		flowPanel = new FlowPanel();
		add(flowPanel);
		setCellHeight(flowPanel, "99%");
		addPanel = new FlowPanel();
		add(addPanel);
		model = mod;
		name = title;
		//tail
		ClickHandler handler = new ClickHandler(){
			public void onClick(ClickEvent event) {
				if (parameterSelection.isEnabled()==false) {
					return;
				}
				if (event.getSource()==overflowControl) {
					calculation.setOverflowControl(overflowControl.getValue());
					return;
				}
				Parameter parameter = parameterSelection.getSelectedParameter();
				if (parameter==null) {
					return;
				}
				if (calculation.getParameters().containsKey(parameter)) {
					Window.alert(appConstants.battleParameterAlreadyAdded());
					return;
				}
				if (event.getSource()==addParameter) {
					calculation.getParameters().put(parameter, 1);
				} else if (event.getSource()==minusParameter) { 
					calculation.getParameters().put(parameter, -1);
				}
				addParameterToPanel(parameter);
				for (ChangeHandler handler : handlers) {
					handler.onChange(null);
				}
			} 
		};
		if (overflowControlControl) { 
			overflowControl = new CheckBox();
			overflowControl.addClickHandler(handler);
			if (model.getSettings().isOverflowControl()) {
				overflowControl.setTitle(appConstants.calculationNoOverflowControl());
			} else {
				overflowControl.setTitle(appConstants.calculationOverflowControl());
			}
			addPanel.add(overflowControl);
		}
		
		addParameter = new Image(Images.ADD_CONNECTION);
		addParameter.setStyleName(Styles.CLICKABLE);
		addParameter.addClickHandler(handler);
		addParameter.setTitle(appConstants.battlePlusParameter());
		addPanel.add(addParameter);
		minusParameter = new Image(Images.MINUS);
		minusParameter.setStyleName(Styles.CLICKABLE);
		minusParameter.addClickHandler(handler);
		minusParameter.setTitle(appConstants.battleMinusParameter());
		addPanel.add(minusParameter);
		parameterSelection = new SimpleParameterListBox(model,npcOnly);
		addPanel.add(parameterSelection);
	}

	public void apply(ParametersCalculation calc) {
		flowPanel.clear();
		this.calculation = calc;
		SpanLabel label = new SpanLabel(name+" = ");
		label.setStyleName(Styles.BOLD);
		flowPanel.add(label);
		constant = new DiceValueWidget(flowPanel);
		constant.apply(calculation.getConstant());
		constant.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event) {
				for (ChangeHandler handler : handlers) {
					handler.onChange(null);
				}
			}
		});
		if (overflowControl != null) {
			overflowControl.setValue(calc.isOverflowControl());
		}
//		add(constant);
		//parameters
		for (Parameter parameter : calculation.getParameters().keySet()) {
			addParameterToPanel(parameter);
		}
	}

	private void addParameterToPanel(final Parameter parameter) {
		Integer value = calculation.getParameters().get(parameter);
		ParameterWidget parameterWidget = new ParameterWidget(parameter,value);
		parameterWidget.addTo(flowPanel);
	}

	private void removeParamer(Parameter parameter, ParameterWidget remove) {
		remove.removeFrom(flowPanel);
		calculation.getParameters().remove(parameter);
		for (ChangeHandler handler : handlers) {
			handler.onChange(null);
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

	public void setEnabled(boolean enabled) {
		constant.setEnabled(enabled);
		parameterSelection.setEnabled(enabled);
		for (int i = 0; i < getWidgetCount(); i++) {
			Widget widget = getWidget(i);
			if (widget instanceof Image) {
				Image image = (Image) widget;
				if (enabled) {
					image.addStyleName(Styles.CLICKABLE);
				} else {
					image.removeStyleName(Styles.CLICKABLE);
				}
			}
		}
		
	}
	
	public class ParameterWidget extends SpanLabel implements ClickHandler{
//	,ChangeHandler{
		private Parameter parameter;
		private SpanLabel name;
//		private NumberTextBox value;
		private Image remove;
		
		public void onClick(ClickEvent event) {
			if (parameterSelection.isEnabled()==false) {
				return;
			}
			removeParamer(parameter,this);
		}
		
		public void addTo(FlowPanel flowPanel) {
			flowPanel.add(this);
//			flowPanel.add(value);
			flowPanel.add(name);
			flowPanel.add(remove);
		}

		public void removeFrom(FlowPanel flowPanel) {
			flowPanel.remove(this);
//			flowPanel.remove(value);
			flowPanel.remove(name);
			flowPanel.remove(remove);
		}

		public ParameterWidget(Parameter parameter, Integer val) {
			remove = new Image(Images.CLOSE_PANEL);
			remove.setTitle(appConstants.calculationRemoveParameter());
			remove.setStyleName(Styles.CLICKABLE);
			remove.addStyleName(Styles.ALIGN_TOP);
			remove.addClickHandler(this);
			name = new SpanLabel();
//			value = new NumberTextBox();
//			value.setStyleName(Styles.SMALL_TEXT);
//			value.setMaxLength(2);
//			value.setWidth("27px");
//			value.setVisibleLength(2);
//			value.addChangeHandler(this);
//			value.setTitle(appConstants.calculationValueParameter());
			apply(parameter);
			apply(val);
			
		}
		private void apply(Integer val) {
			setText(val>0 ? "+":"-");
//			value.setValue(val);
		}
		private void apply(Parameter parameter) {
			this.parameter = parameter;
			name.setText(parameter.getName());
		}

//		public void onChange(ChangeEvent event) {
//			Integer val = value.getIntegerValue();
//			calculation.getParameters().put(parameter, val);
//			apply(val);
//		}
		
	}

}
