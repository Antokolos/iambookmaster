package com.iambookmaster.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.common.EditorTab;
import com.iambookmaster.client.common.MaskPanel;
import com.iambookmaster.client.common.XMLBuilder;
import com.iambookmaster.client.editor.ModelPersist;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.remote.RemotePanel;
import com.iambookmaster.client.remote.RemotePanelListener;
import com.iambookmaster.client.remote.RemoteRequest;
/**
 * About panel
 * @author ggadyatskiy
 */
public class ServerExchangePanel extends VerticalPanel implements EditorTab {

	private AppConstants appConstants = AppLocale.getAppConstants();

	public static final String FUNCTION_LOGIN = "login.do";
	public static final String FUNCTION_PUBLISH = "publish.do";	
	private static final String FUNCTION_SAVE = "save.do";
	private static final String FUNCTION_UPLOAD = "upload.do";

	public static final String FIELD_MODEL = "model";
	public static final String FIELD_OPERATION = "operation";
	public static final String FIELD_CODE = "code";
	public static final String FIELD_DATA = "data";
	public static final String FIELD_MESSAGE = "message";
	public static final String OPERATION_RE_EXPORT = "reexport";
	public static final String OPERATION_CREATE = "create";

	public static final int ERROR_NO_BOOK_ID = -1;
	public static final int ERROR_INVALID_BOOK_ID = -2;
	public static final int ERROR_INVALID_BOOK_VERSION_ID = -3;
	public static final int ERROR_NO_LOGIN = -4;
	public static final int ERROR_BOOK_NOT_FOUND = -5;
	public static final int ERROR_NOT_OWNER = -6;
	public static final int ERROR_BOOK_VERSION_NOT_FOUND = -7;
	public static final int ERROR_UNKNOWN = 0;
	public static final int ERROR_INVALID_MODEL = -8;
	public static final int LOAD_OK = 1;
	
	
	private Image statusImage;
	private Label statusText;
	private RemotePanel remotePanel;
	private boolean requestPerfomed;
	private JavaScriptObject loader;
	
	public String getServerURL() {
		return remotePanel.getServerUrl();
	}

	public void setServerURL(String serverURL) {
		if (GWT.isScript()) {
			remotePanel.setServerUrl(serverURL);
		}
	}

	public ServerExchangePanel() {
		setSize("100%", "100%");
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(5);
		horizontalPanel.setSize("100%", "100%");
		statusImage = new Image(Images.SERVER_ONLINE);
		horizontalPanel.add(statusImage);
		horizontalPanel.setCellHeight(statusImage,"100%");
		horizontalPanel.setCellWidth(statusImage,"1%");
		statusText = new Label();
		horizontalPanel.add(statusText);
		horizontalPanel.setCellHeight(statusText,"100%");
		horizontalPanel.setCellWidth(statusText,"99%");
		add(horizontalPanel);
		setCellHeight(horizontalPanel,"1%");
		setCellWidth(horizontalPanel,"100%");
		
		String url;
		if (GWT.isScript()) {
			//for real
			url = "http://localhost:8080/iambookmaster/remote";				
//			url = "http://iambookmaster.com/remote/";
		} else {
			//for debug
			url = GWT.getHostPageBaseURL();
		}
		remotePanel = new RemotePanel(url,new RemotePanelListener() {

			public void beforeRequest() {
				requestPerfomed = true;
				statusText.setText(appConstants.serverExchangeProcess());
			}

			public void error(String responce) {
				statusText.setText(responce);
				statusImage.setUrl(Images.SERVER_ERROR);
				statusImage.setTitle(responce);
			}

			public void success() {
				statusImage.setUrl(Images.SERVER_ONLINE);
				if (uploadCounter<0) {
					statusImage.setTitle(appConstants.serverExchangeDoneTitle());
					statusText.setText(appConstants.serverExchangeSuccessful());
				} else if (uploadNextPortionOfModel(false)){
					//done
					statusImage.setTitle(appConstants.serverExchangeUploadingTitle());
					statusText.setText(appConstants.serverExchangeUploading());
				} else {
					remotePanel.perform(uploadRequest);
					uploadCounter = -1;
				}
			}

			public void load(final String url) {
				MaskPanel.show();
				DeferredCommand.addCommand(new Command() {
					public void execute() {
						loader = loadModel(loader, url);
					}
				});
			}

			public void serverReplied(String answer) {
				statusImage.setUrl(Images.SERVER_ONLINE);
			}
			
		});
		add(remotePanel);
		setCellHeight(remotePanel,"99%");
		setCellWidth(remotePanel,"100%");
	}
	
