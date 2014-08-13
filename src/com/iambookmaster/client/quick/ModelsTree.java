package com.iambookmaster.client.quick;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.Images;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.NPC;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.common.ScrollContainer;
import com.iambookmaster.client.editor.ModelPersist;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ParameterListener;

public class ModelsTree extends ScrollContainer {
	
	private AppConstants appConstants = AppLocale.getAppConstants();
	
	private HashMap<AbstractParameter,AbstractParameterWidget> widgets;
	private Tree tree;
	private ModelPersist model;
//	private ObjectWidget selected;
	private boolean activationNeed=true;
	private TreeItem npcItem;
	private TreeItem parametersItem;
	private TreeItem battlesItem;
	private TreeItem modificatorsItem;
	private TreeItem alchemyItem;
	private ParameterListener listener;

	public void activate() {
		if (activationNeed) {
			activationNeed = false;
			resetHeight();
		}
	}
	public void activateLater() {
		activationNeed = true;
	}
	
	public ModelsTree(Model mod) {
		this.model = (ModelPersist)mod;
		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setSize("100%", "100%");
		tree = new Tree();
		tree.setWidth("100%");
		
		npcItem = createBasicItem(appConstants.parametersNPCName(),appConstants.parametersNPCAddTitle(),new ClickHandler() {
			public void onClick(ClickEvent event) {
				model.addNewNPC(null);
			}
		},Images.NPC_ICON,new ClickHandler() {
			public void onClick(ClickEvent event) {
				model.sortNPC();
			}
		});
		
		parametersItem = createBasicItem(appConstants.parametersName(),appConstants.parametersAddTitle(),new ClickHandler() {
			public void onClick(ClickEvent event) {
				model.addNewParameter(null);
			}
		},Images.PARAMETER_ICON,new ClickHandler() {
			public void onClick(ClickEvent event) {
				model.sortParameters();
			}
		});
		battlesItem = createBasicItem(appConstants.parametersBattleName(),appConstants.parametersBattleAddTitle(),new ClickHandler() {
			public void onClick(ClickEvent event) {
				model.addNewBattle(null);
			}
		},Images.BATTLE_ICON,new ClickHandler() {
			public void onClick(ClickEvent event) {
				model.sortBattles();
			}
		});
		
		modificatorsItem =  createBasicItem(appConstants.parametersModificatorName(),appConstants.parametersModificatorAddTitle(),new ClickHandler() {
			public void onClick(ClickEvent event) {
				model.addNewModificator(null);
			}
		},Images.MODIFICATOR_ICON,new ClickHandler() {
			public void onClick(ClickEvent event) {
				model.sortModificators();
			}
		});
		
		alchemyItem =  createBasicItem(appConstants.parametersConvertersName(),appConstants.parametersConverteAddTitle(),new ClickHandler() {
			public void onClick(ClickEvent event) {
				model.addNewAlchemy(null);
			}
		},Images.PARAMETER_CONVERTER,new ClickHandler() {
			public void onClick(ClickEvent event) {
				model.sortAlchemy();
			}
		});
		
		
		
		mainPanel.add(tree);
		mainPanel.setCellHeight(tree,"1%");
		
		addStyleName("objects_list");
		setScrollWidget(mainPanel);
		HTML html = new HTML("&nbsp;");
		html.setStyleName("location_list_filler");
		mainPanel.add(html);
		mainPanel.setCellHeight(html,"99%");
		
		listener = new ParameterListener(){
			public void addNewParameter(AbstractParameter parameter) {
				if (parameter instanceof Parameter) {
					addParameter((Parameter) parameter,true);
				} else if (parameter instanceof NPC) {
					addNPC((NPC) parameter,true);
				} else if (parameter instanceof Battle) {
					addBattle((Battle) parameter,true);
				} else if (parameter instanceof Modificator) {
					addModificator((Modificator) parameter,true);
				} else if (parameter instanceof Alchemy) {
					addAlchemy((Alchemy) parameter,true);
				}
			}

			public void refreshAll() {
				reloadTree();
			}

			public void remove(AbstractParameter parameter) {
				TreeItem widget = widgets.get(parameter);
				if (widget != null) {
					widget.getParentItem().removeItem(widget);
				}
			}

			public void select(AbstractParameter parameter) {
				TreeItem widget = widgets.get(parameter);
				if (widget != null) {
					TreeItem selected = tree.getSelectedItem();
					if (selected != widget) {
						selected.setSelected(false);
						widget.setSelected(true);
					}
				}
			}

			public void update(AbstractParameter parameter) {
				AbstractParameterWidget widget = widgets.get(parameter);
				if (widget != null) {
					widget.update(parameter);
				}
			}

			public void showInfo(AbstractParameter parameter) {
			}
			
		};
		model.addParamaterListener(listener);
		reloadTree();
	}
	
	private void addBattle(Battle parameter,boolean show) {
		addItemToNode(battlesItem,new AbstractParameterWidget(parameter),show);
	}
	
	private void addNPC(NPC parameter,boolean show) {
		addItemToNode(npcItem,new AbstractParameterWidget(parameter),show);
	}
	
