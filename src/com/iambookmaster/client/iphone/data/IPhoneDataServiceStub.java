package com.iambookmaster.client.iphone.data;

import java.io.IOException;
import java.util.ArrayList;

import com.google.code.gwt.database.client.Database;
import com.google.code.gwt.database.client.service.Callback;
import com.google.code.gwt.database.client.service.DataServiceException;
import com.google.code.gwt.database.client.service.ListCallback;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.code.gwt.database.client.service.VoidCallback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.iambookmaster.client.common.FileExchangeClient;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.model.Model;

public class IPhoneDataServiceStub extends IPhoneDataService {
	
	static final FileExchangeClient fileExchange = new FileExchangeClient();

	private static final String STATE_FILE = "/_last_emulator_game_state.save";
	
///	private IPhoneDBDataService service;

	private String data;

	private static String basePath;
	public IPhoneDataServiceStub() {
/*		if (Database.isSupported()) {
			service = GWT.create(IPhoneDBDataService.class);
			service.initTable(new VoidCallback() {
				public void onFailure(DataServiceException error) {
					service = null;
				}
				
				public void onSuccess() {
					//ok
				}
			});
		}*/
	}
	

	@Override
	public void loadLastState(Model model, final ScalarCallback<String> callback) {
		if (basePath==null) {
			basePath = fileExchange.selectFolder("Выберите папку с квестами","Выбрать");
			if (basePath==null) {
				callback.onFailure(new DataServiceException("Отменено"));
				return;
			}
		}
		String state = fileExchange.loadFileByPath(basePath+STATE_FILE);
		if (state == null) {
			callback.onSuccess(null);
		} else {
			callback.onSuccess(state);
		} 
	}

	private void notSupported(Callback callback) {
		callback.onFailure(new DataServiceException("not supported"));
	}


	@Override
	public void storeState(final String playerState) {
		if (basePath==null) {
			return;
		}
		try {
			fileExchange.writeFile(basePath+STATE_FILE,playerState);
		} catch (IOException e) {
			e.printStackTrace();
			Window.alert(e.getMessage());
		}
	}


	@Override
	public String getBackground() {
		return IPhoneImages.INSTANCE.backgoundOldBookEditor().getText();
	}


	@Override
	public void donate(VoidCallback callback) {
		callback.onSuccess();
	}


	@Override
	public void calculateDonate(ScalarCallback<String> callback) {
		notSupported(callback);
	}

	public boolean isInAppAvailable() {
		return true;
	}


	@Override
	public void selectAvailableFiles(String exention, final ListCallback<IPhoneFileBean> callback) {
		if (basePath==null) {
			basePath = fileExchange.selectFolder("Выберите папку с квестами","Выбрать");
			if (basePath==null) {
				callback.onFailure(new DataServiceException("Отменено"));
				return;
			}
		}
		String[] files = fileExchange.selectFilesByExtention(basePath,exention);
		if (files != null) {
			ArrayList<IPhoneFileBean> result = new ArrayList<IPhoneFileBean>(files.length);
			for (String name : files) {
				int i = name.lastIndexOf('/');
				if (i<0) {
					i = name.lastIndexOf('\\');
				}
				if (i>0) {
					result.add(createFile(name.substring(i+1),name));
				} else {
					result.add(createFile(name,name));
				}
			}
			callback.onSuccess(result);
		} else {
			callback.onFailure(new DataServiceException("Ошибка чтения папки "+basePath));
		}
	}
	
	private native IPhoneFileBean createFile(String name,String path)/*-{
		var f = {};
		f.name = name;
		f.path = path;
		return f;
	}-*/;


	@Override
	public void loadSingleFile(String name, ScalarCallback<String> callback) {
		String data = fileExchange.loadFileByPath(name);
		if (data==null) {
			callback.onFailure(new DataServiceException("Canceled"));
		} else {
			callback.onSuccess(data);
		}
	}


	@Override
	public void storeState(String state, String fileName, VoidCallback callback) {
		try {
			fileExchange.writeFile(fileName,state);
			callback.onSuccess();
		} catch (IOException e) {
			callback.onFailure(new DataServiceException(e.getMessage()));
		}
	}
	
	
}
