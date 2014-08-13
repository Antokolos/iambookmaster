package com.iambookmaster.client.common;

import java.util.ArrayList;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;

public class WindowResizeCacheListener implements WindowResizeListener{
	
	private static WindowResizeCacheListener instance;
	
	public static void addResizeListener(WindowResizeListener listener) {
		instance.add(listener);
	}
	
	public static void removeResizeListener(WindowResizeListener listener) {
		instance.remove(listener);
	}
	
	private Timer timer;
	private int counter;
	private ArrayList<WindowResizeListener> list;
	private int width;
	private int height;
	
	private WindowResizeCacheListener(){
		list = new ArrayList<WindowResizeListener>();
		height = Window.getClientHeight();
		width = Window.getClientWidth();
		Window.addWindowResizeListener(this);
	}
	
	static {
		instance = new WindowResizeCacheListener();
	}

	public void add(WindowResizeListener listener) {
		list.add(listener);
		
	}

	public void remove(WindowResizeListener listener) {
		for (int i=0;i<list.size();i++) {
			if (list.get(i)==listener) {
				list.remove(i);
				return;
			}
		}
	}

	public void onWindowResized(int width, int height) {
		counter = 4;
		this.width = width;
		this.height = height;
		if (timer==null) {
			timer = new Timer() {

				public void run() {
					if (counter>0) {
						counter--;
						return;
					}
					//resize
					this.cancel();
					timer = null;
					for (int i=0;i<list.size();i++) {
						WindowResizeListener listener = (WindowResizeListener)list.get(i);
						listener.onWindowResized(WindowResizeCacheListener.this.width,WindowResizeCacheListener.this.height);
					}
				}
				
			};
			timer.scheduleRepeating(150);
		}
	}

}
