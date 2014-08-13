package com.iambookmaster.client;

import com.google.code.gwt.database.client.service.ListCallback;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.code.gwt.database.client.service.VoidCallback;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.common.EditorPlayer;
import com.iambookmaster.client.common.FileExchangeClient;
import com.iambookmaster.client.iphone.IPhonePlayerListener;
import com.iambookmaster.client.iphone.IPhoneViewerBook;
import com.iambookmaster.client.iphone.IPhoneViewerOldBook;
import com.iambookmaster.client.iphone.data.IPhoneDataService;
import com.iambookmaster.client.iphone.data.IPhoneFileBean;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.player.PlayerListener;
import com.iambookmaster.client.player.PlayerStyles;

public class IPadPlayerWrapper extends VerticalPanel implements EditorPlayer {
	
	static AppConstants appConstants = AppLocale.getAppConstants();
	static final FileExchangeClient fileExchange = new FileExchangeClient();
	
	final IPhoneViewerBook player;
	private AbsolutePanel canvas;
	private AbsolutePanel wrapper;
	private Image image1;
	private Image image2;
	private Image rotate;
	private int height;
	private int width;
	private boolean activated;
	Model model;

	private PlayerMasterMenu locationMenuPanel;

	protected String lastGameState;
	protected Timer timer;
	private int deviceWidth;
	private int deviceHeight;
	private int scale=70;
	
	public IPadPlayerWrapper(Model md, PlayerListener listener, int deviceWidth, int deviceHeight) {
		this.model = md;
		this.deviceWidth = deviceWidth;
		this.deviceHeight = deviceHeight;
		this.height = deviceHeight;
		this.width = deviceWidth;
		
		setSize("100%", "100%");
		setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		canvas = new AbsolutePanel();
		wrapper = new AbsolutePanel();
		wrapper.setSize("100%", "100%");
		wrapper.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		add(wrapper);
		setCellVerticalAlignment(wrapper, HasVerticalAlignment.ALIGN_TOP);
		wrapper.add(canvas,0,0);
		
		image1 = new Image();
		image1.addMouseUpHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				clearTimer();
			}
		});
		image1.addMouseOutHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				clearTimer();
			}
		});
		image1.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				startTimer(false);
			}
		});
		canvas.add(image1,0,0);
		image2 = new Image();
		image2.addMouseUpHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				clearTimer();
			}
		});
		image2.addMouseOutHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				clearTimer();
			}
		});
		image2.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				startTimer(true);
			}
		});
		canvas.add(image2,0,0);
		player = new IPhoneViewerBook() {
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
		canvas.add(player.getLayout(),43,43);
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

	private void startTimer(final boolean direction) {
		timer = new Timer() {
			@Override
			public void run() {
				if (direction) {
					player.scrollDown();
				} else {
					player.scrollUp();
				}
			}
		};
		timer.scheduleRepeating(200);
	}

	private void clearTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	void applyLayout(boolean horizontal) {
		if (horizontal) {
			height = deviceHeight;
			width = deviceWidth;
			image1.getElement().getStyle().setProperty("cursor", "url(images/cursor-left.ico),default");
			image1.setTitle(appConstants.playerIphonePageLeft());
			
			image2.getElement().getStyle().setProperty("cursor", "url(images/cursor-right.ico),default");
			image2.setTitle(appConstants.playerIphonePageRight());

			image1.setUrl(Images.IPAD_HORIZONTAL_LEFT);
			image2.setUrl(Images.IPAD_HORIZONTAL_RIGHT);
			canvas.setWidgetPosition(image2,554,0);
//			canvas.add(player.getLayout(),43,43);
		} else {
			height = deviceWidth;
			width = deviceHeight;
			image1.getElement().getStyle().setProperty("cursor", "url(images/cursor-left.ico),default");
			image1.setTitle(appConstants.playerIphonePageLeft());
			
			image2.getElement().getStyle().setProperty("cursor", "url(images/cursor-right.ico),default");
			image2.setTitle(appConstants.playerIphonePageRight());
			
			image1.setUrl(Images.IPAD_VERTICAL_BOTTOM);
			image2.setUrl(Images.IPAD_VERTICAL_TOP);
			canvas.setWidgetPosition(image2,426,0);
//			canvas.add(player.getLayout(),43,43);
		}
		canvas.setSize(IPhoneViewerOldBook.toPixels(width+84), IPhoneViewerOldBook.toPixels(height+84));
		player.getLayout().setSize(IPhoneViewerOldBook.toPixels(width), IPhoneViewerOldBook.toPixels(height));
		scale(scale);
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
		applyLayout(height == deviceWidth);
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
			fileExchange.saveFile(lastGameState,IPadPlayerWrapper.appConstants.playerSavedGame());
		}
	}

	public void load() {
		String text = fileExchange.loadFile(IPadPlayerWrapper.appConstants.playerRestoreGame());
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
		return true;
	}

	public void scale(int scale) {
		this.scale= scale;
		//1000 
		int t = (height-height*scale/100)/2;
		int l = (width-width*scale/100)/2;
		switch (scale) {
		case 50:
			canvas.setStyleName(Styles.ZOOM50);
			break;
		case 60:
			canvas.setStyleName(Styles.ZOOM60);
			break;
		case 70:
			canvas.setStyleName(Styles.ZOOM70);
			break;
		case 80:
			canvas.setStyleName(Styles.ZOOM80);
			break;
		case 90:
			canvas.setStyleName(Styles.ZOOM90);
			break;
		default:
			canvas.setStyleName(Styles.ZOOM_NO);
		}
		wrapper.setWidgetPosition(canvas,-l,-t);
	}

	public boolean isSupportModel() {
		return true;
	}

	public void loadModule() {
	}


}
