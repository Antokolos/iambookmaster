package com.iambookmaster.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.DiceValue;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.common.Base64Coder;
import com.iambookmaster.client.common.ExchangePanel;
import com.iambookmaster.client.common.FileExchangeClient;
import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.common.MaskPanel;
import com.iambookmaster.client.common.ResizeListener;
import com.iambookmaster.client.common.TrueHorizontalSplitPanel;
import com.iambookmaster.client.common.XMLBuilder;
import com.iambookmaster.client.editor.ModelOptimizer;
import com.iambookmaster.client.editor.ModelPersist;
import com.iambookmaster.client.importer.ImportTextPanel;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.paragraph.ExportBookCallback;
import com.iambookmaster.client.wizards.RegenerateTextDialog;

public class MasterPanel extends VerticalPanel {
	private AppConstants appConstants = AppLocale.getAppConstants();
	private AppMessages appMessages = AppLocale.getAppMessages();
	
	private static final FileExchangeClient fileExchange = new FileExchangeClient();
	
	private TrueHorizontalSplitPanel splitPanel;
	private MenuBar mainMenu;
	private EditorPanel editorPanel;
	private QuickPanel quickPanel;
	private ModelPersist model;
	private ExchangePanel exchangePanel;
	private AboutPanel aboutPanel;

	public MasterPanel(Model model) {
		this.model = (ModelPersist)model;
		setSize("100%", "100%");
		mainMenu = new MainMenu();
		splitPanel = new TrueHorizontalSplitPanel();
		HorizontalPanel panel = new HorizontalPanel();
		panel.setSize("100%", "100%");
		panel.add(mainMenu);
		panel.setCellWidth(mainMenu, "100%");
		panel.setCellHeight(mainMenu, "100%");
		DeferredCommand.addCommand(new Command() {
			public void execute() {
//				Encoded version of "http://www.iambookmaster.com/remote/status.js"
//				String url = Base64Coder.decodeString("aHR0cDovL3d3dy5pYW1ib29rbWFzdGVyLmNvbS9yZW1vdGUvc3RhdHVzLmpz")+"?time="+new Date().getTime();
				String url = "http://localhost:8080/iambookmaster/remote/status.js"+"?time="+new Date().getTime();
				check4updates(url,MasterPanel.this);
			}
		});
		quickPanel = new QuickPanel(model,mainMenu);
		splitPanel.setLeftWidget(quickPanel);
		
		editorPanel = new EditorPanel(model);
		splitPanel.setRightWidget(editorPanel);
		
		splitPanel.setSize("100%", "100%");
		splitPanel.setSplitEnabled(true);
		splitPanel.setSplitPosition("30%");
		splitPanel.addResizeListener(new ResizeListener(){
			public void onResize(Widget panel) {
				MasterPanel.this.onResize();
			}
			
		});
		add(splitPanel);
		setCellHeight(splitPanel, "100%");
		setCellWidth(splitPanel, "100%");
		
		exchangePanel = new ExchangePanel() {
			//action for load
			public boolean processLoad(String text) {
				Model model = new Model(appConstants,appMessages);
				if (model.fromJSON(text)) {
					//success
					quickPanel.activate(QuickPanel.SETTINGS);
					MasterPanel.this.model.apply(model);
					return true;
				} else {
					return false;
				}
			}
			
		};
		
		modelLoaderInit(this);
		new Timer() {
			@Override
			public void run() {
				onResize();
			}
		}.schedule(4000);
	}
	
	private native void check4updates(String url,MasterPanel panel)/*-{
		$doc.iambookmaster = function(status) {panel.@com.iambookmaster.client.MasterPanel::statusCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(status);;}
		var headID = $doc.getElementsByTagName("head")[0];         
		var newScript = $doc.createElement('script');
		newScript.type = 'text/javascript';
		newScript.src = url;
		headID.appendChild(newScript);		
	}-*/;
	
	private native void modelLoaderInit(MasterPanel panel)/*-{
		$doc.iambookmasterLoad = function(data) {panel.@com.iambookmaster.client.MasterPanel::loadCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(data);;}
	}-*/;

