package com.iambookmaster.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.iambookmaster.client.common.EditorPlayer;

public class PlayerMasterMenu extends PopupPanel {

	private EditorPlayer player;
	private MenuBar locationMenu;
	
	public PlayerMasterMenu(EditorPlayer pl) {
		super(true,true);
		this.player = pl;
		locationMenu = new MenuBar(true);
		if (player.isSupportRotation()) {
	 		locationMenu.addItem(IPhonePlayerWrapper.appConstants.playerIphoneRotate(),new Command() {
				public void execute() {
					hide();
					player.rotate();
				}
			});
		}
		
		if (player.isSupportModel()) {
			locationMenu.addItem(IPhonePlayerWrapper.appConstants.playerIphoneEditParagraph(),new Command() {
				public void execute() {
					hide();
					player.editCurrentParagraph();
				}
			});
		} else {
			locationMenu.addItem(IPhonePlayerWrapper.appConstants.loadModule(),new Command() {
				public void execute() {
					hide();
					player.loadModule();
				}
			});
			
		}
		locationMenu.addSeparator();
		locationMenu.addItem(IPhonePlayerWrapper.appConstants.playerIphoneRestartGame(),new Command() {
			public void execute() {
				hide();
				player.restart();
			}
		});
		if (player.isSupportModel()) {
			locationMenu.addItem(IPhonePlayerWrapper.appConstants.playerIphoneStartCurrent(),new Command() {
				public void execute() {
					hide();
					player.goCurrentParagraph();
				}
			});
		}
		if (player.isSupportSaveAndLoad()) {
			locationMenu.addItem(IPhonePlayerWrapper.appConstants.playerIphoneSaveGame(),new Command() {
				public void execute() {
					hide();
					player.save();
				}
			});
			locationMenu.addItem(IPhonePlayerWrapper.appConstants.playerIphoneLoadGame(),new Command() {
				public void execute() {
					hide();
					player.load();
				}
			});
			add(locationMenu);
		}
		if (player.isSupportScale()) {
			locationMenu.addItem(IPhonePlayerWrapper.appMessages.playerIphoneScale(100),new Command() {
				public void execute() {
					hide();
					player.scale(100);
				}
			});
			locationMenu.addItem(IPhonePlayerWrapper.appMessages.playerIphoneScale(90),new Command() {
				public void execute() {
					hide();
					player.scale(90);
				}
			});
			locationMenu.addItem(IPhonePlayerWrapper.appMessages.playerIphoneScale(80),new Command() {
				public void execute() {
					hide();
					player.scale(80);
				}
			});
			locationMenu.addItem(IPhonePlayerWrapper.appMessages.playerIphoneScale(70),new Command() {
				public void execute() {
					hide();
					player.scale(70);
				}
			});
			locationMenu.addItem(IPhonePlayerWrapper.appMessages.playerIphoneScale(60),new Command() {
				public void execute() {
					hide();
					player.scale(60);
				}
			});
			locationMenu.addItem(IPhonePlayerWrapper.appMessages.playerIphoneScale(50),new Command() {
				public void execute() {
					hide();
					player.scale(50);
				}
			});
			
		}
	}
	
}