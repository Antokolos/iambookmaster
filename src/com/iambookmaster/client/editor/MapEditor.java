package com.iambookmaster.client.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.dnd.client.DragController;
import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.Images;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.common.ScrollContainer;

public abstract class MapEditor extends ScrollContainer {

	private static final int SOURCE_X = 6;
	private static final int SOURCE_Y = 6;
	public static final int STYLE_NORMAL=0;
	public static final int STYLE_CONDITIONAL=1;
	
	private AbsolutePanel mainPanel;
	private Map<MapWidget,ArrayList<MapConnector>> connections = new HashMap<MapWidget,ArrayList<MapConnector>>(); 
	private DragController dragController;
	private MapConnector selectedConnection;
	private MapWidget selectedMapWidget;
	private TitleTextBox titleTextBox;
	
	public int getSelectedPositionY() {
		if (selectedMapWidget == null) {
			return 0;
		} else {
			return getPositionY(selectedMapWidget);
		}
	}

	public int getSelectedPositionX() {
		if (selectedMapWidget == null) {
			return 0;
		} else {
			return getPositionX(selectedMapWidget);
		}
	}

	public void clear() {
		mainPanel.clear();
		connections = new HashMap<MapWidget, ArrayList<MapConnector>>();
		selectedConnection = null;
	}
	
	protected void applyMapSize(int maxDimensionX, int maxDimensionY) {
		mainPanel.setSize(String.valueOf(maxDimensionX)+"px", String.valueOf(maxDimensionY)+"px");	
	}

	public MapEditor(int maxX,int maxY) {
		setSize("100%", "100%");
		mainPanel = new AbsolutePanel();
		mainPanel.setStyleName("editor_panel");
		applyMapSize(maxX,maxY);
	    // Create a DragController for each logical area where a set of draggable
	    // widgets and drop targets will be allowed to interact with one another.
	    dragController = new PickupDragController(mainPanel, true);
	    dragController.addDragHandler(new DragHandler(){
			private int dragConnectionPosition;
			private MapWidget gragMapWidget;
			private MapConnector dragConnectionWidget;

			public void onDragEnd(DragEndEvent event) {
				Widget draggable = event.getContext().draggable;
				if (event.getContext().vetoException==null) {
					if (draggable instanceof MapWidget) {
						updateConnectionsOfWidget((MapWidget) draggable);
					}
				} else if (gragMapWidget != null) {
					//new connection
					gragMapWidget.onDragEnd(event.getContext().desiredDraggableX, event.getContext().desiredDraggableY);
				} else if (dragConnectionWidget != null) {
					//correct connection location
					if (draggable instanceof MapConnector) {
						//vertical correction
						MapConnector connector = (MapConnector)draggable;
						dragConnectionWidget.correctionX = dragConnectionWidget.correctionX + event.getContext().desiredDraggableX-dragConnectionPosition;
						dragConnectionWidget.updateCorrection(dragConnectionWidget.correctionX,dragConnectionWidget.correctionY);
						connector.update();
					} else {
						//horizontal correction
						MapConnectorHorizontral connector = (MapConnectorHorizontral)draggable;
						dragConnectionWidget.correctionY = dragConnectionWidget.correctionY + event.getContext().desiredDraggableY-dragConnectionPosition;
						dragConnectionWidget.updateCorrection(dragConnectionWidget.correctionX,dragConnectionWidget.correctionY);
						connector.connector.update();
					}
				}
			}

			public void onDragStart(DragStartEvent event) {
				gragMapWidget = null; 
				dragConnectionWidget = null;
				Widget draggable = event.getContext().draggable;
				if (draggable instanceof Image) {
					if (draggable.getParent() instanceof MapWidget) {
						gragMapWidget = (MapWidget)draggable.getParent(); 
					}
				} else if (draggable instanceof MapConnector) {
					dragConnectionWidget = (MapConnector)draggable;
					dragConnectionPosition = event.getContext().mouseX;
					
				} else if (draggable instanceof MapConnectorHorizontral) {
					dragConnectionWidget = ((MapConnectorHorizontral)draggable).connector;
					dragConnectionPosition = event.getContext().mouseY;
				}
			}

			public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
				Widget draggable = event.getContext().draggable;
				if (draggable instanceof MapWidget) {
				} else {
					throw new VetoDragException();
				}

			}

			public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
			}
	    	
	    });
		setScrollWidget(mainPanel);
		titleTextBox = new TitleTextBox();
	}
    

	private void updateConnectionsOfWidget(final MapWidget mapWidget) {
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				ArrayList<MapConnector> list = connections.get(mapWidget);
				if (list != null && list.size()>0) {
					//update all connections
					for (int i = 0; i < list.size(); i++) {
						MapConnector connector = list.get(i);
						connector.update();
						
					}
				}
				int x = getPositionX(mapWidget);
				int y = getPositionY(mapWidget);
				if (x>0 || y>0) {
					mapWidget.updatePosition(x,y);
				}
			}
		});
	}


