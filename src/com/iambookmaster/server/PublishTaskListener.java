package com.iambookmaster.server;

public interface PublishTaskListener {
	/**
	 * @return true if we out of time
	 */
	boolean checkTimeout();

}
