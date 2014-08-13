package com.iambookmaster.client.paragraph;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.common.EditorTab;
import com.iambookmaster.client.common.ScrollContainer;
import com.iambookmaster.client.common.SpanLabel;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ParagraphListener;

public class ParagraphFindAndReplace extends VerticalPanel implements EditorTab {

	private final AppConstants appConstants = AppLocale.getAppConstants();
	
	private ScrollContainer scrollContainer;
	private VerticalPanel matches;
	private Model model;
	private TextBox findText;
	private TextBox replaceText;
	private Button find;
	private Button replace;
	private CheckBox caseCencetive;
	private ParagraphListener paragraphListener;
	private String lastSearchText;
//	private CheckBox wholeWord;
	
	public ParagraphFindAndReplace(Model mod) {
		model = mod;
		Grid grid = new Grid(2,3);
		grid.setCellPadding(3);
		grid.getColumnFormatter().setWidth(0,"1%");
		grid.getColumnFormatter().setWidth(1,"98%");
		grid.getColumnFormatter().setWidth(2,"1%");
		grid.setSize("100%", "100%");
		grid.setWidget(0, 0, new Label(appConstants.findReplaceFindText()));
		grid.setWidget(1, 0, new Label(appConstants.findReplaceReplaceText()));
		ClickHandler clickHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.getSource()==find) {
					start();
				} else if (event.getSource()==replace) {
					startReplace();
				}
			}
		};
		findText = new TextBox();
		findText.setWidth("100%");
		grid.setWidget(0, 1, findText);
		replaceText = new TextBox();
		replaceText.setWidth("100%");
		grid.setWidget(1, 1, replaceText);
		find = new Button(appConstants.findReplaceFindButton(),clickHandler);
		grid.setWidget(0, 2, find);
		replace = new Button(appConstants.findReplaceReplaceButton(),clickHandler);
		grid.setWidget(1, 2, replace);
		replace.setEnabled(false);
		add(grid);
		setCellHeight(grid, "1%");
		setCellWidth(grid, "100%");
		
		HorizontalPanel panel = new HorizontalPanel();
		panel.setSize("100%", "100%");
		panel.setSpacing(2);
		caseCencetive = new CheckBox(appConstants.findReplaceCaseSencetive());
		panel.add(caseCencetive);
		panel.setCellWidth(caseCencetive, "1%");
