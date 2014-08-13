package com.iambookmaster.client.iphone;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import com.google.code.gwt.database.client.service.DataServiceException;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
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
import com.iambookmaster.client.iphone.common.IPhoneButton;
import com.iambookmaster.client.iphone.data.IPhoneDataService;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.ContentPlayer;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.player.PlayerState;
import com.iambookmaster.client.player.PlayerStateListener;

public class IPhonePlayer {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	private static final AppMessages appMessages = AppLocale.getAppMessages();

	private final IPhoneDataService dbService;

	private static final IPhoneStyles css = IPhoneImages.INSTANCE.css();

	private static final ContentPlayer contentPlayer = GWT.create(ContentPlayer.class);

	private PlayerState playerState;
	private Model model;
	private IPhoneCanvas canvas;
	private IPhoneViewListener viewListener;
	private TextGenerator textGenerator;
	private IPhoneBattlePanel battlePanel;
	private IPhonePlayerList playerList;
	private Sound backgroundSound;
	private boolean playingSound;
	protected boolean playerListAvailable;
	private ClickHandler restartHandler;
	private ClickHandler restoreHandler;
	private ClickHandler feedbackHandler;
	private boolean firstAdventureListCall = true;
	public boolean showAdventureListCall;
	private IPhonePlayerListener playerListener;
	private ArrayList<Picture> imageFillers;
	private int imageFillerIndex;
	private Picture fillerImage;
	private HashSet<ParagraphConnection> vitalConnections;
	public boolean battleActive;
	public ClickHandler buyFullVersionHandler;
	public ClickHandler donateHandler;
	public ClickHandler goToFullVersionHandler;
	private IPhoneDebugPanel debugPanel;
	protected IPhoneDebugPanelListener debugPanelListener;

	public IPhonePlayer(IPhoneCanvas viewCanvas) {
		dbService = IPhoneDataService.getInstance();
		init();
		canvas = viewCanvas;
		startLoading();
	}

	public IPhonePlayer(IPhoneCanvas viewCanvas, Model model, IPhoneDataService dataService,
			IPhonePlayerListener playerListener) {
		dbService = dataService;
		this.playerListener = playerListener;
		// init();
		canvas = viewCanvas;
		this.model = model;
		initialize(false);
	}

	private native void init()/*-{
		var self = this;
		//		$wnd.resumeMusic = function() {self.@com.iambookmaster.client.iphone.IPhonePlayer::resumeMusic()();;}
	}-*/;

	private void startLoading() {
		IPhoneModelLoader loader = GWT.create(IPhoneModelLoader.class);
		loader.loadModel(new IPhoneModelLoaderListener() {
			public void error(Throwable throwable) {
				IPhoneConsole.showError(throwable);
			}

			public void success(Model mod) {
				model = mod;
				initialize(true);
			}

		}, new Model(appConstants, appMessages));
	}

	// private void finishGame() {
	// updateConnectiosStatus(false);
	// finished = true;
	// }

