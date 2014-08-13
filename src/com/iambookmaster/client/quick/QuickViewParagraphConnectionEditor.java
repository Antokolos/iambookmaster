package com.iambookmaster.client.quick;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.Images;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.common.ColorPicker;
import com.iambookmaster.client.common.SimpleAbstractParameterListBox;
import com.iambookmaster.client.common.SimpleObjectsListBox;
import com.iambookmaster.client.editor.DiceValueWidget;
import com.iambookmaster.client.editor.ModelPersist;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ParagraphConnectionListener;

public class QuickViewParagraphConnectionEditor extends VerticalPanel implements QuickViewWidget {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	
	private ParagraphConnection connection;
	private ModelPersist model;
	private ParagraphConnectionListener locationConnectionListener;
	private Image from;
	private TextBox fromId;
	private Image cut;
	private TextBox toId;
	private Image to;
	private TextBox nameFrom;
	private TextBox nameTo;
	private SimpleObjectsListBox objectsListBox;
	private Image remove;
	private Label direction;
	private Label backName;
	private ColorPicker colorPicker;
	private ListBox type;
	private ListBox strictness;
	private SimpleAbstractParameterListBox<Parameter> parameter;
	private SimpleAbstractParameterListBox<Modificator> modificator;
	private DiceValueWidget parameterValue;

	private CheckBox reverseHiddenUsage;
	public QuickViewParagraphConnectionEditor(Model mod, ParagraphConnection conn) {
		this.model = (ModelPersist)mod;
		setSize("100%", "100%");
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		Label label = new Label(appConstants.quickConnectionTitle(),false);
		horizontalPanel.add(label);
//		horizontalPanel.setCellWidth(label,"99%");
		ClickHandler clickListener = new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (event.getSource()==remove) {
					if (Window.confirm(appConstants.quickConnectionRemoveConfirm())) {
						model.removeParagraphConnection(connection);
					}
				} else if (event.getSource()==cut) {
					if (Window.confirm(appConstants.splitParagraphConnection())) {
						model.splitParagraphConnection(connection);
					}
				} else if (event.getSource()==from) {
					model.unselectParagraphConnection(connection, locationConnectionListener);
					if (model.getCurrentParagraph()==connection.getFrom()) {
						model.unselectParagraph(connection.getFrom(), null);
					}
					model.selectParagraph(connection.getFrom(), null);
				} else if (event.getSource()==to) {
					model.unselectParagraphConnection(connection, locationConnectionListener);
					if (model.getCurrentParagraph()==connection.getTo()) {
						model.unselectParagraph(connection.getTo(), null);
					}
					model.selectParagraph(connection.getTo(), null);
				} else if (event.getSource()==reverseHiddenUsage) {
					connection.setReverseHiddenUsage(reverseHiddenUsage.getValue());
				}
			}
			
		};
		ChangeHandler changeListener  = new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateConnection(event.getSource());
			}
			
		};
		BlurHandler blueHandler  = new BlurHandler() {
			public void onBlur(BlurEvent event) {
				updateConnection(event.getSource());
			}
		};
		remove = new Image(Images.REMOVE);
		remove.setTitle(appConstants.quickConnectionRemoveTitle());
		remove.setStyleName(Styles.CLICKABLE);
		remove.addClickHandler(clickListener);
		horizontalPanel.add(remove);
