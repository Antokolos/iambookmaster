package com.iambookmaster.client.remote;

import java.util.HashMap;

public class RemoteRequest {
	private String function;
	private String url;
	private boolean post;
	private boolean waitForAnswer=true;
	private HashMap<String, String> parameters;

	public boolean isWaitForAnswer() {
		return waitForAnswer;
	}

	public void setWaitForAnswer(boolean waitForAnswer) {
		this.waitForAnswer = waitForAnswer;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public RemoteRequest(String function) {
		this.function = function;
	}

	public RemoteRequest(String function, boolean post) {
		this.function = function;
		this.post = post;
	}

	public String getFunction() {
		return function;
	}

	HashMap<String, String> getParameters() {
		return parameters;
	}

	public boolean isPost() {
		return post;
	}

	public void addParameter(String field, String value) {
		if (parameters==null) {
			parameters = new HashMap<String, String>();
		}
		parameters.put(field, value);
		
	}

}
