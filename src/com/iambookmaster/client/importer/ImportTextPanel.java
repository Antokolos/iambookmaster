package com.iambookmaster.client.importer;

import java.util.ArrayList;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.Images;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.common.CompactHorizontalPanel;
import com.iambookmaster.client.common.NumberTextBox;
import com.iambookmaster.client.common.ResizeListener;
import com.iambookmaster.client.common.ScrollContainer;
import com.iambookmaster.client.common.SpanLabel;
import com.iambookmaster.client.common.TrueVerticalSplitPanel;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
/**
 * Panel for import other books
 * @author ggadyatskiy
 */
public abstract class ImportTextPanel extends PopupPanel{
	
	private static final String STYLE_TYPE_SELECTED = "import_par_type_sel";
	private static final String STYLE_TYPE_UNSELECTED = "import_par_type_unsel";
	
	private static final int TAB_ORIGINAL = 0;
	private static final int TAB_OPTIONS = 1;
	private static final int TAB_PARAGRAPHS = 2;
	private static final int TAB_CONNECTIONS = 3;
	
	private static final AppConstants appConstants = AppLocale.getAppConstants();
	private static final AppMessages appMessages = AppLocale.getAppMessages();
	
	private Button nextButton;
	private Button prevButton;
	private Button finishButton;
	private Model model;
	private StatusPanel statusPanel;
	private TabPanel tabPanel;
	private OriginalPanel originalPanel;
	private TextBookImporter bookImporter;
	private OptionsPanel optionsPanel;
	private ParagraphsPanel paragraphsPanel;
	private int visibleTab;
	private ConnectionsPanel connectionsPanel;
	private TrueVerticalSplitPanel splitPanel;
	
