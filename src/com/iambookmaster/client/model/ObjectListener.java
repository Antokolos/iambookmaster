package com.iambookmaster.client.model;

import com.iambookmaster.client.beans.ObjectBean;

public interface ObjectListener {

	public void refreshAll();

	public void update(ObjectBean object);

	public void select(ObjectBean object);

	public void addNewObject(ObjectBean object);

	public void unselect(ObjectBean object);

	public void remove(ObjectBean object);

	public void showInfo(ObjectBean object);

}
