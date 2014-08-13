package com.iambookmaster.client.iphone.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;


public interface IPhoneImages extends ClientBundle {
//	int WIDHT=320;
//	int HEIGHT=480;
//	int LIMIT = 320;
	IPhoneImages INSTANCE = GWT.create(IPhoneImages.class);
//	int ICON_SIZE = 32;
//	String IPHONE = "smartphone.png";
//	String SERVER = "network-server.png";

	@Source("images/embeded.css")
	IPhoneStyles css();
	
//	@Source("images/media-floppy.png")
//	ImageResource save();
//	
	
	@Source("images/audio-volume-high.png")
	ImageResource audioOn();

	@Source("images/audio-volume-muted.png")
	ImageResource audioOff();

	@Source("images/oldBook.html")
	TextResource backgoundOldBook();

	@Source("images/oldBookEditor.html")
	TextResource backgoundOldBookEditor();

	@Source("images/help.txt")
	TextResource helpModel();

	@Source("images/model.txt")
	TextResource model();

}
