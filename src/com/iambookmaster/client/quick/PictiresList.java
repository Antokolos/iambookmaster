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
import com.iambookmaster.client.beans.Picture;
import com.iambookmaster.client.beans.Sound;
import com.iambookmaster.client.common.ScrollContainer;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.ContentListener;
import com.iambookmaster.client.model.Model;

public class PictiresList extends ScrollContainer {

	private VerticalPanel mainPanel;
	private Model model;
	private ContentListener objectListener;
	private PictureWidget selected;
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
	
	public PictiresList(Model model) {
		mainPanel = new VerticalPanel();
		mainPanel.setSize("100%", "100%");
		mainPanel.setSpacing(3);
		addStyleName("objects_list");
		setScrollWidget(mainPanel);
		this.model = model;
		objectListener = new ContentListener() {

			public void refreshAll() {
				reloadTree();
			}

			public void addNew(Picture picture) {
				PictureWidget widget = new PictureWidget(picture);
				int pos = getWidgetCount();
				mainPanel.insert(widget,pos-1);
				items.put(widget.object, widget);
				mainPanel.setCellHeight(widget,"1%");
			}

			public void addNew(Sound sound) {
			}

			public void remove(Picture picture) {
				PictureWidget widget = items.get(picture);
				if (widget != null) {
					mainPanel.remove(widget);
				}
			}

			public void remove(Sound sound) {
			}

			public void update(Picture picture) {
				PictureWidget widget = items.get(picture);
				if (widget != null) {
					widget.apply(picture);
				}
			}

			public void update(Sound sound) {
			}

			public void select(Sound sound) {
			}

			public void select(Picture picture) {
				PictureWidget widget = items.get(picture);
				if (widget != null) {
					if (selected != widget) {
						if (selected != null) {
							selected.highlight(false);
						}
					}
					widget.highlight(true);
				}
			}

			public void unselect(Sound sound) {
			}

			public void unselect(Picture picture) {
				PictureWidget widget = items.get(picture);
				if (widget != null) {
					widget.highlight(false);
					selected = null;
				}
			}

			public void showInfo(Picture picture) {
			}

			public void showInfo(Sound sound) {
			}

			
		};
		model.addContentListener(objectListener);
		reloadTree();
	}
	
	private void removeObject(Picture bean, PictureWidget widget) {
		if (Window.confirm(AppLocale.getAppConstants().quickConfirmRemoveImage())) {
			model.removePicture(bean);
		}
	}
	
	private HashMap<Picture, PictureWidget> items;
	
	private void reloadTree() {
		items = new HashMap<Picture, PictureWidget>();
		ArrayList<Picture> beans = model.getPictures();
		mainPanel.clear();
		int l = beans.size();
		for (int i = 0; i < l; i++) {
			PictureWidget widget = new PictureWidget(beans.get(i));
			mainPanel.add(widget);
			items.put(widget.object, widget);
			mainPanel.setCellHeight(widget,"1%");
		}
		HTML html = new HTML("&nbsp;");
		html.setStyleName("location_list_filler");
		mainPanel.add(html);
		mainPanel.setCellHeight(html,"99%");
		
	}

	public class PictureWidget extends HorizontalPanel {
		private Label name;
		private Image remove;
		private Picture object;
		private Image findInfo;
		public PictureWidget(Picture obj) {
			setSize("100%", "100%");
			setSpacing(3);
			name = new Label();
			name.setStyleName(Styles.CLICKABLE);
			name.setWordWrap(false);
			ClickHandler clickHandler = new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (event.getSource()==remove) {
						removeObject(object,PictureWidget.this);
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
			remove.setTitle(AppLocale.getAppConstants().quickRemoveImage());
			add(remove);
			setCellWidth(remove, "99%");
			apply(obj);
			highlight(false);
		}
		
		private void apply(Picture object) {
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
				model.selectPicture(object, objectListener);
			}
		}
		private void unselect() {
			if (selected==this) {
				selected = null;
			}
			highlight(false);
			model.unselectPicture(object, objectListener);
		}
	}

	public Picture getSelected() {
		return selected==null ? null : selected.object;
	}

}
