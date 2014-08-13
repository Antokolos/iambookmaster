package com.iambookmaster.client.quick;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.NPC;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.common.NumberTextBox;
import com.iambookmaster.client.model.Model;

public class QuickNPCEditor extends QuickAbstractParameterEditor {

	private NPC npc;
	private FlexTable parametersPanel;
	private HashMap<Parameter,Integer> widgets;
	private TextBox genitiveName;
	
	public QuickNPCEditor(Model mod) {
		super(mod);
	}

	@Override
	protected int getGridWidgetsCount() {
		return 1;
	}
	
	@Override
	public String getEditorName() {
		return appConstants.quickNPCTitle();
	}

	@Override
	public Widget getTail() {
		ChangeHandler changeHandler = new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				npc.setGenitiveName(genitiveName.getText());
				updateParameter(event.getSource());
			}
		};
		genitiveName = new TextBox();
		genitiveName.setWidth("100%");
		genitiveName.addChangeHandler(changeHandler);
		genitiveName.setTitle(appConstants.quickNPCGenativeNameTitle());
		addWidgetToGrid(genitiveName, appConstants.quickNPCGenativeName());
		
		parametersPanel = new FlexTable();
		parametersPanel.setSize("100%", "100%");
		parametersPanel.getColumnFormatter().setWidth(0, "99%");
		parametersPanel.getColumnFormatter().setWidth(1, "1%");
		return parametersPanel;
	}

	public void open(AbstractParameter object) {
		super.open(object);
		npc = (NPC) object;
		genitiveName.setText(npc.getGenitiveName()==null ? "" : npc.getGenitiveName());
		while (parametersPanel.getRowCount()>0) {
			parametersPanel.removeRow(0);
		}
		if (widgets==null) {
			widgets = new HashMap<Parameter,Integer>();
		}
		
		widgets.clear();
		for (Parameter parameter : npc.getValues().keySet()) {
			Integer val = npc.getValues().get(parameter);
			updateParameterWidget(parameter,val);
		}
		ArrayList<AbstractParameter> params = model.getParameters();
		for (AbstractParameter abstractParameter : params) {
			if (abstractParameter instanceof Parameter) {
				Parameter parameter = (Parameter) abstractParameter;
				if (parameter.isHeroOnly()) {
					continue;
				}
				if (widgets.containsKey(parameter)==false) {
					updateParameterWidget(parameter,0);
				}
			}
		}
	}

	private void updateParameterWidget(Parameter parameter, Integer val) {
		ParameterWidger widget;
		if (widgets.containsKey(parameter)) {
			int row = widgets.get(parameter);
			widget = (ParameterWidger)parametersPanel.getWidget(row, 1);
			widget.update(parameter);
		} else {
			//add new Widget
			widget = new ParameterWidger(parameter);
			
			int row = parametersPanel.getRowCount();
			if (row > 0) {
				row--;
			}
			row = parametersPanel.insertRow(row);
			widgets.put(parameter,row);
			parametersPanel.addCell(row);
			parametersPanel.addCell(row);
			parametersPanel.setWidget(row,0,widget.name);
			parametersPanel.setWidget(row,1,widget);
		}
		if (val != null) {
			widget.update(val);
		}
	}
	
	@Override
	protected void parameterWasAdded(AbstractParameter parameter) {
		if (parameter instanceof Parameter) {
			Parameter param = (Parameter) parameter;
			if (param.isHeroOnly()==false) {
				updateParameterWidget(param,null);
			}
		}
	}

	@Override
	protected void parameterWasRemoved(AbstractParameter parameter) {
		if (widgets.containsKey(parameter)) {
			parametersPanel.removeRow(widgets.get(parameter));
		}
	}

	@Override
	protected void parameterWasUpdated(AbstractParameter parameter) {
		if (parameter instanceof Parameter) {
			Parameter param = (Parameter) parameter;
			if (param.isHeroOnly()) {
				//became "not for NPC"
				parametersPanel.removeRow(widgets.get(parameter));
			} else if (widgets.containsKey(parameter)) {
				updateParameterWidget(param,null);
			}
		}
	}

	public class ParameterWidger extends NumberTextBox implements ChangeHandler{

		private Label name;
		private Parameter parameter;
		public ParameterWidger(Parameter parameter) {
			name = new Label();
			setMaxLength(3);
			setVisibleLength(4);
			addChangeHandler(this);
			update(parameter);
		}

		public void update(Parameter parameter) {
			this.parameter = parameter;
			name.setText(parameter.getName());
			setTitle(parameter.getDescription());
		}

		public void update(Integer value) {
			setValue(value);
		}

		public void onChange(ChangeEvent event) {
			if (getIntegerValue()==0) {
				//remove
				npc.getValues().remove(parameter);
			} else {
				//save
				npc.getValues().put(parameter, getIntegerValue());
			}
			updateParameter(null);
		}
		
	}
}
