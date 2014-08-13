package com.iambookmaster.client.editor;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.ListBox;
import com.iambookmaster.client.beans.Picture;
import com.iambookmaster.client.beans.Sound;
import com.iambookmaster.client.model.ContentListener;
import com.iambookmaster.client.model.Model;

public class PicturesListBox extends ListBox implements ContentListener{

	private Model model;
	private Picture selected;
	private int role;
	
	public PicturesListBox(Model model,int role) {
		this.model = model;
		this.role = role;
		model.addContentListener(this);
		refreshData();
	}
	private void refreshData() {
		ArrayList<Picture> objects = model.getPictures();
		clear();
		addItem("","");
		for (Picture picture : objects) {
			addParameterToList(picture);
		}
		
	}
	
	private void addParameterToList(Picture picture) {
		if (role > 0 && (role & picture.getRole()) == 0) {
			return;
		}
		addItem(picture.getName(), picture.getId());
		if (selected == picture) {
			setSelectedIndex(getItemCount()-1);
		}
	}
	
	public void refreshAll() {
		refreshData();
	}

	public Picture getSelectedPicture() {
		int idx = getSelectedIndex();
		if (idx==0) {
			return null;
		}
		ArrayList<Picture> objects = model.getPictures();
		String id = getValue(idx);
		for (Picture picture : objects) {
			if (picture.getId().equals(id)) {
				return picture;
			}
		}
		return null;
	}
	public void setSelectedPicture(Picture selectedPicture) {
		if (selectedPicture==null) {
			setSelectedIndex(0);
			return;
		}
		if (selectedPicture.getId().equals(getValue(getSelectedIndex()))) {
			//the same
			return;
		}
		for (int i = 0; i < getItemCount(); i++) {
			if (selectedPicture.getId().equals(getValue(i))) {
				setSelectedIndex(i);
				break;
			}
		}
	}

	protected void onDetach() {
		super.onDetach();
		model.removeContentListener(this);
	}
	public void update(Picture picture) {
		for (int i = 0; i < getItemCount(); i++) {
			if (picture.getId().equals(getValue(i))) {
				//update id
				setItemText(i, picture.getName());
				break;
			}
		}
	}
	
	public void addNew(Picture picture) {
		addParameterToList(picture);
	}
	
	public void remove(Picture picture) {
		for (int i = 0; i < getItemCount(); i++) {
			if (picture.getId().equals(getValue(i))) {
				//remove it
				removeItem(i);
				if (selected==picture) {
					selected = null;
				}
				break;
			}
		}
	}
	
	public void update(Sound sound) {
	}
	public void addNew(Sound sound) {
	}
	public void remove(Sound sound) {
	}
	public void select(Sound sound) {
	}
	public void unselect(Sound sound) {
	}
	public void select(Picture picture) {
	}
	public void unselect(Picture picture) {
	}
	public void showInfo(Picture picture) {
	}
	public void showInfo(Sound sound) {
	}

	
}
