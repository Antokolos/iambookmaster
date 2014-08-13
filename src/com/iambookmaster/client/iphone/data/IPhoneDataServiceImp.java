package com.iambookmaster.client.iphone.data;

import java.util.ArrayList;
import java.util.Comparator;

import com.google.code.gwt.database.client.service.Callback;
import com.google.code.gwt.database.client.service.DataServiceException;
import com.google.code.gwt.database.client.service.ListCallback;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.code.gwt.database.client.service.VoidCallback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.iambookmaster.client.common.Base64Coder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;

public class IPhoneDataServiceImp extends IPhoneDataService{

	private static final AppConstants appConstants = AppLocale.getAppConstants();

	private static final Object[] PARAMS = new Object[0];
	
	public Frame frame;
	private Callback callback;
	private boolean linkedVersionPresent;
	private boolean cracked;
	private boolean inAppAvailable;

	private StringBuilder buffer; 
	
	public IPhoneDataServiceImp() {
		init();
		frame = new Frame();
		frame.setVisible(false);
		Document.get().getBody().appendChild(frame.getElement());
	}
	
	private native void init()/*-{
		var self = this;
		$wnd.iambm = function(data,full,save,inapp) {
			self.@com.iambookmaster.client.iphone.data.IPhoneDataServiceImp::restore(Ljava/lang/String;ZZZ)(data,full,save,inapp);
		};
		$wnd.iambme = function(data,suggestion) {
			self.@com.iambookmaster.client.iphone.data.IPhoneDataServiceImp::error(Ljava/lang/String;Ljava/lang/String;)(data,suggestion);
		};
		$wnd.iambml = function(data) {
			self.@com.iambookmaster.client.iphone.data.IPhoneDataServiceImp::list(Ljava/lang/String;)(data);
		};
		$wnd.iambms = function(data,type) {
			self.@com.iambookmaster.client.iphone.data.IPhoneDataServiceImp::single(Ljava/lang/String;I)(data,type);
		};
		$wnd.iambmd = function() {
			self.@com.iambookmaster.client.iphone.data.IPhoneDataServiceImp::voidCallback()();
		};
		$wnd.iambmp = function(data,code) {
			if (data) {
				self.@com.iambookmaster.client.iphone.data.IPhoneDataServiceImp::price(Ljava/lang/String;)(data);
			} else {
				self.@com.iambookmaster.client.iphone.data.IPhoneDataServiceImp::noprice(I)(code);
			}
		};
	}-*/;
	
	/**
	 * This method is called from JavaScript
	 */
	private void error(String error,String suggestion){
		if (callback != null) {
			callback.onFailure(new DataServiceException(error));
		}

	}

	/**
	 * This method is called from JavaScript
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void price(String price){
		if (callback instanceof ScalarCallback) {
			ScalarCallback callback = (ScalarCallback) this.callback;
			callback.onSuccess(price);
		}
	}

	/**
	 * This method is called from JavaScript
	 */
	private void noprice(int code){
		if (callback != null) {
			callback.onFailure(new DataServiceException(appConstants.iphoneCannotConnectAppStore(),code,null,PARAMS));
		}
	}

	/**
	 * This method is called from JavaScript
	 */
	private void voidCallback(){
//		Window.alert("Success");
		if (callback instanceof VoidCallback) {
			VoidCallback callback = (VoidCallback) this.callback;
			callback.onSuccess();
		}

	}

	/**
	 * This method is called from JavaScript
	 */
	private void restore(String status,boolean full,boolean cracked,boolean inAppAvailable){
		linkedVersionPresent = full;
		this.cracked = cracked;
		this.inAppAvailable = inAppAvailable;
		if (status==null || status.length()==0) {
			callback.onFailure(new DataServiceException("No state"));
		} else if (callback instanceof ScalarCallback) {
			@SuppressWarnings("unchecked")
			ScalarCallback<String> scalarCallback = (ScalarCallback<String>) callback;
			scalarCallback.onSuccess(status);
		}
	}

	public boolean isLinkedVersionPresent() {
		return linkedVersionPresent;
	}
	
	public boolean isCracked() {
		return cracked;
	}

	public boolean isInAppAvailable() {
		return inAppAvailable;
	}

