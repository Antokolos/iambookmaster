package com.iambookmaster.server;

public class LogicException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private String message;
	public LogicException(String message) {
		this.message = message;
	}
	public LogicException(Throwable error) {
		//full stack
		StringBuffer buffer = new StringBuffer(error.getMessage());
		buffer.append('\n');
		StackTraceElement[] elements = error.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			buffer.append(elements[i].toString());
			buffer.append('\n');
		}
		message = buffer.toString();
	}
	
	public String toString() {
		return message;
	}
	public String getMessage() {
		return message;
	}
}
