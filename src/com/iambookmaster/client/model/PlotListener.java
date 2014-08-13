package com.iambookmaster.client.model;

public interface PlotListener {

	void refreshAll();

	void update(String plot);

	void updateBookRules(String rules);

	void updatePlayerRules(String rules);

	void updateCommercialText(String text);

	void updateDemoInfoText(String text);
}
