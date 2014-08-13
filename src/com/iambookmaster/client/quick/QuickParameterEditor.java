package com.iambookmaster.client.quick;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.common.SimpleAbstractParameterListBox;
import com.iambookmaster.client.editor.DiceValueWidget;
import com.iambookmaster.client.model.Model;

public class QuickParameterEditor extends QuickAbstractParameterEditor {

	private Parameter parameter;
	private CheckBox vital;
	private CheckBox negative;
	private CheckBox heroOnly;
	private SimpleAbstractParameterListBox<Parameter> limit;
	private DiceValueWidget heroValue;
	private CheckBox hasInitial;
	private CheckBox invisible;
	private CheckBox supressOneValue;
	
	public QuickParameterEditor(Model mod) {
		super(mod);
	}

	@Override
	public String getEditorName() {
		return appConstants.quickParameterTitle();
	}

	@Override
	protected int getGridWidgetsCount() {
		return 7;
	}

	@Override
	public Widget getTail() {
		ClickHandler handler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.getSource()==vital) {
					parameter.setVital(vital.getValue());
				} else if (event.getSource()==negative) {
					parameter.setNegative(negative.getValue());
				} else if (event.getSource()==heroOnly) {
					parameter.setHeroOnly(heroOnly.getValue());
				} else if (event.getSource()==hasInitial) {
					parameter.setHeroHasInitialValue(hasInitial.getValue());
				} else if (event.getSource()==invisible) {
					parameter.setInvisible(invisible.getValue());
				} else if (event.getSource()==supressOneValue) {
					parameter.setSuppressOneValue(supressOneValue.getValue());
				} 
				updateParameter(event.getSource());
				applyControls();
			}
			
		};
		vital = new CheckBox();
		vital.addClickHandler(handler);
		vital.setTitle(appConstants.quickParameterVitalTitle());
		addWidgetToGrid(vital, appConstants.quickParameterVital());
		negative = new CheckBox();
		negative.addClickHandler(handler);
		negative.setTitle(appConstants.quickParameterNegativeTitle());
		addWidgetToGrid(negative, appConstants.quickParameterNegative());
		heroOnly = new CheckBox();
		heroOnly.addClickHandler(handler);
		heroOnly.setTitle(appConstants.quickParameterHeroOnlyTitle());
		addWidgetToGrid(heroOnly, appConstants.quickParameterHeroOnly());
		limit = new SimpleAbstractParameterListBox<Parameter>(Parameter.class,model,true);
		limit.setTitle(appConstants.quickParameterLimitTitle());
		limit.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event) {
				parameter.setLimit(limit.getSelectedParameter());
				updateParameter(limit);
			}
		});
		addWidgetToGrid(limit, appConstants.quickParameterLimit());
		
		HorizontalPanel panel = new HorizontalPanel();
		hasInitial = new CheckBox();
		hasInitial.setTitle(appConstants.quickParameterHeroHasInitialValueTitle());
		hasInitial.addClickHandler(handler);
		panel.add(hasInitial);
		heroValue = new DiceValueWidget(panel);
		heroValue.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event) {
				parameter.setHeroInitialValue(heroValue.getDiceValue());
				updateParameter(heroValue);
			}
		});
		addWidgetToGrid(panel, appConstants.quickParameterInitialHeroValue());

		invisible = new CheckBox();
		invisible.setTitle(appConstants.quickParameterInvisibleFullTitle());
		invisible.addClickHandler(handler);
		addWidgetToGrid(invisible, appConstants.quickParameterInvisibleTitle());
		
		supressOneValue = new CheckBox();
		supressOneValue.setTitle(appConstants.quickParameterSupressOneValueTitle());
		supressOneValue.addClickHandler(handler);
		addWidgetToGrid(supressOneValue, appConstants.quickParameterSupressOneValue());
		return null;
	}

	public void open(AbstractParameter object) {
		super.open(object);
		parameter = (Parameter) object;
		vital.setValue(parameter.isVital());
		negative.setValue(parameter.isNegative());
		heroOnly.setValue(parameter.isHeroOnly());
		limit.setSelectedParameter(parameter.getLimit());
		heroValue.apply(parameter.getHeroInitialValue());
		hasInitial.setValue(parameter.isHeroHasInitialValue());
		invisible.setValue(parameter.isInvisible());
		supressOneValue.setValue(parameter.isSuppressOneValue());
		applyControls();
	}

	private void applyControls() {
	}

}
