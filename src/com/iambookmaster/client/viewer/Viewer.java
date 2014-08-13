package com.iambookmaster.client.viewer;

import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.common.Base64Coder;
import com.iambookmaster.client.common.FileExchangeClient;
import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.exceptions.JSONException;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.player.ContentPlayerImpl;
import com.iambookmaster.client.player.PlayImages;
import com.iambookmaster.client.player.Player;
import com.iambookmaster.client.player.PlayerLayout;
import com.iambookmaster.client.player.PlayerStyles;
import com.iambookmaster.client.player.layout.ViewerLayout;

public class Viewer implements EntryPoint {

	private Player player;
	private ContentPlayerImpl contentPlayer;
	private VerticalPanel mainPanel;
	private Image about;
	private Label title;
	private Label authors;
	private Model model;
	private ViewerAboutPanel aboutPanel;
	private String feedbackURL;
	private boolean otherBooks;
	private PlayerLayout layout;
	
	public void onModuleLoad() {
		FileExchangeClient.init(true);
		
		layout = new PlayerLayout() {
			public Element getElement(String id) {
				return DOM.getElementById(id);
			}

			public void addStyle(String id, String style) {
				Element element = layout.getElement(id);
				if (element!=null) {
					element.addClassName(style);
				}
			}
			public void removeStyle(String id, String style) {
				Element element = layout.getElement(id);
				if (element!=null) {
					element.removeClassName(style);
				}
			}
		};
		
		mainPanel = new VerticalPanel();
		mainPanel.setSize("100%", "100%");
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSize("100%", "100%");
		
		about = new Image(PlayImages.ABOUT);
		about.addStyleName(PlayerStyles.CLICKABLE);
		Element element = layout.getElement(PlayerLayout.ABOUT_BUTTON);
		if (element==null) {
			about.addStyleName("player_about");
			about.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					showAboutDialog(true);
				}
			});
			horizontalPanel.add(about);
			horizontalPanel.setCellHeight(about,"100%");
			horizontalPanel.setCellWidth(about,"1%");
		} else {
			com.google.gwt.user.client.Element el = (com.google.gwt.user.client.Element) element;
			DOM.sinkEvents(el,Event.ONCLICK);
			DOM.setEventListener(el, new EventListener(){
				public void onBrowserEvent(Event event) {
					showAboutDialog(true);
				}
			});
			element.addClassName(PlayerLayout.BUTTON_ON_STYLE);
		}
		
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setSize("100%", "100%");
		
		title = new Label();
		element = layout.getElement(PlayerLayout.TITLE);
		if (element==null) {
			title.setStyleName(PlayerStyles.BOOK_TITLE);
			verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			verticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
			verticalPanel.add(title);
			verticalPanel.setCellHeight(title,"1%");
			verticalPanel.setCellWidth(title,"100%");
		} else {
			element.appendChild(title.getElement());
		}
		authors = new Label();
		element = layout.getElement(PlayerLayout.AUTHORS);
		if (element==null) {
			authors.setStyleName(PlayerStyles.BOOK_AUTHOR);
			verticalPanel.add(authors);
			verticalPanel.setCellHeight(authors,"99%");
			verticalPanel.setCellWidth(authors,"100%");
		} else {
			element.appendChild(authors.getElement());
		}

		if (verticalPanel.getWidgetCount()>0) {
			horizontalPanel.add(verticalPanel);
			horizontalPanel.setCellHeight(verticalPanel,"100%");
			horizontalPanel.setCellWidth(verticalPanel,"99%");
		}
		if (horizontalPanel.getWidgetCount()>0) {
			mainPanel.add(horizontalPanel);
			mainPanel.setCellHeight(horizontalPanel,"1%");
			mainPanel.setCellWidth(horizontalPanel,"100%");
		}
		
		showLoading(false);
		
		RootPanel rootPanel = RootPanel.get(PlayerLayout.MAIN);
		if (rootPanel==null) {
			rootPanel = RootPanel.get();
		} else {
			while (rootPanel.getElement().getChildCount()>0){
				rootPanel.getElement().removeChild(rootPanel.getElement().getFirstChild());
			}
		}
		rootPanel.setSize("100%", "100%");
		rootPanel.add(mainPanel);
		
		if (GWT.isScript()==false) {
			feedbackURL = "/feedback.do";
		} else {
//			feedbackURL = "http://iambookmaster.appspot.com/feedback.do";
//			feedbackURL = "http://www.iambookmaster.com/remote/feedback.do";
			feedbackURL = "http://localhost:8080/iambookmaster/remote/feedback.do";
		}
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				load();
			}
			
		});