	private void addParameter(Parameter parameter,boolean show) {
		addItemToNode(parametersItem,new AbstractParameterWidget(parameter),show);
	}
	
	private void addModificator(Modificator parameter,boolean show) {
		addItemToNode(modificatorsItem,new AbstractParameterWidget(parameter),show);
	}
	
	private void addAlchemy(Alchemy parameter,boolean show) {
		addItemToNode(alchemyItem,new AbstractParameterWidget(parameter),show);
	}
	
	private void addItemToNode(TreeItem treeItem, AbstractParameterWidget abstractParameterWidget,boolean show) {
		treeItem.addItem(abstractParameterWidget);
		widgets.put(abstractParameterWidget.parameter, abstractParameterWidget);
		if (show) {
			if (treeItem.getState()==false) {
				treeItem.setState(true);
			}
			tree.setSelectedItem(abstractParameterWidget);
			ensureVisible(abstractParameterWidget);
			model.selectParameter(abstractParameterWidget.getParameter(),listener);
		}
	}
	
	private TreeItem createBasicItem(String name,String title, ClickHandler handler,String imageURL, ClickHandler sortHandler) {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setSize("1%", "1%");
		panel.add(new Image(imageURL));
		Label label = new Label(name,false);
		panel.add(label);
		Image image = new Image(Images.SORT);
		image.addClickHandler(sortHandler);
		image.setTitle(appConstants.sort());
		image.setStyleName(Styles.CLICKABLE);
		panel.add(image);
		image = new Image(Images.ADD_CONNECTION);
		image.addClickHandler(handler);
		image.setTitle(title);
		image.setStyleName(Styles.CLICKABLE);
		panel.add(image);
		final TreeItem item = new TreeItem(panel);
		applyItemStyle(item);
		tree.addItem(item);
		return item;
	}
	
	private void applyItemStyle(TreeItem item) {
		DOM.setStyleAttribute(item.getElement(),"paddingBottom","0px");
		DOM.setStyleAttribute(item.getElement(),"paddingTop","1px");
		DOM.setStyleAttribute(DOM.getChild(item.getElement(),0),"display","inline-block");
		item.setSize("1%", "1%");
	}
	private void reloadTree() {
		widgets=new HashMap<AbstractParameter, AbstractParameterWidget>();
		npcItem.removeItems();
		parametersItem.removeItems();
		battlesItem.removeItems();
		alchemyItem.removeItems();
		modificatorsItem.removeItems();
		ArrayList<AbstractParameter> params = model.getParameters();
		for (AbstractParameter abstractParameter : params) {
			if (abstractParameter instanceof Parameter) {
				addParameter((Parameter) abstractParameter,false);
			} else if (abstractParameter instanceof NPC) {
				addNPC((NPC) abstractParameter,false);
			} else if (abstractParameter instanceof Battle) {
				addBattle((Battle) abstractParameter,false);
			} else if (abstractParameter instanceof Modificator) {
				addModificator((Modificator) abstractParameter,false);
			} else if (abstractParameter instanceof Alchemy) {
				addAlchemy((Alchemy) abstractParameter,false);
			} else {
				throw new IllegalArgumentException("Unsupported class "+abstractParameter);
			}
		}
	}
	
	public AbstractParameter getSelected() {
		TreeItem item = tree.getSelectedItem();
		if (item instanceof AbstractParameterWidget) {
			AbstractParameterWidget widget = (AbstractParameterWidget) item;
			return widget.getParameter();
		} else {
			return null;
		}
	}

	public class AbstractParameterWidget extends TreeItem implements ClickHandler{
		private Label title;
		private Image findInfo;
		private Image remove;
		private AbstractParameter parameter;
		
		public AbstractParameterWidget(AbstractParameter parameter) {
			applyItemStyle(this);
			title = new Label();
			title.setWordWrap(false);
			remove = new Image(Images.REMOVE);
			remove.setTitle(appConstants.buttonRemove());
			remove.addClickHandler(this);
			remove.setStyleName(Styles.CLICKABLE);
			findInfo = new Image(Images.PREVIEW);
			findInfo.addClickHandler(this);
			findInfo.setTitle(AppLocale.getAppConstants().quicShowInfo());
			HorizontalPanel panel = new HorizontalPanel();
			panel.setSize("1%", "1%");
			panel.add(title);
			panel.add(findInfo);
			panel.add(remove);
			setWidget(panel);
			update(parameter);
		}
		
		public AbstractParameter getParameter() {
			return parameter;
		}
		
		public void update(AbstractParameter parameter) {
			this.parameter = parameter;
			title.setText(parameter.getName());
		}
		
		public void setTitle(String title) {
			this.title.setText(title);
		}

		public void onClick(ClickEvent event) {
			if (event.getSource()==remove) {
				if (Window.confirm(appConstants.confirmRemove())) {
					model.removeParameter(getParameter(),null);
				}
			} else if (event.getSource()==findInfo) {
				model.showInfo(parameter);
			}
		}
	}
	
}
