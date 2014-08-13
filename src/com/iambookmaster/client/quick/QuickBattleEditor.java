package com.iambookmaster.client.quick;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.editor.ParametersCalculationWidget;
import com.iambookmaster.client.editor.SimpleParameterListBox;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;

public class QuickBattleEditor extends QuickAbstractParameterEditor {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	
	private Battle battle;
	private CheckBox oneTurnBattle;
	private CheckBox attactDefence;
	private CheckBox differenceIsDamage;
	private SimpleParameterListBox vitalResource;
	private ParametersCalculationWidget attack;
	private ParametersCalculationWidget defense;
	private ParametersCalculationWidget damage;
	private ListBox fatal;
	
	public QuickBattleEditor(Model mod) {
		super(mod);
	}

	@Override
	public String getEditorName() {
		return appConstants.quickBattleEditor();
	}

	@Override
	protected int getGridWidgetsCount() {
		return 5;
	}

	@Override
	public Widget getTail() {
		vitalResource = new SimpleParameterListBox(model,true);
		attack = new ParametersCalculationWidget(appConstants.battleAttack(),model,true,false);
		defense = new ParametersCalculationWidget(appConstants.battleDefence(),model,true,false);
		defense.addStyleName(Styles.BORDER);
		damage = new ParametersCalculationWidget(appConstants.battleDamage(),model,true,false);
		
		ClickHandler handler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.getSource()==oneTurnBattle) {
					battle.setOneTurnBattle(oneTurnBattle.getValue());
				} else if (event.getSource()==attactDefence) {
					battle.setAttackDefense(attactDefence.getValue());
				} else if (event.getSource()==differenceIsDamage) {
					battle.setDifferenceIsDamage(differenceIsDamage.getValue());
				}
				updateParameter(event.getSource());
				applyControls();
			}
		};
		ChangeHandler changeHandler = new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				if (event != null) {
					if (event.getSource()==vitalResource) {
						battle.setVital(vitalResource.getSelectedParameter());
					} else if (event.getSource()==fatal) {
						battle.setFatal(fatal.getSelectedIndex());
					} 
				}
				updateParameter(null);
			}
		};
		attack.addChangeHandler(changeHandler);
		defense.addChangeHandler(changeHandler);
		damage.addChangeHandler(changeHandler);
		
		oneTurnBattle = new CheckBox();
		oneTurnBattle.addClickHandler(handler);
		oneTurnBattle.setTitle(appConstants.quickBattleOneTurnTitle());
		addWidgetToGrid(oneTurnBattle, appConstants.quickBattleOneTurn());
		attactDefence = new CheckBox();
		attactDefence.addClickHandler(handler);
		attactDefence.setTitle(appConstants.quickBattleAttacDefenceTitle());
		addWidgetToGrid(attactDefence, appConstants.quickBattleAttacDefence());
		differenceIsDamage = new CheckBox();
		differenceIsDamage.addClickHandler(handler);
		differenceIsDamage.setTitle(appConstants.quickBattleDefferenceIsDamageTitle());
		addWidgetToGrid(differenceIsDamage, appConstants.quickBattleDefferenceIsDamage());
		
		vitalResource.addChangeHandler(changeHandler);
		vitalResource.setTitle(appConstants.quickBattleVitalTitle());
		addWidgetToGrid(vitalResource, appConstants.quickBattleVital());

		fatal = new ListBox();
		fatal.setTitle(appConstants.battleFatalTitle());
		fatal.addItem(appConstants.battleFatalNone(), Battle.FATAL_NONE_STR);
		fatal.addItem(appConstants.battleFatalDead(), Battle.FATAL_DEAD_STR);
		fatal.addItem(appConstants.battleFatalNormal(), Battle.FATAL_NORMAL_STR);
		fatal.addChangeHandler(changeHandler);
		addWidgetToGrid(fatal, appConstants.quickBattleFatal());
		
		VerticalPanel panel = new VerticalPanel();
		panel.setSize("100%", "100%");
		panel.add(attack);
		panel.setCellWidth(attack,"100%");
		panel.add(defense);
		panel.setCellWidth(defense,"100%");
		panel.add(damage);
		panel.setCellWidth(damage,"100%");
		return panel;
	}

	@Override
	public void open(AbstractParameter object) {
		super.open(object);
		battle = (Battle) object;
		if (battle.getVital()==null) {
			battle.setVital(vitalResource.getSelectedParameter());
		} else {
			vitalResource.setSelectedParameter(battle.getVital());
		}
		oneTurnBattle.setValue(battle.isOneTurnBattle());
		attactDefence.setValue(battle.isAttackDefense());
		differenceIsDamage.setValue(battle.isDifferenceIsDamage());
		attack.apply(battle.getAttack());
		defense.apply(battle.getDefense());
		damage.apply(battle.getDamage());
		fatal.setSelectedIndex(battle.getFatal());
		applyControls();
	}

	private void applyControls() {
		if (battle.isOneTurnBattle()) {
			defense.setEnabled(false);
			damage.setEnabled(false);
			fatal.setEnabled(false);
			differenceIsDamage.setEnabled(false);
			attactDefence.setEnabled(false);
		} else {
			defense.setEnabled(battle.isAttackDefense());
			damage.setEnabled(battle.isDifferenceIsDamage()==false);
			fatal.setEnabled(true);
			differenceIsDamage.setEnabled(true);
			attactDefence.setEnabled(true);
		}
	}

}
