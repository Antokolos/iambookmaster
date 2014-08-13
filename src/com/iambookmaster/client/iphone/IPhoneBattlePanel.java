package com.iambookmaster.client.iphone;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.iphone.common.ClickHandlerPair;
import com.iambookmaster.client.iphone.common.IPhoneButton;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.player.BattleListener;
import com.iambookmaster.client.player.BattleListenerAdapter;
import com.iambookmaster.client.player.PlayerState;
import com.iambookmaster.client.player.PlayerState.FighterData;

public class IPhoneBattlePanel {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	private static final AppMessages appMessages = AppLocale.getAppMessages();
	
	private static IPhoneStyles css = IPhoneImages.INSTANCE.css();
	
	private PlayerState playerState;
	private BattleListener playerListener;
	private HeroWidget heroWidget;
//	private FighterData target;
//	private ArrayList<NPCWidget> npcs;
	private ClickHandler atackHandler;
	private ArrayList<ClickHandlerPair> handlers;
	private IPhoneBattlePanelListner listener;
	private ArrayList<String> messages;
	public IPhoneBattlePanel(PlayerState ps, IPhoneBattlePanelListner lst) {
		this.listener = lst;
		this.playerState = ps;
		messages = new ArrayList<String>();
		playerListener = new BattleListenerAdapter(appConstants,appMessages,playerState) {
			@Override
			protected void addMessage(String message) {
				if (playerState.getModel().getSettings().isShowBattleConsole()) {
					messages.add(message);
				}
			}
		};
		
		atackHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				 attack();
			}
		};
		
