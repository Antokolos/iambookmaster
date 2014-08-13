package com.iambookmaster.client.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.common.AnimationTimer;
import com.iambookmaster.client.common.SpanHTML;
import com.iambookmaster.client.common.SpanLabel;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.player.PlayerState.FighterData;

public class BattlePanelStandard extends VerticalPanel implements BattlePanel{

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	private static final AppMessages appMessages = AppLocale.getAppMessages();
	
	private static int battleCounter;
	
	private Battle battle;
	private Button attack;
	private Button auto;
	private Model model;
	private PlayerState playerState;
	private BattleListener playerListener;
	public String battleName;
	public ClickHandler mainClickHandler;
	private HeroWidget heroWidget;
	private FlowPanel alchemyPanel;
	private AnimationTimer animationTimer;
//	private int battleConsoleHeight;
	private ArrayList<ParagraphConnection> vitalConnections;
	private ArrayList<String> messages = new ArrayList<String>();
	public BattlePanelStandard(Model mod, Battle btl, PlayerState ps, PlayerListener playerLs) {
		animationTimer = new AnimationTimer();
		battleName = "BattlePanelStandard"+String.valueOf(battleCounter++);
		this.battle = btl;
		this.playerState = ps;
		this.model = mod;
		setWidth("100%");
		List<ParagraphConnection> list = model.getOutputParagraphConnections(playerState.getCurrentParagraph());
		for (ParagraphConnection connection : list) {
			if (connection.getType()==ParagraphConnection.TYPE_VITAL_LESS || connection.getType()==ParagraphConnection.TYPE_ENEMY_VITAL_LESS) {
				if (vitalConnections == null) {
					vitalConnections = new ArrayList<ParagraphConnection>();
				}
				vitalConnections.add(connection);
			}
		}
		
		playerListener = new BattleListenerAdapter(appConstants,appMessages,playerState) {
			@Override
			protected void addMessage(String message) {
				messages.add(message);
			}
		};
		
		setStyleName(PlayerStyles.BATTLE_PANEL);
		setSize("100%", "100%");
		if (battle.getVital()==null) {
			errorState();
			Window.alert(appMessages.playerBattleNoVital(battle.getName()));
			return;
		}
		if (playerState.getCurrentParagraph().getEnemies()==null && playerState.getCurrentParagraph().getEnemies().size()==0) {
			errorState();
			Window.alert(appConstants.playerBattleNoEmenies());
			return;
		}
		mainClickHandler = new ClickHandler(){
			public void onClick(ClickEvent event) {
				if (event.getSource()==attack) {
					playerState.nextBattleRound(playerListener);
					redraw();
				} else if (event.getSource()==auto) {
					attack.setEnabled(false);
					auto.setEnabled(false);
					new Timer() {
						@Override
						public void run() {
							if (playerState.nextBattleRound(playerListener)) {
								redraw();
							} else {
								redraw();
								cancel();
							}
						}

					}.scheduleRepeating(500);
				}
			}
		};
		redraw();
	}
	
	private void redraw() {
		if (getWidgetCount()>0) {
			clear();
		}
		boolean heroAlive = playerState.isHeroAlive();
		boolean battleActive = heroAlive && playerState.isBattleActive();
		
		Label label = new Label(battle.getName());
		add(label);
		setCellWidth(label, "100%");
		setCellHorizontalAlignment(label, HasHorizontalAlignment.ALIGN_CENTER);
		
		//add Hero
		heroWidget = new HeroWidget(playerState.getParameters(),heroAlive);
		add(heroWidget);
		
		//add partners
		
		//add enemies
		for (FighterData npc : playerState.getFighters()) {
			NPCWidget widget = new NPCWidget(npc,battleActive);
			add(widget);
		}
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSize("100%", "100%");
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		attack = new Button(appConstants.playerButtonAttack(),mainClickHandler);
		attack.setTitle(appConstants.playerButtonAttackTitle());
		attack.setEnabled(battleActive);
		horizontalPanel.add(attack);
		
		if (battle.isOneTurnBattle()) {
			horizontalPanel.setCellWidth(attack,"100%");
		} else {
			horizontalPanel.setCellWidth(attack,"50%");
			auto = new Button(appConstants.playerButtonAutoBattle(),mainClickHandler);
			auto.setTitle(appConstants.playerButtonAutoBattleTitle());
			auto.setEnabled(battleActive);
			horizontalPanel.add(auto);
			horizontalPanel.setCellWidth(auto,"50%");
		}
		add(horizontalPanel);
		
		if (battleActive) {
			//all applicable alchemy
			alchemyPanel = new FlowPanel();
			alchemyPanel.setWidth("100%");
			add(alchemyPanel);
			updateAlchemyStatus(true);
		}
		
		if (model.getSettings().isShowBattleConsole()) {
			for (String	message : messages) {
				label = new HTML(message);
				label.addStyleName(PlayerStyles.BATTLE_MESSAGE);
				add(label);
			}
			messages.clear();
		}
	}

