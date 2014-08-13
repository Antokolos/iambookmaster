package com.iambookmaster.client.common;

import com.google.gwt.user.client.Random;

public class Dice {

	public static int drop(int size) {
		int res = Random.nextInt(size)+1;
		return res;
	}

}
