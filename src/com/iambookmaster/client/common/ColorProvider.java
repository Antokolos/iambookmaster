package com.iambookmaster.client.common;

public class ColorProvider {

	static final String[] colors = new String[]{"black","blue","green","red","yellow","white","gray"};

	public static String getColorName(int color) {
		if (color>=0 && color<colors.length) { 
			return colors[color];
		} else {
			return colors[0];
		}
	}

}
