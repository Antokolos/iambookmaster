package com.iambookmaster.client;

import java.util.Collections;
import java.util.Comparator;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.NPC;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.beans.Picture;
import com.iambookmaster.client.beans.Sound;
import com.iambookmaster.client.common.ResizeListener;
import com.iambookmaster.client.common.TrueStackPanelListener;
import com.iambookmaster.client.common.TrueVerticalSplitPanel;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.ContentListener;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ObjectListener;
import com.iambookmaster.client.model.ParagraphConnectionListener;
import com.iambookmaster.client.model.ParagraphListener;
import com.iambookmaster.client.model.ParameterListener;
import com.iambookmaster.client.model.SettingsListener;
import com.iambookmaster.client.paragraph.ParagraphsListView;
import com.iambookmaster.client.quick.ModelsTree;
import com.iambookmaster.client.quick.ObjectsList;
import com.iambookmaster.client.quick.PictiresList;
import com.iambookmaster.client.quick.QuickAbstractParameterEditor;
import com.iambookmaster.client.quick.QuickAlchemyEditor;
import com.iambookmaster.client.quick.QuickBattleEditor;
import com.iambookmaster.client.quick.QuickModificatorEditor;
import com.iambookmaster.client.quick.QuickNPCEditor;
import com.iambookmaster.client.quick.QuickParameterEditor;
import com.iambookmaster.client.quick.QuickViewObjectEditor;
import com.iambookmaster.client.quick.QuickViewParagraphConnectionEditor;
import com.iambookmaster.client.quick.QuickViewParagraphEditor;
import com.iambookmaster.client.quick.QuickViewPictureEditor;
import com.iambookmaster.client.quick.QuickViewSoundEditor;
import com.iambookmaster.client.quick.QuickViewWidget;
import com.iambookmaster.client.quick.SettingsList;
import com.iambookmaster.client.quick.SoundsList;

public class QuickPanel extends VerticalPanel {
	private AppConstants appConstants = AppLocale.getAppConstants();

	public static final int LOCATIONS = 0;
	public static final int OBJECTS = 1;
	public static final int PARAMETERS = 2;
	public static final int PICTURES = 3;
	public static final int SOUNDS = 4;
	public static final int SETTINGS = 5;

	private TrueStackPanel stackPanel;
	private TrueVerticalSplitPanel splitPanel;
//	private ScrollContainer quickViewScroll;
	private VerticalPanel quickView;
	private ParagraphsListView locationsList;
	private ObjectsList objectsList;
	private SettingsList settings;
	private SoundsList sounds;
	private PictiresList pictures;
	private Model model;
	private ParagraphListener locationListener;
	private ContentListener contentListener;
	private ParagraphConnectionListener locationConnectionListener;
	private ObjectListener objectListener;
	private QuickViewWidget quickViewWidget;
	private SettingsListener settingsListener;
	private ModelsTree parameters;
	private ParameterListener paramtersListener;

	private TrueStackPanelListener stackPanelListener;
	
