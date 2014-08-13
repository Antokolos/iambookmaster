package com.iambookmaster.client.iphone;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.common.SpanLabel;
import com.iambookmaster.client.iphone.common.IPhoneButton;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.player.PlayerState;

public class IPhonePlayerList {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	private static IPhoneStyles css = IPhoneImages.INSTANCE.css();
	
	private PlayerState playerState;
//	private PlayerStateListener playerStateListener;
	private Model model;
	private IPhoneCanvas canvas;
	private IPhoneViewListenerAdapter listener;
	private IPhoneViewListener owner;
	private ClickHandler close;

	private IPhonePlayerListListener menuListener;
	public void show(IPhoneViewListener owner,boolean leftToRight) {
		this.owner = owner;
		canvas.setListener(listener);
		draw(true,leftToRight);
	}

	private void draw(boolean animate,boolean leftToRight) {
		if (animate) {
			canvas.clearWithAnimation(leftToRight);
		} else {
			canvas.clear();
		}
		canvas.setPageOrientation(true);
		Label label = new Label(appConstants.playerPlayerList());
		label.setStyleName(css.playListTitle());
		canvas.add(label);
		update();
		canvas.add(new HTML("<br/>"));
		IPhoneButton button = new IPhoneButton(appConstants.iphoneContinueGame(), close);
//		label = new Label(appConstants.iphoneContinueGame());
//		label.addClickHandler(close);
//		label.setStyleName(css.stateSelection());
		canvas.add(button);
		canvas.addClickHandler(button, close);
		canvas.done();
	}
	
	public IPhonePlayerList(Model model,PlayerState ps, IPhoneCanvas cv,IPhonePlayerListListener lst) {
		this.model = model;
		this.canvas = cv;
		this.menuListener = lst;
		this.playerState = ps;
		listener = new IPhoneViewListenerAdapter() {
			@Override
			public void redraw(IPhoneCanvas viewer) {
				draw(false,false);
			}

			@Override
			public void forward() {
				menuListener.forward();
			}

			@Override
			public void back() {
				menuListener.back();
			}

		};
		close = new ClickHandler() {
			public void onClick(ClickEvent event) {
				menuListener.forward();
			}
		};
	}
	
	private void update() {
		boolean next=false;
		LinkedHashMap<Parameter, Integer> params = playerState.getParameters();
		if (params.size()>0) {
			HashSet<Parameter> used = new HashSet<Parameter>(params.size());
			boolean first=true;
			for (Parameter parameter : params.keySet()) {
				if (used.contains(parameter) || parameter.isInvisible()) {
					//already used or invisible
					continue;
				}
				int vl = params.get(parameter);
				if (vl==0) {
					//do not show zero value
					continue;
				}
				if (first) {
					first = false;
					Label label = new Label(appConstants.urlListParameters());
					label.setStyleName(css.playListItemsTitle());
					next = true;
					canvas.add(label);
				}
				//add parameter to the list
				ParameterWidget widget = new ParameterWidget(parameter); 
				canvas.add(widget);
				widget.apply(parameter, params,vl);
				if (parameter.getLimit() != null) {
					used.add(parameter.getLimit());
				}
			}
		}
		if (playerState.isBagEmpty()==false) {
			Label label = new Label(appConstants.playerListObjects());
			label.setStyleName(css.playListItemsTitle());
			canvas.add(label);
			next = true;
			Iterator<ObjectBean> iter = playerState.getObjectIterator();
			FlowPanel panel = new FlowPanel();
			panel.getElement().getStyle().setMarginTop(15, Unit.PX);
			while (iter.hasNext()) {
				ObjectBean object = (ObjectBean) iter.next();
				if (object.getIcon()==null) {
					ItemWidget widget = new ItemWidget(object);
					panel.insert(widget,0);
					canvas.addClickHandler(widget, widget);
				} else {
					ItemWidgetImage widget = new ItemWidgetImage(object);					
					panel.insert(widget,0);
					canvas.addClickHandler(widget, widget);
				}
			}
			canvas.add(panel);
		}
		//get available everywhere in peaceful time alchemy
		ArrayList<Alchemy> list = playerState.getAlchemy(false,false);
		if (list != null) {
			Label label = new Label(appConstants.iphoneAvailableAlchemy());
			label.setStyleName(css.playListItemsTitle());
			canvas.add(label);
			for (Alchemy alchemy : list) {
				AlchemyWidget widget = new AlchemyWidget(alchemy);
				canvas.addClickHandler(widget, widget);
				canvas.add(widget);
			}
		}
		
		if (model.getSettings().isShowModificators()) {
			if (playerState.getModificators().size()>0) {
				Label label = new Label(AppLocale.getAppConstants().playerListModificators());
				label.setStyleName(css.playListItemsTitle());
				if (next) {
					//TODO
				}
				next = true;
				canvas.add(label);
				for (Modificator modificator : playerState.getModificators()) {
					canvas.add(new ModificatorWidget(modificator));
				}
			}
		}
		
	}

