package com.iambookmaster.client.iphone;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.iambookmaster.client.common.Base64Coder;
import com.iambookmaster.client.iphone.common.IPhoneButton;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.player.PlayerState;

public class IPhoneDebugPanel {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	private static final IPhoneStyles css = IPhoneImages.INSTANCE.css();
	
	private PlayerState playerState;
	private IPhoneDebugPanelListener owner;
	private ClickHandler back;
	private IPhoneViewListenerAdapter listener;
	private ClickHandler stepBack;
	private ClickHandler saveGame;
	private ArrayList<String> states = new ArrayList<String>();
	private ClickHandler killAll;
	
	public IPhoneDebugPanel(PlayerState ps) {
		this.playerState = ps;
		listener = new IPhoneViewListenerAdapter(){

			@Override
			public void back() {
			}

			@Override
			public void redraw(IPhoneCanvas viewer) {
				draw(viewer,false,false);
			}

			
			@Override
			public void forward() {
			}

			@Override
			public void drawn() {
			}

		};
		back = new ClickHandler() {
			public void onClick(ClickEvent event) {
				owner.close();
			}
		};
		stepBack = new ClickHandler() {
			public void onClick(ClickEvent event) {
				playerState.goBack();
				owner.close();
			}
		};
		saveGame = new ClickHandler() {
			public void onClick(ClickEvent event) {
				states.add(playerState.toJSON());
				owner.close();
			}
		};
		killAll = new ClickHandler() {
			public void onClick(ClickEvent event) {
				playerState.killAllOpponents();
				owner.close();
			}
		};
	}
	
	private void draw(IPhoneCanvas canvas,boolean animate,boolean leftToRight) {
//		this.canvas = canvas;
		canvas.setListener(listener);
		if (animate) {
			canvas.clearWithAnimation(leftToRight);
		} else {
			canvas.clear();
		}
		Label label;
		label = new Label("Debug Mode");
		label.setStyleName(css.bookName());
		canvas.add(label);
		if (playerState.isHasBackState()) {
			addButton(canvas, "На шаг назад",stepBack);
		}
		
		addButton(canvas, "Сохранить игру",saveGame);
		
		if (playerState.isBattleActive()) {
			addButton(canvas, "Убить всех",killAll);
		}
		
		int count = states.size();
		for (int i = 0; i < count; i++) {
			addButton(canvas, "Игра "+i,new RestoreGameClickHandler(i));
		}
		addButton(canvas, appConstants.iphoneThankyouCancel(),back);
		canvas.done();
	}

	private void addButton(IPhoneCanvas canvas, String title, ClickHandler handler) {
		IPhoneButton button = new IPhoneButton(title,handler);
		canvas.add(button);
		canvas.addClickHandler(button, handler);
		if (canvas.isVertical()) {
			button.setWidth(IPhoneViewerOldBook.toPixels(canvas.getClientWidth()-30));
		}
	}
	
	public void show(IPhoneDebugPanelListener owner, IPhoneCanvas canvas,boolean leftToRight) {
		this.owner = owner;
		draw(canvas,true,leftToRight);
	}
	
	public class RestoreGameClickHandler implements ClickHandler {

		private int index;

		public RestoreGameClickHandler(int index) {
			this.index = index;
		}

		public void onClick(ClickEvent event) {
			playerState.restoreState(Base64Coder.encodeString(states.get(index)));
			owner.close();
		}
		
	}

}
