package com.iambookmaster.client.iphone.data;

import com.google.code.gwt.database.client.service.DataServiceException;
import com.google.code.gwt.database.client.service.ListCallback;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.code.gwt.database.client.service.VoidCallback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Frame;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.model.Model;

public abstract class IPhoneDataService {

	private static IPhoneDataService service = GWT.create(IPhoneDataService.class);
	
	public static IPhoneDataService getInstance() {
		return service;
	}

	private Frame frame;
	private ScalarCallback<String> callback;
	
	public abstract void loadLastState(Model model, ScalarCallback<String> callback);

	public abstract void donate(VoidCallback callback);
	
	public abstract void calculateDonate(ScalarCallback<String> callback);
	
	public abstract void storeState(String playerState);
	
	public String getBackground() {
		return IPhoneImages.INSTANCE.backgoundOldBook().getText();
	}
	
	public boolean isLinkedVersionPresent() {
		return true;
	}

	public boolean isCracked() {
		return false;
	}

	public boolean isInAppAvailable() {
		return false;
	}
	
	public static String generateCommandURL(String command,String param) {
		StringBuilder builder = new StringBuilder("command");
		builder.append("://");
		builder.append(command);
		if (param != null) {
			builder.append('/');
			builder.append(param);
		}
		return builder.toString();
	}

	public void cancelRequest() {
	}

	public void removeSplashScreen() {
	}

	public abstract void selectAvailableFiles(String exention, ListCallback<IPhoneFileBean> callback);

	public abstract void loadSingleFile(String name,final ScalarCallback<String> callback);

	public void storeState(String storeGameState, String fileName, VoidCallback callback) {
		callback.onFailure(new DataServiceException("Not supported"));
	}

}
