package com.iambookmaster.client.quick;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.common.NumberTextBox;
import com.iambookmaster.client.common.SimpleAbstractParameterListBox;
import com.iambookmaster.client.editor.DiceValueWidget;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;

public class QuickAlchemyEditor extends QuickAbstractParameterEditor {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	
	private Alchemy alchemy;
	private ListBox battle;
	private SimpleAbstractParameterListBox<Parameter> from;
	private SimpleAbstractParameterListBox<Parameter> to;
	private NumberTextBox fromValue;
	private DiceValueWidget toValue;
	private CheckBox onDemand;
	private CheckBox weapon;
	private SimpleAbstractParameterListBox<Parameter> battleLimit;
	private CheckBox oneTimePerRound;
	private CheckBox overflowControl;
	
	public QuickAlchemyEditor(Model mod) {
		super(mod);
	}

	@Override
	public String getEditorName() {
		return appConstants.quickAlchemyTitle();
	}

	@Override
	protected int getGridWidgetsCount() {
		return 10;
	}

	@Override
	public Widget getTail() {
		ChangeHandler changeHandler = new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				if (event.getSource()==from) {
					alchemy.setFrom(from.getSelectedParameter());
				} else if (event.getSource()==to) {
					alchemy.setTo(to.getSelectedParameter());
				} else if (event.getSource()==toValue) {
					alchemy.setToValue(toValue.getDiceValue());
				} else if (event.getSource()==fromValue) {
					alchemy.setFromValue(fromValue.getIntegerValue());
				} else if (event.getSource()==battleLimit) {
					alchemy.setBattleLimit(battleLimit.getSelectedParameter());
				}
				updateParameter(event.getSource());
			}
		};
		ClickHandler clickHandler = new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (event.getSource()==weapon) {
					alchemy.setWeapon(weapon.getValue());
				} else if (event.getSource()==onDemand) {
					alchemy.setOnDemand(onDemand.getValue());
				} else if (event.getSource()==battle) {
					alchemy.setPlace(battle.getSelectedIndex());
				} else if (event.getSource()==oneTimePerRound) {
					alchemy.setOneTimePerRound(oneTimePerRound.getValue());
				} else if (event.getSource()==overflowControl) {
					alchemy.setOverflowControl(overflowControl.getValue());
				}
				
				updateParameter(event.getSource());
			}
		};
		//from
		from = new SimpleAbstractParameterListBox<Parameter>(Parameter.class,model,false);
		from.addChangeHandler(changeHandler);
		from.setTitle(appConstants.alchemyFromTitle());
		addWidgetToGrid(from, appConstants.quickAlchemyFrom());
		//from Value
		fromValue = new NumberTextBox();
		fromValue.addChangeHandler(changeHandler);
		fromValue.setMaxLength(2);
		fromValue.setVisibleLength(3);
		fromValue.setRange(0,99);
		addWidgetToGrid(fromValue, appConstants.quickAlchemyFromValue());
		//to
		to = new SimpleAbstractParameterListBox<Parameter>(Parameter.class,model,false);
		to.addChangeHandler(changeHandler);
		to.setTitle(appConstants.alchemyToTitle());
		addWidgetToGrid(to, appConstants.quickAlchemyTo());
		//to Value
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		toValue = new DiceValueWidget(horizontalPanel);
		addWidgetToGrid(horizontalPanel, appConstants.quickAlchemyToValue());
		
		//limit control
		overflowControl = new CheckBox();
		overflowControl.addClickHandler(clickHandler);
		if (model.getSettings().isOverflowControl()) {
			overflowControl.setTitle(appConstants.quickAlchemyNoOverflowControlTitle());
			addWidgetToGrid(overflowControl, appConstants.calculationNoOverflowControl());
		} else {
			overflowControl.setTitle(appConstants.quickAlchemyOverflowControlTitle());
			addWidgetToGrid(overflowControl, appConstants.calculationOverflowControl());
		}
		
		//battle
		battle = new ListBox();
		battle.addItem(appConstants.quickAlchemyPeace(),String.valueOf(Alchemy.PLACE_PEACE));
		battle.addItem(appConstants.quickAlchemyBattle(),String.valueOf(Alchemy.PLACE_BATTLE));
		battle.addItem(appConstants.quickAlchemyBoth(),String.valueOf(Alchemy.PLACE_BOTH));
		battle.addClickHandler(clickHandler);
		battle.setTitle(appConstants.quickAlchemyBattleTitle());
		addWidgetToGrid(battle, appConstants.quickAlchemyPlace());
		//on-demand
		onDemand = new CheckBox();
		onDemand.addClickHandler(clickHandler);
		onDemand.setTitle(appConstants.quickAlchemyOnDemandTitle());
		addWidgetToGrid(onDemand, appConstants.quickAlchemyOnDemand());
		//on-demand
		weapon = new CheckBox();
		weapon.addClickHandler(clickHandler);
		weapon.setTitle(appConstants.quickAlchemyWeapondTitle());
		addWidgetToGrid(weapon, appConstants.quickAlchemyWeapond());
		//limit in battle
		battleLimit = new SimpleAbstractParameterListBox<Parameter>(Parameter.class,model,true);
		battleLimit.addChangeHandler(changeHandler);
		battleLimit.setTitle(appConstants.alchemyBattleLimitTitle());
		addWidgetToGrid(battleLimit, appConstants.alchemyBattleLimit());
		//one time per round
		oneTimePerRound = new CheckBox();
		oneTimePerRound.addClickHandler(clickHandler);
		oneTimePerRound.setTitle(appConstants.quickAlchemyOneTimePerRoundTitle());
		addWidgetToGrid(oneTimePerRound, appConstants.quickAlchemyOneTimePerRound());
		
		return null;
	}

	public void open(AbstractParameter object) {
		super.open(object);
		alchemy = (Alchemy) object;
		battle.setSelectedIndex(alchemy.getPlace());
		if (alchemy.getFrom()==null) {
			alchemy.setFrom(from.getSelectedParameter());
		} else {
			from.setSelectedParameter(alchemy.getFrom());
		}
		if (alchemy.getTo()==null) {
			alchemy.setTo(to.getSelectedParameter());
		} else {
			to.setSelectedParameter(alchemy.getTo());
		}
		fromValue.setValue(alchemy.getFromValue());
		toValue.apply(alchemy.getToValue());
		onDemand.setValue(alchemy.isOnDemand());
		weapon.setValue(alchemy.isWeapon());
		oneTimePerRound.setValue(alchemy.isOneTimePerRound());
		battleLimit.setSelectedParameter(alchemy.getBattleLimit());
		overflowControl.setValue(alchemy.isOverflowControl());
	}

}
