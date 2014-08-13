package com.iambookmaster.client.iphone.common;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class IPhoneScrollPanel extends ScrollPanel {

	public static final int DIRECTION_VERTICAL = 1;
	public static final int DIRECTION_HORIZONTAL = 2;
	public static final int DIRECTION_ALL = 3;
	public static final int DIRECTION_NONE = 4;
	public static final int SCROLL_DIRECTION_UP=0;
	public static final int SCROLL_DIRECTION_DOWN=1;
	public static final int SCROLL_DIRECTION_LEFT=2;
	public static final int SCROLL_DIRECTION_RIGHT=3;
	protected static final int RESIZE_LEVEL = 0;
	private static final int MAX_DISTANCE = 40;
//	private static final String CLICK_EVENT = "click";
	
	private int[] startY;
	private int[] startX;
	private int outterHight;
	private int innerHight;
	private int outterWidth;
	private int innerWidth;
	private boolean delay;
	private int direction=DIRECTION_VERTICAL;
	private int scrollDelay;
//	private boolean topArrow;
//	private boolean bottomArrow;

	private boolean start;
	private IPhoneTouchListener listener;
	private int scrollStep=5;
	private ScrollTimer timer;
	
	public boolean isEventsEnabled() {
		return listener.isEventsEnabled();
	}

	public void setEventsEnabled(boolean eventsEnabled) {
		listener.setEventsEnabled(eventsEnabled);
	}

	public boolean isTopArrow() {
		return getScrollPosition()>0;
	}
	
	public void cleanEvent() {
		start = false;
	}
	
	public boolean isBottomArrow() {
		fillHight();
		int max = innerHight-outterHight;
		return getScrollPosition()<max;
	}

	public void setVisible(boolean visible) {
		if (listener.isIE()) {
			//for IE
			getElement().getStyle().setOpacity(visible ? 1:0);
		} else {
			super.setVisible(visible);
		}
	}

	

	public IPhoneScrollPanel() {
		getElement().getStyle().setOverflow(Overflow.HIDDEN);
		listener = new IPhoneTouchListener() {

			private JavaScriptObject lastSource;
			private JavaScriptObject lastTarget;
			private Timer timer;

			private int initialX;
			private int initialY;
			private int smaller;
			private int bigger;
			
			private void superEvent(JavaScriptObject source, String name, JavaScriptObject target) {
				superEvent(lastSource, TOUCHSTART, startX, startY, target);
			}
			@Override
			public boolean event(JavaScriptObject source, String name, int[] x,int[] y, JavaScriptObject target) {
				if (TOUCHSTART.equals(name)) {
					start=true;
					startY = y;
					startX = x;
					initialX = x[0];
					initialY = y[0];
					smaller = 0;
					bigger = 0;
					fillHight();
					if (delay) {
						if (timer!=null) {
							timer.cancel();
							timer = null;
						}
						timer = new Timer() {
							private int counter = 2;
							@Override
							public void run() {
								counter--;
								if (counter<=0) {
									//no move events - it was a click
									cancel();
									timer = null;
									if (hasHandler(lastTarget)) {
										superEvent(lastSource, TOUCHSTART, lastTarget);
									} else {
										//scan all targets by coordinates
										JavaScriptObject target = findClosestTarget(initialX,initialY,MAX_DISTANCE);
										if (target == null) {
											//not target - click to screen
											onClick(initialX,initialY);
										} else {
											superEvent(lastSource, TOUCHSTART, target);
										} 
									}
								}
							}
						};
						timer.scheduleRepeating(250);
						lastSource = source;
						lastTarget = target;
						return false;
					} else {
						//no delay
						superEvent(source, name, x, y, target);
					}
					
				} else if (TOUCHMOVE.equals(name)) {
					if (start) {
						if (timer!=null) {
							timer.cancel();
						}
						if (x.length>1) {
							//directions
							if (startX.length>1) {
								int diffOld = Math.abs(startX[0]-startX[1])+Math.abs(startY[0]-startY[1]);
								int diffNew = Math.abs(x[0]-x[1])+Math.abs(y[0]-y[1]);
								if (diffOld>diffNew) {
									//smaller
									if (smaller++ >= RESIZE_LEVEL) {
										//do it
										start = false;
										makeSmaller();
									}
								} else if (diffOld<diffNew) {
									//bigger
									if (bigger++ >= RESIZE_LEVEL) {
										//do it
										start = false;
										makeBigger();
									}
								}
							}
						} else {
							switch (direction) {
							case DIRECTION_VERTICAL:
								if (Math.abs(x[0]-startX[0]) > Math.abs(y[0]-startY[0])) {
									scrollHorizontal(x[0]-initialX);
								} else {
									scrollY(y[0]);
								}
								break;
							case DIRECTION_HORIZONTAL:
								if (Math.abs(x[0]-startX[0]) < Math.abs(y[0]-startY[0])) {
									scrollVertical(y[0]-initialY);
								} else {
									scrollX(x[0]);
								}
								break;
							case DIRECTION_NONE:
								if (Math.abs(x[0]-startX[0]) < Math.abs(y[0]-startY[0])) {
									scrollVertical(y[0]-initialY);
								} else {
									scrollHorizontal(x[0]-initialX);
								}
								break;
							default:
								scrollY(y[0]);
								scrollX(x[0]);
								break;
							}
						}
						startY = y;
						startX = x;
					}
//				} else if (CLICK_EVENT.equals(name)){
//					onClick(x[0], y[0]);
				} else {
					//cancel
					start = false;
				}
				
				return false;
			}
			
			private void scrollX(int x) {
				if (x<startX[0]) {
					//scroll right
					if (innerWidth>outterWidth) {
						int pos = getHorizontalScrollPosition();
						startX[0] = startX[0]-x;
						if (pos<innerWidth-outterWidth-startY[0]) {
							scrollHorizontalTo(pos+startX[0]);
						} else {
							scrollHorizontalTo(innerWidth-outterWidth);
						}
					}
				} else {
					//scroll left
					int pos = getHorizontalScrollPosition();
					startX[0] = x - startX[0];
					if (pos>startX[0]) {
						scrollHorizontalTo(pos-startX[0]);
					} else {
						scrollHorizontalTo(0);
					}
				}
			}
			//support vertical scroll
			private void scrollY(int y) {
				if (y<startY[0]) {
					//scroll down
					if (innerHight>outterHight) {
						int pos = getScrollPosition();
						scrollVertical(startY[0]-y);
						startY[0] = startY[0]-y;
						if (pos<innerHight-outterHight-startY[0]) {
							scrollTo(pos+startY[0]);
						} else {
							scrollTo(innerHight-outterHight);
						}
					}
				} else {
					//scroll up
					int pos = getScrollPosition();
					scrollVertical(startY[0]-y);
					startY[0] = y - startY[0];
					if (pos>startY[0]) {
						scrollTo(pos-startY[0]);
					} else {
						scrollTo(0);
					}
				}
			}
			
		};
		listener.addListener(getElement(),IPhoneTouchListener.TOUCHSTART,false);
		listener.addListener(getElement(),IPhoneTouchListener.TOUCHMOVE,false);
		listener.addListener(getElement(),IPhoneTouchListener.TOUCHEND,false);
//		listener.addListener(getElement(),CLICK_EVENT,false);
//		listener.addListener(getElement(),"touchEnd",false);
//		listener.addListener(getElement(),"touchcancel",false);
	}
	
	protected void onClick(int initialX, int initialY) {
	}
	
	protected void makeBigger() {
	}

	protected void makeSmaller() {
	}

	protected void scrollVertical(int delta) {
	}

	public void scrollHorizontal(int delta) {
	}
	
	protected String getScrollImage(int direction) {
		return null;
	}

	private void fillHight() {
		outterHight = getOffsetHeight();
		innerHight = DOM.getElementPropertyInt(getElement(), "scrollHeight"); 
		outterWidth = getOffsetWidth();
		innerWidth = DOM.getElementPropertyInt(getElement(), "scrollWidth"); 
	}

	private void scrollTo(int postion) {
		setScrollPosition(postion);
		ScrollEvent event = new IPhoneScrollEvent();
		fireEvent(event);
	}

	protected void scrollHorizontalTo(int postion) {
		setHorizontalScrollPosition(postion);
		ScrollEvent event = new IPhoneScrollEvent();
		fireEvent(event);
	}
	
	public void resetPosition() {
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				fillHight();
				scrollTo(0);
			}
		});
	}

	public class IPhoneScrollEvent extends ScrollEvent {
	}


	public void scrollUp() {
		cancelTimer();
		int oldPos = getScrollPosition();
		if (oldPos>0) {
			int pos = oldPos - scrollStep;
			if (pos<0) {
				pos=0;
			}
			if (scrollDelay==0) {
				scrollTo((pos/scrollStep)*scrollStep);
			} else {
				timer = new ScrollTimer(oldPos,pos,false);
			}
		}
	}

	public void scrollDown() {
		cancelTimer();
		fillHight();
		int oldPos = getScrollPosition();
		int max = innerHight-outterHight;
		if (oldPos<max) {
			int pos = oldPos + scrollStep;
			if (pos>max) {
				pos=max;
			}
			if (scrollDelay==0) {
				scrollTo((pos/scrollStep)*scrollStep);
			} else {
				timer = new ScrollTimer(oldPos,pos,false);
			}
		}
	}
	
	@Override
	public void setWidget(IsWidget w) {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		super.setWidget(w);
	}

	@Override
	public void setWidget(Widget w) {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		super.setWidget(w);
	}
	
	public interface PublicWidget {

		public void onAttach();
		
		public void onDetach();

		public Element getElement();
		
	}

	public class PublicImage extends Image implements PublicWidget {

		public PublicImage(String url, LoadHandler loadHandler, ErrorHandler errorHandler) {
			addLoadHandler(loadHandler);
			addErrorHandler(errorHandler);
			setUrl(url);
		}

		@Override
		public void onAttach() {
			super.onAttach();
		}

		@Override
		public void onDetach() {
			super.onDetach();
		}
		
	}

	public class ScrollTimer extends Timer implements LoadHandler, ErrorHandler{
		
		private int oldPos;
		private int step;
		private int newPos;
		private boolean horizontal;
		private String url;
		private PublicWidget image;
		private PublicWidget fleeper;

		@Override
		public void scheduleRepeating(int periodMillis) {
			super.scheduleRepeating(periodMillis);
		}

		public ScrollTimer(int oldPos, int newPos, boolean horizontal) {
			this.oldPos = oldPos;
			this.newPos = newPos;
			this.horizontal = horizontal;
			step = (newPos - oldPos) / scrollDelay;
			if (horizontal) {
				if (oldPos<newPos) {
					//right
					url = getScrollImage(SCROLL_DIRECTION_RIGHT);
				} else {
					url = getScrollImage(SCROLL_DIRECTION_LEFT);
				}
			} else if (oldPos<newPos) {
				//down
				url = getScrollImage(SCROLL_DIRECTION_DOWN);
			} else {
				//up
				url = getScrollImage(SCROLL_DIRECTION_UP);
			}
			if (url == null) {
				scheduleRepeating(100);
			} else {
				//use this url
				image = new PublicImage(url,this,this);
				Element element = image.getElement();
				Style style = element.getStyle();  
				style.setPosition(Position.ABSOLUTE);
//				scrollImageNode = getElement().appendChild(element);
				image.onAttach();
				if (horizontal) {
					style.setTop(0, Unit.PX);
					if (oldPos<newPos) {
						//right
						style.setLeft(newPos, Unit.PX);
					} else {
						//left
						style.setLeft(oldPos, Unit.PX);
					}
				} else if (oldPos<newPos) {
					//down
					style.setTop(newPos, Unit.PX);
					style.setLeft(0, Unit.PX);
				} else {
					//right
					style.setTop(newPos, Unit.PX);
					style.setLeft(0, Unit.PX);
				}
			}
		}

		@Override
		public void run() {
			oldPos = oldPos + step;
			if (step>0) {
				if (oldPos>=newPos) {
					cancelScroll();
					return;
				}
			} else if (oldPos <= newPos) {
				cancelScroll();
				return;
			}
			if (horizontal) {
				scrollHorizontalTo(oldPos);
			} else {
				scrollTo(oldPos);
			}
		}

		public void cancelScroll() {
			if (horizontal) {
				scrollHorizontalTo(newPos);
			} else {
				scrollTo(newPos);
			}
			if (image != null) {
//				image.onDetach();
				image.getElement().removeFromParent();
//				getElement().removeChild();
//				image.removeFromParent();
			}
			cancel();
		}

		public void onError(ErrorEvent event) {
			scheduleRepeating(100);
		}

		public void onLoad(LoadEvent event) {
			scheduleRepeating(100);
		}
		
	}

	private void cancelTimer() {
		if (timer != null) {
			timer.cancelScroll();
			timer = null;
		}
	}

	public boolean canScrollRight() {
		return getHorizontalScrollPosition()>0;
	}
	public boolean scrollRight() {
		cancelTimer();
		int oldPos = getHorizontalScrollPosition();
		if (oldPos>0) {
			int pos = oldPos - scrollStep;
			if (pos<0) {
				pos=0;
			}
			if (scrollDelay==0) {
				scrollHorizontalTo((pos/scrollStep)*scrollStep);
			} else {
				timer = new ScrollTimer(oldPos,pos,true);
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean canScrollLeft() {
		fillHight();
		int max = innerWidth-outterWidth;
		return getHorizontalScrollPosition()<max;
	}
	
	public boolean scrollLeft() {
		cancelTimer();
		fillHight();
		int oldPos = getHorizontalScrollPosition();
		int max = innerWidth-outterWidth;
		if (oldPos<max) {
			int pos = oldPos + scrollStep;
			if (pos>max) {
				pos=max;
			}
			if (scrollDelay==0) {
				scrollHorizontalTo((pos/scrollStep)*scrollStep);
			} else {
				timer = new ScrollTimer(oldPos,pos,true);
			}
			return true;
		} else {
			return false;
		}
	}

	public void resetHandlers() {
		cancelTimer();
		listener.resetHandlers();
	}

	public HandlerRegistration addClickHandler(Widget widget, ClickHandler handler) {
		return listener.addClickHandler(widget, handler);
	}

	public void setScrollDelay(boolean delay) {
		this.delay = delay;
	}
	public boolean isSrollDelay() {
		return delay;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public void setScrollStep(int step) {
		scrollStep = step;
	}

	public int getScrollDelay() {
		return scrollDelay;
	}

	public void setScrollDelay(int scrollDelay) {
		this.scrollDelay = scrollDelay;
	}

	@Override
	protected void onDetach() {
		cancelTimer();
		super.onDetach();
	}


//	public boolean isScrollVisible() {
//		System.out.println("offsetHeight");
//		System.out.println(getElement().getClientHeight());
//		System.out.println(getContainerElement().getClientHeight());
//		System.out.println(getWidget().getElement().getClientHeight());
//		System.out.println(DOM.getElementPropertyInt(getElement(), "scrollHeight"));
//		return getElement().getOffsetHeight() < getContainerElement().getClientHeight();
//	}
}
