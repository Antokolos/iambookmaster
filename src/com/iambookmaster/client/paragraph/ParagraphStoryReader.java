package com.iambookmaster.client.paragraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.Images;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.common.EditorTab;
import com.iambookmaster.client.common.ExchangePanel;
import com.iambookmaster.client.common.MaskPanel;
import com.iambookmaster.client.common.ScrollContainer;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ParagraphListener;
import com.iambookmaster.client.paragraph.PathFinder.GameState;
import com.iambookmaster.client.paragraph.PathFinder.WayFinder.ParagraphTransition;

public class ParagraphStoryReader extends ScrollContainer implements EditorTab {
	public static final int TYPE_WHOLE_STORY=0;
	public static final int TYPE_ALL_SUCCESS=1;
	public static final int TYPE_LONG_AND_SHORT_SUCCESS=2;
	private static final String STYLE_SEPARATOR = "reader_separator";
	private static final String STYLE_PARAGRAPH = "reader_paragraph";
	private static final String STYLE_ERROR = "reader_error";
	
	private final AppConstants appConstants = AppLocale.getAppConstants();
	private final AppMessages appMessages = AppLocale.getAppMessages();
	
	private boolean activated;
	private Model model;
	private VerticalPanel wholeStory;
	private Model.FullParagraphDescriptonBuilder fullParagraphDescriptonBuilder;
	private ParagraphConextMenu paragraphConextMenu;
	private ParagraphListener paragraphListener;
	private HashMap<Paragraph,ArrayList<ParagraphWidget>> widgets=new HashMap<Paragraph, ArrayList<ParagraphWidget>>();
	private HashMap<Paragraph,ParagraphCorrectorWidget> exWidgets=new HashMap<Paragraph, ParagraphCorrectorWidget>();
	public ParagraphStoryReader(Model mod) {
		model = mod;
		paragraphListener = new ParagraphListener() {
			public void addNewParagraph(Paragraph location) {
			}
			public void edit(Paragraph location) {
			}
			public void refreshAll() {
				closePanel();
			}
			public void remove(Paragraph location) {
				closePanel();
			}
			public void select(Paragraph location) {
			}
			public void unselect(Paragraph location) {
			}
			public void update(Paragraph location) {
				updateParagraph(location);
			}
			
		};
		model.addParagraphListener(paragraphListener);
		wholeStory = new VerticalPanel();
		wholeStory.setSize("100%", "100%");
		setScrollWidget(wholeStory);
		
		fullParagraphDescriptonBuilder = model.getFullParagraphDescriptonBuilder();
		fullParagraphDescriptonBuilder.setConnectionPattern("<img src=\""+Images.OTHER_CONNECTION+"\"/>");
		fullParagraphDescriptonBuilder.setConnectionMarkedPattern("<img src=\""+Images.SELECTED_CONNECTION+"\"/>");
	}

	private void updateParagraph(Paragraph paragraph) {
		ArrayList<ParagraphWidget> list = widgets.get(paragraph);
		if (list != null) {
			ArrayList<ParagraphConnection> connections = model.getOutputParagraphConnections(paragraph);
			for (int i = 0; i < list.size(); i++) {
				ParagraphWidget widget = list.get(i);
				widget.apply(paragraph, widget.getNextParagraph(),connections);
			}
		}
		ParagraphCorrectorWidget widget = exWidgets.get(paragraph);
		if (widget != null) {
			widget.apply(paragraph);
		}
	}

	/**
	 * Close this panel
	 */
	private void closePanel() {
		model.removeParagraphListener(paragraphListener);
	}

	public void viewAllParagraphConnectionNames() {
		ArrayList<ParagraphConnection> connections = model.getParagraphConnections();
		for (ParagraphConnection paragraphConnection : connections) {
			if (paragraphConnection.isConditional()) {
				
			} else {
				//show from-to
				
			}
		}
	}