//	private final PopupPanel dndPopupLabel = new PopupPanel(){
//		{
//			this.setStyleName("map_editor_dnd");
//			this.setWidget(new Image(Images.ADD_CONNECTION));
//		}
//	};

	protected void selectWidget(MapWidget mapWidget, MapConnector connector) {
		if (mapWidget == null) {
			if (selectedMapWidget != null) {
				//clear selected widget
				selectedMapWidget.unselect();
				selectedMapWidget.hightlight(false);
				selectedMapWidget = null;
			}
			if (connector==null) {
				//nothing
			} else {
				if (selectedConnection==connector) {
//					connector.hightlight(false);
//					connector.unselect();
//					dragController.makeNotDraggable(selectedConnection);
//					selectedConnection = null;
				} else {
					connector.hightlight(true);
					if (selectedConnection != null) {
						selectedConnection.hightlight(false);
						selectedConnection.unselect();
						try {
							dragController.makeNotDraggable(selectedConnection);
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							dragController.makeNotDraggable(selectedConnection.horizontralConnection);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					selectedConnection = connector;
					connector.select();
					dragController.makeDraggable(selectedConnection);
					dragController.makeDraggable(selectedConnection.horizontralConnection);
				}
			}
		} else {
			if (selectedConnection != null) {
				//clear selected widget
				selectedConnection.hightlight(false);
				selectedConnection.unselect();
				try {
					dragController.makeNotDraggable(selectedConnection);
				} catch (Exception e) {
				}
				try {
					dragController.makeNotDraggable(selectedConnection.horizontralConnection);
				} catch (Exception e) {
				}
				selectedConnection = null;
			}
			if (selectedMapWidget!=mapWidget) {
				if (selectedMapWidget != null) {
					selectedMapWidget.unselect();
					selectedMapWidget.hightlight(false);
				}
				selectedMapWidget = mapWidget;
				selectedMapWidget.select();
				selectedMapWidget.hightlight(true);
			}
		}
	}

	public abstract class MapWidget extends HorizontalPanel implements MouseDownHandler {
		private Label nameLabel;
		private Image connectorImage;
		
		public MapWidget() {
			DOM.setStyleAttribute(getElement(), "zIndex", "100");
			nameLabel = new Label();
			nameLabel.setWordWrap(false);
			nameLabel.addMouseDownHandler(this);
			add(nameLabel);
		}
		
		public void draw(int x,int y) {
			nameLabel.setText(getFullName());
			
			//image for connections
			Widget widget = getQuickWidget();
			if (widget != null) {
				add(widget);
			}
			connectorImage = new Image(Images.ADD_CONNECTION);
			connectorImage.setStyleName(Styles.MOVE);
			add(connectorImage);
			mainPanel.add(this, x, y);
			dragController.makeDraggable(this,nameLabel);
			dragController.makeDraggable(connectorImage);
			hightlight(false);
		}
		
		public void delete() {
			ArrayList<MapConnector> list = connections.get(this);
			if (list != null) {
				connections.remove(this);
				for (int i = 0; i < list.size(); i++) {
					MapConnector connector = list.get(i);
					connector.removeWidget();
				}
			}
			dragController.makeNotDraggable(this);
			dragController.makeNotDraggable(connectorImage);
			mainPanel.remove(this);
		}
		
		public void hightlight(boolean highlight) {
			if (highlight) {
				setStyleName("map_widget_selected");
			} else {
				setStyleName("map_widget");
			}
		}

		public abstract void unselect();

		public abstract void select();
		
		public abstract Widget getQuickWidget();
		
		protected abstract void updatePosition(int x, int y);
		
		public abstract String getName();
		
		public abstract String getFullName();
		
		
		public void onMouseDown(MouseDownEvent event) {
			if (event.getSource()==nameLabel) {
				selectWidget(MapWidget.this,null);
			}
		}

		public void onDragEnd(int absX,int absY) {
			int l = mainPanel.getWidgetCount();
			for (int i = 0; i < l; i++) {
				Widget widget = mainPanel.getWidget(i);
				if (widget instanceof MapWidget) {
					MapWidget mapWidget = (MapWidget) widget;
					int x1 = mapWidget.getAbsoluteLeft();
					int x2 = x1+mapWidget.getOffsetWidth();
					int y1 = mapWidget.getAbsoluteTop();
					int y2 = y1+mapWidget.getOffsetHeight();
					if (x1<=absX && x2>=absX && y1 <=absY && y2 >=absY) {
						//this
						connectTo(mapWidget);
						break;
					}
				}
			}
		}
		public abstract void connectTo(MapWidget mapWidget);
		
		public abstract void onNameChanged(String name);
		
		public void redraw(int x, int y) {
			if (x==0 && y==0) {
				x=0;
			}
			mainPanel.setWidgetPosition(this, x, y);
			nameLabel.setText(getFullName());
			updateConnectionsOfWidget(this);
		}

		public void editTitle() {
			titleTextBox.show(this);
		}
		
		public void setTitleStyle(String style) {
			nameLabel.setStyleName(style);
			nameLabel.addStyleName(Styles.MOVE);
		}
	}
	
	public class TitleTextBox extends TextBox implements BlurHandler,ChangeHandler, KeyPressHandler{
		private MapWidget mapWidget;
		private Command focusCommand = new Command() {
			public void execute() {
				setFocus(false);
			}
		};
		public TitleTextBox() {
			addBlurHandler(this);
			addChangeHandler(this);
			addKeyPressHandler(this);
			DOM.setStyleAttribute(getElement(), "zIndex", "10000");
		}
		public void show(MapWidget widget) {
			mapWidget = widget;
			setText(widget.getName());
			mainPanel.add(this,getPositionX(widget),getPositionY(widget));
			setSize(String.valueOf(widget.getOffsetWidth())+"px",String.valueOf(widget.getOffsetHeight())+"px");
			DeferredCommand.addCommand(focusCommand);
		}
		public void onBlur(BlurEvent event) {
			mainPanel.remove(this);
		}
		public void onChange(ChangeEvent event) {
			mapWidget.onNameChanged(this.getText());
		}
		public void onKeyPress(KeyPressEvent event) {
			if (event.getCharCode()==KeyCodes.KEY_ENTER) {
				mapWidget.onNameChanged(this.getText());
				mainPanel.remove(this);
			}
			if (event.getCharCode()==KeyCodes.KEY_ESCAPE) {
				//no changes in this case
				setText(mapWidget.getName());
				mainPanel.remove(this);
			}
		}
	}
	
	
	private int getPositionY(Widget widget) {
		return DOM.getElementPropertyInt(widget.getElement(), "offsetTop");
	}
	private int getPositionX(Widget widget1) {
		return DOM.getElementPropertyInt(widget1.getElement(), "offsetLeft");
	}
	
	public final class MapConnectorHorizontral extends HTML {
		private MapConnector connector;

		public MapConnectorHorizontral(MapConnector connector) {
			super("&nbsp;");
			DOM.setStyleAttribute(getElement(), "zIndex", "1");
			DOM.setStyleAttribute(getElement(), "fontSize", "1px");
			this.connector=connector;
		}
		
	}
	
	public abstract class MapConnector extends HTML implements MouseDownHandler {
		protected int correctionY;
		protected int correctionX;
		private static final String BORDER_ATTRIBUTE_HOR = "borderLeft";
		private static final String BORDER_ATTRIBUTE_VER = "borderTop";
		private MapWidget widget1;
		private MapWidget widget2;
		private boolean hightlight;
		private Image source;
		private int style;
		private String color;
		private MapConnectorHorizontral horizontralConnection;
		
		public void remove() {
			ArrayList<MapConnector> list = connections.get(widget1);
			if (list != null) {
				list.remove(this);
			}
			list = connections.get(widget2);
			if (list != null) {
				list.remove(this);
			}
			removeWidget();
		}

		
		private void removeWidget() {
			mainPanel.remove(this);
			mainPanel.remove(horizontralConnection);
			if (source != null) {
				mainPanel.remove(source);
			}
			if (selectedConnection==this) {
				selectedConnection = null;
			}
		}


		public MapConnector(MapWidget widget1,MapWidget widget2, boolean bothDirections, int style, String color,int corrX,int corrY) {
			super("&nbsp;");
			horizontralConnection = new MapConnectorHorizontral(this);
			correctionX = corrX;
			correctionY = corrY;
			if (bothDirections) {
				setWidth("6px");
				horizontralConnection.setHeight("6px");
			} else {
				setWidth("6px");
				horizontralConnection.setHeight("6px");
			}
			DOM.setStyleAttribute(getElement(), "fontSize", "1px");
			DOM.setStyleAttribute(getElement(), "zIndex", "1");
			this.style = style;
			this.color = color;
			if (bothDirections==false) {
				source = new Image(Images.SOURCE);
				mainPanel.add(source, 0, 0);
			}
			addMouseDownHandler(this);
			horizontralConnection.addMouseDownHandler(this);
			this.widget1 = widget1;
			this.widget2 = widget2;
			addConnection(widget1);
			addConnection(widget2);
			mainPanel.add(this, 0, 0);
			mainPanel.add(horizontralConnection, 0, 0);
			if (bothDirections==false) {
				mainPanel.add(source, 0, 0);
			}
		}
		
		public int getStyle() {
			return style;
		}


		public void setStyle(int style) {
			this.style = style;
		}
		public void update() {
			int ya1 = getPositionY(widget1);
			int xa1 = getPositionX(widget1);
			int yb1 = getPositionY(widget2);
			int xb1 = getPositionX(widget2);
			if (ya1<=yb1) {
				_update(xa1,ya1,xa1+widget1.getOffsetWidth(),ya1+widget1.getOffsetHeight(),xb1,yb1,xb1+widget2.getOffsetWidth(),yb1+widget2.getOffsetHeight(),true);
			} else {
				//mirror
				_update(xb1,yb1,xb1+widget2.getOffsetWidth(),yb1+widget2.getOffsetHeight(),xa1,ya1,xa1+widget1.getOffsetWidth(),ya1+widget1.getOffsetHeight(),false);
			}
		}

		protected Image getSourceImage() {
			return source;
		}

		private void _update(int xa1, int ya1, int xa2, int ya2, int xb1, int yb1, int xb2, int yb2, boolean normalDirection) {
			boolean top=false;
			boolean left=false;
			int x;
			int y;
			int h;
			int w;
			if (ya2<yb1) {
				//all widget is above
				if (xa2<xb1) {
					//widget 1 on the left
					top = true;
					x = xa2;
					if (correctionY == 0) {
						y = ya2;
					} else {
						y = ya2+correctionY;
						if (y>ya2) {
							y = ya2;
							correctionY = 0;
							updateCorrection(correctionX,correctionY);
						} else if (y<ya1) {
							y = ya1;
							correctionY = ya1-ya2;
							updateCorrection(correctionX,correctionY);
						}
					}
					w = xb1 - x;
					if (correctionX > 0) {
						if (xb2-xb1<correctionX) {
							w = xb2 - x;
							correctionX = xb2-xb1;
							updateCorrection(correctionX,correctionY);
						} else {
							w = w+correctionX;
						}
					} else if (correctionX<0){
						correctionX = 0;
						updateCorrection(correctionX,correctionY);
					}
					h = yb1 - y;
					if (h<1) {
						h=1;
					}
					if (w<1) {
						w = 1;
					}
					if (source != null) {
						if (normalDirection) {
							mainPanel.setWidgetPosition(source, x-SOURCE_X, y-SOURCE_Y+4);
						} else {
							mainPanel.setWidgetPosition(source, x+w-SOURCE_X, yb1-SOURCE_Y);
						}
					}
				} else if (xa1 > xb2){
					//widget 1 on the right
					top = true;
					left = true;
					if (correctionY == 0) {
						y = ya2;
					} else {
						y = ya2+correctionY;
						if (y<ya1) {
							y = ya1;
							correctionY = ya1-ya2;
							updateCorrection(correctionX,correctionY);
						} else if (y>ya2) {
							y = ya2;
							correctionY = 0;
							updateCorrection(correctionX,correctionY);
						}
					}
					if (correctionX < 0) {
						x = xb2+correctionX;
						if (x<xb1) {
							correctionX = xb1-xb2;
							updateCorrection(correctionX,correctionY);
							x=xb1;
						}
					} else {
						if (correctionX>0){
							correctionX=0;
							updateCorrection(correctionX,correctionY);
						}
						x = xb2;
					}
					w = xa1 - x;
					h = yb1 - y;
					if (h<1) {
						h=1;
					}
					if (w<1) {
						w = 1;
					}
					if (source != null) {
						if (normalDirection) {
							mainPanel.setWidgetPosition(source, xa1-SOURCE_X, y-SOURCE_Y+4);
						} else {
							mainPanel.setWidgetPosition(source, x-SOURCE_X, yb1-SOURCE_Y);
						}
					}
				} else if (xa1<xb1){
					//widget 1 a bit left
					left = true;
					if (correctionX > 0) {
						x = xb1+correctionX;
						if (x > xb2) {
							correctionX = xb2-xb1;
							updateCorrection(correctionX,correctionY);
							x=xb2;
						}
						if (x>xa2) {
							correctionX = xa2-xb1;
							updateCorrection(correctionX,correctionY);
							x=xa2;
						}
					} else {
						if (correctionX<0){
							correctionX=0;
							updateCorrection(correctionX,correctionY);
						}
						x = xb1;
					}
					y = ya2;
					w = 2;
					h = yb1 - y;
					if (h<1) {
						h=1;
					}
					if (source != null) {
						if (normalDirection) {
							mainPanel.setWidgetPosition(source, x-SOURCE_X, y-SOURCE_Y+4);
						} else {
							mainPanel.setWidgetPosition(source, x+w-SOURCE_X, yb1-SOURCE_Y);
						}
					}
				} else {
					//widget 1 a bit right
					if (correctionX > 0) {
						x = xa1+correctionX;
						if (x > xa2) {
							correctionX = xa2-xa1;
							updateCorrection(correctionX,correctionY);
							x=xa2;
						}
						if (x > xb2) {
							x = xb2;
							correctionX = xb2-xa1;
							updateCorrection(correctionX,correctionY);
						}
					} else {
						if (correctionX<0){
							correctionX=0;
							updateCorrection(correctionX,correctionY);
						}
						x = xa1;
					}
					y = ya2;
					w = 2;
					h = yb1 - y;
					if (h<1) {
						h=1;
					}
					if (source != null) {
						if (normalDirection) {
							mainPanel.setWidgetPosition(source, x-1, y-SOURCE_Y+4);
						} else {
							mainPanel.setWidgetPosition(source, x+w-3, yb1-SOURCE_Y);
						}
					}
				}
			} else if (xa2<xb1) {
				//y intersection, left
				top = true;
				x = xa2;
				if (correctionY>0) {
					y = ya1+correctionY;
					if (y>ya2) {
						correctionY = ya2-ya1;
						updateCorrection(correctionX,correctionY);
						y=ya2;
					}
					if (y>yb2) {
						correctionY = yb2-ya1;
						updateCorrection(correctionX,correctionY);
						y=yb2;
					}
				} else {
					if (correctionY<0){
						correctionY=0;
						updateCorrection(correctionX,correctionY);
					}
					y = ya1;
				}
				w = xb1 - xa2;
				h = yb1 - y;
				if (h<1) {
					h=1;
				}
				if (w<1) {
					w = 1;
				}
				if (source != null) {
					if (normalDirection) {
						mainPanel.setWidgetPosition(source, x-SOURCE_X, y-SOURCE_Y+4);
					} else {
						mainPanel.setWidgetPosition(source, xb1-SOURCE_X, y-SOURCE_Y);
					}
				}
			} else {
				//y intersection, right
				top = true;
				left = true;
				x = xb2;
				if (correctionY>0) {
					y = ya1+correctionY;
					if (y>ya2) {
						correctionY = ya2-ya1;
						updateCorrection(correctionX,correctionY);
						y=ya2;
					}
					if (y>yb2) {
						correctionY = yb2-ya1;
						updateCorrection(correctionX,correctionY);
						y=yb2;
					}
				} else {
					if (correctionY<0){
						correctionY=0;
						updateCorrection(correctionX,correctionY);
					}
					y = ya1;
				}
				w = xa1 - xb2;
				h = yb1 - y;
				if (h<1) {
					h=1;
				}
				if (w<1) {
					w = 1;
				}
				if (source != null) {
					if (normalDirection) {
						mainPanel.setWidgetPosition(source, xa1-SOURCE_X, y-SOURCE_Y+4);
					} else {
						mainPanel.setWidgetPosition(source, x-SOURCE_X, y+h-SOURCE_Y);
					}
				}
			}
//			if (source==null) {
//				x=x-2;
//				w=w+4;
//				y=y-4;
//				h=h+8;
//			}
			if (left) {
				mainPanel.setWidgetPosition(this, x, y);
			} else {
				//right or middle
				mainPanel.setWidgetPosition(this, x+w, y);
			}
			setHeight(String.valueOf(h));
			
			if (top) {
				mainPanel.setWidgetPosition(horizontralConnection, x, y);
			} else {
				//bottom or middle
				mainPanel.setWidgetPosition(horizontralConnection, x, y+h);
			}
			horizontralConnection.setWidth(String.valueOf(w));
			apply(hightlight);
		}

		
		private void addConnection(MapWidget widget) {
			ArrayList<MapConnector> list = connections.get(widget);
			if (list==null) {
				list = new ArrayList<MapConnector>();
				connections.put(widget,list);
			}
			list.add(this);
		}

		public void onMouseDown(MouseDownEvent event) {
			selectConnection(this);
		}

		public void hightlight(boolean hightlight) {
			this.hightlight = hightlight;
			apply(hightlight);
		}

		private void apply(boolean highlight) {
			String value;
			if (style==STYLE_NORMAL) {
				if (source==null) {
					value = highlight ? "6px double "+color:"3px double "+color;
				} else {
					value = highlight ? "2px solid "+color:"1px solid "+color;
				}
			} else {
				if (source==null) {
					value = highlight ? "2px dashed "+color:"1px dashed "+color;
				} else {
					value = highlight ? "4px dotted "+color:"1px dotted "+color;
				}
			}
			DOM.setStyleAttribute(getElement(), BORDER_ATTRIBUTE_HOR, value);
			DOM.setStyleAttribute(horizontralConnection.getElement(), BORDER_ATTRIBUTE_VER, value);
		}

		private void selectConnection(MapConnector connector) {
			selectWidget(null, this);
		}
		
		public abstract void unselect();
		public abstract void select();
		public abstract void updateCorrection(int x,int y);


		public String getColor() {
			return color;
		}


		public void setColor(String color) {
			this.color = color;
		}
		
	}


}