	private void objectSelected(ObjectBean object) {
		playerState.selectObject(object);
	}
	
	public class ModificatorWidget extends Label {
		public ModificatorWidget(Modificator modificator) {
			if (model.getSettings().isVerticalObjects()) {
				setText(modificator.getName());
			} else {
				setText(modificator.getName()+",");
			}
			setWordWrap(false);
			if (canvas.isVertical()==false) {
				getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			}
		}
		
	}
	
	public class ParameterWidget extends FlowPanel {
		private Parameter parameter;
		private Label name;
		private Label value;
		public ParameterWidget(Parameter parameter) {
			this.parameter = parameter;
			name = new SpanLabel();
			add(name);
			value = new SpanLabel();
			add(value);
			setStyleName(css.playListParameter());
			if (canvas.isVertical() || model.getSettings().isVerticalObjects()) {
			} else {
				getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			}
			name.setText(parameter.getName()+":");
			value.setStyleName(css.playListParameterValue());
		}
		
		public void apply(Parameter param, Map<Parameter, Integer> params, int vl) {
			if (parameter.getLimit()==null || parameter.getLimit().isInvisible()) {
				//no limit
				if (param.isSuppressOneValue() && vl==1) {
					value.setVisible(false);
					name.setText(parameter.getName());
				} else {
					if (value.isVisible()==false) {
						value.setVisible(true);
						name.setText(parameter.getName()+":");
					}
					value.setText(String.valueOf(vl));
				}
 			} else {
 				//update value/limit
 				value.setText(String.valueOf(vl)+"/"+params.get(parameter.getLimit()));
 			}
		}
		
	}
	
	public class AlchemyWidget extends Label implements ClickHandler{
		private Alchemy alchemy;
		
		public AlchemyWidget(Alchemy alchemy) {
			this.alchemy = alchemy; 
			setText(alchemy.getName());
			setStyleName(css.playListObject());
			addClickHandler(this);
			if (canvas.isVertical()==false) {
				getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			}
		}

		public void onClick(ClickEvent event) {
			if (playerState.apply(alchemy)) {
				draw(true,false);
			}
		}
	}
	
	public class ItemWidget extends Label implements ClickHandler{
		private ObjectBean object;
		public ItemWidget(ObjectBean obj) {
			this.object = obj;
			setText(object.getName());
			if (model.getSettings().isHiddenUsingObjects()) {
				setStyleName(css.playListObject());
				setTitle(appConstants.playerClickToUse());
			} else {
				setStyleName(css.playListObjectPassive());
			}
			addClickHandler(this);
			if (canvas.isVertical()==false) {
				getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			}
		}
		
		public void onClick(ClickEvent event) {
//			IPhoneConsole.addMessage("object "+object.getName()+", hidden="+model.getSettings().isHiddenUsingObjects());
			if (model.getSettings().isHiddenUsingObjects()) {
				objectSelected(object);
			}
		}

	}

	public class ItemWidgetImage extends Image implements ClickHandler{
		private ObjectBean object;
		public ItemWidgetImage(ObjectBean obj) {
			this.object = obj;
			//TODO big version
			setUrl(obj.getIcon().getUrl());
			if (model.getSettings().isHiddenUsingObjects()) {
//				setStyleName(css.playListObject());
				setTitle(appConstants.playerClickToUse());
			} else {
//				setStyleName(css.playListObjectPassive());
			}
			addClickHandler(this);
//			if (canvas.isVertical()==false) {
//				getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
//			}
		}
		
		public void onClick(ClickEvent event) {
			if (model.getSettings().isHiddenUsingObjects()) {
				objectSelected(object);
			}
		}
	}
}
