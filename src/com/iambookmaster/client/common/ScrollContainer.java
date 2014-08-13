package com.iambookmaster.client.common;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class ScrollContainer extends AbsolutePanel implements WindowResizeListener {

    private ResizeTimer timer;
    private ScrollPanel scrollPanel;
	public ScrollContainer() {
		super.setHeight("100%");
		super.setWidth("100%");
//		HTML html  = new HTML("&nbsp;");
//		html.setSize("100%", "100%");
//		super.add(html, 0, 0);
		//static, 
//	    DOM.setStyleAttribute(html.getElement(), "position", "relative");
//		html.getElement().getStyle().setProperty(name, value)
		scrollPanel = new ScrollPanel();
		scrollPanel.setWidth("100%");
		scrollPanel.setAlwaysShowScrollBars(true);
		super.add(scrollPanel, 0, 0);
		DOM.setStyleAttribute(getElement(), "overflow", "hidden");
		resetHeight();
	}

	public void setScrollWidget(Widget widget) {
		scrollPanel.setWidget(widget);
		DOM.setStyleAttribute(widget.getElement(), "overflow", "visible");
	}
	
	public void onWindowResized(int width, int height) {
		resetHeight();
	}

	public void onAttach() {
		super.onAttach();
		WindowResizeCacheListener.addResizeListener(this);
		resetHeight();
	}

	protected void onDetach() {
		super.onDetach();
		if (timer != null) {
			timer.cancel();
		}
		WindowResizeCacheListener.removeResizeListener(this);
	}

	public void resetHeight() {
		if (timer ==null) {
			timer = new ResizeTimer(200);
		}
	}
	
	private class ResizeTimer extends Timer {
		public ResizeTimer(int rep) {
			scheduleRepeating(rep);
		}

		public void run() {
			if (ScrollContainer.this.isAttached()==false) {
				return;
			}
			int h = getOffsetHeight();
			int w = getOffsetWidth();
			if (h>0 && w>0) {
				timer = null;
				this.cancel();
				scrollPanel.setHeight(String.valueOf(h)+"px");
				scrollPanel.setWidth(String.valueOf(w)+"px");
			}
		}
		
	}
	
	public void setAlwaysShowScrollBars(boolean alwaysShow) {
		scrollPanel.setAlwaysShowScrollBars(alwaysShow);
	}

	public void ensureVisible(UIObject item) {
		scrollPanel.ensureVisible(item);
	}
	
}
