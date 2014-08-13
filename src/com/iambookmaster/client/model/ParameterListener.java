package com.iambookmaster.client.model;

import com.iambookmaster.client.beans.AbstractParameter;

public interface ParameterListener {

	public void refreshAll();

	public void addNewParameter(AbstractParameter parameter);

	public void select(AbstractParameter parameter);

	public void update(AbstractParameter parameter);

	public void remove(AbstractParameter parameter);

	public void showInfo(AbstractParameter parameter);

}