	/**
	 * This methos is called from JavaScript
	 * @param status
	 */
	@SuppressWarnings("unused")
	private void loadCallback(JavaScriptObject data){
		JSONParser parser = JSONParser.getInstance();
		int code = parser.propertyNoCheckInt(data, ServerExchangePanel.FIELD_CODE);
		String message = parser.propertyNoCheckString(data, ServerExchangePanel.FIELD_MESSAGE);
		switch (code) {
		case ServerExchangePanel.LOAD_OK:
			final JavaScriptObject obj = (JavaScriptObject)parser.propertyNoCheck(data, ServerExchangePanel.FIELD_DATA);
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					try {
						Model model = new Model(AppLocale.getAppConstants(),AppLocale.getAppMessages());
						model.restore(obj,JSONParser.getInstance());
						MasterPanel.this.model.apply(model);
						DeferredCommand.addCommand(new Command(){
							public void execute() {
								editorPanel.activateParagraphMap();
							}
						});
					} catch (Exception e) {
						Window.alert("Error parsing data\n"+e.toString()+'\n'+e.getMessage());
						e.printStackTrace();
					}
					editorPanel.serverLogin();
					MaskPanel.hide();
				}
			});
			break;
		default:
			if (data==null) {
				Window.alert("Unknown error during loading");
			} else {
				Window.alert(message);
			}
			editorPanel.serverDone();
			MaskPanel.hide();
		}
		
	}
	/**
	 * This method is called from JavaScript
	 * @param status
	 */
	@SuppressWarnings("unused")
	private void statusCallback(JavaScriptObject status){
		JSONParser parser = JSONParser.getInstance();
		int maj = parser.propertyNoCheckInt(status, "major");
		int min = parser.propertyNoCheckInt(status, "minor");
		String server = parser.propertyNoCheckString(status, "server");
		if (server != null) {
			//alternative URL for server module
			editorPanel.setServerURL(server);
		}
		if (maj>Model.HI_VERSION || min>Model.LO_VERSION) {
			if (aboutPanel==null) { 
				aboutPanel = new AboutPanel();
			}
			aboutPanel.centerAndShow(maj,min);
		}
	}
	
	private Command addNewLocations = new Command() {
		public void execute() {
			model.addNewParagraph(null);
		}
	};
	private Command addNewObject = new Command() {
		public void execute() {
			model.addNewObject(null);
		}
	};
	private Command addNewSound = new Command() {
		public void execute() {
			model.addSound(null);
		}
	};
	private Command addNewImage = new Command() {
		public void execute() {
			model.addPicture(null);
		}
	};

	private Command validateAll = new Command() {
		public void execute() {
			editorPanel.validateAll();
		}
	};
	
	private Command validateText = new Command() {
		public void execute() {
			editorPanel.validateText();
		}
	};
	
	private Command validateMap = new Command() {
		public void execute() {
			editorPanel.validateMap();
		}
	};
	
	private Command wholeRead = new Command() {
		public void execute() {
			editorPanel.wholeRead();
		}
	};
	
	private Command successRead = new Command() {
		public void execute() {
			editorPanel.successRead();
		}
	};
	
	private Command longAndShort = new Command() {
		public void execute() {
			editorPanel.successLongAndShort();
		}
	};
	
	private Command removeDuplicateConnections = new Command() {

		public void execute() {
			ArrayList<ParagraphConnection> connections = model.getParagraphConnections();
			HashSet<String> keys = new HashSet<String>(connections.size());
			for (Iterator<ParagraphConnection> iterator = connections.iterator(); iterator.hasNext();) {
				ParagraphConnection connection = iterator.next();
				String id = connection.getFromId()+","+connection.getToId();
				if (keys.contains(id)) {
					System.out.println("Duplicate connection from "+connection.getFrom().getNumber()+"."+connection.getFrom().getName()+" -> "+connection.getTo().getNumber()+"."+connection.getTo().getName());
					iterator.remove();
				} else {
					keys.add(id);
				}
				
			}
			
		}
	};
	
	private Command connectionByNumbers = new Command() {
		public void execute() {
			String numStr = Window.prompt(appConstants.paragraphByNumberPrompt(), "0");
			if (numStr==null) {
				return;
			}
			int numFrom;
			try {
				numFrom = Integer.parseInt(numStr);
			} catch (NumberFormatException e) {
				Window.alert(appMessages.paragraphByNumberWrong(numStr));
				return;
			}
			numStr = Window.prompt(appConstants.paragraphByNumberLinkedPrompt(), "0");
			if (numStr==null) {
				return;
			}
			int numTo;
			try {
				numTo = Integer.parseInt(numStr);
			} catch (NumberFormatException e) {
				Window.alert(appMessages.paragraphByNumberWrong(numStr));
				return;
			}
			Paragraph from=null;
			Paragraph to=null;
			ArrayList<Paragraph> list = model.getParagraphs();
			for (Paragraph paragraph : list) {
				if (paragraph.getNumber()==numFrom) {
					//found
					from = paragraph;
					if (to != null) {
						break;
					}
				}
				if (paragraph.getNumber()==numTo) {
					//found
					to = paragraph;
					if (from != null) {
						break;
					}
				}
			}
			if (from==null) {
				Window.alert(appMessages.paragraphByNumberWrong(String.valueOf(numFrom)));
				return;
			}
			if (to==null) {
				Window.alert(appMessages.paragraphByNumberWrong(numStr));
				return;
			}
			if (from==to) {
				Window.alert(appMessages.paragraphConnectionTheSameNumber(numStr));
				return;
			}
			list=null;
			ArrayList<ParagraphConnection> list2 = model.getParagraphConnections();
			for (ParagraphConnection connection : list2) {
				if ((connection.getFrom()==from && connection.getTo()==to) ||
				    (connection.getTo()==from && connection.getFrom()==to)) {
					//found
					model.selectParagraphConnection(connection, null);
					return;
				}
			}
			if (Window.confirm(appMessages.paragraphConnectionCreateConfirm(numFrom,from.getName(),numTo,to.getName()))) {
				ParagraphConnection connection = new ParagraphConnection();
				if (model.getSettings().isOneWayConnectionsOnly()) {
					connection.setBothDirections(false);
				} else {
					connection.setBothDirections(!Window.confirm(appConstants.paragraphConnectionCreateTwoWays()));
				}
				connection.setFrom(from);
				connection.setTo(to);
				model.addParagraphConnection(connection,null);
				model.selectParagraphConnection(connection,null);
			}
		}
	};
	
	private Command paragraphByNumber = new Command() {
		public void execute() {
			String numStr = Window.prompt(appConstants.paragraphByNumberPrompt(), "0");
			if (numStr==null) {
				return;
			}
			try {
				int num = Integer.parseInt(numStr);
				ArrayList<Paragraph> list = model.getParagraphs();
				for (Paragraph paragraph : list) {
					if (paragraph.getNumber()==num) {
						//found
						model.selectParagraph(paragraph, null);
						return;
					}
				}
				Window.alert(appMessages.paragraphByNumberWrong(numStr));
			} catch (NumberFormatException e) {
				Window.alert(appMessages.paragraphByNumberWrong(numStr));
			}
		}
	};
	
	public class MainMenu extends MenuBar {
		
		public MainMenu() {
			MenuItem item = new MenuItem("<img src=\""+Images.ABOUT+"\"/>",true,new Command() {
				public void execute() {
					if (aboutPanel==null) { 
						aboutPanel = new AboutPanel();
					}
					aboutPanel.centerAndShow(appConstants.locale());
				}
			});
			
			item.setTitle(appConstants.menuAbout());
			addItem(item);
			
			MenuBar validate = new MenuBar(true);
			validate.addItem(appConstants.menuValidateMap(),validateMap);
			validate.addItem(appConstants.menuValidateText(),validateText);
			validate.addItem(appConstants.menuValidateAll(),validateAll);
			validate.addItem(appConstants.menuWholeRead(),wholeRead);
			validate.addItem(appConstants.menuExportForExternalCorrection(),new Command() {
				public void execute() {
					editorPanel.externalCorrection();
				}
			});
			validate.addItem(appConstants.menuAllSuccessStories(),successRead);
			validate.addItem(appConstants.menuLongAndShortSuccess(),longAndShort);
			
			validate.addItem(appConstants.menuValidateConnectionNames(),new Command() {
				public void execute() {
					editorPanel.validateConnectionNames();
				}
			});
			
			MenuBar importGame = new MenuBar(true);
			importGame.addItem(appConstants.menuImportFromText(),new Command() {
				public void execute() {
					//try to import text book
					new ImportTextPanel() {
						public boolean applyImportedModel(Model mod) {
							model.apply(mod);
							return true;
						}
					}.centerAndShow();
				}
			});
			importGame.addItem(appConstants.menuJoinOtherModel(),new Command() {
				public void execute() {
					//join two projects
					if (model.getCurrentParagraph() == null) {
						Window.alert(appConstants.joinHaveToSelectParagraph());
					} else if (Window.confirm(appMessages.confirmJoinOtherProject(model.getCurrentParagraph().getName()))){
						new ExchangePanel() {
							//action for load
							public boolean processLoad(String text) {
								Model mod = new Model(AppLocale.getAppConstants(),AppLocale.getAppMessages());
								if (mod.fromJSON(text)) {
									//success
									((ModelPersist)model).addModel(model.getCurrentParagraph(),mod);
									return true;
								} else {
									return false;
								}
							}
						}.showLoad(appConstants.joinModelTitle());
					}
					
				}
			});
			
			MenuBar wizards = new MenuBar(true);
			wizards.addItem(appConstants.menuWizardsClassicBattle(),new Command() {
				public void execute() {
					boolean exists = false;
					ArrayList<AbstractParameter> parameters = model.getParameters();
					for (AbstractParameter abstractParameter : parameters) {
						if (abstractParameter instanceof Modificator) {
							continue;
						}
						exists = true;
						break;
					}
					if (exists) {
						if (Window.confirm(appConstants.wizardClassicBattleExists())==false) {
							return;
						}
					} else {
						if (Window.confirm(appConstants.wizardClassicBattleNew())==false) {
							return;
						}
					}
					createClassicBattleSystem();
				}
			});
			
			wizards.addItem(appConstants.menuWizardsFindAndReplace(),new Command() {
				public void execute() {
					editorPanel.findAndReplace();
				}
			});
			
			wizards.addItem(appConstants.menuImportArrange(),new Command() {
				public void execute() {
					if (Window.confirm(appConstants.menuImportArrangeConfirm())) {
						ModelOptimizer.arrange(model);
						model.refreshParagraphs();
					}
				}
			});
			
			wizards.addItem(appConstants.menuCreateConnectionNames(),new Command() {
				public void execute() {
					ModelPersist modelPersist = (ModelPersist)model;
					modelPersist.generateConnectionNames();
				}
			});
			
			wizards.addItem(appConstants.menuRefreshParagraphsText(),new Command() {
				public void execute() {
					new RegenerateTextDialog().centerAndShow();
				}
			});
			
			wizards.addItem(appConstants.menuConnectionByNumbers(),connectionByNumbers);
			wizards.addItem(appConstants.menuParagraphNumber(),paragraphByNumber);
			wizards.addItem(appConstants.menuDetectAllCommercials(),new Command() {
				public void execute() {
					editorPanel.findAllCommercials();
				}
			});
			if (GWT.isScript()==false) {
				wizards.addItem("Удалить дублирующиеся переходы",removeDuplicateConnections);
			}
			
			MenuBar server = new MenuBar(true);
			server.addItem(appConstants.menuServerLogin(),new Command() {
				public void execute() {
					editorPanel.serverLogin();
				}
			});
			server.addItem(appConstants.menuServerSaveBook(),new Command() {
				public void execute() {
					editorPanel.saveBookToServer();
				}
			});
			server.addItem(appConstants.menuServerPublishBook(),new Command() {
				public void execute() {
					if (Window.confirm(appConstants.confirmRenumeration())) {
						editorPanel.publishBook(false);
					}
				}
			});
			server.addItem(appConstants.menuServerRePublish(),new Command() {
				public void execute() {
					editorPanel.publishBook(true);
				}
			});
			
			MenuBar local = new MenuBar(true);
			local.addItem(appConstants.menuSave(),new Command() {
				public void execute() {
					String modelJSON = ((ModelPersist)model).toJSON(Model.EXPORT_ALL,JSONBuilder.getStartInstance());
					MaskPanel.hide();
					fileExchange.saveFile(modelJSON, appConstants.menuSaveProjectFile());
				}
			});
			local.addItem(appConstants.menuLoad(),new Command() {
				public void execute() {
					final String text = fileExchange.loadFile(appConstants.menuLoadProjectFile());
					if (text != null) {
						MaskPanel.show();
						DeferredCommand.addCommand(new Command(){
							public void execute() {
								Model model = new Model(AppLocale.getAppConstants(),AppLocale.getAppMessages());
								if (model.fromJSON(text)) {
									//success
									editorPanel.activateParagraphMap();
									quickPanel.activate(QuickPanel.SETTINGS);
									MasterPanel.this.model.apply(model);
								}
								MaskPanel.hide();
							}
						});
					}
				}
			});
			local.addItem(appConstants.menuClipboardSave(),new Command() {
				public void execute() {
					MaskPanel.show();
					DeferredCommand.addCommand(new Command() {
						public void execute() {
							String modelJSON = ((ModelPersist)model).toJSON(Model.EXPORT_ALL,JSONBuilder.getStartInstance());
							MaskPanel.hide();
							exchangePanel.showSave(modelJSON,appConstants.copyAndSaveModelTitle());
						}
					});
					
				}
			});
			local.addItem(appConstants.menuClipboardLoad(),new Command() {
				public void execute() {
					exchangePanel.showLoad(appConstants.loadSavedModelTitle());
				}
			});
			
			local.addItem(appConstants.menuExportForPlayer(),new Command() {
				public void execute() {
					MaskPanel.show();
					DeferredCommand.addCommand(new Command() {
						public void execute() {
							String modelJSON = "iambookmaster="+((ModelPersist)model).toJSON(Model.EXPORT_PLAY,JSONBuilder.getStartInstance())+";";
							MaskPanel.hide();
							if (fileExchange.checkApplet()) {
								fileExchange.saveFile(modelJSON, appConstants.copyAndSavePlayerTitle());
							} else {
								exchangePanel.showExport(modelJSON,appConstants.copyAndSavePlayerTitle());
							}
						}
					});
				}
			});
			local.addItem(appConstants.menuReExportText(),new Command() {
				public void execute() {
					editorPanel.exportBook(true,new ExportBookCallback() {
						public void onError() {
							//everything in GUI
						}
						public void onSuccess(String text) {
							if (fileExchange.checkApplet()) {
								fileExchange.saveFile(text, appConstants.exportTextTitle());
							} else {
								exchangePanel.showExport(text,appConstants.exportTextTitle());
							}
						}
					});
				}
			});
			local.addItem(appConstants.menuSaveLight(),new Command() {
				public void execute() {
					ModelPersist light = ((ModelPersist)model).getLightMode();
					//Settings is shared !!!!
					boolean demo = light.getSettings().isDemoVersion();
					light.getSettings().setDemoVersion(true);
					String modelJSON = light.toJSON(Model.EXPORT_ALL,JSONBuilder.getStartInstance());
					//Settings is shared, restore value
					light.getSettings().setDemoVersion(demo);
					MaskPanel.hide();
					fileExchange.saveFile(modelJSON, appConstants.menuSaveProjectFileLight());
				}
			});
			
			if (GWT.isScript()==false) {
				final String title = "Экспорт в XML";
				local.addItem(title,new Command() {
					public void execute() {
						String modelJSON = ((ModelPersist)model).toJSON(Model.EXPORT_ALL,XMLBuilder.getStartInstance());
						MaskPanel.hide();
						fileExchange.saveFile(modelJSON, title);
					}
				});
			}
			
			MenuBar manu = new MenuBar(true);
			MenuBar playMenu = new MenuBar(true);
			playMenu.addItem(appConstants.menuPlayerNormal(),new Command() {
				public void execute() {
					editorPanel.play(EditorPanel.PLAYER_WEB);
				}
			});
			playMenu.addItem(appConstants.menuPlayerIPhone(),new Command() {
				public void execute() {
					editorPanel.play(EditorPanel.PLAYER_IPHONE);
				}
			});
			playMenu.addItem(appConstants.menuPlayerIPad(),new Command() {
				public void execute() {
					editorPanel.play(EditorPanel.PLAYER_IPAD);
				}
			});
			playMenu.addItem(appConstants.menuPlayer800X600(),new Command() {
				public void execute() {
					editorPanel.play(EditorPanel.PLAYER_800X600);
				}
			});
			playMenu.addItem(appConstants.menuPlayer1024X600(),new Command() {
				public void execute() {
					editorPanel.play(EditorPanel.PLAYER_1024X600);
				}
			});
			playMenu.addItem(appConstants.menuURQ(),new Command() {
				public void execute() {
					editorPanel.play(EditorPanel.PLAYER_URQ);
				}
			});
			
			manu.addItem(appConstants.menuPlayer(),playMenu);
			
			manu.addItem(appConstants.menuServer(),server);
			manu.addItem(appConstants.menuLocal(),local);
			manu.addSeparator();
			manu.addItem(appConstants.menuImport(),importGame);
			manu.addItem(appConstants.menuWizards(),wizards);
			manu.addSeparator();
			manu.addItem(appConstants.menuValidation(),validate);
			manu.addItem(appConstants.menuRules(),new Command(){
				public void execute() {
					editorPanel.editRules();
				}
			});
			manu.addItem(appConstants.commercialWelcomeText(),new Command(){
				public void execute() {
					editorPanel.editCommercialWelcome();
				}
			});
			
			addItem(appConstants.maneProject(), manu);
			
			//add new
			manu = new MenuBar(true);
			manu.addItem(appConstants.menuNewParagraph(),addNewLocations);
			manu.addItem(appConstants.menuNewObject(),addNewObject);
			manu.addItem(appConstants.menuNewSound(),addNewSound);
			manu.addItem(appConstants.menuNewImage(),addNewImage);
			manu.addSeparator();
			manu.addItem(appConstants.menuNewParameter(),new Command() {
				public void execute() {
					model.addNewParameter(null);
				}
			});
			manu.addItem(appConstants.menuNewNPC(),new Command() {
				public void execute() {
					model.addNewNPC(null);
				}
			});
			manu.addItem(appConstants.menuNewBattle(),new Command() {
				public void execute() {
					model.addNewBattle(null);
				}
			});
			manu.addItem(appConstants.menuNewModificator(),new Command() {
				public void execute() {
					model.addNewModificator(null);
				}
			});
			manu.addItem(appConstants.menuNewAlchemy(),new Command() {
				public void execute() {
					model.addNewAlchemy(null);
				}
			});
			
			addItem(appConstants.menuAddNew(), manu);
		}

	}

	protected void createClassicBattleSystem() {
		Parameter live = model.addNewParameter(null);
		live.setName(appConstants.wizardClassicBattleLive());
		live.setDescription(appConstants.wizardClassicBattleLiveDescription());
		live.setVital(true);
		live.setHeroInitialValue(new DiceValue(2,6,10));
		
		Parameter maxPower = model.addNewParameter(null);
		maxPower.setName(appConstants.wizardClassicBattleMaxPower());
		maxPower.setDescription(appConstants.wizardClassicMaxBattlePowerDescription());
		maxPower.setHeroInitialValue(new DiceValue(0,6,7));
		
		Parameter power = model.addNewParameter(null);
		power.setName(appConstants.wizardClassicBattlePower());
		power.setDescription(appConstants.wizardClassicBattlePowerDescription());
		power.setLimit(maxPower);
		maxPower.setHeroInitialValue(new DiceValue(maxPower.getHeroInitialValue()));
		
		Parameter maxLuck = model.addNewParameter(null);
		maxLuck.setName(appConstants.wizardClassicBattleMaxLuck());
		maxLuck.setDescription(appConstants.wizardClassicBattleMaxLuckDescription());
		maxLuck.setHeroOnly(true);
		maxLuck.setHeroInitialValue(new DiceValue(6,0,6));
		
		Parameter luck = model.addNewParameter(null);
		luck.setName(appConstants.wizardClassicBattleLuck());
		luck.setDescription(appConstants.wizardClassicBattleLuckDescription());
		luck.setHeroOnly(true);
		luck.setLimit(maxLuck);
		luck.setHeroInitialValue(new DiceValue(1,2,3));
		
		Parameter food = model.addNewParameter(null);
		food.setName(appConstants.wizardClassicBattleFood());
		food.setDescription(appConstants.wizardClassicBattleFoodDescription());
		food.setHeroOnly(true);
		food.setHeroInitialValue(new DiceValue(0,6,3));
		
		Battle battle = model.addNewBattle(null);
		battle.setName(appConstants.wizardClassicBattleBattle());
		battle.setDescription(appConstants.wizardClassicBattleBattleDescription());
		battle.setVital(live);
		DiceValue _2d6 = new DiceValue();
		_2d6.setN(2);
		battle.getAttack().setConstant(_2d6);
		battle.getAttack().getParameters().put(power, 1);
		DiceValue minus2 = new DiceValue();
		minus2.setConstant(2);
		minus2.setN(0);
		battle.getDamage().setConstant(minus2);
		
		Alchemy alchemy = model.addNewAlchemy(null);
		alchemy.setName(appConstants.wizardClassicBattleFoodConvetion());
		alchemy.setDescription(appConstants.wizardClassicBattleFoodConvetionDescription());
		alchemy.setFrom(food);
		alchemy.setTo(live);
		alchemy.setOnDemand(false);
		alchemy.setPlace(Alchemy.PLACE_PEACE);
		alchemy.setFromValue(1);
		alchemy.setToValue(new DiceValue(6,0,3));
		model.refreshParameters();
	}
	
	public void onResize() {
		quickPanel.onResize();
		editorPanel.onResize();
	}
}
