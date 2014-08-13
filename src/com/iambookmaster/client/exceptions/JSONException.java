package com.iambookmaster.client.exceptions;

public class JSONException extends Exception {

	private static final long serialVersionUID = 1L;

	private String message;
	public JSONException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
