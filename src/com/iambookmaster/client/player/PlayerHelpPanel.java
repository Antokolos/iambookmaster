package com.iambookmaster.client.player;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.locale.AppLocale;
/**
 * About panel
 * @author ggadyatskiy
 */
public abstract class PlayerHelpPanel extends VerticalPanel {
	
	private Frame frame;
	public PlayerHelpPanel() {
		setStyleName("exchangePanel");
		setSpacing(5);
		setSize("100%", "100%");
		frame =  new Frame();
		frame.setSize("100%", "100%");
		add(frame);
		setCellWidth(frame,"100%");
		setCellHeight(frame,"99%");
		
		ClickHandler listener = new ClickHandler() {
			public void onClick(ClickEvent event) {
				onClose();
			}
		};
		HTML html = new HTML(AppLocale.getAppConstants().copyright());
		add(html);
		setCellWidth(html,"100%");
		setCellHorizontalAlignment(html, HasHorizontalAlignment.ALIGN_CENTER);
		setCellHeight(html,"1%");
		
		Button closeButton = new Button(AppLocale.getAppConstants().buttonClose(),listener);
		add(closeButton);
		setCellHorizontalAlignment(closeButton,HasHorizontalAlignment.ALIGN_CENTER);
		setCellWidth(closeButton,"100%");
		setCellHeight(closeButton,"1%");
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				frame.setUrl(AppLocale.getAppConstants().pagePlayerHelp());
			}
		});
		
	}
	protected abstract void onClose();
	
}
