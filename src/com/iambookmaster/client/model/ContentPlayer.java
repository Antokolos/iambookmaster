package com.iambookmaster.client.model;

public interface ContentPlayer {

	void openURL(String url);

	void playSound(String url, boolean loop);

	void stopSound();
	
	void playBackgroundSound(String url);

	void stopBackgroundSound();
}