	private native JavaScriptObject loadModel(JavaScriptObject loader,String url) /*-{
		var headID = $doc.getElementsByTagName("head")[0];         
		if (loader!=null) {
			headID.removeChild(loader);		
		}
		var newScript = $doc.createElement('script');
		newScript.type = 'text/javascript';
		newScript.src = url;
		headID.appendChild(newScript);		
		return newScript;
	}-*/;
	
	public void activate() {
		if (requestPerfomed==false) {
			performLogin();
		}
	}
	
	public void deactivate() {
	}
	
	public void close() {
	}
	
	/**
	 * Login to the server
	 */
	public void performLogin() {
		RemoteRequest request = new RemoteRequest(FUNCTION_LOGIN);
		request.addParameter(RemotePanel.LOCALE_IN_REQUEST, appConstants.locale());
		remotePanel.perform(request);
	}

	private String uploadModel;
	private int uploadCounter=-1;
	private RemoteRequest uploadRequest;
	private String uploadModelId;
	/**
	 * Publish book to the server
	 */
	public void performPublishing(ModelPersist model,boolean reExport) {
		XMLBuilder builder = XMLBuilder.getStartInstance();
		model.toJSON(Model.EXPORT_ALL, builder);
		uploadModel = builder.toXML();
		uploadModelId = model.getGameId();
		uploadCounter = 0;
		uploadRequest = new RemoteRequest(FUNCTION_PUBLISH,false);
		uploadRequest.addParameter(FIELD_DATA, model.getGameId());
		uploadRequest.addParameter(RemotePanel.LOCALE_IN_REQUEST, appConstants.locale());
		if (reExport) {
			uploadRequest.addParameter(FIELD_OPERATION,OPERATION_RE_EXPORT);
		} else {
			uploadRequest.addParameter(FIELD_OPERATION,OPERATION_CREATE);
		}
		uploadNextPortionOfModel(true);
	}

	public void performSave(ModelPersist model) {
		XMLBuilder builder = XMLBuilder.getStartInstance();
		model.toJSON(Model.EXPORT_ALL, builder);
		uploadModel = builder.toXML();
		uploadCounter = 0;
		uploadModelId = model.getGameId();
		uploadRequest = new RemoteRequest(FUNCTION_SAVE,true);
		uploadRequest.addParameter(RemotePanel.LOCALE_IN_REQUEST, appConstants.locale());
		uploadRequest.addParameter(FIELD_DATA, model.getGameId());
		uploadNextPortionOfModel(true);
	}

	private boolean uploadNextPortionOfModel(boolean first) {
		if (uploadCounter<uploadModel.length()) {
			RemoteRequest request = new RemoteRequest(FUNCTION_UPLOAD,true);
			int to = uploadCounter+15000;
			if (to>uploadModel.length()) {
				request.addParameter(FIELD_MODEL,uploadModel.substring(uploadCounter));
				uploadCounter=uploadModel.length();
			} else {
				request.addParameter(FIELD_MODEL,uploadModel.substring(uploadCounter,to));
				uploadCounter=to;
			}
			request.addParameter(FIELD_DATA,uploadModelId);
			request.addParameter(RemotePanel.LOCALE_IN_REQUEST, appConstants.locale());
			if (first) {
				request.addParameter(FIELD_CODE,"first");
			}
			remotePanel.perform(request);
			return true;
		} else {
			return false;
		}
	}

	public void done() {
		remotePanel.done();
	} 

}
