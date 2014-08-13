package com.iambookmaster.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
/**
 * Status panel for debugging
 * @author ggadyatskiy
 */
public class StatusPanel {

	private static StatusPanel instance;
	private static boolean active;
	
	private static final String STYLE_MESSAGE="status_message";
	private static final String STYLE_ALERT="status_alert";
	private static final String STYLE_ERROR="status_error";
	
    /**
     * Formatter for Date
     */
	private static final DateTimeFormat dateFormatter = DateTimeFormat.getFormat("dd/MMM/y HH:mm:ss");
	
	private static StatusPanel getInstance() {
		if (instance==null) {
			instance = new StatusPanel();
		}
		return instance;
	}
	
	public static void activate() {
		active = true;
	}
	
	public static boolean isActive() {
		return active;
	}

	public static void addMessage(String message) {
		getInstance().add(message,STYLE_MESSAGE,true);
	}

	public static void addError(String message) {
		if (GWT.isScript()) {
			getInstance().add(message,STYLE_ERROR,true);
		} else {
			//host mode
			System.out.print(dateFormatter.format(new Date()));
			System.out.print(' ');
			System.err.println(message);
		} 
	}

	public static void addAlert(String message) {
		getInstance().add(message,STYLE_ALERT,true);
	}

	private JavaScriptObject console; 
	private StatusPanel() {
	}
	
	private void add(String message,String style, boolean addTime) {
		if (active==false) {
			return;
		}
		StringBuffer buffer = new StringBuffer("<div class=\"");
		buffer.append(style);
		buffer.append("\">");
		if (addTime) {
			buffer.append(dateFormatter.format(new Date()));
			buffer.append(' ');
		}
		buffer.append(message);
		buffer.append("</div>");
		String text = buffer.toString();
		try {
			if (console==null || !writeToConsole(text)) {
				createConsole(text);
			}
		} finally {
		}
	}

	private native void createConsole(String html) /*-{
		var newwindow = window.open('#','_blank','height=400,width=400,scrollbars=1,resizable=1,location=1');
		this.@com.iambookmaster.client.StatusPanel::console = newwindow;
		newwindow.document.open();
		newwindow.document.write('<title>Status panel</title>');
		newwindow.document.write(html);
	}-*/; 
	
	private native boolean writeToConsole(String html) /*-{
		try {
			this.@com.iambookmaster.client.StatusPanel::console.document.write(html);
			return true;
		} catch (e) {
			return false;
		}
	}-*/;

	public static void addError(Throwable arg0) {
		if (GWT.isScript()==false) {
			//host mode
			arg0.printStackTrace();
			return;
		}
		if (active==false) {
			return;
		}
		StatusPanel panel = getInstance();
		panel.add(arg0.getMessage(),STYLE_ERROR,true);
		StackTraceElement[] elements = arg0.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			panel.add(elements[i].toString(),STYLE_ERROR,false);
		}
	} 
		
		

}
