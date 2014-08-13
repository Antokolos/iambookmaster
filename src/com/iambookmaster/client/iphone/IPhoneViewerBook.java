package com.iambookmaster.client.iphone;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.dom.client.Style;
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
import com.iambookmaster.client.iphone.common.IPhoneScrollPanel;
import com.iambookmaster.client.iphone.common.IPhoneTouchProvider;
import com.iambookmaster.client.iphone.data.IPhoneDataService;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.player.PlayerState;

public class IPhoneViewerBook implements EntryPoint {

	static final IPhoneStyles css = IPhoneImages.INSTANCE.css();

//	private static final IPhoneTouchSimulator simulator = GWT.create(IPhoneTouchSimulator.class);
	
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

//	private Panel rootPanel;

	private Widget background;
	private Image mainImage;

	private LoadHandler verticalMainImageHandler;

	boolean verticalOrientation;
	private IPhoneViewListener listener;

	private int xCorrection;

	private AbsolutePanel layout;
	
	private int clientWidth;

	private int clientHight;
	
	private Timer animation;
	
	private boolean loadHandlerStarted;

//	protected boolean pageOrientation;

	private ErrorHandler verticalMainImageErrprHandler;

	private IPhonePlayer player;

	private HandlerRegistration resizeRegistration;

	private int fontSize;

	public AbsolutePanel getLayout() {
		return layout;
	}