//		addAd();
	}

	private void showLoading(boolean replace) {
		if (replace) {
			mainPanel.remove(mainPanel.getWidgetCount()-1);
		}
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		Image image = new Image(PlayImages.LOADING);
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(image);
		horizontalPanel.add(new Label(AppLocale.getAppConstants().playerLoading()));
		mainPanel.add(horizontalPanel);
	}

	private native void check4updates(String url,Viewer panel)/*-{
		$doc.iambookmaster = function(status) {panel.@com.iambookmaster.client.viewer.Viewer::statusCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(status);;}
		var headID = $doc.getElementsByTagName("head")[0];         
		var newScript = $doc.createElement('script');
		newScript.type = 'text/javascript';
		newScript.src = url;
		headID.appendChild(newScript);		
	}-*/;

	/**
	 * This methos is called from JavaScript
	 * @param status
	 */
	@SuppressWarnings("unused")
	private void statusCallback(JavaScriptObject status){
		int maj = JSONParser.getInstance().propertyNoCheckInt(status, "version");
		if (maj>model.getSettings().getGameVersion()) {
			//new version available
			if (aboutPanel == null) {
				//show dialog
				showAboutDialog(true);
			}
			aboutPanel.newVersionAvailable(maj);
		}
		JSONParser parser = JSONParser.getInstance();
		feedbackURL = parser.propertyNoCheckString(status, "feedback");
		otherBooks = parser.propertyNoCheckBoolean(status, "otherBooks");
		if (feedbackURL != null && player != null) {
			player.enableFeedback(feedbackURL, otherBooks);
		}
		String allBooksURL = parser.propertyNoCheckString(status, "allBooks");
		if (allBooksURL != null && player != null) {
			player.setAllBooksURL(allBooksURL);
		}
	}
	
	private void showAboutDialog(boolean back) {
		if (aboutPanel == null) {
			aboutPanel = new ViewerAboutPanel(model) {
				@Override
				protected void onClose() {
					if (player==null) {
						showLoading(true);
					} else {
						player.restore();
					}
				}
			};
		}
		aboutPanel.setBack(back);
		if (player == null) {
			addToMain(aboutPanel);
		} else {
			player.show(aboutPanel);
		}
	}

	private void addToMain(Widget panel) {
		mainPanel.remove(mainPanel.getWidgetCount()-1);
		mainPanel.add(panel);
		mainPanel.setCellWidth(panel,"100%");
		mainPanel.setCellHeight(panel,"100%");
	}

	private static native JavaScriptObject getBookJS() /*-{
		if ($wnd.iambookmaster) {
			return $wnd.iambookmaster;
		} else {
			return null;
		}
	}-*/;

	private static native Element findBookFrame() /*-{
		var frm = $doc.getElementById('__iambookmaster');
		try {
			return frm.contentWindow.document.body;
		} catch (e) {
			return null;
		}
	}-*/;

	protected void load() {
		try {
			JavaScriptObject book = getBookJS();
			if (book==null) {
				Element book2 = findBookFrame();
				if (book == null) {
					loadHTTP();
				} else {
					loadModel(JSONParser.eval(book2.getInnerText()));
				}
			} else {
				loadModel(book);
			}
		} catch (Exception e) {
			error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	protected void loadHTTP() {
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, "/book.txt");
	    try {
	        requestBuilder.sendRequest(null,new RequestCallback() {

				public void onError(Request request, Throwable exception) {
	        		error(exception.getMessage());
	        	}

	        	public void onResponseReceived(Request request, Response response) {
	        		if (response.getStatusCode()>=400) {
	        			error(response.getText());
	        		} else {
		        		try {
							loadModel(JSONParser.eval(response.getText()));
						} catch (Exception e) {
							error(e.getMessage());
							e.printStackTrace();
						}
	        		}
	        		
	        	}
	        });
	      } catch (Exception e) {
	    	  error(e.getMessage());
	    	  e.printStackTrace();
	      }
	}

	protected void loadModel(JavaScriptObject modelJS) throws JSONException {
		Model mod = new Model(AppLocale.getAppConstants(),AppLocale.getAppMessages());
		mod.restore(modelJS,JSONParser.getInstance());
		this.model = mod;
		//success
		if (model.getVersion()>model.getModelVersion()) {
			showOldVesrion(model);
		}
		Document.get().setTitle("\""+model.getSettings().getBookTitle()+"\" "+model.getSettings().getBookAuthors());
		title.setText(model.getSettings().getBookTitle());
		authors.setText(model.getSettings().getBookAuthors());
		contentPlayer = new ContentPlayerImpl();
		model.setContentPlayer(contentPlayer);
		
		player = new Player(model,null,layout);
		if (model.getSettings().isShowAboutOnStart()==false) {
			player.start();
		}
		ViewerLayout.applySize(mainPanel, player);
		mainPanel.remove(mainPanel.getWidgetCount()-1);
//		mainPanel.setBorderWidth(5);
		mainPanel.add(player);
		mainPanel.setCellHeight(player,"99%");
		mainPanel.setCellWidth(player,"100%");
		
		Window.addWindowClosingHandler(new ClosingHandler() {
			public void onWindowClosing(ClosingEvent event) {
			}
			
		});
		
		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				player.onResize();
			}
		});
		
		player.onResize();
		//encoded version of "http://www.iambookmaster.com/remote/player.js?g="
