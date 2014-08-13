package com.iambookmaster.client.common;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanelImages;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TrueHorizontalSplitPanel extends VerticalPanel {
	
//	private static final String cursorStyle = "sp4_split_panel_cursor";
	
	private static final AbstractImagePrototype abstractImagePrototype = new AbstractImagePrototype() {
		public void applyTo(Image image) {
		}
		public Image createImage() {
			return null;
		}
		public String getHTML() {
			return "<table class=\"spilit_panel_table\"><tr><td><table class=\"spilit_panel_sub_table\"><tr><td><img width=\"1px\" height=\"1px\" src=\"clear.cache.gif\"/></td></tr></table></td></tr></table>";
		}
	};
	
	private final HorizontalSplitPanel panel;
	private List<ResizeListener> listeners = new ArrayList<ResizeListener>();
	private boolean splitEnabled=true;
	private String position="50%";
	private boolean activated;
	
	public TrueHorizontalSplitPanel() {
		panel = new HorizontalSplitPanel(new HorizontalPanelSplitter());
		setSize("100%", "100%");
		super.add(panel);
		setCellHeight(panel, "100%");
		setCellWidth(panel, "100%");
	}

	/**
	 * @deprecated Use setLeftWidget or setRightWidget
	 * @param widget
	 */
	public void add(Widget widget) {
	}

	/**
	 * @deprecated Use setLeftWidget or setRightWidget
	 * @param widget
	 */
	public boolean remove(Widget widget) {
		return false;
	}

	/**
	 * @deprecated Use setLeftWidget or setRightWidget
	 * @param widget
	 */
	public int getWidgetsCount() {
		return 0;
	}

	public void setLeftWidget(Widget widget) {
		panel.setLeftWidget(widget);
	}

	public void setRightWidget(Widget widget) {
		panel.setRightWidget(widget);
	}

	public void setStyleName(String style) {
		super.setStyleName(style);
		panel.setStyleName(style);
	}

	public void addStyleName(String style) {
		super.addStyleName(style);
		panel.addStyleName(style);
	}

	public void removeStyleName(String style) {
		super.removeStyleName(style);
		panel.removeStyleName(style);
	}

	public void activate() {
		if (activated==false) {
			activated = true;
			//not initialized yet
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					panel.setSplitPosition(position);
					fireResizeListeners();
				}
			});
		}
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
					fireResizeListeners();
				}
			}

		};
		Element element = panel.getElement();
		int events = DOM.getEventsSunk(element);
		DOM.setEventListener(element,interseptor);
		DOM.sinkEvents(element, events);
		if (panel.getLeftWidget() != null) {
			Element el = DOM.getParent(panel.getLeftWidget().getElement());
			DOM.setStyleAttribute(el, "overflow", "hidden");
		}
		if (panel.getRightWidget() != null) {
			Element el = DOM.getParent(panel.getRightWidget().getElement());
			DOM.setStyleAttribute(el, "overflow", "hidden");
		}
	}
	
	private void fireResizeListeners() {
		if (listeners.size()==1) {
			//most of cases
			((ResizeListener)listeners.get(0)).onResize(TrueHorizontalSplitPanel.this);
		} else {
			for (int i=0;i<listeners.size();i++) {
				((ResizeListener)listeners.get(i)).onResize(TrueHorizontalSplitPanel.this);
			}
		}
	}
	public void addResizeListener(ResizeListener listener) {
		listeners.add(listener);
	}

	public void removeResizeListener(ResizeListener listener) {
		listeners.remove(listener);
	}

	public boolean isSplitEnabled() {
		return splitEnabled;
	}

	
	
	public void setSplitEnabled(boolean splitEnabled) {
		if (splitEnabled) {
			if (this.splitEnabled=false) {
				setSplitElement("<table class='hsplitter' height='100%' cellpadding='0' "
			            + "cellspacing='0'><tr><td align='center' valign='middle'>"
			            + abstractImagePrototype.getHTML());
			}
		} else {
			if (this.splitEnabled) {
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
	public class HorizontalPanelSplitter implements HorizontalSplitPanelImages {
		public AbstractImagePrototype horizontalSplitPanelThumb() {
			return abstractImagePrototype;
		};
	}
}