	public void onModuleLoad() {
		load();
	}
	public void load() {
		RootPanel rootPanel = RootPanel.get();
		rootPanel.setSize("100%", "100%");
//		IPhoneTouchListener phoneTouchListener = new IPhoneTouchListener() {
//			@Override
//			public boolean event(JavaScriptObject source, String name, int[] x, int[] y, JavaScriptObject target) {
//				return superEvent(source, name, x, y, target);
//			}
//		};
//		phoneTouchListener.addListener(rootPanel.getElement(),IPhoneTouchListener.TOUCHSTART,false);
//		phoneTouchListener.addListener(rootPanel.getElement(),IPhoneTouchListener.TOUCHMOVE,false);
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
				IPhoneViewerBook.this.onResize();
			}
		});
		layout = new AbsolutePanel();
		layout.setSize("100%", "100%");
		layout.getElement().getStyle().setBackgroundColor("#474541");
		initialize(showIntro);
	}

	private void initialize(boolean showIntro) {
		initScrollPanel();
		layout.add(scrollPanel,10,10);
		
		initListeners();
		
		verticalOrientation = !getOrientaton();
		onResize();
		
		if (showIntro) {
			loadPlayer();
		}

	}

	public void loadPlayer(Model model,IPhoneDataService dataService,IPhonePlayerListener listener) {
		redrawScreen();
		player = new IPhonePlayer(viewCanvas,model,dataService,listener) {
			@Override
			protected void showIntroScreen(boolean hasContinue) {
				super.showIntroScreen(hasContinue);
			}
		};
	}
	
	protected void loadPlayer() {
		redrawScreen();
		//disable overlay
		player = new IPhonePlayer(viewCanvas);
	}


	private void initScrollPanel() {
		scrollPanel = new IPhoneScrollPanel() {
//			
			@Override
			public void scrollHorizontal(int delta) {
				stopAnimation();
				if (listener != null) {
					if (delta>clientWidth/3) {
						//back gesture
						cleanEvent();
						listener.back();
					} else if (delta < clientWidth/-3 ) {
						//forward gesture
						cleanEvent();
						listener.forward();
					}
				}
			}

			@Override
			public void scrollUp() {
				stopAnimation();
				super.scrollUp();
			}

			@Override
			public void scrollDown() {
				stopAnimation();
				super.scrollDown();
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
			protected void scrollVertical(int delta) {
				stopAnimation();
			}
		};
		scrollPanel.setScrollDelay(true);
		mainPanel = new FlowPanel();
		mainPanel.setWidth("100%");
		applyFontSize();
		applyOrientation();
		scrollPanel.setWidget(mainPanel);
	}

	protected void applyFontSize() {
		if (fontSize<MIN_FONT_SIZE) {
			fontSize = MIN_FONT_SIZE;
		} else if (fontSize>MAX_FONT_SIZE) {
			fontSize = MAX_FONT_SIZE;
		}
		mainPanel.getElement().getStyle().setFontSize(fontSize, Unit.PX);
	}
	
	private void applyOrientation() {
		mainPanel.setHeight(toPixels(getClientHeight()-50));
		mainPanel.setStyleName(css.mainStyleIPad());
		Style style =  mainPanel.getElement().getStyle();
/*
	column-width: 300px;
	-webkit-column-width: 300px;
	-moz-column-width: 300px;
		
	column-gap: 20px;	
	-webkit-column-gap: 20px;	
	-moz-column-gap: 20px;	
 */
		int w = (getClientWidth()-206)/2;//1024-840
		if (verticalOrientation) {
			style.clearProperty("MozColumnWidth");
			style.clearProperty("MozColumnGap");
			style.clearProperty("ColumnWidth");
			style.clearProperty("ColumnGap");
			style.clearProperty("WebkitColumnWidth");
			style.clearProperty("WebkitColumnGap");
		} else {
			style.setProperty("MozColumnWidth",toPixels(w));
			style.setProperty("MozColumnGap","40px");
			style.setProperty("ColumnWidth",toPixels(w));
			style.setProperty("ColumnGap","40px");
			style.setProperty("WebkitColumnWidth",toPixels(w));
			style.setProperty("WebkitColumnGap","40px");
		}
		style.setFontSize(30, Unit.PX);
	}

	private void initListeners() {
		verticalMainImageErrprHandler = new ErrorHandler() {
			
			public void onError(ErrorEvent event) {
				loadHandlerStarted = false;
				scrollPanel.setVisible(true);
				viewCanvas.performDone();
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
				DeferredCommand.addCommand(performDoneCanvas);
				scrollPanel.setVisible(true);
			}
		};
		
		viewCanvas = new IPhoneCanvasImpl();
	}

	private void applyPageOrientation(boolean vert) {
		int h = getClientHeight();
		int w = getClientWidth();
		//true - we are on big screen
		//TODO iPhone retina support
		if (h>w) {
			//vertical
			if (h==1024 && w==768) {
				//iPad, 1 page
				checkIfBackgroundIsImage(h,w,false);
				return;
			} else if (h==480 && w==320) {
				//iPhone,
				checkIfBackgroundIsImage(h,w,false);
				return;
			}
		//horizontal
		} else if (w==1024 && h==768) {
			//iPad
			checkIfBackgroundIsImage(h,w,true);
			return;
		} else if (w==480 && h==320) {
			//iPhone
			checkIfBackgroundIsImage(h,w,false);
			return;
		}
		
		//unknown device, use flexible design
		if ((background instanceof HTML)==false) {
			if (background != null) {
				layout.remove(background);
			}
			background = new HTML(IPhoneDataService.getInstance().getBackground());
			layout.insert(background,0,0,0);
		}
		
//		if (Math.max(h,w)>=800 && Math.min(h,w)>=600) {
//			//big screen
//		} else {
//			
//		}
		
		//right page
		layout.setWidgetPosition(background, 10-clientWidth, 0);
		layout.setWidgetPosition(scrollPanel, 10, 10);
		if (vert) {
			scrollPanel.setSize(toPixels(clientWidth-25), toPixels(clientHight-20));
			background.setSize(toPixels(clientWidth*2-10), toPixels(clientHight));
		} else {
			background.setSize(toPixels(clientWidth*2-10), toPixels(clientHight));
			scrollPanel.setSize(toPixels(clientWidth-25), toPixels(clientHight-20));
		}
		//TODO 1-2 pages
		
	}

	private void checkIfBackgroundIsImage(int height, int width, boolean twoPages) {
		String url = new StringBuilder(IPhoneTouchProvider.noImagesPath() ? "b" : "images/b").append(width).append('x').append(height).append(".png").toString();
		if (background instanceof Image) {
			Image image = (Image) background;
			if (image.getUrl().endsWith(url)==false) {
				image.setUrl(url);
			}
		} else {
			if (background != null) {
				layout.remove(background);
			}
			background = new Image(url);
			layout.insert(background, 0, 0, 0);
		}
		
		if (height>width) {
			layout.setWidgetPosition(scrollPanel, 25, 20);
			scrollPanel.setSize(toPixels(clientWidth-70), toPixels(clientHight-30));
		} else {
			layout.setWidgetPosition(scrollPanel, 30, 30);
			scrollPanel.setSize(toPixels(clientWidth-50), toPixels(clientHight-40));
		}
		
	}

	public void onResize() {
		stopAnimation();
		viewCanvas.cancelAnimation();
		boolean vert = getOrientaton();
		clientWidth = getClientWidth();
		clientHight = getClientHeight();
		applyPageOrientation(vert);
		if (vert != verticalOrientation) {
			redrawScreen();
		} 
			

	}

	private void stopAnimation() {
		if (animation != null) {
			animation.cancel();
			animation = null;
		}
	}


	private void redrawScreen() {
		verticalOrientation = getOrientaton();
		mainPanel.clear();
		applyOrientation();
		
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

		private Command checkScrolling = new Command() {
			public void execute() {
				if (scrollPanel.isBottomArrow()) {
					//TODO
//					scrollAnimation = new IPhoneScrollAnimation(scrollPanel);
				}
			}
		};
		
		
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
			stopAnimation();
			if (oldScrollPanel != null && layout.getWidgetIndex(oldScrollPanel)>0) {
				layout.remove(oldScrollPanel);
			}
			oldScrollPanel = scrollPanel;
			leftToRight = l2r;
			initScrollPanel();
			initCanvas();
			//set initial size and position
			int w = oldScrollPanel.getOffsetWidth();
			int h = oldScrollPanel.getOffsetHeight();
			scrollPanel.setSize(toPixels(w), toPixels(h));
			endPosition = layout.getWidgetLeft(oldScrollPanel);
			if (l2r) {
				startPosition = endPosition-w;
			} else {
				startPosition = endPosition+w;
			}
			layout.add(scrollPanel,startPosition,CANVAS_TOP);
			_clear();
		}

		public void clear() {
			stopAnimation();
			if (oldScrollPanel != null && layout.getWidgetIndex(oldScrollPanel)>0) {
				layout.remove(oldScrollPanel);
			}
			oldScrollPanel = null;
			_clear();
		}

		private void _clear() {
			loadHandlerStarted = false;
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
			IPhoneViewerBook.this.listener = listener;
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
			listener.drawn();
			DeferredCommand.addCommand(checkScrolling);
		}

		public void done() {
			if (loadHandlerStarted==false) {
				performDone();
			}
		} 

		public void setBackgroundImage(String url) {
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
		}

		public void changePageOrientation() {
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
			DeferredCommand.addCommand(checkScrolling);
		}

		public int getClientWidth() {
			return IPhoneViewerBook.this.getClientWidth();
		}

	}

	public void close() {
		resizeRegistration.removeHandler();
	}


	public IPhonePlayer getPlayer() {
		return player;
	}

	public PlayerState getPlayerState() {
		return player.getPlayerState();
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

}
