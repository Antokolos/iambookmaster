package com.iambookmaster.client.viewer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.player.PlayerStyles;
/**
 * Info that player is old
 * @author ggadyatskiy
 */
public abstract class OldVersionPanel extends VerticalPanel {

	public OldVersionPanel(Model model) {
		super();
		setStyleName(PlayerStyles.ABOUT_PANEL);
		setSpacing(5);
		setSize("100%", "100%");
		HTML html = new HTML(AppLocale.getAppMessages().playerTooOld(Model.HI_VERSION,Model.LO_VERSION,model.getVersionHi(),model.getVersionLo()));
		html.setStyleName(PlayerStyles.OLD_VERSION_MESSAGE);
		add(html);
		setCellWidth(html,"100%");
		setCellHorizontalAlignment(html, HasHorizontalAlignment.ALIGN_CENTER);
		setCellHeight(html,"100%");
		
		html = new HTML(AppLocale.getAppConstants().copyright());
		add(html);
		setCellWidth(html,"100%");
		setCellHorizontalAlignment(html, HasHorizontalAlignment.ALIGN_CENTER);
		setCellHeight(html,"1%");
		
		ClickHandler listener = new ClickHandler() {
			public void onClick(ClickEvent event) {
				onClose();
			}
		};
		Button closeButton = new Button("Close",listener);
		add(closeButton);
		setCellWidth(closeButton,"100%");
		setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_CENTER);
		setCellHeight(closeButton,"1%");
	}

	protected abstract void onClose();
	
}
