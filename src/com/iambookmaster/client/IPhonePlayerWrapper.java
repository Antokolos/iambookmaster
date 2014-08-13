package com.iambookmaster.client;

import com.google.code.gwt.database.client.service.ListCallback;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.code.gwt.database.client.service.VoidCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.common.EditorPlayer;
import com.iambookmaster.client.common.FileExchangeClient;
import com.iambookmaster.client.iphone.IPhonePlayerListener;
import com.iambookmaster.client.iphone.IPhoneViewerOldBook;
import com.iambookmaster.client.iphone.data.IPhoneDataService;
import com.iambookmaster.client.iphone.data.IPhoneFileBean;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.player.PlayerListener;
import com.iambookmaster.client.player.PlayerStyles;

public class IPhonePlayerWrapper extends VerticalPanel implements EditorPlayer {
	
	static AppConstants appConstants = AppLocale.getAppConstants();
	static AppMessages appMessages = AppLocale.getAppMessages();
	static final FileExchangeClient fileExchange = new FileExchangeClient();
	static final int WIDHT = 320;
	private static final int HEIGHT = 480;
	
	final IPhoneViewerOldBook player;
	private AbsolutePanel canvas;
	private Image image1;
	private Image image2;
	private Image rotate;
	int width= WIDHT;
	private int height= HEIGHT;
	private boolean activated;
	Model model;

	private PlayerMasterMenu locationMenuPanel;

	protected String lastGameState;
	protected Timer timer;
	
	public IPhonePlayerWrapper(Model md, PlayerListener listener, int deviceWidth, int deviceHheight) {
		this.model = md;
		setSize("100%", "100%");
		canvas = new AbsolutePanel();
		canvas.setSize("729px", "729px");
		add(canvas);
		image1 = new Image();
		image1.getElement().getStyle().setProperty("cursor", "url(images/cursor-up.ico),default");
		image1.setTitle(appConstants.playerIphoneScrollUp());
		image1.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				player.scrollDown();
			}
		});
		canvas.add(image1,0,0);
		image2 = new Image();
		image2.getElement().getStyle().setProperty("cursor", "url(images/cursor-down.ico),default");
		image2.setTitle(appConstants.playerIphoneScrollDown());
		image2.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				player.scrollUp();
			}
		});
		canvas.add(image2,0,0);
		player = new IPhoneViewerOldBook() {
			@Override
			protected int getClientWidth() {
				return width;
			}

			@Override
			protected int getClientHeight() {
				return height;
			}
			
		};
		try {
			player.init();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		canvas.add(player.getLayout(),0,0);
		rotate = new Image(Images.IPHONE_SETTINGS);
		rotate.setStyleName(Styles.CLICKABLE);
		rotate.setTitle(appConstants.playerIphoneOptions());
		rotate.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				locationMenuPanel.setPopupPosition(Event.getCurrentEvent().getClientX(),Event.getCurrentEvent().getClientY());
				locationMenuPanel.show();
			}
		});
		canvas.add(rotate,0,0);
		applyLayout(true);
		locationMenuPanel = new PlayerMasterMenu(this);
	}

//	private void startTimer(final boolean direction) {
//		timer = new Timer() {
//			@Override
//			public void run() {
//				if (direction) {
//					player.scrollDown();
//				} else {
//					player.scrollUp();
//				}
//			}
//		};
//		timer.scheduleRepeating(200);
//	}
//
//	private void clearTimer() {
//		if (timer != null) {
//			timer.cancel();
//			timer = null;
//		}
//	}

	void applyLayout(boolean horizontal) {
		if (horizontal) {
			image1.setUrl(Images.IPHONE_HORIZONTAL_TOP);
			image2.setUrl(Images.IPHONE_HORIZONTAL_BOTTOM);
			width = HEIGHT;
			height = WIDHT;
			canvas.setWidgetPosition(image2,-1,190);
			canvas.setWidgetPosition(player.getLayout(),131,30);
		} else {
			image1.setUrl(Images.IPHONE_VERTICAL_LEFT);
			image2.setUrl(Images.IPHONE_VERTICAL_RIGHT);
			width = WIDHT;
			height = HEIGHT;
			canvas.setWidgetPosition(image2,0,363);
			canvas.setWidgetPosition(player.getLayout(),30,131);
		}
		player.getLayout().setSize(IPhoneViewerOldBook.toPixels(width), IPhoneViewerOldBook.toPixels(height));
		player.onResize();
	}

	public void deactivate() {
		// TODO Auto-generated method stub
		
	}

	public void activate() {
		if (activated==false) {
			activated = true;
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					player.setStyleName(PlayerStyles.IPHONE);
					loadPlayer();
				}
			});
		}
	}

	private void loadPlayer() {
		player.loadPlayer(model, new IPhoneDataService() {
			
			@Override
			public void storeState(String state) {
				lastGameState=state;
			}
			
			@Override
			public void loadLastState(Model model, ScalarCallback<String> callback) {
			}

			@Override
			public void donate(VoidCallback callback) {
			}

			@Override
			public void calculateDonate(ScalarCallback<String> callback) {
			}

			@Override
			public void selectAvailableFiles(String exention, ListCallback<IPhoneFileBean> callback) {
				
			}

			@Override
			public void loadSingleFile(String name, ScalarCallback<String> callback) {
				// TODO Auto-generated method stub
				
			}
		}, new IPhonePlayerListener() {
			public boolean onParagraph(Paragraph paragraph) {
				return true;
			}
			
			public boolean onOpenPlayerList() {
				return true;
			}
			
			public boolean onOpenFeedback() {
				return true;
			}
		});
	}

	public void close() {
		player.close();
	}

	public void start() {
	}

	public boolean isSupportRotation() {
		return true;
	}

	public void rotate() {
		applyLayout(width == IPhonePlayerWrapper.WIDHT);
	}

	public void editCurrentParagraph() {
		model.editParagraph(player.getPlayerState().getCurrentParagraph(), null);
	}

	public void restart() {
		lastGameState=null;
		player.getPlayer().start();
	}

	public void save() {
		if (lastGameState != null) {
			fileExchange.saveFile(lastGameState,IPhonePlayerWrapper.appConstants.playerSavedGame());
		}
	}

	public void load() {
		String text = fileExchange.loadFile(IPhonePlayerWrapper.appConstants.playerRestoreGame());
		if (text != null) {
			player.getPlayer().restoreGame(text);
		}
	}

	public boolean isSupportSaveAndLoad() {
		return fileExchange.checkApplet();		
	}

	public void goCurrentParagraph() {
		player.getPlayer().setCurrentParagraph(model.getCurrentParagraph());
		
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
