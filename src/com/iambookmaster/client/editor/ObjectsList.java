package com.iambookmaster.client.editor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.Images;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.common.SimpleObjectsListBox;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ObjectListener;

public class ObjectsList extends FlexTable implements ObjectListener{

	private Model model;
	private Image addObject;
	private ChangeHandler changeListener;
	private SimpleObjectsListBox list;
	
	public ObjectsList(Model model) {
		this.model = model;
		setStyleName(Styles.BORDER);
		model.addObjectsListener(this);
		list = new SimpleObjectsListBox(model);
		
		addObject = new Image(Images.ADD_CONNECTION);
		addObject.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ObjectBean sel = list.getSelectedObject();
				if (sel != null) {
					for (int i = 1; i < getRowCount(); i++) {
						Widget widget = getWidget(i, 0);
						if (widget instanceof ItemWidget) {
							ItemWidget itemWidget = (ItemWidget) widget;
							if (sel==itemWidget.object) {
								//already added
								return;
							}
						}
					}
					addObjectToList(sel);
					list.setSelectedIndex(0);
				}
			}
		});
		list.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event) {
				ObjectBean sel = list.getSelectedObject();
				if (sel != null) {
					for (int i = 1; i < getRowCount(); i++) {
						Widget widget = getWidget(i, 0);
						if (widget instanceof ItemWidget) {
							ItemWidget itemWidget = (ItemWidget) widget;
							if (sel==itemWidget.object) {
								//already added
								list.setSelectedIndex(0);
								break;
							}
						}
					}
				}
				//in other cases - fire event
				changeListener.onChange(null);
			}
		});
		addObject.setStyleName(Styles.CLICKABLE);
		addObject.setTitle(AppLocale.getAppConstants().titleAddObjectToList());
		addControls();
	}
	
	private void addControls() {
		insertRow(0);
		addCell(0);
		setWidget(0, 0, list);
		addCell(0);
		setWidget(0, 1, addObject);
		getColumnFormatter().setWidth(0, "99%");
		getColumnFormatter().setWidth(1, "1%");
	}

	public void addNewObject(ObjectBean object) {
	}

	public void refreshAll() {
	}

	public void select(ObjectBean object) {
	}

	public void unselect(ObjectBean object) {
	}

	public void update(ObjectBean object) {
		for (int i = 1; i < getRowCount(); i++) {
			Widget widget = getWidget(i, 0);
			if (widget instanceof ItemWidget) {
				ItemWidget itemWidget = (ItemWidget) widget;
				if (itemWidget.object==object) {
					itemWidget.refresh();
				}
			}
		}
	}
	
	public HashSet<ObjectBean> getSelectedObjects() {
		HashSet<ObjectBean> res = new HashSet<ObjectBean>();
		ObjectBean sel = list.getSelectedObject();
		if (sel != null) {
			res.add(sel);
		}
		for (int i = 1; i < getRowCount(); i++) {
			Widget widget = getWidget(i, 0);
			if (widget instanceof ItemWidget) {
				ItemWidget itemWidget = (ItemWidget) widget;
				res.add(itemWidget.object);
			}
		}
		return res;
	}
	public void setSelectedObjects(Set<ObjectBean> selectedObject) {
		int s = selectedObject.size();
		if (s==0) {
			while (getRowCount()>1) {
				removeRow(1);
			}
			list.setSelectedIndex(0);
			return;
		}
		HashSet<ObjectBean> curr = getSelectedObjects();
		if (curr.containsAll(selectedObject)) {
			return;
		} else {
			while (getRowCount()>1) {
				removeRow(1);
			}
			//add other objects
			Iterator<ObjectBean> iterator = selectedObject.iterator();
			list.setSelectedObject(iterator.next());
			while (iterator.hasNext()) {
				addObjectToList(iterator.next());
			}
		}
	}

	private void addObjectToList(ObjectBean bean) {
		ItemWidget widget = new ItemWidget(bean);
		int row = insertRow(1);
		addCell(row);
		setWidget(row, 0, widget);
		addCell(row);
		setWidget(row, 1, widget.removeButton);
	}

	protected void onDetach() {
		super.onDetach();
		model.removeObjectsListener(this);
	}

	public void remove(ObjectBean object) {
	}
	
	public void addChangeHandler(ChangeHandler listener) {
		this.changeListener = listener;
	}
	
	private void removeObject(ItemWidget itemWidget) {
		for (int i = 1; i < getRowCount(); i++) {
			Widget widget = getWidget(i, 0);
			if (widget==itemWidget) {
				removeRow(i);
				changeListener.onChange(null);
				break;
			}
		}
	}
	
	
	public class ItemWidget extends Label implements ClickHandler {
		private Image removeButton;
		private ObjectBean object;
		
		public ItemWidget(ObjectBean object) {
			this.object = object;
			setWordWrap(false);
			removeButton = new Image(Images.REMOVE);
			removeButton.setTitle(AppLocale.getAppConstants().titleRemoveObjectFromList());
			removeButton.addClickHandler(this);
			refresh();
		}

		public void refresh() {
			if (model.getSettings().isShowParagraphNumbers()) {
				StringBuilder builder = new StringBuilder();
				if (object.getKey() > 0) {
					builder.append('+');
				}
				builder.append(object.getKey());
				builder.append(' ');
				builder.append(object.getName());
				setText(builder.toString());
			} else {
				setText(object.getName());
			}
		}

		public void onClick(ClickEvent event) {
			if (Window.confirm(AppLocale.getAppMessages().confirmRemoveObjectFromList(object.getName()))) {
				removeObject(this);
			}
		}
		
	}


	public void showInfo(ObjectBean object) {
	}


}
