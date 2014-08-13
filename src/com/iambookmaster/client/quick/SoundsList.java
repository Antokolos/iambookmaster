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

public class SoundsList extends ScrollContainer {

	private VerticalPanel mainPanel;
	private Model model;
	private ContentListener objectListener;
	private SoundWidget selected;
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
	
	public SoundsList(Model model) {
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
			}

			public void addNew(Sound sound) {
				SoundWidget widget = new SoundWidget(sound);
				int pos = getWidgetCount();
				mainPanel.insert(widget,pos-1);
				items.put(widget.object, widget);
				mainPanel.setCellHeight(widget,"1%");
			}

			public void remove(Picture picture) {
			}

			public void remove(Sound sound) {
				SoundWidget widget = items.get(sound);
				if (widget != null) {
					mainPanel.remove(widget);
				}
			}

			public void update(Picture picture) {
			}

			public void update(Sound sound) {
				SoundWidget widget = items.get(sound);
				if (widget != null) {
					widget.apply(sound);
				}
			}

			public void select(Sound sound) {
				SoundWidget widget = items.get(sound);
				if (widget != null) {
					if (selected != widget) {
						if (selected != null) {
							selected.highlight(false);
						}
					}
					widget.highlight(true);
				}
			}

			public void select(Picture picture) {
			}

			public void unselect(Sound sound) {
				SoundWidget widget = items.get(sound);
				if (widget != null) {
					widget.highlight(false);
					selected = null;
				}
			}

			public void unselect(Picture picture) {
			}

			public void showInfo(Picture picture) {
			}

			public void showInfo(Sound sound) {
			}

			
		};
		model.addContentListener(objectListener);
		reloadTree();
	}
	
	private void removeObject(Sound bean, SoundWidget widget) {
		if (Window.confirm(AppLocale.getAppConstants().removeSoundConfirm())) {
			model.removeSound(bean);
		}
	}
	
	private HashMap<Sound, SoundWidget> items;
	
	private void reloadTree() {
		items = new HashMap<Sound, SoundWidget>();
		ArrayList<Sound> beans = model.getSounds();
		mainPanel.clear();
		int l = beans.size();
		for (int i = 0; i < l; i++) {
			SoundWidget widget = new SoundWidget(beans.get(i));
			mainPanel.add(widget);
			items.put(widget.object, widget);
			mainPanel.setCellHeight(widget,"1%");
		}
		HTML html = new HTML("&nbsp;");
		html.setStyleName("location_list_filler");
		mainPanel.add(html);
		mainPanel.setCellHeight(html,"99%");
		
	}

	public class SoundWidget extends HorizontalPanel{
		private Label name;
		private Image remove;
		private Sound object;
		private Image findInfo;
		public SoundWidget(Sound obj) {
			setSize("100%", "100%");
			setSpacing(3);
			name = new Label();
			name.addStyleName(Styles.CLICKABLE);
			name.setWordWrap(false);
			ClickHandler clickHandler = new ClickHandler(){
				public void onClick(ClickEvent event) {
					if (event.getSource()==remove) {
						removeObject(object,SoundWidget.this);
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
			remove.setTitle(AppLocale.getAppConstants().quickRemoveSoundTitle());
			add(remove);
			setCellWidth(remove, "99%");
			highlight(false);
			apply(obj);
		}
		
		private void apply(Sound object) {
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
				model.selectSound(object, objectListener);
			}
		}
		private void unselect() {
			if (selected==this) {
				selected = null;
			}
			highlight(false);
			model.unselectSound(object, objectListener);
		}
	}

	public Sound getSelected() {
		return selected==null ? null : selected.object;
	}

}
