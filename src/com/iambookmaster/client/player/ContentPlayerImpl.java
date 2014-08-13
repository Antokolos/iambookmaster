package com.iambookmaster.client.player;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.iambookmaster.client.common.JSONParser;

/*
		//other solution
		//http://code.google.com/p/gwt-voices/wiki/GettingStarted
		//
//	    Sound sound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG,url);
//	    sound.play();

		
		 <embed src="http://www.yoursite.com/music/musicfile.wav"
autostart="true" loop="false" hidden="true">
<embed src="/music/musicfile.wav" autostart="true" loop="false"
width="350" height="200">
		
 */
public class ContentPlayerImpl implements com.iambookmaster.client.model.ContentPlayer {

	/**
	 * IANA assigned media type <code>audio/basic</code> for RFC 2045/2046.
	 * Typical filename extensions include <code>.au</code> and
	 * <code>.snd</code>.
	 */
	private static final String MIME_TYPE_AUDIO_BASIC = "audio/basic";
	
	/**
	 * IANA assigned media type <code>audio/mpeg</code> for RFC 3003. Typical
	 * filename extensions include <code>.mp1</code>, <code>.mp2</code> and
	 * <code>.mp3</code>.
	 */
	private static final String MIME_TYPE_AUDIO_MPEG = "audio/mpeg";
	
	/**
	 * Using <code>audio/x-aiff</code> instead of the more popular, but
	 * unregistered, <code>audio/aiff</code>. Typical filename extension is
	 * <code>.aif</code>.
	 */
	private static final String MIME_TYPE_AUDIO_X_AIFF = "audio/x-aiff";
	
	/**
	 * Using <code>audio/x-midi</code> instead of the more popular, but
	 * unregistered, <code>audio/midi</code>. Typical filename extensions
	 * include <code>.mid</code> and <code>.midi</code>.
	 */
	private static final String MIME_TYPE_AUDIO_X_MIDI = "audio/x-midi";
	
	/**
	 * Using <code>audio/x-wav</code> instead of the more popular, but
	 * unregistered, <code>audio/wav</code>. Typical filename extension is
	 * <code>.wav</code>.
	 */
	private static final String MIME_TYPE_AUDIO_X_WAV = "audio/x-wav";
	  
	private Element soundPlayer;
	private Element backgroundPlayer;
//	private SoundController soundController = new SoundController();
	public ContentPlayerImpl() {
	}
	
	public void openURL(String url) {
		Window.open(url, "_blank", "");
	}

	public void playBackgroundSound(String url) {
		stopBackgroundSound();
		backgroundPlayer = DOM.createElement("EMBED");
		setMIMEAndStart(backgroundPlayer,url,true);
	}
	
	public void playSound(String url, boolean loop) {
		stopSound();
		soundPlayer = DOM.createElement("EMBED");
		setMIMEAndStart(soundPlayer,url,loop);
	}

	private void setMIMEAndStart(Element soundElement, String url,boolean loop) {
		//try to detect type of content by extention
		int i = url.lastIndexOf('.');
		if (i>0) {
			String ext = url.substring(i+1).toLowerCase();
			if (ext.startsWith("mp")) {
				soundElement.setAttribute("type",MIME_TYPE_AUDIO_MPEG);
			} else if (ext.startsWith("mid")) {
				soundElement.setAttribute("type",MIME_TYPE_AUDIO_X_MIDI);
			} else if (ext.startsWith("wav")) {
				soundElement.setAttribute("type",MIME_TYPE_AUDIO_X_WAV);
			} else if (ext.startsWith("au") || "snd".equals(ext)) {
				soundElement.setAttribute("type",MIME_TYPE_AUDIO_BASIC);
			} else if ("aif".equals(ext)) {
				soundElement.setAttribute("type",MIME_TYPE_AUDIO_X_AIFF);
			} else {
				soundElement.setAttribute("type",MIME_TYPE_AUDIO_MPEG);
			}
		} else { 
			soundElement.setAttribute("type",MIME_TYPE_AUDIO_MPEG);
		}
		soundElement.setAttribute("hidden","true");
		soundElement.setAttribute("src",JSONParser.escape(url));
		soundElement.setAttribute("loop",String.valueOf(loop));
		soundElement.setAttribute("autostart","true");
		soundElement.setAttribute("enablejavascript","true");
		Document.get().getBody().appendChild(soundElement);
	}

	private native void stopPlayer(Element soundPlayer) /*-{
		try {
			soundPlayer.Stop();
		} catch (e){
		}
	}-*/;

	public void stopSound() {
		if (soundPlayer!=null) {
			soundPlayer.setAttribute("loop","false");
			Document.get().getBody().removeChild(soundPlayer);
			stopPlayer(soundPlayer);
			soundPlayer = null;
		}
	}
	public void stopBackgroundSound() {
		if (backgroundPlayer!=null) {
			backgroundPlayer.setAttribute("loop","false");
			Document.get().getBody().removeChild(backgroundPlayer);
			stopPlayer(backgroundPlayer);
			backgroundPlayer = null;
		}
	}

}
