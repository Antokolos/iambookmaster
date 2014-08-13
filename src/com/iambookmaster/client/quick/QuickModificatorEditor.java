package com.iambookmaster.client.quick;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;

public class QuickModificatorEditor extends QuickAbstractParameterEditor {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	
	private Modificator modificator;
	private CheckBox absolute;
	
	public QuickModificatorEditor(Model mod) {
		super(mod);
	}

	@Override
	public String getEditorName() {
		return appConstants.quickModificatorTitle();
	}

	@Override
	protected int getGridWidgetsCount() {
		return 1;
	}

	@Override
	public Widget getTail() {
		absolute = new CheckBox();
		absolute.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				modificator.setAbsolute(absolute.getValue());
				updateParameter(event.getSource());
			}
			
		});
		absolute.setTitle(appConstants.quickModificatorAbsoluteTitle());
		addWidgetToGrid(absolute, appConstants.quickModificatorAbsolute());
		return null;
	}

	public void open(AbstractParameter object) {
		super.open(object);
		modificator = (Modificator) object;
		absolute.setValue(modificator.isAbsolute());
	}

}
