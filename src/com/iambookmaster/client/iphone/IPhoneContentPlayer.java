package com.iambookmaster.client.iphone;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Frame;
import com.iambookmaster.client.model.ContentPlayer;

public class IPhoneContentPlayer implements ContentPlayer {

	public Frame frame;
	public IPhoneContentPlayer() {
		frame = new Frame();
		frame.setVisible(false);
		Document.get().getBody().appendChild(frame.getElement());
		
	}
	public void openURL(String url) {
		sendCommand("url",escape(url),false);
	}
	
    private native String escape(String s)/*-{
		return escape(s);
	}-*/;

	public void playBackgroundSound(String url) {
		sendCommand("music",url,true);
	}
	
	private void sendCommand(String command,String file,boolean extractExtention) {
		StringBuilder builder = new StringBuilder("command");
		builder.append("://");
		builder.append(command);
		if (file != null) {
			builder.append('/');
			if (extractExtention) {
				int i = file.lastIndexOf('.');
				if (i<0) {
					builder.append(file);
				} else {
					builder.append(file.substring(0,i));
					builder.append('/');
					builder.append(file.substring(i+1));
				}
			} else {
				builder.append(file);
			}
		}
		frame.setUrl(builder.toString());
		
	}

	public void playSound(String url, boolean loop) {
		sendCommand("sound",url,true);
	}

	public void stopSound() {
		sendCommand("stopSound",null,false);
	}
	public void stopBackgroundSound() {
		sendCommand("stopMusic",null,false);
	}

}
