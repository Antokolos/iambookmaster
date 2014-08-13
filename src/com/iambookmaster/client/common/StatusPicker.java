package com.iambookmaster.client.common;

import com.google.gwt.user.client.ui.ListBox;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;

public class StatusPicker extends ListBox {
	
	private static final AppConstants appConstants = AppLocale.getAppConstants();
	
	public StatusPicker() {
		addItem(appConstants.statusProposal(), String.valueOf(Model.STATUS_PROPOSAL));
		addItem(appConstants.statusDraft(), String.valueOf(Model.STATUS_DRAFT));
		addItem(appConstants.statusFinal(), String.valueOf(Model.STATUS_FINAL));
	}
}
