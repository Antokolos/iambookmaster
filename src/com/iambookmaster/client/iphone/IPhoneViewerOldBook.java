package com.iambookmaster.client.iphone;

import com.iambookmaster.client.iphone.data.IPhoneDataService;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.player.PlayerState;

public class IPhoneViewerOldBook extends IPhoneViewerOldBookSkin {

	private IPhonePlayer player;

	public void loadPlayer(Model model,IPhoneDataService dataService,IPhonePlayerListener listener) {
		redrawScreen();
		player = new IPhonePlayer(viewCanvas,model,dataService,listener) {
			@Override
			protected void showIntroScreen(boolean hasContinue) {
				super.showIntroScreen(hasContinue);
			}
		};
	}
	
	protected void loadPlayer() {
		redrawScreen();
		//disable overlay
		player = new IPhonePlayer(viewCanvas);
	}

	public IPhonePlayer getPlayer() {
		return player;
	}

	public PlayerState getPlayerState() {
		return player.getPlayerState();
	}

	

}
