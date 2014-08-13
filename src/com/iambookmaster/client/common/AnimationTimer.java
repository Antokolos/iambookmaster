package com.iambookmaster.client.common;

import java.util.ArrayList;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.player.PlayerStyles;

public class AnimationTimer extends Timer {
	
	private int counter;
	private ArrayList<Widget> widgets;
	
	public AnimationTimer() {
		scheduleRepeating(500);
	}
	
	@Override
	public void run() {
		if (counter>0) {
			counter--;
		} else if (widgets != null){
			ArrayList<Widget> list = widgets;
			widgets = null;
			for (Widget widget : list) {
				widget.removeStyleName(PlayerStyles.ANIMATED);
			}
		}
	}
	
	public void add(Widget widget) {
		counter = 3;
		if (widgets==null) {
			widgets = new ArrayList<Widget>();
		} else if (widgets.contains(widget)) {
			return;
		}
		widgets.add(widget);
		widget.addStyleName(PlayerStyles.ANIMATED);
	}

}