//		target = selectTarget();
		handlers = new ArrayList<ClickHandlerPair>();
	}
	
	public Widget createBattleWidget(boolean active) {
		FlowPanel canvas = new FlowPanel();
		handlers.clear();
		Label label = new Label(playerState.getCurrentBattle().getName());
		label.setStyleName(css.battleTitle());
		canvas.add(label);
		if (active && playerState.getFighters().size()>1) {
			label = new Label(appConstants.iphoneButtleInstructions());
			canvas.add(label);
		}
		
		//add Hero
		heroWidget = new HeroWidget();
		canvas.add(heroWidget);
		
		//add enemies
		for (FighterData npc : playerState.getFighters()) {
			NPCWidget widget = new NPCWidget(npc,active);
			canvas.add(widget);
			handlers.add(new ClickHandlerPair(widget, widget));
		}
		
		if (active) {
			if (playerState.isFinished()==false) {
				IPhoneButton button = new IPhoneButton(appConstants.playerButtonAttack(),atackHandler);
//				if (listener.isVertical() == false) {
//					label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
//				}
//				label.setStyleName(css.stateSelection());
//				label.addClickHandler(atackHandler);
				canvas.add(button);
				handlers.add(new ClickHandlerPair(atackHandler,button));
			
	//		if (battle.isOneTurnBattle()==false) {
	//			auto = new Button(appConstants.playerButtonAutoBattle(),mainClickHandler);
	//			auto.setTitle(appConstants.playerButtonAutoBattleTitle());
	//			canvas.add(auto);
	//			canvas.addClickHandler(auto, mainClickHandler);
	//		}
			
				//all applicable alchemy
				ArrayList<Alchemy> list = playerState.getAlchemy(true,true);
				
				if (list != null) {
					for (Alchemy alchemy : list) {
						AlchemyLink link = new AlchemyLink(alchemy);
						canvas.add(link);
						handlers.add(new ClickHandlerPair(link, link));
						if (alchemy.getBattleLimit() == null) {
							if (alchemy.getFromValue()>0) {
								heroWidget.addParameter(alchemy.getFrom(), playerState.getParameters().get(alchemy.getFrom()),playerState.getParameters());
							}
						} else {
							heroWidget.addParameter(alchemy.getBattleLimit(), playerState.getHeroBatleLimits().get(alchemy.getBattleLimit()),playerState.getParameters());
						}
					}
				}
			}
		}
		for (String message : messages) {
			label = new HTML(message);
			label.setStyleName(css.battleMessage());
			canvas.add(label);
		}
		return canvas;
	}

	public void addListeners(IPhoneCanvas canvas) {
		for (ClickHandlerPair pair : handlers) {
			canvas.addClickHandler(pair.getWidget(), pair.getHandler());
		}
	}


	public class AlchemyLink extends IPhoneButton implements ClickHandler{
		private Alchemy alchemy;
		public AlchemyLink(Alchemy alchemy) {
			super(alchemy.getName());
//			setStyleName(css.stateSelection());
//			if (listener.isVertical()==false) {
//				getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
//			}
			this.alchemy = alchemy;
//			setText(alchemy.getName());
			addClickHandler(this);
		}

		public void onClick(ClickEvent event) {
			messages.clear();
			if (playerState.doAlchemyInBattle(alchemy)) {
				listener.redraw(true);
			}
		}
	}
	
	
	
	public class NPCWidget extends FlowPanel implements ClickHandler{
		private Label name;
		private FighterData data;
		
		public NPCWidget(FighterData data, boolean active) {
			this.data = data;
			
			name = new Label(data.getNpc().getNpc().getName());
			if (data.isAlive()) {
				name.setStyleName(css.battleNpcName());
			} else {
				name.setStyleName(css.battleFighterDead());
			}
			add(name);
			
			if (data.isAlive()) {
				if (data.isFriend()) {
					setStyleName(css.battleFriendWidget());
				} else if (playerState.getFighters().size()<2 || !active) {
					setStyleName(css.battleFighterWidget());
				} else if (playerState.isTarget(data)) {
					setStyleName(css.battleFighterWidgetSelected());
				} else if (data.isCanBeTarget()){
					sinkEvents(Event.ONCLICK);
					setStyleName(css.battleFighterWidgetUnselected());
					name.addStyleName(css.battleNpcNameUnselected());
				} else {
					//non-selectable
					setStyleName(css.battleFighterWidgetNonAvalalble());
				}
			} else {
				setStyleName(css.battleFighterWidget());
//				setStyleName(css.battleFighterWidgetDead());
			}
		
			for (Parameter parameter : data.getParameters().keySet()) {
				int value = data.getParameters().get(parameter);
				Label label = new Label(parameter.getName());
				label.setStyleName(css.battleNpcParameterName());
				add(label);
				add(new ParameterWidget(parameter,value,null));
			}
		}

		@Override
		public void onBrowserEvent(Event event) {
			onClick(null);
		}

		public void onClick(ClickEvent event) {
			if (data.isCanBeTarget()) {
				playerState.selectTarget(data);
				listener.redraw(false);
			}
		}

	}
	
	
	public class HeroWidget extends FlowPanel {
		private Label name;
		private HashSet<Parameter> used;
		public void updateParameters() {
			for (int i = 0; i < this.getWidgetCount(); i++) {
				Widget widget = this.getWidget(i);
				if (widget instanceof ParameterWidget) {
					ParameterWidget parameterWidget = (ParameterWidget) widget;
					parameterWidget.apply(playerState.getParameters().get(parameterWidget.parameter),playerState.getParameters());
				}
			}
		}

		public HeroWidget() {
			used = new HashSet<Parameter>(playerState.getParameters().size());
			name = new Label(appConstants.playerBattleHero());
			setStyleName(css.battleFighterWidget());
			if (playerState.isHeroAlive()) {
				if (playerState.isHeroFighting()) {
					name.setStyleName(css.battleNpcName());
				} else {
					name.setStyleName(css.battleHeroInactive());
				}
			} else {
				name.setStyleName(css.battleFighterDead());
			}
			add(name);
			
			for (Parameter parameter : playerState.getParameters().keySet()) {
				//add parameter to the list
				if (playerState.getCurrentBattle().dependsOn(parameter)) {
					addParameter(parameter,playerState.getParameters().get(parameter),playerState.getParameters());
				}
			}
			
		}

		public void addParameter(Parameter parameter, int value, Map<Parameter,Integer> params) {
			if (used.contains(parameter) || parameter.isInvisible()) {
				//already used or invisible
				return;
			}
			Label label = new Label(parameter.getName());
			label.setStyleName(css.battleNpcParameterName());
			add(label);
			add(new ParameterWidget(parameter,value,params));
		}

	}

	public class ParameterWidget extends Label{
		private Parameter parameter;
		public ParameterWidget(Parameter parameter, int value, Map<Parameter, Integer> params) {
			setStyleName(css.battleNpcParameterValue());
			this.parameter = parameter;
			apply(value,params);
		}
		
		public void apply(int val, Map<Parameter, Integer> params) {
			if (parameter.getLimit()==null || params==null) {
				//no limit
				setText(String.valueOf(val));
 			} else {
 				//update value/limit
 				setText(String.valueOf(val)+"/"+params.get(parameter.getLimit()));
 			}
		}
	}



	public void attack() {
		messages.clear();
		playerState.nextBattleRound(playerListener);
		listener.redraw(true);
	}

}
