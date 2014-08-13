package com.iambookmaster.client.player;

import java.util.ArrayList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.NPC;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.beans.Picture;
import com.iambookmaster.client.beans.Sound;
import com.iambookmaster.client.beans.Sprite;
import com.iambookmaster.client.common.Base64Coder;
import com.iambookmaster.client.common.ColorProvider;
import com.iambookmaster.client.common.EditorPlayer;
import com.iambookmaster.client.common.EditorTab;
import com.iambookmaster.client.common.FileExchangeClient;
import com.iambookmaster.client.common.MaskPanel;
import com.iambookmaster.client.common.ScrollContainer;
import com.iambookmaster.client.common.SpanHTML;
import com.iambookmaster.client.common.SpanLabel;
import com.iambookmaster.client.common.TrueVerticalSplitPanel;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ParagraphConnectionListener;
import com.iambookmaster.client.model.ParagraphListener;
import com.iambookmaster.client.model.ParagraphParsingHandler;
import com.iambookmaster.client.player.layout.ViewerLayout;

public class Player extends HorizontalPanel implements EditorTab,EditorPlayer {
	
	private static final String STYLE_CLICKABLE = "clickable";
	private static final String STYLE_DISABLE_GRAPH = "player_disable_graph";
	
	private static final FileExchangeClient fileExchange = new FileExchangeClient();
	
