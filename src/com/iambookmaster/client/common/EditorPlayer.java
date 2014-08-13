package com.iambookmaster.client.common;


public interface EditorPlayer extends EditorTab{

	void start();

	boolean isSupportRotation();

	void rotate();

	void editCurrentParagraph();

	void restart();

	void save();

	void load();

	boolean isSupportSaveAndLoad();

	void goCurrentParagraph();

	boolean isSupportScale();

	void scale(int scale);

	boolean isSupportModel();

	void loadModule();
	

}
