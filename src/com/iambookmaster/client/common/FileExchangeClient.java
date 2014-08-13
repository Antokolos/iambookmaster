package com.iambookmaster.client.common;

import java.io.IOException;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;

public class FileExchangeClient {

	private static AppConstants appConstants = AppLocale.getAppConstants();
	
	private native String selectFileApplet(JavaScriptObject app,boolean save,String title)/*-{
		return app.selectfile(title,save);
	}-*/;
	
	public String selectFile(String title,boolean save){ 
		if (checkApplet()==false) {
			return null;
		}
		JavaScriptObject app = getApplet();
		String res = selectFileApplet(app,false,title);
		if (res.equals("OK")) {
			return getData(app);
		} else {
			return null;
		}
	}

	private native String loadFileApplet(String file,JavaScriptObject app)/*-{
		return app.readfile(file);
	}-*/;

	public String loadFile(String title) {
		try {
			if (checkApplet()==false) {
				return null;
			}
			JavaScriptObject app = getApplet();
			String res = selectFileApplet(app,false,title);
			if (res.equals("OK")) {
				String file = getData(app);
				res = loadFileApplet(file, app);
				if (res.equals("OK")) {
					return getData(app);
				}
			} else if (res != null){
				Window.alert(res);
			}
		} catch (Exception e) {
			Window.alert(e.getMessage());
		}
		return null;
	}

	public static boolean checkApplet() {
		if (_checkApplet()==false) {
			return false;
		}
		return true;
	}
	
	private static native boolean _checkApplet()/*-{
		var app = $doc.applets["fileExchangeApplet"];
		try {
			app.getBuffer();
			return true;
		} catch (e) {
			return false;
		}
	}-*/;
	
	private static native JavaScriptObject getApplet()/*-{
		return $doc.applets["fileExchangeApplet"];
	}-*/;

	private native String getData(JavaScriptObject app)/*-{
		return app.getBuffer();
	}-*/;
	
	private native String getData64(JavaScriptObject app)/*-{
		return app.getBuffer64();
	}-*/;

	private native void setData(JavaScriptObject app,String data)/*-{
		return app.setBuffer(data);
	}-*/;
	/**
	 * Write file for non-XUL browsers
	 * @param data
	 * @return
	 */
	public void saveFile(String data,String title) {
		try {
			if (checkApplet()==false) {
				return;
			}
			JavaScriptObject app = getApplet();
			String file = selectFileApplet(app,true,title);
			if (file.equals("OK")) {
				file = getData(app);
				setData(app,data);
				saveFileApplet(app,file);
			} else if (file != null){
				Window.alert(file);
			}
		} catch (Exception e) {
			Window.alert(e.getMessage());
		}
	}
	
	private native String saveFileApplet(JavaScriptObject app,String file)/*-{
		return app.writefile(file);
	}-*/;

	public static void init(boolean skipJava) {
		JavaScriptObject obj = getJavaDeployer();
		if (obj==null || isJREAvailable(obj) || skipJava) {
			//ok
		} else if (Window.confirm(appConstants.installJRE())) {
			//install
			installJRE(obj);
		}
	}
	private native static void installJRE(JavaScriptObject deployJava) /*-{
		try {
			deployJava.installLatestJRE();
		} catch (e) {
		}
	}-*/;


	private native static boolean isJREAvailable(JavaScriptObject deployJava) /*-{
		try {
			return deployJava.getJREs().length>0;
		} catch (e) {
			return false;
		}
	}-*/;

	private native static JavaScriptObject getJavaDeployer() /*-{
		return $wnd.deployJava;
	}-*/;

	public String loadFileByPath(String name) {
		if (checkApplet()==false) {
			return null;
		}
		JavaScriptObject app = getApplet();
		String res = loadFileApplet(name,app);
		if (res.equals("OK")) {
			return Base64Coder.decodeString(getData64(app));
		} else {
			return null;
		}
	}
	
	private native String _selectFolder(JavaScriptObject app,String title,String button,String defFolder)/*-{
		return app.selectFolder(title,button,defFolder);
	}-*/;

	public String selectFolder(String title,String button) {
		if (checkApplet()==false) {
			return null;
		}
		JavaScriptObject app = getApplet();
		String res = _selectFolder(app,title,button,null);
		if (res.equals("OK")) {
			return getData(app);
		} else {
			return null;
		}
	}

	public String[] selectFilesByExtention(String path, String extension) {
		if (checkApplet()==false) {
			return null;
		}
		JavaScriptObject app = getApplet();
		String res = _selectFilesByExtention(app,path,extension);
		if (res.equals("OK")) {
			res = getData(app);
			if (res.length()==0) {
				return new String[0];
			} else {
				return res.split("\n");
			}
		} else {
			return null;
		}
	}

	private native String _selectFilesByExtention(JavaScriptObject app, String path, String extension)/*-{
		return app.selectFilesByExtention(path,extension);
	}-*/;

	public void writeFile(String fileName, String data) throws IOException {
		if (checkApplet()==false) {
			throw new IOException("No Applet");
		}
		JavaScriptObject app = getApplet();
		setData(app, data);
		String res = saveFileApplet(app,fileName);
		if (res.equals("OK")==false) {
			throw new IOException("Ошибка записи "+res);
		}
	}

}