	private static final AppConstants appConstants = AppLocale.getAppConstants();
	private static final AppMessages appMessages = AppLocale.getAppMessages();
	private VerticalPanel mainPanel;
	private Image edit;
	private Image restart;
	private PlayerListener playerListener;
	private ParagraphConnectionListener locationConnectionListener;
	private ParagraphListener locationListener;
	private Model model;
	private Image items;
	private Image save;
	private Image load;
	private Image audio;
	private Image background;
	private Image help;
	private Image feedback;
	private boolean audioEnabled;
	private boolean graphEnabled;
	private boolean active;
	private boolean resizeOnActivation;
	private PlayerState playerState;
	private TrueVerticalSplitPanel horizontalSplitPanel;
	private ScrollContainer scrollContainer;
	private PlayerList playerList;
	private PopUpPlayerList popUpPlayerList;
	private boolean finished;
//	mailto:iambookmaster@gmail.com
	private String feedbackURL=Base64Coder.decodeString("bWFpbHRvOmlhbWJvb2ttYXN0ZXJAZ21haWwuY29t");
	private boolean authorHasOtherBooks;
	private Timer feedbackAnimator;
	private String allBooksURL="http://www.iambookmaster.com/";
	private BattlePanel battlePanel;
	private FlowPanel textParagraphPanel;
	private ClickHandler mainClickHandler;
	private boolean saveEnabled;
	private PlayerLayout layout;
	private ScrollContainer bagContainer;
	private ArrayList<Widget> detachPanels=new ArrayList<Widget>();
	private int nextImageFiller;

	
	public Player(Model mod,PlayerListener playerLs,PlayerLayout layout) {
		this.model = mod;
		setSize("100%", "100%");
		if (layout==null) {
			layout = new PlayerLayout() {
				public Element getElement(String id) {
					return null;
				}
				public void addStyle(String id, String style) {
				}
				public void removeStyle(String id, String style) {
				}
			};
		}
		this.layout = layout;
		if (model.getSettings().getApplicationColor() != 0) {
			DOM.setStyleAttribute(getElement(), "backgroundColor", ColorProvider.getColorName(model.getSettings().getApplicationColor()));
		}
		
		audioEnabled = (model.getSettings().isDisableAudio()==false);
		graphEnabled = (model.getSettings().isDisableImages()==false);
		
		playerState = new PlayerState(mod,appConstants,appMessages);
		
		playerState.setAllowAudio(audioEnabled);
		playerState.setAllowImages(graphEnabled);

		playerState.addPlayerStateListener(new PlayerStateListener() {

			public void addObject(ObjectBean object) {
				//not for us
			}

			public void lostObject(ObjectBean object) {
				//not for us
			}
			
			public void removeObject(ObjectBean object) {
				//not for us
			}

			public void useObject(ObjectBean object,boolean success) {
				if (popUpPlayerList != null && popUpPlayerList.isVisible()) {
					//hide Player list after using
					popUpPlayerList.hide();
				}
				if (success) {
					drawLocation(true);
				} else {
					showMessage(object.getNextMissusedMessage(appConstants));
				}
			}

			public void reset() {
				finished = false;
				removeBattlePanel();
				saveEnable(true);
			}

			public void finish() {
				finishGame();
				animateFeedback();
			}

			public void battle(Battle battle, boolean start) {
				if (start) {
					saveEnable(false);
					removeBattlePanel();
					battlePanel = new BattlePanelStandard(model,battle,playerState,playerListener);
				} else {
					updateConnectiosStatus(true);
					if (finished==false) {
						saveEnable(true);
					}
					//end battle,
				}
				
			}

			public void enemy(NPC npc, boolean add) {
			}

			public void changeModificator(Modificator parameter, boolean value) {
				//not for us
			}

			public void changeParameter(Parameter parameter, int value) {
				if (parameter.isVital() && value<=0) {
					Window.alert(appMessages.urqHeroDiedByVitalParameter(parameter.getName()));
				}
			}

			public void disableConnection(ParagraphConnection connection) {
				enableOneConnection(connection,false);
			}

			public void enableConnection(ParagraphConnection connection) {
				enableOneConnection(connection,true);
			}

		});
		this.playerListener = playerLs;
		
		mainClickHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				processClick(event.getSource());
			}

		};
		
		VerticalPanel iconsPanel = new VerticalPanel();
		iconsPanel.setStyleName(PlayerStyles.ICONS_PANEL);
		iconsPanel.setSpacing(5);
		iconsPanel.setSize("100%", "100%");
		
		if (model.getSettings().getPlayerListType()!=Model.PLAYER_LIST_TYPE_NONE) {
			playerList = new PlayerList(model,playerState);
			//add button
			if (model.getSettings().getPlayerListType()==Model.PLAYER_LIST_TYPE_POPUP) {
				items = new Image(PlayImages.BAG);
				items.setTitle(appConstants.playerTitlePlayerList());
				addButton(PlayerLayout.ITEMS_BUTTON,items,iconsPanel);
			}
		}
		
		help = new Image(PlayImages.HELP);
		help.setTitle(appConstants.playerTitleHelp());
		addButton(PlayerLayout.HELP_BUTTON,help,iconsPanel);
		
		save = new Image(PlayImages.SAVE);
		save.setTitle(appConstants.playerTitleSaveGame());
		addButton(PlayerLayout.SAVE_BUTTON,save,iconsPanel);
		
		load = new Image(PlayImages.LOAD);
		load.setTitle(appConstants.playerTitleLoadGame());
		addButton(PlayerLayout.LOAD_BUTTON,load,iconsPanel);
		
		restart = new Image(PlayImages.RESET);
		restart.setTitle(appConstants.playerTitleRestartGame());
		addButton(PlayerLayout.RESTART_BUTTON,restart,iconsPanel);
		
		background = new Image();
		background.setTitle(appConstants.playerTitleDisableImages());
		if (model.getSettings().isDisableImages()==false) {
			addButton(PlayerLayout.DISABLE_IMAGES_BUTTON,background,iconsPanel);
		}
		applyBackground();
		
		audio = new Image();
		audio.setTitle(appConstants.playerTitleAudioEnable());
		if (model.getSettings().isDisableAudio()==false) {
			addButton(PlayerLayout.DISABLE_SOUND_BUTTON,audio,iconsPanel);
		}
		applyAudio();

		feedback = new Image(PlayImages.FEEDBACK);
		feedback.setTitle(appConstants.feedbackProvideTitle());
		addButton(PlayerLayout.FEEDBACK_BUTTON,feedback,iconsPanel);

		if (playerListener != null) {
			//only for editor
			edit = new Image(PlayImages.EDIT);
			edit.setStyleName(STYLE_CLICKABLE);
			edit.addClickHandler(mainClickHandler);
			edit.setTitle(appConstants.playerTitleEditParagraph());
			iconsPanel.add(edit);
			iconsPanel.setCellHeight(edit, "1%");
			iconsPanel.setCellWidth(edit, "100%");
		} 
		
		if (iconsPanel.getWidgetCount()>0) {
			HTML html = new HTML("&nbsp;");
			iconsPanel.add(html);
			iconsPanel.setCellHeight(html, "99%");
			iconsPanel.setCellWidth(html, "100%");
			add(iconsPanel);
			setCellHeight(iconsPanel, "100%");
			setCellWidth(iconsPanel, "1%");
		}
		
		mainPanel = new VerticalPanel();
		mainPanel.setSize("100%", "100%");
		mainPanel.setSpacing(5);
		if (model.getSettings().getTextBackground() != 0) {
			DOM.setStyleAttribute(mainPanel.getElement(), "backgroundColor", ColorProvider.getColorName(model.getSettings().getTextBackground()));
		}
		if (model.getSettings().getPlayerListType()==Model.PLAYER_LIST_TYPE_ALWAYS) {
			//add bag
			Element element = layout.getElement(PlayerLayout.BAG);
			if (element==null) {
				horizontalSplitPanel = new TrueVerticalSplitPanel(true,true);
				horizontalSplitPanel.setTopWidget(mainPanel);
				horizontalSplitPanel.setBottomWidget(playerList);
				horizontalSplitPanel.setSplitPosition("70%");
				add(horizontalSplitPanel);
				setCellHeight(horizontalSplitPanel, "100%");
				setCellWidth(horizontalSplitPanel, "99%");
			} else {
				scrollContainer = new ScrollContainer();
				scrollContainer.setScrollWidget(mainPanel);
				scrollContainer.setAlwaysShowScrollBars(false);
				add(scrollContainer);
				setCellHeight(scrollContainer, "100%");
				setCellWidth(scrollContainer, "99%");
//				getElement().getStyle().setProperty("border", "1px solid red");
				
//				scrollContainer.getElement().getStyle().setProperty("border", "1px solid green");
				
				bagContainer = new ScrollContainer();
				bagContainer.setAlwaysShowScrollBars(false);
				bagContainer.setScrollWidget(playerList);
				element.appendChild(bagContainer.getElement());
				ViewerLayout.applySize(element, bagContainer);
				bagContainer.onAttach();
			}
		} else {
			scrollContainer = new ScrollContainer();
			scrollContainer.setScrollWidget(mainPanel);
			scrollContainer.setAlwaysShowScrollBars(false);
			add(scrollContainer);
			setCellHeight(scrollContainer, "100%");
			setCellWidth(scrollContainer, "99%");
		}
		
		locationListener = new ParagraphListener() {
			public void addNewParagraph(Paragraph location) {
			}
			public void edit(Paragraph location) {
			}
			public void refreshAll() {
				start();
			}
			public void select(Paragraph location) {
			}
			public void unselect(Paragraph location) {
			}
			public void update(Paragraph location) {
				if (location==playerState.getCurrentParagraph()) {
					drawLocation(false);
				}
			}
			public void remove(Paragraph location) {
				if (location==playerState.getCurrentParagraph()) {
					drawLocation(false);
				}
			}
		};
		locationConnectionListener = new ParagraphConnectionListener() {
			public void refreshAll() {
			}
			public void select(ParagraphConnection connection) {
			}
			public void unselect(ParagraphConnection connection) {
			}
			public void update(ParagraphConnection connection) {
				if (connection.getFrom()==playerState.getCurrentParagraph() || connection.getTo()==playerState.getCurrentParagraph()) {
					drawLocation(false);
				}
			}
			public void remove(ParagraphConnection connection) {
				if (connection.getFrom()==playerState.getCurrentParagraph() || connection.getTo()==playerState.getCurrentParagraph()) {
					drawLocation(false);
				}
			}
			public void addNew(ParagraphConnection connection) {
				if (connection.getFrom()==playerState.getCurrentParagraph() || connection.getTo()==playerState.getCurrentParagraph()) {
					drawLocation(false);
				}
			}
		};
		
		model.addParagraphListener(locationListener);
		model.addParagraphConnectionListener(locationConnectionListener);
		
	}

	
	private void addButton(String id, final Image image, VerticalPanel iconsPanel) {
		Element element = layout.getElement(id);
		image.setStyleName(STYLE_CLICKABLE);
		if (element==null) {
			image.addClickHandler(mainClickHandler);
			iconsPanel.add(image);
			iconsPanel.setCellHeight(image, "1%");
			iconsPanel.setCellWidth(image, "100%");
		} else {
			com.google.gwt.user.client.Element el = (com.google.gwt.user.client.Element) element;
			DOM.sinkEvents(el,Event.ONCLICK);
			DOM.setEventListener(el, new EventListener(){
				public void onBrowserEvent(Event event) {
					processClick(image);
				}
			});
			layout.addStyle(id, PlayerLayout.BUTTON_ON_STYLE);
		}
	}

	private void processClick(Object source) {
		if (finished==false) {
			if (source==items) {
				//show current items in popup
				if (popUpPlayerList == null) {
					popUpPlayerList = new PopUpPlayerList();
				} else {
					popUpPlayerList.centerAndShow();
				}
			} else if (source==save) {
				//save current game
				if (saveEnabled==false) {
					return;
				}
				String save64 = playerState.saveState(false);
				if (fileExchange.checkApplet()) {
					fileExchange.saveFile(save64,appConstants.playerSavedGame());
				} else {
					createExchangePanel(save64);
				}
			} else if (source==background) {
				graphEnabled = !graphEnabled;
				playerState.setAllowImages(graphEnabled);
				applyBackground();
				drawLocation(false);
			} else if (source==audio) {
				audioEnabled = !audioEnabled;
				playerState.setAllowAudio(audioEnabled);
				applyAudio();
				drawLocation(false);
			}
		}
		if (source==restart) {
			if (Window.confirm(appConstants.playerRestartGame())) {
				start();
			}
		} else if (source==load) {
			if (FileExchangeClient.checkApplet()) {
				String text = fileExchange.loadFile(appConstants.playerRestoreGame());
				if (text != null) {
					Player.this.processLoad(text);
				}
			} else {
				createExchangePanel(null);
			}
		} else if (source==feedback) {
			//send feedback
			sendFeedback();
		} else if (source==help) {
			showHelp();
		} else if (source==edit) {
			model.editParagraph(playerState.getCurrentParagraph(),locationListener);
		}
	}

	private void createExchangePanel(final String save) {
		VerticalPanel panel = new VerticalPanel();
		panel.setSpacing(5);
		panel.setSize("100%", "100%");
		Label title = new Label(save==null ? appConstants.playerRestoreGame() : appConstants.playerSavedGame());
		panel.add(title);
		panel.setCellWidth(title,"100%");
		panel.setCellHeight(title,"1%");
		
		final TextArea textArea = new TextArea();
		textArea.setHeight("100%");
		textArea.setWidth("100%");
		panel.add(textArea);
		panel.setCellWidth(textArea,"100%");
		panel.setCellHeight(textArea,"99%");
		if (save != null) {
			textArea.setReadOnly(true);
			DeferredCommand.addCommand(new Command(){
				public void execute() {
					textArea.setText(save);
				}
			});
		}
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(5);
		horizontalPanel.setSize("100%", "100%");
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		if (save==null) {
			Button loadButton = new Button(AppLocale.getAppConstants().buttonLoad(),new ClickHandler(){
				public void onClick(ClickEvent event) {
					MaskPanel.show();
					DeferredCommand.addCommand(new Command(){
						public void execute() {
							if (processLoad(textArea.getText())==false) {
								MaskPanel.hide();
							}
						}
					});
				}
			});
			horizontalPanel.add(loadButton);
			horizontalPanel.setCellWidth(loadButton,"1%");
		}
		Button button = new Button(AppLocale.getAppConstants().buttonClose(),new ClickHandler(){
			public void onClick(ClickEvent event) {
				restore();
			}
		});
		horizontalPanel.add(button);
		horizontalPanel.setCellWidth(button,"1%");
		HTML html = new HTML("&nbsp;");
		horizontalPanel.add(html);
		horizontalPanel.setCellWidth(html,"98%");
		panel.add(horizontalPanel);
		panel.setCellWidth(horizontalPanel,"100%");
		panel.setCellHeight(horizontalPanel,"1%");
		show(panel);
	}


	private boolean processLoad(String text) {
		try {
			playerState.restoreState(text);
			if (model.getSettings().isDisableAudio()==false) {
				audioEnabled = playerState.isAllowAudio();
			}
			if (model.getSettings().isDisableImages()==false) {
				graphEnabled = playerState.isAllowImages();
			}
			applyBackground();
			applyAudio();
			applyBackgroundPicture(playerState.getBackground());
			model.stopBackgroundSound();
			Sound sound = playerState.getBackgroundSound();
			if (audioEnabled && sound != null) {
				model.playBackground(sound);
			}
			finished = false;
			MaskPanel.hide();
			saveEnable(true);
			drawLocation(false);
			return true;
		} catch (Exception e) {
			Window.alert(e.toString());
			return false;
		}
	}

	protected void enableOneConnection(ParagraphConnection connection, boolean enable) {
		for (int i = 0; i < textParagraphPanel.getWidgetCount(); i++) {
			Widget widget = textParagraphPanel.getWidget(i);
			if (widget instanceof TextLink) {
				TextLink link = (TextLink) widget;
				if (link.connection==connection) {
					if (enable != link.enabled) {
						link.setEnabled(enable);
					}
				}
			}
		}
	}

	protected void saveEnable(boolean enable) {
		saveEnabled = enable;
		if (enable) {
			save.removeStyleName(PlayerStyles.IMAGE_DISABLED);
			save.addStyleName(PlayerStyles.CLICKABLE);
			layout.addStyle(PlayerLayout.SAVE_BUTTON, PlayerLayout.BUTTON_ON_STYLE);
			layout.removeStyle(PlayerLayout.SAVE_BUTTON, PlayerLayout.BUTTON_DISABLED_STYLE);
		} else {
			save.removeStyleName(PlayerStyles.CLICKABLE);
			save.setStyleName(PlayerStyles.IMAGE_DISABLED);
			layout.addStyle(PlayerLayout.SAVE_BUTTON, PlayerLayout.BUTTON_DISABLED_STYLE);
			layout.removeStyle(PlayerLayout.SAVE_BUTTON, PlayerLayout.BUTTON_ON_STYLE);
		}
	}

	protected boolean updateConnectiosStatus(boolean enableAll) {
		for (int i = 0; i < textParagraphPanel.getWidgetCount(); i++) {
			Widget widget = textParagraphPanel.getWidget(i);
			if (widget instanceof AlchemyLink) {
				AlchemyLink link = (AlchemyLink) widget;
				if (enableAll) {
					link.setEnabled(playerState.meetsCondition(link.alchemy,false));
				} else {
					link.setEnabled(false);
				}
				
			} else if (widget instanceof TextLink) {
				TextLink link = (TextLink) widget;
				if (enableAll) {
					ParagraphConnection connection = link.connection;
					if (playerState.meetsCondition(connection)) {
						//match
 						link.setEnabled(true);
	 					if (connection.getStrictness() == ParagraphConnection.STRICTNESS_MUST) {
							//only this connection can be used
							if (model.getSettings().isSkipMustGoParagraphs()) {
								goLocation(connection.getTo());
								return false;
							}
							for (int j = 0; j < i; j++) {
								widget = textParagraphPanel.getWidget(j);
								if (widget instanceof TextLink) {
									TextLink link2 = (TextLink) widget;
									link2.setEnabled(false);
								}
							}
							enableAll = false;
						}
					} else {
 						link.setEnabled(false);
					}
				} else {
					link.setEnabled(false);
				}
			}
		}
		return true;
	}

	private void removeBattlePanel() {
		if (battlePanel != null) {
			//remove old panel at any case
			battlePanel.close();
			Widget widget = (Widget)battlePanel;
			mainPanel.remove(widget);
			battlePanel = null;
		}
	}

	private void sendFeedback() {
		boolean email = feedbackURL.toLowerCase().startsWith("mailto:");
		FeedbackPanel feedbackPanel = new FeedbackPanel(authorHasOtherBooks,feedbackURL,playerState.getCurrentParagraph(),model,email,allBooksURL) {
			@Override
			protected void onClose() {
				restore();
			}
		};
		show(feedbackPanel);
		if (feedbackAnimator != null) {
			stopFeedbackAnimation();
		}
	}

	private void showHelp() {
		show(new PlayerHelpPanel(){
			@Override
			protected void onClose() {
				restore();
			}
		});
	}

	private void finishGame() {
		updateConnectiosStatus(false);
		finished = true;
	}

	private void showMessage(String message) {
		int i = mainPanel.getWidgetCount();
		Label label = new Label(message);
		label.setStyleName(PlayerStyles.MESSAGE);
		mainPanel.insert(label, i-1);
	}

	private void applyBackground() {
		layout.removeStyle(PlayerLayout.DISABLE_IMAGES_BUTTON, PlayerLayout.BUTTON_OFF_STYLE);
		layout.removeStyle(PlayerLayout.DISABLE_IMAGES_BUTTON, PlayerLayout.BUTTON_ON_STYLE);
		layout.removeStyle(PlayerLayout.DISABLE_IMAGES_BUTTON, PlayerLayout.BUTTON_DISABLED_STYLE);
		if (model.getSettings().isDisableImages()) {
			background.setTitle(appConstants.playerTitleImagesIsDisabledForTheGame());
			background.setUrl(PlayImages.DISABLE_GRAPH);
			background.addStyleName(STYLE_DISABLE_GRAPH);
			layout.addStyle(PlayerLayout.DISABLE_IMAGES_BUTTON, PlayerLayout.BUTTON_DISABLED_STYLE);
		} else if (graphEnabled) {
			background.removeStyleName(STYLE_DISABLE_GRAPH);
			background.setUrl(PlayImages.ENABLE_GRAPH);
			background.setTitle(appConstants.playerTitleDisableImages());
			layout.addStyle(PlayerLayout.DISABLE_IMAGES_BUTTON, PlayerLayout.BUTTON_ON_STYLE);
		} else {
			background.setTitle(appConstants.playerTitleEnableImages());
			background.setUrl(PlayImages.DISABLE_GRAPH);
			background.addStyleName(STYLE_DISABLE_GRAPH);
			layout.addStyle(PlayerLayout.DISABLE_IMAGES_BUTTON, PlayerLayout.BUTTON_OFF_STYLE);
		}
	}

	private void applyAudio() {
		layout.removeStyle(PlayerLayout.DISABLE_SOUND_BUTTON, PlayerLayout.BUTTON_OFF_STYLE);
		layout.removeStyle(PlayerLayout.DISABLE_SOUND_BUTTON, PlayerLayout.BUTTON_ON_STYLE);
		layout.removeStyle(PlayerLayout.DISABLE_SOUND_BUTTON, PlayerLayout.BUTTON_DISABLED_STYLE);
		if (model.getSettings().isDisableAudio()) {
			audio.setTitle(appConstants.playerAudioIsDisableInGame());
			audio.setUrl(PlayImages.DISABLE_AUDIO);
			layout.addStyle(PlayerLayout.DISABLE_SOUND_BUTTON, PlayerLayout.BUTTON_DISABLED_STYLE);
		} else if (audioEnabled) {
			audio.setUrl(PlayImages.ENABLE_AUDIO);
			audio.setTitle(appConstants.playerTitleAudioDisable());
			layout.addStyle(PlayerLayout.DISABLE_SOUND_BUTTON, PlayerLayout.BUTTON_ON_STYLE);
		} else {
			audio.setTitle(appConstants.playerTitleAudioEnable());
			audio.setUrl(PlayImages.DISABLE_AUDIO);
			layout.removeStyle(PlayerLayout.DISABLE_SOUND_BUTTON, PlayerLayout.BUTTON_DISABLED_STYLE);
			layout.addStyle(PlayerLayout.DISABLE_SOUND_BUTTON, PlayerLayout.BUTTON_OFF_STYLE);
		}
	}

	public void start() {
		saveEnable(true);
		finished = false;
		playerState.reset();
		if (playerState.getCurrentParagraph()==null) {
			Window.alert(appConstants.validatorStartParagraphIsNotSet());
			return;
		}
		applyBackgroundPicture(null);
		model.stopBackgroundSound();
		drawLocation(true);
	}
	
	public class SpritePanel extends AbsolutePanel {

		@Override
		protected void onDetach() {
			super.onDetach();
		}

		@Override
		public void onAttach() {
			super.onAttach();
		}
	}
	
	public class ComplexImageLoader implements LoadHandler {
		private Picture picture;
		private Image image;
		private ArrayList<Sprite> sprites;
		private SpritePanel panel;
		private int counter;
		public ComplexImageLoader(Picture picture, ArrayList<Sprite> sprites) {
			this.picture=picture;
			this.image = new Image();
			this.sprites = sprites;
			image.addLoadHandler(this);
			panel = new SpritePanel();
			panel.add(image, 0, 0);
			counter = sprites.size();
			panel.setVisible(false);
			for (Sprite sprite : sprites) {
				Image image = new Image();
				image.addLoadHandler(this);
				panel.add(image, sprite.getX(), sprite.getY());
				image.setUrl(sprite.getPicture().getUrl());
			}
			image.setUrl(picture.getUrl());
			applyImage(panel, true);
			
		}

		public void onLoad(LoadEvent event) {
			counter--;
			if (event.getSource()==image) {
				panel.setSize(String.valueOf(image.getWidth())+"px", String.valueOf(image.getHeight())+"px");
			}
			if (counter<0) {
				panel.setVisible(true);
			}
		}
	}
	
	private void drawLocation(boolean apply) {
		//start animation
		final Paragraph currentLocation = playerState.getCurrentParagraph();
		if (saveEnabled==false && currentLocation.getBattle()==null) {
			saveEnable(true);
		}
		
		if (apply) {
			stopFeedbackAnimation();
			removeBattlePanel();
			playerState.apply(currentLocation);
		}
		model.selectParagraph(currentLocation, locationListener);
		if (currentLocation.getDescription().length()==0 && playerListener != null) {
			playerListener.emptyDescription(currentLocation);
		}
		ArrayList<Paragraph> ids = new ArrayList<Paragraph>();
		
		TextGenerator text = new TextGenerator();
		
		if (playerListener == null) {
			//no monitoring
			playerState.getFullParagraphDescripton(currentLocation, ids,null,text); 
		} else {
			ArrayList<String> errors = new ArrayList<String>();
			playerState.getFullParagraphDescripton(currentLocation, ids,errors,text);
			if (errors.size()>0) {
				playerListener.showErrors(currentLocation,errors);
			}
		}
		//play sound effects
		if (audioEnabled) {
			if (currentLocation.hasSounds()) {
				Sound sound = currentLocation.getNextSound();
				model.playSound(sound);
			} else {
				model.stopSound();
			}
			if (currentLocation.hasBackgroundSounds()) {
				Sound sound = currentLocation.getNextBackgroundSound();
				if (playerState.getBackgroundSound() != sound) {
					playerState.setBackgroundSound(sound);
					model.playBackground(sound);
				}
			}
		} else {
			model.stopSound();
			model.stopBackgroundSound();
		}
		mainPanel.clear();
		if (detachPanels.size()>0) {
			for (Widget widget : detachPanels) {
				if (widget instanceof SpritePanel) {
					SpritePanel panel = (SpritePanel) widget;
					panel.onDetach();
				}
				widget.removeFromParent();
			}
			detachPanels.clear();
		}
		applyImage(null,true);
		if (graphEnabled) {
			if (currentLocation.hasTopImages()) {
				if (currentLocation.hasSprites()) {
					//sprites
					Picture topImage = currentLocation.getNextTopImage();
					new ComplexImageLoader(topImage,currentLocation.getSprites());
				} else {
					Image image = new Image();
					Picture topImage = currentLocation.getNextTopImage();
					new ImageLoadListener(topImage,image);
					applyImage(image,true);
				}
			} else if (currentLocation.hasBottomImages()==false) {
				nextImageFiller = nextImageFiller + 1;
				Picture filler = null;
				int l = model.getPictures().size();
				for (int i = nextImageFiller; i < l; i++) {
					Picture pkt = model.getPictures().get(i);
					if (pkt.isFiller()) {
						nextImageFiller = i;
						filler = pkt;
						break;
					}
				}
				if (filler==null) {
					for (int i = 0; i < l && i < nextImageFiller; i++) {
						Picture pkt = model.getPictures().get(i);
						if (pkt.isFiller()) {
							nextImageFiller = i;
							filler = pkt;
							break;
						}
					}
				}
				if (filler!=null) {
					//next filler
					Image image = new Image();
					new ImageLoadListener(filler,image);
					applyImage(image,true);
				}
			}
			if (currentLocation.hasBackgroundImages()) {
				Picture picture = currentLocation.getNextBackgroundImage();
				playerState.setBackground(picture);
				applyBackgroundPicture(picture);
			}
		} else {
			applyBackgroundPicture(null);
		}
		textParagraphPanel = text.getText();
		if (currentLocation.isFail() || currentLocation.isSuccess()) {
			//no alchemy
		} else if (battlePanel == null){
			//all applicable peace alchemy
			ArrayList<Alchemy> list = playerState.getAlchemy(false, true);
			if (list != null) {
				boolean addSeparator=true;
				for (Alchemy alchemy : list) {
					if (model.getSettings().isAddAlchemyToText() && alchemy.isOnDemand()) {
						//aleady in the text
						continue;
					}
					if (addSeparator) {
						HTML html = new HTML("<br/><br/>");
						html.getElement().getStyle().setDisplay(Display.INLINE);
						textParagraphPanel.add(html);
						addSeparator = false;
					}
					textParagraphPanel.add(new SpanHTML(alchemy.getName()));
					textParagraphPanel.add(new AlchemyLink(alchemy));
				}
			}
			if (updateConnectiosStatus(true)==false) {
				return;
			}
		} else {
			updateConnectiosStatus(true);
		}
		if (model.getSettings().getTextColor() != 0) {
			DOM.setStyleAttribute(textParagraphPanel.getElement(), "color", ColorProvider.getColorName(model.getSettings().getTextColor()));
		}
		mainPanel.add(textParagraphPanel);
		mainPanel.setCellHeight(textParagraphPanel,"1%");
		mainPanel.setCellWidth(textParagraphPanel,"100%");
		if (graphEnabled) {
			if (currentLocation.hasBottomImages()) {
				Image image = new Image();
				Picture bottomImage = currentLocation.getNextBottomImage();
				new ImageLoadListener(bottomImage,image);
				applyImage(image,false);
			}
		}
		if (battlePanel != null) {
			Widget widget = (Widget)battlePanel;
			mainPanel.add(widget);
//			mainPanel.setCellHeight(widget,"1%");
			mainPanel.setCellWidth(widget,"100%");
		}
		if (playerState.isFinished()) {
			//end of the game
			updateConnectiosStatus(false);
		}
		HTML html = new HTML("&nbsp;");
		html.setStyleName(PlayerStyles.FILLER);
		mainPanel.add(html);
		mainPanel.setCellHeight(html,"99%");
		mainPanel.setCellWidth(html,"100%");
		if (apply==false && battlePanel != null) {
			battlePanel.restore();
		}
//		scrollContainer.ensureVisible(mainPanel.getWidget(0));
	}
	
	private void applyImage(Widget image, boolean top) {
		if (image==null){
			//clear all images
			Element element = layout.getElement(PlayerLayout.IMAGE_TOP);
			if (element != null) {
				while (element.getChildCount()>0) {
					element.removeChild(element.getChild(0));
				}
			}
			element = layout.getElement(PlayerLayout.IMAGE_BOTTOM);
			if (element != null) {
				while (element.getChildCount()>0) {
					element.removeChild(element.getChild(0));
				}
			}
			element = layout.getElement(PlayerLayout.IMAGE);
			if (element != null) {
				while (element.getChildCount()>0) {
					element.removeChild(element.getChild(0));
				}
			}
		} else {
			Element element = layout.getElement(top ? PlayerLayout.IMAGE_TOP:PlayerLayout.IMAGE_BOTTOM);
			if (element == null) {
				element = layout.getElement(PlayerLayout.IMAGE);
				if (element == null) {
					mainPanel.add(image);
					mainPanel.setCellHeight(image,"1%");
					mainPanel.setCellHorizontalAlignment(image,HasHorizontalAlignment.ALIGN_CENTER);
					mainPanel.setCellWidth(image,"100%");
					return;
				} else {
					element.appendChild(image.getElement());
				}
			} else {
				element.appendChild(image.getElement());
			}
			if (image instanceof SpritePanel) {
				//free panel has to be attached
				SpritePanel panel = (SpritePanel) image;
				detachPanels.add(panel);
				panel.onAttach();
			}
		}
	}

	private void animateFeedback() {
		feedbackAnimator = new Timer(){
			private boolean tick;
			@Override
			public void run() {
				tick=(tick==false);
				if (tick) {
					if (playerState.getCurrentParagraph().isSuccess()) {
						feedback.setUrl(PlayImages.FEEDBACK_WELL);
					} else {
						feedback.setUrl(PlayImages.FEEDBACK_SAD);
					}
					layout.addStyle(PlayerLayout.FEEDBACK_BUTTON, PlayerLayout.BUTTON_ON_STYLE);
					layout.removeStyle(PlayerLayout.FEEDBACK_BUTTON, PlayerLayout.BUTTON_OFF_STYLE);
				} else {
					feedback.setUrl(PlayImages.FEEDBACK);
					layout.addStyle(PlayerLayout.FEEDBACK_BUTTON, PlayerLayout.BUTTON_OFF_STYLE);
					layout.removeStyle(PlayerLayout.FEEDBACK_BUTTON, PlayerLayout.BUTTON_ON_STYLE);
				}
			}
		};
		feedbackAnimator.scheduleRepeating(500);
	}

	private void stopFeedbackAnimation() {
		if (feedbackAnimator != null) {
			feedbackAnimator.cancel();
			feedbackAnimator = null;
			feedback.setUrl(PlayImages.FEEDBACK);
		}
	}
	
	
	public class AlchemyLink extends Image implements ClickHandler{
		private Alchemy alchemy;
		private boolean enabled=true;
		public AlchemyLink(Alchemy alchemy) {
			this.alchemy = alchemy;
			setEnabled(true);
			addClickHandler(this);
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
			if (enabled) {
				setUrl(PlayImages.SELECT);
				setStyleName(STYLE_CLICKABLE);
			} else {
				removeStyleName(STYLE_CLICKABLE);
				setUrl(PlayImages.SELECT_DISABLED);
			}
		}

		public void onClick(ClickEvent event) {
			if (enabled) {
				doAlchemy(alchemy);
			}
		}
		
	}
	public class TextLink extends Image implements ClickHandler{
		private Paragraph paragraph;
		private ParagraphConnection connection;
		private boolean enabled=true;
		public TextLink(Paragraph next, ParagraphConnection connection,boolean enable) {
			super();
			this.paragraph = next;
			this.connection = connection;
			setEnabled(enable);
			addClickHandler(this);
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
			if (enabled) {
				setUrl(PlayImages.SELECT);
				setStyleName(STYLE_CLICKABLE);
			} else {
				removeStyleName(STYLE_CLICKABLE);
				setUrl(PlayImages.SELECT_DISABLED);
			}
		}

		public void onClick(ClickEvent event) {
			if (enabled) {
				goLocation(paragraph);
			}
		}
		
	}

	public class TextGenerator implements ParagraphParsingHandler {
		
		private FlowPanel panel;
		
		public FlowPanel getText() {
			return panel;
		}

		public TextGenerator() {
			panel = new FlowPanel();
		}

		public void addLinkTo(Paragraph current, final Paragraph next, ParagraphConnection connection) {
			panel.add(new TextLink(next,connection,current.getBattle() == null));
		}

		public void addObject(Paragraph current,ObjectBean objectBean,String key) {
//			SpanLabel label = new SpanLabel(key);
//			panel.add(label);
		}

		public void addText(Paragraph current,String text) {
			int i = text.indexOf('\n');
			if (i<0) {
				SpanLabel label = new SpanLabel(text.replace('\r',' '));
				panel.add(label);
			} else {
				int pos = 0;
				while (i>=0) {
					if (pos<i) {
						//add text
						SpanLabel label = new SpanLabel(text.substring(pos,i).replace('\r',' '));
						panel.add(label);
						pos = i+1;
					} else {
						pos++;
					}
					HTML html = new HTML("<br/>");
					html.getElement().getStyle().setDisplay(Display.INLINE);
					panel.add(html);
					if (pos<text.length() && text.charAt(pos)=='\r') {
						pos++;
					}
					i = text.indexOf('\n',pos); 
				}
				SpanLabel label = new SpanLabel(text.substring(pos).replace('\r',' '));
				panel.add(label);
			}
		}

		public void addAlchemy(Paragraph paragraph, String toValue,Alchemy alchemy) {
			SpanLabel label = new SpanLabel(toValue);
			label.setStyleName(PlayerStyles.ALCHEMY_FROM_VALUE);
			panel.add(label);
			panel.add(new AlchemyLink(alchemy));
		}

		public void addBattle(Battle battle, Paragraph paragraph) {
		}

		public void addAlchemyFromValue(Paragraph paragraph, String value) {
			SpanLabel label = new SpanLabel(value);
			label.setStyleName(PlayerStyles.ALCHEMY_FROM_VALUE);
			panel.add(label);
		}
		
	}

	private void applyBackgroundPicture(Picture picture) {
		if (picture==null) {
			DOM.setStyleAttribute(mainPanel.getElement(),"backgroundImage","");
		} else {
			String url = "url(\""+picture.getUrl()+"\")";
			DOM.setStyleAttribute(mainPanel.getElement(),"backgroundImage",url);
			if (picture.isNoRepeat()) {
				DOM.setStyleAttribute(mainPanel.getElement(),"backgroundRepeat","no-repeat");
			}
			DOM.setStyleAttribute(mainPanel.getElement(),"backgroundPosition","center");
		}
	}
	
	public void doAlchemy(Alchemy alchemy) {
		playerState.apply(alchemy);
		updateConnectiosStatus(true);
	}

	private void goLocation(Paragraph location) {
		playerState.setCurrentParagraph(location);
		drawLocation(true);
	}
	
	public void onResize() {
		if (active) {
			if (scrollContainer != null) {
				scrollContainer.resetHeight();
			}
			if (bagContainer != null) {
				bagContainer.resetHeight();
			}
		} else {
			resizeOnActivation = true;
		}
	}

	public void activate() {
		if (resizeOnActivation) {
			resizeOnActivation = false;
			if (scrollContainer != null) {
				scrollContainer.resetHeight();
			}
			if (horizontalSplitPanel != null) {
				horizontalSplitPanel.activate();
			}
			if (bagContainer != null) {
				bagContainer.resetHeight();
			}
		}
		active = true;
	}

	public void deactivate() {
		active = false;
	}
	
	public class PopUpPlayerList extends PopupPanel{
		private ScrollContainer container;
		public PopUpPlayerList() {
			super(true,true);
			setStyleName(PlayerStyles.BAG_POPUP);
			VerticalPanel panel = new VerticalPanel();
			panel.setSpacing(5);
			panel.setSize("100%", "100%");
			setWidget(panel);
			container = new ScrollContainer();
			container.setScrollWidget(playerList);
			panel.add(container);
			panel.setCellHeight(container,"99%");
			panel.setCellWidth(container,"100%");
			Button button = new Button("Close",new ClickHandler() {
				public void onClick(ClickEvent event) {
					hide();
				}
			});
			panel.add(button);
			panel.setCellHeight(button,"99%");
			panel.setCellWidth(button,"100%");
			panel.setCellHorizontalAlignment(button,HasHorizontalAlignment.ALIGN_CENTER);
			centerAndShow();
		}

		private void centerAndShow() {
			int cw = mainPanel.getOffsetWidth(); 
			int w = cw -200;
			if (w<400) {
				w = 400;
			}
			int ch = Math.min(mainPanel.getOffsetHeight(),Window.getClientHeight()); 
			int h = ch-100;
			if (h<300) {
				h = 300;
			}
			setSize(String.valueOf(w)+"px", String.valueOf(h)+"px");
			setPopupPosition(mainPanel.getAbsoluteLeft()+(cw/2)-(w/2),mainPanel.getAbsoluteTop()+(ch/2)-(h/2));
			container.resetHeight();
			show();
		}

	}

	public void close() {
		stopFeedbackAnimation();
		model.removeParagraphListener(locationListener);
		model.removeParagraphConnectionListener(locationConnectionListener);
		model.stopSound();
		model.stopBackgroundSound();
	}
	
	public class ImageLoadListener implements LoadHandler {
		private Picture picture;
		private Image imagee;
		private boolean big;
		public ImageLoadListener(Picture picture,Image image) {
			this.picture = picture;
			this.imagee = image;
			image.addLoadHandler(this);
			if (picture.getBigUrl()==null || picture.getBigUrl().isEmpty()) {
				image.setUrl(picture.getUrl());
			} else {
				big = true;
				image.setUrl(picture.getBigUrl());
			}
		}

		public void onError(Widget sender) {
			sender.removeFromParent();
		}

		public void onLoad(LoadEvent event) {
			if (big) {
				picture.setBigWidht(imagee.getWidth());
				picture.setBigHeight(imagee.getHeight());
			} else {
				picture.setWidht(imagee.getWidth());
				picture.setHeight(imagee.getHeight());
			}
		}
		
	}
	
	public void enableFeedback(String url,boolean otherBooks) {
		feedbackURL = url;
		authorHasOtherBooks = otherBooks;
	}

	public void setAllBooksURL(String allBooksURL) {
		this.allBooksURL = allBooksURL; 
	}

	public void show(Widget panel) {
		mainPanel.clear();
		mainPanel.add(panel);
		mainPanel.setCellHeight(panel, "100%");
		mainPanel.setCellWidth(panel, "100%");
		mainPanel.setCellHorizontalAlignment(panel, HasHorizontalAlignment.ALIGN_CENTER);
		mainPanel.setCellVerticalAlignment(panel, HasVerticalAlignment.ALIGN_MIDDLE);
	}

	public void restore() {
		if (playerState.getCurrentParagraph()==null) {
			start();
		} else {
			drawLocation(false);
		}
	}


	public boolean isSupportRotation() {
		// TODO Auto-generated method stub
		return false;
	}


	public void rotate() {
		// TODO Auto-generated method stub
		
	}


	public void editCurrentParagraph() {
		// TODO Auto-generated method stub
		
	}


	public void restart() {
		// TODO Auto-generated method stub
		
	}


	public void save() {
		// TODO Auto-generated method stub
		
	}


	public void load() {
		// TODO Auto-generated method stub
		
	}


	public boolean isSupportSaveAndLoad() {
		// TODO Auto-generated method stub
		return false;
	}


	public void goCurrentParagraph() {
		// TODO Auto-generated method stub
		
	}


	public boolean isSupportScale() {
		return false;
	}


	public void scale(int scale) {
	}
	
	public boolean isSupportModel() {
		return true;
	}

	public void loadModule() {
	}
}