//		wholeWord = new CheckBox(appConstants.findReplaceWholeWord());
//		panel.add(wholeWord);
//		panel.setCellWidth(wholeWord, "99%");
		add(panel);
		setCellHeight(panel, "1%");
		setCellWidth(panel, "100%");
		
		matches = new VerticalPanel();
		matches.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		matches.setStyleName("editor_panel");
		matches.setSpacing(2);
		matches.setSize("100%", "100%");
		scrollContainer = new ScrollContainer();
		scrollContainer.addStyleName(Styles.BORDER);
		scrollContainer.setScrollWidget(matches);
		add(scrollContainer);
		setCellHeight(scrollContainer, "99%");
		setCellWidth(scrollContainer, "100%");
		scrollContainer.resetHeight();
	}
	
	protected void startReplace() {
		String to = replaceText.getText();
		for (int i = 0; i < matches.getWidgetCount(); i++) {
			Widget widget = matches.getWidget(i);
			if (widget instanceof FoundWidget) {
				FoundWidget foundWidget = (FoundWidget) widget;
				foundWidget.replace(lastSearchText,to);
			}
		}
		replace.setEnabled(false);
		replaceText.setEnabled(false);
	}

	protected void start() {
		ArrayList<Paragraph> list = model.getParagraphs();
		matches.clear();
		replace.setEnabled(false);
		replaceText.setEnabled(false);
		if (caseCencetive.getValue()) {
			lastSearchText = findText.getText().trim();
		} else {
			lastSearchText = findText.getText().trim().toLowerCase();
		}
		if (lastSearchText.length()==0) {
			Window.alert(appConstants.findReplaceEmptyString());
			return;
		}
		for (Paragraph paragraph : list) {
			String text;
			if (caseCencetive.getValue()) {
				text = paragraph.getDescription();
			} else {
				text = paragraph.getDescription().toLowerCase();
			}
			if (text.indexOf(lastSearchText)>=0) {
				//found
				Widget widget = new FoundWidget(paragraph,text,lastSearchText);
				matches.add(widget);
				matches.setCellHeight(widget, "1%");
				matches.setCellWidth(widget, "100%");
			}
		}
		Label widget;
		if (matches.getWidgetCount()>0) {
			widget = new HTML("&nbsp;");
			widget.setStyleName(Styles.FILLER);
			replace.setEnabled(true);
			replaceText.setEnabled(true);
		} else {
			widget = new Label(appConstants.findReplaceNotFound());
			widget.setSize("100%", "100%");
			widget.setStyleName(Styles.BOLD);
		}
		matches.add(widget);
		matches.setCellHeight(widget, "99%");
		matches.setCellWidth(widget, "100%");
		
		paragraphListener = new ParagraphListener(){

			public void addNewParagraph(Paragraph location) {
			}

			public void edit(Paragraph location) {
			}

			public void refreshAll() {
			}

			public void remove(Paragraph location) {
			}

			public void select(Paragraph location) {
			}

			public void unselect(Paragraph location) {
			}

			public void update(Paragraph location) {
				for (int i = 0; i < matches.getWidgetCount(); i++) {
					Widget widget = matches.getWidget(i);
					if (widget instanceof FoundWidget) {
						FoundWidget foundWidget = (FoundWidget) widget;
						if (foundWidget.paragraph==location) {
							String text;
							if (caseCencetive.getValue()) {
								text = location.getDescription();
							} else {
								text = location.getDescription().toLowerCase();
							}
							foundWidget.apply(location, text, lastSearchText);
							break;
						}
						
					}
				}
			}
			
		};
		model.addParagraphListener(paragraphListener);
	}
	

	public void activate() {
		scrollContainer.resetHeight();
	}

	public void deactivate() {
	}

	public void close() {
		model.removeParagraphListener(paragraphListener);
	}
	
	public class FoundWidget extends VerticalPanel implements ClickHandler{
		private CheckBox replaceAll;
		private FlowPanel panel;
		private Paragraph paragraph;
		public FoundWidget(Paragraph par,String text, String search) {
			paragraph = par;
			setStyleName(Styles.BORDER);
			setSize("100%", "100%");
			replaceAll = new CheckBox();
			replaceAll.setValue(true);
			if (model.getSettings().isShowParagraphNumbers() && paragraph.getNumber() !=0) {
				replaceAll.setText(String.valueOf(paragraph.getNumber())+". "+paragraph.getName());
			} else {
				replaceAll.setText(paragraph.getName());
			}
			replaceAll.setStyleName(Styles.PARGRAPH_MARKED);
			replaceAll.setWidth("100%");
			add(replaceAll);
			setCellHeight(replaceAll, "1%");
			setCellWidth(replaceAll, "100%");
			panel = new FlowPanel();
			panel.setSize("100%", "100%");
			add(panel);
			setCellHeight(panel, "1%");
			setCellWidth(panel, "100%");
			apply(paragraph,text,search);
		}
		
		public void replace(String search, String to) {
			if (replaceAll.getValue()) {
				panel.clear();
				String text;
				if (caseCencetive.getValue()) {
					text = paragraph.getDescription();
				} else {
					text = paragraph.getDescription().toLowerCase();
				}
				int start=0;
				int l = search.length();
				StringBuilder builder = new StringBuilder();
				while (true) {
					int pos = text.indexOf(search,start);
					if (pos>=0) {
						String from = paragraph.getDescription().substring(start,pos);
						SpanLabel label = new SpanLabel(from);
						label.addClickHandler(this);
						label.addStyleName(Styles.CLICKABLE);
						panel.add(label);
						builder.append(from);
						builder.append(to);
						
						label = new SpanLabel(to);
						label.setStyleName(Styles.FOUND_TEXT);
						label.addStyleName(Styles.CLICKABLE);
						label.addClickHandler(this);
						panel.add(label);
						start = pos+l;
					} else {
						//no more
						String from = paragraph.getDescription().substring(start);
						SpanLabel label = new SpanLabel(from);
						label.addClickHandler(this);
						label.addStyleName(Styles.CLICKABLE);
						panel.add(label);
						builder.append(from);
						break;
					}
				}
				paragraph.setDescription(builder.toString());
				model.updateParagraph(paragraph, paragraphListener);
			}
		}

		public void apply(Paragraph paragraph, String text, String search) {
			panel.clear();
			int start=0;
			int l = search.length();
			while (true) {
				int pos = text.indexOf(search,start);
				if (pos>=0) {
					SpanLabel label = new SpanLabel(paragraph.getDescription().substring(start,pos));
					label.addClickHandler(this);
					label.addStyleName(Styles.CLICKABLE);
					panel.add(label);
					label = new SpanLabel(paragraph.getDescription().substring(pos,pos+l));
					label.setStyleName(Styles.FOUND_TEXT);
					label.addStyleName(Styles.CLICKABLE);
					label.addClickHandler(this);
					panel.add(label);
					start = pos+l;
				} else {
					//no more
					SpanLabel label = new SpanLabel(paragraph.getDescription().substring(start));
					label.addClickHandler(this);
					label.addStyleName(Styles.CLICKABLE);
					panel.add(label);
					break;
				}
			}
		}

		public void onClick(ClickEvent event) {
			if (event.getSource()==replaceAll) {
				
			} else {
				model.editParagraph(paragraph, paragraphListener);
			}
		}
	}
	
}
