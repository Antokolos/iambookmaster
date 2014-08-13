package com.iambookmaster.client.common;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class WidgetsLine extends ComplexPanel {

		public WidgetsLine() {
			super();
			Element element = DOM.createDiv();
			setElement(element);
			setSize("100%", "100%");
			DOM.setStyleAttribute(element, "whiteSpace", "normal");
		}
		
		public void add(Widget widget) {
			if (widget instanceof Label) {
				Label label = (Label)widget;
				appendInSpan(label,label.getWordWrap());
			} else {
				super.add(widget,getElement());
			}
		}

		private void appendInSpan(final Widget widget, boolean wordWrap) {
			Element el = DOM.createSpan();
			Element oldEl = widget.getElement();
			DOM.setInnerHTML(el, DOM.getInnerHTML(oldEl));
			String style = widget.getStyleName();
			if (style != null && style.length()>0) {
				DOM.setElementProperty(el, "className", style);
			}
			if (wordWrap) {
				DOM.setStyleAttribute(el, "whiteSpace", "normal");
			} else {
				DOM.setStyleAttribute(el, "whiteSpace", "nowrap");
			}
			EventListener eventListener = new EventListener() {

				public void onBrowserEvent(Event event) {
					widget.onBrowserEvent(event);
				}
				
			};
			DOM.setEventListener(el, eventListener);
			DOM.sinkEvents(el, DOM.getEventsSunk(oldEl));
			DOM.appendChild(getElement(), el);
		}

		public void add(String text) {
			addString(getElement(),text);
		}

		private native void addString(Element e,String t) /*-{
			e.innerHTML=e.innerHTML+t;
		}-*/;

		public void addNBSP() {
			add("&nbsp;");
		}
		
		public void clear() {
			DOM.setInnerHTML(getElement(), "");
		}
}
