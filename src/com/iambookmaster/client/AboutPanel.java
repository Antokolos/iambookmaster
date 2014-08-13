package com.iambookmaster.client;
 
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
/**
 * About panel
 * @author ggadyatskiy
 */
public class AboutPanel extends PopupPanel{
	private AppConstants appConstants = AppLocale.getAppConstants();
	private AppMessages appMessages = AppLocale.getAppMessages();
	
	private Frame frame;
	private Label title;
	public AboutPanel() {
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
		HTML html = new HTML(appConstants.copyright());
		panel.add(html);
		panel.setCellWidth(html,"100%");
		panel.setCellHeight(html,"1%");
		frame =  new Frame();
		frame.setSize("100%", "100%");
		panel.add(frame);
		panel.setCellWidth(frame,"100%");
		panel.setCellHeight(frame,"99%");
		
		ClickHandler listener = new ClickHandler() {
			public void onClick(ClickEvent event) {
				AboutPanel.this.hide();
			}
		};
		Button closeButton = new Button(appConstants.closeButton(),listener);
		panel.add(closeButton);
		panel.setCellWidth(closeButton,"100%");
		panel.setCellHeight(closeButton,"1%");
		panel.setCellHorizontalAlignment(closeButton,HasHorizontalAlignment.ALIGN_CENTER);
	}
	
	public void centerAndShow(String locale) {
		title.setText(appMessages.aboutTheProgram(Model.HI_VERSION,Model.LO_VERSION));
		centerAndShow();
		frame.setUrl(appConstants.pageAbout());
	}

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
		show();
	}

	public void centerAndShow(int maj, int min) {
		title.setText(appMessages.getAboutTitle(maj,min));
		centerAndShow();
//		frame.setUrl("http://www.iambookmaster.com"+"/"+"remote/"+appConstants.locale() +"/"+maj+"_"+min+"/about.html");
		frame.setUrl("http://localhost:8080/iambookmaster"+"/"+"remote/"+appConstants.locale() +"/"+maj+"_"+min+"/about.html");
	}
	
}