	public void externalCorrection() {
		StringBuffer buffer = new StringBuffer();
		wholeStory.clear();
		exWidgets.clear();
		final ArrayList<Paragraph> list = model.getParagraphs();
		for (int i = 0; i < list.size(); i++) {
			Paragraph paragraph = list.get(i);
			buffer.append(i);
			buffer.append('\n');
			buffer.append(paragraph.getDescription());
			buffer.append('\n');
			buffer.append('\n');
			exWidgets.put(paragraph,new ParagraphCorrectorWidget(i,paragraph));
		}
		ExchangePanel exchangePanel = new ExchangePanel() {
			public boolean processLoad(String text) {
				text = text.replace("\r\n", "\n");
				StringBuilder parse = new StringBuilder(text);
				//check it first
				for (int i = 0; i < list.size(); i++) {
					//import next paragraph
					if (parse.length()==0) {
						Window.alert(appMessages.modelBulkCorrectionLoadUnexpectedEnd(i));
					}
					int next = selectNextParagraph(parse,null);
					if (next != i ) {
						if (next<0) {
							Window.alert(appMessages.modelBulkCorrectionLoadUnexpectedEnd(i));
						} else {
							Window.alert(appMessages.modelBulkCorrectionLoadWrongNumber(next,i));
						}
						return false;
					} 
				}
				//real import 
				parse.append(text);
				for (int i = 0; i < list.size(); i++) {
					//import next paragraph
					StringBuilder builder = new StringBuilder();
					selectNextParagraph(parse,builder);
					Paragraph paragraph = list.get(i);
					paragraph.setDescription(builder.toString());
					model.updateParagraph(paragraph,null);
				}
				MaskPanel.hide();
				return true;
			}

			private int selectNextParagraph(StringBuilder source, StringBuilder text) {
				int number=-1;
				while (true){
					String line = getNextLine(source).trim();
					if (isNumber(line)) {
						if (number<0) {
							//number must be first
							number = Integer.parseInt(line);
						} else {
							//text, we found next paragraph
							source.insert(0, '\n');
							source.insert(0,line);
							if (text != null) {
								int l = text.length();
								if (l>1 && text.substring(l-1).equals("\n")) {
									text.setLength(l-1);
								}
							}
							return number;
						}
					} else if (number<0) {
						//number must be first
						if (line.length()>0) {
							//non-empty, error
							return -1;
						}
					} else if (text != null){
						//text
						if (text.length()>0) {
							text.append('\n');
						}
						text.append(line);
					}
					if (source.length()==0) {
						return number;
					}
				}
			}

			private boolean isNumber(String line) {
				for (int i = 0; i < line.length(); i++) {
					if (Character.isDigit(line.charAt(i))==false) {
						return false;
					}
				}
				return line.length()>0;
			}

			private String getNextLine(StringBuilder source) {
				int l = source.indexOf("\n");
				String res;
				if (l==0) {
					//empty line
					source.replace(0, 1, "");
					res="";
				} else if (l>0) {
					res = source.substring(0,l);
					source.replace(0, l+1, "");
					return res;
				} else {
					res = source.toString();
					source.setLength(0);
				}
				return res;
			}

			@Override
			protected void onClose() {
				if (Window.confirm(appConstants.modelBulkCorrection())) {
					showLoad(appConstants.modelBulkCorrectionLoad());
				}
			}
			
		};
		exchangePanel.showExport(buffer.toString(),appConstants.modelTextForCorrection());
	}

