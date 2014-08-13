package com.iambookmaster.client.paragraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.Images;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.common.ColorProvider;
import com.iambookmaster.client.common.EditorTab;
import com.iambookmaster.client.common.MaskPanel;
import com.iambookmaster.client.editor.MapEditor;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ParagraphConnectionListener;
import com.iambookmaster.client.model.ParagraphListener;
import com.iambookmaster.client.model.SettingsListener;
import com.iambookmaster.client.paragraph.PathFinder.GameState;

public class ParagraphsMapEditor extends MapEditor implements EditorTab{
	
	private final AppConstants appConstants = AppLocale.getAppConstants();
	
	private Model model;
	private Map<Paragraph,ParagraphWidget> locations = new HashMap<Paragraph,ParagraphWidget>();
	private Map<ParagraphConnection,LocationConnectionWidget> connections = new HashMap<ParagraphConnection,LocationConnectionWidget>();
	private ParagraphListener locationListener;
	private ParagraphConnectionListener locationConnectionListener;
	private LocationMenuPanel locationMenuPanel;
	private NewConnectionPanel newConnectionPanel;
	private ParagraphWidget selectedWidget;
	private ParagraphWidget selectedWidget2;
	private boolean active;
	private HashSet<Paragraph> updataParagraphs = new HashSet<Paragraph>();
	private HashSet<ParagraphConnection> updataConnections = new HashSet<ParagraphConnection>();
	private boolean updateAll;
	private SettingsListener settingsListener; 
	private boolean showParagraphNumbers;

	private Timer updateTimer;
	public void activate() {
		if (selectedWidget != null) {
			selectedWidget.hightlight(false);
		}
		if (updateAll) {
			updateAll = false;
			updataConnections.clear();
			updataParagraphs.clear();
			updateAllConnections();
		} else {
			if (updataParagraphs.size()>0) {
				for (Iterator<Paragraph> iter = updataParagraphs.iterator(); iter.hasNext();) {
					Paragraph paragraph = iter.next();
					ParagraphWidget widget = locations.get(paragraph);
					if (widget != null) {
						widget.updateParagraph(paragraph);
					}
				}
				updataParagraphs.clear();
			}
			if (updataConnections.size()>0) { 
				for (Iterator<ParagraphConnection> iter = updataConnections.iterator(); iter.hasNext();) {
					ParagraphConnection connection = iter.next();
					LocationConnectionWidget widget = connections.get(connection);
					if (widget != null) {
						widget.updateConnection(connection);
					}
				}
				updataConnections.clear();
			}
		}
		selectedWidget = locations.get(model.getCurrentParagraph());
		if (selectedWidget != null) {
			selectedWidget.hightlight(true);
		}
		active = true;
	}

