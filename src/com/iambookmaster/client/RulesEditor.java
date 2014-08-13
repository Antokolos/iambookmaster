package com.iambookmaster.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.common.EditorTab;
import com.iambookmaster.client.common.SpanLabel;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.PlotListener;

public class RulesEditor extends VerticalPanel implements EditorTab,ClickHandler{

	private AppConstants appConstants = AppLocale.getAppConstants();
	private AppMessages appMessages = AppLocale.getAppMessages();
	
	private TextArea bookRules;
	private TextArea playerRules;
	private Model model;
	private PlotListener plotListener;
	private Image generateBook;
	public RulesEditor(Model mod) {
		this.model = mod;
		setSize("100%", "100%");
		FlowPanel panel = new FlowPanel();
		panel.setWidth("100%");
		generateBook = new Image(Images.REGENERATE_TEXT);
		generateBook.setStyleName(Styles.CLICKABLE);
		generateBook.addClickHandler(this);
		generateBook.setTitle(appConstants.rulesRecreateButtonTitle());
		panel.add(generateBook);
		panel.add(new SpanLabel(appConstants.rulesBookRules(),false));
		add(panel);
		setCellHeight(panel,"1%");
		setCellWidth(panel,"100%");
		
		ChangeHandler handler = new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				if (event.getSource()==bookRules) {
					model.updateBookRules(bookRules.getText().trim(), plotListener);
				} else if (event.getSource()==playerRules) {
					model.updatePlayerRules(playerRules.getText().trim(), plotListener);
				}
			}
		};
		
		bookRules = new TextArea();
		bookRules.setSize("100%", "100%");
		bookRules.setText(model.getBookRules());
		bookRules.addChangeHandler(handler);
		add(bookRules);
		setCellHeight(bookRules,"70%");
		setCellWidth(bookRules,"100%");
		
		panel = new FlowPanel();
		panel.setWidth("100%");
		panel.add(new SpanLabel(appConstants.rulesPlayerRules(),false));
		add(panel);
		setCellHeight(panel,"1%");
		setCellWidth(panel,"100%");
		
		playerRules = new TextArea();
		playerRules.setSize("100%", "100%");
		playerRules.setText(model.getPlayerRules());
		playerRules.addChangeHandler(handler);
		add(playerRules);
		setCellHeight(playerRules,"30%");
		setCellWidth(playerRules,"100%");
		
		plotListener = new PlotListener() {
			public void refreshAll() {
				bookRules.setText(model.getBookRules());
				playerRules.setText(model.getPlayerRules());
			}
			public void update(String pl) {
			}
			public void updateBookRules(String rules) {
				bookRules.setText(rules);
			}
			public void updatePlayerRules(String rules) {
				playerRules.setText(rules);
			}
			public void updateCommercialText(String text) {
			}
			public void updateDemoInfoText(String text) {
			}
		};
		model.addPlotListener(plotListener);
	}
	
	public void activate() {
	}
	public void deactivate() {
	}
	public void close() {
		model.removePlotListener(plotListener);
	}

	public void onClick(ClickEvent event) {
		if (event.getSource()==generateBook) {
			if (bookRules.getText().trim().length()==0 || Window.confirm(appConstants.rulesConfirmRecreate())) {
				//recreate
				String rules = recreatePaperRules();
				model.updateBookRules(rules, null);
			}
		}
	}

	private String recreatePaperRules() {
		StringBuilder builder = new StringBuilder(appConstants.rulesTemplateIntro());
		builder.append('\n');
		boolean hasModificators=false;
		boolean hasAbsoluteModificators=false;
		ArrayList<AbstractParameter> list = model.getParameters();
		ArrayList<Battle> battles = new ArrayList<Battle>();
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		HashSet<Parameter> limits = new HashSet<Parameter>();
		HashMap<Parameter,ArrayList<Alchemy>> alchemies = new HashMap<Parameter, ArrayList<Alchemy>>();
		for (AbstractParameter parameter : list) {
			if (parameter instanceof Modificator) {
				Modificator modificator = (Modificator) parameter;
				if (modificator.isAbsolute()) {
					hasAbsoluteModificators = true;
				} else {
					hasModificators = true; 
				}
			} else if (parameter instanceof Battle) {
				battles.add((Battle) parameter);
			} else if (parameter instanceof Parameter) {
				Parameter param = (Parameter) parameter;
				parameters.add(param);
				if (param.getLimit() != null) {
					limits.add(param.getLimit());
				}
			} else if (parameter instanceof Alchemy) {
				Alchemy alchemy = (Alchemy)parameter;
				if (alchemy.getFromValue() > 0) {
					ArrayList<Alchemy> listAl = alchemies.get(alchemy.getFrom());
					if (listAl == null) {
						listAl = new ArrayList<Alchemy>();
						alchemies.put(alchemy.getFrom(), listAl);
					}
					listAl.add(alchemy);
				}
			}
			
		}

		HashSet<Parameter> important = new HashSet<Parameter>();
		for (Parameter parameter : parameters) {
			if (limits.contains(parameter)) {
				//limits is only for Player
				continue;
			}
			if (parameter.isHeroHasInitialValue() || !parameter.isHeroOnly()) {
				//has initial value or NPC can have it
				important.add(parameter);
				continue;
			}
			for (AbstractParameter param : list) {
				if (param instanceof Battle) {
					if (param.dependsOn(parameter)) {
						important.add(parameter);
						break;
					}
//				} else if (param instanceof Alchemy) {
//					Alchemy alchemy = (Alchemy)param;
//					if (alchemy.isOnDemand()==false && alchemy.dependsOn(parameter)) {
//						important.add(parameter);
//						break;
//					}
				}
			}
		}
		
		Paragraph paragraph = model.getStartParagraph();
		if (paragraph != null && paragraph.getChangeParameters() != null) {
			for (Parameter parameter : paragraph.getChangeParameters().keySet()) {
				important.add(parameter);
			}
		}
		
		if (important.size()>0) {
			builder.append('\n');
			builder.append(appConstants.rulesTemplateHeroHasParameters());
			builder.append('\n');
			//describe important parameters
			for (Parameter parameter : important) {
				if (parameter.getLimit() == null) {
					builder.append(appMessages.rulesTemplateParameter(parameter.getName(),parameter.getDescription()));
				} else if (important.contains(parameter.getLimit())){
					builder.append(appMessages.rulesTemplateParameterLimitAfter(parameter.getName(),parameter.getDescription(),parameter.getLimit().getName()));
				} else {
					builder.append(appMessages.rulesTemplateParameterHasLimit(parameter.getName(),parameter.getDescription()));
				}
				if (parameter.isVital()) {
					if (parameter.isHeroOnly()) {
						builder.append(appConstants.rulesTemplateParameterVitalHero());
					} else {
						builder.append(appConstants.rulesTemplateParameterVital());
					}
				} else if (parameter.isNegative()) {
					builder.append(appConstants.rulesTemplateParameterNegative());
				} else {
					builder.append(appConstants.rulesTemplateParameterPositive());
				}
				if (alchemies.containsKey(parameter)) {
					ArrayList<Alchemy> listAl = alchemies.get(parameter);
					for (Alchemy alchemy : listAl) {
						if (alchemy.isOnDemand()==false) {
							//let know about this alchemy
							String from = String.valueOf(alchemy.getFromValue());
							String to = alchemy.getToValue().toString();
							switch (alchemy.getPlace()) {
							case Alchemy.PLACE_BATTLE:
								if (alchemy.isWeapon()) {
									builder.append(appMessages.rulesTemplateAlchemyWeapon(alchemy.getTo().getName(),from,to));
								} else {
									builder.append(appMessages.rulesTemplateAlchemyBattle(alchemy.getTo().getName(),from,to));
								}
								if (alchemy.isOneTimePerRound()) {
									builder.append(appConstants.rulesTemplateAlchemyOneTimePerRound());
								}
								break;
							case Alchemy.PLACE_PEACE:
								builder.append(appMessages.rulesTemplateAlchemyPease(alchemy.getTo().getName(),from,to));
								break;
							case Alchemy.PLACE_BOTH:
								builder.append(appMessages.rulesTemplateAlchemyBoth(alchemy.getTo().getName(),from,to));
								if (alchemy.isOneTimePerRound()) {
									builder.append(appConstants.rulesTemplateAlchemyOneTimePerRound());
								}
								break;
							}
						}
					}
					
				}
				builder.append('\n');
				builder.append('\n');
			}
			builder.append('\n');
		}
		//objects
		if (model.getObjects().size()>0) {
			if (model.getSettings().isHiddenUsingObjects()) {
				builder.append(appConstants.rulesTemplateHiddenObjects());
				builder.append('\n');
			} else {
				builder.append(appConstants.rulesTemplateObjects());
				builder.append('\n');
			}
		}
		
		//battles
		if (battles.size()>0) {
			builder.append('\n');
			if (battles.size()>1) { 
				builder.append(appConstants.rulesTemplateBattles());
				builder.append('\n');
			}
			for (Battle battle : battles) {
				String attack = battle.getAttack().toString(appMessages);
				if (battle.isOneTurnBattle()) {
					builder.append(appMessages.rulesTemplateBattleOneTurn(battle.getName(),attack));
					builder.append('\n');
				} else {
					if (battle.isAttackDefense()){
						String defense = battle.getDefense().toString(appMessages);
						builder.append(appMessages.rulesTemplateBattleAttackDefense(battle.getName(),attack,defense));
						builder.append('\n');
					} else {
						builder.append(appMessages.rulesTemplateBattleAttack(battle.getName(),attack));
						builder.append('\n');
					}
					if (battle.isDifferenceIsDamage()) {
						if (battle.isAttackDefense()) {
							builder.append(appMessages.rulesTemplateBattleDifferenceIsDamageAD(battle.getVital().getName()));
						} else {
							builder.append(appMessages.rulesTemplateBattleDifferenceIsDamage(battle.getVital().getName()));
						}
					} else if (battle.getDamage().getParameters().size()==0){
						builder.append(appMessages.rulesTemplateBattleDamage(battle.getVital().getName(),battle.getDamage().toString(appMessages)));
					} else {
						builder.append(appMessages.rulesTemplateBattleDamagePar(battle.getVital().getName(),battle.getDamage().toString(appMessages)));
					}
					if (battle.getAttack().getConstant().getN() != 0 && battle.getAttack().getConstant().getSize() >0) {
						int n = Math.abs(battle.getAttack().getConstant().getN())*battle.getAttack().getConstant().getSize();
						switch (battle.getFatal()) {
						case Battle.FATAL_DEAD:
							builder.append(appMessages.rulesTemplateBattleFatalKill(n));
							builder.append('\n');
							break;
						case Battle.FATAL_NORMAL:
							builder.append(appMessages.rulesTemplateBattleFatalDamage(n,battle.getVital().getName()));
							builder.append('\n');
							break;
						}
					}
					builder.append(appMessages.rulesTemplateBattleEnd(battle.getVital().getName()));
					builder.append('\n');
				}
				builder.append('\n');
			}
		}
		
		//modificators
		if (hasAbsoluteModificators) {
			//absolute only
			builder.append(appConstants.rulesTemplateAbsoluteModificators());
			builder.append('\n');
		}
		if (hasModificators){
			//usual only
			builder.append(appConstants.rulesTemplateNormalModificators());
			builder.append('\n');
		}
		
		return builder.toString();
	}
}
