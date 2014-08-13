package com.iambookmaster.client.common;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.HTML;

public class SpanHTML extends HTML {

	public SpanHTML(String html) {
		super(html);
		removeBlock();
	}

	public SpanHTML() {
		super();
		removeBlock();
	}

	private void removeBlock() {
//		getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		getElement().getStyle().setDisplay(Display.INLINE);
	}

}