	public void deactivate() {
		active = false;
	}

	
	public ParagraphsMapEditor(Model mod) {
		super(mod.getSettings().getMaxDimensionX(),mod.getSettings().getMaxDimensionY());
//		DOM.setStyleAttribute(getElement(), "border", "2px solid green");
		this.model = mod;
		locationListener = new ParagraphListener(){
			public void addNewParagraph(Paragraph location) {
				if (location.getX()==0 && location.getY()==0) {
					location.setX(getSelectedPositionX()+20);
					location.setY(getSelectedPositionY()+20);
				}
				new ParagraphWidget(location);
			}

			public void refreshAll() {
				refreshLocations();
			}

			public void select(Paragraph location) {
//				
//				System.out.println("select.active="+active);
//				
				selectedWidget = locations.get(location);
				if (active && selectedWidget != null) {
					selectedWidget.hightlight(true);
					ensureVisible(selectedWidget);
				}
			}

			public void unselect(Paragraph location) {
				ParagraphWidget widget = locations.get(location);
				if (widget != null) {
					widget.hightlight(false);
				}
				selectedWidget = null;
			}

			public void update(Paragraph location) {
//				
//				System.out.println("update.active="+active);
//				
				ParagraphWidget widget = locations.get(location);
				if (widget != null) {
					if (active) {
						widget.updateParagraph(location);
					} else {
						updataParagraphs.add(location);
					}
				}
			}

			public void edit(Paragraph location) {
				//not our task
			}

			public void remove(Paragraph location) {
				ParagraphWidget widget = locations.get(location);
				if (widget != null) {
					widget.delete();
				}
			}
		};
		model.addParagraphListener(locationListener);
		
		showParagraphNumbers = model.getSettings().isShowParagraphNumbers();
		settingsListener = new SettingsListener() {
			public void settingsWereUpated() {
				if (showParagraphNumbers != model.getSettings().isShowParagraphNumbers()) {
					showParagraphNumbers = model.getSettings().isShowParagraphNumbers();
					//TODO
					refreshLocations();
				}
				applyMapSize(model.getSettings().getMaxDimensionX(),model.getSettings().getMaxDimensionY());
			}
		};
		model.addSettingsListener(settingsListener);
		
		locationConnectionListener = new ParagraphConnectionListener() {
			public void refreshAll() {
			}
			public void select(ParagraphConnection connection) {
				LocationConnectionWidget connectionWidget = connections.get(connection);
				if (connectionWidget != null) {
					selectWidget(null,connectionWidget);
				}
			}
			public void unselect(ParagraphConnection connection) {
				LocationConnectionWidget connectionWidget = connections.get(connection);
				if (connectionWidget != null) {
					selectWidget(null,null);
				}
			}
			public void update(ParagraphConnection connection) {
				LocationConnectionWidget connectionWidget = connections.get(connection);
				if (connectionWidget != null) {
					
					System.out.println("connection.update.active="+active);
					
					if (active) {
						connectionWidget.updateConnection(connection);
					} else {
						updataConnections.add(connection);
					}
				}
				
			}
			public void remove(ParagraphConnection connection) {
				LocationConnectionWidget connectionWidget = connections.get(connection);
				if (connectionWidget != null) {
					connectionWidget.remove();
				}
			}
			public void addNew(ParagraphConnection connection) {
				ParagraphWidget widget1 = locations.get(connection.getFrom());
				ParagraphWidget widget2 = locations.get(connection.getTo());
				if (widget1 != null && widget2 != null) {
					connections.put(connection,new LocationConnectionWidget(connection,widget1,widget2));
					
					System.out.println("connection.addNew.active="+active);
					
					if (active==false) {
						updataConnections.add(connection);
					}
				}
			}
		};
		model.addParagraphConnectionListener(locationConnectionListener);
		
		locationMenuPanel = new LocationMenuPanel();
		newConnectionPanel = new NewConnectionPanel();
	}
	
	public void refreshLocations() {
		if (active) {
			updateAllConnections();
		} else {
			updateAll = true;
		}
	}

