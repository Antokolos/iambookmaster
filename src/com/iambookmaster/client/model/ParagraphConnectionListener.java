package com.iambookmaster.client.model;

import com.iambookmaster.client.beans.ParagraphConnection;

public interface ParagraphConnectionListener {

	public void refreshAll();

	public void select(ParagraphConnection connection);

	public void unselect(ParagraphConnection connection);

	public void update(ParagraphConnection connection);

	public void remove(ParagraphConnection connection);

	public void addNew(ParagraphConnection connection);

}
