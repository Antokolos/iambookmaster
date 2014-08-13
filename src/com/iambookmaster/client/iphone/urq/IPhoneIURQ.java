package com.iambookmaster.client.iphone.urq;

import java.util.List;

import com.google.code.gwt.database.client.service.DataServiceException;
import com.google.code.gwt.database.client.service.ListCallback;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.common.AbsolutePanelSpan;
import com.iambookmaster.client.iphone.IPhoneCanvas;
import com.iambookmaster.client.iphone.IPhoneConsole;
import com.iambookmaster.client.iphone.IPhonePlayerListener;
import com.iambookmaster.client.iphone.IPhoneViewListener;
import com.iambookmaster.client.iphone.common.IPhoneScrollPanel;
import com.iambookmaster.client.iphone.data.IPhoneDataService;
import com.iambookmaster.client.iphone.data.IPhoneFileBean;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;

public class IPhoneIURQ implements EntryPoint {

	static final IPhoneStyles css = IPhoneImages.INSTANCE.css();

	private static final int BUTTON_SIZE = 40;
	private static final int BUTTON_SPACE = 50;
	static final String BUTTON_SIZE_PX = toPixels(BUTTON_SIZE);
	static final String BUTTON_SPACE_PX = toPixels(BUTTON_SPACE);

	protected static final int CANVAS_TOP = 10;
	
	private static final int MIN_FONT_SIZE = 8;
	private static final int MAX_FONT_SIZE = 30;

	private AbsolutePanel canvas;
	private IPhoneScrollPanel scrollPanel;
	
	private FlowPanel mainPanel;

	private IPhoneCanvasImpl viewCanvas;

	private HTML background;
	private Image mainImage;

	private LoadHandler verticalMainImageHandler;

	boolean verticalOrientation;
	private IPhoneViewListener listener;

	private int xCorrection;

	private AbsolutePanel layout;
	
	private int clientWidth;

	private int clientHight;
	
	private Timer scrollAnimation;
	private Timer animation;
	
	private boolean loadHandlerStarted;

	private boolean doScrollAnimation;

//	private boolean leftPage;

	protected boolean pageOrientation;

	private ErrorHandler verticalMainImageErrprHandler;

//	private Image introImage;

	private HandlerRegistration resizeRegistration;

	protected int fontSize = 14;

	private int scrollPanelHeight;
	private int scrollPanelWidth;

	private IPhonePlayerListener playerListener;

	private IPhoneDataService dataService;

	protected IPhoneURQMainPanel rootPanel;

	public AbsolutePanel getLayout() {
		return layout;
	}

