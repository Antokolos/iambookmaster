package com.iambookmaster.client.iphone.common;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

public abstract class IPhoneTouchListener {
	
	public final static IPhoneTouchProvider provider = GWT.create(IPhoneTouchProvider.class);
	
	public final static String TOUCHSTART = provider.getStartEvent();
	public final static String TOUCHMOVE = provider.getMoveEvent();
	public final static String TOUCHEND = provider.getEndEvent();
	private static final String TOUCHCANCEL = provider.getCancelEvent();
	
	private HashMap<JavaScriptObject, HandlerManager> handlers = new HashMap<JavaScriptObject, HandlerManager>();
	
	private boolean eventsEnabled=true;
	
	public boolean isEventsEnabled() {
		return eventsEnabled;
	}

	public void setEventsEnabled(boolean eventsEnabled) {
		this.eventsEnabled = eventsEnabled;
	}

//	public HashMap<JavaScriptObject, HandlerManager> getHandlers() {
//		return handlers;
//	}

//	public void setHandlers(HashMap<JavaScriptObject, HandlerManager> handlers) {
//		this.handlers = handlers;
//	}

	public void resetHandlers() {
		handlers.clear();
	}
	
	public JavaScriptObject findClosestTarget(int initialX, int initialY,int distance) {
		JavaScriptObject best = null;
		int bestX=0;
		int bestY=0;
		for (JavaScriptObject object : handlers.keySet()) {
			com.google.gwt.user.client.Element element = (com.google.gwt.user.client.Element) object;
			int l = element.getAbsoluteLeft();
			int w = element.getOffsetWidth();
			int t = element.getAbsoluteTop();
			int h = element.getOffsetHeight();
			l = difference(l,w,initialX);
			t = difference(t,h,initialY);
			if (l<=distance && t<=distance) {
				if (best==null || bestX+bestY>l+t) {
					bestX = l;
					bestY = t;
					best = object;
				}
			}
		}
		return best;
	}
	
	private int difference(int left, int width, int x) {
		if (left>x) {
			return left-x;
		} else if (left+width>x){
			return 0;
		} else {
			return x-left-width; 
		}
	}

	public void addListener(Element element,String name,boolean cartupe){
		_addListener(element,name,cartupe);
		if (TOUCHEND.equals(name) && TOUCHCANCEL != null) {
			_addListener(element,TOUCHCANCEL,cartupe);
		} 
	}
	
	private int[] createArray(int a0) {
		return new int[]{a0};
	}
	private int[] createArray(int a0,int a1) {
		return new int[]{a0,a1};
	}
	private int[] createArray(int a0,int a1,int a2) {
		return new int[]{a0,a1,a2};
	}
		
	public native void _addListener(Element element,final String name,boolean cartupe) /*-{
		var self = this;
		var f = function(e) {
			if (e.preventDefault) {
				e.preventDefault();
			}
			var aX;
			var aY;
			if (e.touches) {
				var tr = e.targetTouches[0].target;
				if(tr.nodeType == 3) {
					tr = tr.parentNode;
				}
				if (e.touches.length==1) {
					aX = self.@com.iambookmaster.client.iphone.common.IPhoneTouchListener::createArray(I)(e.touches[0].clientX);
					aY = self.@com.iambookmaster.client.iphone.common.IPhoneTouchListener::createArray(I)(e.touches[0].clientY);
				} else if (e.touches.length==2) {
					aX = self.@com.iambookmaster.client.iphone.common.IPhoneTouchListener::createArray(II)(e.touches[0].clientX,e.touches[1].clientX);
					aY = self.@com.iambookmaster.client.iphone.common.IPhoneTouchListener::createArray(II)(e.touches[0].clientY,e.touches[1].clientY);
				} else {
					aX = self.@com.iambookmaster.client.iphone.common.IPhoneTouchListener::createArray(III)(e.touches[0].clientX,e.touches[1].clientX,e.touches[2].clientX);
					aY = self.@com.iambookmaster.client.iphone.common.IPhoneTouchListener::createArray(III)(e.touches[0].clientY,e.touches[1].clientY,e.touches[2].clientY);
				}
			} else {
				aX = self.@com.iambookmaster.client.iphone.common.IPhoneTouchListener::createArray(I)(e.clientX);
				aY = self.@com.iambookmaster.client.iphone.common.IPhoneTouchListener::createArray(I)(e.clientY);
			}
			self.@com.iambookmaster.client.iphone.common.IPhoneTouchListener::_event(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;[I[ILcom/google/gwt/core/client/JavaScriptObject;)(element,name,aX,aY,tr);
			return false;
		};
		if (element.addEventListener){  
			element.addEventListener(name, f, cartupe);
		} else {
			element.attachEvent('on'+name, f);  
		}
	}-*/;
	
	private boolean _event(JavaScriptObject source,String name,int[] x, int[] y,JavaScriptObject target) {
		return event(source,name,x,y,target);
	}
	public abstract boolean event(JavaScriptObject source,String name,int[] x, int[] y,JavaScriptObject target);

	public boolean superEvent(JavaScriptObject source,String name,int[] x, int[] y,JavaScriptObject target) {
		if (eventsEnabled==false) {
			return false;
		}
		HandlerManager manager = handlers.get(target);
		if (manager != null) {
			//simulate click event
			manager.fireEvent(new IPhoneClickEvent());
		}	
		return false;
	}
	
	public boolean hasHandler(JavaScriptObject target) {
		return handlers.containsKey(target);
	}

	public HandlerRegistration addClickHandler(Widget widget,ClickHandler handler) {
		Element element;
		if (widget instanceof HasSubElement) {
			element = ((HasSubElement) widget).getSubElement();
		} else {
			element = widget.getElement();
		}
		Element el = element.getFirstChildElement();
		if (el != null) {
			//complex element
			addClickHandler(el,widget,handler);
		}
		return addClickHandler(element,widget,handler);
	}

	private HandlerRegistration addClickHandler(Element element, Widget widget, ClickHandler handler) {
		if (handlers.containsKey(element)) {
			return handlers.get(element).addHandler(ClickEvent.getType(), handler);
		} else {
			HandlerManager manager = new HandlerManager(widget);
			handlers.put(element,manager);
			return manager.addHandler(ClickEvent.getType(), handler);
		}
	}

	public class IPhoneClickEvent extends ClickEvent {
	}	

	public boolean isIE() {
		return provider.isIE();
	}
}