	protected void updateAllConnections() {
		if (updateTimer != null) {
			return;
		}
		MaskPanel.show();
		clear();
		connections.clear();
		updateTimer = new Timer() {
			private ArrayList<Paragraph> list = new ArrayList<Paragraph>(model.getParagraphs());
			private Iterator<Paragraph> iteratorParagraph = list.iterator();
			private ArrayList<ParagraphConnection> listConnections = new ArrayList<ParagraphConnection>(model.getParagraphConnections());
			private Iterator<ParagraphConnection> iteratorConnection = listConnections.iterator();
			private HashMap<Paragraph, ParagraphWidget>map = new HashMap<Paragraph, ParagraphWidget>(list.size());
			private int first;
			@Override
			public void run() {
				if (first==0) {
					first = 1;
					int w = getOffsetWidth();
					int h = getOffsetHeight();
					//add visible paragraphs
					while (iteratorParagraph.hasNext()) {
						Paragraph paragraph = iteratorParagraph.next();
						if (w>=paragraph.getX() && h >=paragraph.getY()) {
							//visible now, draw now
							map.put(paragraph, new ParagraphWidget(paragraph));
							iteratorParagraph.remove();
						}
					}
					//add visible connections
					while (iteratorConnection.hasNext()) {
						ParagraphConnection connection = iteratorConnection.next();
						ParagraphWidget widget1 = map.get(connection.getFrom());
						ParagraphWidget widget2 = map.get(connection.getTo());
						if (widget1==null && widget2 != null) {
							//only one of widget exists
							list.remove(connection.getFrom());
							map.put(connection.getFrom(), new ParagraphWidget(connection.getFrom()));
						} else if (widget1 != null && widget2 == null) { 
							//only one of widget exists
							list.remove(connection.getTo());
							map.put(connection.getTo(), new ParagraphWidget(connection.getTo()));
						}
					}
					iteratorConnection = listConnections.iterator();
					iteratorParagraph = list.iterator();
					return;
				} else if (first==1) {
					first = 2;
					//add visible connections
					while (iteratorConnection.hasNext()) {
						ParagraphConnection connection = iteratorConnection.next();
						ParagraphWidget widget1 = map.get(connection.getFrom());
						ParagraphWidget widget2 = map.get(connection.getTo());
						if (widget1 !=null && widget2 != null) {
							connections.put(connection,new LocationConnectionWidget(connection,widget1,widget2));
							iteratorConnection.remove();
						}
					}
					iteratorConnection = listConnections.iterator();
					//remove mask and add other paragraphs in shadow
					MaskPanel.hide();
					return;
				}
				//add other staff
				int count=50;
				if (iteratorParagraph.hasNext()) {
					while (iteratorParagraph.hasNext()) {
						Paragraph location = iteratorParagraph.next();
						map.put(location, new ParagraphWidget(location));
						if (count--<0) {
							return;
						}
					}
					//pause between last paragraph and first connection
					return;
				}
				while (iteratorConnection.hasNext()) {
					ParagraphConnection connection = iteratorConnection.next();
					ParagraphWidget widget1 = map.get(connection.getFrom());
					ParagraphWidget widget2 = map.get(connection.getTo());
					if (widget1 != null && widget2 != null) {
						connections.put(connection,new LocationConnectionWidget(connection,widget1,widget2));
					}
					if (count--<0) {
						return;
					}
				}
				updateTimer = null;
				cancel();
				MaskPanel.hide();
			}
			
		};
		updateTimer.scheduleRepeating(50);

	}

	public class ParagraphWidget extends MapWidget {
		private Paragraph location; 
		private Image typeImage;
		
		public ParagraphWidget(Paragraph location) {
			this.location = location;
			locations.put(location,this);
			applyTitleStyle();
			draw(location.getX(),location.getY());
		}


		//usually is called externally
		public void updateParagraph(Paragraph location) {
			this.location = location; //usually - useless, it should be the same
			applyTypeToImage();
			applyTitleStyle();
			redraw(location.getX(),location.getY());
		}
		
		private void applyTitleStyle() {
			String style = "paragraph_map_prop"; 
			switch (location.getStatus()) {
			case Model.STATUS_DRAFT:
				style = "paragraph_map_draft";
				break;
			case Model.STATUS_FINAL:
				style = "paragraph_map_final";
				break;
			}
//			if (location.getBattle() != null) {
//				style = style + " paragraph_map_battle";
//			}
			setTitleStyle(style);
		}


		public String getFullName() {
			if (model.getSettings().isShowParagraphNumbers() && location.getNumber()>0) {
				return String.valueOf(location.getNumber())+". "+location.getName();
			} else {
				return location.getName();
			}
		}
		
		public String getName() {
			return location.getName() ;
		}
		
		public void connectTo(MapWidget mapWidget) {
			if (mapWidget instanceof ParagraphWidget) {
				selectedWidget2 = (ParagraphWidget) mapWidget;
				if (this==selectedWidget2) {
					//useless
					return;
				}
				if (model.getSettings().isOneWayConnectionsOnly()) {
					//one way connection only
					addNewConnection(this,selectedWidget2,false);
				} else { 
					selectedWidget = this;
					int x = getAbsoluteLeft();
					int y = getAbsoluteTop();
					newConnectionPanel.setPopupPosition(x,y);
					newConnectionPanel.show();
				}
			} else {
				Window.alert(appConstants.cannotConnectParagraphToNonParagraph());
			}
		}
		
