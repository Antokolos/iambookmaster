package com.iambookmaster.client.iphone.urq;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.iambookmaster.client.iphone.IPhoneCanvas;
import com.iambookmaster.client.iphone.IPhoneViewListenerAdapter;
import com.iambookmaster.client.iphone.common.IPhoneButton;
import com.iambookmaster.client.iphone.common.IPhoneFlatButton;
import com.iambookmaster.client.iphone.data.IPhoneFileBean;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;

public abstract class IPhoneURQSelectFile extends IPhoneViewListenerAdapter {

	static final IPhoneStyles css = IPhoneImages.INSTANCE.css();
	
	private IPhoneCanvas canvas;
	private ClickHandler backHandler;
	private List<IPhoneFileBean> files;
	private boolean processing;
	private final String title;
	private String newButtonTitle;
	private ClickHandler newFileHandler;

	public IPhoneURQSelectFile(String title) {
		this.title = title;
		backHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				back();
			}
		};
		newFileHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				newFile();
			}
		};
	}

	protected void newFile() {
	}

	public IPhoneURQSelectFile(String title,String newButton) {
		this(title);
		this.newButtonTitle = newButton;
	}

	public void show(IPhoneCanvas canvas,List<IPhoneFileBean> files) {
		this.canvas = canvas;
		this.files = files;
		processing = false;
		_redraw(canvas,1);
	}

	public void redraw(IPhoneCanvas canvas) {
		_redraw(canvas, 0);
	}
	public void _redraw(IPhoneCanvas canvas,int animiation) {
		canvas.setListener(this);
		switch (animiation) {
		case -1:
			canvas.clearWithAnimation(true);
			break;
		case 1:
			canvas.clearWithAnimation(false);
			break;
		default:
			canvas.clear();
			break;
		}
		
		if (processing) {
			Label label = new Label("Идет обработка...");
			label.setStyleName(css.urqScreenTitle());
			canvas.add(label);
		} else {
			Label label = new Label(title,false);
			label.setStyleName(css.urqScreenTitle());
			canvas.add(label);
			if (newButtonTitle != null) {
				IPhoneButton button = new IPhoneButton(newButtonTitle);
				canvas.add(button);
				canvas.addClickHandler(button, newFileHandler);
			}
			int i=1;
			for (IPhoneFileBean file : files) {
				ClickHandler handler = new ActionClickHandler(file);
				IPhoneFlatButton button = new IPhoneFlatButton(file.getName());
				canvas.add(button);
				canvas.addClickHandler(button, handler);
				i++;
			}
			
			IPhoneButton button = new IPhoneButton("Назад");
			canvas.addClickHandler(button, backHandler);
			canvas.add(button);
		}
		
		canvas.done();
	}

	public void drawn() {
	}
	
	public class ActionClickHandler implements ClickHandler {
		
		private IPhoneFileBean file;

		public ActionClickHandler(IPhoneFileBean file) {
			this.file = file;
		}

		public void onClick(ClickEvent event) {
			processing = true;
			redraw(canvas);
			Scheduler.get().scheduleDeferred(new Command() {
				public void execute() {
					selectFile(file);
				}
			});
		}
		
	}

	protected abstract void selectFile(IPhoneFileBean file);

}
