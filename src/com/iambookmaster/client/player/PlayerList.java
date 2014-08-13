package com.iambookmaster.client.player;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.NPC;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.common.AnimationTimer;
import com.iambookmaster.client.common.ColorProvider;
import com.iambookmaster.client.common.SpanLabel;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;

public class PlayerList extends VerticalPanel {

	private PlayerState playerState;
	private PlayerStateListener playerStateListener;
	private FlowPanel bag;
	private FlowPanel parameters;
	private FlowPanel modificators;
	private Model model;
	private AnimationTimer animationTimer;
	private Label bagLabel;
	
	public PlayerList(Model model,PlayerState ps) {
		this.model = model;
		animationTimer = new AnimationTimer();
		setSize("100%", "100%");
		setStyleName(PlayerStyles.BAG_STYLE);
		if (model.getSettings().getBagColor() != 0) {
			DOM.setStyleAttribute(getBody(), "backgroundColor", ColorProvider.getColorName(model.getSettings().getBagColor()));
		}
		this.playerState = ps;
		playerStateListener = new PlayerStateListener() {
			public void addObject(ObjectBean object) {
				addObjectToBag(object);
			}
			public void lostObject(ObjectBean object) {
				removeObjectFromBag(object);
			}
			
			public void removeObject(ObjectBean object) {
				removeObjectFromBag(object);
			}

			public void useObject(ObjectBean object,boolean success) {
				//not for us
			}

			public void reset() {
				 update();
				
			}

			public void finish() {
				int l = bag.getWidgetCount();
				for (int i = 0; i < l; i++) {
					Widget widget = bag.getWidget(i);
					if (widget instanceof ItemWidget) {
						ItemWidget itemWidget = (ItemWidget) widget;
						itemWidget.finish();
					}
				}
				
			}
			public void changeParameter(Parameter parameter, int value) {
				int l = parameters.getWidgetCount();
				for (int i = 0; i < l; i++) {
					ParameterWidget widget = (ParameterWidget)parameters.getWidget(i);
					if (widget.parameter==parameter || widget.parameter.getLimit()==parameter) {
						if (widget.parameter==parameter && value==0) {
							parameters.remove(i);
						} else {
							widget.apply(parameter, playerState.getParameters(),true);
						}
						return;
					}
				}
				if (parameter.isInvisible()) {
					//do not show
					return;
				}
				//not found, add
				if (l==0) {
					parameters.addStyleName(PlayerStyles.PARAMETERS_PANEL);
				}
				ParameterWidget widget = new ParameterWidget(parameter);
				parameters.add(widget);
				widget.apply(parameter, playerState.getParameters(),true);
			}
			
			public void battle(Battle parameter, boolean start) {
			}
			
			public void changeModificator(Modificator modificator, boolean add) {
				if (modificators != null) {
					int l = modificators.getWidgetCount();
					for (int i = 1; i < l; i++) {
						ModificatorWidget widget = (ModificatorWidget)modificators.getWidget(i);
						if (widget.modificator==modificator) {
							//exists
							if (add==false) {
								if (l==1) {
									modificators.removeStyleName(PlayerStyles.MODIFICATORS_PANEL);
								}
								modificators.remove(i);
							}
							return;
						}
					}
					if (add) {
						//not exist - add
						if (l==0) {
							modificators.addStyleName(PlayerStyles.MODIFICATORS_PANEL);
						}
						modificators.add(new ModificatorWidget(modificator));
					}
				}
			}
			
			public void enemy(NPC npc, boolean add) {
			}
			public void disableConnection(ParagraphConnection connection) {
			}
			public void enableConnection(ParagraphConnection connection) {
			}

		};
		playerState.addPlayerStateListener(playerStateListener);
		Label label = new Label(AppLocale.getAppConstants().playerPlayerList());
		label.setStyleName(PlayerStyles.BAG_TITLE);
		add(label);
		setCellHorizontalAlignment(label,HasHorizontalAlignment.ALIGN_CENTER);
		setCellHeight(label,"1%");
		setCellWidth(label,"100%");
		parameters = new FlowPanel();
		parameters.setSize("100%", "100%");
		if (model.getSettings().getTextColor() != 0) {
			DOM.setStyleAttribute(parameters.getElement(), "color", ColorProvider.getColorName(model.getSettings().getTextColor()));
		}
		add(parameters);
		setCellHeight(parameters,"1%");
		setCellWidth(parameters,"100%");
		bag = new FlowPanel();
		bag.setStyleName(PlayerStyles.BAG);
		if (model.getSettings().getTextColor() != 0) {
			DOM.setStyleAttribute(bag.getElement(), "color", ColorProvider.getColorName(model.getSettings().getTextColor()));
		}
//		bag.setSize("100%", "100%");
		add(bag);
		if (model.getSettings().isShowModificators()) {
			setCellHeight(bag,"1%");
		} else {
			setCellHeight(bag,"99%");
		}
		setCellWidth(bag,"100%");
		if (model.getSettings().isShowModificators()) {
			modificators = new FlowPanel();
			modificators.setStyleName(PlayerStyles.MODIFICATORS_PANEL);
			if (model.getSettings().getTextColor() != 0) {
				DOM.setStyleAttribute(modificators.getElement(), "color", ColorProvider.getColorName(model.getSettings().getTextColor()));
			}
			modificators.setSize("100%", "100%");
			add(modificators);
			
			HTML html = new HTML("&nbsp");
			html.setStyleName(PlayerStyles.FILLER);
			add(html);
			setCellHeight(html,"99%");
			setCellWidth(html,"100%");
		}
		
		update();
	}
	
