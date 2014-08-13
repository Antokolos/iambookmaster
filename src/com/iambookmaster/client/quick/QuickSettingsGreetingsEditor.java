package com.iambookmaster.client.quick;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.Images;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.Greeting;
import com.iambookmaster.client.common.CompactHorizontalPanel;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.SettingsListener;
import com.iambookmaster.client.viewer.GreetingWidgetFactory;

public class QuickSettingsGreetingsEditor extends VerticalPanel implements QuickViewWidget {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	
	private Model model;
	private SettingsListener settingsListener;
	private Image addNewGreeting;
	private Image preview;
	private VerticalPanel list;
	private PreviewPopup previewPopup;
	public QuickSettingsGreetingsEditor(Model mod) {
		this.model = mod;
		setSize("100%", "100%");
		Label label = new Label(appConstants.quickGreetingsTitle());
		add(label);
		setCellHeight(label,"1%");
		setCellWidth(label,"100%");
		
		ClickListener clickListener  = new ClickListener() {
			public void onClick(Widget sender) {
				if (sender==addNewGreeting) {
					Greeting greeting = new Greeting();
					model.getSettings().getGreetings().add(greeting);
					new GreetingWidget(greeting);
					model.updateSettings(settingsListener);
				} else if (sender==preview) {
					if (previewPopup==null) {
						previewPopup = new PreviewPopup();
					}
					previewPopup.preview(model.getSettings().getGreetings());
					
				}
			}
		};
		CompactHorizontalPanel horizontalPanel = new CompactHorizontalPanel();
		addNewGreeting = new Image(Images.ADD_CONNECTION);
		addNewGreeting.setStyleName(Styles.CLICKABLE);
		addNewGreeting.setTitle(appConstants.quickGreetingsAddTitle());
		addNewGreeting.addClickListener(clickListener);
		horizontalPanel.addCompactWidget(addNewGreeting);
		preview = new Image(Images.PREVIEW);
		preview.setStyleName(Styles.CLICKABLE);
		preview.setTitle(appConstants.quickGreetingsPreviewAll());
		preview.addClickListener(clickListener);
		horizontalPanel.addCompactWidget(preview);
		horizontalPanel.addFullText("");
		add(horizontalPanel);
		setCellHeight(horizontalPanel,"1%");
		setCellWidth(horizontalPanel,"100%");
		//list of greetings
		list = new VerticalPanel();
		list.setSize("100%", "100%");
		add(list);
		setCellHeight(list,"1%");
		setCellWidth(list,"100%");
		//filler
		HTML html = new HTML("&nbsp;");
		html.setStyleName(Styles.FILLER);
		add(html);
		setCellHeight(html,"99%");
		setCellWidth(html,"100%");
		
		settingsListener = new SettingsListener(){
			public void settingsWereUpated() {
				update();
			}
		};
		model.addSettingsListener(settingsListener);
		update();
	}
	
	public void update() {
		list.clear();
		ArrayList<Greeting> greetings = model.getSettings().getGreetings();
		for (int i = 0; i < greetings.size(); i++) {
			new GreetingWidget(greetings.get(i));
		}
		
	}

	public void close() {
		model.removeSettingsListener(settingsListener);
	}

	private void removeGreeting(Greeting greeting, GreetingWidget widget) {
		if (Window.confirm(appConstants.quickGreetingsRemoveConfirm())) {
			model.getSettings().getGreetings().remove(greeting);
			list.remove(widget);
			model.updateSettings(settingsListener);
		}
		
	}
	
	private void previewGreeting(Greeting greeting) {
		if (previewPopup==null) {
			previewPopup = new PreviewPopup();
		}
		previewPopup.preview(greeting);
	}
	
	
	public class PreviewPopup extends PopupPanel implements ClickListener{

		public PreviewPopup() {
			super(true,true);
			setStyleName(Styles.POPUP);
		}
		public void preview(Greeting greeting) {
			clear();
			VerticalPanel panel = new VerticalPanel();
			panel.setSize("100%", "100%");
			Widget widget = GreetingWidgetFactory.create(greeting);
			panel.add(widget);
			panel.setCellHeight(widget,"99%");
			panel.setCellWidth(widget,"100%");
			Button button = new Button("Close");
			button.addClickListener(this);
			panel.add(button);
			panel.setCellHeight(button,"1%");
			panel.setCellWidth(button,"100%");
			panel.setCellHorizontalAlignment(button,HasHorizontalAlignment.ALIGN_CENTER);
			setWidget(panel);
			center();
		}

