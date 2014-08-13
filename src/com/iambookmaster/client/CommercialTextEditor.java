package com.iambookmaster.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.common.EditorTab;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.PlotListener;

public class CommercialTextEditor extends VerticalPanel implements EditorTab{

	private AppConstants appConstants = AppLocale.getAppConstants();
	
	private TextArea commercialText;
	private TextBox firstPageText;
	private Model model;
	private PlotListener plotListener;
	public CommercialTextEditor(Model mod) {
		this.model = mod;
		setSize("100%", "100%");
		Label label = new Label(appConstants.commercialTextMainTitle());
		add(label);
		setCellHeight(label,"1%");
		setCellWidth(label,"100%");
		
		ChangeHandler handler = new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				if (event.getSource()==commercialText) {
					model.updateCommercialText(commercialText.getText().trim(), plotListener);
				} else if (event.getSource()==firstPageText) {
					model.updateFirstPageDemoInfoText(firstPageText.getText().trim(), plotListener);
				}
			}
		};
		
		firstPageText = new TextBox();
		firstPageText.setWidth("100%");
		firstPageText.setText(model.getDemoInfoText());
		firstPageText.addChangeHandler(handler);
		add(firstPageText);
		setCellHeight(firstPageText,"1%");
		setCellWidth(firstPageText,"100%");
		
		label = new Label(appConstants.commercialTextDefaultTitle());
		add(label);
		setCellHeight(label,"1%");
		setCellWidth(label,"100%");
		
		commercialText = new TextArea();
		commercialText.setSize("100%", "100%");
		commercialText.setText(model.getCommercialText());
		commercialText.addChangeHandler(handler);
		add(commercialText);
		setCellHeight(commercialText,"99%");
		setCellWidth(commercialText,"100%");
		
		plotListener = new PlotListener() {
			public void refreshAll() {
				commercialText.setText(model.getCommercialText());
				firstPageText.setText(model.getDemoInfoText());
			}
			public void update(String pl) {
			}
			public void updateBookRules(String rules) {
			}
			public void updatePlayerRules(String rules) {
			}
			public void updateCommercialText(String text) {
				commercialText.setText(text);
			}
			public void updateDemoInfoText(String text) {
				firstPageText.setText(text);
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
