package com.iambookmaster.client.common;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.ListBox;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ObjectListener;

public class SimpleObjectsListBox extends ListBox implements ObjectListener{

	private Model model;
	private ObjectBean selectedObject;
	private static final String EMPTY = "";
	
	public SimpleObjectsListBox(Model model) {
		this.model = model;
		model.addObjectsListener(this);
		refreshData();
	}
	private void refreshData() {
		ArrayList<ObjectBean> objects = model.getObjects();
		clear();
		addItem(EMPTY, EMPTY);
		int idx = 0;
		for (int i = 0; i < objects.size(); i++) {
			ObjectBean object = objects.get(i);
			addItem(object.getName(), object.getId());
			if (selectedObject == object) {
				idx = i+1;
			}
		}
		setSelectedIndex(idx);
	}
	
	public void addNewObject(ObjectBean object) {
		addItem(object.getName(), object.getId());
	}

	public void refreshAll() {
		refreshData();
	}

	public void select(ObjectBean object) {
	}

	public void unselect(ObjectBean object) {
	}

	public void update(ObjectBean object) {
		refreshData();
	}
	
	public ObjectBean getSelectedObject() {
		int idx = getSelectedIndex();
		if (idx!=0) {
			ArrayList<ObjectBean> objects = model.getObjects();
			String id = getValue(idx);
			for (int i = 0; i < objects.size(); i++) {
				ObjectBean object = objects.get(i);
				if (object.getId().equals(id)) {
					return object;
				}
			}
		}
		return null;
	}
	public void setSelectedObject(ObjectBean selectedObject) {
		if (selectedObject==null) {
			setSelectedIndex(0);
			return;
		}
		if (selectedObject.getId().equals(getValue(getSelectedIndex()))) {
			//the same
			return;
		}
		for (int i = 0; i < getItemCount(); i++) {
			if (selectedObject.getId().equals(getValue(i))) {
				setSelectedIndex(i);
				break;
			}
		}
	}

	protected void onDetach() {
		super.onDetach();
		model.removeObjectsListener(this);
	}

	public void remove(ObjectBean object) {
		for (int i = 0; i < getItemCount(); i++) {
			if (object.getId().equals(getValue(i))) {
				removeItem(i);
				break;
			}
		}
	}
	public void showInfo(ObjectBean object) {
	}

}