	public IPhoneIURQ() {
	}

	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get();
		rootPanel.setSize("100%", "100%");
		if (GWT.isScript()) {
			GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){
				public void onUncaughtException(Throwable e) {
					IPhoneConsole.showError(e);
				}
			});
		}
		load(true);
		rootPanel.add(layout);
	}
	
	public void init() {
		load(false);
	}
	private void load(boolean showIntro) {
		css.ensureInjected();
		resizeRegistration = Window.addResizeHandler(new ResizeHandler(){
			public void onResize(ResizeEvent event) {
				IPhoneIURQ.this.onResize();
			}
		});
		layout = new AbsolutePanel();
		layout.setSize("100%", "100%");
		layout.getElement().getStyle().setBackgroundColor("#474541");
		
		background = new HTML();
		layout.add(background,0,0);
		
		initScrollPanel();
		layout.add(scrollPanel,10,10);
		
		initListeners();
		
		verticalOrientation = !getOrientaton();
		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				onResize();
				background.setHTML(IPhoneDataService.getInstance().getBackground());
				loadPlayer();
			}
		});
	}

	public void loadPlayer(IPhoneDataService dataService,IPhonePlayerListener listener) {
		playerListener = listener;
		this.dataService = dataService;
		loadPlayer();
	}
	
	protected void loadPlayer() {
		if (dataService==null) {
			dataService = IPhoneDataService.getInstance();
		}
		dataService.selectAvailableFiles("qst",new ListCallback<IPhoneFileBean>() {
			public void onFailure(DataServiceException error) {
				Window.alert(error.getMessage());
			}
	
			public void onSuccess(List<IPhoneFileBean> result) {
				rootPanel = new IPhoneURQMainPanel(dataService,result);
				dataService.loadLastState(null, new ScalarCallback<String>() {
					public void onFailure(DataServiceException error) {
						removeSplashScreen();
					}
					
					public void onSuccess(final String state) {
						if (state==null) {
							//nothing to restore
							removeSplashScreen();
						} else {
							final String questName = rootPanel.getPlayer().getLastQuestName(state);
							if (questName==null) {
								//nothing to restore
								removeSplashScreen();
							} else {
								dataService.loadSingleFile(questName, new ScalarCallback<String>() {
									public void onFailure(DataServiceException error) {
										//quest was removed
										removeSplashScreen();
									}
									public void onSuccess(String result) {
										//quest exists, play from the last state
										rootPanel.playSavedGame(questName,result,state);
										removeSplashScreen();
									}
								});							
							}
						}
					}
				});
			}
		});
	}
	
	private void removeSplashScreen() {
		rootPanel.show(viewCanvas,true);
		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				dataService.removeSplashScreen();
			}
		});
		
	}
	


	private void initScrollPanel() {
		scrollPanel = new IPhoneScrollPanel() {
//			
			@Override
			public void scrollHorizontal(int delta) {
				stopScrollAnimation();
				if (Math.abs(delta)>clientWidth/4) {
					IPhoneIURQ.this.scrollHorizontal(delta>0);
				}
			}

			@Override
			protected void makeBigger() {
				fontSize = fontSize + 2;
				applyFontSize();
			}

			@Override
			protected void makeSmaller() {
				fontSize = fontSize - 2;
				applyFontSize();
			}
			
			@Override
			public void scrollUp() {
				stopScrollAnimation();
				if (listener != null) {
					//back gesture
					cleanEvent();
					listener.back();
				}
			}

			@Override
			public void scrollDown() {
				stopScrollAnimation();
				if (listener != null) {
					//forward gesture
					cleanEvent();
					listener.forward();
				}
			}			

			@Override
			protected void onClick(int x, int y) {
				if (listener != null) {
					listener.click(x,y);
				}
			}

		};
		scrollPanel.setScrollDelay(true);
		scrollPanel.setDirection(IPhoneScrollPanel.DIRECTION_NONE);
		mainPanel = new FlowPanel();
		if (scrollPanelWidth != 0) {
			mainPanel.setWidth(toPixels(scrollPanelWidth));
		}
		mainPanel.setStyleName(css.mainStyle());
		applyFontSize();
		scrollPanel.setWidget(mainPanel);
	}

	protected void scrollHorizontal(boolean right) {
		scrollPanel.cleanEvent();
		if (right) {
			scrollPanel.scrollRight();
		} else if (scrollPanel.scrollLeft()==false && listener != null){
			listener.forward();
		}
	}

	protected void applyFontSize() {
		if (fontSize<MIN_FONT_SIZE) {
			fontSize = MIN_FONT_SIZE;
		} else if (fontSize>MAX_FONT_SIZE) {
			fontSize = MAX_FONT_SIZE;
		}
		mainPanel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
		scrollPanel.setHorizontalScrollPosition(0);
	}

	private void initListeners() {
		verticalMainImageErrprHandler = new ErrorHandler() {
			
			public void onError(ErrorEvent event) {
				loadHandlerStarted = false;
				scrollPanel.setVisible(true);
				if (doScrollAnimation) {
					doScrollAnimation = false;
					viewCanvas.performDone();
				}
//				scrollPanel.setVisible(true);
			}
		};
		verticalMainImageHandler = new LoadHandler(){
			private Command performDoneCanvas = new Command() {
				public void execute() {
					viewCanvas.performDone();
				}
			};

			public void onLoad(LoadEvent event) {
				loadHandlerStarted = false;
				int hMax = getClientHeight();
				int wMax = getClientWidth();
				int max = Math.max(hMax,wMax);
				if (verticalOrientation) {
					int w = mainImage.getWidth();
					int h = mainImage.getHeight();
					if (w>=wMax-10) {
						xCorrection = 0;
					} else {
						xCorrection = wMax / 2 - 5 - w / 2;
					}
					if (h>max) {
						h = max;
					}
					canvas.setHeight(toPixels(h+5));
					canvas.setWidth("100%");
					canvas.setWidgetPosition(mainImage, xCorrection, 0);
				} else {
					int w = mainImage.getWidth();
					int h = mainImage.getHeight();
					if (w>max) {
						w = max;
					}
					canvas.setSize(toPixels(w+5), toPixels(h+5));
				}
				if (doScrollAnimation) {
					doScrollAnimation = false;
					DeferredCommand.addCommand(performDoneCanvas );
				}
				scrollPanel.setVisible(true);
			}
		};
		
		viewCanvas = new IPhoneCanvasImpl();
	}

	private void applyPageOrientation(boolean vert) {
		scrollPanelHeight = clientHight-20;
		scrollPanelWidth = clientWidth - 25;
		mainPanel.setWidth(toPixels(scrollPanelWidth));
		if (pageOrientation) {
			//left page
			layout.setWidgetPosition(background, 0, 0);
			layout.setWidgetPosition(scrollPanel, 20, 10);
			if (vert) {
				background.setSize(toPixels(clientWidth*2-10), toPixels(clientHight));
				scrollPanel.setSize(toPixels(scrollPanelWidth), toPixels(scrollPanelHeight));
			} else {
				background.setSize(toPixels(clientWidth*2-10), toPixels(clientHight));
				scrollPanel.setSize(toPixels(scrollPanelWidth), toPixels(scrollPanelHeight));
			}
		} else {
			//right page
			layout.setWidgetPosition(background, 10-clientWidth, 0);
			layout.setWidgetPosition(scrollPanel, 10, 10);
			if (vert) {
				scrollPanel.setSize(toPixels(scrollPanelWidth), toPixels(scrollPanelHeight));
				background.setSize(toPixels(clientWidth*2-10), toPixels(clientHight));
			} else {
				background.setSize(toPixels(clientWidth*2-10), toPixels(clientHight));
				scrollPanel.setSize(toPixels(scrollPanelWidth), toPixels(scrollPanelHeight));
			}
		}
	}


	public void onResize() {
		stopScrollAnimation();
		viewCanvas.cancelAnimation();
		boolean vert = getOrientaton();
		clientWidth = getClientWidth();
		clientHight = getClientHeight();
		applyPageOrientation(vert);
		if (vert != verticalOrientation) {
			redrawScreen();
		} 
			

	}

	private void stopScrollAnimation() {
		if (scrollAnimation != null) {
			scrollAnimation.cancel();
			scrollAnimation = null;
		}
		if (animation != null) {
			animation.cancel();
			animation = null;
		}
	}


	private void redrawScreen() {
		verticalOrientation = getOrientaton();
		mainPanel.clear();
		initCanvas();
		if (listener != null) {
			listener.redraw(viewCanvas);
		}
		scrollPanel.resetPosition();
		
	}


	private void initCanvas() {
		canvas = verticalOrientation ? new AbsolutePanel() : new AbsolutePanelSpan();
		if (verticalOrientation) {
			canvas.setStyleName(css.canvasVertical());
		} else {
			canvas.setStyleName(css.canvasHorizontal());
		}
		mainPanel.add(canvas);
	}


	public static String toPixels(int size) {
		return String.valueOf(size)+"px";
	}

	private boolean getOrientaton() {
		return getClientHeight()>getClientWidth();
	}
	
	protected int getClientWidth() {
		return Window.getClientWidth();
	}

	protected int getClientHeight() {
		return Window.getClientHeight();
	}

	public class IPhoneCanvasImpl implements IPhoneCanvas {
		private IPhoneScrollPanel oldScrollPanel;
		private boolean leftToRight;
		private int startPosition;
		private int endPosition;
		private boolean firstCancel;
		private boolean updatePageOrientation;

//		private Command checkScrolling = new Command() {
//			public void execute() {
				//disable scroll animation
//				if (scrollPanel.isBottomArrow()) {
//					scrollAnimation = new IPhoneScrollAnimation(scrollPanel);
//				}
//			}
//		};
		
		
		public void add(Widget widget) {
			mainPanel.add(widget);
		}

		private void cancelAnimation() {
			oldScrollPanel = null;
		}

		public void addSprite(String url, int x, int y) {
			checkImage(true);
		}

		public void addSprite(ImageResource resource, int x, int y) {
			checkImage(true);
		}

		public void clearWithAnimation(boolean l2r) {
			stopScrollAnimation();
			if (oldScrollPanel != null && layout.getWidgetIndex(oldScrollPanel)>0) {
				layout.remove(oldScrollPanel);
			}
			oldScrollPanel = scrollPanel;
			leftToRight = l2r;
			initScrollPanel();
			initCanvas();
			//set initial size and position
			scrollPanel.setSize(toPixels(scrollPanelWidth), toPixels(scrollPanelHeight));
			endPosition = layout.getWidgetLeft(oldScrollPanel);
			if (l2r) {
				startPosition = endPosition-scrollPanelWidth;
			} else {
				startPosition = endPosition+scrollPanelHeight;
			}
			layout.add(scrollPanel,startPosition,CANVAS_TOP);
			_clear();
		}

		public void clear() {
			stopScrollAnimation();
			if (oldScrollPanel != null && layout.getWidgetIndex(oldScrollPanel)>0) {
				layout.remove(oldScrollPanel);
			}
			oldScrollPanel = null;
			_clear();
		}

		private void _clear() {
			updatePageOrientation=false;
			loadHandlerStarted = false;
			doScrollAnimation = false;
//			stopScrollAnimation();
			scrollPanel.resetHandlers();
			int l = mainPanel.getWidgetCount();
			//keep just canvas
			while (l>1) {
				l--;
				mainPanel.remove(l);
			}
			canvas.clear();
			canvas.setSize("1px", "1px");
			//disable events
			scrollPanel.setEventsEnabled(false);
			scrollPanel.setScrollPosition(0);
			scrollPanel.setHorizontalScrollPosition(0);
		}

		public void setImage(String url) {
			resetImage();
			mainImage.setUrl(url);
		}

		public void setImage(ImageResource resource) {
			resetImage();
			mainImage.setResource(resource);
		}

		private void resetImage() {
			checkImage(false);
			if (oldScrollPanel != null) {
				scrollPanel.setVisible(false);
			}
			doScrollAnimation = true;
			mainImage = new Image();
			loadHandlerStarted=true;
			mainImage.addLoadHandler(verticalMainImageHandler);
			mainImage.addErrorHandler(verticalMainImageErrprHandler);
			canvas.add(mainImage, 0, 0);
		}

		private void checkImage(boolean set) {
			if (set) {
			} else if (mainPanel.getWidgetCount()>1) {
				//not set but some widgets were added
				throw new IllegalStateException("Image must be the first");
			}
		}

		public void setListener(IPhoneViewListener listener) {
			IPhoneIURQ.this.listener = listener;
		}

		private void performDone() {
			if (oldScrollPanel != null) {
				//animation
				animation = new Timer() {
					private int offset;
					@Override
					public void run() {
						offset = offset + 100;
						int pos;
						if (leftToRight) {
							pos = offset+startPosition;
							if (pos >= endPosition) {
								//done
								cancel();
								return;
							}
							layout.setWidgetPosition(oldScrollPanel, endPosition+offset, CANVAS_TOP);
						} else {
							pos = startPosition-offset;
							if (pos <= endPosition) {
								//done
								cancel();
								return;
							}
							layout.setWidgetPosition(oldScrollPanel, endPosition-offset, CANVAS_TOP);
						}
						layout.setWidgetPosition(scrollPanel, pos, CANVAS_TOP);
					}
					
					@Override
					public void cancel() {
						super.cancel();
						if (firstCancel==false) {
							layout.setWidgetPosition(scrollPanel, endPosition, CANVAS_TOP);
							layout.remove(oldScrollPanel);
							animation = null;
							performDoneEnd();
						}
					}
					
				};
				//cancel() is fired on scheduleRepeating()
				firstCancel = true;
				animation.scheduleRepeating(verticalOrientation ? 100:50);
				firstCancel = false;
			} else {
				performDoneEnd();
			}
		}
		
		private void performDoneEnd() {
			//enable events
			scrollPanel.setEventsEnabled(true);
			if (updatePageOrientation) {
				applyPageOrientation(verticalOrientation);
			}
			listener.drawn();
//			DeferredCommand.addCommand(checkScrolling);
		}

		public void done() {
			if (loadHandlerStarted) {
				doScrollAnimation = true;
			} else {
				performDone();
			}
		} 

		public void setBackgroundImage(String url) {
//			if (url != null) {
//				scrollPanel.getElement().getStyle().setBackgroundImage("url("+url+")");
//			} else {
//				scrollPanel.getElement().getStyle().setBackgroundImage("url("+url+")");
//			}
		}

		public void disableAudio() {
//			audioDisabled=true;
//			if (audioEnabled) {
//				listener.audio(false);
//			}
//			audioEnabled = false;
//			audioIcon.setStyleName(css.buttonDisabled());
//			applyAudio();
		}
		
		public void setAudio(boolean allowAudio) {
//			if (audioDisabled==false) {
//				audioEnabled = allowAudio;
//				applyAudio();
//			}
		}

		public HandlerRegistration addClickHandler(Widget widget, ClickHandler handler) {
			return scrollPanel.addClickHandler(widget, handler);
			
		}

		public void setPageOrientation(boolean leftPage) {
			if (pageOrientation != leftPage) {
				pageOrientation = leftPage;
				updatePageOrientation=true;
			}
		}

		public void changePageOrientation() {
			pageOrientation = pageOrientation==false;
			updatePageOrientation=true;
		}

		public void removeWidget(Widget widget) {
			mainPanel.remove(widget);
		}
		
		public boolean isVertical() {
			return verticalOrientation;
		}

		public boolean isBottomVisible() {
			return scrollPanel.isBottomArrow()==false;
		}

		public void scrollPageDown() {
//			DeferredCommand.addCommand(checkScrolling);
		}

		public int getClientWidth() {
			return IPhoneIURQ.this.getClientWidth();
		}

	}

	public void close() {
		resizeRegistration.removeHandler();
	}


	public void scrollDown() {
		scrollPanel.scrollDown();
	}

	public void scrollUp() {
		scrollPanel.scrollUp();
	}

	public void setStyleName(String style) {
		layout.setStyleName(style);
	}

	public void restart() {
		// TODO Auto-generated method stub
		
	}

	public void restoreGame(String text) {
		// TODO Auto-generated method stub
		
	}

}