	public class AlchemyLink extends Image implements ClickHandler{
		private Alchemy alchemy;
		private boolean enabled=true;
		public AlchemyLink(Alchemy alchemy) {
			this.alchemy = alchemy;
			setStyleName(PlayerStyles.ALCHEMY_LINK);
			setEnabled(true);
			addClickHandler(this);
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
			if (enabled) {
				setUrl(PlayImages.SELECT);
				setStyleName(PlayerStyles.CLICKABLE);
			} else {
				removeStyleName(PlayerStyles.CLICKABLE);
				setUrl(PlayImages.SELECT_DISABLED);
			}
		}

		public void onClick(ClickEvent event) {
			if (enabled && playerState.doAlchemyInBattle(alchemy)) {
				redraw();
			}
		}
	}
	
	
	
	private void updateAlchemyStatus(boolean enable) {
		ArrayList<Alchemy> alchemyList = playerState.getAlchemy(true, true);
		alchemyPanel.clear();
		if (alchemyList != null) {
			for (Alchemy alchemy : alchemyList) {
				if (alchemy.getBattleLimit() == null) {
					if (alchemy.getFromValue()>0) {
						heroWidget.addParameter(alchemy.getFrom(), playerState.getParameters().get(alchemy.getFrom()),playerState.getParameters());
					}
				} else {
					heroWidget.addParameter(alchemy.getBattleLimit(), playerState.getHeroBatleLimits().get(alchemy.getBattleLimit()),playerState.getParameters());
				}
				alchemyPanel.add(new SpanHTML(alchemy.getName()));
				AlchemyLink link = new AlchemyLink(alchemy);
				alchemyPanel.add(link);
				link.setEnabled(enable);
			}
		}
	}

	private void errorState() {
	}

	public class NPCWidget extends FlowPanel implements ClickHandler{
		private SpanLabel name;
		private FighterData data;
		
		public NPCWidget(FighterData data, boolean active) {
			this.data = data;
			if (data.isAlive()) {
				if (data.isFriend()) {
					setStyleName(PlayerStyles.NPC_FRIEND);
				} else if (playerState.getFighters().size()<2) {
					setStyleName(PlayerStyles.NPC_SINGLE);
				} else if (playerState.isTarget(data)) {
					setStyleName(PlayerStyles.NPC_SELECTED);
				} else if (data.isCanBeTarget()){
					if (active) {
						setTitle(appConstants.battleClickToSelectTarget());
						sinkEvents(Event.ONCLICK);
					}
					setStyleName(PlayerStyles.NPC_NON_SELECTED);
				} else {
					setStyleName(PlayerStyles.NPC_NON_AVAILABLE);
				}
			} else {
				setStyleName(PlayerStyles.NPC_DEAD);
			}
			
			name = new SpanLabel(data.getNpc().getNpc().getName()+":");
			add(name);
			for (Parameter parameter : data.getParameters().keySet()) {
				int value = data.getParameters().get(parameter);
				SpanLabel label = new SpanLabel(parameter.getName());
				label.setStyleName(PlayerStyles.PARAMETER_NAME);
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
				redraw();
			}
		}

	}
	
	public class HeroWidget extends FlowPanel {

		private HashSet<Parameter> used;
		private SpanLabel name;
		
		public HeroWidget(Map<Parameter,Integer> parameters, boolean heroAlive) {
			if (heroAlive) {
				if (playerState.isHeroFighting()) {
					setStyleName(PlayerStyles.HERO_ALIVE);
				} else {
					setStyleName(PlayerStyles.HERO_INACTIVE);
				}
			} else {
				setStyleName(PlayerStyles.HERO_DEAD);
			}
			used = new HashSet<Parameter>(playerState.getParameters().size());
			name = new SpanLabel(appConstants.playerBattleHero());
			add(name);
			
			for (Parameter parameter : playerState.getParameters().keySet()) {
				//add parameter to the list
				if (battle.dependsOn(parameter)) {
					Integer value = playerState.getParameters().get(parameter);
					addParameter(parameter,value,playerState.getParameters());
				}
			}
		}

		public void addParameter(Parameter parameter, int value, Map<Parameter,Integer> params) {
			if (used.contains(params) || parameter.isInvisible()) {
				return;
			}
			used.add(parameter);
			if (parameter.getLimit() != null) {
				used.add(parameter.getLimit());
			}
			SpanLabel label = new SpanLabel(parameter.getName()+":");
			label.setStyleName(PlayerStyles.PARAMETER_NAME);
			add(label);
			add(new ParameterWidget(parameter,value,params));
		}
	}

	public class ParameterWidget extends SpanLabel{
		private Parameter parameter;
		public ParameterWidget(Parameter parameter, int value, Map<Parameter, Integer> params) {
			setStyleName(PlayerStyles.PARAMETER_VALUE);
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
	
	public void close() {
//		playerState.removePlayerStateListener(playerStateListener);
		
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		animationTimer.cancel();
		close();
	}

	public void restore() {
		//TODO
	}

}
