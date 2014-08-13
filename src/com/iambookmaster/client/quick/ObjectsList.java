package com.iambookmaster.client.quick;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.Images;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.common.ScrollContainer;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ObjectListener;

public class ObjectsList extends ScrollContainer {
	
	private VerticalPanel mainPanel;
	private Model model;
	private ObjectListener objectListener;
	private ObjectWidget selected;
	private boolean activationNeed=true;
	
	public void activate() {
		if (activationNeed) {
			activationNeed = false;
			resetHeight();
		}
	}
	public void activateLater() {
		activationNeed = true;
	}
	
	public ObjectsList(Model model) {
		mainPanel = new VerticalPanel();
		mainPanel.setSize("100%", "100%");
		mainPanel.setSpacing(3);
		addStyleName("objects_list");
		setScrollWidget(mainPanel);
		this.model = model;
		objectListener = new ObjectListener() {

			public void addNewObject(ObjectBean object) {
				ObjectWidget widget = new ObjectWidget(object);
				int pos = getWidgetCount();
				mainPanel.insert(widget,pos-1);
				items.put(widget.object, widget);
				mainPanel.setCellHeight(widget,"1%");
			}

			public void refreshAll() {
				reloadTree();
			}

			public void select(ObjectBean object) {
				ObjectWidget widget = items.get(object);
				if (widget != null) {
					widget.select();
				}
			}

			public void update(ObjectBean object) {
				ObjectWidget widget = items.get(object);
				if (widget != null) {
					widget.apply(object);
				}
			}

			public void unselect(ObjectBean object) {
				ObjectWidget widget = items.get(object);
				if (widget != null) {
					widget.unselect();
				}
			}

			public void remove(ObjectBean object) {
				ObjectWidget widget = items.get(object);
				if (widget != null) {
					mainPanel.remove(widget);
				}
			}

			public void showInfo(ObjectBean object) {
			}

			
		};
		model.addObjectsListener(objectListener);
		reloadTree();
	}
	
	private void removeObject(ObjectBean bean, ObjectWidget widget) {
		if (Window.confirm(AppLocale.getAppConstants().quickRemoveObjectTitle())) {
			model.removeObjects(bean);
		}
	}
	
	private HashMap<ObjectBean, ObjectWidget> items;
	
	private void reloadTree() {
		items = new HashMap<ObjectBean, ObjectWidget>();
		ArrayList<ObjectBean> beans = model.getObjects();
		mainPanel.clear();
		int l = beans.size();
		for (int i = 0; i < l; i++) {
			ObjectWidget widget = new ObjectWidget(beans.get(i));
			mainPanel.add(widget);
			items.put(widget.object, widget);
			mainPanel.setCellHeight(widget,"1%");
		}
		HTML html = new HTML("&nbsp;");
		html.setStyleName("location_list_filler");
		mainPanel.add(html);
		mainPanel.setCellHeight(html,"99%");
		
	}

	public class ObjectWidget extends HorizontalPanel {
		private Label name;
		private Image remove;
		private Image findInfo;
		private ObjectBean object;
		public ObjectWidget(ObjectBean obj) {
			setSize("100%", "100%");
			setSpacing(3);
			name = new Label();
			name.setStyleName(Styles.CLICKABLE);
			name.setWordWrap(false);
			ClickHandler clickHandler = new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (event.getSource()==remove) {
						removeObject(object,ObjectWidget.this);
					} else if (event.getSource()==findInfo) {
						model.showInfo(object);
					} else {
						select();
					}
				}
			};
			name.addClickHandler(clickHandler);
			add(name);
			setCellWidth(name, "1%");
			findInfo = new Image(Images.PREVIEW);
			findInfo.addClickHandler(clickHandler);
			findInfo.setTitle(AppLocale.getAppConstants().quicShowInfo());
			add(findInfo);
			setCellWidth(findInfo, "1%");
			remove = new Image(Images.REMOVE);
			remove.addClickHandler(clickHandler);
			remove.setTitle(AppLocale.getAppConstants().quickRemoveObject());
			add(remove);
			setCellWidth(remove, "99%");
			apply(obj);
			highlight(false);
		}
		
		private void apply(ObjectBean object) {
			this.object = object;
			name.setText(object.getName());
		}
		private void highlight(boolean highlight) {
			if (highlight) {
				setStyleName("location_list_item_selected");
			} else {
				setStyleName("location_list_item");
			}
		}
		
		private void select() {
			if (selected != this) {
				if (selected != null) {
					selected.unselect();
				}
				selected = this;
				highlight(true);
				model.selectObject(object, objectListener);
			}
		}
		private void unselect() {
			if (selected==this) {
				selected = null;
			}
			highlight(false);
			model.unselectObject(object, objectListener);
		}
	}

	public ObjectBean getSelected() {
		return selected==null ? null : selected.object;
	}
	

}
