package com.iambookmaster.client.quick;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.Picture;
import com.iambookmaster.client.editor.PicturesListBox;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ParameterListener;

public abstract class QuickAbstractParameterEditor extends VerticalPanel implements QuickViewWidget {

	protected static final AppConstants appConstants = AppLocale.getAppConstants();
	
	private AbstractParameter parameter;
	protected Model model;
	private ParameterListener parameterListener;
	private TextBox name;
	private TextArea description;
	private int gridRow;
	private Grid grid;

	private PicturesListBox icon;
	public QuickAbstractParameterEditor(Model mod) {
		this.model = mod;
		setSize("100%", "100%");
		Label label = new Label(getEditorName());
		add(label);
		setCellHeight(label,"1%");
		setCellWidth(label,"100%");
		grid = new Grid(getGridWidgetsCount()+2,2);
		grid.setSize("100%", "100%");
		ChangeHandler changeHandler = new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateParameter(event.getSource());
			}
			
		};
		KeyPressHandler keyPressHandler  = new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				switch (event.getCharCode()) {
				case KeyCodes.KEY_ENTER:
					updateParameter(event.getSource());
					break;
				case KeyCodes.KEY_ESCAPE:
					open(parameter);
					break;
				}
			}
		};
		name = new TextBox();
		name.addChangeHandler(changeHandler);
		name.addKeyPressHandler(keyPressHandler);
		grid.setWidget(gridRow,0,new Label(appConstants.quickItemName()));
		grid.setWidget(gridRow,1,name);
		gridRow++;
		
		icon = new PicturesListBox(model,getIconType());
		icon.addChangeHandler(changeHandler);
		grid.setWidget(gridRow,0,new Label(appConstants.quickItemIcon()));
		grid.setWidget(gridRow,1,icon);
		gridRow++;
		
		add(grid);
		setCellHeight(grid,"1%");
		setCellWidth(grid,"100%");
		label = new Label(appConstants.quickItemMasterDescription());
		add(label);
		setCellHeight(label,"1%");
		setCellWidth(label,"100%");
		description = new TextArea();
		description.addChangeHandler(changeHandler);
		description.setWidth("100%");
		description.setVisibleLines(3);
		add(description);
		setCellHeight(description,"1%");
		setCellWidth(description,"100%");

		Widget widget = getTail();
		if (widget != null) {
			add(widget);
			setCellHeight(widget,"1%");
			setCellWidth(widget,"100%");
		}
		HTML html = new HTML("&nbsp;");
		html.setStyleName(Styles.FILLER);
		add(html);
		setCellHeight(html,"99%");
		setCellWidth(html,"100%");
		
		parameterListener = new ParameterListener(){
			public void refreshAll() {
				open(QuickAbstractParameterEditor.this.parameter);
			}
			public void addNewParameter(AbstractParameter parameter) {
				parameterWasAdded(parameter);
			}
			public void remove(AbstractParameter parameter) {
				parameterWasRemoved(parameter);
			}
			public void select(AbstractParameter parameter) {
			}
			public void update(AbstractParameter parameter) {
				parameterWasUpdated(parameter);
			}
			public void showInfo(AbstractParameter parameter) {
			}
		};
		model.addParamaterListener(parameterListener);
//		open(obj);
	}

	protected int getIconType() {
		return Picture.ROLE_ICON;
	}

	protected void parameterWasUpdated(AbstractParameter parameter) {
	}

	protected void parameterWasRemoved(AbstractParameter parameter) {
	}

	protected void parameterWasAdded(AbstractParameter parameter) {
	}

	protected int getGridWidgetsCount() {
		return 0;
	}

	protected void addWidgetToGrid(Widget widet,String name) {
		grid.setWidget(gridRow,0,new Label(name));
		grid.setWidget(gridRow++,1,widet);
	}

	public abstract String getEditorName();
	
	public Widget getTail() {
		return null;
	}

	protected void updateParameter(Object sender) {
		if (sender==name) {
			parameter.setName(name.getText().trim());
		} else if (sender==description) {
			parameter.setDescription(description.getText().trim());
		}
		model.updateParameter(parameter, parameterListener);
	}

	public void open(final AbstractParameter object) {
		this.parameter = object;
		name.setText(object.getName());
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				description.setText(object.getDescription());
			}
		});
		
	}
	
	public void close() {
		model.removeParamaterListener(parameterListener);
	}

}
