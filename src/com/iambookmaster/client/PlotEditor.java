package com.iambookmaster.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.common.EditorTab;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.PlotListener;

public class PlotEditor extends VerticalPanel implements EditorTab{

	private TextArea plot;
	private Model model;
	private PlotListener plotListener;
	public PlotEditor(Model mod) {
		this.model = mod;
		setSize("100%", "100%");
		plot = new TextArea();
		plot.setSize("100%", "100%");
		plot.setText(model.getPlot());
		plot.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				model.updatePlot(plot.getText().trim(), plotListener);
			}
		});
		add(plot);
		setCellHeight(plot,"100%");
		setCellWidth(plot,"100%");
		plotListener = new PlotListener() {
			public void refreshAll() {
				plot.setText(model.getPlot());
			}
			public void update(String pl) {
				plot.setText(pl);
			}
			public void updateBookRules(String rules) {
			}
			public void updatePlayerRules(String rules) {
			}
			public void updateCommercialText(String text) {
			}
			public void updateDemoInfoText(String text) {
			}
		};
		model.addPlotListener(plotListener);
	}
	
	public void activate() {
	}
	public void deactivate() {
	}
	public void close() {
		model.removePlotListener(plotListener);
	}
}
