package com.iambookmaster.client.iphone.urq;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.code.gwt.database.client.service.DataServiceException;
import com.google.code.gwt.database.client.service.ListCallback;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.code.gwt.database.client.service.VoidCallback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.iambookmaster.client.common.Base64Coder;
import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.iphone.IPhoneCanvas;
import com.iambookmaster.client.iphone.IPhoneViewListenerAdapter;
import com.iambookmaster.client.iphone.common.IPhoneButton;
import com.iambookmaster.client.iphone.common.IPhoneFlatButton;
import com.iambookmaster.client.iphone.data.IPhoneDataService;
import com.iambookmaster.client.iphone.data.IPhoneFileBean;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;
import com.iambookmaster.client.iurq.Core;
import com.iambookmaster.client.iurq.URQParser;
import com.iambookmaster.client.iurq.URQUI;
import com.iambookmaster.client.iurq.logic.Btn;
import com.iambookmaster.client.iurq.logic.InvVar;
import com.iambookmaster.client.iurq.logic.InvVar.Action;
import com.iambookmaster.client.iurq.logic.Pause;
import com.iambookmaster.client.iurq.logic.Play;
import com.iambookmaster.client.iurq.logic.URQImage;

public class IPhoneURQPlayer extends IPhoneViewListenerAdapter{

	static final IPhoneStyles css = IPhoneImages.INSTANCE.css();
	
	private static final String FIELD_GAME_STATE = "a";
	private static final String FIELD_TEXT = "b";
	private static final String FIELD_GAME_TEXT = "c";
	private static final String FIELD_LOCATION = "d";
	private static final String FIELD_GAME_BUTTONS = "e";
	private static final String FIELD_GAME_NAME = "f";
	private static final String FIELD_GAME_ANYKEY = "i";
	private Core core;
	private IPhoneCanvas canvas;
	
	private ArrayList<Btn> buttons = new ArrayList<Btn>(); 
	private ArrayList<String> texts = new ArrayList<String>();
	private IPhoneURQPlayerListenr listener;
	private ClickHandler gameOverHander; 
	private IPhoneURQInventory inventory;
	private boolean redrawn;
	private IPhoneURQInventoryListener inventoryListenr;
	private IPhoneURQActions inventotyActions;
	private IPhoneDataService dataService;
	private String fileName;
	private int nextFileCounter;
	private boolean anykeyState;
	private Timer pauseTimer;
	private int timerCounter;
	protected boolean pauseSuspended;
	private boolean stateSaved;


	public IPhoneURQPlayer(IPhoneDataService dataService,IPhoneURQPlayerListenr lst) {
		this.listener = lst;
		this.dataService = dataService;
		gameOverHander = new ClickHandler() {
			public void onClick(ClickEvent event) {
				cancelPause();
				listener.exit();
			}
		};
		inventoryListenr = new IPhoneURQInventoryListener() {
			public void useInventory(String name, InvVar var) {
				use(name);
			}

			public void useInventory() {
				use(core.getInventoryName());
			}

			private void use(String	itemName) {
				nextAction();
				Vector<Action> actions = core.listOfInventActions(itemName);
				redrawn = false;
				if (actions == null || actions.size()==0) {
					//nothing
				} else if (actions.size()==1) {
					//single action - use now
					core.doAction(actions.get(0));
				} else {
					//show list of actions
					inventotyActions.show(canvas, core, itemName, actions);
					return;
				}
				processCommand();
				if (redrawn==false) {
					_redraw(canvas,1);
				}	
			}

			public void forward() {
			}

			public void back() {
				_redraw(canvas,-1);
			}

			public void doAction(Action action) {
				nextAction();
				redrawn = false;
				core.doAction(action);
				core.tick();
				if (redrawn==false) {
					_redraw(canvas,1);
				}	
			}
		};
		
		inventory = new IPhoneURQInventory(inventoryListenr);
		inventotyActions = new IPhoneURQActions(inventoryListenr);
		init();
	}

