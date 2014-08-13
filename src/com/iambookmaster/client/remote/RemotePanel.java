package com.iambookmaster.client.remote;

import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.common.Base64Coder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;

public class RemotePanel extends VerticalPanel {

	public static final String CODE_REDIRECT = "redirect";
	public static final String CODE_SUCCESS = "success";
	public static final String CODE_ERROR = "error";
	public static final String CODE_LOAD = "load";

	public static final String FIELD_ANSWER_CODE = "c";
	public static final String FIELD_URL = "u";
	public static final String FIELD_CALLBACK = "c";
	public static final String FIELD_ERROR_SHORT = "e";
	public static final String FIELD_DESCRIPTION = "d";
	public static final String FIELD_WAIT = "w";
	
	private static int COUNTER;
	private static final AppConstants appConstants = AppLocale.getAppConstants();
//	private static final String GO_BACK = GWT.getHostPageBaseURL()+appConstants.remoteBackPage();
	private static final String PROCESS = GWT.getHostPageBaseURL()+appConstants.remoteProgressPage();
//	private static final String CALLBACK = GWT.getHostPageBaseURL()+appConstants.remoteSuccessPage();
	private static String CALLBACK;
	private static String CALLBACK_BASE64;
	public static final String LOCALE_IN_REQUEST = "locale";
	
	private Timer timeoutChecher;
	private RemotePanelListener listener;
	private String serverUrl;
	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	private RemoteRequest currentRequest;
	private NamedFrame frame;
	private FormPanel form;
	private FlowPanel formElements;
	private String framName;
	private HashMap<String,String> urlMapping = new HashMap<String, String>(5);
	public RemotePanel(String serverUrl,RemotePanelListener listener) {
		this.listener = listener;
		this.serverUrl = serverUrl;
		CALLBACK = GWT.getHostPageBaseURL()+appConstants.remoteProgressPage();
		if (CALLBACK.startsWith("file:///")) {
			CALLBACK = CALLBACK.replace("file://", "file://///");
		}
		CALLBACK_BASE64 = Base64Coder.encodeString(CALLBACK);
		setSize("100%", "100%");
		framName = "RemotePanel"+String.valueOf(++COUNTER);
		frame = new NamedFrame(framName);
		frame.setUrl(CALLBACK);
		frame.setSize("100%", "100%");
		add(frame);
		setCellHeight(frame, "100%");
		setCellWidth(frame, "100%");
		form = new FormPanel(frame);
		form.setVisible(false);
		add(form);
		setCellHeight(form, "1px");
		setCellWidth(form, "100%");
		formElements = new FlowPanel();
		form.add(formElements);
	}
	
	
	public void refresh() {
		perform(currentRequest);
	}

	private Command perfomCommand = new Command() {
		public void execute() {
			setFrameWindowName(frame.getElement(), framName);
			formElements.clear();
			form.setAction(currentRequest.getUrl());
			form.setMethod(currentRequest.isPost() ? FormPanel.METHOD_POST : FormPanel.METHOD_GET);
			form.setEncoding(FormPanel.ENCODING_URLENCODED);
			final HashMap<String,String> params = currentRequest.getParameters();
			if (params != null && params.size()>0) {
				addParameters(params,CALLBACK_BASE64);
			} else {
				addParameter(FIELD_CALLBACK,CALLBACK_BASE64);
			}
			if (currentRequest.isWaitForAnswer()) {
				//wait for answer
				timeoutChecher = new Timer() {
					@Override
					public void run() {
						String name;
						try {
							name = getFrameWindowName(frame.getElement());
							if (framName.equals(name)) {
								//nothing
							} else if (name.length()>0) {
								//found
								timeoutChecher.cancel();
								parseAnswer(name);
							}
						} catch (Throwable e) {
						}
					}
				};
				timeoutChecher.scheduleRepeating(300);
			} else {
				listener.success();
			}
			form.submit();
		}
		
	};
	public void perform(RemoteRequest request) {
		if (timeoutChecher != null) {
			timeoutChecher.cancel();
		}
		listener.beforeRequest();
		currentRequest = request;
		if (request.getUrl() == null) {
			if (urlMapping.containsKey(request.getFunction())) {
				request.setUrl(urlMapping.get(request.getFunction()));
			} else {
				request.setUrl(serverUrl+request.getFunction());
			}
		}
		frame.setUrl(PROCESS);
		new Timer() {
			public void run() {
				//wait for our local page is loaded
				String name = getFrameWindowName(frame.getElement());
				if (name.length()>0) {
					cancel();
					DeferredCommand.addCommand(perfomCommand);
				}
			}
		}.scheduleRepeating(300);
	}
	
