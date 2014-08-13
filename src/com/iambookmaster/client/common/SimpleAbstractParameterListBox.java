package com.iambookmaster.client.common;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.ListBox;
import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ParameterListener;

public class SimpleAbstractParameterListBox<T> extends ListBox implements ParameterListener{

	private static final String EMPTY="";
	private Model model;
	private T selected;
	private boolean addEmpty;
	@SuppressWarnings("unchecked")
	private AbstractParameterFilter filter;
	@SuppressWarnings("unchecked")
	private Class clazz;
	
	@SuppressWarnings("unchecked")
	public SimpleAbstractParameterListBox(Class clazz,Model model) {
		this(clazz,model,false,null);
	}
	@SuppressWarnings("unchecked")
	public SimpleAbstractParameterListBox(Class clazz,Model model, boolean addEmpty) {
		this(clazz,model,addEmpty,null);
	}
	@SuppressWarnings("unchecked")
	public SimpleAbstractParameterListBox(Class clazz,Model model, boolean addEmpty,AbstractParameterFilter filter) {
		this.model = model;
		this.clazz = clazz;
		this.addEmpty =addEmpty;
		this.filter = filter;
		model.addParamaterListener(this);
		refreshData();
	}
	private void refreshData() {
		ArrayList<AbstractParameter> objects = model.getParameters();
		clear();
		if (addEmpty) {
			addItem(EMPTY);
			setSelectedIndex(0);
		}
		for (AbstractParameter abstractParameter : objects) {
			if (filter==null || filter.match(abstractParameter)) {
				addParameterToList(abstractParameter);
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void addParameterToList(AbstractParameter abstractParameter) {
		if (clazz.equals(abstractParameter.getClass())) {
			if (filter==null || filter.match(abstractParameter)) {
				for (int i = 0; i < getItemCount(); i++) {
					if (abstractParameter.getId().equals(getValue(i))) {
						//already in the list
						return;
					}
				}
				addItem(abstractParameter.getName(), abstractParameter.getId());
				if (selected==null) {
					if (addEmpty==false) {
						selected = (T)abstractParameter;
						setSelectedIndex(getItemCount()-1);
					}
				} else if (selected == abstractParameter) {
					setSelectedIndex(getItemCount()-1);
				}
			}
		}
	}
	
	public void refreshAll() {
		refreshData();
	}

	@SuppressWarnings("unchecked")
	public T getSelectedParameter() {
		int idx = getSelectedIndex();
		if (idx==0 && getItemCount()==0) {
			return null;
		}
		ArrayList<AbstractParameter> objects = model.getParameters();
		String id = getValue(idx);
		if (addEmpty && getSelectedIndex()==0) {
			return null;
		}
		for (int i = 0; i < objects.size(); i++) {
			AbstractParameter object = objects.get(i);
			if (object.getId().equals(id)) {
				return (T)object;
			}
		}
		return null;
	}
	public void setSelectedParameter(T param) {
		if (param==null) {
			setSelectedIndex(0);
			return;
		}
		AbstractParameter selectedParameter = (AbstractParameter)param;
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
