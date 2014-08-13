package com.iambookmaster.client.common;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.locale.AppLocale;
/**
 * Panel for exchange 
 * @author ggadyatskiy
 */
public abstract class ExchangePanel extends PopupPanel{
	
	private TextArea textArea;
	private Button loadButton;
	private Label title;
	public ExchangePanel() {
		super();
		setStyleName("exchangePanel");
		VerticalPanel panel = new VerticalPanel();
		panel.setSpacing(5);
		panel.setSize("100%", "100%");
		setWidget(panel);
		title = new Label();
		panel.add(title);
		panel.setCellWidth(title,"100%");
		panel.setCellHeight(title,"1%");
		
		textArea = new TextArea();
		textArea.setHeight("100%");
		panel.add(textArea);
		panel.setCellWidth(textArea,"100%");
		panel.setCellHeight(textArea,"99%");
		ClickHandler listener = new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.getSource()==loadButton) {
					MaskPanel.show();
					DeferredCommand.addCommand(new Command(){
						public void execute() {
							if (processLoad(textArea.getText())) {
								hide();
							} else {
								MaskPanel.hide();
							}
						}
					});
				} else {
					//cancel
					hide();
					onClose();
				}
			}
		};
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(5);
		horizontalPanel.setSize("100%", "100%");
		loadButton = new Button(AppLocale.getAppConstants().buttonLoad(),listener);
		horizontalPanel.add(loadButton);
		horizontalPanel.setCellWidth(loadButton,"1%");
		Button button = new Button(AppLocale.getAppConstants().buttonClose(),listener);
		horizontalPanel.add(button);
		horizontalPanel.setCellWidth(button,"1%");
		HTML html = new HTML("&nbsp;");
		horizontalPanel.add(html);
		horizontalPanel.setCellWidth(html,"98%");
		panel.add(horizontalPanel);
		panel.setCellWidth(horizontalPanel,"100%");
		panel.setCellHeight(horizontalPanel,"1%");
	}
	
	protected void onClose() {
	}
	public abstract boolean processLoad(String text);
	
	private void centerAndShow() {
		int cw = Window.getClientWidth(); 
		int w = cw -200;
		if (w<400) {
			w = 400;
		}
		int ch = Window.getClientHeight(); 
		int h = ch-100;
		if (h<300) {
			h = 300;
		}
		setSize(String.valueOf(w)+"px", String.valueOf(h)+"px");
		setPopupPosition((cw/2)-(w/2),(ch/2)-(h/2));
		textArea.setWidth(String.valueOf(w-10)+"px");
		show();
	}
	public void showSave(String text,String title) {
		this.title.setText(title);
		loadButton.setEnabled(false);
		textArea.setText(text);
		centerAndShow();
	}
	
	public void showLoad(String title) {
		this.title.setText(title);
		loadButton.setEnabled(true);
		textArea.setText("");
		centerAndShow();
	}
	public void showExport(String data,String title) {
		this.title.setText(title);
		loadButton.setEnabled(false);
		textArea.setText(data);
		centerAndShow();
	}
	
}