	private void initialize(boolean loadState) {
		imageFillers = new ArrayList<Picture>();
		for (Picture picture : model.getPictures()) {
			if (picture.isFiller()) {
				imageFillers.add(picture);
			}
		}
		playerListAvailable = model.getSettings().getPlayerListType() != Model.PLAYER_LIST_TYPE_NONE;
		model.setContentPlayer(contentPlayer);
		// buttonState.setPlayerList(model.getSettings().getPlayerListType() !=
		// Model.PLAYER_LIST_TYPE_NONE);
		playerState = new PlayerState(model, appConstants, appMessages);
		playerState.setAllowAudio(model.getSettings().isDisableAudio() == false);
		playerState.setAllowImages(true);

		//TODO for test env. only
//		debugPanel = new IPhoneDebugPanel(playerState);
		debugPanelListener = new IPhoneDebugPanelListener() {
			public void close() {
				drawLocation(false, true);
			}
		};
		
		playerState.addPlayerStateListener(new PlayerStateListener() {

			private IPhoneBattlePanelListner battleListener = new IPhoneBattlePanelListner() {
				public void redraw(boolean animate) {
					// save current battle state
					String state = playerState.saveState(false);
					dbService.storeState(state);
					drawLocation(false, animate);
				}

				public boolean isVertical() {
					return canvas.isVertical();
				}
			};

			public void addObject(ObjectBean object) {
				showAdventureListCall = firstAdventureListCall;
			}

			public void battle(Battle battle, boolean start) {
				if (start) {
					battleActive = true;
					battlePanel = new IPhoneBattlePanel(playerState, battleListener);
					// battleStarted = true;
				} else {
					battleActive = false;
					// drawLocation(false,true);
					// updateConnectiosStatus(true);
					// end battle,
				}
			}

			public void changeModificator(Modificator parameter, boolean value) {
				if (model.getSettings().isShowModificators()) {
					showAdventureListCall = firstAdventureListCall;
				}
			}

			public void changeParameter(Parameter parameter, int value) {
				if (parameter.isVital() && value <= 0) {
					IPhoneMessage.showMessage(appMessages.urqHeroDiedByVitalParameter(parameter.getName()));
				}
				showAdventureListCall = firstAdventureListCall;
			}

			public void disableConnection(ParagraphConnection connection) {
				// enableOneConnection(connection,false);
			}

			public void enableConnection(ParagraphConnection connection) {
				// enableOneConnection(connection,true);
			}

			public void enemy(NPC npc, boolean add) {
			}

			public void finish() {
				battleActive = false;
				// finishGame();
			}

			public void lostObject(ObjectBean object) {
				showAdventureListCall = firstAdventureListCall;
			}

			public void removeObject(ObjectBean object) {
			}

			public void reset() {
			}

			public void useObject(ObjectBean object, boolean success) {
				if (success) {
					drawLocation(true, true);
				} else {
					showMessage(object.getNextMissusedMessage(appConstants));
				}
			}

		});

		viewListener = new IPhoneViewListenerAdapter() {

			public void redraw(IPhoneCanvas viewer) {
				drawLocation(false, false);
			}

			@Override
			public void forward() {
				if (playerState.isFinished()) {
//					showFeedbackPanel(false);
				} else if (battlePanel != null && battleActive) {
					battlePanel.attack();
				} else if (textGenerator != null && textGenerator.links.size() == 1) {
					textGenerator.links.get(0).onClick(null);
				}
			}

			@Override
			public void back() {
				if (playerState.isFinished()) {
					// open feedback
					showDebugPanel();
//					showFeedbackPanel(true);
				} else if (model.getSettings().getPlayerListType() != Model.PLAYER_LIST_TYPE_NONE) {
					openPlayerList(true);
				} else {
					showDebugPanel();
				}
			}

		};

		playerList = new IPhonePlayerList(model, playerState, canvas, new IPhonePlayerListListener() {
			public void back() {
				showDebugPanel();
			}

			public void forward() {
				drawLocation(false, true);
			}
		});

		restartHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				start();
			}
		};
		buyFullVersionHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				IPhoneFeedbackPanel.buyFullVersion(model);
			}
		};
		donateHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				IPhoneThankyouPanel panel = new IPhoneThankyouPanel(model,dbService);
				panel.show(new IPhoneThankyouPanelListener() {
					public void close() {
						drawLocation(false, true);
					}
				}, canvas, false);
			}
		};
		goToFullVersionHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				//run automatically
				playerState.getMetadata().setAutorun(true);
				String json = playerState.saveState(true);
				//restore
				playerState.getMetadata().setAutorun(false);
				IPhoneFeedbackPanel.goToFullVersion(model,json);
			}
		};
		
		restoreHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				String data = playerState.getStateFromHistory();
				if (data != null) {
					restoreGame(data);
				}
			}
		};
		feedbackHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				showFeedbackPanel(false);
			}
		};

		textGenerator = new TextGenerator();

		if (loadState) {
			dbService.loadLastState(model, new ScalarCallback<String>() {
				public void onFailure(DataServiceException error) {
					
//					Window.alert("onFailure");
					
					showIntroScreen(false);
				}

				public void onSuccess(String data) {
					playerState.restoreState(data);
					// we have saved state, ask user
//					history[history.length - 1] = playerState.saveState();
					
//					Window.alert("onSuccess="+playerState.getMetadata().isAutorun());
					
					if (playerState.getMetadata().isAutorun()) {
						//run game now
						playerState.getMetadata().setAutorun(false);
						model.stopSound();
						model.stopBackgroundSound();
						drawLocation(true, true);
						if (playerState.isAllowAudio()) {
							// play sound effects
							processAudio(true);
						}
					} else {
						showIntroScreen(true);
					}
				}
			});
		} else {
			playerState.setAllowAudio(model.getSettings().isDisableAudio() == false);
			start();
		}
	}

	private void showDebugPanel() {
		if (debugPanel != null) {
			debugPanel.show(debugPanelListener, canvas, false);						
		}
	}

	public void restoreGame(String data) {
		playerState.restoreState(data);
		drawLocation(false, true);
		dbService.storeState(data);
	}

	protected void showIntroScreen(boolean hasContinue) {
		if (model.getSettings().isDisableAudio()) {
			playerState.setAllowAudio(false);
		}
		
		IPhoneIntroScreen introScreen = new IPhoneIntroScreen(new IPhoneIntroScreenListener() {

			public void start() {
				IPhonePlayer.this.start();
			}

			public void continueGame() {
				model.stopSound();
				model.stopBackgroundSound();
				drawLocation(false, true);
				if (playerState.isAllowAudio()) {
					// play sound effects
					processAudio(true);
				}
			}

		}, model, playerState,dbService);
		introScreen.show(hasContinue, canvas);
		new Timer() {
			@Override
			public void run() {
				dbService.removeSplashScreen();
			}
		}.schedule(1000);
			
	}

	private void showFeedbackPanel(boolean leftToRight) {
		if (playerListener == null || playerListener.onOpenFeedback()) {
			IPhoneFeedbackPanel feedbackPanel = new IPhoneFeedbackPanel(model,dbService);
			feedbackPanel.show(new IPhoneFeedbackPanelListener() {
				public void close() {
					drawLocation(false, true);
				}
			}, canvas, leftToRight);
		}
	}

	private void openPlayerList(boolean leftToRigth) {
		if (playerListener == null || playerListener.onOpenPlayerList()) {
			if (playerState.hasObjects()) {
				firstAdventureListCall = false;
			}
			playerList.show(viewListener, leftToRigth);
		}
	}

	// private void openMainMenu() {
	// if (mainMenu==null) {
	// mainMenu = new IPhoneMainMenu(null,new IPhoneMainMenuListener() {
	// public void close() {
	// if (playerListAvailable) {
	// //open player list
	// openPlayerList();
	// } else {
	// drawLocation(false);
	// }
	// }
	//
	// public boolean isAudioAvailable() {
	// return playerState.isAllowAudio();
	// }
	//
	// public boolean isAudioEnabled() {
	// return model.getSettings().isDisableAudio()==false;
	// }
	//
	// public boolean isSaveEnabled() {
	// return false;
	// }
	//
	// public boolean isLoadEnabled() {
	// return false;
	// }
	//
	// public void forward() {
	// if (playerListAvailable) {
	// //open player list
	// openPlayerList();
	// } else {
	// drawLocation(false);
	// }
	// }
	//
	// public void back() {
	// drawLocation(false);
	// }
	//
	// });
	// }
	// mainMenu.show(canvas);
	// }

	protected void showMessage(String message) {
		IPhoneMessage.showMessage(message);
	}

	public void start() {
		model.stopSound();
		model.stopBackgroundSound();
		playerState.reset();
		if (playerState.getCurrentParagraph() == null) {
			throw new IllegalArgumentException("No Start Paragraph");
		}
		drawLocation(true, true);
	}

	// private String getGameId() {
	// return model.getGameKey()==null || model.getGameKey().length()<5 ?
	// model.getGameId() : model.getGameKey();
	// }

	private void drawLocation(boolean apply, boolean animation) {
		final Paragraph currentLocation = playerState.getCurrentParagraph();
		if (playerListener != null && playerListener.onParagraph(currentLocation) == false) {
			// nothing
			return;
		}
		// start animation
		showAdventureListCall = false;
		// battleStarted = false;

		if (apply) {
			battlePanel = null;
			playerState.apply(currentLocation);
		}
		textGenerator.clear(currentLocation);
		playerState.getFullParagraphDescripton(currentLocation, null, textGenerator, null);

		if (textGenerator.isMustGo() && model.getSettings().isSkipMustGoParagraphs()) {
			// skip it
			playerState.setCurrentParagraph(textGenerator.getNext());
			drawLocation(apply, animation);
			return;
		}

		// if (apply) {
		// if (battleStarted) {
		// return;
		// }
		// }

		canvas.setListener(viewListener);
		// ArrayList<Paragraph> ids = new ArrayList<Paragraph>();

		if (animation) {
			canvas.clearWithAnimation(false);
			if (model.getSettings().getPlayerListType() == Model.PLAYER_LIST_TYPE_NONE) {
				// roll pages
				canvas.changePageOrientation();
			} else {
				// always on the right page
				canvas.setPageOrientation(false);
			}
		} else {
			canvas.clear();
		}
		if (playerState.isAllowAudio()) {
			// play sound effects
			processAudio(apply);
		}

		// apply images
		if (currentLocation.hasTopImages()) {
			Picture topImage = currentLocation.getNextTopImage();
			if (currentLocation.hasSprites()) {
				// sprites
				// new
				// ComplexImageLoader(topImage,currentLocation.getSprites());
			} else {
				canvas.setImage(topImage.getUrl());
			}
		} else if (imageFillers.isEmpty() == false) {
			if (apply || fillerImage == null) {
				imageFillerIndex++;
				if (imageFillerIndex >= imageFillers.size()) {
					imageFillerIndex = 0;
				}
				fillerImage = imageFillers.get(imageFillerIndex);
			}
			canvas.setImage(fillerImage.getUrl());

		}
		if (currentLocation.hasBackgroundImages()) {
			Picture back = currentLocation.getNextBackgroundImage();
			canvas.setBackgroundImage(back.getUrl());
		}
		// no monitoring
		textGenerator.done();
		canvas.done();
		if (apply && currentLocation != model.getStartParagraph()) {
			// do not remember the first paragraph
			if (currentLocation.isFail() || playerState.isHeroAlive() == false) {
				// for fail location - save 5 steps back
				String data = playerState.getStateFromHistory();
				if (data != null) {
					// save previous step
					dbService.storeState(data);
				}
			} else {
				String state = playerState.saveState(true);
				dbService.storeState(state);
			}
		}
	}

	private void processAudio(boolean apply) {
		// if (currentLocation.hasSounds() && apply) {
		// Sound sound = currentLocation.getNextSound();
		// model.playSound(sound);
		// playingSound = currentLocation.hasBackgroundSounds();
		// } else if (currentLocation.hasBackgroundSounds()) {
		// playingSound = false;
		// Sound sound = currentLocation.getNextBackgroundSound();
		// if (sound != backgroundSound) {
		// backgroundSound = sound;
		// model.playBackground(sound);
		// }
		// } else {
		// playingSound = false;
		// model.stopSound();
		// }
	}

	// protected void enableOneConnection(ParagraphConnection connection) {
	// for (IPhoneParagraphTextGenerator.Link widget: textGenerator.links) {
	// if (widget instanceof TextLink) {
	// TextLink link = (TextLink) widget;
	// link.setEnabled(link.connection==connection);
	// }
	// }
	// }

	// protected boolean updateConnectiosStatus(boolean enableAll) {
	// for (IPhoneParagraphTextGenerator.Link widget: textGenerator.links) {
	// if (widget instanceof AlchemyLink) {
	// AlchemyLink link = (AlchemyLink) widget;
	// if (enableAll) {
	// link.setEnabled(playerState.meetsCondition(link.alchemy,false));
	// } else {
	// link.setEnabled(false);
	// }
	//
	// } else if (widget instanceof TextLink) {
	// TextLink link = (TextLink) widget;
	// if (enableAll) {
	// ParagraphConnection connection = link.connection;
	// if (connection.getStrictness() == ParagraphConnection.STRICTNESS_MUST) {
	// if (connection.isConditional()==false ||
	// playerState.meetsCondition(connection)) {
	// //only this connection can be used
	// if (model.getSettings().isSkipMustGoParagraphs()) {
	// goLocation(connection.getTo());
	// return false;
	// }
	// for (IPhoneParagraphTextGenerator.Link widget2: textGenerator.links) {
	// if (widget2==widget) {
	// break;
	// }
	// widget2.setEnabled(false);
	// }
	// enableAll = false;
	// } else {
	// //does not match the condition
	// link.setEnabled(false);
	// continue;
	// }
	// } else if (connection.getStrictness() ==
	// ParagraphConnection.STRICTNESS_MUST_NOT) {
	// if (playerState.meetsCondition(connection)) {
	// link.setEnabled(false);
	// continue;
	// }
	// } else if (connection.isConditional()) {
	// link.setEnabled(playerState.meetsCondition(connection));
	// continue;
	// }
	// link.setEnabled(true);
	// } else {
	// link.setEnabled(false);
	// }
	// }
	// }
	// return true;
	// }

	private void goLocation(Paragraph location) {
		playerState.setCurrentParagraph(location);
		drawLocation(true, true);
	}

	public class AlchemyLink extends IPhoneParagraphTextGenerator.Link {
		private Widget bigWidget;
//		private SpanLabel clicker;
//		private SpanLabel clicker2;
		private Alchemy alchemy;

		public AlchemyLink(Alchemy alchemy, boolean meet, int counter) {
			this.alchemy = alchemy;
			String color = IPhoneButton.getColorValue(counter);
			String title = appMessages.iphoneChoice(counter);
			setText(title);
			getElement().getStyle().setBackgroundColor(color);
			setStyleName(css.choiceNormal());

			if (meet) {
				bigWidget = new IPhoneButton(counter,alchemy.getName(),this);
//				HorizontalPanel panel = new HorizontalPanel();
//				clicker = new SpanLabel(title);
//				clicker.addStyleName(css.choiceBigWidget());
//				clicker.getElement().getStyle().setBackgroundColor(color);
//				clicker.addClickHandler(this);
//				panel.addStyleName(css.choiceBigWidgetBorder());
//				panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
//				panel.add(clicker);
//				clicker2 = new SpanLabel(alchemy.getName(), false);
//				clicker2.addClickHandler(this);
//				clicker2.setStyleName(css.choiceBigWidgetText());
//				panel.add(clicker2);
//				if (canvas.isVertical() == false) {
//					// no text or horizontal orientation - some widgets in a
//					// line
//					panel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
//				}
//				bigWidget = panel;
			}
		}

		public void onClick(ClickEvent event) {
			if (playerState.apply(alchemy)) {
				drawLocation(false, true);
			}
		}

		@Override
		public void addBigWidget() {
			if (bigWidget != null) {
				canvas.add(bigWidget);
				canvas.addClickHandler(this, this);
//				canvas.addClickHandler(clicker2, this);
			}
		}

		@Override
		public void setEnabled(boolean enabled) {
			// TODO
			
		}

	}

