package com.iambookmaster.client.remote;

public interface RemotePanelListener {

	void beforeRequest();

	void error(String responce);

	void success();

	void load(String id);

	void serverReplied(String answer);

}
