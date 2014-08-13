package com.iambookmaster.client.iphone;

public interface IPhoneMainMenuListener {

	void close();

	boolean isAudioAvailable();

	boolean isAudioEnabled();

	boolean isSaveEnabled();

	boolean isLoadEnabled();

	void forward();

	void back();

}
