package com.iambookmaster.client.common;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CompactHorizontalPanel extends HorizontalPanel {
	public CompactHorizontalPanel() {
		setSize("100%", "100%");
	}

	public void addFullText(String text) {
		Label label = new Label(text);
		label.setSize("100%", "100%");
		addFullWidget(label);
	}

	public void addText(String text) {
		Label label = new Label(text);
		label.setSize("100%", "100%");
		addCompactWidget(label);
	}

	public void addCompactWidget(Widget widget) {
		add(widget);
		setCellHeight(widget,"100%");
		setCellWidth(widget,"1%");
	}
	public void addFullWidget(Widget widget) {
		add(widget);
		setCellHeight(widget,"100%");
		setCellWidth(widget,"99%");
	}

	public void addText(String text, boolean wordWrap) {
		Label label = new Label(text,wordWrap);
		label.setSize("100%", "100%");
		addCompactWidget(label);
	}
}