	public void setWidth(String width) {
		super.setWidth(width);
		if (bag != null) {
			bag.setWidth(width);
		}
		if (parameters != null) {
			parameters.setWidth(width);
		}
		if (modificators != null) {
			modificators.setWidth(width);
		}
		
	}

	private void update() {
		parameters.clear();
		if (bagLabel != null) {
			remove(bagLabel);
		}
		bag.clear();
		if (playerState.isBagEmpty()==false) {
			Iterator<ObjectBean> iter = playerState.getObjectIterator();
			while (iter.hasNext()) {
				ObjectBean object = (ObjectBean) iter.next();
				addObjectToBag(object);
			}
		}
		LinkedHashMap<Parameter, Integer> params = playerState.getParameters();
		if (params.size()>0) {
			HashSet<Parameter> used = new HashSet<Parameter>(params.size());
			for (Parameter parameter : params.keySet()) {
				if (parameter.isInvisible()) {
					continue;
				}
				if (used.contains(parameter)) {
					//already used
					continue;
				}
				//add parameter to the list
				ParameterWidget widget = new ParameterWidget(parameter); 
				parameters.add(widget);
				widget.apply(parameter, params,false);
				if (parameter.getLimit() != null) {
					used.add(parameter.getLimit());
				}
			}
			parameters.addStyleName(PlayerStyles.PARAMETERS_PANEL);
		} else {
			parameters.removeStyleName(PlayerStyles.PARAMETERS_PANEL);
		}
		if (modificators != null) {
			modificators.clear();
			if (playerState.getModificators().size()>0) {
				modificators.addStyleName(PlayerStyles.MODIFICATORS_PANEL);
			} else {
				modificators.removeStyleName(PlayerStyles.MODIFICATORS_PANEL);
			}
			if (playerState.getModificators() != null && playerState.getModificators().size()>0) {
				for (Modificator modificator : playerState.getModificators()) {
					modificators.add(new ModificatorWidget(modificator));
				}
			}
		}
		
	}

	private void addObjectToBag(ObjectBean object) {
		if (bag.getWidgetCount()==0) {
			bagLabel = object.getIcon() == null ? new SpanLabel() : new Label();
			if (model.getSettings().isHiddenUsingObjects()) {
				bagLabel.setText(AppLocale.getAppConstants().playerListObjects());
			} else {
				bagLabel.setText(AppLocale.getAppConstants().playerListObjectsNoUse());
			}
			if (model.getSettings().getTextColor() != 0) {
				DOM.setStyleAttribute(bagLabel.getElement(), "color", ColorProvider.getColorName(model.getSettings().getTextColor()));
			}
			bagLabel.setStyleName(PlayerStyles.BOLD);
			insert(bagLabel,getWidgetIndex(bag));
		}
		ItemWidget widget = new ItemWidget(object);
		bag.add(widget);
	}

	private void removeObjectFromBag(ObjectBean object) {
		if (bag.getWidgetCount()>0) {
//			bag.add(new SpanLabel(", "));
			for (int i = 0; i < bag.getWidgetCount(); i++) {
				Widget widget = bag.getWidget(i);
				if (widget instanceof ItemWidget) {
					ItemWidget itemWidget = (ItemWidget) widget;
					if (itemWidget.object==object) {
						bag.remove(widget);
						break;
					}
				}
			}
		}
	}

	private void objectSelected(ObjectBean object) {
		playerState.selectObject(object);
	}
	
