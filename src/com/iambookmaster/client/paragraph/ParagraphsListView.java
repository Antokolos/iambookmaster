package com.iambookmaster.client.paragraph;

import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.Images;
import com.iambookmaster.client.StatusPanel;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.common.ScrollContainer;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ParagraphListener;
import com.iambookmaster.client.model.SettingsListener;

public class ParagraphsListView extends ScrollContainer {
	
	public static final String STYLE_DRAFT = "loc_status_draft";
	public static final String STYLE_FINAL = "loc_status_final";
	public static final String STYLE_PROPOSAL = "loc_status_prop";
	private static final String STYLE_SELECTED = "location_list_item_selected";
	
	private VerticalPanel mainPanel;
	private Model model;
	private ParagraphListener locationListener;
	private ParagraphWidget selected;
	private boolean activationNeed=true;
	private boolean showNumbers;
	private boolean reloadOnActivation;
	private boolean active;
	private HashMap<Paragraph, ParagraphWidget> items;
	
	
	public void deactivate() {
		active=false;
	}

	public void activate() {
		active=true;
		if (activationNeed) {
			activationNeed = false;
			resetHeight();
		}
		if (reloadOnActivation) {
			reloadOnActivation = false;
			reloadTree();
		}
	}
	public void activateLater() {
		activationNeed = true;
	}
	
	public ParagraphsListView(Model mod) {
		mainPanel = new VerticalPanel();
		mainPanel.setSize("100%", "100%");
		mainPanel.setSpacing(3);
		addStyleName("location_list");
		setScrollWidget(mainPanel);
		this.model = mod;
		locationListener = new ParagraphListener() {
			public void addNewParagraph(Paragraph location) {
				ParagraphWidget widget = new ParagraphWidget(location);
				int pos = getWidgetCount();
				mainPanel.insert(widget,pos-1);
				items.put(widget.paragraph, widget);
				mainPanel.setCellHeight(widget,"1%");
			}
			public void edit(Paragraph location) {
			}
			public void refreshAll() {
				if (active) {
					reloadTree();
				} else {
					reloadOnActivation=true;
				}
			}
			public void select(Paragraph location) {
				ParagraphWidget widget = items.get(location);
				if (widget != null) {
					widget.select();
				}
			}
			public void unselect(Paragraph location) {
				ParagraphWidget widget = items.get(location);
				if (widget != null) {
					widget.unselect();
				}
			}
			public void update(Paragraph location) {
				ParagraphWidget widget = items.get(location);
				if (widget != null) {
					widget.apply(location);
				}
			}
			public void remove(Paragraph location) {
				int l = mainPanel.getWidgetCount();
				for (int i = 0; i < l; i++) {
					Widget widget = mainPanel.getWidget(i);
					if (widget instanceof ParagraphWidget) {
						ParagraphWidget locationWidget = (ParagraphWidget) widget;
						if (locationWidget.paragraph==location) {
							mainPanel.remove(i);
							break;
						}
					}
				}
			}
		};
		model.addParagraphListener(locationListener);
		
		showNumbers = model.getSettings().isShowParagraphNumbers();
		model.addSettingsListener(new SettingsListener(){
			public void settingsWereUpated() {
				if (showNumbers != model.getSettings().isShowParagraphNumbers()) {
					showNumbers = model.getSettings().isShowParagraphNumbers();
					for (int i = 0; i < mainPanel.getWidgetCount(); i++) {
						Widget widget = mainPanel.getWidget(i);
						if (widget instanceof ParagraphWidget) {
							ParagraphWidget locationWidget = (ParagraphWidget) widget;
							locationWidget.apply(locationWidget.paragraph);
						}
					}
				}
			}
		});
		reloadTree();
	}
	
	private void removeLocation(Paragraph location, ParagraphWidget widget) {
		if (Window.confirm(AppLocale.getAppConstants().confirmRemove())) {
			model.removeParagraph(location);
		}
	}
	
	private void reloadTree() {
		if (items==null) {
			items = new HashMap<Paragraph, ParagraphWidget>();
		} else {
			items.clear();
		}
		mainPanel.clear();
		new Timer() {
			private Iterator<Paragraph> iterator = model.getParagraphs().iterator(); 
			@Override
			public void run() {
				int count=50;
				while (iterator.hasNext()) {
					ParagraphWidget widget = new ParagraphWidget(iterator.next());
					mainPanel.add(widget);
					items.put(widget.paragraph, widget);
					mainPanel.setCellHeight(widget,"1%");
					if (count--<0) {
						return;
					}
				}
				cancel();
				HTML html = new HTML("&nbsp;");
				html.setStyleName("location_list_filler");
				mainPanel.add(html);
				mainPanel.setCellHeight(html,"99%");
				StatusPanel.addMessage("reloadTree-end");
			}
			
		}.scheduleRepeating(100);
	}

	public Paragraph getSelected() {
		return selected==null ? null : selected.paragraph;
	}
	
	public class ParagraphWidget extends HorizontalPanel{
		private Image icon;
		private Label name;
		private Image commercial;
		private Image remove;
		private Paragraph paragraph;
		public ParagraphWidget(Paragraph location) {
			setSize("100%", "100%");
			setSpacing(3);
			ClickHandler clickHandler = new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (event.getSource()==remove) {
						removeLocation(paragraph,ParagraphWidget.this);
					} else {
						select();
					}
				}
			};
			icon = new Image();
			icon.addClickHandler(clickHandler);
			add(icon);
			setCellWidth(icon, "1%");
			name = new Label();
			name.setWordWrap(false);
			name.addClickHandler(clickHandler);
			add(name);
			setCellWidth(name, "1%");
			commercial = new Image(Images.COMMERCIAL);
			add(commercial);
			setCellWidth(commercial, "1%");
			remove = new Image(Images.REMOVE);
			remove.addClickHandler(clickHandler);
			remove.setTitle(AppLocale.getAppConstants().titleRemove());
			add(remove);
			setCellWidth(remove, "99%");
			apply(location);
		}
		private void apply(Paragraph location) {
			this.paragraph = location;
			icon.setUrl(ParagraphsMapEditor.getParagraphTypeURL(location));
			if (showNumbers) {
				name.setText(location.getNumber()+" "+location.getName());
			} else {
				name.setText(location.getName());
			}
			commercial.setVisible(location.isCommercial());
			highlight(false);
		}
		private void highlight(boolean highlight) {
			if (highlight) {
				setStyleName(STYLE_SELECTED);
			} else {
				switch (paragraph.getStatus()) {
				case Model.STATUS_DRAFT:
					setStyleName(STYLE_DRAFT);
					break;
				case Model.STATUS_FINAL:
					setStyleName(STYLE_FINAL);
					break;
				default:
					setStyleName(STYLE_PROPOSAL);
				} 
			}
		}
		
		private void select() {
			if (selected != this) {
				if (selected != null) {
					selected.unselect();
				}
				selected = this;
				highlight(true);
				model.selectParagraph(paragraph, locationListener);
			}
		}
		private void unselect() {
			if (selected==this) {
				selected = null;
			}
			highlight(false);
			model.unselectParagraph(paragraph, locationListener);
		}
	}

}