//		horizontalPanel.setCellWidth(remove,"99%");
		add(horizontalPanel);
		setCellHeight(horizontalPanel,"1%");
		setCellWidth(horizontalPanel,"100%");
		Grid grid = new Grid(12,2);
		grid.setSize("100%", "100%");
		int row=0;
		colorPicker = new ColorPicker(changeListener);
		label = new Label();
		label.setStyleName(Styles.BOLD);
		grid.setWidget(row,0,new Label(appConstants.connectionColor()));
		grid.setWidget(row,1,colorPicker);
		row++;
		
		nameFrom = new TextBox();
		nameFrom.setTitle(appConstants.quickConnectionNameTitle());
		nameFrom.addBlurHandler(blueHandler);
		grid.setWidget(row,0,new Label(appConstants.quickConnectionNameFrom()));
		grid.setWidget(row,1,nameFrom);
		row++;
		
		nameTo = new TextBox();
		nameTo.setTitle(appConstants.quickConnectionNameTitle());
		nameTo.addBlurHandler(blueHandler);
		backName = new Label(appConstants.quickConnectionNameTo());
		grid.setWidget(row,0,backName);
		grid.setWidget(row,1,nameTo);
		row++;
		
		HorizontalPanel panel = new HorizontalPanel();
		
		from = new Image(Images.LEFT_GREEN);
		from.addStyleName(Styles.CLICKABLE);
		from.setTitle(appConstants.openParagraph());
		from.addClickHandler(clickListener);
		panel.add(from);
		
		fromId = new TextBox();
		fromId.setReadOnly(true);
		fromId.setTitle(appConstants.quickConnectionTextLinkDesciption());
		fromId.setWidth("100%");
		panel.add(fromId);

		cut = new Image(Images.SPLIT);
		cut.addStyleName(Styles.CLICKABLE);
		cut.setTitle(appConstants.splitParagraphConnection());
		cut.addClickHandler(clickListener);
		panel.add(cut);
		
		toId = new TextBox();
		toId.setReadOnly(true);
		toId.setTitle(appConstants.quickConnectionTextLinkDesciption());
		toId.setWidth("100%");
		panel.add(toId);
		
		to = new Image(Images.RIGHT_GREEN);
		to.addStyleName(Styles.CLICKABLE);
		to.setTitle(appConstants.openParagraph());
		to.addClickHandler(clickListener);
		panel.add(to);
		
		grid.setWidget(row,0,new Label(appConstants.quickConnectionID()));
		grid.setWidget(row,1,panel);
		row++;
		
		grid.setWidget(row,0,new Label(appConstants.quickConnectionType()));
		direction = new Label();
		grid.setWidget(row,1,direction);
		row++;
		
		type = new ListBox();
		type.addItem(appConstants.connectionTypeNormal(), ParagraphConnection.TYPE_NORMAL_STR);
		type.addItem(appConstants.connectionTypeObject(), ParagraphConnection.TYPE_NORMAL_STR);
		type.addItem(appConstants.connectionTypeParameterIsMore(), ParagraphConnection.TYPE_PARAMETER_MORE_STR);
		type.addItem(appConstants.connectionTypeParameterIsLess(), ParagraphConnection.TYPE_PARAMETER_LESS_STR);
		type.addItem(appConstants.connectionTypeModificatorPresent(), ParagraphConnection.TYPE_MODIFICATOR_STR);
		type.addItem(appConstants.connectionTypeModificatorNotPresent(), ParagraphConnection.TYPE_NO_MODIFICATOR_STR);
		type.addItem(appConstants.connectionTypeVitalLess(), ParagraphConnection.TYPE_VITAL_LESS_STR);
		type.addItem(appConstants.connectionTypeEnemyVitalLess(), ParagraphConnection.TYPE_ENEMY_VITAL_LESS_STR);
		type.addItem(appConstants.connectionTypeBattleRoundsMore(), ParagraphConnection.TYPE_BATTLE_ROUND_MORE_STR);
		type.addChangeHandler(changeListener);
		type.setTitle(appConstants.connectionTypeTitle());
		grid.setWidget(row,0,new Label(appConstants.connectionType()));
		grid.setWidget(row,1,type);
		row++;
		
		strictness = new ListBox();
		strictness.addItem(appConstants.connectionStrictnessCan(), ParagraphConnection.STRICTNESS_CAN_STR);
		strictness.addItem(appConstants.connectionStrictnessMust(), ParagraphConnection.STRICTNESS_MUST_STR);
		strictness.addItem(appConstants.connectionStrictnessMustNot(), ParagraphConnection.STRICTNESS_MUST_NOT_STR);
		strictness.addChangeHandler(changeListener);
		strictness.setTitle(appConstants.connectionStrictnessTitle());
		grid.setWidget(row,0,new Label(appConstants.connectionStrictness()));
		grid.setWidget(row,1,strictness);
		row++;
		
		objectsListBox = new SimpleObjectsListBox(mod);
		objectsListBox.addChangeHandler(changeListener);
		objectsListBox.setTitle(appConstants.quickConnectionConditionObjectTitle());
		grid.setWidget(row,0,new Label(appConstants.quickConnectionConditionObject()));
		grid.setWidget(row,1,objectsListBox);
		row++;
		
		parameter = new SimpleAbstractParameterListBox<Parameter>(Parameter.class,model,true);
		parameter.setTitle(appConstants.connectionParameterTitle());
		parameter.addChangeHandler(changeListener);
		grid.setWidget(row,0,new Label(appConstants.connectionParameter()));
		grid.setWidget(row,1,parameter);
		row++;
		
		horizontalPanel  = new HorizontalPanel();
		parameterValue = new DiceValueWidget(horizontalPanel);
		parameterValue.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateConnection(parameterValue);
			}
		});
		grid.setWidget(row,0,new Label(appConstants.connectionParameterValue()));
		grid.setWidget(row,1,horizontalPanel);
		row++;

		modificator = new SimpleAbstractParameterListBox<Modificator>(Modificator.class,model,true);
		modificator.setTitle(appConstants.connectionModificatorTitle());
		modificator.addChangeHandler(changeListener);
		grid.setWidget(row,0,new Label(appConstants.connectionModificator()));
		grid.setWidget(row,1,modificator);
		row++;
		
		reverseHiddenUsage =  new CheckBox(appConstants.quickReverseHiddenUsage());
		reverseHiddenUsage.setTitle(appConstants.quickReverseHiddenUsageTitle());
		reverseHiddenUsage.addClickHandler(clickListener);
		grid.setWidget(row,0,new HTML("&nbsp;"));
		grid.setWidget(row,1,reverseHiddenUsage);
		row++;		
		
		add(grid);
		setCellHeight(grid,"1%");
		setCellWidth(grid,"100%");
		
		HTML html = new HTML("&nbsp;");
		add(html);
		setCellHeight(html,"99%");
		setCellWidth(html,"100%");
		
		locationConnectionListener = new ParagraphConnectionListener(){
			public void refreshAll() {
				open(QuickViewParagraphConnectionEditor.this.connection);
			}
			public void select(ParagraphConnection connection) {
			}
			public void unselect(ParagraphConnection connection) {
			}
			public void update(ParagraphConnection connection) {
				if (connection==QuickViewParagraphConnectionEditor.this.connection) {
					open(QuickViewParagraphConnectionEditor.this.connection);
				}
			}
			public void remove(ParagraphConnection connection) {
			}
			public void addNew(ParagraphConnection connection) {
				if (connection==QuickViewParagraphConnectionEditor.this.connection) {
					open(QuickViewParagraphConnectionEditor.this.connection);
				}
			}
		};
		model.addParagraphConnectionListener(locationConnectionListener);
		open(conn);
	}

	private void updateConnection(Object object) {
		if (object==objectsListBox) {
			ObjectBean obj = objectsListBox.getSelectedObject();
			connection.setObject(obj);
			if (obj==null) {
				type.setSelectedIndex(0);
			} else {
				type.setSelectedIndex(1);
			}
		} else if (object==colorPicker) {
			connection.setColor(colorPicker.getSelectedIndex());
		} else if (object==type) {
			int tp = type.getSelectedIndex();
			if (tp>1) {
				connection.setType(tp-1);
			} else {
				connection.setType(ParagraphConnection.TYPE_NORMAL);
			}
			if (connection.getType()!=ParagraphConnection.TYPE_NORMAL) {
				connection.setObject(null);
				objectsListBox.setSelectedObject(null);
			}
			applyControls();
		} else if (object==parameter) {
			connection.setParameter(parameter.getSelectedParameter());
		} else if (object==modificator) {
			connection.setModificator(modificator.getSelectedParameter());
		} else if (object==parameterValue) {
			connection.setParameterValue(parameterValue.getDiceValue());
		} else if (object==strictness) {
			connection.setStrictness(strictness.getSelectedIndex());
		} else if (object==nameFrom) {
			connection.setNameFrom(nameFrom.getText().trim());
		} else if (object==nameTo) {
			connection.setNameTo(nameTo.getText().trim());
		}
		model.updateParagraphConnection(connection, locationConnectionListener);
	}
	
	public void open(ParagraphConnection connection) {
		this.connection = connection;
		fromId.setText(connection.getFromId());
		toId.setText(connection.getToId());
		objectsListBox.setSelectedObject(connection.getObject());
		if (connection.isBothDirections()) {
			direction.setText(appConstants.quickConnectionBiDirection());
			objectsListBox.setEnabled(false);
		} else {
			direction.setText(appConstants.quickConnectionOneWay());
			objectsListBox.setEnabled(true);
		}
		colorPicker.setSelectedIndex(connection.getColor());
		if (connection.isBothDirections()==false) {
			if (connection.getType()==ParagraphConnection.TYPE_NORMAL) {
				if (connection.getObject()==null) {
					type.setSelectedIndex(0);
				} else {
					type.setSelectedIndex(1);
				}
			} else {
				type.setSelectedIndex(connection.getType()+1);
			}
			parameter.setSelectedParameter(connection.getParameter());
			modificator.setSelectedParameter(connection.getModificator());
			parameterValue.apply(connection.getParameterValue());
			strictness.setSelectedIndex(connection.getStrictness());
		}
		nameFrom.setText(connection.getNameFrom());
		nameTo.setText(connection.getNameTo());
		reverseHiddenUsage.setValue(connection.isReverseHiddenUsage());
		applyControls();
	}
	
	private void applyControls() {
		if (connection.isBothDirections()) {
			parameter.setEnabled(false);
			modificator.setEnabled(false);
			parameterValue.setEnabled(false);
			type.setEnabled(false);
			strictness.setEnabled(false);
			nameTo.setEnabled(true);
			nameTo.setVisible(true);
			backName.setVisible(true);
			return;
		} 
		nameTo.setEnabled(false);
		nameTo.setVisible(false);
		backName.setVisible(false);
		
		type.setEnabled(true);
		strictness.setEnabled(true);
		
		int tp = type.getSelectedIndex();
		if (tp>1) {
			tp=tp-1;
		} else {
			tp=ParagraphConnection.TYPE_NORMAL;
		}
		switch (tp) {
		case ParagraphConnection.TYPE_PARAMETER_LESS:
		case ParagraphConnection.TYPE_PARAMETER_MORE:
			parameter.setEnabled(true);
			modificator.setEnabled(false);
			objectsListBox.setEnabled(false);
			parameterValue.setEnabled(true);
			if (connection.getParameterValue()==null) {
				connection.setParameterValue(parameterValue.getDiceValue());
			}
			break;
			
		case ParagraphConnection.TYPE_MODIFICATOR:
		case ParagraphConnection.TYPE_NO_MODIFICATOR:
			parameter.setEnabled(false);
			modificator.setEnabled(true);
			parameterValue.setEnabled(false);
			objectsListBox.setEnabled(false);
			break;

		case ParagraphConnection.TYPE_VITAL_LESS:
		case ParagraphConnection.TYPE_ENEMY_VITAL_LESS:
			parameter.setEnabled(false);
			modificator.setEnabled(false);
			parameterValue.setEnabled(true);
			objectsListBox.setEnabled(false);
			if (connection.getParameterValue()==null) {
				connection.setParameterValue(parameterValue.getDiceValue());
			}
			break;
			
		case ParagraphConnection.TYPE_BATTLE_ROUND_MORE:
			parameter.setEnabled(false);
			modificator.setEnabled(false);
			parameterValue.setEnabled(true);
			objectsListBox.setEnabled(false);
			if (connection.getParameterValue()==null) {
				connection.setParameterValue(parameterValue.getDiceValue());
			}
			break;
			
		default:
			parameter.setEnabled(false);
			modificator.setEnabled(false);
			parameterValue.setEnabled(false);
			objectsListBox.setEnabled(true);
			break;
		}
	}

	public void close() {
		model.removeParagraphConnectionListener(locationConnectionListener);
	}

	public ParagraphConnection getLocationConnection() {
		return connection;
	}


}
