package com.iambookmaster.client.iphone.urq;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.iambookmaster.client.iphone.IPhoneCanvas;
import com.iambookmaster.client.iphone.IPhoneViewListenerAdapter;
import com.iambookmaster.client.iphone.common.IPhoneButton;
import com.iambookmaster.client.iphone.common.IPhoneFlatButton;
import com.iambookmaster.client.iurq.Core;
import com.iambookmaster.client.iurq.logic.InvVar;

public class IPhoneURQInventory extends IPhoneViewListenerAdapter {
	
	private IPhoneURQInventoryListener listener;
	private IPhoneCanvas canvas;
	private Core core;
	private ClickHandler inventoryHandler;
	private ClickHandler backHandler;

	public IPhoneURQInventory(IPhoneURQInventoryListener lst) {
		listener = lst;
		inventoryHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				listener.useInventory();
			}
		};
		backHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				listener.back();
			}
		};
	}

	public void show(IPhoneCanvas canvas,Core core) {
		this.canvas = canvas;
		this.core = core;
		canvas.setListener(this);
		redraw(canvas);
	}

	public void redraw(IPhoneCanvas canvas) {
		canvas.clear();
		HashMap<String,InvVar> inv = core.getInvent();
		Grid grid = new Grid(inv.size()+1,2);
		grid.setWidth("100%");
		CellFormatter formatter = grid.getCellFormatter();
		IPhoneFlatButton button = new IPhoneFlatButton(core.getInventoryName());
		canvas.addClickHandler(button, inventoryHandler);
		grid.setWidget(0, 0, button);
		formatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		grid.getCellFormatter().setWidth(0, 0, "99%");
		int row=1;
		for (Entry<String,InvVar> entry : inv.entrySet()) {
			ClickHandler handler = new InventoryClickHandler(entry.getKey(),entry.getValue());
			button = new IPhoneFlatButton(entry.getKey());
			grid.setWidget(row, 0, button);
			canvas.addClickHandler(button, handler);
			formatter.setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_LEFT);
			
			InvVar var = entry.getValue();
			String text;
			if (var.getType()==InvVar.FLOAT) {
				if (var.getFloat()==1.0) {
					text = "";
				} else {
					text = String.valueOf(var.getFloat());
				}
			} else if (var.getType()==InvVar.STRING) {
				text = var.getString();
			} else if (var.getInt()>1) {
				text= String.valueOf(var.getInt());
			} else {
				text = "";
			}
			grid.setWidget(row, 1, new Label(text,false));
			formatter.setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
			row++;
		}
		canvas.add(grid);
		IPhoneButton iPhoneButton = new IPhoneButton("Назад");
		canvas.addClickHandler(iPhoneButton, backHandler);
		canvas.add(iPhoneButton);
		
		canvas.done();
	}

	public void back() {
		listener.back();
	}

	public void forward() {
		listener.forward();
	}

	public void drawn() {
	}
	
	public class InventoryClickHandler implements ClickHandler {
		
		private String name;
		private InvVar var;

		public InventoryClickHandler(String name, InvVar var) {
			this.name = name;
			this.var = var;
		}

		public void onClick(ClickEvent event) {
			listener.useInventory(name,var);
		}
		
	}

}
