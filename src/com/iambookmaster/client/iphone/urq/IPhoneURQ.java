package com.iambookmaster.client.iphone.urq;

import java.util.List;

import com.google.code.gwt.database.client.service.DataServiceException;
import com.google.code.gwt.database.client.service.ListCallback;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.iambookmaster.client.iphone.IPhonePlayerListener;
import com.iambookmaster.client.iphone.IPhoneViewerOldBookSkin;
import com.iambookmaster.client.iphone.data.IPhoneDataService;
import com.iambookmaster.client.iphone.data.IPhoneFileBean;

public class IPhoneURQ extends IPhoneViewerOldBookSkin {

	private IPhoneURQMainPanel rootPanel;
	
	public void loadPlayer(IPhoneDataService dataService,IPhonePlayerListener listener) {
		playerListener = listener;
		this.dataService = dataService;
		loadPlayer();
	}
	
	@Override
	protected void loadPlayer() {
		if (dataService==null) {
			dataService = IPhoneDataService.getInstance();
		}
		dataService.selectAvailableFiles("qst",new ListCallback<IPhoneFileBean>() {
			public void onFailure(DataServiceException error) {
				Window.alert(error.getMessage());
			}
	
			public void onSuccess(List<IPhoneFileBean> result) {
				rootPanel = new IPhoneURQMainPanel(dataService,result);
				dataService.loadLastState(null, new ScalarCallback<String>() {
					public void onFailure(DataServiceException error) {
						removeSplashScreen();
					}
					
					public void onSuccess(final String state) {
						if (state==null) {
							//nothing to restore
							removeSplashScreen();
						} else {
							final String questName = rootPanel.getPlayer().getLastQuestName(state);
							if (questName==null) {
								//nothing to restore
								removeSplashScreen();
							} else {
								dataService.loadSingleFile(questName, new ScalarCallback<String>() {
									public void onFailure(DataServiceException error) {
										//quest was removed
										removeSplashScreen();
									}
									public void onSuccess(String result) {
										//quest exists, play from the last state
										rootPanel.playSavedGame(questName,result,state);
										removeSplashScreen();
									}
								});							
							}
						}
					}
				});
			}
		});
	}
	
	private void removeSplashScreen() {
		rootPanel.show(viewCanvas,true);
		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				dataService.removeSplashScreen();
			}
		});
		
	}
	
	public void restart() {
		// TODO Auto-generated method stub
		
	}

	public void restoreGame(String text) {
		// TODO Auto-generated method stub
		
	}

	
}
