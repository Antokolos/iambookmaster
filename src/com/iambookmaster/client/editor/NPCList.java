package com.iambookmaster.client.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.Images;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.NPC;
import com.iambookmaster.client.beans.NPCParams;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.common.NumberTextBox;
import com.iambookmaster.client.common.SimpleAbstractParameterListBox;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ParameterListener;

public class NPCList extends FlexTable {
	
	private static final AppConstants constants = AppLocale.getAppConstants();
	private static final AppMessages messages = AppLocale.getAppMessages();
	
	private Model model;
	private Image addObject;
	private ChangeHandler changeListener;
	private SimpleAbstractParameterListBox<NPC> list;
	private ParameterListener listener;
	private CheckBox fightTogether;
	public NPCList(Model model) {
		this.model = model;
		setStyleName(Styles.BORDER);
		listener = new ParameterListener() {

			public void addNewParameter(AbstractParameter parameter) {
			}

			public void refreshAll() {
			}

			public void remove(AbstractParameter parameter) {
			}

			public void select(AbstractParameter parameter) {
			}

			public void update(AbstractParameter parameter) {
				for (int i = 1; i < getRowCount(); i++) {
					Widget widget = getWidget(i, 0);
					if (widget instanceof ItemWidget) {
						ItemWidget itemWidget = (ItemWidget) widget;
						if (itemWidget.object.getNpc()==parameter) {
							itemWidget.refresh();
						}
					}
				}
			}

			public void showInfo(AbstractParameter parameter) {
			}
			
		};
		model.addParamaterListener(listener);
		list = new SimpleAbstractParameterListBox<NPC>(NPC.class,model,true);
		
		list.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event) {
				//in other cases - fire event
				changeListener.onChange(null);
			}
		});
		addObject = new Image(Images.ADD_CONNECTION);
		addObject.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				NPC sel = list.getSelectedParameter();
				if (sel != null) {
					addObjectToList(new NPCParams(sel));
					list.setSelectedIndex(0);
					fireUpdate();
				}
			}
		});
		addObject.setStyleName(Styles.CLICKABLE);
		addObject.setTitle(constants.titleAddObjectToList());
		
		fightTogether = new CheckBox();
		fightTogether.setTitle(constants.ParagraphEditorFightTogether());
		fightTogether.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				fireUpdate();
			}
		});
		addControls();
	}
	
	private void addControls() {
		insertRow(0);
		addCell(0);
		setWidget(0, 0, list);
		addCell(0);
		setWidget(0, 1, addObject);
		addCell(0);
		setWidget(0, 2, fightTogether);
		addCell(0);
		setWidget(0, 3, new HTML("&nbsp;"));
		getColumnFormatter().setWidth(0, "99%");
		getColumnFormatter().setWidth(1, "1%");
		getColumnFormatter().setWidth(2, "1%");
		getColumnFormatter().setWidth(3, "1%");
	}

	public ArrayList<NPCParams> getSelectedNPCs() {
		ArrayList<NPCParams> res = new ArrayList<NPCParams>();
		NPC sel = list.getSelectedParameter();
//		if (sel != null) {
//			res.add(sel);
//		}
		for (int i = 1; i < getRowCount(); i++) {
			Widget widget = getWidget(i, 0);
			if (widget instanceof ItemWidget) {
				ItemWidget itemWidget = (ItemWidget) widget;
				res.add(itemWidget.object);
			}
		}
		if (res.size()>1) {
			Collections.sort(res,new Comparator<NPCParams>() {
				public int compare(NPCParams o1, NPCParams o2) {
					if (o1.getRound()==o2.getRound()) {
						if (o1.isFriend()==o2.isFriend()) {
							return o1.getNpc().getName().compareTo(o2.getNpc().getName());
						} else if (o1.isFriend()){
							return -1;
						} else {
							return 1;
						}
					} else {
						return o1.getRound()-o2.getRound();
					}
				}
			});
		}
		return res;
	}
	public void setSelectedObjects(List<NPCParams> selectedObject) {
		if (selectedObject == null || selectedObject.size()==0) {
			while (getRowCount()>1) {
				removeRow(1);
			}
			list.setSelectedIndex(0);
			return;
		}
		ArrayList<NPCParams> curr = getSelectedNPCs();
		if ((curr.size()==0 && selectedObject.size()==0) || curr.equals(selectedObject)) {
			return;
		} else {
			while (getRowCount()>1) {
				removeRow(1);
			}
			//add other objects
			Iterator<NPCParams> iterator = selectedObject.iterator();
			while (iterator.hasNext()) {
				addObjectToList(iterator.next());
			}
		}
	}

	private void addObjectToList(NPCParams bean) {
		ItemWidget widget = new ItemWidget(bean);
		int row = insertRow(getRowCount());
		addCell(row);
		setWidget(row, 0, widget);
		addCell(row);
		setWidget(row, 1, widget.rounds);
		addCell(row);
		setWidget(row, 2, widget.friend);
		addCell(row);
		setWidget(row, 3, widget.removeButton);
	}

	protected void onDetach() {
		super.onDetach();
		model.removeParamaterListener(listener);
	}

	public void addChangeHandler(ChangeHandler listener) {
		this.changeListener = listener;
	}
	
	private void removeObject(ItemWidget itemWidget) {
		for (int i = 1; i < getRowCount(); i++) {
			Widget widget = getWidget(i, 0);
			if (widget==itemWidget) {
				removeRow(i);
				fireUpdate();
				break;
			}
		}
	}
	
	
	public class ItemWidget extends Label implements ClickHandler,ChangeHandler {
		private Image removeButton;
		private NPCParams object;
		private NumberTextBox rounds;
		private CheckBox friend;
		
		public ItemWidget(NPCParams object) {
			this.object = object;
			setWordWrap(false);
			
			rounds = new NumberTextBox();
			rounds.setRange(0,99);
			rounds.setVisibleLength(2);
			rounds.setMaxLength(2);
			rounds.addChangeHandler(this);
			rounds.setTitle(constants.paragraphEditorEnemyRoundsTitle());
			
			friend = new CheckBox();
			friend.addClickHandler(this);
			friend.setTitle(constants.paragraphEditorEnemyFriendTitle());
			
			removeButton = new Image(Images.REMOVE);
			removeButton.setTitle(constants.titleRemoveObjectFromList());
			removeButton.addClickHandler(this);
			refresh();
		}

		public void refresh() {
			setText(object.getNpc().getName());
			friend.setValue(object.isFriend());
			rounds.setValue(object.getRound());
		}

		public void onClick(ClickEvent event) {
			if (event.getSource()==removeButton) {
				if (Window.confirm(messages.confirmRemoveNPCFromList(object.getNpc().getName()))) {
					removeObject(this);
				}
			} else if (event.getSource()==friend) {
				object.setFriend(friend.getValue());
				fireUpdate();
			}
		}

		public void onChange(ChangeEvent event) {
			if (event.getSource()==rounds) {
				object.setRound(rounds.getIntegerValue());
				fireUpdate();
			}
		}
		
	}


	public void showInfo(ObjectBean object) {
	}

	public void fireUpdate() {
		changeListener.onChange(null);
	}

	public boolean isFightTogether() {
		return fightTogether.getValue()==false;
	}

	public void setFightTogether(boolean fightTogether) {
		this.fightTogether.setValue(fightTogether==false);
	}

}