	private void parseAnswer(String answer) {
//		Window.prompt("Answer", answer);
		listener.serverReplied(answer);
		JavaScriptObject source;
		String code;
		String url;
		JSONParser parser = JSONParser.getInstance();
		try {
			String base = Base64Coder.decodeString(answer); 
			source = JSONParser.eval(base);
			code = parser.propertyString(source, FIELD_ANSWER_CODE);
			url = parser.propertyNoCheckString(source, FIELD_URL);
		} catch (Exception e) {
			//wrong
			listener.error(answer);
			return;
		}
		if (CODE_REDIRECT.equals(code)) {
			currentRequest.setUrl(url);
			//remember redirect for future
			urlMapping.put(currentRequest.getFunction(), url);
			refresh();
		} else if (CODE_SUCCESS.equals(code)) {
			listener.success();
			if (url == null) {
				frame.setUrl(CALLBACK);
				printFullDescription(source);
			} else {
				frame.setUrl(PROCESS);
				final String callUrl = url;
				DeferredCommand.addCommand(new Command(){
					public void execute() {
						frame.setUrl(callUrl);
					}
				});
			}
		} else if (CODE_LOAD.equals(code)) {
			if (url == null) {
				frame.setUrl(CALLBACK);
				listener.error(AppLocale.getAppConstants().remoteCannotLoadModel());
			} else {
				listener.load(url);
				frame.setUrl(PROCESS);
			}
		} else if (CODE_ERROR.equals(code)) {
			listener.error(code);
			printFullDescription(source);
		} else {
			//other code - error
			listener.error(JSONParser.getInstance().propertyNoCheckString(source, FIELD_ERROR_SHORT));
			if (url == null) {
				frame.setUrl(CALLBACK);
				printFullDescription(source);
			} else {
				frame.setUrl(url);
			}
		}
	}
	
	private void printFullDescription(JavaScriptObject source) {
		final String fullDescription = JSONParser.getInstance().propertyNoCheckString(source, FIELD_DESCRIPTION);
		if (fullDescription != null) {
			new Timer() {
				public void run() {
					if (printFullDescription(frame.getElement(),fullDescription)) {
						cancel();
					} 
				}
			}.schedule(500);
		}
	}

	private native String getFrameWindowName(Element frame) /*-{
		 try {
	  		var doc = (frame.contentWindow || frame.contentDocument);
	  		return doc.window.name;
	    } catch (e) {
	    	return '';
	    }}-*/;
	
	private native boolean printFullDescription(Element frame,String text) /*-{
		try {
			var doc = (frame.contentWindow || frame.contentDocument);
		  	if (doc.document) {
		   		doc = doc.document;
			}
			doc.body.innerHTML=text;
			return true;
		} catch (e) {
			return false;
		}
	}-*/;

//	private native JavaScriptObject getFrameDocument(Element frame) /*-{
//		var doc = (frame.contentWindow || frame.contentDocument);
//	  	if (doc.document) {
//	   		doc = doc.document;
//		}
//		return doc;
//   	}-*/;
		
	private native void setFrameWindowName(Element frame,String name) /*-{
	 	try {
 			var doc = (frame.contentWindow || frame.contentDocument);
 			doc.window.name = name;
   		} catch (e) {
   		}
	}-*/;

	protected void addParameters(HashMap<String, String> params, String callback) {
		for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext();) {
			String name = iterator.next();
			addParameter(name,params.get(name));
		}
		addParameter(FIELD_CALLBACK,callback);
		
	}

	private void addParameter(String name, String value) {
		Hidden hidden = new Hidden(name);
		hidden.setValue(value);
		formElements.add(hidden);
		
	}


	public void done() {
		frame.setUrl(CALLBACK);
	}

}
