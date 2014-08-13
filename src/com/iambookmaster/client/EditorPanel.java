package com.iambookmaster.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.Picture;
import com.iambookmaster.client.beans.Sound;
import com.iambookmaster.client.common.EditorPlayer;
import com.iambookmaster.client.common.EditorTab;
import com.iambookmaster.client.editor.ModelPersist;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.ContentListener;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ObjectListener;
import com.iambookmaster.client.model.ParagraphListener;
import com.iambookmaster.client.model.ParameterListener;
import com.iambookmaster.client.paragraph.ExportBookCallback;
import com.iambookmaster.client.paragraph.ParagraphEditor;
import com.iambookmaster.client.paragraph.ParagraphFindAndReplace;
import com.iambookmaster.client.paragraph.ParagraphStoryReader;
import com.iambookmaster.client.paragraph.ParagraphValidator;
import com.iambookmaster.client.paragraph.ParagraphsMapEditor;
import com.iambookmaster.client.player.Player;
import com.iambookmaster.client.player.PlayerListener;

public class EditorPanel extends VerticalPanel{
	
	public static final int PLAYER_WEB = 0;
	public static final int PLAYER_IPHONE = 1;
	public static final int PLAYER_IPAD = 2;
	public static final int PLAYER_800X600 = 3;
	public static final int PLAYER_1024X600 = 4;
	public static final int PLAYER_URQ = 5;

	private AppConstants appConstants = AppLocale.getAppConstants();
	
	private TabPanel tabPanel;
	private ParagraphsMapEditor mapEditor;
	private PlotEditor plotEditor;
	private EditorPlayer player;
	private ParagraphValidator validator;
	private ParagraphStoryReader wholeReader;
	private ModelPersist model;
	private ParagraphEditor paragraphEditor;
	private int currentWidget; 
	private ServerExchangePanel serverExchangePanel;
	private InfoPanel infoPanel;

	private ParagraphFindAndReplace replacer;

	private RulesEditor rules;
	private int currentPlayerMode;
	private CommercialTextEditor commercial;
	