	public ImportTextPanel() {
		super();
		setStyleName("exchangePanel");
		VerticalPanel panel = new VerticalPanel();
		panel.setSpacing(5);
		panel.setSize("100%", "100%");
		setWidget(panel);
		Label title = new Label(appConstants.importPanelTitle());
		panel.add(title);
		panel.setCellWidth(title,"100%");
		panel.setCellHeight(title,"1%");
		
		bookImporter = new TextBookImporter(new TextBookImporterListener(){
			public void error(String text) {
				statusPanel.addError(text);
			}
			public void info(String text) {
				statusPanel.addMessage(text, false);
			}
			public void warning(String text) {
				statusPanel.addWarning(text);
			}
			public void endStage(String text) {
				statusPanel.addMessage(text, true);
			}
			public void startStage(String text) {
				statusPanel.addMessage(text, false);
			}
			
			public void startParseParagraph(Paragraph paragraph) {
				connectionsPanel.nextParagraph(paragraph);
			}
			
			public void endParseParagraph(Paragraph paragraph) {
				connectionsPanel.endParagraph(paragraph);
			}
			public void parseParagraphAddError(String text) {
				connectionsPanel.addParagraphError(text);
			}
			public void parseParagraphAddLink(String link, Paragraph paragraph) {
				connectionsPanel.addParagraphLink(link,paragraph);
			}
			public void parseParagraphAddText(String text) {
				connectionsPanel.addParagraphText(text);
			}
		},appConstants,appMessages);
		
		
		tabPanel = new TabPanel();
		tabPanel.setSize("100%", "100%");
		tabPanel.getDeckPanel().setSize("100%", "100%");
		tabPanel.addTabListener(new TabListener() {
			public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
				return true;
			}

			public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
				visibleTab = tabIndex;
				prevButton.setEnabled(tabIndex>0);
				nextButton.setEnabled(tabIndex<TAB_CONNECTIONS);
				finishButton.setEnabled(false);
				switch (visibleTab) {
				case TAB_PARAGRAPHS:
					if (originalPanel.isChanged() || optionsPanel.isChanged()) {
						if (paragraphsPanel.isChanged()) {
							if (Window.confirm(appConstants.importParseOriginalAgain())==false) {
								return;
							}
						}
						statusPanel.clear();
						optionsPanel.applyParameters();
						originalPanel.clearChanged();
						optionsPanel.clearChanged();
						model = new Model(AppLocale.getAppConstants(),AppLocale.getAppMessages());
						model.getSettings().setShowParagraphNumbers(true);
						bookImporter.importBook(model, originalPanel.getText());
						paragraphsPanel.update(model);
					}
					paragraphsPanel.onActivate();
					break;
				case TAB_CONNECTIONS:
					connectionsPanel.onActivate();
					if (paragraphsPanel.isChanged()) {
						if (connectionsPanel.isChanged()) {
							if (Window.confirm(appConstants.importParseParagraphAgain())==false) {
								return;
							}
						}
						paragraphsPanel.clearChanged();
						connectionsPanel.update(model);
						finishButton.setEnabled(connectionsPanel.isHasParagraphs());
					}
					break;
				case TAB_OPTIONS:
					optionsPanel.onActivate();
					break;
				}
			}
			
		});
		originalPanel = new OriginalPanel();
		tabPanel.add(originalPanel, appConstants.importTabOriginal());
		optionsPanel = new OptionsPanel();
		tabPanel.add(optionsPanel, appConstants.importTabOptions());
		paragraphsPanel = new ParagraphsPanel();
		tabPanel.add(paragraphsPanel, appConstants.importTabParagraphs());
		connectionsPanel = new ConnectionsPanel();
		tabPanel.add(connectionsPanel, appConstants.importTabConnections());
		
		splitPanel = new TrueVerticalSplitPanel(true,true);
		splitPanel.addResizeListener(new ResizeListener(){
			public void onResize(Widget panel) {
				ImportTextPanel.this.onResize();
			}
		});
		panel.add(splitPanel);
		panel.setCellWidth(splitPanel,"100%");
		panel.setCellHeight(splitPanel,"99%");
		splitPanel.setTopWidget(tabPanel);
		statusPanel = new StatusPanel();
		splitPanel.setBottomWidget(statusPanel);
		splitPanel.setSplitPosition("70%");
		ClickListener listener = new ClickListener() {
			public void onClick(Widget sender) {
				if (sender==nextButton) {
					if (visibleTab<TAB_CONNECTIONS) {
						visibleTab++;
					}
					tabPanel.selectTab(visibleTab);
				} else if (sender==prevButton) {
					if (visibleTab>TAB_ORIGINAL) {
						visibleTab--;
					}
					tabPanel.selectTab(visibleTab);
				} else if (sender==finishButton) {
					//trase numbers
					bookImporter.traseMap();
					if (applyImportedModel(model)) {
						hide();
					}
				} else {
					//cancel
					hide();
				}
			}
		};
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(5);
		horizontalPanel.setSize("100%", "100%");
		prevButton = new Button(appConstants.buttonPrev(),listener);
		prevButton.setEnabled(false);
		horizontalPanel.add(prevButton);
		horizontalPanel.setCellWidth(prevButton,"1%");
		nextButton = new Button(appConstants.buttonNext(),listener);
		horizontalPanel.add(nextButton);
		horizontalPanel.setCellWidth(nextButton,"1%");
		finishButton = new Button(appConstants.buttonFinish(),listener);
		finishButton.setEnabled(false);
		horizontalPanel.add(finishButton);
		horizontalPanel.setCellWidth(finishButton,"1%");
		HTML html = new HTML("&nbsp;");
		horizontalPanel.add(html);
		horizontalPanel.setCellWidth(html,"98%");
		Button button = new Button(appConstants.buttonClose(),listener);
		horizontalPanel.add(button);
		horizontalPanel.setCellWidth(button,"1%");
		panel.add(horizontalPanel);
		panel.setCellWidth(horizontalPanel,"100%");
		panel.setCellHeight(horizontalPanel,"1%");
		tabPanel.selectTab(TAB_ORIGINAL);
	}
	
	public abstract boolean applyImportedModel(Model model);
	
	public void onResize() {
		switch (visibleTab) {
		case TAB_PARAGRAPHS:
			paragraphsPanel.resetHeight();
			connectionsPanel.resizeOnActivate();
			break;
		case TAB_CONNECTIONS:
			connectionsPanel.resetHeight();
			paragraphsPanel.resizeOnActivate();
			break;
		case TAB_OPTIONS:
			connectionsPanel.resizeOnActivate();
			paragraphsPanel.resizeOnActivate();
			break;
		}
	}
	public void centerAndShow() {
		int cw = Window.getClientWidth(); 
		int w = cw -100;
		if (w<400) {
			w = 400;
		}
		int ch = Window.getClientHeight(); 
		int h = ch-50;
		if (h<300) {
			h = 300;
		}
		setSize(String.valueOf(w)+"px", String.valueOf(h)+"px");
		setPopupPosition((cw/2)-(w/2),(ch/2)-(h/2));
		show();
		splitPanel.activate();
	}
	
	public class OriginalPanel extends VerticalPanel implements ChangeListener{
		private TextArea textArea;
		private boolean changed;
		public OriginalPanel() {
			setSize("100%", "100%");
			setSpacing(5);
			Label title = new Label(appConstants.importTextTitle());
			add(title);
			setCellWidth(title,"100%");
			setCellHeight(title,"1%");
			
			textArea = new TextArea();
			textArea.addChangeListener(this);
			textArea.setSize("100%","100%");
			add(textArea);
			setCellWidth(textArea,"100%");
			setCellHeight(textArea,"99%");
			
		}

		public String getText() {
			return textArea.getText().trim();
		}

		public void onChange(Widget sender) {
			changed = true;
		}

		public boolean isChanged() {
			return changed;
		}
		public void clearChanged() {
			changed = false;
		}
		
	}
	
	public class OptionsPanel extends VerticalPanel implements ClickListener,ChangeListener{
		private CheckBox paragraphNumberInSeparateLine;
		private TextBox paragraphSeparator;
		private TextBox paragraphReferenseStart;
		private TextBox paragraphReferenseEnd;
		private boolean changed;
		private CheckBox detectSecreteNumbers;
		private TextBox secretNumberStart;
		private TextBox secretNumberEnd;
		public OptionsPanel() {
			paragraphNumberInSeparateLine = new CheckBox(appConstants.importNumberInSeparateLine());
			paragraphNumberInSeparateLine.setChecked(bookImporter.isParagraphNumberInSeparateLine());
			paragraphNumberInSeparateLine.addClickListener(this);
			add(paragraphNumberInSeparateLine);
			setCellWidth(paragraphNumberInSeparateLine,"100%");
			setCellHeight(paragraphNumberInSeparateLine,"1%");
			CompactHorizontalPanel widgetPanel = new CompactHorizontalPanel();
			widgetPanel.addText("Paragraph number separator",false);
			paragraphSeparator = new TextBox();
			paragraphSeparator.addChangeListener(this);
			paragraphSeparator.setText(String.valueOf(bookImporter.getParagraphNumberSeparator()));
			paragraphSeparator.setMaxLength(1);
			paragraphSeparator.setVisibleLength(2);
			widgetPanel.addFullWidget(paragraphSeparator);
			add(widgetPanel);
			setCellWidth(widgetPanel,"100%");
			setCellHeight(widgetPanel,"1%");
			
			widgetPanel = new CompactHorizontalPanel();
			widgetPanel.addText("In text",false);
			paragraphReferenseStart = new TextBox();
			paragraphReferenseStart.addChangeListener(this);
			paragraphReferenseStart.setMaxLength(1);
			paragraphReferenseStart.setText(String.valueOf(bookImporter.getParagraphReferenceStart()));
			paragraphReferenseStart.setVisibleLength(2);
			widgetPanel.addCompactWidget(paragraphReferenseStart);
			widgetPanel.addText(appConstants.importParagraphReference(),false);
			paragraphReferenseEnd = new TextBox();
			paragraphReferenseEnd.addChangeListener(this);
			paragraphReferenseEnd.setText(String.valueOf(bookImporter.getParagraphReferenceEnd()));
			paragraphReferenseEnd.setMaxLength(1);
			paragraphReferenseEnd.setVisibleLength(2);
			widgetPanel.addFullWidget(paragraphReferenseEnd);
			add(widgetPanel);
			setCellWidth(widgetPanel,"100%");
			setCellHeight(widgetPanel,"1%");
			
//			widgetPanel = new CompactHorizontalPanel();
//			detectSecreteNumbers = new CheckBox();
//			detectSecreteNumbers.setValue(bookImporter.isSecretKeyDetection());
//			widgetPanel.addCompactWidget(detectSecreteNumbers);
//			widgetPanel.addText("Secret key detection. ",false);
//			secretNumberStart = new TextBox();
//			secretNumberStart.setMaxLength(1);
//			secretNumberStart.setVisibleLength(2);
//			secretNumberStart.setText(String.valueOf(bookImporter.getSecretKeyStart()));
//			widgetPanel.addCompactWidget(secretNumberStart);
//			widgetPanel.addText("key",false);
//			secretNumberEnd = new TextBox();
//			secretNumberEnd.setMaxLength(1);
//			secretNumberEnd.setVisibleLength(2);
//			secretNumberEnd.setText(String.valueOf(bookImporter.getSecretKeyEnd()));
//			widgetPanel.addFullWidget(secretNumberEnd);
//			add(widgetPanel);
//			setCellWidth(widgetPanel,"100%");
//			setCellHeight(widgetPanel,"1%");
			//filler
			HTML html = new HTML("&nbsp;");
			add(html);
			setCellWidth(html,"100%");
			setCellHeight(html,"99%");
		}
		
		public boolean isChanged() {
			return changed;
		}

		public void clearChanged() {
			changed = false;
			
		}

		public void onActivate() {
		}

		public void applyParameters() {
			bookImporter.setParagraphNumberInSeparateLine(paragraphNumberInSeparateLine.isChecked());
			if (paragraphSeparator.getText().length()==0) {
				bookImporter.setParagraphNumberSeparator(' ');
			} else {
				bookImporter.setParagraphNumberSeparator(paragraphSeparator.getText().charAt(0));
			}
			if (paragraphReferenseStart.getText().length()==0) {
				bookImporter.setParagraphReferenceStart(' ');
			} else {
				bookImporter.setParagraphReferenceStart(paragraphReferenseStart.getText().charAt(0));
			}
			if (paragraphReferenseEnd.getText().length()==0) {
				bookImporter.setParagraphReferenceEnd(' ');
			} else {
				bookImporter.setParagraphReferenceEnd(paragraphReferenseEnd.getText().charAt(0));
			}
//			bookImporter.setSecretKeyDetection(detectSecreteNumbers.isChecked());
//			if (secretNumberStart.getText().length()==0) {
//				bookImporter.setSecretKeyStart(' ');
//			} else {
//				bookImporter.setSecretKeyStart(secretNumberStart.getText().charAt(0));
//			}
//			if (secretNumberEnd.getText().length()==0) {
//				bookImporter.setSecretKeyEnd(' ');
//			} else {
//				bookImporter.setSecretKeyEnd(secretNumberEnd.getText().charAt(0));
//			}
		}

		public void onClick(Widget sender) {
			changed = true;
		}

		public void onChange(Widget sender) {
			changed = true;
		}
	}
	
	public class StatusPanel extends VerticalPanel {
		private boolean changed;
		
		public StatusPanel(){
			setSize("100%", "100%");
		}
		
		public void addFiller() {
			HTML html = new HTML("&nbsp");
			add(html);
			setCellWidth(html,"100%");
			setCellHeight(html,"99%");			
		}

		public void clearChanged() {
			changed = false;
		}

		public boolean isChanged() {
			return changed;
		}

		public void clear() {
			changed = false;
			super.clear();
		}

		public void addMessage(String text,boolean end) {
			changed = true;
			new Message(text,end);
		}

		public void addError(String text) {
			changed = true;
			new Message(text,Images.ERROR);
		}

		public void addWarning(String text) {
			changed = true;
			new Message(text,Images.WARNING);
		}

		public class Message extends HorizontalPanel{
			private static final String STYLE = "validation_line";
			private static final String STYLE_END = "validation_line_end";
			private Image img;
			private Label label;
			
			public Message(String text, String image) {
				setWidth("100%");
				img = new Image(image);
				img.setStyleName(STYLE);
				add(img);
				setCellWidth(img,"1%");
				label = new Label(text);
				label.setWidth("100%");
				label.setStyleName(STYLE);
				add(label);
				setCellWidth(label,"99%");
				StatusPanel.this.add(this);
				StatusPanel.this.setCellWidth(this,"100%");
			}
	
			public Message(String text,boolean end) {
				setWidth("100%");
				label = new Label(text);
				if (end) {
					label.setStyleName(STYLE_END);
				} else {
					label.setStyleName(STYLE);
				}
				label.setWidth("100%");
				add(label);
				setCellWidth(label,"100%");
				StatusPanel.this.add(this);
				StatusPanel.this.setCellWidth(this,"100%");
			}
	
		}

	}
	
	public class ParagraphsPanel extends ScrollContainer {
		private VerticalPanel panel;
		private boolean activated;
		private boolean changed;
		public ParagraphsPanel() {
			panel = new VerticalPanel();
			panel.setSize("100%", "100%");
			setScrollWidget(panel);
		}

		public void resizeOnActivate() {
			activated = false;
		}

		public void clearChanged() {
			changed = false;
		}

		public boolean isChanged() {
			return changed;
		}

		public void update(Model model) {
			panel.clear();
			ArrayList<Paragraph> list = bookImporter.getParagraphs();
			for (int i = 0; i < list.size(); i++) {
				new Item(list.get(i));
			}
			HTML html = new HTML("&nbsp");
			panel.add(html);
			panel.setCellWidth(html,"100%");
			panel.setCellHeight(html,"99%");			
			changed = true;
		}

		public void onActivate() {
			if (activated==false) {
				activated = true;
				resetHeight();
			}
		}

		private void merge(Item item, boolean up) {
			changed = true;
			int pos = panel.getWidgetIndex(item);
			String desc;
			Item mrg;
			if (up) {
				if (pos==0) {
					Window.alert(appConstants.importParagraphIsFirst());
					return;
				}
				mrg = (Item)panel.getWidget(pos-1);
				desc = mrg.paragraph.getDescription()+'\n'+item.paragraph.getDescription();
			} else {
				if (pos==panel.getWidgetCount()-2) {
					Window.alert(appConstants.importParagraphIsLast());
					return;
				}
				mrg = (Item)panel.getWidget(pos+1);
				desc = item.paragraph.getDescription()+'\n'+mrg.paragraph.getDescription();
			}
			mrg.paragraph.setDescription(desc);
			mrg.text.setText(desc);
			panel.remove(pos);
		}
		
		private void split(Item item) {
			changed = true;
			int pos = panel.getWidgetIndex(item);
			Paragraph paragraph = model.addNewParagraph(null);
			paragraph.setName(item.paragraph.getName());
			paragraph.setDescription(item.paragraph.getDescription());
			paragraph.setNumber(item.paragraph.getNumber());
			new Item(paragraph,pos);
		}

		public class Item extends Grid implements ChangeListener,ClickListener{
			private NumberTextBox number;
			private Label name;
			private Label text;
			private Paragraph paragraph;
			private Image mergeUp;
			private Image mergeDown;
			private Image split;
			
			public Item(Paragraph paragraph) {
				this(paragraph,-1);
			}
			public Item(Paragraph paragraph, int before) {
				super(2,2);
				setStyleName("input_par_separator");
				this.paragraph = paragraph;
				getColumnFormatter().setStylePrimaryName(0,"1%");
				getColumnFormatter().setStylePrimaryName(1,"99%");
				getCellFormatter().setHeight(0,0, "1%");
				getCellFormatter().setHeight(1,0, "99%");
				getCellFormatter().setHeight(0,1, "1%");
				getCellFormatter().setHeight(1,1, "99%");
				setSize("100%", "100%");
				number = new NumberTextBox();
				number.setRange(0,9999);
				number.setMaxLength(4);
				number.setVisibleLength(4);
				number.setValue(paragraph.getNumber());
				number.setStyleName("input_par_num");
				number.addChangeListener(this);
				setWidget(0,0,number);
				FlowPanel flowPanel = new FlowPanel();
				flowPanel.setSize("100%", "100%");
				//merge up
				mergeUp = new Image(Images.UP_GREEN);
				mergeUp.setStyleName(Styles.CLICKABLE);
				mergeUp.addClickListener(this);
				mergeUp.setTitle(appConstants.importMergePrevParagraph());
				flowPanel.add(mergeUp);
				//merge down
				mergeDown = new Image(Images.DOWN_GREEN);
				mergeDown.setStyleName(Styles.CLICKABLE);
				mergeDown.addClickListener(this);
				mergeDown.setTitle(appConstants.importMergeNextParagraph());
				flowPanel.add(mergeDown);
				//split
				split = new Image(Images.SPLIT);
				split.setStyleName(Styles.CLICKABLE);
				split.addClickListener(this);
				split.setTitle(appConstants.importSplitParagraph());
				flowPanel.add(split);
				
				setWidget(1,0,flowPanel);
				
				name = new Label();
				name.addStyleName(Styles.CLICKABLE);
				name.setTitle(appConstants.importClickToEdit());
				name.addClickListener(this);
				name.setText(paragraph.getName());
				setWidget(0,1,name);
				
				text = new Label();
				text.addStyleName(Styles.CLICKABLE);
				text.setTitle(appConstants.importClickToEdit());
				text.addClickListener(this);
				applyText();
				setWidget(1,1,text);
				
				if (before<0) {
					ParagraphsPanel.this.panel.add(this);
				} else {
					ParagraphsPanel.this.panel.insert(this,before);
				}
				ParagraphsPanel.this.panel.setCellWidth(this,"100%");
				ParagraphsPanel.this.panel.setCellHeight(this,"1%");
			}
			
			private void applyText() {
				text.setText(paragraph.getDescription());
			}
			public void onChange(Widget sender) {
				if (sender==number) {
					paragraph.setNumber(number.getIntegerValue());
					changed = true;
				}
			}

			public void onClick(Widget sender) {
				if (sender==mergeUp) {
					merge(this,true);
				} else if (sender==mergeDown){
					merge(this,false);
				} else if (sender==split){
					split(this);
				} else if (sender==text) {
					new EditArea();
				} else if (sender==name) {
					new EditText();
				}
			}
			
			public class EditArea extends TextArea implements FocusListener {
				public EditArea() {
					int h = Item.this.text.getOffsetHeight();
					if (h<30) {
						h = 30;
					}
					setSize(String.valueOf(Item.this.text.getOffsetWidth())+"px", String.valueOf(h)+"px");
					addFocusListener(this);
					Item.this.setWidget(1,1,this);
					setText(Item.this.paragraph.getDescription());
					setFocus(true);
				}

				public void onFocus(Widget sender) {
				}

				public void onLostFocus(Widget sender) {
					Item.this.setWidget(1,1,Item.this.text);
					Item.this.paragraph.setDescription(getText().trim());
					applyText();
					changed = true;
				}
				
			}
			
			public class EditText extends TextBox implements FocusListener {
				public EditText() {
					setSize(String.valueOf(Item.this.name.getOffsetWidth())+"px", String.valueOf(Item.this.name.getOffsetHeight())+"px");
					addFocusListener(this);
					Item.this.setWidget(0,1,this);
					setText(Item.this.paragraph.getName());
					setFocus(true);
				}

				public void onFocus(Widget sender) {
				}

				public void onLostFocus(Widget sender) {
					Item.this.setWidget(0,1,Item.this.name);
					Item.this.paragraph.setName(getText().trim());
					Item.this.name.setText(Item.this.paragraph.getName());
					changed = true;
				}
				
			}
		}
	}
	
	public class ConnectionsPanel extends ScrollContainer {
		private VerticalPanel panel;
		private boolean activated;
		private boolean changed;
		private Item nextItem;
		public ConnectionsPanel() {
			panel = new VerticalPanel();
			panel.setSize("100%", "100%");
			setScrollWidget(panel);
		}
		public boolean isHasParagraphs() {
			return panel.getWidgetCount()>1;
		}
		public void addParagraphText(String text) {
			nextItem.addText(text);
		}
		public void addParagraphLink(String link, Paragraph paragraph) {
			nextItem.addLink(link,paragraph);
		}
		public void addParagraphError(String text) {
			nextItem.addError(text);
		}
		
		public void endParagraph(Paragraph paragraph) {
			nextItem = null;
		}
		public void nextParagraph(Paragraph paragraph) {
			nextItem = new Item(paragraph);
		}
		
		public void resizeOnActivate() {
			activated = false;
			
		}
		public void update(Model model) {
			statusPanel.clear();
			bookImporter.parseParagraphsText();
			int l = panel.getWidgetCount();
			//regenerate
			for (int i = 0; i < l; i++) {
				Widget widget = panel.getWidget(i);
				if (widget instanceof Item) {
					Item item = (Item) widget;
					item.applyType();
				}
			}
		}
		
		public void onActivate() {
			if (activated==false) {
				activated = true;
				resetHeight();
			}
		}
		
		public boolean isChanged() {
			return changed;
		}
		
		public void cleanChanged() {
			changed = false;
		}
		
		protected void selectParagraph(Paragraph paragraph) {
			int l = panel.getWidgetCount();
			for (int i = 0; i < l; i++) {
				Widget widget = panel.getWidget(i);
				if (widget instanceof Item) {
					Item item = (Item) widget;
					if (item.paragraph==paragraph) {
						ensureVisible(item);
						break;
					}
					
				}
			}
		}
		
		public void clearOldStartParagraph(Paragraph oldStart) {
			int l = panel.getWidgetCount();
			for (int i = 0; i < l; i++) {
				Widget widget = panel.getWidget(i);
				if (widget instanceof Item) {
					Item item = (Item) widget;
					if (item.paragraph==oldStart) {
						oldStart.setType(Paragraph.TYPE_NORMAL);
						item.applyType();
						break;
					}
					
				}
			}
		}

		public class Item extends Grid implements ClickListener{
			private Label number;
			private Label name;
			private FlowPanel text;
			private Paragraph paragraph;
			private NumberTextBox addNumber;
			private Image add;
			private Image normal;
			private Image home;
			private Image success;
			private Image fail;
			
			public Item(Paragraph paragraph) {
				this(paragraph,-1);
			}
			public void addError(String error) {
				Label label = new SpanLabel(error);
				label.addStyleName("reader_error");
				text.add(label);
			}
			public void addLink(String link, final Paragraph par) {
				Link lnk = new Link(link,par,false);
				text.add(lnk);
			}
			public void addText(String text) {
				Label label = new SpanLabel(text);
				this.text.add(label);
			}
			public Item(Paragraph paragraph, int before) {
				super(2,2);
				setStyleName("input_par_separator");
				this.paragraph = paragraph;
				getColumnFormatter().setStylePrimaryName(0,"1%");
				getColumnFormatter().setStylePrimaryName(1,"99%");
				getCellFormatter().setHeight(0,0, "1%");
				getCellFormatter().setHeight(1,0, "99%");
				getCellFormatter().setHeight(0,1, "1%");
				getCellFormatter().setHeight(1,1, "99%");
				setSize("100%", "100%");
				number = new Label(String.valueOf(paragraph.getNumber()));
				setWidget(0,0,number);
				
				VerticalPanel panel = new VerticalPanel();
				panel.setSize("100%", "100%");
				HorizontalPanel horizontalPanel = new HorizontalPanel();
				horizontalPanel.setSize("100%", "100%");
				//add link contros
				addNumber = new NumberTextBox();
				addNumber.setRange(0, 9999);
				addNumber.setVisibleLength(4);
				addNumber.setTitle(appConstants.importTypeToAddConnections());
				horizontalPanel.add(addNumber);
				horizontalPanel.setCellWidth(addNumber, "1%");
				add = new Image(Images.ADD_CONNECTION);
				add.addStyleName(Styles.CLICKABLE);
				add.setTitle(appConstants.importAddConnection());
				add.addClickListener(this);
				horizontalPanel.add(add);
				horizontalPanel.setCellWidth(add, "99%");
				panel.add(horizontalPanel);
				panel.setCellHeight(horizontalPanel, "1%");
				//type of paragraph
				horizontalPanel = new HorizontalPanel();
				horizontalPanel.setSize("100%", "100%");
				normal = new Image(Images.LOCATION_NORMAL);
				normal.setStyleName(STYLE_TYPE_SELECTED);
				normal.addClickListener(this);
				normal.setTitle(appConstants.importMakeNormal());
				horizontalPanel.add(normal);
				horizontalPanel.setCellWidth(normal, "1%");
				home = new Image(Images.LOCATION_START);
				home.setStyleName(STYLE_TYPE_UNSELECTED);
				home.addClickListener(this);
				home.setTitle(appConstants.importMakeStart());
				horizontalPanel.add(home);
				horizontalPanel.setCellWidth(home, "1%");
				success = new Image(Images.LOCATION_SUCCESS);
				success.setStyleName(STYLE_TYPE_UNSELECTED);
				success.addClickListener(this);
				success.setTitle(appConstants.importMakeSuccess());
				horizontalPanel.add(success);
				horizontalPanel.setCellWidth(success, "1%");
				fail = new Image(Images.LOCATION_FAIL);
				fail.setStyleName(STYLE_TYPE_UNSELECTED);
				fail.addClickListener(this);
				fail.setTitle(appConstants.importMakeFail());
				horizontalPanel.add(fail);
				horizontalPanel.setCellWidth(fail, "1%");

				panel.add(horizontalPanel);
				panel.setCellHeight(horizontalPanel, "99%");
				applyType();
				
				setWidget(1,0,panel);
				
				name = new Label();
				name.addStyleName(Styles.CLICKABLE);
				name.setTitle(appConstants.importClickToEdit());
				name.addClickListener(this);
				name.setText(paragraph.getName());
				setWidget(0,1,name);
				
				text = new FlowPanel();
				text.setStyleName("input_par");
				text.setSize("100%", "100%");
				setWidget(1,1,text);
				
				ConnectionsPanel.this.panel.add(this);
				ConnectionsPanel.this.panel.setCellWidth(this,"100%");
				ConnectionsPanel.this.panel.setCellHeight(this,"1%");
			}
			
			public void onClick(Widget sender) {
				if (sender==name) {
					new EditName();
				} else if (sender==add) {
					addLink();
				} else if (sender==normal) {
					if (paragraph.getType()==Paragraph.TYPE_START) {
						Window.alert(appConstants.importSelectOtherAsStart());
					} else {
						paragraph.setType(Paragraph.TYPE_NORMAL);
					}
					applyType();
				} else if (sender==home) {
					if (paragraph.getType()!=Paragraph.TYPE_START) {
						Paragraph oldStart = model.getStartParagraph();
						clearOldStartParagraph(oldStart);
						model.makeParagraphAsStart(paragraph);
					}
					applyType();
				} else if (sender==success) {
					if (paragraph.getType()==Paragraph.TYPE_START) {
						Window.alert(appConstants.importSelectOtherAsStart());
					} else {
						paragraph.setType(Paragraph.TYPE_SUCCESS);
					}
					applyType();
				} else if (sender==fail) {
					if (paragraph.getType()==Paragraph.TYPE_START) {
						Window.alert(appConstants.importSelectOtherAsStart());
					} else {
						paragraph.setType(Paragraph.TYPE_FAIL);
					}
					applyType();
				}
			}
			
			private void applyType() {
				normal.setStyleName(STYLE_TYPE_UNSELECTED);
				home.setStyleName(STYLE_TYPE_UNSELECTED);
				success.setStyleName(STYLE_TYPE_UNSELECTED);
				fail.setStyleName(STYLE_TYPE_UNSELECTED);
				switch (paragraph.getType()) {
				case Paragraph.TYPE_FAIL:
					fail.setStyleName(STYLE_TYPE_SELECTED);
					break;
				case Paragraph.TYPE_START:
					home.setStyleName(STYLE_TYPE_SELECTED);
					break;
				case Paragraph.TYPE_SUCCESS:
					success.setStyleName(STYLE_TYPE_SELECTED);
					break;
				default:
					//normal
					normal.setStyleName(STYLE_TYPE_SELECTED);
				}
			}
			private void addLink() {
				int num = addNumber.getIntegerValue();
				if (paragraph.getNumber()==num) {
					Window.alert(appConstants.importCannotLinkToItself());
					return;
				}
				ArrayList<Paragraph> list = model.getParagraphs();
				for (int i = 0; i < list.size(); i++) {
					Paragraph par = list.get(i);
					if (par.getNumber()==num) {
						//found
						ArrayList<ParagraphConnection> conns = model.getParagraphConnections();
						for (int j = 0; j < conns.size(); j++) {
							ParagraphConnection connection = conns.get(i);
							if (connection.getFrom()==paragraph && connection.getTo()==par) {
								//already exists
								Window.alert(appConstants.importConnectionExists());
								return;
							}
							if (connection.getTo()==paragraph && connection.getFrom()==par) {
								if (connection.isBothDirections()) {
									Window.alert(appConstants.importConnectionExists());
								} else if (Window.confirm(appConstants.importCretateBiConnection())) {
									connection.setBothDirections(true);
								}
								return;
							}
						}
						//no this connection
						ParagraphConnection connection = new ParagraphConnection();
						connection.setFrom(paragraph);
						connection.setTo(par);
//						connection.setFromId(Model.CONNECTION_ID_PREFIX+paragraph.getId());
//						connection.setToId(Model.CONNECTION_ID_PREFIX+par.getId());
						model.addParagraphConnection(connection, null);
						Link link = new Link(Model.CONNECTION_DELIMETER_FROM+connection.getToId()+Model.CONNECTION_DELIMETER_TO,par,true);
						text.add(link);
						addNumber.setText("");
						return;
					}
				}
				Window.alert(appMessages.importUnknownParagraphNumber(num));
			}
			
			public void removeConnection(Link link) {
				Paragraph to = link.paragraph;
				if (Window.confirm(appMessages.importRemoveConnectionTo(to.getNumber()))) {
					ArrayList<ParagraphConnection> list = model.getParagraphConnections();
					for (int i = 0; i < list.size(); i++) {
						ParagraphConnection connection = list.get(i);
						if (connection.getFrom()==paragraph && connection.getTo()==to) {
							list.remove(i);
							if (link.manual) {
								//remove it
								text.remove(link);
							} else {
								//it is it text, just remove marks
								link.linkRemoved();
							}
							return;
						}
					}
				}
			}
			
			public class EditName extends TextBox implements FocusListener {
				public EditName() {
					setSize(String.valueOf(Item.this.name.getOffsetWidth())+"px", String.valueOf(Item.this.name.getOffsetHeight())+"px");
					addFocusListener(this);
					Item.this.setWidget(0,1,this);
					setText(Item.this.paragraph.getName());
					setFocus(true);
				}

				public void onFocus(Widget sender) {
				}

				public void onLostFocus(Widget sender) {
					Item.this.setWidget(0,1,Item.this.name);
					Item.this.paragraph.setName(getText().trim());
					Item.this.name.setText(Item.this.paragraph.getName());
				}
				
			}
			
			public class Link extends HorizontalPanel implements ClickListener{
				private Label text; 
				private Image remove;
				private Paragraph paragraph;
				private boolean manual;
				public Link(String link, Paragraph par, boolean manual) {
					paragraph = par;
					this.manual = manual;
					text = new Label(link);
					addStyleName("validator_par_marked");
					addStyleName(Styles.CLICKABLE);
					add(text);
					DOM.setStyleAttribute(getElement(),"display","inline");
//					setSize("100%", "100%");
					remove = new Image(Images.REMOVE);
					remove.setTitle(appConstants.importRemoveConnection());
					add(remove);
					remove.addClickListener(this);
					text.addClickListener(this);
				}

				public void linkRemoved() {
					removeStyleName("validator_par_marked");
					removeStyleName(Styles.CLICKABLE);
					text.removeClickListener(this);
					remove(remove);
				}

				public void onClick(Widget sender) {
					if (sender==text) {
						selectParagraph(paragraph);
					} else if (sender==remove) {
						removeConnection(this);
					}
				}
				
			}

		}

	}
	
}