	public void create(final int type) {
		MaskPanel.show();
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				create(type,null);
				MaskPanel.hide();
			}
		});
	}
	public void create(int type,Paragraph selectedSuccess) {
		wholeStory.clear();
		widgets.clear();
		Paragraph start = model.getStartParagraph();
		if (start==null) {
			fatalError(appConstants.modelStartParagraphNotSet());
			return;
		}
		ArrayList<ArrayList<Paragraph>> stories;
		PathFinder finder = new PathFinder(model);
		int findMode;
		switch (type) {
		case TYPE_ALL_SUCCESS:
			findMode = PathFinder.FIND_ALL;
			break;
		case TYPE_LONG_AND_SHORT_SUCCESS:
			findMode = PathFinder.FIND_MIN_MAX;
			break;
		default:
			//TYPE_WHOLE_STORY
			findMode = PathFinder.FIND_ONE; //FIND_ALL;
			break;
		}
		if (selectedSuccess==null) {
			ArrayList<Paragraph> success = model.getAllSuccessParagraphs();
			if (success.size()==0) {
				fatalError(appConstants.modelSuccessParagraphsNotSet());
				return;
			}
			//collect all possible success stories
			stories = new ArrayList<ArrayList<Paragraph>>();
			for (int i = 0; i < success.size(); i++) {
				Paragraph succes = success.get(i);
				GameState objects = finder.new GameState(); 
				ArrayList<ArrayList<Paragraph>> res = finder.findWays(start, succes, objects, null, null, findMode);
				if (res != null) {
					for (int j = 0; j < res.size(); j++) {
						stories.add(res.get(j));
					}
				}
			}
		} else {
			//show only stories to this final
			GameState objects = finder.new GameState(); 
			stories = finder.findWays(start, selectedSuccess, objects, null, null, findMode);
		}
		
		if (stories.size()==0) {
			//no way to pass
			fatalError(appConstants.modelSuccessParagraphsCannotBeReached());
			return;
		}
		
		switch (type) {
		case TYPE_ALL_SUCCESS:
			for (int i = 0; i < stories.size(); i++) {
				if (i>0) {
					addSeparator();
				}
				drawStory(stories.get(i));
			}
			break;
		case TYPE_LONG_AND_SHORT_SUCCESS:
			//show longes and shortes stories
			int max = 0;
			int maxSize = stories.get(max).size();
			int min = 0;
			int minSize = stories.get(min).size();
			for (int i = 1; i < stories.size(); i++) {
				int cur = stories.get(i).size();
				if (cur>maxSize) {
					max = i;
				}
				if (cur<minSize) {
					min = i;
				}
			}
			if (min==max) {
				//the same
				drawStory(stories.get(min));
			} else {
				//different
				drawStory(stories.get(max));
				addSeparator();
				drawStory(stories.get(min));
			}
			break;

		default:
			//TYPE_WHOLE_STORY
			drawWholeStory(stories,finder);
			break;
		}
		//filler
		HTML html = new HTML("&nbsp;");
		wholeStory.add(html);
		wholeStory.setCellHeight(html,"99%");
		wholeStory.setCellWidth(html,"100%");
	}

	private void addSeparator() {
		HTML html = new HTML("&nbsp;");
		html.setStyleName(STYLE_SEPARATOR);
		wholeStory.add(html);
		wholeStory.setCellHeight(html,"1%");
		wholeStory.setCellWidth(html,"100%");
		
	}

	private void drawWholeStory(ArrayList<ArrayList<Paragraph>> stories, PathFinder finder) {
		HashMap<Paragraph,ArrayList<ParagraphTransition>> map = finder.getMap();
		
		int max = 0;
		int maxSize = stories.get(max).size();
		for (int i = 1; i < stories.size(); i++) {
			int cur = stories.get(i).size();
			if (cur>maxSize) {
				max = i;
			}
		}

		//draw the longest story
		HashSet<ObjectBean> objects = new HashSet<ObjectBean>();
		ArrayList<Paragraph> mainStory = stories.get(max);
		stories.remove(max);
		int l = mainStory.size();
		for (int i = 0; i < l; i++) {
			Paragraph paragraph = mainStory.get(i);
			objects.addAll(paragraph.getGotObjects());
			objects.removeAll(paragraph.getLostObjects());
			Paragraph next;
			if (i<l-1) {
				next = mainStory.get(i+1);
			} else {
				next = null;
			}
			new ParagraphWidget(paragraph,next,objects,true,map);
		}
		mainStory = null;
		
		//scan other stories
		for (int i = 1; i < stories.size(); i++) {
			ArrayList<Paragraph> story = stories.get(i);
			objects.clear();
			boolean chain=false;
			int len = story.size()-1;
			for (int j = 0; j < len; j++) {
				Paragraph paragraph = story.get(j);
				objects.addAll(paragraph.getGotObjects());
				objects.removeAll(paragraph.getLostObjects());
				boolean nextChain=false;
				Paragraph next = story.get(j+1);
				ArrayList<ParagraphTransition> trans = map.get(paragraph);
				if (trans != null) {
					for (int k = 0; k < trans.size(); k++) {
						ParagraphTransition transition = trans.get(k);
						if (transition.getParagraph()==next) {
							//found
//							new ParagraphConnectionWidget(paragraph,transition.getConnection());
//							if (trans.size()==1) {
//								map.remove(paragraph);
//							} else {
//								trans.remove(k);
//							}
							nextChain=true;
							break;
						}
					}
				}
				if (chain) {
					//chain
					if (nextChain) {
						//continue chain
					} else {
						//used, stop chain
						chain=false;
						new ParagraphWidget(paragraph,null,objects,false,map);
						continue;
					}
				} else {
					//no current chain
					if (nextChain) {
						//unused, start chan
						addSeparator();
//						if (j>0) {
//							//draw prev. paragraph
//							Paragraph prev = story.get(j-1);
//							if (map.containsKey(prev)) {
//								//remove this connection
//								removeUserConnection(prev,paragraph,map);
//							}
//							new ParagraphWidget(prev,paragraph,objects,false);
//						}
						chain = true;
					} else {
						continue;
					}
				}
				new ParagraphWidget(paragraph,next,objects,true,map);
			}
		}
		
		//draw all other connections
		while (map.isEmpty()==false) {
			Iterator<Paragraph> iterator = map.keySet().iterator();
			Paragraph paragraph = iterator.next();
			ArrayList<ParagraphTransition> trans = map.get(paragraph);
			if (trans==null) {
				//already used
				iterator.remove();
				continue;
			}
			//unused
			addSeparator();
			boolean add=false;
			objects.clear();
			outter:
			while (true) {
				for (int i = 0; i < trans.size(); i++) {
					ParagraphTransition transition = trans.get(i);
					if (map.get(transition.getParagraph()) != null) {
						//unused too, it is chain, follow it
						if (transition.getConnection().getObject() != null) {
							//fake this object
							objects.add(transition.getConnection().getObject());
						}
//						new ParagraphConnectionWidget(paragraph,transition.getConnection());
						new ParagraphWidget(paragraph,transition.getParagraph(),objects,true,map);
//						if (trans.size()==1) {
//							//the last
//							map.put(paragraph,null);
//						} else {
//							trans.remove(i);
//						}
						paragraph = transition.getParagraph();
						trans = map.get(paragraph);
						continue outter;
					}
				}
				//no chain or end, mark as used
//				map.put(paragraph,null);
//				iterator.remove();
				//draw all
				for (int i = 0; i < trans.size(); i++) {
					ParagraphTransition transition = trans.get(i);
					if (transition.getConnection().getObject() != null) {
						//fake this object
						objects.add(transition.getConnection().getObject());
					}
					if (add) {
						addSeparator();
					} else {
						add = true;
					}
//					new ParagraphConnectionWidget(paragraph,transition.getConnection());
					new ParagraphWidget(paragraph,transition.getParagraph(),objects,true,map);
					new ParagraphWidget(transition.getParagraph(),null,objects,true,map);
				}
				break;
			}
		}
	}

	private void removeUserConnection(Paragraph paragraph, Paragraph next, HashMap<Paragraph, ArrayList<ParagraphTransition>> map) {
		ArrayList<ParagraphTransition> trans = map.get(paragraph);
//		if (trans == null) {
//			System.out.println("!!!");
//		}
		for (int j = 0; j < trans.size(); j++) {
			ParagraphTransition transition = trans.get(j);
			if (transition.getParagraph()==next) {
				//found, add to the panel
				new ParagraphConnectionWidget(paragraph,transition.getConnection());
				//remove from the list
				if (trans.size()==1) {
					//all connections were used
					map.remove(paragraph);
				} else {
					//used this connection
					trans.remove(j);
				}
				return;
			}
		}
	}

	public class ParagraphConnectionWidget extends Label implements ClickHandler{

		private ParagraphConnection connection;

		public ParagraphConnectionWidget(Paragraph from, ParagraphConnection connection) {
			if (connection.isHiddenUsage(model.getSettings())) {
				//invisible
				return;
			}
			this.connection = connection;
			setWidth("100%");
			setStyleName(Styles.CLICKABLE);
			addStyleName(Styles.BOLD);
			addClickHandler(this);
			if (connection.getFrom()==from) {
				//forward
				setText(connection.getNameFrom());
			} else {
				//back
				setText(connection.getNameTo());
			}
			wholeStory.add(this);
			wholeStory.setCellHeight(this, "1%");
			wholeStory.setCellWidth(this, "100%");
		}

		public void onClick(ClickEvent event) {
			model.selectParagraphConnection(connection, null);
		}

	}

	/**
	 * Show all possible sucees stories
	 * @param stories
	 */
	private void drawStory(ArrayList<Paragraph> mainStory) {
		int l = mainStory.size();
		HashSet<ObjectBean> objects = new HashSet<ObjectBean>();
		for (int i = 0; i < l; i++) {
			Paragraph paragraph = mainStory.get(i);
			objects.addAll(paragraph.getGotObjects());
			objects.removeAll(paragraph.getLostObjects());
			Paragraph next;
			if (i<l-1) {
				next = mainStory.get(i+1);
			} else {
				next = null;
			}
			new ParagraphWidget(paragraph,next,objects,true,null);
		}
	}

	private void fatalError(String text) {
		Label label = new Label(text);
		label.setStyleName(STYLE_ERROR);
		wholeStory.add(label);
	}
	
	
	public void showParagraphContextMenu(ParagraphWidget widget, Widget sender) {
		if (paragraphConextMenu==null) {
			paragraphConextMenu = new ParagraphConextMenu();
		}
		paragraphConextMenu.show(widget,sender);
	}

	public class ParagraphConextMenu extends PopupPanel {

		private ParagraphWidget currentWidget;
		private MenuBar newConnectionMenu;
		private MenuItem markFinal;
		private MenuItem markDraft;
		private MenuItem markProposal;
		
		public ParagraphConextMenu() {
			super(true,true);
			newConnectionMenu = new MenuBar(true);
			newConnectionMenu.addItem(appConstants.buttonEdit(),new Command() {
				public void execute() {
					model.selectParagraph(currentWidget.getParagraph(), null);
					model.editParagraph(currentWidget.getParagraph(), null);
					hide();
				}
			});
			
			markFinal = new MenuItem(appConstants.buttonMarkFinal(),new Command() {
				public void execute() {
					updateStatus(Model.STATUS_FINAL);
				}
			});
			newConnectionMenu.addItem(markFinal);
			
			markDraft = new MenuItem(appConstants.buttonMarkDraft(),new Command() {
				public void execute() {
					updateStatus(Model.STATUS_DRAFT);
				}
			});
			newConnectionMenu.addItem(markDraft);
			
			markProposal = new MenuItem(appConstants.buttonMarkProposal(),new Command() {
				public void execute() {
					updateStatus(Model.STATUS_PROPOSAL);
				}
			});
			newConnectionMenu.addItem(markProposal);
			
			add(newConnectionMenu);
		}

		private void updateStatus(int status) {
			currentWidget.getParagraph().setStatus(status);
			model.updateParagraph(currentWidget.getParagraph(), null);
			hide();
		}

		public void show(ParagraphWidget widget, Widget sender) {
			currentWidget = widget;
			switch (widget.getParagraph().getStatus()) {
			case Model.STATUS_DRAFT:
				markDraft.setVisible(false);
				markFinal.setVisible(true);
				markProposal.setVisible(true);
				break;
			case Model.STATUS_FINAL:
				markDraft.setVisible(true);
				markFinal.setVisible(false);
				markProposal.setVisible(true);
				break;
			default:
				markDraft.setVisible(true);
				markFinal.setVisible(true);
				markProposal.setVisible(false);
				//Model.STATUS_PROPOSAL:
			}
			setPopupPosition(sender.getAbsoluteLeft(), sender.getAbsoluteTop());
			show();
		}
		
	}
	
	public class ParagraphWidget extends HorizontalPanel implements ClickListener{
		private Image image;
		private HTML html;
		private Paragraph paragraph;
		private Paragraph nextParagraph;
		private HashSet<ObjectBean> objects;
		public ParagraphWidget(Paragraph par, Paragraph next, HashSet<ObjectBean> objects, boolean active, HashMap<Paragraph, ArrayList<ParagraphTransition>> map) {
			paragraph = par;
			nextParagraph = next;
			setSize("100%", "100%");
			setStyleName(STYLE_PARAGRAPH);
			image = new Image();
			image.addStyleName("clickable");
			image.setTitle(appConstants.titleContextMenu());
			image.addClickListener(this);
			add(image);
			setCellWidth(image, "1%");
			html = new HTML();
			html.setStyleName("reader_text");
			html.setSize("100%", "100%");
			add(html);
			setCellWidth(html, "99%");
			wholeStory.add(this);
			wholeStory.setCellHeight(this,"1%");
			wholeStory.setCellWidth(this,"100%");
			ArrayList<ParagraphWidget> list = widgets.get(paragraph);
			if (list==null) {
				list = new ArrayList<ParagraphWidget>();
				widgets.put(paragraph,list);
			}
			list.add(this);
			this.objects = new HashSet<ObjectBean>(objects.size());
			this.objects.addAll(objects);
			apply(paragraph,nextParagraph,null);
			if (par != null && map != null && map.containsKey(par)) {
				//remove this connection
				removeUserConnection(par,next,map);
			}
		}
		
		public void apply(Paragraph paragraph, Paragraph nextParagraph,ArrayList<ParagraphConnection> outputConnections) {
			ArrayList<String> errors = new ArrayList<String>();
			fullParagraphDescriptonBuilder.setObjects(objects);
			String text = fullParagraphDescriptonBuilder.getFullParagraphDescripton(paragraph, null, errors,nextParagraph,outputConnections);
			String url;
			if (errors.size()>0) {
				//has errors
				url = Images.PARAPGRAPH_ERROR;
			} else if (paragraph.getStatus()==Model.STATUS_FINAL){
				//no errors, final
				url = Images.PARAPGRAPH_FINAL;
			} else {
				url = Images.PARAPGRAPH_NOT_FINAL;
			}
			image.setUrl(url);
			html.setHTML(text);
		}

		public void onClick(Widget sender) {
			showParagraphContextMenu(this,sender);
		}

		public Paragraph getParagraph() {
			return paragraph;
		}

		public Paragraph getNextParagraph() {
			return nextParagraph;
		}
		
	}

	public class ParagraphCorrectorWidget extends VerticalPanel implements ClickListener{
		private Paragraph paragraph;
		private Label text;
		public ParagraphCorrectorWidget(int number, Paragraph par) {
			setSize("100%", "100%");
			setStyleName(STYLE_PARAGRAPH);
			Label label = new Label(String.valueOf(number));
			label.addStyleName(Styles.CLICKABLE);
			label.addStyleName(Styles.PARGRAPH_NUMBER);
			label.addClickListener(this);
			add(label);
			setCellHeight(label, "1%");
			setCellWidth(label, "100%");
			text = new Label();
			text.addStyleName(Styles.CLICKABLE);
			text.addClickListener(this);
			add(text);
			setCellHeight(text, "99%");
			setCellWidth(text, "100%");
			apply(par);
			wholeStory.add(this);
			wholeStory.setCellHeight(this,"1%");
			wholeStory.setCellWidth(this,"100%");
		}

		public void apply(Paragraph par) {
			paragraph = par;
			text.setText(par.getDescription());
		}

		public void onClick(Widget sender) {
			model.selectParagraph(paragraph, null);
			model.editParagraph(paragraph, null);
			if (sender==text) {
				//mark
				text.addStyleName(Styles.PARGRAPH_MARKED);
			} else {
				text.removeStyleName(Styles.PARGRAPH_MARKED);
			}
		}

	}

	public void activate() {
		if (activated==false) {
			activated = true;
			resetHeight();
		}
	}

	public void close() {
		model.removeParagraphListener(paragraphListener);
	}

	public void deactivate() {
	}


}
