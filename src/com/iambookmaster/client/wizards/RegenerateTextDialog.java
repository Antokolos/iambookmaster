package com.iambookmaster.client.wizards;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.common.MaskPanel;
import com.iambookmaster.client.common.StatusPicker;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
/**
 * @author ggadyatskiy
 */
public class RegenerateTextDialog extends PopupPanel{
	
	private static final AppConstants appConstants = AppLocale.getAppConstants();
	private static final AppMessages appMessages = AppLocale.getAppMessages();
	
	private StatusPicker status;
	private Button doButton;
	public RegenerateTextDialog() {
		super();
		setStyleName("exchangePanel");
		VerticalPanel panel = new VerticalPanel();
		panel.setSpacing(5);
		panel.setSize("100%", "100%");
		setWidget(panel);
		Label title = new Label(appConstants.menuRefreshParagraphsText());
		panel.add(title);
		panel.setCellWidth(title,"100%");
		panel.setCellHeight(title,"1%");
		title = new Label(appConstants.menuRefreshParagraphsTextWarning());
		title.getElement().getStyle().setColor("red");
		title.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		panel.add(title);
		panel.setCellWidth(title,"100%");
		panel.setCellHeight(title,"1%");
		
		title = new Label(appConstants.menuRefreshParagraphsStatus());
		panel.add(title);
		status = new StatusPicker();
		panel.add(status);
		ClickHandler listener = new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.getSource()==doButton) {
					MaskPanel.show();
					DeferredCommand.addCommand(new Command(){
						public void execute() {
							//TODO
						}
					});
				} else {
					//cancel
					hide();
				}
			}
		};
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(5);
		horizontalPanel.setSize("100%", "100%");
		doButton = new Button(appConstants.buttonRegenerate(),listener);
		horizontalPanel.add(doButton);
		horizontalPanel.setCellWidth(doButton,"1%");
		Button button = new Button(appConstants.buttonClose(),listener);
		horizontalPanel.add(button);
		horizontalPanel.setCellWidth(button,"99%");
		panel.add(horizontalPanel);
		panel.setCellWidth(horizontalPanel,"100%");
		panel.setCellHeight(horizontalPanel,"1%");
	}
	
	public void centerAndShow() {
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
		show();
	}
}