//		String url = Base64Coder.decodeString("aHR0cDovL3d3dy5pYW1ib29rbWFzdGVyLmNvbS9yZW1vdGUvcGxheWVyLmpzP2c9");
		String url = "http://localhost:8080/iambookmaster/remote/player.js?g=";
		check4updates(url+model.getGameId()+"&v="+model.getSettings().getGameVersion()+"&n="+Base64Coder.encodeString(JSONBuilder.encodeUTF2Esc(model.getSettings().getBookTitle()))+"&time="+new Date().getTime(),Viewer.this);
		if (feedbackURL != null) {
			player.enableFeedback(feedbackURL, otherBooks);
		}
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				player.activate();
				if (model.getSettings().isShowAboutOnStart()) {
					showAboutDialog(false);
				}
			}
		});
		
	}

	private void showOldVesrion(Model model) {
		OldVersionPanel panel = new OldVersionPanel(model){
			@Override
			protected void onClose() {
				player.restore();
			}
		};
		player.show(panel);
	}

	protected void error(String message) {
		mainPanel.remove(0);
		Label label = new Label(AppLocale.getAppConstants().playerErrorLoading());
		mainPanel.add(label);
		if (message==null) {
			label = new Label("NullPointerException");
		} else if (message.length()>300) {
			label = new Label(message.substring(0,300));
		} else {
			label = new Label(message);
		}
		label.setStyleName(PlayerStyles.ERROR);
		mainPanel.add(label);
	}
	
//	public void addAd() {
//		Element parent = DOM.getElementById(PlayerLayout.GOOGLE_ADD);
//		if (parent != null) {
//			while (DOM.getChildCount((com.google.gwt.user.client.Element) parent)>0) {
//				parent.removeChild(parent.getFirstChild());
//			}
//			//<script type="text/javascript"><!--google_ad_client = "pub-5970515493173285";/* 234x60, created 2/7/10 */google_ad_slot = "2583934471";google_ad_width = 234;google_ad_height = 60;//--></script><script type="text/javascript"	src="http://pagead2.googlesyndication.com/pagead/show_ads.js"></script>
//			Element add = DOM.createDiv();
//			add.setInnerHTML(Base64Coder.decodeString("PHNjcmlwdCB0eXBlPSJ0ZXh0L2phdmFzY3JpcHQiPjwhLS1nb29nbGVfYWRfY2xpZW50ID0gInB1Yi01OTcwNTE1NDkzMTczMjg1IjsvKiAyMzR4NjAsIGNyZWF0ZWQgMi83LzEwICovZ29vZ2xlX2FkX3Nsb3QgPSAiMjU4MzkzNDQ3MSI7Z29vZ2xlX2FkX3dpZHRoID0gMjM0O2dvb2dsZV9hZF9oZWlnaHQgPSA2MDsvLy0tPjwvc2NyaXB0PjxzY3JpcHQgdHlwZT0idGV4dC9qYXZhc2NyaXB0IglzcmM9Imh0dHA6Ly9wYWdlYWQyLmdvb2dsZXN5bmRpY2F0aW9uLmNvbS9wYWdlYWQvc2hvd19hZHMuanMiPjwvc2NyaXB0Pg=="));
//			add.getStyle().setWidth(234, Unit.PX);
//			add.getStyle().setHeight(60, Unit.PX);
//			parent.appendChild(add);
//		}
//	}
	
}