	public class PlayerListWidget extends Label {
		public PlayerListWidget() {
			super(model.getSettings().isVerticalObjects() ? DOM.createDiv() : DOM.createSpan());
		}
	}
	public class ModificatorWidget extends PlayerListWidget {
		
		private Modificator modificator;

		public ModificatorWidget(Modificator modificator) {
			if (model.getSettings().isVerticalObjects()) {
				setText(modificator.getName());
			} else {
				setText(modificator.getName()+",");
			}
			setWordWrap(false);
			setStyleName(PlayerStyles.MODIFICATOR);
			if (modificators.getWidgetCount()==0) {
				SpanLabel label = new SpanLabel(AppLocale.getAppConstants().playerListModificators());
				label.setStyleName(PlayerStyles.BOLD);
				modificators.add(label);
			}
			this.modificator = modificator;
		}
		
	}
	
	public class ParameterWidget extends PlayerListWidget {

		private Parameter parameter;
		public ParameterWidget(Parameter parameter) {
			this.parameter = parameter;
			setStyleName(PlayerStyles.HERO_LIST_PARAMETER);
			if (model.getSettings().isVerticalObjects()) {
				setWordWrap(false);
			}
		}
		
		public void apply(Parameter param, Map<Parameter, Integer> params,boolean animate) {
			int value = params.get(parameter);
			if (parameter.getLimit()==null) {
				if (parameter.isSuppressOneValue() && value==1) {
					//show name only
					setText(AppLocale.getAppMessages().playerParameterOneValue(parameter.getName()));
				} else {
					//no limit
					setText(AppLocale.getAppMessages().playerParameterValue(parameter.getName(),value));
				}
 			} else {
 				Integer limit = params.get(parameter.getLimit());
 				if (limit==null || parameter.getLimit().isInvisible()) {
 					//no limit
 					setText(AppLocale.getAppMessages().playerParameterValue(parameter.getName(),value));
 				} else {
 					//update value/limit
 					setText(AppLocale.getAppMessages().playerParameterValueLimit(parameter.getName(),value, params.get(parameter.getLimit())));
 				}
 			}
			if (animate) {
				animationTimer.add(this);
			}
		}
		
	}
	
	public class ItemWidget extends FlowPanel implements MouseListener,LoadHandler{
		private ObjectBean object;
		private Label text;
		private boolean big;
		private Image image;
		
		public ItemWidget(ObjectBean object) {
			setStyleName(PlayerStyles.OBJECT_IN_BAG);
			this.object = object;
			SourcesMouseEvents widget;
			if (object.getIcon()==null) {
				getElement().getStyle().setDisplay(model.getSettings().isVerticalObjects() ? Display.BLOCK : Display.INLINE_BLOCK);
				setTitle(AppLocale.getAppConstants().playerClickToUse());
				text = new Label();
				if (model.getSettings().isVerticalObjects()) {
					text.setText(object.getName());
					text.setWordWrap(false);
				} else {
					text.setText(object.getName()+",");
				}
				widget = text;
				add(text);
			} else {
				getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
				setTitle(object.getName());
				image = new Image();
				image.addLoadHandler(this);
				if (object.getIcon().getBigUrl()==null) {
					image.setUrl(object.getIcon().getUrl() );
				} else {
					big = true;
					image.setUrl(object.getIcon().getBigUrl());
				}
				add(image);
				widget = image;
			}
			if (model.getSettings().isHiddenUsingObjects()) {
				addStyleName(PlayerStyles.CLICKABLE);
				widget.addMouseListener(this);
			}
			animationTimer.add(this);
		}
		public void finish() {
			setTitle(null);
			removeStyleName(PlayerStyles.CLICKABLE);
		}
		
		public void onMouseDown(Widget sender, int x, int y) {
			objectSelected(object);
		}
		public void onMouseEnter(Widget sender) {
			if (text==null) {
				
			} else {
				addStyleName(PlayerStyles.BAG_SELECTED);
			}
		}
		public void onMouseLeave(Widget sender) {
			if (text==null) {
				
			} else {
				removeStyleName(PlayerStyles.BAG_SELECTED);
			}
		}
		public void onMouseMove(Widget sender, int x, int y) {
		}
		public void onMouseUp(Widget sender, int x, int y) {
		}
		public void onLoad(LoadEvent event) {
			if (big) {
				object.getIcon().setBigWidht(image.getWidth());
				object.getIcon().setBigHeight(image.getHeight());
			} else {
				object.getIcon().setWidht(image.getWidth());
				object.getIcon().setHeight(image.getHeight());
			}
		}
		
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		animationTimer.cancel();
	}
	
	

}
