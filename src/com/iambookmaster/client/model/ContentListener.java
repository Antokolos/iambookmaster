package com.iambookmaster.client.model;

import com.iambookmaster.client.beans.Picture;
import com.iambookmaster.client.beans.Sound;

public interface ContentListener {

	void refreshAll();

	void update(Picture picture);

	void addNew(Picture picture);

	void remove(Picture picture);

	void update(Sound sound);

	void addNew(Sound sound);

	void remove(Sound sound);

	void select(Sound sound);

	void unselect(Sound sound);

	void select(Picture picture);

	void unselect(Picture picture);

	void showInfo(Picture picture);

	void showInfo(Sound sound);

}