	public void init() {
		core = new Core("Инвентарь");
		core.init(new URQUI() {
			
			private StringBuilder builder=new StringBuilder();
			private boolean ignoreEmptyLine=true;
			public void resizeItems() {
			}
			
			public void print(String s, int color) {
				int i = s.indexOf('\n');
				if (i<0) {
					builder.append(s);
					ignoreEmptyLine = false;
				} else if (i==0) {
					//at the beginning
					if (ignoreEmptyLine) {
						if (s.length()==1) {
							//empty line
							return;
						} else {
							s = s.substring(1);
						}
					}
					texts.add(builder.toString());
					builder.setLength(0);
					if (s.length()==1) {
						ignoreEmptyLine = true;
						return;
					} else {
						builder.append(s.substring(1));
					}
				} else if (i==s.length()-1) {
					//at the end
					builder.append(s.substring(0,i));
					texts.add(builder.toString());
					builder.setLength(0);
				} else {
					//in the middle
					builder.append(s.substring(0,i));
					texts.add(builder.toString());
					builder.setLength(0);
					print(s.substring(i+1),color);
				}
				ignoreEmptyLine = false;
			}
			
			public void invRefresh() {
			}
			
			public String getInput() {
				return null;
			}
			
			public void enableInput() {
			}
			
			public void doExit() {
				listener.exit();
			}
			
			public void disableInput() {
			}
			
			public void clear() {
				anykeyState = false;
				builder.setLength(0);
				ignoreEmptyLine = true;
				buttons.clear();
				texts.clear();
			}
			
			public void addButton(Btn btn) {
				buttons.add(btn);
			}

			public void end() {
				flushBuffer();
				if (canvas != null) {
					_redraw(canvas,1);
					if (stateSaved==false) {
						//no "save" command was performed
						dataService.storeState(storeGameState(null));
					}
				}
			}

			private void flushBuffer() {
				if (builder.length()>0) {
					texts.add(builder.toString());
					builder.setLength(0);
				}
				ignoreEmptyLine = true;
			}

			public String loadFile(String s) {
				return null;
			}

			public void play(Play operator) {
			}

			public void anykey() {
				anykeyState=true;
				end();
				
			}

			public void save(String location) {
				flushBuffer();
				saveState(location);
			}

			public void showImage(URQImage image) {
				// TODO Auto-generated method stub
				
			}

			public void pause(Pause pause) {
				startPause((int)pause.getTime()/100);
				end();
			}
			
		});
	}

	private void startPause(int time) {
		if (time > 0 && pauseTimer==null) {
			pauseTimer = new Timer() {
				@Override
				public void run() {
					if (pauseSuspended) {
						return;
					}
					if (timerCounter>0) {
						timerCounter--;
						if (timerCounter==0) {
							//last tick
							cancelPause();
							stateSaved=false;
							core.tick();
						}
					} else {
						//release timer
						cancelPause();
					}
				}
			};
			pauseTimer.scheduleRepeating(100);
		}
		timerCounter = time;
		
	}
	
	private void cancelPause() {
		timerCounter = 0;
		if (pauseTimer!=null) {
			pauseTimer.cancel();
			pauseTimer = null;
		}
		pauseSuspended = false;
	}

	

