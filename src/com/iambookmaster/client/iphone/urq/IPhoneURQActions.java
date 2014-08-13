package com.iambookmaster.client.iphone.urq;

import java.util.Vector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.iambookmaster.client.iphone.IPhoneCanvas;
import com.iambookmaster.client.iphone.IPhoneViewListenerAdapter;
import com.iambookmaster.client.iphone.common.IPhoneButton;
import com.iambookmaster.client.iphone.common.IPhoneFlatButton;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;
import com.iambookmaster.client.iurq.Core;
import com.iambookmaster.client.iurq.logic.InvVar.Action;

public class IPhoneURQActions extends IPhoneViewListenerAdapter {
	
	static final IPhoneStyles css = IPhoneImages.INSTANCE.css();
	
	private IPhoneURQInventoryListener listener;
	private IPhoneCanvas canvas;
	private Core core;
	private Vector<Action> actions;
	private String actionName;
	private ClickHandler backHandler;

	public IPhoneURQActions(IPhoneURQInventoryListener lst) {
		listener = lst;
		backHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				listener.back();
			}
		};
	}

	public void show(IPhoneCanvas canvas,Core core,String actionName,Vector<Action> actions) {
		this.canvas = canvas;
		this.core = core;
		this.actions = actions;
		this.actionName = actionName;
		canvas.setListener(this);
		redraw(canvas);
	}

	public void redraw(IPhoneCanvas canvas) {
		canvas.clear();
		Label label = new Label(actionName);
		label.setStyleName(css.urqScreenTitle());
		canvas.add(label);
		for (Action action : actions) {
			ClickHandler handler = new ActionClickHandler(action);
			IPhoneFlatButton button = new IPhoneFlatButton(action.getLocName().substring(actionName.length()+5));
			canvas.add(button);
			canvas.addClickHandler(button, handler);
		}
		
		IPhoneButton button = new IPhoneButton("Назад");
		canvas.addClickHandler(button, backHandler);
		canvas.add(button);
		
		canvas.done();
	}

	public void back() {
		listener.back();
	}

	public void forward() {
		listener.forward();
	}

	public void drawn() {
	}
	
	public class ActionClickHandler implements ClickHandler {
		
		private Action action;

		public ActionClickHandler(Action action) {
			this.action = action;
		}

		public void onClick(ClickEvent event) {
			listener.doAction(action);
		}
		
	}

}