	public void loadLastState(Model model, ScalarCallback<String> callback) {
		this.callback = callback;
		frame.setUrl(generateCommandURL("restore",null));
	}

	@Override
	public void storeState(String data) {
		frame.setUrl(generateCommandURL("state",data));
	}

	@Override
	public void donate(VoidCallback callback) {
		this.callback = callback;
		frame.setUrl(generateCommandURL("donate",null));
	}

	@Override
	public void calculateDonate(ScalarCallback<String> callback) {
		this.callback = callback;
		frame.setUrl(generateCommandURL("price",null));
	}

	@Override
	public void cancelRequest() {
		this.callback = null;
	}
	
	@Override
	public void removeSplashScreen() {
		callback = null;
		frame.setUrl(generateCommandURL("show",null));
	}

	@Override
	public void selectAvailableFiles(String exention, ListCallback<IPhoneFileBean> callback) {
		this.callback = callback;
		frame.setUrl(generateCommandURL("files",exention));
	}
	
	@Override
	public void loadSingleFile(String name, ScalarCallback<String> callback) {
		this.callback = callback;
		this.buffer = null;
		frame.setUrl(generateCommandURL("load",name));
	}

	/**
	 * This method is called from JavaScript
	 */
	@SuppressWarnings({ "unchecked"})
	private void list(String json){
		try {
			if (callback instanceof ListCallback) {
				ListCallback<IPhoneFileBean> callback = (ListCallback<IPhoneFileBean>) this.callback;
				JSONParser parser = JSONParser.getInstance();
				JavaScriptObject list = JSONParser.evalArray(json);
				int l = parser.length(list);
				ArrayList<IPhoneFileBean> res = new ArrayList<IPhoneFileBean>(l);
				for (int i = 0; i < l; i++) {
					res.add((IPhoneFileBean)parser.getRow(list, i));
				}
				java.util.Collections.sort(res,new Comparator<IPhoneFileBean>() {
					public int compare(IPhoneFileBean o1, IPhoneFileBean o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
				callback.onSuccess(res);
			} else if (callback != null){
				callback.onFailure(new DataServiceException("Incorrect callback"));
			}
		} catch (Throwable e) {
			sendError(e);
		}
	}

	/**
	 * This method is called from JavaScript
	 */
	@SuppressWarnings({ "unchecked"})
	private void single(String data,int type){
		try {
			if (callback instanceof ScalarCallback) {
				ScalarCallback<String> callback = (ScalarCallback<String>) this.callback;
					switch (type) {
					case 1:
						if (buffer==null) {
							callback.onSuccess(Base64Coder.decode(data,"UTF-8"));
						} else {
							buffer.append(Base64Coder.decode(data,"UTF-8"));
							callback.onSuccess(buffer.toString());
							buffer = null;
						}
						break;
					case 2:
						if (buffer==null) {
							callback.onSuccess(Base64Coder.decodeString(data));
						} else {
							buffer.append(Base64Coder.decodeString(data));
							callback.onSuccess(buffer.toString());
							buffer = null;
						}
						break;
					case 3:
						//add data to buffer
						if (buffer==null) {
							buffer = new StringBuilder(Base64Coder.decode(data,"UTF-8"));
						} else {
							buffer.append(Base64Coder.decode(data,"UTF-8"));
						}
						break;
					case 4:
						//add data to buffer
						if (buffer==null) {
							buffer = new StringBuilder(Base64Coder.decodeString(data));
						} else {
							buffer.append(Base64Coder.decodeString(data));
						}
						break;
					default:
						callback.onSuccess(data);
						break;
					}
			} else if (callback != null){
				buffer = null;
				callback.onFailure(new DataServiceException("Incorrect callback"));
			}
		} catch (Throwable e) {
			buffer = null;
			sendError(e);
		}
	}

	private void sendError(Throwable e) {
		e.printStackTrace();
		if (callback==null) {
			Window.alert("ERROR: "+e.getMessage());
		} else {
			callback.onFailure(new DataServiceException("ERROR: "+e.getMessage()));
		}
	}

	@Override
	public void storeState(String state, String fileName, VoidCallback callback) {
		this.callback = callback;
		int i = fileName.lastIndexOf('/');
		if (i>0) {
			fileName = fileName.substring(i+1);
		}
		frame.setUrl(generateCommandURL("save",fileName+'/'+state));
	}

}
