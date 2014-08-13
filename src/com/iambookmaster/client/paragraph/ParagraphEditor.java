package com.iambookmaster.client.paragraph;

import java.util.ArrayList;
import java.util.HashMap;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.Images;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.beans.ParametersCalculation;
import com.iambookmaster.client.beans.Picture;
import com.iambookmaster.client.beans.Sound;
import com.iambookmaster.client.beans.Sprite;
import com.iambookmaster.client.common.EditorTab;
import com.iambookmaster.client.common.ResizeListener;
import com.iambookmaster.client.common.ScrollContainer;
import com.iambookmaster.client.common.SimpleAbstractParameterListBox;
import com.iambookmaster.client.common.StatusPicker;
import com.iambookmaster.client.common.TrueVerticalSplitPanel;
import com.iambookmaster.client.editor.NPCList;
import com.iambookmaster.client.editor.ObjectsList;
import com.iambookmaster.client.editor.ParametersCalculationWidget;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.ContentListener;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ParagraphConnectionListener;
import com.iambookmaster.client.model.ParagraphListener;

public class ParagraphEditor extends VerticalPanel implements EditorTab{

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	private static final AppMessages appMessages = AppLocale.getAppMessages();

	private Paragraph paragraph;
	private Model model;
	private ParagraphListener paragraphListener;
	private ContentListener contentListener;
	private ParagraphConnectionListener connectionListener;
	private TextBox name;
	private TextArea locationText;
	private StatusPicker status;
	private TrueVerticalSplitPanel splitPanel;
	private TrueVerticalSplitPanel splitPanel2;
	private HorizontalPanel prevParagraphs;
	private HorizontalPanel nextParagraphs;
	private boolean activationNeed=true;
	private Picture currentPicture;
	private Sound currentSound;
	private ObjectsList getObjects;
	private ObjectsList lostObjects;
	private NPCList enemies;
	private SimpleAbstractParameterListBox<Battle> battle;
	private HashMap<Paragraph, ParagraphWidget> prevs = new HashMap<Paragraph, ParagraphWidget>();
	private HashMap<Paragraph, ParagraphWidget> nexts = new HashMap<Paragraph, ParagraphWidget>();
//	private boolean active;
	private TabPanel tabPanel;
	private int currentWidget;
	private Image addParameterChanges;
	private SimpleAbstractParameterListBox<Parameter> parameterSelector;
	private ScrollContainer updates;
	private VerticalPanel updatesPanel;
	private MainTab mainTab;
	private SecondTab secondTab;
	private SimpleAbstractParameterListBox<Modificator> modificatorSelector;
	private SimpleAbstractParameterListBox<Alchemy> alchemySelector;
	private Image addModificator;
	private Image clearModificator;
	private Image enableAlchemy;
	private Image disableAlchemy;
	private ImagesTab imagesTab;
	
	public void activate() {
		if (activationNeed) {
			activationNeed = false;
			onResize();
		}
//		active = true;
	}
	public void deactivate() {
//		active = false;
	}
	
	public void activateLater() {
		activationNeed = true;
	}
	
