package com.iambookmaster.client.iphone;

import com.google.gwt.user.client.Timer;
import com.iambookmaster.client.iphone.common.IPhoneScrollPanel;

public class IPhoneScrollAnimation extends Timer {

	private static final int MAX = 20;
	private int counter=MAX;
	private IPhoneScrollPanel scrollPanel;
	public IPhoneScrollAnimation(IPhoneScrollPanel scrollPanel) {
		scheduleRepeating(500);
		this.scrollPanel = scrollPanel;
	}
	
	@Override
	public void run() {
		if (counter>0) {
			counter--;
		} else if (scrollPanel.isBottomArrow()){
			scrollPanel.scrollDown();
		} else {
			cancel();
		}
	}

}
