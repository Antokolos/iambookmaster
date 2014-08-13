package com.iambookmaster.client.iphone;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;

public class IPhoneAudioState extends HorizontalPanel{
	
	private static final IPhoneStyles css = IPhoneImages.INSTANCE.css();
	private Image audio;
	private Image noAudio;
	private boolean enabled;
	public IPhoneAudioState(boolean enabled,IPhoneCanvas canvas) {
		audio = new Image(IPhoneImages.INSTANCE.audioOn());
		noAudio = new Image(IPhoneImages.INSTANCE.audioOff());
		setWidth("100%");
		setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		add(audio);
		ClickHandler handler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				setEnabled(true);
			}
		};
		audio.addClickHandler(handler);
		canvas.addClickHandler(audio, handler);
		add(noAudio);
		handler = new ClickHandler() {
				public void onClick(ClickEvent event) {
					setEnabled(false);
				}
			};
		noAudio.addClickHandler(handler);
		canvas.addClickHandler(noAudio, handler);
		setCellWidth(audio, "50%");
		setCellWidth(noAudio, "50%");
		setSpacing(10);
		setEnabled(enabled);
	}
	
	protected void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (enabled) {
			audio.setStyleName(css.audioStateSelected());
			noAudio.setStyleName(css.audioStateNonSelected());
		} else {
			noAudio.setStyleName(css.audioStateSelected());
			audio.setStyleName(css.audioStateNonSelected());
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

}
