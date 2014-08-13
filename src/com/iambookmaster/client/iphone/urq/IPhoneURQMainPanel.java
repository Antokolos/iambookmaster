package com.iambookmaster.client.iphone.urq;

import java.util.List;

import com.google.code.gwt.database.client.service.DataServiceException;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.iambookmaster.client.iphone.IPhoneCanvas;
import com.iambookmaster.client.iphone.IPhoneViewListenerAdapter;
import com.iambookmaster.client.iphone.common.IPhoneButton;
import com.iambookmaster.client.iphone.common.IPhoneTextFileViewer;
import com.iambookmaster.client.iphone.data.IPhoneDataService;
import com.iambookmaster.client.iphone.data.IPhoneFileBean;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;

public class IPhoneURQMainPanel extends IPhoneViewListenerAdapter {

	static final IPhoneStyles css = IPhoneImages.INSTANCE.css();
	
	private IPhoneCanvas canvas;
	private List<IPhoneFileBean> files;
	private ClickHandler playSingleFileHandler;
	private IPhoneDataService dataService;
	private ClickHandler selectFileHandler;
	private ClickHandler helpHandler;
	private IPhoneURQPlayer player;
	private boolean gameActive;
	private ClickHandler backHandler;
	private IPhoneURQSelectFile fileSelector; 

	public IPhoneURQMainPanel(IPhoneDataService dataService,List<IPhoneFileBean> result) {
		this.files = result;
		this.dataService = dataService;
		playSingleFileHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				gameActive = false;
				playFile(files.get(0).getPath());
			}
		};
		selectFileHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				gameActive = false;
				fileSelector.show(canvas, files);
			}
		};
		helpHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				IPhoneTextFileViewer viewer = new IPhoneTextFileViewer() {
					@Override
					public void back() {
						IPhoneURQMainPanel.this.show(canvas,false);
					}

					@Override
					public void forward() {
						IPhoneURQMainPanel.this.show(canvas,true);
					}
					
				};
				viewer.show(canvas, 
						"iURQ 1.0<br/><br/>Плеер предназначен для проигрывания произведений в жанре Интерактивная фантастика для платформы URQ.<br/><br/>" +
						"Сдвигайте экран вверх/вниз для скроллинга<br/><br/>" +
						"Сдвиньте экран влево для переключения в Инвентарь<br/><br/>"+
						"Сдвиньте экран вправо для переключения в главное меню<br/><br/>"+
						"Принимаются только файлы в формате QST, кодировка UTF-8.<br/><br/>" +
						"Данная версия не поддерживает картинки, звук и ввод текста<br/><br/>" +
						"Используйте iTunes чтобы загрузить дополнительные квесты<br/><br/>" +
						"Программа читает список доступных квестов в момент старта. Если вы добавили новый квест - потребуется перезапустить программу, чтобы начать в него играть. <br/><br/>" +
						"Используйте стандартные жесты для изменения размера шрифта<br/><br/>"
				);
			}
		};
		backHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				player.show(canvas);
			}
		};
		fileSelector = new IPhoneURQSelectFile("Выбрать игру") {
			@Override
			protected void selectFile(IPhoneFileBean file) {
				playFile(file.getPath());
			}

			public void back() {
				IPhoneURQMainPanel.this.show(canvas,false);
			}

			public void forward() {
				IPhoneURQMainPanel.this.show(canvas,true);
			}
		};
		
		player = new IPhoneURQPlayer(dataService,new IPhoneURQPlayerListenr() {

			public void exit() {
				gameActive=false;
				IPhoneURQMainPanel.this.show(canvas,true);
			}

			public void rootMenu() {
				gameActive=true;
				IPhoneURQMainPanel.this.show(canvas,false);
			}
		});
	}

	private void playFile(final String name) {
		dataService.loadSingleFile(name,new ScalarCallback<String>() {
			public void onFailure(DataServiceException error) {
				Window.alert("onFailure: "+error.getMessage());
			}

			public void onSuccess(String result) {
				player.play(result,canvas,true,name,null);
			}
		});
	}

	public void show(IPhoneCanvas canvas,boolean direction) {
		this.canvas = canvas;
		canvas.setListener(this);
		_redraw(canvas,direction ? 1 : -1);
	}

	public void redraw(IPhoneCanvas viewer) {
		_redraw(viewer, 0);
	}
	
	private void _redraw(IPhoneCanvas viewer,int animation) {
		switch (animation) {
		case -1:
			viewer.clearWithAnimation(true);
			break;
		case 1:
			viewer.clearWithAnimation(false);
			break;
		default:
			viewer.clear();
			break;
		}
		Label label = new Label("iURQ 1.0");
		label.setStyleName(css.urqTitle());
		viewer.add(label);
		label = new Label("iURQ - плеер для проигрывания текстовых квестов в формате URQ. Аббревиатура расшифровывается как Universal Ripsoft Quest, по названию первой программы-плеера.");
//		label.setStyleName(css.urqDescription());
		viewer.add(label);
		IPhoneButton button;
		if (gameActive) {
			button = new IPhoneButton("Продолжить игру");
			canvas.add(button);
			canvas.addClickHandler(button, backHandler);
		}
		
		if (files.size()==1){
			button = new IPhoneButton("Начать игру");
			canvas.add(button);
			canvas.addClickHandler(button, playSingleFileHandler);
		} else if (files.size()>1){
			button = new IPhoneButton("Выбрать игру");
			canvas.add(button);
			canvas.addClickHandler(button, selectFileHandler);
		}
		
		button = new IPhoneButton("Помощь");
		canvas.add(button);
		canvas.addClickHandler(button, helpHandler);
			
		canvas.done();
	}

	public void back() {
		if (gameActive) {
			player.show(canvas);
		}
	} 

	public void forward() {
		if (gameActive) {
			//back to game
			player.show(canvas);
		} else if (files.size()==1){
			//play
			playFile(files.get(0).getPath());
		} else if (files.size()>1){
			//select file
		}
	}

	public void drawn() {
	}

	public void playSavedGame(String questFileName, String questText, final String state) {
		//load quest and process it
		player.play(questText,canvas,false,questFileName,new Command() {
			public void execute() {
				try {
					player.restoreState(state,false);
					//show "Back to Game" button
					gameActive = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public IPhoneURQPlayer getPlayer() {
		return player;
	}

}
