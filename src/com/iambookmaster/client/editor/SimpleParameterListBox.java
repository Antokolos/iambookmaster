package com.iambookmaster.client.editor;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.ListBox;
import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ParameterListener;

public class SimpleParameterListBox extends ListBox implements ParameterListener{

	private Model model;
	private Parameter selected;
	private boolean npcOnly;
	
	public SimpleParameterListBox(Model model,boolean npcOnly) {
		this.model = model;
		this.npcOnly = npcOnly;
		model.addParamaterListener(this);
		refreshData();
	}
	private void refreshData() {
		ArrayList<AbstractParameter> objects = model.getParameters();
		clear();
		for (AbstractParameter abstractParameter : objects) {
			addParameterToList(abstractParameter);
		}
		
	}
	
	private void addParameterToList(AbstractParameter abstractParameter) {
		if (abstractParameter instanceof Parameter) {
			Parameter parameter = (Parameter) abstractParameter;
			if (npcOnly && parameter.isHeroOnly()) {
				return;
			}
			addItem(parameter.getName(), parameter.getId());
			if (selected == parameter) {
				setSelectedIndex(getItemCount()-1);
			}
		}
	}
	
	public void refreshAll() {
		refreshData();
	}

	public Parameter getSelectedParameter() {
		int idx = getSelectedIndex();
		ArrayList<AbstractParameter> objects = model.getParameters();
		String id = getValue(idx);
		for (int i = 0; i < objects.size(); i++) {
			AbstractParameter object = objects.get(i);
			if (object.getId().equals(id)) {
				return (Parameter)object;
			}
		}
		return null;
	}
	public void setSelectedParameter(AbstractParameter selectedParameter) {
		if (selectedParameter==null) {
			setSelectedIndex(0);
			return;
		}
		if (selectedParameter.getId().equals(getValue(getSelectedIndex()))) {
			//the same
			return;
		}
		for (int i = 0; i < getItemCount(); i++) {
			if (selectedParameter.getId().equals(getValue(i))) {
				setSelectedIndex(i);
				break;
			}
		}
	}

	protected void onDetach() {
		super.onDetach();
		model.removeParamaterListener(this);
	}


	public void addNewParameter(AbstractParameter parameter) {
		addParameterToList(parameter);
	}
	
	public void remove(AbstractParameter parameter) {
		for (int i = 0; i < getItemCount(); i++) {
			if (parameter.getId().equals(getValue(i))) {
				//remove it
				removeItem(i);
				if (selected==parameter) {
					selected = null;
				}
				break;
			}
		}
	}
	
	public void select(AbstractParameter parameter) {
	}
	
	public void update(AbstractParameter parameter) {
		for (int i = 0; i < getItemCount(); i++) {
			if (parameter.getId().equals(getValue(i))) {
				//update id
				setItemText(i, parameter.getName());
				break;
			}
		}
	}
	public void showInfo(AbstractParameter parameter) {
	}
	
}