	public void open(Paragraph loc) {
		paragraph = loc;
		name.setText(paragraph.getName());
		locationText.setText(paragraph.getDescription());
		status.setSelectedIndex(paragraph.getStatus());
		getObjects.setSelectedObjects(paragraph.getGotObjects());
		lostObjects.setSelectedObjects(paragraph.getLostObjects());
		ArrayList<ParagraphConnection> connections = model.getAllParagraphConnections(paragraph);
		prevParagraphs.clear();
		nextParagraphs.clear();
		prevs.clear();
		nexts.clear();
		for (int i = 0; i < connections.size(); i++) {
			Paragraph input=null;
			Paragraph output=null;
			ParagraphConnection connection = connections.get(i);
			if (connection.getTo()==paragraph) {
				//to here
				input = connection.getFrom();
				if (connection.isBothDirections()) {
					output = connection.getFrom();
				}
			} else {
				//from here
				output = connection.getTo();
				if (connection.isBothDirections()) {
					input = connection.getTo();
				}
			}
			if (input != null) {
				ParagraphWidget panel = new ParagraphWidget(input,connection,true);
				prevs.put(input, panel);
				prevParagraphs.add(panel);
				prevParagraphs.setCellHeight(panel,"100%");
			}
			if (output != null) {
				ParagraphWidget panel = new ParagraphWidget(output,connection,false);
				nexts.put(output, panel);
				nextParagraphs.add(panel);
				nextParagraphs.setCellHeight(panel,"100%");
			}
		}
		if (connections.size()>1) {
			//adjust size of prev. paragraphs
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					ajustSize();
				}

			});
		}
		battle.setSelectedParameter(paragraph.getBattle());
		enemies.setSelectedObjects(paragraph.getEnemies());
		enemies.setFightTogether(paragraph.isFightTogether());
		tabPanel.selectTab(0);
		
		while (updatesPanel.getWidgetCount()>1) {
			updatesPanel.remove(0);
		}
		if (paragraph.getChangeModificators() != null) {
			for (Modificator modificator : paragraph.getChangeModificators().keySet()) {
				Boolean value = paragraph.getChangeModificators().get(modificator);
				new ChangeModificatorWidget(modificator,value);
			}
		}
		if (paragraph.getChangeParameters()!=null) {
			for (Parameter parameter : paragraph.getChangeParameters().keySet()) {
				ParametersCalculation calculation = paragraph.getChangeParameters().get(parameter);
				new ChangeParameterWidget(parameter,calculation);
			}
		}
		if (paragraph.getAlchemy() != null) {
			for (Alchemy alchemy : paragraph.getAlchemy().keySet()) {
				Boolean value = paragraph.getAlchemy().get(alchemy);
				new AlchemyWidget(alchemy,value);
			}
		}
		imagesTab.reloadContent();
		mainTab.applyType();
	}

	public void ajustSize() {
		int l = prevParagraphs.getWidgetCount();
		for (int i = 0; i < l; i++) {
			ParagraphWidget paragraphWidget = (ParagraphWidget)prevParagraphs.getWidget(i);
			int h = paragraphWidget.getTextHight();
			if (paragraphWidget.getOffsetWidth()<h) {
				prevParagraphs.setCellWidth(paragraphWidget, String.valueOf(h)+"px");
			}
		}
		l = nextParagraphs.getWidgetCount();
		for (int i = 0; i < l; i++) {
			ParagraphWidget paragraphWidget = (ParagraphWidget)nextParagraphs.getWidget(i);
			int h = paragraphWidget.getTextHight();
			if (paragraphWidget.getOffsetWidth()<h) {
				nextParagraphs.setCellWidth(paragraphWidget, String.valueOf(h)+"px");
			}
		}
	}
	
	public class MainTab extends VerticalPanel implements EditorTab {
		
		private static final String STYLE_TYPE_SELECTED = "import_par_type_sel";
		private static final String STYLE_TYPE_UNSELECTED = "import_par_type_unsel";

		private Image addInConnection;
		private Image addOutConnection;
		private Image normal;
		private Image home;
		private Image success;
		private Image fail;
		private Image addParagraph;
		private VerticalPanel toolbar;
		private ResizeListener resizeListener;
		private Image commercial;

		public MainTab() {
			setSize("100%", "100%");
			resizeListener = new ResizeListener() {
				public void onResize(Widget panel) {
					ParagraphEditor.this.onResize();
					int h = splitPanel2.getSplitPosition();
					int h2 = toolbar.getOffsetHeight();
					if (h>0 && h2>0 && h2>h) {
						splitPanel2.setSplitPosition(String.valueOf(h2)+"px");
					}
				}
			};
			
			//prev. locations
			splitPanel = new TrueVerticalSplitPanel(true,false) {

				protected void onActivate() {
					initPanel2();
				}
				
			};
			splitPanel.setSplitPosition("33%");
			splitPanel.setBottomWidget(new HTML("&nbsp;"));
			splitPanel.addResizeListener(resizeListener);
			prevParagraphs = new HorizontalPanel();
			prevParagraphs.setHeight("100%");
			prevParagraphs.setSpacing(3);
			splitPanel.setTopWidget(prevParagraphs);

			add(splitPanel);
			setCellHeight(splitPanel,"99%");
			setCellWidth(splitPanel,"100%");
		}
		
		private void initPanel2() {
			splitPanel2 = new TrueVerticalSplitPanel(false,true);
			splitPanel2.addResizeListener(resizeListener);
			
			ClickHandler handler = new ClickHandler(){

				public void onClick(ClickEvent event) {
					if (event.getSource()==addOutConnection) {
						if (model.getCurrentParagraph() == paragraph) {
							Window.alert(appConstants.paragraphsTheSame());
						} else {
							Paragraph par = model.getCurrentParagraph();
							addParagraph(par,true,false);
						}
					} else if (event.getSource()==addInConnection) {
						if (model.getCurrentParagraph() == paragraph) {
							Window.alert(appConstants.paragraphsTheSame());
						} else {
							Paragraph par = model.getCurrentParagraph();
							addParagraph(par,true,true);
						}
					} else if (event.getSource()==normal) {
						model.makeParagraphAsNormal(paragraph);
						applyType();
					} else if (event.getSource()==home) {
						model.makeParagraphAsStart(paragraph);
						applyType();
					} else if (event.getSource()==success) {
						model.makeParagraphAsSuccess(paragraph);
						applyType();
					} else if (event.getSource()==fail) {
						model.makeParagraphAsFail(paragraph);
						applyType();
					} else if (event.getSource()==commercial) {
						if (paragraph.getType()==Paragraph.TYPE_START) {
							Window.alert(appConstants.importSelectOtherAsStart());
						} else {
							paragraph.setType(Paragraph.TYPE_COMMERCIAL);
						}
						applyType();
					} else if (event.getSource()==addParagraph) {
						//create a new paragraph and add it to the current
						Paragraph newPar = model.addNewParagraph(null);
						if (paragraph.getX()>0 && paragraph.getY()>0) {
							newPar.setX(paragraph.getX()+40);
							newPar.setY(paragraph.getY()+40);
						}
						addParagraph(newPar, false,false);
					}
					
				}

			};
			//description of paragraph
			HorizontalPanel panel = new HorizontalPanel();
			panel.setSize("100%", "100%");
			toolbar = new VerticalPanel();
			toolbar.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			toolbar.setSize("100%", "100%");
			
			addInConnection = new Image(Images.UP_GREEN);
			addInConnection.addStyleName(Styles.CLICKABLE);
			addInConnection.setTitle(appConstants.importAddInConnection());
			addInConnection.addClickHandler(handler);
			toolbar.add(addInConnection);
			
			//type of paragraph
			normal = new Image(Images.LOCATION_NORMAL);
			normal.setStyleName(STYLE_TYPE_SELECTED);
			normal.addClickHandler(handler);
			normal.setTitle(appConstants.importMakeNormal());
			toolbar.add(normal);
			
			home = new Image(Images.LOCATION_START);
			home.setStyleName(STYLE_TYPE_UNSELECTED);
			home.addClickHandler(handler);
			home.setTitle(appConstants.importMakeStart());
			toolbar.add(home);
			
			success = new Image(Images.LOCATION_SUCCESS);
			success.setStyleName(STYLE_TYPE_UNSELECTED);
			success.addClickHandler(handler);
			success.setTitle(appConstants.importMakeSuccess());
			toolbar.add(success);
			
			fail = new Image(Images.LOCATION_FAIL);
			fail.setStyleName(STYLE_TYPE_UNSELECTED);
			fail.addClickHandler(handler);
			fail.setTitle(appConstants.importMakeFail());
			toolbar.add(fail);
			
			commercial = new Image(Images.COMMERCIAL);
			commercial.setStyleName(STYLE_TYPE_UNSELECTED);
			commercial.addClickHandler(handler);
			commercial.setTitle(appConstants.importMakeCommecial());
			toolbar.add(commercial);
			
			addParagraph = new Image(Images.ADD_CONNECTION);
			addParagraph.setStyleName(STYLE_TYPE_UNSELECTED);
			addParagraph.addClickHandler(handler);
			addParagraph.setTitle(appConstants.createParagraphAndConnection());
			toolbar.add(addParagraph);
			
			addOutConnection = new Image(Images.DOWN_GREEN);
			addOutConnection.addStyleName(Styles.CLICKABLE);
			addOutConnection.setTitle(appConstants.importAddOutConnection());
			addOutConnection.addClickHandler(handler);
			toolbar.add(addOutConnection);
			
			BlurHandler blurHandler = new BlurHandler() {
				public void onBlur(BlurEvent event) {
					updateLocation(event.getSource());
				}
			};
			
			panel.add(toolbar);
			panel.setCellHeight(toolbar,"100%");
			panel.setCellWidth(toolbar,"1%");
			locationText = new TextArea();
			locationText.addBlurHandler(blurHandler);
			locationText.setSize("100%", "100%");
			panel.add(locationText);
			panel.setCellHeight(locationText,"100%");
			panel.setCellWidth(locationText,"99%");
			splitPanel2.setTopWidget(panel);

			nextParagraphs = new HorizontalPanel();
			nextParagraphs.setHeight("100%");
			nextParagraphs.setSpacing(3);
			splitPanel2.setBottomWidget(nextParagraphs);
			splitPanel2.setSplitPosition("50%");
			splitPanel.setBottomWidget(splitPanel2);
			open(paragraph);
		}
		public void activate() {
		}

		public void close() {
		}

		public void deactivate() {
		}
		
		private void applyType() {
			normal.setStyleName(STYLE_TYPE_UNSELECTED);
			home.setStyleName(STYLE_TYPE_UNSELECTED);
			success.setStyleName(STYLE_TYPE_UNSELECTED);
			fail.setStyleName(STYLE_TYPE_UNSELECTED);
			commercial.setStyleName(STYLE_TYPE_UNSELECTED);
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
			case Paragraph.TYPE_COMMERCIAL:
				commercial.setStyleName(STYLE_TYPE_SELECTED);
				break;
			default:
				//normal
				normal.setStyleName(STYLE_TYPE_SELECTED);
			}
		}
		
	}
	
	private void addParagraph(Paragraph par,boolean existed,boolean reverse) {
		if (existed) {
			for (ParagraphConnection connection : model.getParagraphConnections()) {
				if ((connection.getFrom()==paragraph && connection.getTo()==par) ||
					(connection.getTo()==paragraph && connection.getFrom()==par)){
					model.selectParagraphConnection(connection, connectionListener);
					Window.alert(appConstants.paragraphsAlreadyConnected());
					return;
				}
			}
		}
		ParagraphConnection connection = new ParagraphConnection();
		if (model.getSettings().isOneWayConnectionsOnly()) {
			connection.setBothDirections(false);
		} else {
			connection.setBothDirections(!Window.confirm(appConstants.paragraphConnectionCreateTwoWays()));
		}
		if (reverse) {
			connection.setFrom(par);
			connection.setTo(paragraph);
		} else {
			connection.setFrom(paragraph);
			connection.setTo(par);
		}
		model.addParagraphConnection(connection,null);
		if (existed) {
			model.selectParagraphConnection(connection,null);
		} else {
			model.selectParagraph(par,null);
		}
	}
	
	public class SecondTab extends VerticalPanel implements EditorTab {
		private boolean resize=true;
		private boolean active;
		
		public SecondTab() {
			Grid grid = new Grid(2,5);
			grid.setSize("100%", "100%");
			grid.setCellSpacing(3);
			grid.getColumnFormatter().setWidth(0, "1%");
			grid.getColumnFormatter().setWidth(1, "49%");
			grid.getColumnFormatter().setWidth(2, "1%");
			grid.getColumnFormatter().setWidth(3, "1%");
			grid.getColumnFormatter().setWidth(4, "49%");
			
			grid.setWidget(0, 0, new Label(appConstants.paragraphFoundItems(),false));
			getObjects = new ObjectsList(model);
			getObjects.addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					paragraph.setGotObjects(getObjects.getSelectedObjects());
					model.updateParagraph(paragraph, paragraphListener);
					onResize();
				}
			});
			grid.setWidget(0, 1, getObjects);
			
			grid.setWidget(0, 3, new Label(appConstants.paragraphLostItems(),false));
			lostObjects = new ObjectsList(model);
			lostObjects.addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					paragraph.setLostObjects(lostObjects.getSelectedObjects());
					model.updateParagraph(paragraph, paragraphListener);
					onResize();
				}
			});
			grid.setWidget(0, 4, lostObjects);
			
			ChangeHandler changeListener = new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					updateLocation(event.getSource());
				}
			};
			
			grid.setWidget(1, 0, new Label(appConstants.paragraphBattle(),false));
			battle = new SimpleAbstractParameterListBox<Battle>(Battle.class,model,true);
			battle.addChangeHandler(changeListener);
			grid.setWidget(1, 1, battle);
			
			grid.setWidget(1, 3, new Label(appConstants.paragraphBattleWithNPC(),false));
			enemies = new NPCList(model);
			enemies.addChangeHandler(new ChangeHandler(){
				public void onChange(ChangeEvent event) {
					updateLocation(enemies);
					onResize();
				}
			});
			grid.setWidget(1, 4, enemies);
			
			add(grid);
			setCellHeight(grid,"1%");
			setCellWidth(grid,"100%");

			
			ClickHandler clickHandler = new ClickHandler() {

				public void onClick(ClickEvent event) {
					if (event.getSource()==addModificator) {
						Modificator modificator = modificatorSelector.getSelectedParameter();
						if (modificator != null && paragraph.hasChangeModificator(modificator)==false) {
							//set this modificator
							new ChangeModificatorWidget(modificator,true);
							paragraph.addChangeModificator(modificator, true);
							updateLocation(addModificator);
						}
					} else if (event.getSource()==clearModificator) {
						Modificator modificator = modificatorSelector.getSelectedParameter();
						if (modificator != null && paragraph.hasChangeModificator(modificator)==false) {
							//clear this modificator
							new ChangeModificatorWidget(modificator,false);
							paragraph.addChangeModificator(modificator, false);
							updateLocation(clearModificator);
						}
						
					} else if (event.getSource()==enableAlchemy) {
						Alchemy alchemy = alchemySelector.getSelectedParameter();
						if (alchemy != null && paragraph.hasAlchemy(alchemy)==false) {
							if (alchemy.isOnDemand()) {
								//add this alchemy
								new AlchemyWidget(alchemy,true);
								paragraph.addAlchemy(alchemy, true);
								updateLocation(enableAlchemy);
							} else {
								Window.alert(appConstants.ParagraphEditorNonDemandAlchemy1());
							}
						}
						
					} else if (event.getSource()==disableAlchemy) {
						Alchemy alchemy = alchemySelector.getSelectedParameter();
						if (alchemy != null && paragraph.hasAlchemy(alchemy)==false) {
							//add this alchemy
							if (alchemy.isOnDemand()) {
								Window.alert(appConstants.ParagraphEditorNonDemandAlchemy2());
							} else {
								new AlchemyWidget(alchemy,false);
								paragraph.addAlchemy(alchemy, false);
								updateLocation(disableAlchemy);
							}
						}
						
					} else if (event.getSource()==addParameterChanges) {
						Parameter parameter = parameterSelector.getSelectedParameter();
						if (parameter != null && paragraph.hasChangeParameter(parameter)==false) {
							//add changes of this parameter
							ParametersCalculation calculation = new ParametersCalculation();
							calculation.getParameters().put(parameter, 1);
							new ChangeParameterWidget(parameter,calculation);
							paragraph.addChangeParameter(parameter, calculation);
							updateLocation(addParameterChanges);
						}
					}
				}
			};
			
			//add parameter
			HorizontalPanel horizontalPanel = new HorizontalPanel();
			horizontalPanel.setSize("100%", "100%");
			horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			horizontalPanel.setSpacing(3);
			Label label = new Label(appConstants.paragraphEditorChangeParameters(),false);
			horizontalPanel.add(label);
			horizontalPanel.setCellWidth(label, "1%");
			parameterSelector = new SimpleAbstractParameterListBox<Parameter>(Parameter.class,model, false);
			parameterSelector.setTitle(appConstants.ParagraphEditorSelectParameterTitle());
			parameterSelector.addClickHandler(clickHandler);
			horizontalPanel.add(parameterSelector);
			horizontalPanel.setCellWidth(parameterSelector, "1%");
			addParameterChanges = new Image(Images.ADD_CONNECTION);
			addParameterChanges.setStyleName(Styles.CLICKABLE);
			addParameterChanges.setTitle(appConstants.ParagraphEditorAddParameterTitle());
			addParameterChanges.addClickHandler(clickHandler);
			horizontalPanel.add(addParameterChanges);
			horizontalPanel.setCellWidth(addParameterChanges, "99%");
			add(horizontalPanel);
			setCellHeight(horizontalPanel,"1%");
			setCellWidth(horizontalPanel,"100%");
			
			//add Modificator
			horizontalPanel = new HorizontalPanel();
			horizontalPanel.setSize("100%", "100%");
			horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			horizontalPanel.setSpacing(3);
			label = new Label(appConstants.paragraphEditorChangeModificator(),false);
			horizontalPanel.add(label);
			horizontalPanel.setCellWidth(label, "1%");
			modificatorSelector = new SimpleAbstractParameterListBox<Modificator>(Modificator.class,model);
			modificatorSelector.setTitle(appConstants.ParagraphEditorSelectParameterTitle());
			horizontalPanel.add(modificatorSelector);
			horizontalPanel.setCellWidth(modificatorSelector, "1%");
			addModificator = new Image(Images.ADD_CONNECTION);
			addModificator.setStyleName(Styles.CLICKABLE);
			addModificator.setTitle(appConstants.ParagraphEditorSetModificator());
			addModificator.addClickHandler(clickHandler);
			horizontalPanel.add(addModificator);
			horizontalPanel.setCellWidth(addModificator, "1%");
			clearModificator = new Image(Images.MINUS);
			clearModificator.setStyleName(Styles.CLICKABLE);
			clearModificator.setTitle(appConstants.ParagraphEditorClearModificator());
			clearModificator.addClickHandler(clickHandler);
			horizontalPanel.add(clearModificator);
			horizontalPanel.setCellWidth(clearModificator, "99%");
			add(horizontalPanel);
			setCellHeight(horizontalPanel,"1%");
			setCellWidth(horizontalPanel,"100%");
			
			//add Alchemy
			horizontalPanel = new HorizontalPanel();
			horizontalPanel.setSize("100%", "100%");
			horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			horizontalPanel.setSpacing(3);
			label = new Label(appConstants.paragraphEditorAlchemy(),false);
			horizontalPanel.add(label);
			horizontalPanel.setCellWidth(label, "1%");
			alchemySelector = new SimpleAbstractParameterListBox<Alchemy>(Alchemy.class,model);
			alchemySelector.setTitle(appConstants.ParagraphEditorSelectAlchemyTitle());
			horizontalPanel.add(alchemySelector);
			horizontalPanel.setCellWidth(alchemySelector, "1%");
			enableAlchemy = new Image(Images.ADD_CONNECTION);
			enableAlchemy.setStyleName(Styles.CLICKABLE);
			enableAlchemy.setTitle(appConstants.ParagraphEditorEnableAlchemy());
			enableAlchemy.addClickHandler(clickHandler);
			horizontalPanel.add(enableAlchemy);
			horizontalPanel.setCellWidth(enableAlchemy, "1%");
			disableAlchemy = new Image(Images.MINUS);
			disableAlchemy.setStyleName(Styles.CLICKABLE);
			disableAlchemy.setTitle(appConstants.ParagraphEditorDisableAlchemy());
			disableAlchemy.addClickHandler(clickHandler);
			horizontalPanel.add(disableAlchemy);
			horizontalPanel.setCellWidth(disableAlchemy, "99%");
			add(horizontalPanel);
			setCellHeight(horizontalPanel,"1%");
			setCellWidth(horizontalPanel,"100%");
			
			updates = new ScrollContainer();
			updatesPanel = new VerticalPanel();
			updatesPanel.setSize("100%", "100%");
			updates.setScrollWidget(updatesPanel);
			HTML filler = new HTML("&nbsp;");
			filler.setStyleName(Styles.FILLER);
			updatesPanel.add(filler);
			updatesPanel.setCellHeight(filler,"99%");
			add(updates);
			setCellHeight(updates,"99%");
			setCellWidth(updates,"100%");
		}
		
		public void activate() {
			if (resize) {
				onResize();
			}
			active = true;
		}

		public void close() {
		}

		public void deactivate() {
			active = false;
		}

		public void onResize() {
			updates.resetHeight();
			resize = false;
		}
		
	}
	
	public class ChangeModificatorWidget extends HorizontalPanel implements ClickHandler{
		private Modificator modificator;
		private Label label;
		public ChangeModificatorWidget(Modificator modificator, boolean set) {
			setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			Image image = new Image(Images.REMOVE);
			image.setStyleName(Styles.CLICKABLE);
			image.addClickHandler(this);
			image.setTitle(appConstants.paragraphRemoveModificator());
			add(image);
			label = new Label();
			label.setWordWrap(false);
			add(label);
			int pos = updatesPanel.getWidgetCount()-1;
			updatesPanel.insert(this,pos);
			updatesPanel.setCellHeight(this,"1%");
			apply(modificator, set);
		}
		
		public void apply(Modificator modificator,boolean set) {
			this.modificator = modificator;
			if (set) {
				label.setText(appMessages.paragraphEditModificatorSet(modificator.getName()));
			} else {
				label.setText(appMessages.paragraphEditModificatorClear(modificator.getName()));
			}
			label.setTitle(modificator.getDescription());
		}
		
		public void onClick(ClickEvent event) {
			//remove
			updatesPanel.remove(this);
			paragraph.getChangeModificators().remove(modificator);
		}
		
	}
	
	public class AlchemyWidget extends HorizontalPanel implements ClickHandler{
		private Alchemy modificator;
		private Label label;
		public AlchemyWidget(Alchemy modificator, boolean set) {
			setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			Image image = new Image(Images.REMOVE);
			image.setStyleName(Styles.CLICKABLE);
			image.addClickHandler(this);
			image.setTitle(appConstants.paragraphRemoveModificator());
			add(image);
			label = new Label();
			label.setWordWrap(false);
			add(label);
			int pos = updatesPanel.getWidgetCount()-1;
			updatesPanel.insert(this,pos);
			updatesPanel.setCellHeight(this,"1%");
			apply(modificator, set);
		}
		
		public void apply(Alchemy modificator,boolean set) {
			this.modificator = modificator;
			if (set) {
				label.setText(appMessages.paragraphEditAlchemyAvailable(modificator.getName()));
			} else {
				label.setText(appMessages.paragraphEditAlchemyDisabled(modificator.getName()));
			}
			label.setTitle(modificator.getDescription());
		}
		
		public void onClick(ClickEvent event) {
			//remove
			updatesPanel.remove(this);
			paragraph.getAlchemy().remove(modificator);
		}
		
	}
	public class ChangeParameterWidget extends HorizontalPanel implements ClickHandler,ChangeHandler{
		
		private ParametersCalculationWidget calculationWidget;
		private Parameter parameter;
		
		public ChangeParameterWidget(Parameter parameter,ParametersCalculation calculation) {
			setWidth("100%");
			setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			Image image = new Image(Images.REMOVE);
			image.setStyleName(Styles.CLICKABLE);
			image.addClickHandler(this);
			image.setTitle(appConstants.removeParameterCalculationFromParagraph());
			add(image);
			setCellWidth(image, "1%");
			this.parameter = parameter;
			calculationWidget = new ParametersCalculationWidget(parameter.getName(),model,false,true);
			calculationWidget.addStyleName(Styles.BORDER);
			calculationWidget.addChangeHandler(this);
			calculationWidget.apply(calculation);
			add(calculationWidget);
			setCellWidth(calculationWidget, "99%");
			int pos = updatesPanel.getWidgetCount()-1;
			updatesPanel.insert(this,pos);
			updatesPanel.setCellHeight(this,"1%");
		}

		public void onClick(ClickEvent event) {
			if (Window.confirm(appConstants.removeParameterCalculationFromParagraph())){
				//remove
				updatesPanel.remove(this);
				paragraph.getChangeParameters().remove(parameter);
			}
		}

		public void onChange(ChangeEvent event) {
			updateLocation(addParameterChanges);
//			paragraph.getChangeParameters().put(parameter, calculationWidget.get)
		}
		
	}
	
	public ParagraphEditor(Model mod, Paragraph loc) {
		model=mod;
		paragraph = loc;
		setSize("100%", "100%");
		setStyleName("location_editor_panel");
		Grid grid = new Grid(1,5);
		grid.setSize("100%", "100%");
		grid.setCellSpacing(3);
		grid.getColumnFormatter().setWidth(0, "1%");
		grid.getColumnFormatter().setWidth(1, "49%");
		grid.getColumnFormatter().setWidth(2, "1%");
		grid.getColumnFormatter().setWidth(3, "1%");
		grid.getColumnFormatter().setWidth(4, "49%");
		Label label = new Label(appConstants.paragraphName());
		grid.setWidget(0, 0, label);
		final ChangeHandler changeListener = new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateLocation(event.getSource());
			}
		};
		final BlurHandler blurHandler = new BlurHandler() {
			public void onBlur(BlurEvent event) {
				updateLocation(event.getSource());
			}
		};
		KeyPressHandler keyboardListener = new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode()==KeyCodes.KEY_ESCAPE) {
					if (event.getSource()==name) {
						name.setText(paragraph.getName());
					}
				} else if (event.getCharCode()==KeyCodes.KEY_ENTER) {
					updateLocation(event.getSource());
				}
			}

		};
		
		name = new TextBox();
		name.setWidth("100%");
		name.addBlurHandler(blurHandler);
		name.addKeyPressHandler(keyboardListener);
		grid.setWidget(0, 1, name);

		label = new Label(appConstants.paragraphStatus(),false);
		grid.setWidget(0, 3, label);
		status = new StatusPicker();
		status.addChangeHandler(changeListener);
		grid.setWidget(0, 4, status);
		
		add(grid);
		setCellHeight(grid,"1%");
		setCellWidth(grid,"100%");
		
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
		tabPanel.getDeckPanel().setSize("100%", "100%");
		
		mainTab = new MainTab();
		tabPanel.add(mainTab, appConstants.ParagraphsEditorMainTab());
		secondTab = new SecondTab();
		tabPanel.add(secondTab, appConstants.ParagraphsEditorSecondTab());
		imagesTab = new ImagesTab();
		tabPanel.add(imagesTab, appConstants.ParagraphsEditorImagesTab());
		
		model = mod;
		paragraphListener = new ParagraphListener() {
			public void addNewParagraph(Paragraph location) {
			}
			public void edit(Paragraph location) {
			}
			public void refreshAll() {
			}

			public void remove(Paragraph location) {
				if (location==ParagraphEditor.this.paragraph) {
					//ouw paragraph was removed....go to start
					open(model.getStartParagraph());
				}
			}

			public void select(Paragraph location) {
			}

			public void unselect(Paragraph location) {
			}

			public void update(Paragraph location) {
				if (location==ParagraphEditor.this.paragraph) {
					open(location);
				} else if (prevs.containsKey(location)) {
					ParagraphWidget widget = prevs.get(location);
					widget.apply(location);
				} else if (nexts.containsKey(location)) {
					ParagraphWidget widget = nexts.get(location);
					widget.apply(location);
				}
			}
			
		};
		contentListener = new ContentListener() {
			public void addNew(Picture picture) {
			}

			public void addNew(Sound sound) {
			}

			public void refreshAll() {
			}

			public void remove(Picture picture) {
				if (currentPicture==picture) {
					currentPicture=null;
				}
			}

			public void remove(Sound sound) {
				if (currentSound==sound) {
					currentSound=null;
				}
			}

			public void select(Sound sound) {
				currentSound = sound;
			}

			public void select(Picture picture) {
				currentPicture = picture;
			}

			public void unselect(Sound sound) {
			}

			public void unselect(Picture picture) {
			}

			public void update(Picture picture) {
				imagesTab.update(picture);
			}

			public void update(Sound sound) {
				imagesTab.update(sound);
			}

			public void showInfo(Picture picture) {
			}

			public void showInfo(Sound sound) {
			}
			
		};
		connectionListener =  new ParagraphConnectionListener() {

			public void refreshAll() {
			}

			public void remove(ParagraphConnection connection) {
				if (connection.getTo()==paragraph || connection.getFrom()==paragraph) {
					//refresh data
					open(paragraph);
				}
				
			}

			public void select(ParagraphConnection connection) {
			}

			public void unselect(ParagraphConnection connection) {
			}

			public void update(ParagraphConnection connection) {
				updateConnection(connection);
			}

			private void updateConnection(ParagraphConnection connection) {
				if (connection.getTo()==paragraph) {
					ParagraphWidget widget = prevs.get(connection.getFrom());
					if (widget != null) {
						widget.apply(connection.getFrom(), connection);
					}
					widget = nexts.get(connection.getFrom());
					if (widget != null) {
						widget.apply(connection.getFrom(), connection);
					}
				} else if (connection.getFrom()==paragraph) {
					//two way link
					ParagraphWidget widget = prevs.get(connection.getTo());
					if (widget != null) {
						widget.apply(connection.getTo(), connection);
					}
					widget = nexts.get(connection.getTo());
					if (widget != null) {
						widget.apply(connection.getTo(), connection);
					}
				}
			}

			public void addNew(ParagraphConnection connection) {
				if (connection.getTo()==paragraph || connection.getFrom()==paragraph) {
					//refresh data
					open(paragraph);
				}
			}
			
		};
		model.addParagraphListener(paragraphListener);
		model.addContentListener(contentListener);
		model.addParagraphConnectionListener(connectionListener);
		
		add(tabPanel);
		setCellHeight(tabPanel, "100%");
		setCellWidth(tabPanel, "100%");
		tabPanel.selectTab(0);
		splitPanel.activate();
	}

	public void onResize() {
		if (secondTab.active) {
			secondTab.onResize();
		} else {
			secondTab.resize = true;
		}
	}
	
	private void updateLocation(Object sender) {
		if (sender==name) {
			paragraph.setName(name.getText().trim());
		} else if (sender==locationText) {
			paragraph.setDescription(locationText.getText().trim());
		} else if (sender==status) {
			paragraph.setStatus(status.getSelectedIndex());
		} else if (sender==enemies) {
			paragraph.setEnemies(enemies.getSelectedNPCs());
			paragraph.setFightTogether(enemies.isFightTogether());
		} else if (sender==battle) {
			paragraph.setBattle(battle.getSelectedParameter());
		}
		model.updateParagraph(paragraph, paragraphListener);
	}
	
	public Paragraph getParagraph() {
		return paragraph;
	}
	
	public class ParagraphWidget extends VerticalPanel implements ClickHandler{
		private Paragraph paragraph;
		private ParagraphConnection connection;
		private Label name;
		private Label connectionName;
		private Label descr;
		private boolean inputConnection;
		public ParagraphWidget(Paragraph input, ParagraphConnection connection, boolean inputConnection) {
			setSize("100%", "100%");
			this.connection = connection;
			this.inputConnection = inputConnection;
			name = new Label();
			name.setWordWrap(false);
			switch (input.getStatus()) {
			case Model.STATUS_DRAFT:
				name.setStyleName(ParagraphsListView.STYLE_DRAFT);
				break;
			case Model.STATUS_FINAL:
				name.setStyleName(ParagraphsListView.STYLE_FINAL);
				break;
			default:
				name.setStyleName(ParagraphsListView.STYLE_PROPOSAL);
			} 
			name.addStyleName(Styles.CLICKABLE);
			name.addStyleName("paragraph_edit_title");
			name.addStyleName(Styles.BOLD);
			name.setTitle(appConstants.ParagraphEditorClick2Paragraph());
			name.addClickHandler(this);
			
			add(name);
			setCellWidth(name,"100%");
			setCellHeight(name,"1%");
			if (inputConnection==false && model.getSettings().isShowConnectionNames()) {
				addConnectionName();
			}
			descr = new Label();
//			descr.setSize("100%", "100%");
			add(descr);
			setCellWidth(descr,"100%");
			setCellHeight(descr,"99%");
			if (inputConnection && model.getSettings().isShowConnectionNames()) {
				addConnectionName();
			}
			apply(input,connection);
		}
		
		private void addConnectionName() {
			connectionName = new Label();
			connectionName.setSize("100%", "100%");
			connectionName.addStyleName(Styles.CLICKABLE);
			connectionName.addStyleName(Styles.BORDER);
			name.setTitle(appConstants.ParagraphEditorClick2Connection());
			connectionName.addClickHandler(this);
			add(connectionName);
			setCellWidth(connectionName,"100%");
			setCellHeight(connectionName,"1%");
		}

		public int getTextHight() {
			return descr.getOffsetHeight();
		}
		
		public void apply(Paragraph input) {
			paragraph = input;
			StringBuilder builder = new StringBuilder();
			if (inputConnection==false) { 
				if (model.getSettings().isShowConnectionsIDs()) {
					if (connection.getFrom()==ParagraphEditor.this.paragraph) {
						builder.append('<');
						builder.append(connection.getToId());
						builder.append("> ");
					} else if (connection.isBothDirections()){
						builder.append('<');
						builder.append(connection.getFromId());
						builder.append("> ");
					}
				}
			}
			if (connectionName != null) {
				if (connection.isBothDirections()) {
					connectionName.setVisible(true);
					if (connection.getFrom()==paragraph) {
						//normal order
						if (inputConnection) {
							connectionName.setText(connection.getNameFrom());
						} else {
							connectionName.setText(connection.getNameTo());
						}
					} else {
						//reverse order
						if (inputConnection) {
							connectionName.setText(connection.getNameTo());
						} else {
							connectionName.setText(connection.getNameFrom());
						}
					}
				} else if (inputConnection) {
					connectionName.setVisible(false);
				} else {
					connectionName.setText(connection.getNameFrom());
				}
			}
			if (model.getSettings().isShowParagraphNumbers() && paragraph.getNumber() != 0) {
				builder.append(paragraph.getNumber());
				builder.append(". ");
			} else {
			}
			builder.append(paragraph.getName());
			name.setText(builder.toString());
			String dsr = input.getDescription();
			if (dsr==null) {
				dsr="";
			}
			descr.setText(dsr);
		}

		public void apply(Paragraph input,ParagraphConnection connection) {
			this.connection = connection;
			apply(input);
			descr.setStyleName("paragraph_edit_text");
			if (connection.isBothDirections()) {
				//two way
				addStyleName("prev_two_way");
			} else if (connection.isConditional()) {
				//one way
				addStyleName("prev_condition");
			} else {
				//conditional
				addStyleName("prev_one_way");
			}
		}

		public void onClick(ClickEvent event) {
			if (event.getSource()==name) {
				model.selectParagraph(paragraph, paragraphListener);
				open(paragraph);
			} else if (event.getSource()==connectionName) {
				model.selectParagraphConnection(connection,connectionListener);
			}
		}

	}
	
	
	public void close() {
		model.removeParagraphListener(paragraphListener);
		model.removeContentListener(contentListener);
		model.removeParagraphConnectionListener(connectionListener);
	}
	
	public class ImagesTab extends VerticalPanel implements EditorTab {
		private static final int TOP_IMAGES = 0;
		private static final int BOTTOM_IMAGES = 1;
		private static final int BACK_IMAGES = 2;
		private static final int SPRITE = 3;
		
		private boolean resize=true;
		private boolean active;
		private ContentButton<Sound> addSound;
		private ContentButton<Sound> addMusic;
		private ContentButton<Picture> addBottomImage;
		private ContentButton<Picture> addTopImage;
		private ContentButton<Picture> addBackground;
		private ContentButton<Picture> addSprite;
		
		private VerticalPanel contentSound;
		private VerticalPanel contentMusic;
		private VerticalPanel contentBottom;
		private VerticalPanel contentTop;
		private VerticalPanel contentBackground;
		private AbsolutePanel board;
		private VerticalPanel contentSprites;
		private Image backImage;
		private PickupDragController dragController;
		protected int boardWidth;
		protected int boardHeight;
		
		public ImagesTab() {
			//Content
			addTopImage = new ContentButton<Picture>(true) {
				public void addContent(Picture picture) {
					ArrayList<Picture> imgs = paragraph.getTopImages();
					if (imgs.contains(picture)==false) {
						PictureWidget widget = new PictureWidget(picture,TOP_IMAGES);
						widget.selector.setValue(imgs.isEmpty());
						imgs.add(picture);
						contentTop.add(widget);
						contentTop.setCellHeight(widget, "1%");
						contentTop.setCellWidth(widget, "100%");
						if (widget.selector.getValue()) {
							//update image
							updateTopImage(widget.picture);
							correctZOrder();
						}
						model.updateParagraph(paragraph, paragraphListener);
					}
				}
			};
			contentTop = new VerticalPanel();
			addContentPanel(appConstants.paragraphTopImage(),addTopImage,contentTop);
			
			addSprite = new ContentButton<Picture>(true) {
				public void addContent(Picture picture) {
					ArrayList<Sprite> imgs = paragraph.getSprites();
					Sprite sprite = new Sprite();
					sprite.setPicture(picture);
					imgs.add(sprite);
					PictureWidget widget = new PictureWidget(sprite);
					contentSprites.add(widget);
					contentSprites.setCellHeight(widget, "1%");
					contentSprites.setCellWidth(widget, "100%");
					addSprite(sprite);
					correctZOrder();
					model.updateParagraph(paragraph, paragraphListener);
				}
			};
			contentSprites = new VerticalPanel();
			addContentPanel(appConstants.paragraphSprites(),addSprite,contentSprites);
			
			board = new AbsolutePanel();
			board.setStyleName(Styles.BORDER);
			board.setSize("100%", "100%");
			add(board);
			setCellHeight(board, "99%");

			setCellWidth(board, "100%");
		    dragController = new PickupDragController(board, true);
		    dragController.addDragHandler(new DragHandler(){
				private int x0;
				private int y0;
				private int w;
				private int h;
				private ImageSprite image;
				private int x;
				private int y;
				public void onDragEnd(DragEndEvent event) {
					x = event.getContext().desiredDraggableX - x0;
					y = event.getContext().desiredDraggableY - y0;
					boolean correct=false;
					if (x>boardWidth-w) {
						x = boardWidth-w;
						correct = true;
					}
					
					if (y>boardHeight-h) {
						y = boardHeight-h;
						correct = true;
		    		}
					if (x<0) {
						x=0;
						correct = true;
					}
					if (y<0) {
						y=0;
						correct = true;
					}
					image = (ImageSprite)event.getSource();
					image.getSprite().setX(x);
					image.getSprite().setY(y);
					if (correct) {
						DeferredCommand.addCommand(new Command(){
							public void execute() {
								board.setWidgetPosition(image, x, y);
							}
						});
					}
					model.updateParagraph(paragraph, paragraphListener);
				}

				public void onDragStart(DragStartEvent event) {
					x0 = board.getAbsoluteLeft();
					y0 = board.getAbsoluteTop();
					ImageSprite image = (ImageSprite)event.getSource();
					w = image.getWidth();
					h = image.getHeight();
				}

				public void onPreviewDragEnd(DragEndEvent event)throws VetoDragException {
				}

				public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
				}
		    });
			
			addBottomImage = new ContentButton<Picture>(true) {
				public void addContent(Picture picture) {
					ArrayList<Picture> imgs = paragraph.getBottomImages();
					if (imgs.contains(picture)==false) {
						imgs.add(picture);
						PictureWidget widget = new PictureWidget(picture,BOTTOM_IMAGES);
						contentBottom.add(widget);
						contentBottom.setCellHeight(widget, "1%");
						contentBottom.setCellWidth(widget, "100%");
						model.updateParagraph(paragraph, paragraphListener);
					}
				}
			};
			contentBottom = new VerticalPanel();
			addContentPanel(appConstants.paragraphBottomImage(),addBottomImage,contentBottom);
			
			addBackground = new ContentButton<Picture>(true) {
				public void addContent(Picture picture) {
					ArrayList<Picture> imgs = paragraph.getBackgroundImages();
					if (imgs.contains(picture)==false) {
						imgs.add(picture);
						PictureWidget widget = new PictureWidget(picture,BACK_IMAGES);
						contentBackground.add(widget);
						contentBackground.setCellHeight(widget, "1%");
						contentBackground.setCellWidth(widget, "100%");
						model.updateParagraph(paragraph, paragraphListener);
					}
				}
			};
			contentBackground = new VerticalPanel();
			addContentPanel(appConstants.paragraphBackgroundImage(),addBackground,contentBackground);
			
			addSound = new ContentButton<Sound>(false) {
				public void addContent(Sound sound) {
					ArrayList<Sound> sounds = paragraph.getSounds();
					if (sounds.contains(sound)==false) {
						sounds.add(sound);
						SoundWidget widget = new SoundWidget(sound,false);
						contentSound.add(widget);
						contentSound.setCellHeight(widget, "1%");
						contentSound.setCellWidth(widget, "100%");
						model.updateParagraph(paragraph, paragraphListener);
					}
				}
			};
			contentSound = new VerticalPanel();
			addContentPanel(appConstants.paragraphSoundEffects(),addSound,contentSound);
			
			addMusic = new ContentButton<Sound>(false) {
				public void addContent(Sound sound) {
					ArrayList<Sound> sounds = paragraph.getBackgroundSounds();
					if (sounds.contains(sound)==false) {
						sounds.add(sound);
						SoundWidget widget = new SoundWidget(sound,true);
						contentMusic.add(widget);
						contentMusic.setCellHeight(widget, "1%");
						contentMusic.setCellWidth(widget, "100%");
						model.updateParagraph(paragraph, paragraphListener);
					}
				}
			};
			contentMusic = new VerticalPanel();
			addContentPanel(appConstants.paragraphBackgroundSound(),addMusic,contentMusic);
			
		}

		protected void addSprite(Sprite sprite) {
			ImageSprite image = new ImageSprite(sprite);
			board.add(image,sprite.getX(),sprite.getY());
			dragController.makeDraggable(image);
		}

		protected void updateTopImage(Picture picture) {
			if (backImage != null) {
				if (picture.getUrl().equals(backImage.getUrl())) {
					//the same
					return;
				} else {
					board.remove(backImage);
				}
			}
			backImage = new Image();
//			if (picture.getHeight()>0 && picture.getWidht()>0) {
//				//use it
//				backImage.setUrl(picture.getUrl());
//				backImage.setWidth(String.valueOf(picture.getWidht())+"px");
//				backImage.setHeight(String.valueOf(picture.getHeight())+"px");
//			} else {
				backImage.addErrorHandler(new ErrorHandler(){
					public void onError(ErrorEvent event) {
						Window.alert(appMessages.paragraphCannotLoadImage(backImage.getUrl()));
					}
				});
				backImage.addLoadHandler(new LoadHandler(){
					public void onLoad(LoadEvent event) {
						boardWidth = backImage.getWidth();
						boardHeight = backImage.getHeight();
					}
				});
				backImage.setUrl(picture.getUrl());
//			}
			board.add(backImage,0,0);
		}

		public void reloadContent() {
			updateContenPanel(contentTop,paragraph.getTopImages(),TOP_IMAGES);
			updateContenPanel(contentSprites,paragraph.getSprites());
			updateContenPanel(contentBottom,paragraph.getBottomImages(),BOTTOM_IMAGES);
			updateContenPanel(contentBackground,paragraph.getBackgroundImages(),BACK_IMAGES);
			updateContenPanel(contentSound,paragraph.getSounds(),false);
			updateContenPanel(contentMusic,paragraph.getBackgroundSounds(),true);
			//draw image
			board.clear();
			if (paragraph.hasTopImages()) {
				updateTopImage(paragraph.getTopImages().get(0));
				((PictureWidget)contentTop.getWidget(0)).selector.setValue(true);
				ArrayList<Sprite> list = paragraph.getSprites();
				for (Sprite sprite : list) {
					addSprite(sprite);
				}
				correctZOrder();
			}
		}

		private void correctZOrder() {
			if (backImage != null) {
				backImage.getElement().getStyle().setZIndex(1);
			}
			ArrayList<Sprite> list = paragraph.getSprites();
			outter:
			for (int i = 0; i < board.getWidgetCount(); i++) {
				Widget widget = board.getWidget(i);
				if (widget instanceof ImageSprite) {
					ImageSprite image = (ImageSprite) widget;
					int z=1;
					for (Sprite sprite : list) {
						z++;
						if (sprite==image.sprite) {
							image.getElement().getStyle().setZIndex(z);
							continue outter;
						}
					}
					//error?
					image.getElement().getStyle().setZIndex(z);
				}
			}
		}

		private void updateContenPanel(VerticalPanel panel, ArrayList<Sound> sounds, boolean back) {
			if (panel.getWidgetCount()>0) {
				panel.clear();
			}
			for (Sound sound : sounds) {
				SoundWidget widget = new SoundWidget(sound,back);
				panel.add(widget);
				panel.setCellWidth(widget, "100%");
				panel.setCellHeight(widget, "1%");
			}
		}

		private void updateContenPanel(VerticalPanel panel, ArrayList<Sprite> sprites) {
			if (panel.getWidgetCount()>0) {
				panel.clear();
			}
			for (Sprite sprite : sprites) {
				PictureWidget widget = new PictureWidget(sprite);
				panel.add(widget);
				panel.setCellWidth(widget, "100%");
				panel.setCellHeight(widget, "1%");
			}
		}

		private void updateContenPanel(VerticalPanel panel, ArrayList<Picture> images, int type) {
			if (panel.getWidgetCount()>0) {
				panel.clear();
			}
			for (Picture picture : images) {
				PictureWidget widget = new PictureWidget(picture,type);
				panel.add(widget);
				panel.setCellWidth(widget, "100%");
				panel.setCellHeight(widget, "1%");
			}
		}

		public void animateSprite(Sprite sprite) {
			for (int i = 0; i < board.getWidgetCount(); i++) {
				Widget widget = board.getWidget(i);
				if (widget instanceof ImageSprite) {
					final ImageSprite image = (ImageSprite) widget;
					if (image.sprite==sprite) {
						//animate it
						image.getElement().getStyle().setDisplay(Display.NONE);
						new Timer() {
							int i=5;
							@Override
							public void run() {
								Display display = Display.INLINE_BLOCK;
								if (i>0) {
									if (i%2==0) {
										display = Display.NONE;
									}
									i--;
								} else {
									cancel();
								}
								image.getElement().getStyle().setDisplay(display);
							}
						}.scheduleRepeating(300);
						return;
					}
					
				}
			}
		}
		
		private void addContentPanel(String title, ContentButton button, VerticalPanel panel) {
			HorizontalPanel horizontalPanel = new HorizontalPanel();
			horizontalPanel.setSize("100%", "100%");
			horizontalPanel.setStyleName(Styles.BORDER);
			horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			horizontalPanel.setSpacing(3);
			horizontalPanel.add(button);
			horizontalPanel.setCellWidth(button, "1%");
			Label label = new Label(title,false);
			horizontalPanel.add(label);
			horizontalPanel.setCellWidth(label, "1%");
			horizontalPanel.add(panel);
			panel.setSize("100%", "100%");
			horizontalPanel.setCellWidth(panel, "98%");
			
			add(horizontalPanel);
			setCellHeight(horizontalPanel,"1%");
			setCellWidth(horizontalPanel,"100%");
		}

		public void update(Sound sound) {
			update(sound,contentMusic);
			update(sound,contentSound);
		}

		private void update(Sound sound, VerticalPanel content) {
			for (int i = 0; i < content.getWidgetCount(); i++) {
				SoundWidget widget = (SoundWidget)content.getWidget(i);
				if (widget.sound==sound) {
					widget.apply(sound);
				}
			}
		}

		public void update(Picture picture) {
			update(picture,contentTop);
			update(picture,contentBottom);
			update(picture,contentBackground);
		}

		private void update(Picture picture, VerticalPanel content) {
			for (int i = 0; i < content.getWidgetCount(); i++) {
				PictureWidget widget = (PictureWidget)content.getWidget(i);
				if (widget.picture==picture) {
					widget.apply(picture);
				}
			}
		}

		public void activate() {
			active = true;
		}

		public void close() {
		}

		public void deactivate() {
			active = false;
		}
		
		public class ImageSprite extends Image {
			private Sprite sprite;

			public ImageSprite(Sprite sprite) {
				this.sprite = sprite;
				addErrorHandler(new ErrorHandler(){
					public void onError(ErrorEvent event) {
						Window.alert(appMessages.paragraphCannotLoadImage(backImage.getUrl()));
					}
				});
				setUrl(sprite.getPicture().getUrl());
			}

			public Sprite getSprite() {
				return sprite;
			}
		}

		public class PictureWidget extends HorizontalPanel implements ClickHandler {
			private static final String TOP_IMAGES_SELECTOR = "topImagesSelector";
			private Label name;
			private Image remove;
			private Image up;
			private Image down;
			private Picture picture;
			private Sprite sprite;
			private int type;
			private RadioButton selector;
			
			public PictureWidget(Picture picture,int type) {
				this.type = type;
				if (type==TOP_IMAGES) {
					selector = new RadioButton(TOP_IMAGES_SELECTOR);
					selector.addClickHandler(this);
					add(selector);
					setCellWidth(selector,"1%");
				} else if (type==SPRITE) {
					up = new Image(Images.UP_GREEN);
					up.setStyleName(Styles.CLICKABLE);
					up.addClickHandler(this);
					up.setTitle(appConstants.paragraphMoveSpriteUp());
					add(up);
					setCellWidth(up,"1%");
					down = new Image(Images.DOWN_GREEN);
					down.setStyleName(Styles.CLICKABLE);
					down.addClickHandler(this);
					down.setTitle(appConstants.paragraphMoveSpriteDown());
					add(down);
					setCellWidth(down,"1%");
				}
				name = new Label();
				setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				setStyleName("paragraph_edit_content_box");
				name.setTitle(appConstants.clickToPreview());
				name.setStyleName(Styles.CLICKABLE);
				name.addStyleName(Styles.BOLD);
				name.addClickHandler(this);
				add(name);
				setCellWidth(name,"99%");
				remove = new Image(Images.REMOVE);
				remove.setStyleName(Styles.CLICKABLE);
				remove.setTitle(appConstants.buttonRemove());
				remove.addClickHandler(this);
				add(remove);
				setCellWidth(remove,"1%");
				apply(picture);
			}
			
			public PictureWidget(Sprite sprite) {
				this(sprite.getPicture(),SPRITE);
				this.sprite = sprite;
			}

			public void apply(Picture picture) {
				this.picture = picture;
				name.setText(picture.getName());
			}

			public void onClick(ClickEvent event) {
				if (event.getSource()==name) {
					if (sprite==null) {
						//preview
						model.previewImage(picture);
					} else {
						//mark sprite
						animateSprite(sprite);
					}
				} else if (event.getSource()==remove) {
					switch (type) {
					case TOP_IMAGES:
						paragraph.getTopImages().remove(picture);
						contentTop.remove(this);
						if (selector.getValue()) {
							//selected
							if (backImage != null) {
								if (picture.getUrl().equals(backImage.getUrl())) {
									board.remove(backImage);
								}
							}
						}
						break;
					case BOTTOM_IMAGES:
						paragraph.getBottomImages().remove(picture);
						contentBottom.remove(this);
						break;
					case BACK_IMAGES:
						paragraph.getBackgroundImages().remove(picture);
						contentBackground.remove(this);
						break;
					case SPRITE:
						paragraph.getSprites().remove(sprite);
						contentSprites.remove(this);
						break;
					}
					model.updateParagraph(paragraph, paragraphListener);
				} else if (event.getSource()==selector) {
					//select new top image
					updateTopImage(picture);
					correctZOrder();
				} else if (event.getSource()==up) {
					sendSpriteTo(this,true);
				} else if (event.getSource()==down) {
					sendSpriteTo(this,false);
				}
			}
			
		}
		
		public class SoundWidget extends HorizontalPanel implements ClickHandler{
			private Label name;
			private Image remove;
			private Sound sound;
			private boolean background;
			public SoundWidget(Sound sound,boolean background) {
				this.background = background;
				setStyleName("paragraph_edit_content_box");
				setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				name = new Label();
				name.setTitle(appConstants.clickToHear());
				name.setStyleName(Styles.CLICKABLE);
				name.addClickHandler(this);
				add(name);
				setCellWidth(name,"99%");
				remove = new Image(Images.REMOVE);
				remove.setTitle(appConstants.buttonRemove());
				remove.setStyleName("clickable");
				remove.addClickHandler(this);
				add(remove);
				setCellWidth(remove,"1%");
				apply(sound);
			}
			
			public void apply(Sound sound) {
				this.sound = sound;
				name.setText(sound.getName());			
			}

			public void onClick(ClickEvent event) {
				if (event.getSource()==name) {
					model.playSound(sound);
				} else if (event.getSource()==remove) {
					if (background) {
						paragraph.getBackgroundSounds().remove(sound);
					} else {
						paragraph.getSounds().remove(sound);
					}
					if (background) {
						contentMusic.remove(this);
					} else {
						contentSound.remove(this);
					}
					model.updateParagraph(paragraph, paragraphListener);
				}
			}
			
		}
		
		public abstract class ContentButton<T> extends Image implements ClickHandler{
			private boolean graph;
			
			public ContentButton(boolean graph) {
				this.graph = graph;
				setUrl(Images.ADD_CONNECTION);
				setStyleName(Styles.CLICKABLE);
				setTitle(appConstants.paragraphAddSelectedContenr());
				addClickHandler(this);
				
			}
			
			@SuppressWarnings("unchecked")
			public final void onClick(ClickEvent event) {
				if (graph) {
					if (currentPicture == null) {
						Window.alert(appConstants.paragraphSelectImageInList());
					} else {
						addContent((T)currentPicture);
					}
				} else {
					if (currentSound == null) {
						Window.alert(appConstants.paragraphSelectSoundInList());
					} else {
						addContent((T)currentSound);
					}
				}
			}
			
			public abstract void addContent(T content);
			
		}

		public void sendSpriteTo(PictureWidget pictureWidget, boolean back) {
			ArrayList<Sprite> list = paragraph.getSprites();
			int i=0;
			for (Sprite sprite : list) {
				if (sprite==pictureWidget.sprite) {
					//found
					if (back) {
						if (i==0) {
							//already up
							return;
						} else {
							list.remove(i);
							list.add(i-1, sprite);
						}
					} else {
						int j = list.size()-2;
						if (i<j) {
							list.remove(i);
							list.add(i+1,sprite);
						} else if (i==j) {
							list.remove(i);
							list.add(sprite);
						} else {
							//already in bottom
							return;
						}
					}
					updateContenPanel(contentSprites, list);
					correctZOrder();
					return;
				}
				i++;
			}
		}

	
	}

}