	public EditorPanel(Model model) {
		this.model = (ModelPersist)model;
		setSize("100%", "100%");
		tabPanel = new TabPanel();
		tabPanel.setSize("100%", "100%");
		tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
				if (event.getItem() != currentWidget) {
					Widget widget = tabPanel.getWidget(currentWidget);
					if (widget instanceof EditorTab) {
						EditorTab editorTab = (EditorTab)widget;
						editorTab.deactivate();
					}
				}
			}
		});
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			public void onSelection(SelectionEvent<Integer> event) {
				currentWidget = event.getSelectedItem();
				Widget widget = tabPanel.getWidget(event.getSelectedItem());
				if (widget instanceof EditorTab) {
					EditorTab editorTab = (EditorTab)widget;
					editorTab.activate();
				}
			}
		});
		//add initial tabs
		mapEditor = new ParagraphsMapEditor(model);
		tabPanel.add(mapEditor, appConstants.ParagraphsMapEditorTitle());
		plotEditor = new PlotEditor(model);
		tabPanel.add(plotEditor, appConstants.PlotEditor());
		serverExchangePanel = new ServerExchangePanel();
		tabPanel.add(serverExchangePanel, appConstants.serverPanelTitle());
		//set parameters to Tabs
		tabPanel.getDeckPanel().setSize("100%", "100%");
		add(tabPanel);
		setCellHeight(tabPanel, "100%");
		setCellWidth(tabPanel, "100%");
		tabPanel.selectTab(0);
		
		
		model.addParagraphListener(new ParagraphListener() {
			public void addNewParagraph(Paragraph location) {
			}
			public void edit(Paragraph location) {
				if (paragraphEditor==null) {
					paragraphEditor = new ParagraphEditor(EditorPanel.this.model,location);
					tabPanel.add(paragraphEditor, new TabWidget(appConstants.ParagraphEditorTitle(),paragraphEditor));
				} else {
					paragraphEditor.open(location);
				}
				tabPanel.selectTab(tabPanel.getWidgetIndex(paragraphEditor));
			}
			public void refreshAll() {
			}
			public void select(Paragraph location) {
			}
			public void unselect(Paragraph location) {
			}
			public void update(Paragraph location) {
			}
			public void remove(Paragraph location) {
			}
		});
		model.addObjectsListener(new ObjectListener(){
			public void addNewObject(ObjectBean object) {
			}
			public void refreshAll() {
			}
			public void remove(ObjectBean object) {
			}
			public void select(ObjectBean object) {
			}
			public void showInfo(ObjectBean object) {
				checkAndSelectInfoPanel();
				infoPanel.showInfo(object);
			}
			public void unselect(ObjectBean object) {
			}
			public void update(ObjectBean object) {
			}
		});
		model.addContentListener(new ContentListener(){
			public void addNew(Picture picture) {
			}
			public void addNew(Sound sound) {
			}
			public void refreshAll() {
			}
			public void remove(Picture picture) {
			}
			public void remove(Sound sound) {
			}
			public void select(Sound sound) {
			}
			public void select(Picture picture) {
			}
			public void showInfo(Picture picture) {
				checkAndSelectInfoPanel();
				infoPanel.showInfo(picture);
			}
			public void showInfo(Sound sound) {
				checkAndSelectInfoPanel();
				infoPanel.showInfo(sound);
			}

			public void unselect(Sound sound) {
			}

			public void unselect(Picture picture) {
			}

			public void update(Picture picture) {
			}

			public void update(Sound sound) {
			}
			
		});
		model.addParamaterListener(new ParameterListener(){
			public void addNewParameter(AbstractParameter parameter) {
			}

			public void refreshAll() {
			}

			public void remove(AbstractParameter parameter) {
			}

			public void select(AbstractParameter parameter) {
			}

			public void showInfo(AbstractParameter parameter) {
				checkAndSelectInfoPanel();
				infoPanel.showInfo(parameter);
			}

			public void update(AbstractParameter parameter) {
			}
			
		});
	}
	public void onResize() {
		mapEditor.resetHeight();
		if (paragraphEditor != null) {
			if (paragraphEditor.isVisible()) {
				paragraphEditor.onResize();
			} else {
				paragraphEditor.activateLater();
			}
		}
	}
	
	public void play(int mode) {
		if (player!=null && currentPlayerMode != mode) {
			removeTab(player);
		}
		if (player==null) {
			PlayerListener listener = new PlayerListener() {
				public void edit(Paragraph location) {
				}

				public void showErrors(Paragraph location,ArrayList<String> errors) {
					StringBuffer buffer = new StringBuffer(appConstants.TheFollowinErrorsWereDetect());
					for (int i = 0; i < errors.size(); i++) {
						buffer.append(errors.get(i));
						buffer.append('\n');
					}
					Window.alert(buffer.toString());
				}

				public void emptyDescription(Paragraph location) {
					EditorPanel.this.model.regenerateText(location, Model.EXPORT_ALL);
				}
			};
			currentPlayerMode = mode;
			switch (mode) {
			case PLAYER_WEB:
				player = new Player(model,listener,null);
				break;
			case PLAYER_IPHONE:
				player = new IPhonePlayerWrapper(model,listener,480,320);
				break;
			case PLAYER_1024X600:
				player = new IPadPlayerWrapper(model,listener,1024,600);
				break;
			case PLAYER_800X600:
				player = new IPadPlayerWrapper(model,listener,800,600);
				break;
			case PLAYER_IPAD:
				player = new IPadPlayerWrapper(model,listener,1024,768);
				break;
			case PLAYER_URQ:
				player = new IPhoneURQWrapper(listener,480,320);
				break;
			}
			tabPanel.add((Widget)player, new TabWidget(appConstants.PlayerTitle(),player));
		}
		tabPanel.selectTab(tabPanel.getWidgetIndex((Widget)player));
		player.start();
	}
	
	public void validateAll() {
		checkAndSelectValidator();
		validator.startTesting(true,true);
	}
	
	private void checkAndSelectInfoPanel() {
		if (infoPanel==null) {
			infoPanel = new InfoPanel(EditorPanel.this.model);
		}
		if (tabPanel.getWidgetIndex(infoPanel)<0) {
			tabPanel.add(infoPanel, new TabWidget(appConstants.infoPanelTitle(),infoPanel));
		}
		tabPanel.selectTab(tabPanel.getWidgetIndex(infoPanel));
	}
	
	private void checkAndSelectValidator() {
		if (validator==null) {
			validator = new ParagraphValidator(model);
			tabPanel.add(validator, new TabWidget(appConstants.ValidatorTitle(),validator));
		}
		tabPanel.selectTab(tabPanel.getWidgetIndex(validator));
	}
	public void wholeRead() {
		showStories();
		wholeReader.create(ParagraphStoryReader.TYPE_WHOLE_STORY);
	}
	
	public void successRead() {
		if (Window.confirm(appConstants.WarininCollectionAllStories())) {
			showStories();
			wholeReader.create(ParagraphStoryReader.TYPE_ALL_SUCCESS);
		}
	}

	public void successLongAndShort() {
		showStories();
		wholeReader.create(ParagraphStoryReader.TYPE_LONG_AND_SHORT_SUCCESS);
	}
	
	private void showStories() {
		if (wholeReader==null) {
			wholeReader = new ParagraphStoryReader(model);
			tabPanel.add(wholeReader, new TabWidget(appConstants.StoryReaderTitle(),wholeReader));
		}
		tabPanel.selectTab(tabPanel.getWidgetIndex(wholeReader));
	}
	
	public void validateMap() {
		checkAndSelectValidator();
		validator.startTesting(false,true);
	}
	
	public void validateText() {
		checkAndSelectValidator();
		validator.startTesting(true,false);
	}
	
	public void validateConnectionNames() {
		checkAndSelectValidator();
		validator.testConnections();
	}
	
	public void exportBook(boolean reExport, ExportBookCallback callback) {
		checkAndSelectValidator();
		validator.createText(callback,reExport);
	}
	
	public void externalCorrection() {
		showStories();
		wholeReader.externalCorrection();
	}
	
	private void removeTab(EditorTab tab) {
		tab.close();
		//select other tab
		tabPanel.selectTab(0);
		tabPanel.remove((Widget)tab);
		if (tab==player) {
			player = null;
		} else if (tab==paragraphEditor) {
			paragraphEditor = null;
		} else if (tab==wholeReader) {
			wholeReader = null;
		} else if (tab==validator) {
			validator = null;
		}  
		
	}
	
	public class TabWidget extends HorizontalPanel {
		private EditorTab tab;
		private Image image;
		public TabWidget(String name,EditorTab tb) {
			this.tab = tb;
			Label label = new Label(name,false);
			label.setStyleName(Styles.TAB_LABEL);
			add(label);
			image = new Image(Images.CLOSE_PANEL);
			image.setStyleName(Styles.CLICKABLE);
			image.setTitle("Close tab");
			image.addMouseDownHandler(new MouseDownHandler() {
				public void onMouseDown(MouseDownEvent event) {
					if (Window.confirm(appConstants.CloseTabConfirm())) {
						removeTab(tab);
					}
				}
			});
			image.addMouseOverHandler(new MouseOverHandler() {
				public void onMouseOver(MouseOverEvent event) {
					image.setUrl(Images.CLOSE_PANEL_ON);
				}
			});
			image.addMouseOutHandler(new MouseOutHandler() {
				public void onMouseOut(MouseOutEvent event) {
					image.setUrl(Images.CLOSE_PANEL);
				}
			});
			add(image);
		}
		
	}

	public void serverLogin() {
		serverExchangePanel.performLogin();
		tabPanel.selectTab(tabPanel.getWidgetIndex(serverExchangePanel));
	}
	
	public void publishBook(final boolean reExport) {
		checkAndSelectValidator();
		validator.startTesting(true,true,true,new ExportBookCallback() {
			public void onError() {
				//all errors were already shown
			}
			public void onSuccess(String text) {
				serverExchangePanel.performPublishing((ModelPersist)model,reExport);
				tabPanel.selectTab(tabPanel.getWidgetIndex(serverExchangePanel));
			}
		});
		
	}
	public void setServerURL(String url) {
		serverExchangePanel.setServerURL(url);
	}
	public void saveBookToServer() {
		serverExchangePanel.performSave((ModelPersist)model);
		tabPanel.selectTab(tabPanel.getWidgetIndex(serverExchangePanel));
	}
	public void serverDone() {
		serverExchangePanel.done();
	}
	public void activateParagraphMap() {
		tabPanel.selectTab(tabPanel.getWidgetIndex(mapEditor));
	}

	private void checkAndSelectFindAndReplace() {
		if (replacer==null) {
			replacer = new ParagraphFindAndReplace(model);
			tabPanel.add(replacer, new TabWidget(appConstants.FindAndReplaceTitle(),replacer));
		}
		tabPanel.selectTab(tabPanel.getWidgetIndex(replacer));
	}
	
	public void findAndReplace() {
		checkAndSelectFindAndReplace();
	}
	
	private void checkAndSelectRules() {
		if (rules==null) {
			rules = new RulesEditor(model);
			tabPanel.add(rules, new TabWidget(appConstants.EditRulesTitle(),rules));
		}
		tabPanel.selectTab(tabPanel.getWidgetIndex(rules));
	}
	
	public void editRules() {
		checkAndSelectRules();
	}
	public void findAllCommercials() {
		if (Window.confirm(appConstants.confirmFindCommercial())) {
			checkAndSelectValidator();
			validator.findCommercialParagraph();
		}
	}
	public void editCommercialWelcome() {
		if (commercial==null) {
			commercial = new CommercialTextEditor(model);
			tabPanel.add(commercial, new TabWidget(appConstants.commercialWelcomeText(),commercial));
		}
		tabPanel.selectTab(tabPanel.getWidgetIndex(commercial));
	}

}