		public void preview(ArrayList<Greeting> greetings) {
			clear();
			VerticalPanel panel = new VerticalPanel();
			panel.setSize("100%", "100%");
			for (int i = 0; i < greetings.size(); i++) {
				Greeting greeting = greetings.get(i);
				Widget widget = GreetingWidgetFactory.create(greeting);
				panel.add(widget);
				panel.setCellHeight(widget,"10%");
				panel.setCellWidth(widget,"100%");
			}
			Button button = new Button(appConstants.buttonClose());
			button.addClickListener(this);
			panel.add(button);
			panel.setCellHeight(button,"1%");
			panel.setCellWidth(button,"100%");
			panel.setCellHorizontalAlignment(button,HasHorizontalAlignment.ALIGN_CENTER);
			setWidget(panel);
			center();
		}

		public void onClick(Widget sender) {
			hide();
		}
		
	}
	
	public class GreetingWidget extends Grid implements ClickListener,ChangeListener{
		private TextBox name;
		private TextBox url;
		private TextBox text;
		private TextBox urlIcon;
		private Image remove;
		private Image preview;
		private Greeting greeting;
		public GreetingWidget(Greeting greeting) {
			super(4,2);
			setStyleName(Styles.GREETING);
			getColumnFormatter().setWidth(0, "1%");
			getColumnFormatter().setWidth(1, "99%");
			setSize("100%", "100%");
			setSpacing(2);
			//name
			setWidget(0,0,new Label(appConstants.quickGreetingsName()));
			name = new TextBox();
			name.addChangeListener(this);
//			name.setWidth("100%");
			setWidget(0,1,name);
			
			//url
			setWidget(1,0,new Label(appConstants.quickGreetingsURL(),false));
			url = new TextBox();
			url.addChangeListener(this);
//			url.setWidth("100%");
			setWidget(1,1,url);
			
			//icon url
			setWidget(2,0,new Label(appConstants.quickGreetingsIcon()));
			urlIcon = new TextBox();
			urlIcon.addChangeListener(this);
//			urlIcon.setWidth("100%");
			setWidget(2,1,urlIcon);
			
			//text
			HorizontalPanel horizontalPanel = new HorizontalPanel();
			preview = new Image(Images.PREVIEW);
			preview.addClickListener(this);
			preview.setTitle(appConstants.quickGreetingsPreview());
			horizontalPanel.add(preview);
			remove = new Image(Images.REMOVE);
			remove.addClickListener(this);
			remove.setTitle(appConstants.titleRemove());
			horizontalPanel.add(remove);
			setWidget(3,0,horizontalPanel);
			text = new TextBox();
			text.addChangeListener(this);
//			text.setWidth("100%");
			text.setTitle(appConstants.quickGreetingsText());
			setWidget(3,1,text);
			
			list.add(this);
			list.setCellHeight(this,"1%");
			list.setCellWidth(this,"100%");
			//fill data
			apply(greeting);
		}
		
		private void apply(Greeting greeting) {
			this.greeting = greeting;
			name.setText(greeting.getName());
			url.setText(greeting.getUrl());
			urlIcon.setText(greeting.getImageUrl());
			text.setText(greeting.getText());
		}
		
		public void onClick(Widget sender) {
			if (sender==remove) {
				removeGreeting(greeting,this);
			} else if (sender==preview) {
				previewGreeting(greeting);
			}
		}

		public void onChange(Widget sender) {
			if (sender==name) {
				greeting.setName(name.getText().trim());
			} else if (sender==url) {
				greeting.setUrl(url.getText().trim());
			} else if (sender==urlIcon) {
				greeting.setImageUrl(urlIcon.getText().trim());
			} else if (sender==text) {
				greeting.setText(text.getText().trim());
			}
			model.updateSettings(settingsListener);
		}

	}

}
