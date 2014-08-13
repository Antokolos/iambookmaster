package com.iambookmaster.client.viewer;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.beans.Greeting;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.player.PlayerStyles;
/**
 * About panel
 * @author ggadyatskiy
 */
public abstract class ViewerAboutPanel extends VerticalPanel{
	
	private static final AppConstants appConstants = AppLocale.getAppConstants();
	private static final AppMessages appMessages = AppLocale.getAppMessages();
	
	private Label description;
	private Model model;
	private Button closeButton;
	public ViewerAboutPanel(Model mod) {
		model = mod;
		setStyleName("playerAboutPanel");
		setSpacing(5);
		setSize("100%", "100%");
		if (model != null) {
			Label title = new Label(model.getSettings().getBookTitle());
			title.setStyleName(PlayerStyles.BOOK_TITLE);
			add(title);
			setCellWidth(title,"100%");
			setCellHeight(title,"1%");
			title = new Label(model.getSettings().getBookAuthors());
			title.setStyleName(PlayerStyles.BOOK_AUTHOR);
			add(title);
			setCellWidth(title,"100%");
			setCellHeight(title,"1%");
			int l = model.getSettings().getGreetings().size(); 
			if (l>0) {
				VerticalPanel grt = new VerticalPanel();
				grt.setSpacing(5);
				ArrayList<Greeting> list = model.getSettings().getGreetings();
				Label label = new Label(appConstants.playerGreetingsFor());
				grt.add(label);
				grt.setCellHeight(label,"1%");
				grt.setCellWidth(label,"100%");
				for (int i = 0; i < l; i++) {
					Greeting greeting = list.get(i);
					Widget widget = GreetingWidgetFactory.create(greeting);
					grt.add(widget);
					grt.setCellHeight(widget,"1%");
					grt.setCellWidth(widget,"100%");
				}
				add(grt);
				setCellWidth(grt,"100%");
				setCellHeight(grt,"1%");
			}
			description = new Label();
			description.setSize("100%","100%");
			description.setText(model.getSettings().getBookDescription());
			description.setStyleName(PlayerStyles.BOOK_DESCRIPTION);
			add(description);
			setCellWidth(description,"100%");
			setCellHeight(description,"1%");
			
			if (model.getPlayerRules().length()>0) {
				Label rules = new Label(model.getPlayerRules());
				rules.setSize("100%","100%");
				rules.setStyleName(PlayerStyles.BOOK_RULES);
				add(rules);
				setCellWidth(rules,"100%");
				setCellHeight(rules,"1%");
			}
			
			HTML html = new HTML("&nbsp;");
			html.setStyleName(PlayerStyles.FILLER);
			add(html);
			setCellHeight(html,"100%");
			
			title = new Label(appMessages.playerBookWasScretedBy(model.getVersionHi(),model.getVersionLo()));
			add(title);
			setCellWidth(title,"100%");
			setCellHorizontalAlignment(title, HasHorizontalAlignment.ALIGN_CENTER);
			setCellHeight(title,"1%");
		}
		
		Label label = new Label(appMessages.playerVersion(Model.HI_VERSION,Model.LO_VERSION));
		add(label);
		setCellWidth(label,"100%");
		setCellHorizontalAlignment(label, HasHorizontalAlignment.ALIGN_CENTER);
		setCellHeight(label,"1%");
		HTML html = new HTML(appConstants.copyright());
		add(html);
		setCellWidth(html,"100%");
		setCellHorizontalAlignment(html, HasHorizontalAlignment.ALIGN_CENTER);
		setCellHeight(html,"1%");
		
		ClickHandler listener = new ClickHandler() {
			public void onClick(ClickEvent event) {
				onClose();
			}
		};
		closeButton = new Button(appConstants.buttonClose(),listener);
		add(closeButton);
		setCellWidth(closeButton,"100%");
		setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_CENTER);
		setCellHeight(closeButton,"1%");
	}
	
	protected abstract void onClose();

	public void newVersionAvailable(int version) {
		HTML title = new HTML(appMessages.playerNewGameVersionAvailable(model.getGameId()));
		title.setStyleName(PlayerStyles.NEW_VERSION);
		insert(title,2);
		setCellWidth(title,"100%");
		setCellHeight(title,"1%");		
	}
	
	public void setBack(boolean back) {
		if (back) {
			closeButton.setText(appConstants.buttonBackToGame());
		} else {
			closeButton.setText(appConstants.buttonStartGame());
		}
	}

}
