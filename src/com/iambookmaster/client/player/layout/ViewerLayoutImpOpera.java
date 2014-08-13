package com.iambookmaster.client.player.layout;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * Opera implementation
 */
class ViewerLayoutImpOpera extends ViewerLayoutImpl {

	@Override
	public void applySize(Widget parent, Widget child) {
		int w = parent.getOffsetWidth();
		int h = parent.getOffsetHeight();
		if (w>0 && h>0) {
			child.setSize(String.valueOf(w)+"px", String.valueOf(h)+"px");
		}
		
	}

	@Override
	public void applySize(Element parent, Widget child) {
		int w = parent.getOffsetWidth();
		int h = parent.getOffsetHeight();
		if (w>0 && h>0) {
			child.setSize(String.valueOf(w)+"px", String.valueOf(h)+"px");
		}
	}
	
}