//	private static final String[] colors = new String[] { 
//			"rgb(89,183,72)", "rgb(80,166,175)", "rgb(159,163,92)",
//			"rgb(203,168,52)", "rgb(0,0,0)", "rgb(194,124,197)", 
//			"rgb(255,67,71)", "rgb(84,41,24)" };

	public class TextLink extends IPhoneParagraphTextGenerator.Link {
		private Paragraph paragraph;
		private ParagraphConnection connection;
		// private boolean enabled=true;
		private Widget bigWidget;
//		private Widget clicker;
//		private Widget clicker2;
		private String nextName;
		private int number;

		public TextLink(Paragraph next, ParagraphConnection connection, boolean enabled, int number) {
			this.paragraph = next;
			this.connection = connection;
			this.number = number;
			String color = IPhoneButton.getColorValue(number);
			setText(appMessages.iphoneChoice(number));
			getElement().getStyle().setBackgroundColor(color);
			setStyleName(css.choiceNormal());
			addClickHandler(this);
			if (connection.getTo() == next) {
				// forward
				nextName = connection.getNameFrom();
			} else {
				// reverse
				nextName = connection.getNameTo();
			}
			setEnabled(enabled);
		}

		public void onClick(ClickEvent event) {
			goLocation(paragraph);
		}

		@Override
		public void addBigWidget() {
			if (bigWidget != null) {
				canvas.add(bigWidget);
				canvas.addClickHandler(bigWidget, this);
			}
		}

		public String getNextName() {
			return nextName;
		}

		@Override
		public ParagraphConnection getConnection() {
			return connection;
		}

		@Override
		public void setEnabled(boolean enabled) {
			if (enabled) {
				bigWidget = new IPhoneButton(number,nextName,this);
			} else {
				bigWidget = null;				
			}
		}

	}

	public class TextGenerator extends IPhoneParagraphTextGenerator {

		public TextGenerator() {
			super(playerState);
		}

		public void addBattle(Battle battle, Paragraph paragraph) {
			if (battlePanel != null) {
				widgets.add(battlePanel.createBattleWidget(battleActive));
			}
		}

		public void done() {
			for (Widget widget : widgets) {
				canvas.add(widget);
			}
			if (battlePanel != null && battleActive) {
				battlePanel.addListeners(canvas);
				for (Iterator<IPhoneParagraphTextGenerator.Link> iterator = links.iterator(); iterator.hasNext();) {
					Link link = iterator.next();
					ParagraphConnection connection = link.getConnection();
					if (connection == null) {
						iterator.remove();
					} else if (playerState.meetsCondition(connection)==false){
//						if (playerState.alwaysVisible(connection)) {
							iterator.remove();
//						}
					}

				}
			}
			if (playerListAvailable && showAdventureListCall) {
				// show notification about existence of Adventure List
				HTML html = new HTML("<br/>");
				html.getElement().getStyle().setDisplay(Display.INLINE);
				canvas.add(html);
				canvas.add(new IPhoneAdventureListAnimation());
			}

			if (playerState.isFinished()) {
				HTML html = new HTML("<br/>");
				html.getElement().getStyle().setDisplay(Display.INLINE);
				canvas.add(html);

//				Label label;
				IPhoneButton button;
				if (playerState.getCurrentParagraph().isFail() || !playerState.isHeroAlive()) {
					//game over
					if (playerState.isCanRestoreFromHistory()) {
//						label = new Label(appConstants.iphoneRestoreFailedGame());
//						label.addClickHandler(restoreHandler);
//						label.setStyleName(css.stateSelection());
//						applyLableStyle(label);
						button = new IPhoneButton(appConstants.iphoneRestoreFailedGame(), restoreHandler);
						canvas.add(button);
						canvas.addClickHandler(button, restoreHandler);
					}
				} 
				if (playerState.getCurrentParagraph().isSuccess() && 
					model.getSettings().isDemoVersion() && 
					playerState.getCurrentParagraph().isCommercial()){
					//end of demo-game
					if (dbService.isLinkedVersionPresent()) {
						Label label = new HTML(appConstants.iphoneGoToFullVersionText());
						canvas.add(label);
						button = new IPhoneButton(appConstants.iphoneGoToFullVersion(), goToFullVersionHandler);
						canvas.addClickHandler(button, goToFullVersionHandler);
					} else {
						Label label = new HTML(appConstants.commercialTextDefault());
						canvas.add(label);
						button = new IPhoneButton(appConstants.iphoneBuyFullVersion(), buyFullVersionHandler);
						canvas.addClickHandler(button, buyFullVersionHandler);
					}
					canvas.add(button);
				} else {
					button = new IPhoneButton(appConstants.iphoneStartNewGame(), restartHandler);
					canvas.add(button);
					canvas.addClickHandler(button, restartHandler);
				}
//				label = new Label(appConstants.iphoneStartNewGame());
//				label.addClickHandler(restartHandler);
//				label.setStyleName(css.stateSelection());
//				applyLableStyle(label);
//				canvas.add(label);
//				canvas.addClickHandler(label, restartHandler);
				button = new IPhoneButton(appConstants.iphoneViewMore(), feedbackHandler);
				canvas.add(button);
				canvas.addClickHandler(button, feedbackHandler);
				
				if (dbService.isInAppAvailable()) {
					Label label = new Label(appConstants.iphoneDonationText());
					canvas.add(label);
					button = new IPhoneButton(appConstants.iphoneDonation(), donateHandler);
					canvas.add(button);
					canvas.addClickHandler(button, donateHandler);
				}
				
//				label = new Label(appConstants.iphoneViewMore());
//				label.addClickHandler(feedbackHandler);
//				label.setStyleName(css.stateSelection());
//				applyLableStyle(label);
//				canvas.add(label);
//				canvas.addClickHandler(label, feedbackHandler);

			} else {
				if (links.size() > 0) {
					//check for Alchemy
					if (battlePanel == null || !battleActive) {
						ArrayList<Alchemy> list = playerState.getAlchemy(false, true);
						if (list != null) {
//							boolean addSeparator=true;
							for (Alchemy alchemy : list) {
								if ((alchemy.isOnDemand()==false) || model.getSettings().isAddAlchemyToText()) {
									//in adventurer list
									continue;
								}
								if (playerState.meetsCondition(alchemy, false)==false) {
									//cannot be used now
									continue;
								}
//								if (addSeparator) {
//									//TODO
//									addSeparator = false;
//								}
								Link link = new AlchemyLink(alchemy, true, getNextCounter());
//								widgets.add(link);
								links.add(link);
							}
						}
					}
					
					if (links.size() == 1 && (battlePanel == null || !battleActive)) {
						IPhoneParagraphTextGenerator.Link single = links.get(0);
						canvas.removeWidget(single);
//						HTML html = new HTML("&nbsp;");
//						html.getElement().getStyle().setFontSize(1, Unit.PX);
//						canvas.add(html);
//						NextWidget widget = new NextWidget(single);
//	//					widget.setColor(1);
//						canvas.add(widget);
//						canvas.addClickHandler(widget, widget);
					} else {
//						for (IPhoneParagraphTextGenerator.Link link : links) {
//							if (link.isHasBigWidget()) {
//								
//							}
//						}
//						HTML html = new HTML(appConstants.iphoneYourChoice());
//						html.setStyleName(css.choiceTitle());
//						canvas.add(html);

						for (IPhoneParagraphTextGenerator.Link link : links) {
//							canvas.add(link);
							canvas.addClickHandler(link, link);
//							link.addBigWidget();
						}
					}
				}
			}
		}

//		private void applyLableStyle(Label label) {
//			if (canvas.isVertical() == false) {
//				label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
//			}
//		}

		@Override
		protected Link createTextLink(Paragraph next, ParagraphConnection connection, boolean enabled) {
			return new TextLink(next, connection, enabled, getNextCounter());
		}

		@Override
		public void addAlchemy(Paragraph paragraph, String value, Alchemy alchemy) {
			super.addAlchemy(paragraph, value, alchemy);
			if (battleActive || mustGo) {
				// no condition check anymore
				return;
			}
			boolean meet = playerState.meetsCondition(alchemy, false);
			Link link = new AlchemyLink(alchemy, meet, getNextCounter());
			widgets.add(link);
			links.add(link);
		}

	}

	public class NextWidget extends IPhoneButton implements ClickHandler {
		private IPhoneParagraphTextGenerator.Link link;

		public NextWidget(IPhoneParagraphTextGenerator.Link link) {
//			setWordWrap(false);
			super(link.getNextName() == null || link.getNextName().length()==0 ? appConstants.iphoneContinue() : link.getNextName());
//			if (link.getNextName() == null) {
//				setText(appConstants.iphoneContinue());
//			} else {
//				setText(link.getNextName());
//			}
//			setStyleName(css.stateSelection());
//			getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			this.link = link;
			addClickHandler(this);
		}

		public void onClick(ClickEvent event) {
			link.onClick(event);
		}

	}

	public void setCurrentParagraph(Paragraph currentParagraph) {
		playerState.setCurrentParagraph(currentParagraph);
		drawLocation(true,true);
	}

	public PlayerState getPlayerState() {
		return playerState;
	}

}
