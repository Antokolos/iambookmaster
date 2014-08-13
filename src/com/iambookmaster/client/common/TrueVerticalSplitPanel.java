package com.iambookmaster.client.common;

import java.util.ArrayList;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.VerticalSplitPanel;
import com.google.gwt.user.client.ui.VerticalSplitPanelImages;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget should provide Vertical Split Table
 * @author ggadyatskiy
 *
 */
public class TrueVerticalSplitPanel extends VerticalPanel {
	
	private static final AbstractImagePrototype abstractImagePrototype = new AbstractImagePrototype() {
		public void applyTo(Image image) {
		}
		public Image createImage() {
			return null;
		}
		public String getHTML() {
			return "<table class=\"split_panel_ver_table\"><tr><td><table class=\"split_panel_sub_ver_table\"><tr><td><img  width=\"1px\" height=\"1px\" src=\"clear.cache.gif\"/></td></tr></table></td></tr></table>";
		}
	};

	private final VerticalSplitPanel panel;
	private ArrayList<ResizeListener> listeners;
	private boolean splitEnabled=true;
	private String position="50%";
	private boolean activated;

	private boolean disableScrollingTop=true;
	private boolean disableScrollingBottom=true;
	
	public TrueVerticalSplitPanel() {
		panel = new VerticalSplitPanel(new VerticalPanelSplitter());
		setSize("100%", "100%");
		panel.setSize("100%", "100%");
		super.add(panel);
		setCellHeight(panel, "100%");
		setCellWidth(panel, "100%");
//		panel.addStyleName(cursorStyle);
	}
//	public TrueVerticalSplitPanel(Widget topVidget, Widget bottomWidget) {
//		setSize("100%", "100%");
//		panel = new VerticalSplitPanel(new VerticalPanelSplitter());
//		panel.setTopWidget(topVidget);
//		panel.setBottomWidget(bottomWidget);
//		panel.setSize("100%", "100%");
//		super.add(panel);
//		setCellHeight(panel, "100%");
//		setCellWidth(panel, "100%");
////		panel.addStyleName(cursorStyle);
//	}

	public TrueVerticalSplitPanel(boolean enableScrollingTop, boolean enableScrollingBottom) {
		this();
		this.disableScrollingTop = (enableScrollingTop==false);
		this.disableScrollingBottom = (enableScrollingBottom==false);
	}

	/**
	 * @deprecated User setTopWidget or setBottomWidget
	 * @param widget
	 */
	public void add(Widget widget) {
	}

	/**
	 * @deprecated User setTopWidget or setBottomWidget
	 * @param widget
	 */
	public boolean remove(Widget widget) {
		return false;
	}

	/**
	 * @deprecated User setTopWidget or setBottomWidget
	 * @param widget
	 */
	public int getWidgetsCount() {
		return 0;
	}

	public void activate() {
		if (activated==false) {
			activated = true;
			//not initialized yet
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					panel.setSplitPosition(position);
					onActivate();
				}
			});
		}
	}
	
	protected void onActivate() {
	}
	public void setTopWidget(Widget widget) {
		panel.setTopWidget(widget);
	}

	public void setBottomWidget(Widget widget) {
		panel.setBottomWidget(widget);
	}

	public void setStyleName(String style) {
		super.setStyleName(style);
		panel.setStyleName(style);
//		if (splitEnabled) {
//			panel.addStyleName(cursorStyle);
//		}
	}

	public void addStyleName(String style) {
		super.addStyleName(style);
		panel.addStyleName(style);
	}

	public void removeStyleName(String style) {
		super.removeStyleName(style);
		panel.removeStyleName(style);
	}

	public void setSplitPosition(String pos) {
		position = pos;
		panel.setSplitPosition(pos);
	}

	protected void onAttach() {
		super.onAttach();
		EventListener interseptor = new EventListener() {
			public void onBrowserEvent(Event event) {
				if (splitEnabled==false) {
					return;
				}
				boolean resize = panel.isResizing();
				panel.onBrowserEvent(event);
				if (resize && DOM.eventGetType(event)==Event.ONMOUSEUP && listeners !=null) {
					//change size
					if (listeners.size()==1) {
						//most of cases
						((ResizeListener)listeners.get(0)).onResize(TrueVerticalSplitPanel.this);
					} else {
						for (int i=0;i<listeners.size();i++) {
							((ResizeListener)listeners.get(i)).onResize(TrueVerticalSplitPanel.this);
						}
					}
				}
			}
		};
		Element element = panel.getElement();
		int events = DOM.getEventsSunk(element);
		DOM.setEventListener(element,interseptor);
		DOM.sinkEvents(element, events);
		if (disableScrollingTop && panel.getTopWidget() != null) {
			Element el = DOM.getParent(panel.getTopWidget().getElement());
			DOM.setStyleAttribute(el, "overflow", "hidden");
		}
		if (disableScrollingBottom &&panel.getBottomWidget() != null) {
			Element el = DOM.getParent(panel.getBottomWidget().getElement());
			DOM.setStyleAttribute(el, "overflow", "hidden");
		}
	}
	
	public void addResizeListener(ResizeListener listener) {
		if (listeners==null) {
			listeners = new ArrayList<ResizeListener>();
		}
		listeners.add(listener);
	}

	public void removeResizeListener(ResizeListener listener) {
		if (listeners==null) {
			return;
		}
		listeners.remove(listener);
	}

	public boolean isSplitEnabled() {
		return splitEnabled;
	}

	public void setSplitEnabled(boolean splitEnabled) {
		if (splitEnabled) {
			if (this.splitEnabled==false) {
//				panel.addStyleName(cursorStyle);
				setSplitElement("<div class='vsplitter' "
						+ "style='text-align:center;'>" + abstractImagePrototype.getHTML() + "</div>");
			}
		} else {
			if (this.splitEnabled) {
//				panel.removeStyleName(cursorStyle);
				setSplitElement("&nbsp;");
			}
		}
		this.splitEnabled = splitEnabled;
	}

	private void setSplitElement(String elem) {
		Element element = DOM.getChild(panel.getElement(), 0);
		if (element != null) {
			Element elementSplit = DOM.getChild(element, 1);
			if (elementSplit != null) {
				DOM.setInnerHTML(elementSplit,elem );
			}
		}
	}

	/**
	 * Splitter for Horizontal Split Panel
	 * @author ggadyatskiy
	 *
	 */
	public class VerticalPanelSplitter implements VerticalSplitPanelImages {
		public AbstractImagePrototype verticalSplitPanelThumb() {
			return abstractImagePrototype;
		};
	}

	public int getSplitPosition() {
		Element element = DOM.getChild(panel.getElement(), 0);
		if (element == null) {
			return 0;
		} else {
			return DOM.getChild(element, 0).getOffsetHeight();
		}
	}
}
