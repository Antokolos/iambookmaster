package com.iambookmaster.client.common;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class AbsolutePanelSpan extends AbsolutePanel {

	public AbsolutePanelSpan() {
		super(DOM.createSpan());
	    DOM.setStyleAttribute(getElement(), "position", "relative");
	    DOM.setStyleAttribute(getElement(), "overflow", "hidden");	
	}

//	@Override
//	public void onAttach() {
//		super.onAttach();
//	}
	
}