		protected void updatePosition(int x, int y) {
			location.setX(x);
			location.setY(y);
			model.updateParagraph(location,locationListener);
		}

		public Widget getQuickWidget() {
			typeImage = new Image();
			typeImage.setStyleName(Styles.CLICKABLE);
			typeImage.setTitle(appConstants.titleContextMenu());
			typeImage.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					selectedWidget = ParagraphWidget.this;
					int w= Window.getClientWidth()-220;
					int h = Window.getClientHeight()-230;
					int l = typeImage.getAbsoluteLeft();
					int t = typeImage.getAbsoluteTop();
					if (w<l) {
						l = w;
					}
					if (h<t) {
						t = h;
					}
					if (l<0) {
						l = 0;
					}
					if (t<0) {
						t = 0;
					}
					locationMenuPanel.setPopupPosition(l,t);
					locationMenuPanel.show();
				}
			});
			applyTypeToImage();
			return typeImage;
		}
		
		private void applyTypeToImage() {
			typeImage.setUrl(getParagraphTypeURL(location));
		}
		public void select() {
			model.selectParagraph(location,locationListener);
		}
		
		public void onNameChanged(String name) {
			location.setName(name);
			model.updateParagraph(location, locationListener);
			redraw(location.getX(), location.getY());
		}

		public void unselect() {
			model.unselectParagraph(location,locationListener);
		}
	}
	
	public class LocationConnectionWidget extends MapConnector {
		private ParagraphConnection connection;
		public LocationConnectionWidget(ParagraphConnection connection,ParagraphWidget widget1, ParagraphWidget widget2) {
			super(widget1, widget2,connection.isBothDirections(),connection.getType()== ParagraphConnection.TYPE_NORMAL && connection.getObject()==null ? MapEditor.STYLE_NORMAL : MapEditor.STYLE_CONDITIONAL,ColorProvider.getColorName(connection.getColor()),connection.getCorrectionX(),connection.getCorrectionY());
			this.connection = connection; 
			update();
			applySource();
		}

		public void updateConnection(ParagraphConnection connection) {
			this.connection = connection;
			if (this.connection==null) {
				this.connection = connection; 
			}
			int style;
			if (connection.getType()== ParagraphConnection.TYPE_NORMAL && connection.getObject()==null) {
				style = MapEditor.STYLE_NORMAL;
			} else {
				style = MapEditor.STYLE_CONDITIONAL;
			}

			applySource();
			
			if (style != getStyle() || !getColor().equals(ColorProvider.getColorName(connection.getColor()))) {
				setStyle(style);
				setColor(ColorProvider.getColorName(connection.getColor()));
				update();
			}
		}

		private void applySource() {
			if (getSourceImage() != null) {
				if (connection.getStrictness()==ParagraphConnection.STRICTNESS_MUST) {
					getSourceImage().setUrl(Images.SOURCE_MUST);
				} else {
					getSourceImage().setUrl(Images.SOURCE);
				}
			}
		}

		public void select() {
			if (this.connection==null) {
				this.connection = null; 
			}
			model.selectParagraphConnection(connection,locationConnectionListener);
		}

		public void unselect() {
			model.unselectParagraphConnection(connection,locationConnectionListener);
		}

		@Override
		public void updateCorrection(int x, int y) {
			connection.setCorrectionX(x);
			connection.setCorrectionY(y);
		}
		
	}
	
	public class LocationMenuPanel extends PopupPanel {

		private MenuBar locationMenu;
		
		public LocationMenuPanel() {
			super(true,true);
			locationMenu = new MenuBar(true);
			locationMenu.addItem(appConstants.buttonRename(),new Command() {
				public void execute() {
					LocationMenuPanel.this.hide();
					selectedWidget.editTitle();
				}
			});
			
			locationMenu.addItem(appConstants.buttonEdit(),new Command() {
				public void execute() {
					model.editParagraph(selectedWidget.location,locationListener);
					LocationMenuPanel.this.hide();
				}
			});
			
			locationMenu.addItem(appConstants.buttonCheckAvailability(),new Command() {
				public void execute() {
					LocationMenuPanel.this.hide();
					PathFinder finder = new PathFinder(model);
					if (model.getStartParagraph()==null) {
						Window.alert(appConstants.modelStartParagraphNotSet());
						return;
					}
					GameState objects = finder.new GameState(); 
					if (finder.findWays(model.getStartParagraph(), selectedWidget.location, objects, null, null, PathFinder.FIND_ONE)==null) {
						Window.alert(appConstants.modelCannotReachParagraph());
					} else {
						Window.alert(appConstants.modelCanReachParagraph());
					}
				}
			});
			
			locationMenu.addSeparator();
			locationMenu.addItem(appConstants.buttonMakeParagraphNormal(),new Command() {
				public void execute() {
					LocationMenuPanel.this.hide();
					model.makeParagraphAsNormal(selectedWidget.location);
				}
			});
			locationMenu.addItem(appConstants.buttonMakeParagraphStart(),new Command() {
				public void execute() {
					LocationMenuPanel.this.hide();
					model.makeParagraphAsStart(selectedWidget.location);
				}
			});
			locationMenu.addItem(appConstants.buttonMakeParagraphFail(),new Command() {
				public void execute() {
					LocationMenuPanel.this.hide();
					model.makeParagraphAsFail(selectedWidget.location);
				}
			});
			locationMenu.addItem(appConstants.buttonMakeParagraphSuccess(),new Command() {
				public void execute() {
					LocationMenuPanel.this.hide();
					model.makeParagraphAsSuccess(selectedWidget.location);
				}
			});
			locationMenu.addItem(appConstants.importMakeCommecial(),new Command() {
				public void execute() {
					LocationMenuPanel.this.hide();
					model.makeParagraphAsCommercial(selectedWidget.location);
				}
			});
			add(locationMenu);
		}
		
	}
	public class NewConnectionPanel extends PopupPanel {

		private MenuBar newConnectionMenu;
		
		public NewConnectionPanel() {
			super(true,true);
			newConnectionMenu = new MenuBar(true);
			newConnectionMenu.addItem(appConstants.modelBiDirection(),new Command() {
				public void execute() {
					addNewConnection(selectedWidget,selectedWidget2,true);
					NewConnectionPanel.this.hide();
				}
			});
			newConnectionMenu.addItem(appConstants.modelToHere(),new Command() {
				public void execute() {
					addNewConnection(selectedWidget,selectedWidget2,false);
					NewConnectionPanel.this.hide();
				}
			});
			newConnectionMenu.addItem(appConstants.modelFromHere(),new Command() {
				public void execute() {
					addNewConnection(selectedWidget2,selectedWidget,false);
					NewConnectionPanel.this.hide();
				}
			});
			add(newConnectionMenu);
		}
		
	}
	
	private void addNewConnection(ParagraphWidget from,ParagraphWidget to, boolean bothDirections) {
		ParagraphConnection connection = new ParagraphConnection();
		connection.setBothDirections(bothDirections);
		connection.setFrom(from.location);
		connection.setTo(to.location);
		connections.put(connection,new LocationConnectionWidget(connection,from,to));
		model.addParagraphConnection(connection,locationConnectionListener);
	}
	
	public static String getParagraphTypeURL(Paragraph paragraph) {
		switch (paragraph.getType()) {
		case Paragraph.TYPE_FAIL:
			return Images.LOCATION_FAIL;
		case Paragraph.TYPE_START:
			return Images.LOCATION_START;
		case Paragraph.TYPE_SUCCESS:
			return Images.LOCATION_SUCCESS;
		case Paragraph.TYPE_COMMERCIAL:
			return Images.COMMERCIAL;
		default:
			if (paragraph.getBattle()==null) {
				if (paragraph.getGotObjects().size()+paragraph.getLostObjects().size()==0) {
					return Images.LOCATION_NORMAL;
				} else {
					return Images.LOCATION_NORMAL_OBJECT;
				}
			} else {
				return Images.NPC_ICON;
			}
		}
	}

	public void close() {
		model.removeParagraphConnectionListener(locationConnectionListener);
		model.removeParagraphListener(locationListener);
		model.removeSettingsListener(settingsListener);
	}

	

}