	public String getLastQuestName(String json) {
		try {
			return JSONParser.getInstance().propertyString(JSONParser.eval(Base64Coder.decodeString(json)), FIELD_GAME_NAME);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void restoreState(String json, boolean fromSave) {
		JavaScriptObject obj = JSONParser.eval(Base64Coder.decodeString(json));
		JSONParser parser = JSONParser.getInstance();
		String state = parser.propertyString(obj, FIELD_GAME_STATE);
		core.load(new URQParser(), state);
		stateSaved=true;
    	core.tick();
    	if (fromSave==false) {
	    	texts.clear();
	    	buttons.clear();
			Object data = parser.property(obj, FIELD_GAME_TEXT);
			if (data != null) {
				//restore content of the screen
				int len = parser.length(data);
				for (int i = 0; i < len; i++) {
					Object row = parser.getRow(data, i);
					String text = parser.propertyString(row, FIELD_TEXT);
					texts.add(text);
				}
			}
			data = parser.property(obj, FIELD_GAME_BUTTONS);
			if (data != null) {
				//restore active buttons
				int len = parser.length(data);
				StringBuilder builder = new StringBuilder("btn ");
				for (int i = 0; i < len; i++) {
					Object row = parser.getRow(data, i);
					String text = parser.propertyString(row, FIELD_TEXT);
					String location = parser.propertyString(row, FIELD_LOCATION);
					builder.setLength(4);
					builder.append(location);
					builder.append(',');
					builder.append(text);
					Btn btn = new Btn(core, builder.toString());
					buttons.add(btn);
				}
			}
    	}
		
	}


	private String storeGameState(String location) {
		JSONBuilder builder = JSONBuilder.getStartInstance();
		builder.newRow();
		builder.field(FIELD_GAME_STATE, core.save(location));
		builder.field(FIELD_GAME_NAME, fileName);
		builder.field(FIELD_GAME_ANYKEY, anykeyState);
		if (texts.isEmpty()==false) {
			JSONBuilder jsonBuilder = builder.getInstance();
			for (String text : texts) {
				jsonBuilder.newRow();
				jsonBuilder.field(FIELD_TEXT, text);
			}
			builder.childArray(FIELD_GAME_TEXT,jsonBuilder);
		}
		if (buttons.isEmpty()==false) {
			JSONBuilder jsonBuilder = builder.getInstance();
			for (Btn btn : buttons) {
				jsonBuilder.newRow();
				jsonBuilder.field(FIELD_TEXT, btn.getName());
				jsonBuilder.field(FIELD_LOCATION, btn.getLocation());
			}
			builder.childArray(FIELD_GAME_BUTTONS,jsonBuilder);
		}
		return Base64Coder.encodeString(builder.toString());
	}

	private void saveState(String location) {
		stateSaved=true;
		String nextFileName;
		if (nextFileCounter>0) {
			nextFileName = "_"+nextFileCounter+".sav";
		} else {
			nextFileName = ".sav";
		}
		int i = fileName.lastIndexOf('.');
		if (i>0) {
			nextFileName = fileName.substring(0,i)+nextFileName;
		} else {
			nextFileName = fileName+nextFileName;
		}
		i = fileName.lastIndexOf('/');
		if (i<0) {
			i = fileName.lastIndexOf('\\');
		}
		final String name = i>0 ? nextFileName.substring(i+1) : nextFileName;
		dataService.storeState(storeGameState(location),nextFileName,new VoidCallback() {
			public void onFailure(DataServiceException error) {
				Window.alert("Ошибка сохранения "+error.getMessage());
			}

			public void onSuccess() {
				nextFileCounter++;
				Window.alert("Игра сохранена в "+name);
			}
		});
	}

	public void play(String quest, IPhoneCanvas cvs,final boolean runNow, final String fileName, final Command executeWhenReady) {
//		Window.alert("Len="+quest.length());
		cancelPause();
		nextFileCounter = 0;
		this.canvas = cvs;
		this.fileName = fileName;
		init();
		stateSaved = true;
		new URQParser().startParse(quest, core);
		if (executeWhenReady != null) {
			executeWhenReady.execute();
		} else if (cvs != null) {
			runLoadedGame();
		}
	}

	private void runLoadedGame() {
		dataService.selectAvailableFiles("sav", new ListCallback<IPhoneFileBean>() {
			public void onFailure(DataServiceException error) {
				//no saved games
				processCommand();
			}
			
			public void onSuccess(List<IPhoneFileBean> result) {
				int i = fileName.lastIndexOf('.');
				if (i<=0 || result.size()==0) {
					//no save files
					processCommand();
					return;
				}
				ArrayList<IPhoneFileBean> saves = new ArrayList<IPhoneFileBean>();
				String prefix = fileName.substring(0,i);
				for (IPhoneFileBean fileBean : result) {
					if (fileBean.getPath().startsWith(prefix)) {
						//our save
						saves.add(fileBean);
					}
				}
				if (saves.size()>0) {
					String last = saves.get(saves.size()-1).getPath();
					last = last.substring(prefix.length());
					i = last.lastIndexOf('.');
					if (i>0) {
						last = last.substring(0,i);
					}
					if (last.length()>0 && last.charAt(0)=='_') {
						try {
							nextFileCounter=Integer.parseInt(last.substring(1))+1;
						} catch (NumberFormatException e) {
							nextFileCounter=1;
						}
					} else {
						nextFileCounter=1;
					}
					//select saved game
					IPhoneURQSelectFile selectFile = new IPhoneURQSelectFile("Выбрать сохраненную игру","Новая игра") {
						public void forward() {
							//start from new game
							processCommand();
						}
						
						public void back() {
							listener.exit();
						}
						
						@Override
						protected void selectFile(IPhoneFileBean file) {
							//restore saved game
							dataService.loadSingleFile(file.getPath(), new ScalarCallback<String>() {
								public void onFailure(DataServiceException error) {
									//failed - start from new game
									processCommand();
								}
								public void onSuccess(String result) {
									try {
										restoreState(result,true);
										//IPhoneURQPlayer.this.show(canvas);
									} catch (Exception e) {
										e.printStackTrace();
										core.reset();
										processCommand();
									}
								}
							});
						}

						@Override
						protected void newFile() {
							//start from new game
							processCommand();
						}
						
					};
					selectFile.show(canvas, saves);
				} else {
					//no saved games
					processCommand();
				}
			}
		});
	}

	private void processCommand() {
		//perform initial steps
		stateSaved=false;
		core.tick();
		//redraw screen
//		show(canvas);
		
	}
	
	public void show(IPhoneCanvas canvas) {
		this.canvas = canvas;
		_redraw(canvas,1);
	}

	public void redraw(IPhoneCanvas canvas) {
		_redraw(canvas, 0);
	}
	
	public void _redraw(IPhoneCanvas canvas,int animation) {
		if (pauseSuspended) {
			pauseSuspended = false;
		}
		canvas.setListener(IPhoneURQPlayer.this);
		switch (animation) {
		case -1:
			canvas.clearWithAnimation(true);
			break;
		case 1:
			canvas.clearWithAnimation(false);
			break;
		default:
			canvas.clear();
			break;
		}
		for (String text : texts) {
			if (text.length()>0) {
				canvas.add(new Label(text));
			} else {
				canvas.add(new HTML("&nbsp;"));
			}
		}
		if (anykeyState) {
			//wait for any click
		} else if (buttons.size()==0 && pauseTimer==null) {
			//no buttons, no pause - end of the game
			IPhoneButton button = new IPhoneButton("Конец");
			canvas.add(button);
			canvas.addClickHandler(button, gameOverHander);
		} else {
			//some actions available
			for (Btn btn : buttons) {
				ClickHandler handler = new ClickHandlerBtn(btn);
				IPhoneFlatButton button = new IPhoneFlatButton(btn.getName());
				canvas.add(button);
				canvas.addClickHandler(button, handler);
			}
		}
		canvas.done();
	}

	@Override
	public void click(int x, int y) {
		if (anykeyState) {
			//user clicked to screen
			nextAction();
			core.tick();
		}
	}

	private void nextAction() {
		stateSaved=false;
		anykeyState = false;
		cancelPause();
	}

	public void back() {
		pauseSuspended = true;
		listener.rootMenu();
	}

	public void forward() {
		//switch to Inventory
		inventory.show(canvas,core);
	}

	public void drawn() {
		redrawn=true;
	}
	
	public class ClickHandlerBtn implements ClickHandler {

		private Btn btn;

		public ClickHandlerBtn(Btn btn) {
			this.btn = btn;
		}

		public void onClick(ClickEvent event) {
			nextAction();
			buttons.clear();
			core.doButton(btn);
			core.tick();
		}
		
	}

}
