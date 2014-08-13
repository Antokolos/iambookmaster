package com.iambookmaster.client.common;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.Label;

public class SpanLabel extends Label {

	public SpanLabel(String text) {
		super(text);
		removeBlock();
	}

	public SpanLabel() {
		removeBlock();
	}

	public SpanLabel(String text, boolean wordWrap) {
		super(text,wordWrap);
		removeBlock();
	}

	private void removeBlock() {
//		getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		getElement().getStyle().setDisplay(Display.INLINE);
	}

}