	public QuickPanel(Model md, Widget menu) {
		this.model = md;
		settingsListener = new SettingsListener() {
			public void settingsWereUpated() {
			}
		};
		locationListener = new ParagraphListener() {
			public void addNewParagraph(Paragraph location) {
				quickViewLocation(location);
			}

			public void edit(Paragraph location) {
			}

			public void refreshAll() {
				quickViewNothing();
			}

			public void select(Paragraph location) {
				quickViewLocation(location);
			}

			public void unselect(Paragraph location) {
			}

			public void update(Paragraph location) {
			}

			public void remove(Paragraph location) {
				quickViewNothing();
			}
			
		};
		locationConnectionListener = new ParagraphConnectionListener() {
			public void refreshAll() {
			}
			public void select(ParagraphConnection connection) {
				quickViewLocationConnection(connection);
			}
			public void unselect(ParagraphConnection connection) {
			}
			public void update(ParagraphConnection connection) {
			}
			public void remove(ParagraphConnection connection) {
				quickViewNothing();
			}
			public void addNew(ParagraphConnection connection) {
				quickViewLocationConnection(connection);
			}
			
		};
		objectListener = new ObjectListener() {
			public void addNewObject(ObjectBean object) {
			}
			public void refreshAll() {
			}
			public void select(ObjectBean object) {
				quickViewObject(object);
			}
			public void unselect(ObjectBean object) {
			}
			public void update(ObjectBean object) {
			}
			public void remove(ObjectBean object) {
				quickViewNothing();
			}
			public void showInfo(ObjectBean object) {
			}
		};
		contentListener = new ContentListener() {

			public void addNew(Picture picture) {
			}

			public void addNew(Sound sound) {
			}

			public void refreshAll() {
			}

			public void remove(Picture picture) {
				quickViewNothing();
			}

			public void remove(Sound sound) {
				quickViewNothing();
			}

			public void select(Sound sound) {
				quickViewSound(sound);
			}

			public void select(Picture picture) {
				quickViewPicture(picture);
			}

			public void unselect(Sound sound) {
			}

			public void unselect(Picture picture) {
			}

			public void update(Picture picture) {
			}

			public void update(Sound sound) {
			}

			public void showInfo(Picture picture) {
			}

			public void showInfo(Sound sound) {
			}
			
		};
		
		paramtersListener = new ParameterListener() {
			public void addNewParameter(AbstractParameter parameter) {
			}

			public void refreshAll() {
			}

			public void remove(AbstractParameter parameter) {
				quickViewNothing();
			}

			public void select(AbstractParameter parameter) {
				if (parameters.getSelected() != null) {
					quickViewParameter(parameters.getSelected());
				} else {
					quickViewNothing();
				}
			}

			public void update(AbstractParameter parameter) {
			}

			public void showInfo(AbstractParameter parameter) {
			}
			
		};
		model.addParamaterListener(paramtersListener);
		model.addParagraphListener(locationListener);
		model.addParagraphConnectionListener(locationConnectionListener);
		model.addObjectsListener(objectListener);
		model.addContentListener(contentListener);
		model.addSettingsListener(settingsListener);
		setSize("100%", "100%");
		setStyleName("quick_panel");
		add(menu);
		setCellHeight(menu, "1%");
		setCellWidth(menu, "100%");
		
		splitPanel = new TrueVerticalSplitPanel(false,true);
		stackPanel = new TrueStackPanel();
		locationsList = new ParagraphsListView(model);
		stackPanel.add(locationsList);
		stackPanel.setStackHeader(LOCATIONS,appConstants.quickParagraphs(),Images.LOCATIONS_LIST,appConstants.quickParagraphsTitle(),new EventListener() {
			private ParagraphOrderMenu menu = new ParagraphOrderMenu(); 
			public void onBrowserEvent(Event event) {
				menu.setPopupPosition(event.getClientX(),event.getClientY());
				menu.show();
			}
		});
		stackPanel.showStack(LOCATIONS);
		objectsList = new ObjectsList(model);
		stackPanel.add(objectsList);
		stackPanel.setStackHeader(OBJECTS,appConstants.quickObjects(),Images.OBJECTS_LIST,appConstants.quickObjectsTitle(),new EventListener() {
			public void onBrowserEvent(Event event) {
				Collections.sort(model.getObjects(),new Comparator<ObjectBean>() {
					public int compare(ObjectBean o1, ObjectBean o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
				model.refreshObjects();
			}
		});
		parameters = new ModelsTree(model);
		stackPanel.add(parameters);
		stackPanel.setStackHeader(PARAMETERS,appConstants.quickModels(),Images.MODELS_TREE,appConstants.quickModelsTitle());
		pictures = new PictiresList(model);
		stackPanel.add(pictures);
		stackPanel.setStackHeader(PICTURES,appConstants.quickImages(),Images.IMAGES_LIST,appConstants.quickImagesTitle(),new EventListener() {
			public void onBrowserEvent(Event event) {
				Collections.sort(model.getPictures(),new Comparator<Picture>() {
					public int compare(Picture o1, Picture o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
				model.refreshPictures();
			}
		});
		sounds = new SoundsList(model);
		stackPanel.add(sounds);
		stackPanel.setStackHeader(SOUNDS,appConstants.quickSounds(),Images.SOUNDS_LIST,appConstants.quickSoundsTitle(),new EventListener() {
			public void onBrowserEvent(Event event) {
				Collections.sort(model.getSounds(),new Comparator<Sound>() {
					public int compare(Sound o1, Sound o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
				model.refreshSounds();
			}
		});
		settings = new SettingsList(model) {
			public void onSelected() {
				updateSettingsWidget();
			}
			
		};
		stackPanel.add(settings);
		stackPanel.setStackHeader(SETTINGS,appConstants.quickSettings(),Images.SETTINGS,appConstants.quickSettingsTitle());
		
		stackPanel.selectHeader(LOCATIONS, true);
		stackPanelListener = new TrueStackPanelListener() {
			public void activate(int index) {
				switch (index) {
				case LOCATIONS:
					locationsList.activate();
					if (locationsList.getSelected() != null) {
						quickViewLocation(locationsList.getSelected());
					} else {
						quickViewNothing();
					}
					break;
				case OBJECTS:
					locationsList.deactivate();
					objectsList.activate();
					if (objectsList.getSelected() != null) {
						quickViewObject(objectsList.getSelected());
					} else {
						quickViewNothing();
					}
					break;
				case PARAMETERS:
					locationsList.deactivate();
					parameters.activate();
					if (parameters.getSelected() != null) {
						quickViewParameter(parameters.getSelected());
					} else {
						quickViewNothing();
					}
					break;
				case PICTURES:
					locationsList.deactivate();
					pictures.activate();
					if (pictures.getSelected() != null) {
						quickViewPicture(pictures.getSelected());
					} else {
						quickViewNothing();
					}
					break;
				case SOUNDS:
					locationsList.deactivate();
					sounds.activate();
					if (sounds.getSelected() != null) {
						quickViewSound(sounds.getSelected());
					} else {
						quickViewNothing();
					}
					break;
				case SETTINGS:
					locationsList.deactivate();
					settings.activate();
					updateSettingsWidget();
					break;
				}
			}
		};
		stackPanel.setListener(stackPanelListener);
		splitPanel.setTopWidget(stackPanel);
		//quick view-edit
		quickView = new VerticalPanel();
		quickView.setSize("100%", "100%");
		quickView.setStyleName("editor_panel");
//		quickViewScroll = new ScrollContainer();
//		quickViewScroll.setScrollWidget(quickView);
//		DOM.setStyleAttribute(quickViewScroll.getElement(), "border", "1px solid green");
		splitPanel.setBottomWidget(quickView);
		
		splitPanel.setSize("100%", "100%");
		splitPanel.setSplitPosition("50%");
		splitPanel.setSplitEnabled(true);
		splitPanel.addResizeListener(new ResizeListener() {
			public void onResize(Widget panel) {
//				quickViewScroll.resetHeight();
			}
		});
		add(splitPanel);
		setCellHeight(splitPanel, "100%");
		setCellWidth(splitPanel, "100%");
		splitPanel.addResizeListener(new ResizeListener() {
			public void onResize(Widget panel) {
				QuickPanel.this.onResize();
			}
		});
		locationsList.activate();
	}
	
	private void updateSettingsWidget() {
		QuickViewWidget widget = settings.getSelectedWidget();
		if (widget==null) {
			quickViewNothing();
		} else {
			quickViewSettings(widget);
		} 
	}

	public void quickViewSettings(QuickViewWidget widget) {
		addEditor((Widget)widget);
	}

	public void quickViewNothing() {
		clearQuickView();
		quickView.add(new HTML("&nbsp;"));
		quickViewWidget = null;
	}

	public void quickViewLocation(Paragraph location) {
		if (quickViewWidget instanceof QuickViewParagraphEditor) {
			QuickViewParagraphEditor editor = (QuickViewParagraphEditor) quickViewWidget;
			editor.open(location);
		} else {
			clearQuickView();
			QuickViewParagraphEditor editor = new QuickViewParagraphEditor(model,location);
			addEditor(editor);
		}
	}

	private void clearQuickView() {
		if (quickViewWidget != null) {
			quickViewWidget.close();
		}
		quickView.clear();
	}

	public void quickViewObject(ObjectBean object) {
		if (quickViewWidget instanceof QuickViewObjectEditor) {
			QuickViewObjectEditor editor = (QuickViewObjectEditor) quickViewWidget;
			editor.open(object);
		} else {
			clearQuickView();
			QuickViewObjectEditor editor = new QuickViewObjectEditor(model,object);
			addEditor(editor);
		}
	}

	protected void quickViewParameter(AbstractParameter selected) {
		if (selected instanceof Parameter) {
			QuickAbstractParameterEditor editor;
			if (quickViewWidget instanceof QuickParameterEditor) {
				editor = (QuickParameterEditor) quickViewWidget;
			} else {
				clearQuickView();
				editor = new QuickParameterEditor(model);
				addEditor(editor);
			}
			editor.open((Parameter)selected);
		} else if (selected instanceof NPC) {
			QuickNPCEditor editor;
			if (quickViewWidget instanceof QuickNPCEditor) {
				editor = (QuickNPCEditor) quickViewWidget;
			} else {
				clearQuickView();
				editor = new QuickNPCEditor(model);
				addEditor(editor);
			}
			editor.open((NPC)selected);
		} else if (selected instanceof Battle) {
			QuickAbstractParameterEditor editor;
			if (quickViewWidget instanceof QuickBattleEditor) {
				editor = (QuickBattleEditor) quickViewWidget;
			} else {
				clearQuickView();
				editor = new QuickBattleEditor(model);
				addEditor(editor);
			}
			editor.open((Battle)selected);
		} else if (selected instanceof Modificator) {
			QuickAbstractParameterEditor editor;
			if (quickViewWidget instanceof QuickModificatorEditor) {
				editor = (QuickModificatorEditor) quickViewWidget;
			} else {
				clearQuickView();
				editor = new QuickModificatorEditor(model);
				addEditor(editor);
			}
			editor.open((Modificator)selected);
		} else if (selected instanceof Alchemy) {
			QuickAbstractParameterEditor editor;
			if (quickViewWidget instanceof QuickAlchemyEditor) {
				editor = (QuickAlchemyEditor) quickViewWidget;
			} else {
				clearQuickView();
				editor = new QuickAlchemyEditor(model);
				addEditor(editor);
			}
			editor.open((Alchemy)selected);
		} else {
			quickViewNothing();
		}
	}

	public void quickViewSound(Sound object) {
		if (quickViewWidget instanceof QuickViewSoundEditor) {
			QuickViewSoundEditor editor = (QuickViewSoundEditor) quickViewWidget;
			editor.open(object);
		} else {
			clearQuickView();
			QuickViewSoundEditor editor = new QuickViewSoundEditor(model,object);
			addEditor(editor);
		}
	}

	public void quickViewPicture(Picture object) {
		if (quickViewWidget instanceof QuickViewPictureEditor) {
			QuickViewPictureEditor editor = (QuickViewPictureEditor) quickViewWidget;
			editor.open(object);
		} else {
			clearQuickView();
			QuickViewPictureEditor editor = new QuickViewPictureEditor(model,object);
			addEditor(editor);
		}
	}

	private void addEditor(Widget editor) {
		quickView.clear();
		quickView.add(editor);
		quickView.setCellWidth(editor,"100%");
		quickView.setCellHeight(editor,"100%");
		quickViewWidget = (QuickViewWidget)editor;
	}

	public void quickViewLocationConnection(ParagraphConnection connection) {
		if (quickViewWidget instanceof QuickViewParagraphConnectionEditor) {
			QuickViewParagraphConnectionEditor editor = (QuickViewParagraphConnectionEditor) quickViewWidget;
			editor.open(connection);
		} else {
			clearQuickView();
			QuickViewParagraphConnectionEditor editor = new QuickViewParagraphConnectionEditor(model,connection);
			addEditor(editor);
		}
	}

	public void onResize() {
//		quickViewScroll.resetHeight();
		locationsList.activateLater();
		objectsList.activateLater();
		pictures.activateLater();
		sounds.activateLater();
		settings.activateLater();
		parameters.activateLater();
		switch (stackPanel.getSelectedIndex()) {
		case LOCATIONS:
			locationsList.activate();
			break;
		case OBJECTS:
			objectsList.activate();
			break;
		case PICTURES:
			pictures.activate();
			break;
		case SOUNDS:
			sounds.activate();
			break;
		case PARAMETERS:
			parameters.activate();
			break;
		case SETTINGS:
			settings.activate();
			break;
		}
	}

	public void activate(int widget) {
		stackPanel.showStack(widget);
		stackPanel.selectHeader(widget, true);
		stackPanelListener.activate(widget);
		
	}
	
	public class ParagraphOrderMenu extends PopupPanel {

		private MenuBar newConnectionMenu;
		
		public ParagraphOrderMenu() {
			super(true,true);
			newConnectionMenu = new MenuBar(true);
			newConnectionMenu.addItem(appConstants.sortByName(),new Command() {
				public void execute() {
					ParagraphOrderMenu.this.hide();
					Collections.sort(model.getParagraphs(),new Comparator<Paragraph>() {
						public int compare(Paragraph o1, Paragraph o2) {
							return o1.getName().compareToIgnoreCase(o2.getName());
						}
					});
					model.refreshParagraphs();
				}
			});
			newConnectionMenu.addItem(appConstants.sortByNumber(),new Command() {
				public void execute() {
					ParagraphOrderMenu.this.hide();
					Collections.sort(model.getParagraphs(),new Comparator<Paragraph>() {
						public int compare(Paragraph o1, Paragraph o2) {
							return o1.getNumber()-o2.getNumber();
						}
					});
					model.refreshParagraphs();
				}
			});
			add(newConnectionMenu);
		}
		
	}	
}
