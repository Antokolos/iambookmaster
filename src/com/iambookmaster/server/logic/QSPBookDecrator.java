package com.iambookmaster.server.logic;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.DiceValue;
import com.iambookmaster.client.beans.Greeting;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.NPC;
import com.iambookmaster.client.beans.NPCParams;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.beans.ParametersCalculation;
import com.iambookmaster.client.beans.Sprite;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ParagraphParsingHandler;
import com.iambookmaster.client.paragraph.BookDecorator;

public class QSPBookDecrator implements BookDecorator {
	
	private static final String VAR_MODIFICATOR_EXISTS = "modificatorExist";
	private static final String VAR_CAN_GO = "canGo";
	private static final String VAR_ALCHEMY_DISABLED_PREFIX = "alchemyDisabled";
	private static final String VAR_ENEMY_PREFIX = "enemy";
	private static final String VAR_BATTLE_MODE = "battleMode";
	private static final String VAR_MUST_GO = "mustGo";
//	private static final String VAR_MODEL = "model";
	private static final String VAR_PARAMETER_PREFIX = "parameter";
	private static final String VAR_CURRENT_ENEMY_TYPE = "enemyType";
	private static final String VAR_CURRENT_PARAGRAPH = "currentLocation";
	private static final String VAR_ENEMY_VITAL = "enemyVital";
	private static final String VAR_HERO_ATTACK_SELECTION = "heroAttackSelection";
	private static final String VAR_ATTACK_VALUE = "attackValue";
	private static final String VAR_DEFENSE_VALUE = "defenseValue";
	private static final String VAR_DAMAGE_VALUE = "damageValue";
	private static final String VAR_TEMP = "temp";
	private static final String VAR_CALCULATION = "calc";
	private static final String VAR_DICE = "d";
	private static final String VAR_DICE_MAX = "dMax";
	private static final String VAR_SELECTOR = "targetSelector";
	private static final String VAR_SELECTED_ENEMY = "selectedEnemy";
	private static final String VAR_TEMP_PARAMETER_PREFIX = "tempParameter";
	private static final String VAR_ALCHEMY_USED_IN_BATTLE_ROUND = "alchemyUsed";
	private static final String VAR_FATAL = "fatal";
	private static final String VAR_DICE_TEMP = "diceTemp";
	private static final String VAR_SOUND = "sound";
	private static final String VAR_MUSIC = "music";
	
	private static final char PARAGRAPH_PREFIX = 'l';
	
	private static final String VALUE_MODIFICATOR_SET = "1";
	private static final String VALUE_MODIFICATOR_NOT_SET = "0";
	
	private static final String PROC_USE_OBJECT = "useObject";
	private static final String PROC_ADD_BATTLE_ALCHEMY = "battleAlchemy";
	private static final String PROC_CHECK_ENEMY_ALIVE = "checkEnemyAlive";
	private static final String PROC_ENABLE_ALCEMY = "enableAlchemy";
	private static final String PROC_SHOW_ENEMY_PARAMS = "showEnemyParams";
	private static final String PROC_ADD_PEACE_ALCHEMY = "peaceAlchemy";
	private static final String PROC_VITAL = "vitalHeroCheck";
	private static final String PROC_SHOW_ENEMY_NAME = "showEnemyName";
	private static final String PROC_SHOW_PARAMS = "showHeroParams";
	private static final String PROC_USE_ALCEMY = "useAlchemy";
	private static final String PROC_ATTACK = "attack";
	private static final String PROC_SELECT_TARGET = "target";
	private static final String PROC_SELECT_ENEMY = "selectEnemy";
	private static final String PROC_SELECT_TARGET_AUTO = "autoTarget";
	private static final String PROC_SHOW_ENEMY_SELECTION = "markSelectedTarget";
	private static final String PROC_ABOUT = "about";
	private static final String PROC_GREETINGS = "greetings";
	private static final String PROC_GO_CURRENT = "goCurrent";
	private static final String PROC_BATTLE = "battle";
	private static final String PROC_CHECK_ENEMY_ALIVE_AND_SHOW_SELECTION = "showAliveEnemyForSelect";
	private static final String PROC_CHECK_ENEMY_ALIVE_AND_SELECT = "selectAliveEnemy";
	private static final String PROC_CHECK_ENEMY_ALIVE_AND_KILL = "checkEnemyAndKill";
	private static final String VAR_OBJECTS = "OBJECTS";
	private static final String BEFORE_PARAM = "<<";
	private static final String AFTER_PARAM = ">>";
	private static final String PROC_TIMER = "timer";

	public static final String ENCODING = "Cp1251";
	
	private StringBuilder buffer;
	private final AppConstants appConstants;
	private final AppMessages appMessages;
	private HashMap<Paragraph, ArrayList<ParagraphAction>> paragraphActions=new HashMap<Paragraph, ArrayList<ParagraphAction>>();
	private HashMap<ObjectBean, String> objects=new HashMap<ObjectBean, String>();
	private Model model;
	private HashSet<Parameter> limits;
	private ArrayList<Parameter> vitals=new ArrayList<Parameter>();
	private boolean alchemyPresent;
	private boolean alchemyPeacePresent;
	private boolean alchemyBattlePresent;
	private HashMap<String, String> diceProces=new HashMap<String, String>();
	private StringBuilder diceBuffer;
	private HashSet<Parameter> battleAlchemyLimits;
	private HashSet<String> battleSet = new HashSet<String>();
	
	private boolean obfuscate=true;
	private int obsCounter=0;
	private HashMap<String,String> obs=new HashMap<String, String>();
	private boolean hasParameters;
	
	private String getID(Object...ids) {
		String id;
		if (ids.length==1) {
			id = String.valueOf(ids[0]);
		} else {
			StringBuffer buffer = new StringBuffer();
			for (Object object : ids) {
				buffer.append(String.valueOf(object));
			}
			id = buffer.toString();
		}
		if (obfuscate) {
			if (obs.containsKey(id)) {
				return obs.get(id);
			} else {
				StringBuffer buffer = new StringBuffer();
				int i=obsCounter++;
				//first - character
				buffer.append((char)(65+i%22));
				i = i /22;
				//second - number
				buffer.append((char)(48+i%10));
				i = i /10;
				//next - characters
				while (i>0) {
					buffer.append((char)(65+i%22));
					i = i /22;
				}
				String res = buffer.toString();
				obs.put(id,res);
				return res;
			}
		} else {
			return id;
		}
	}
	public class ParagraphAction {

		private int id;
		private ParagraphConnection connection;
		private Paragraph to;
		private Paragraph from;


		public ParagraphAction(Paragraph from, Paragraph to,ParagraphConnection connection,int id) {
			super();
			this.id = id;
			this.connection = connection;
			this.to = to;
			this.from = from;
		}

		public int getId() {
			return id;
		}

		public ParagraphConnection getConnection() {
			return connection;
		}

		public Paragraph getTo() {
			return to;
		}

		public Paragraph getFrom() {
			return from;
		}

		public String getObjectVar() {
			return objects.get(connection.getObject());
		}

		public String getModificatorVar() {
			return getID(VAR_PARAMETER_PREFIX,connection.getModificator().getId());
		}

		public String getParameterVar() {
			return getID(VAR_PARAMETER_PREFIX,connection.getParameter().getId());
		}

		public String getActionName() {
			if (model.getSettings().isShowConnectionNames()) {
				StringBuilder builder = new StringBuilder(appMessages.urqButtonAction(id)).append(' ');
				if (connection.getFrom()==from) {
					//from
					builder.append(connection.getNameFrom());
				} else {
					builder.append(connection.getNameTo());
				}
				return builder.toString();
			} else {
				return appMessages.urqButtonAction(id);
			}
		}
	}
	
	public void println(String text) {
		buffer.append("*PL '");
		int pos = buffer.length();
		buffer.append(text);
		while (pos<buffer.length()) {
			switch (buffer.charAt(pos)) {
			case '\r':
				if (buffer.charAt(pos+1)=='\n') {
					buffer.replace(pos, pos+2, "'\n*PL '");
				} else {
					buffer.replace(pos, pos+1, "'\n*PL '");
				}
			 	pos = pos + 7;
				break;
			case '\n':
				buffer.replace(pos, pos+1, "'\n*PL '");
			 	pos = pos + 6;
				break;
			case '\'':
			 	buffer.insert(pos, "''");
			 	pos = pos + 2;
				break;
			case '<':
			 	buffer.replace(pos, pos+1,"&lt;");
			 	pos = pos + 3;
				break;
			case '>':
			 	buffer.replace(pos, pos+1, "&gt;");
			 	pos = pos + 3;
				break;
			case '©':
			 	buffer.replace(pos, pos+1, "&copy;");
			 	pos = pos + 5;
				break;
			case '®':
			 	buffer.replace(pos, pos+1, "&reg;");
			 	pos = pos + 4;
				break;
			case '&':
			 	buffer.replace(pos, pos+1, "&amp;");
			 	pos = pos + 4;
				break;
			}
			pos++;
//			if (pos-len>60) {
//				buffer.insert(pos, "'\n*P '");
//			 	pos = pos + 6;
//			 	len = pos;
//			}
		}
		buffer.append("'\n");
		
	}

	public void printLine(StringBuilder builder,String text) {
		int pos = builder.length();
		builder.append(text);
		while (pos<builder.length()) {
			switch (builder.charAt(pos)) {
			case '\n':
				//cut last 
				builder.setLength(pos);
				break;
			case '\'':
			 	buffer.insert(pos, "''");
			 	pos = pos + 2;
				break;
			case '<':
			 	buffer.replace(pos, pos+1,"&lt;");
			 	pos = pos + 3;
				break;
			case '>':
			 	buffer.replace(pos, pos+1, "&gt;");
			 	pos = pos + 3;
				break;
			case '©':
			 	buffer.replace(pos, pos+1, "&copy;");
			 	pos = pos + 5;
				break;
			case '®':
			 	buffer.replace(pos, pos+1, "&reg;");
			 	pos = pos + 4;
				break;
			case '&':
			 	buffer.replace(pos, pos+1, "&amp;");
			 	pos = pos + 4;
				break;
			}
			pos++;
		}
	}
	
	public QSPBookDecrator(Model mod,AppConstants appConstants,AppMessages appMessages) {
		this.model = mod;
		this.appConstants = appConstants;
		this.appMessages = appMessages;
		diceBuffer = new StringBuilder();
		buffer = new StringBuilder();
		buffer.append("#start\nshowinput 0\n");
		buffer.append(getID(VAR_CURRENT_PARAGRAPH));
		buffer.append("=0\n");
		if (model.getSettings().getBookTitle() != null) {
			println(model.getSettings().getBookTitle());
		}
		if (model.getSettings().getBookAuthors() != null) {
			println(model.getSettings().getBookAuthors());
		}
		println(appConstants.bookCreatedByText());
		addButton(buffer,appConstants.urqAbout(),getID(PROC_ABOUT));
		if (model.getSettings().isShowAboutOnStart()==false) {
			addButton(model.getStartParagraph(),appConstants.decoratorStart());
		}
		
		buffer.append("-\n#");
		if (model.getSettings().isDisableAudio()==false && model.getSounds().size()>0) {
			buffer.append(getID(PROC_TIMER));
			buffer.append("\nif $");
			buffer.append(getID(VAR_MUSIC));
			buffer.append("='' or ISPLAY($");
			buffer.append(getID(VAR_MUSIC));
			buffer.append(") : EXIT\nPLAY($");
			buffer.append(getID(VAR_MUSIC));
			buffer.append(")\n-\n#");
		}
		
		buffer.append(getID(PROC_ABOUT));
		buffer.append('\n');
		if (model.getSettings().getBookDescription() != null) {
			println(model.getSettings().getBookDescription());
		}
		if (model.getPlayerRules().length()>0) {
			println(model.getPlayerRules());
		}
		if (model.getSettings().getGreetings() != null && model.getSettings().getGreetings().size()>0) {
			buffer.append("GS '");
			buffer.append(getID(PROC_GREETINGS));
			buffer.append("'\n");
		}
		addButton(model.getStartParagraph(),appConstants.decoratorStart());
		buffer.append("\n-\n");
		
		//support procedures
		HashMap<Object,ArrayList<ParagraphConnection>> objConnections;
		battleAlchemyLimits = new HashSet<Parameter>();
		if (model.getSettings().isHiddenUsingObjects()) {
			objConnections = new HashMap<Object, ArrayList<ParagraphConnection>>();
			for (ParagraphConnection connection : model.getParagraphConnections()) {
				if (connection.getType()==ParagraphConnection.TYPE_NORMAL && connection.getObject() != null) {
					ArrayList<ParagraphConnection> list = objConnections.get(connection.getObject());
					if (list==null) {
						list = new ArrayList<ParagraphConnection>();
						objConnections.put(connection.getObject(), list);
					}
					list.add(connection);
				}
			}
		} else {
			objConnections=null;
		}

		int maxEnemies=0;
		ArrayList<Paragraph> book = model.getParagraphs();
		
		ArrayList<Integer> numbers = new ArrayList<Integer>(book.size());
		for (Paragraph paragraph : book) {
			numbers.add(paragraph.getNumber());
			if (paragraph.getEnemies() != null) {
				if (maxEnemies < paragraph.getEnemies().size() ) {
					maxEnemies = paragraph.getEnemies().size();
				}
			}
		}
		Collections.sort(numbers);
		writeGoProc(0,numbers.size(),numbers,false);
		
		ArrayList<ObjectBean> objs =  model.getObjects();
		ArrayList<AbstractParameter> params = model.getParameters();
		
		//check for Parameters
		limits = new HashSet<Parameter>();
		StringBuilder battleAlchemy=null;
		StringBuilder peaceAlchemy=null;
		StringBuilder enableAlchemy=null;
		StringBuilder npcNames=null;
		StringBuilder alchemyUsage=null;
		for (AbstractParameter par : params) {
			if (par instanceof Parameter) {
				Parameter parameter = (Parameter) par;
				if (parameter.getLimit() != null) {
					limits.add(parameter.getLimit());
				}
				if (parameter.isVital()){
					vitals.add(parameter);
				}
			} else if (par instanceof Alchemy) {
				Alchemy alchemy = (Alchemy)par;
				if (alchemyUsage==null) {
					alchemyUsage = new StringBuilder();
				}
				if (alchemy.getBattleLimit() != null) {
					battleAlchemyLimits.add(alchemy.getBattleLimit());
				}
				alchemyUsage.append('#');
				alchemyUsage.append(getID(PROC_USE_ALCEMY+alchemy.getId()));
				alchemyUsage.append('\n');
				alchemyUsage.append(getID(VAR_ALCHEMY_USED_IN_BATTLE_ROUND));
				alchemyUsage.append("=1\n");
				alchemyUsage.append(getID(VAR_PARAMETER_PREFIX,alchemy.getFrom().getId()));
				alchemyUsage.append('=');
				alchemyUsage.append(getID(VAR_PARAMETER_PREFIX,alchemy.getFrom().getId()));
				alchemyUsage.append('-');
				alchemyUsage.append(alchemy.getFromValue());
				alchemyUsage.append('\n');
				if (alchemy.getBattleLimit() != null) {
					alchemyUsage.append("if ");
					alchemyUsage.append(getID(VAR_BATTLE_MODE));
					alchemyUsage.append(">0 : ");
					alchemyUsage.append(getID(VAR_TEMP_PARAMETER_PREFIX+alchemy.getBattleLimit().getId()));
					alchemyUsage.append('=');
					alchemyUsage.append(getID(VAR_TEMP_PARAMETER_PREFIX+alchemy.getBattleLimit().getId()));
					alchemyUsage.append("-1\n");
				}
				addDiceValue(alchemyUsage, alchemy.getToValue(),false);
				if (alchemy.isWeapon()) {
//					alchemyUsage.append('\n');
					for (int i = 0; i < maxEnemies; i++) {
						alchemyUsage.append("if ");
						alchemyUsage.append(getID(VAR_SELECTED_ENEMY));
						alchemyUsage.append('=');
						alchemyUsage.append(i);
						alchemyUsage.append(" : ");
						alchemyUsage.append(getID(VAR_ENEMY_PREFIX,i,VAR_PARAMETER_PREFIX,alchemy.getTo().getId()));
						alchemyUsage.append('=');
						alchemyUsage.append(getID(VAR_ENEMY_PREFIX,i,VAR_PARAMETER_PREFIX,alchemy.getTo().getId()));
						alchemyUsage.append('-');
						alchemyUsage.append(getID(VAR_DICE));
						if (alchemy.getTo().isVital() || alchemy.getTo().isNegative()==false) {
							alchemyUsage.append("&if ");
							alchemyUsage.append(getID(VAR_ENEMY_PREFIX,i,VAR_PARAMETER_PREFIX,alchemy.getTo().getId()));
							alchemyUsage.append("<=0 : ");
							alchemyUsage.append(getID(VAR_ENEMY_PREFIX,i,VAR_PARAMETER_PREFIX,alchemy.getTo().getId()));
							alchemyUsage.append("=0");
							if (alchemy.getTo().isVital()) {
								//kill the enemy
								alchemyUsage.append('&');
								alchemyUsage.append(getID(VAR_BATTLE_MODE));
								alchemyUsage.append('=');
								alchemyUsage.append(getID(VAR_BATTLE_MODE));
								alchemyUsage.append("-1&GS '");
								alchemyUsage.append(getID(PROC_SELECT_TARGET_AUTO));
								alchemyUsage.append('\'');
							}
						}
						alchemyUsage.append('\n');
					}
				} else {
					alchemyUsage.append('&');
					alchemyUsage.append(getID(VAR_PARAMETER_PREFIX,alchemy.getTo().getId()));
					alchemyUsage.append('=');
					alchemyUsage.append(getID(VAR_PARAMETER_PREFIX,alchemy.getTo().getId()));
					alchemyUsage.append('+');
					alchemyUsage.append(getID(VAR_DICE));
					
					if (model.getSettings().isOverflowControl() ^ alchemy.isOverflowControl()) {
						if (alchemy.getTo().getLimit() != null) {
							//control limits
							alchemyUsage.append("&if ");
							alchemyUsage.append(getID(VAR_PARAMETER_PREFIX,alchemy.getTo().getLimit().getId()));
							alchemyUsage.append('<');
							alchemyUsage.append(getID(VAR_PARAMETER_PREFIX,alchemy.getTo().getId()));
							alchemyUsage.append(" : ");
							alchemyUsage.append(getID(VAR_PARAMETER_PREFIX,alchemy.getTo().getId()));
							alchemyUsage.append('=');
							alchemyUsage.append(getID(VAR_PARAMETER_PREFIX,alchemy.getTo().getLimit().getId()));
						}
					}
					alchemyUsage.append('\n');
					
				}
				alchemyUsage.append("XGT '");
				alchemyUsage.append(getID(PROC_GO_CURRENT));
				alchemyUsage.append("'\n-\n");
				
				alchemyPresent = true;
				if (enableAlchemy==null) {
					enableAlchemy = new StringBuilder();
					enableAlchemy.append('#');
					enableAlchemy.append(getID(PROC_ENABLE_ALCEMY));
					enableAlchemy.append('\n');
				}
				enableAlchemy.append("if ");
				enableAlchemy.append(getID(VAR_ALCHEMY_DISABLED_PREFIX,alchemy.getId()));
				enableAlchemy.append("=1 : ");
				enableAlchemy.append(getID(VAR_ALCHEMY_DISABLED_PREFIX,alchemy.getId()));
				enableAlchemy.append("=0\n");
				
				if (alchemy.isOnDemand()==false) {
					if (alchemy.getPlace() != Alchemy.PLACE_BATTLE) {
						//peace
						if (peaceAlchemy==null) {
							alchemyPeacePresent = true;
							peaceAlchemy = new StringBuilder();
							peaceAlchemy.append('#');
							peaceAlchemy.append(getID(PROC_ADD_PEACE_ALCHEMY));
							peaceAlchemy.append('\n');
							peaceAlchemy.append("*NL \n");
						}
						addAlchemy(peaceAlchemy,alchemy,false);
						
					}
					if (alchemy.getPlace() != Alchemy.PLACE_PEACE) {
						//battle
						if (battleAlchemy==null) {
							alchemyBattlePresent = true;
							battleAlchemy = new StringBuilder();
							battleAlchemy.append('#');
							battleAlchemy.append(getID(PROC_ADD_BATTLE_ALCHEMY));
							battleAlchemy.append('\n');
							battleAlchemy.append("*NL \n");
						}
						addAlchemy(battleAlchemy,alchemy,true);
					}
				}
			} else if (par instanceof NPC) {
				NPC npc = (NPC)par;
				if (npcNames==null) {
					npcNames = new StringBuilder();
					npcNames.append('#');
					npcNames.append(getID(PROC_SHOW_ENEMY_NAME));
					npcNames.append('\n');
				}
				npcNames.append("if ");
				npcNames.append(getID(VAR_CURRENT_ENEMY_TYPE));
				npcNames.append('=');
				npcNames.append(npc.getId());
				npcNames.append(" : *P '");
				printLine(npcNames,npc.getName());
				npcNames.append(" '\n");
			}
		}
		if (npcNames != null) {
			buffer.append(npcNames.toString());
			buffer.append("-\n");
		}
		if (peaceAlchemy != null) {
			buffer.append(peaceAlchemy.toString());
			buffer.append("-\n");
		}
		if (battleAlchemy != null) {
			buffer.append(battleAlchemy.toString());
			buffer.append("-\n");
		}
		if (enableAlchemy != null) {
			buffer.append(enableAlchemy.toString());
			buffer.append("-\n");
		}
		if (alchemyUsage != null) {
			buffer.append(alchemyUsage.toString());
		}
		StringBuilder[] showEnemyFunctions = new StringBuilder[maxEnemies];
		StringBuilder[] checkEnemyAliveFunctions = new StringBuilder[maxEnemies];
		
		for (int i = 0; i < maxEnemies; i++) {
			showEnemyFunctions[i] = new StringBuilder().append('#')
				.append(getID(PROC_SHOW_ENEMY_PARAMS,i)).append('\n');
			checkEnemyAliveFunctions[i] = new StringBuilder().append('#')
				.append(getID(PROC_CHECK_ENEMY_ALIVE,i)).append('\n');
		}
		hasParameters = false;
		for (AbstractParameter par : params) {
			if (par instanceof Parameter) {
				Parameter parameter = (Parameter) par;
				if (limits.contains(parameter)) {
					//limit is shown with main parameter
					continue;
				}
				if (parameter.isHeroOnly()==false) {
					//NPC param
					for (int i = 0; i < maxEnemies; i++) {
						if (parameter.isInvisible()==false) {
							//do not show invisible parameters
							showEnemyFunctions[i].append("if ")
								.append(getID(VAR_ENEMY_PREFIX,i,VAR_PARAMETER_PREFIX,parameter.getId()))
								.append("<>0 : *P '");
							printLine(showEnemyFunctions[i], parameter.getName());
							
							showEnemyFunctions[i].append(" <<")	
								.append(getID(VAR_ENEMY_PREFIX,i,VAR_PARAMETER_PREFIX,parameter.getId()))
							    .append(">> '\n");
						}
						
						if (parameter.isVital()) {
							//check for vital parameters
							showEnemyFunctions[i].append("if ")
							.append(getID(VAR_ENEMY_PREFIX,i,VAR_PARAMETER_PREFIX,parameter.getId()))
							.append("<=0 : ")
							.append(getID(VAR_ENEMY_PREFIX,i,VAR_PARAMETER_PREFIX,parameter.getId()))
							.append("=0&")
							.append(getID(VAR_CURRENT_ENEMY_TYPE))
							.append("=0\n");
						}
						
						//check for alive
						if (parameter.isVital()) {
							checkEnemyAliveFunctions[i].append("if ")
							.append(getID(VAR_ENEMY_PREFIX,i,VAR_PARAMETER_PREFIX,parameter.getId()))
							.append("<=0 : ")
							.append(getID(VAR_CURRENT_ENEMY_TYPE))
							.append("=0\n");
						}
					}
				}
				if (hasParameters==false) {
					buffer.append('#');
					buffer.append(getID(PROC_SHOW_PARAMS));
					buffer.append("\nCLEAR\n");
					hasParameters = true;
				}
				if (parameter.isInvisible()==false) {
					//do not show invisible parameters
					buffer.append("if ");
					if (battleAlchemyLimits.contains(parameter)) {
						//special limit for the battle
						buffer.append(getID(VAR_BATTLE_MODE));
						buffer.append(">0 and ");
						buffer.append(getID(VAR_TEMP_PARAMETER_PREFIX));
						buffer.append(">0 : ");
						buffer.append("PL '");
						buffer.append(parameter.getName());
						buffer.append(":<<");
						buffer.append(getID(VAR_TEMP_PARAMETER_PREFIX));
						buffer.append(parameter.getId());
						buffer.append(">>");
						if (parameter.getLimit() != null) {
							buffer.append("/<<");
							buffer.append(getID(VAR_PARAMETER_PREFIX,parameter.getLimit().getId()));
							buffer.append(">>");
						}
						buffer.append(" '\nif ");
						buffer.append(getID(VAR_BATTLE_MODE));
						buffer.append("=0 and ");
					}
					buffer.append(getID(VAR_PARAMETER_PREFIX,parameter.getId()));
					buffer.append("<>0 : ");
					buffer.append("PL '");
					buffer.append(parameter.getName());
					buffer.append(":<<");
					buffer.append(getID(VAR_PARAMETER_PREFIX,parameter.getId()));
					buffer.append(">>");
					if (parameter.getLimit() != null) {
						buffer.append("/<<");
						buffer.append(getID(VAR_PARAMETER_PREFIX,parameter.getLimit().getId()));
						buffer.append(">>");
					}
					buffer.append(" '\n");
				}
			}
			
		}
		if (hasParameters) {
			buffer.append("-\n");
		}
		
		for (int i = 0; i < maxEnemies; i++) {
			buffer.append('#');
			buffer.append(getID(PROC_CHECK_ENEMY_ALIVE_AND_SHOW_SELECTION,i));
			buffer.append("\nGS '");
			buffer.append(getID(PROC_CHECK_ENEMY_ALIVE,i));
			buffer.append("'\nif ");
			buffer.append(getID(VAR_CURRENT_ENEMY_TYPE));
			buffer.append(">0 : ");
			buffer.append(getID(VAR_SELECTOR));
			buffer.append('=');
			buffer.append(getID(VAR_SELECTOR));
			buffer.append("+1&*NL '<<");
			buffer.append(getID(VAR_SELECTOR));
			buffer.append(">> '&");
			
			buffer.append("ACT '");
			buffer.append(appMessages.qspAttackEnemty(getID(VAR_SELECTOR)));
			buffer.append("':GT '");
			buffer.append(getID(PROC_SELECT_ENEMY,i));
			buffer.append("'\n");
			
			buffer.append("if ");
			buffer.append(getID(VAR_CURRENT_ENEMY_TYPE));
			buffer.append(">0 : ");
			buffer.append("GS '");
			buffer.append(getID(PROC_SHOW_ENEMY_NAME));
			buffer.append("'\n-\n");
			
			buffer.append('#');
			buffer.append(getID(PROC_CHECK_ENEMY_ALIVE_AND_SELECT,i));
			buffer.append("\nGS '");
			buffer.append(getID(PROC_CHECK_ENEMY_ALIVE,i));
			buffer.append("'\nif ");
			buffer.append(getID(VAR_CURRENT_ENEMY_TYPE));
			buffer.append(">0 : ");
			buffer.append(getID(VAR_SELECTED_ENEMY));
			buffer.append('=');
			buffer.append(i);
			buffer.append('&');
			buffer.append(getID(VAR_SELECTOR));
			buffer.append('=');
			buffer.append(getID(VAR_BATTLE_MODE));
			buffer.append("\n-\n");
			
			buffer.append('#');
			buffer.append(getID(PROC_CHECK_ENEMY_ALIVE_AND_KILL,i));
			buffer.append("\nGS '");
			buffer.append(getID(PROC_CHECK_ENEMY_ALIVE,i));
			//is alive?
			buffer.append("'\nif ");
			buffer.append(getID(VAR_CURRENT_ENEMY_TYPE));
			buffer.append("=0 : ");
			buffer.append(getID(VAR_BATTLE_MODE));
			buffer.append('=');
			buffer.append(getID(VAR_BATTLE_MODE));
			buffer.append("-1\nif ");
			buffer.append(getID(VAR_CURRENT_ENEMY_TYPE));
			buffer.append("=0 : GS '");
			buffer.append(getID(PROC_SELECT_TARGET_AUTO));
			buffer.append("'\n-\n");
			
		}

		//functions for printing NPC parameters and checking is alive
		for (int i = 0; i < maxEnemies; i++) {
			showEnemyFunctions[i].append("-\n");
			buffer.append(showEnemyFunctions[i].toString());
			
			checkEnemyAliveFunctions[i].append("\n-\n");
			buffer.append(checkEnemyAliveFunctions[i].toString());
			
			buffer.append('#');
			buffer.append(getID(PROC_SHOW_ENEMY_SELECTION,i));
			buffer.append("\nif ");
			buffer.append(getID(VAR_BATTLE_MODE));
			buffer.append(">1 and ");
			buffer.append(getID(VAR_SELECTED_ENEMY));
			buffer.append('=');
			buffer.append(i);
			buffer.append(" : *P '");
			printLine(buffer,appConstants.urqTarget());
			buffer.append("'\n-\n");
			
		}
		
		if (maxEnemies>1) {
			//manual target selection procedure
			buffer.append('#');
			buffer.append(getID(PROC_SELECT_TARGET));
			buffer.append("\n*PL '");
			buffer.append(appConstants.urqSelectTarget());
			buffer.append("'\n");
			buffer.append(getID(VAR_SELECTOR));
			buffer.append("=0\n");
			for (int i = 0; i < maxEnemies; i++) {
				buffer.append("if ");
				buffer.append(getID(VAR_SELECTOR));
				buffer.append('<');
				buffer.append(getID(VAR_BATTLE_MODE));
				buffer.append(" : ");
				buffer.append(getID(VAR_CURRENT_ENEMY_TYPE));
				buffer.append('=');
				buffer.append(getID(VAR_ENEMY_PREFIX,i));
				buffer.append("&GS '");
				buffer.append(getID(PROC_CHECK_ENEMY_ALIVE_AND_SHOW_SELECTION,i));
				buffer.append("'\n");
			}
			addButton(buffer,appConstants.urqCancelTargetSelection(),getID(PROC_GO_CURRENT));
			buffer.append("\n-\n");
			//functions for set selected Enemy
			for (int i = 0; i < maxEnemies; i++) {
				buffer.append('#');
				buffer.append(getID(PROC_SELECT_ENEMY,i));
				buffer.append('\n');
				buffer.append(getID(VAR_SELECTED_ENEMY));
				buffer.append('=');
				buffer.append(i);
				buffer.append("\nXGT '");
				buffer.append(getID(PROC_GO_CURRENT));
				buffer.append("'\n-\n");
			}
		}
		if (maxEnemies>0) {
			//auto target selection procedure
			buffer.append('#');
			buffer.append(getID(PROC_SELECT_TARGET_AUTO));
			buffer.append('\n');
			buffer.append(getID(VAR_SELECTOR));
			buffer.append("=0\n");
			buffer.append(getID(VAR_SELECTED_ENEMY));
			buffer.append("=-1\n");
			for (int i = 0; i < maxEnemies; i++) {
				buffer.append("if ");
				buffer.append(getID(VAR_SELECTOR));
				buffer.append('<');
				buffer.append(getID(VAR_BATTLE_MODE));
				buffer.append(" : ");
				buffer.append(getID(VAR_CURRENT_ENEMY_TYPE));
				buffer.append("=1&GS '");
				buffer.append(getID(PROC_CHECK_ENEMY_ALIVE_AND_SELECT,i));
				buffer.append("'\n");
			}
			buffer.append("-\n");
			//attack selected enemy
			buffer.append('#');
			buffer.append(getID(PROC_ATTACK));
			buffer.append('\n');
			buffer.append(getID(VAR_HERO_ATTACK_SELECTION));
			buffer.append('=');
			buffer.append(getID(VAR_SELECTED_ENEMY));
			buffer.append('&');
			buffer.append(getID(VAR_ALCHEMY_USED_IN_BATTLE_ROUND));
			buffer.append("=0\n");
			buffer.append("XGT '");
			buffer.append(getID(PROC_GO_CURRENT));
			buffer.append("'\n-\n");
		}
		
		if (model.getSettings().isShowModificators()) {
			//check for modificator
			buffer.append(getID(VAR_MODIFICATOR_EXISTS));
			buffer.append("=0\n");
			boolean firstMod = true;
			for (AbstractParameter parameter : params) {
				if (parameter instanceof Modificator) {
					Modificator modificator = (Modificator) parameter;
					if (firstMod) {
						buffer.append("p '");
						buffer.append(appConstants.playerListModificators());
						buffer.append("'\n");
						firstMod = false;
					}
					buffer.append("if ");
					buffer.append(getID(VAR_PARAMETER_PREFIX,modificator.getId()));
					buffer.append('=');
					buffer.append(VALUE_MODIFICATOR_SET);
					buffer.append(" : ");
					buffer.append("p '");
					buffer.append(modificator.getName());
					buffer.append("'&");
					buffer.append(getID(VAR_MODIFICATOR_EXISTS));
					buffer.append("=1\n");
				}
			}
			if (firstMod==false) {
				buffer.append("if ");
				buffer.append(getID(VAR_MODIFICATOR_EXISTS));
				buffer.append("=0 : *PL '");
				buffer.append(appConstants.urqNoModificators());
				buffer.append("'\nif ");
				buffer.append(getID(VAR_MODIFICATOR_EXISTS));
				buffer.append("=1 : *NL ");
				buffer.append('\n');
			}
		}
		buffer.append("-\n");
		
		buffer.append('#');
		buffer.append(getID(PROC_USE_OBJECT));
		buffer.append("\nif ");
		buffer.append(getID(VAR_MUST_GO));
		buffer.append(">0 : EXIT\n");
	
		//object using actions
		for (ObjectBean bean : objs) {
			StringBuilder builder = new StringBuilder(bean.getName());
			for (int i = 0; i < builder.length(); i++) {
				char c = builder.charAt(i);
				if (c=='\'') {
					builder.insert(i++, '\'');
				} else if (Character.isJavaIdentifierPart(c)==false) {
					builder.setCharAt(i, ' ');
				}
			}
//			if (builder.length()>32) {
//				builder.setLength(32);
//			}
			objects.put(bean,builder.toString());
			if (objConnections != null) {
				buffer.append("if $SELOBJ='");
				buffer.append(builder);
				buffer.append("' :\n");
				ArrayList<ParagraphConnection> list = objConnections.get(bean);
				for (ParagraphConnection connection : list) {
					buffer.append("if ");
					buffer.append(getID(VAR_CURRENT_PARAGRAPH));
					buffer.append('=');
					buffer.append(connection.getFrom().getNumber());
					buffer.append(" : UNSELECT&GT '");
					buffer.append(getID(PARAGRAPH_PREFIX,connection.getTo().getNumber()));
					buffer.append("'\n");
				}
				buffer.append("END\n");
			}
		}
		buffer.append("*PL '");
		buffer.append(appConstants.urqCannotUseObject());
		buffer.append("'\nUNSELECT\n-\n");
		
		//check for vital parameters
		buffer.append('#');
		buffer.append(getID(PROC_VITAL));
		buffer.append('\n');
		buffer.append(getID(VAR_TEMP));
		buffer.append("=1\n");
		for (Parameter parameter : vitals) {
			buffer.append("if ");
			buffer.append(getID(VAR_PARAMETER_PREFIX,parameter.getId()));
			buffer.append("<=0 : ");
			buffer.append(getID(VAR_MUST_GO));
			buffer.append("=1&");
			buffer.append(getID(VAR_TEMP));
			buffer.append("=0&");
			buffer.append(getID(VAR_CAN_GO));
			buffer.append("=-1&*PL '");
			buffer.append(appMessages.urqHeroDiedByVitalParameter(parameter.getName()));
			buffer.append("'\n");
		}
		//restart if Game is Over
		buffer.append("if ");
		buffer.append(getID(VAR_TEMP));
		buffer.append("=0 : ");
		addButton(buffer,appConstants.playerTitleRestartGame(),getID(PARAGRAPH_PREFIX,model.getStartParagraph().getNumber()));
		buffer.append("\n-\n");
	}

	private void addButton(StringBuilder buffer, String text,String location) {
		buffer.append("ACT '");
		printLine(buffer, text);
		buffer.append("':GT '");
		buffer.append(location);
		buffer.append("'\n");
	}

	private void writeGoProc(int from, int to, ArrayList<Integer> numbers,boolean prefix) {
		if (to-from>10) {
			//use sub calls
			int mid = from+(to-from)/2;
			int num = numbers.get(mid);
			writeGoProc(from,mid,numbers,true);
			writeGoProc(mid,to,numbers,true);
			
			buffer.append('#');
			if (prefix) {
				buffer.append(getID(PROC_GO_CURRENT,from,'_',to));
			} else {
				buffer.append(getID(PROC_GO_CURRENT));
			}
			buffer.append('\n');
			buffer.append("if ");
			buffer.append(getID(VAR_CURRENT_PARAGRAPH));
			buffer.append('<');
			buffer.append(num);
			buffer.append(" : XGT '");
			buffer.append(getID(PROC_GO_CURRENT,from,'_',mid));
			buffer.append("'\nXGT '");
			buffer.append(getID(PROC_GO_CURRENT,mid,'_',to));
			buffer.append("'\n-\n");
		} else {
			buffer.append('#');
			if (prefix) {
				buffer.append(getID(PROC_GO_CURRENT,from,'_',to));
			} else {
				buffer.append(getID(PROC_GO_CURRENT));
			}
			buffer.append('\n');
			int len = to-1;
			for (int i = from; i < len; i++) {
				int num = numbers.get(i);
				buffer.append("if ");
				buffer.append(getID(VAR_CURRENT_PARAGRAPH));
				buffer.append('=');
				buffer.append(num);
				buffer.append(" : XGT '");
				buffer.append(getID(PARAGRAPH_PREFIX,num));
				buffer.append("'\n");
			}
			int num = numbers.get(len);
			buffer.append("XGT '");
			buffer.append(getID(PARAGRAPH_PREFIX,num));
			buffer.append("'\n-\n");
		}
	}

	private void addBattleCalculation(StringBuilder builder, Battle battle, int enemyCounter, boolean heroAttack, boolean printResult) {
		//attack vs. attack
		if (heroAttack) {
			if (battle.isOneTurnBattle()) {
				//one turn
				builder.append("if ");
				builder.append(getID(VAR_ATTACK_VALUE));
				builder.append('>');
				builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_ATTACK_VALUE));
				builder.append(" : ");
				if (printResult) {
					builder.append("*P '");
					builder.append(appConstants.urqKilled());
					builder.append('\'');
				} else {
					builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_PARAMETER_PREFIX,battle.getVital().getId()));
					builder.append("=0&");
					builder.append(getID(VAR_BATTLE_MODE));
					builder.append('=');
					builder.append(getID(VAR_BATTLE_MODE));
					builder.append("-1&GS '");
					builder.append(getID(PROC_SELECT_TARGET_AUTO));
					builder.append('\'');
				}
				builder.append('\n');
			} else {
				addBattleHeroAttackEnemy(builder,battle,enemyCounter,heroAttack,printResult);
			}
		} else if (battle.isOneTurnBattle()) {
			builder.append("if ");
			builder.append(getID(VAR_ATTACK_VALUE));
			builder.append('<');
			builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_ATTACK_VALUE));
			builder.append(" : ");
			if (printResult) {
				builder.append("*P '");
				builder.append(appConstants.urqKilled());
				builder.append('\'');
			} else {
				builder.append(getID(VAR_PARAMETER_PREFIX,battle.getVital().getId()));
				builder.append("=0");
			}
			builder.append('\n');
		} else if (battle.isOneTurnBattle()==false) {
			//many turns, enemy attack
			addBattleEmemyAttackHero(builder,battle,enemyCounter,heroAttack,printResult);
		}
	}

	private void addBattleEmemyAttackHero(StringBuilder builder, Battle battle,	int enemyCounter, boolean heroAttack, boolean printResult) {
		//Enemy attack
		if (battle.getFatal() == Battle.FATAL_NORMAL) {
			builder.append("if ");
			builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_FATAL));
			builder.append("=1 : ");
			builder.append(getID(VAR_TEMP));
			builder.append('=');
			if (battle.isDifferenceIsDamage()) {
				builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_ATTACK_VALUE));
			} else {
				builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_DAMAGE_VALUE));
			}
			builder.append('&');
			if (printResult) {
				builder.append("&*P ' ");
				builder.append(appMessages.urqBattleFatalStrike(getID(VAR_TEMP),BEFORE_PARAM,AFTER_PARAM));
				builder.append('\'');
			} else {
				builder.append('&');
				builder.append(getID(VAR_PARAMETER_PREFIX,battle.getVital().getId()));
				builder.append('=');
				builder.append(getID(VAR_PARAMETER_PREFIX,battle.getVital().getId()));
				builder.append('-');
				builder.append(getID(VAR_TEMP));
				builder.append("&if ");
				builder.append(getID(VAR_PARAMETER_PREFIX,battle.getVital().getId()));
				builder.append("<0 : ");
				builder.append(getID(VAR_PARAMETER_PREFIX,battle.getVital().getId()));
				builder.append("=0");
			}
			builder.append('\n');
		}
		builder.append("if ");
		if (battle.getFatal() == Battle.FATAL_NORMAL) {
			builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_FATAL));
			builder.append("=0 and ");
		}
		if (battle.isAttackDefense()) {
			builder.append(getID(VAR_DEFENSE_VALUE));
		} else {
			builder.append(getID(VAR_ATTACK_VALUE));
		}
		builder.append('<');
		builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_ATTACK_VALUE));
		builder.append(" : ");
		builder.append(getID(VAR_TEMP));
		builder.append('=');
		if (battle.isDifferenceIsDamage()) {
			builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_ATTACK_VALUE));
			builder.append('-');
			if (battle.isAttackDefense()) {
				builder.append(getID(VAR_DEFENSE_VALUE));
			} else {
				builder.append(getID(VAR_ATTACK_VALUE));
			}
		} else {
			builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_DAMAGE_VALUE));
		}
		if (printResult) {
			builder.append("&*P ' ");
			builder.append(appMessages.urqBattleRoundDamage(getID(VAR_TEMP),BEFORE_PARAM,AFTER_PARAM));
			builder.append(" '");
		} else {
			builder.append('&');
			builder.append(getID(VAR_PARAMETER_PREFIX,battle.getVital().getId()));
			builder.append('=');
			builder.append(getID(VAR_PARAMETER_PREFIX,battle.getVital().getId()));
			builder.append('-');
			builder.append(getID(VAR_TEMP));
			builder.append("&if ");
			builder.append(getID(VAR_PARAMETER_PREFIX,battle.getVital().getId()));
			builder.append("<0 : ");
			builder.append(getID(VAR_PARAMETER_PREFIX,battle.getVital().getId()));
			builder.append("=0");
		}
		builder.append('\n');
	}

	private void addBattleHeroAttackEnemy(StringBuilder builder, Battle battle, int enemyCounter, boolean heroAttack, boolean printResult) {
		//Hero attack enemy
		if (battle.getFatal() != Battle.FATAL_NONE) {
			//check for Fatal Strike
			builder.append("if ");
			builder.append(getID(VAR_FATAL));
			builder.append("=1 : ");
			if (battle.getFatal() == Battle.FATAL_DEAD) {
				if (printResult) {
					builder.append("*P ' ");
					builder.append(appConstants.urqKillByFatalStrike());
					builder.append("'\n");
				} else {
					builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_PARAMETER_PREFIX,battle.getVital().getId()));
					builder.append("=0&");
					builder.append(getID(VAR_BATTLE_MODE));
					builder.append('=');
					builder.append(getID(VAR_BATTLE_MODE));
					builder.append("-1&GS '");
					builder.append(getID(PROC_SELECT_TARGET_AUTO));
					builder.append("'\n");
				}
			} else {
				builder.append(getID(VAR_TEMP));
				builder.append('=');
				if (battle.isDifferenceIsDamage()) {
					builder.append(getID(VAR_ATTACK_VALUE));
				} else {
					builder.append(getID(VAR_DAMAGE_VALUE));
				}
				builder.append('&');
				if (printResult) {
					builder.append("*P ' ");
					builder.append(appMessages.urqBattleFatalStrike(getID(VAR_TEMP),BEFORE_PARAM,AFTER_PARAM));
					builder.append(" '&if ");
					builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_PARAMETER_PREFIX,battle.getVital().getId()));
					builder.append("<=0 : *P '");
					builder.append(appConstants.urqKilled());
					builder.append("'\n");
				} else {
					builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_PARAMETER_PREFIX,battle.getVital().getId()));
					builder.append('=');
					builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_PARAMETER_PREFIX,battle.getVital().getId()));
					builder.append('-');
					builder.append(getID(VAR_TEMP));
					//is alive?
					builder.append("&if ");
					builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_PARAMETER_PREFIX,battle.getVital().getId()));
					builder.append("<=0 : ");
					builder.append(getID(VAR_BATTLE_MODE));
					builder.append('=');
					builder.append(getID(VAR_BATTLE_MODE));
					builder.append("-1&GS '");
					builder.append(getID(PROC_SELECT_TARGET_AUTO));
					builder.append("'\n");
				}
			}
		}
		builder.append("if ");
		if (battle.getFatal() != Battle.FATAL_NONE) {
			builder.append(getID(VAR_FATAL));
			builder.append("=0 and ");
		}
		builder.append(getID(VAR_ATTACK_VALUE));
		builder.append('>');
		if (battle.isAttackDefense()) {
			builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_DEFENSE_VALUE));
		} else {
			builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_ATTACK_VALUE));
		}
		builder.append(" : ");
		builder.append(getID(VAR_TEMP));
		builder.append('=');
		if (battle.isDifferenceIsDamage()) {
			builder.append(getID(VAR_ATTACK_VALUE));
			builder.append('-');
			if (battle.isAttackDefense()) {
				builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_DEFENSE_VALUE));
			} else {
				builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_ATTACK_VALUE));
			}
		} else {
			builder.append(getID(VAR_DAMAGE_VALUE));
		}
		if (printResult) {
			builder.append("&if ");
			builder.append(getID(VAR_TEMP));
			builder.append(">0 : *P ' ");
			builder.append(appMessages.urqBattleRoundDamage(getID(VAR_TEMP),BEFORE_PARAM,AFTER_PARAM));
			builder.append(" '&if ");
			builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_PARAMETER_PREFIX,battle.getVital().getId()));
			builder.append("<=0 : *P '");
			builder.append(appConstants.urqKilled());
			builder.append(" '\n");
		} else {
			builder.append('&');
			builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_PARAMETER_PREFIX,battle.getVital().getId()));
			builder.append('=');
			builder.append(getID(VAR_ENEMY_PREFIX,enemyCounter,VAR_PARAMETER_PREFIX,battle.getVital().getId()));
			builder.append('-');
			builder.append(getID(VAR_TEMP));
			builder.append('&');
			//is still alive
			builder.append(getID(VAR_CURRENT_ENEMY_TYPE));
			builder.append("=1&GS '");
			builder.append(getID(PROC_CHECK_ENEMY_ALIVE_AND_KILL,enemyCounter));
			builder.append("'\n");
		}
	}

	private void addAlchemy(StringBuilder builder, Alchemy alchemy,boolean battle) {
		builder.append("if ");
		builder.append(getID(VAR_MUST_GO));
		builder.append("=0 and ");
		builder.append(getID(VAR_BATTLE_MODE));
		if (battle) {
			builder.append(">0");
			if (alchemy.isOneTimePerRound()) {
				builder.append(" and ");
				builder.append(getID(VAR_ALCHEMY_USED_IN_BATTLE_ROUND));
				builder.append("=0");
			}
			if (alchemy.getBattleLimit() != null) {
				builder.append(" and ");
				builder.append(getID(VAR_TEMP_PARAMETER_PREFIX,alchemy.getBattleLimit().getId()));
				builder.append(">0");
			}
		} else {
			builder.append("=0");
		}
		builder.append(" and ");
		builder.append(getID(VAR_PARAMETER_PREFIX,alchemy.getFrom().getId()));
		builder.append(">=");
		builder.append(alchemy.getFromValue());
		builder.append(" and ");
		builder.append(getID(VAR_ALCHEMY_DISABLED_PREFIX,alchemy.getId()));
		builder.append("=0 : ");
		addButton(builder, alchemy.getName(), getID(PROC_USE_ALCEMY,alchemy.getId()));
	}
	
	public ParagraphParsingHandler getParagraphParsingHandler() {
		return null;
	}
	
	public void appendParagraph(int number,String text,Paragraph paragraph,ArrayList<ParagraphConnection> connections,ArrayList<ParagraphConnection> incomeConnections) {
		StringBuilder initBuffer = new StringBuilder();
		boolean showParams = true;//paragraph==model.getStartParagraph() || (paragraph.getChangeParameters() != null && paragraph.getChangeParameters().size()>0);
		buffer.append('#');
		buffer.append(getID(PARAGRAPH_PREFIX,number));
		buffer.append('\n');
		//current paragraph number
		if (paragraph==model.getStartParagraph()) {
			buffer.append("if ");
			buffer.append(getID(VAR_CURRENT_PARAGRAPH));
			buffer.append("<>");
			buffer.append(number);
			buffer.append(" :\nKILLALL&USEHTML=1&CLOSE ALL&");
			buffer.append("$ONOBJSEL='");
			buffer.append(getID(PROC_USE_OBJECT));
			buffer.append("'\n$");
			buffer.append(getID(VAR_SOUND));
			buffer.append("=''\n$");
			buffer.append(getID(VAR_MUSIC));
			buffer.append("=''\n");
			if (model.getSettings().isDisableAudio()==false && model.getSounds().size()>0) {
				buffer.append("$COUNTER='");
				buffer.append(getID(PROC_TIMER));
				buffer.append("'\n");
			}
			for (AbstractParameter par : model.getParameters()) {
				if (par instanceof Parameter) {
					Parameter parameter = (Parameter) par;
					if (parameter.isHeroHasInitialValue()) {
						//initialize this parameter
						addDiceValue(buffer,parameter.getHeroInitialValue(),false);
						buffer.append(getID(VAR_PARAMETER_PREFIX,parameter.getId()));
						buffer.append('=');
						buffer.append(getID(VAR_DICE));
						buffer.append('\n');
					}
				}
			}
			//check for initialized limits for non-initialized parameters
			for (AbstractParameter par : model.getParameters()) {
				if (par instanceof Parameter) {
					Parameter parameter = (Parameter) par;
					if (parameter.isHeroHasInitialValue()==false && parameter.getLimit() != null && parameter.getLimit().isHeroHasInitialValue()) {
						//initialize this parameter by limit
						buffer.append(getID(VAR_PARAMETER_PREFIX,parameter.getId()));
						buffer.append('=');
						buffer.append(getID(VAR_PARAMETER_PREFIX,parameter.getLimit().getId()));
						buffer.append('\n');
					}
				}
			}
			buffer.append("END\n");
		}
		buffer.append(getID(VAR_MUST_GO));
		buffer.append("=0\n");
		if (model.getSettings().isDisableAudio()==false) {
			if (paragraph.hasBackgroundSounds()) {
				//set music
				String url = paragraph.getNextBackgroundSound().getUrl();
	//			buffer.append("if ");
	//			buffer.append(getID(VAR_CURRENT_PARAGRAPH));
	//			buffer.append("<>");
	//			buffer.append(number);
	//			buffer.append(" :\n");
				buffer.append("if ISPLAY($");
				buffer.append(getID(VAR_MUSIC));
				buffer.append(") and $");
				buffer.append(getID(VAR_MUSIC));
				buffer.append("<>'");
				buffer.append(url);
				buffer.append("' : CLOSE($");
				buffer.append(getID(VAR_MUSIC));
				buffer.append(")\n$");
				buffer.append(getID(VAR_MUSIC));
				buffer.append("='");
				buffer.append(url);
				buffer.append("'\nPLAY $");
				buffer.append(getID(VAR_MUSIC));
				buffer.append('\n');
	//			buffer.append("\nEND\n");
			}
			if (paragraph.hasSounds()) {
				//set music
				buffer.append("if ISPLAY($");
				buffer.append(getID(VAR_SOUND));
				buffer.append(") : CLOSE($");
				buffer.append(getID(VAR_SOUND));
				buffer.append(")\n$");
				buffer.append(getID(VAR_SOUND));
				buffer.append("='");
				buffer.append(paragraph.getNextSound().getUrl());
				buffer.append("'\nPLAY $");
				buffer.append(getID(VAR_SOUND));
				buffer.append('\n');
			} else {
				buffer.append("if ISPLAY($");
				buffer.append(getID(VAR_SOUND));
				buffer.append(") : CLOSE($");
				buffer.append(getID(VAR_SOUND));
				buffer.append(")&");
				buffer.append(getID(VAR_SOUND));
				buffer.append("=''\n");
			}
		}
		if (paragraph.getBattle() != null) {
			//battle
			buffer.append("NOSAVE=1\nif ");
			buffer.append(getID(VAR_CURRENT_PARAGRAPH));
			buffer.append("<>");
			buffer.append(number);
			buffer.append(" :\n");
			
			buffer.append(getID(VAR_ALCHEMY_USED_IN_BATTLE_ROUND));
			buffer.append("=0\n");
			buffer.append(getID(VAR_BATTLE_MODE));
			buffer.append('=');
			buffer.append(paragraph.getEnemies().size());
			buffer.append('\n');
			buffer.append(getID(VAR_HERO_ATTACK_SELECTION));
			buffer.append("=-1\n");
			//add enemies
			int counter = 0;
			for (NPCParams npcParams : paragraph.getEnemies()) {
				NPC npc = npcParams.getNpc();
				//type of enemy
				buffer.append(getID(VAR_ENEMY_PREFIX,counter));
				buffer.append('=');
				buffer.append(npc.getId());
				buffer.append('\n');
				for (Parameter parameter : npc.getValues().keySet()) {
					int val = npc.getValues().get(parameter);
					if (val==0) {
						continue;
					}
					if (parameter.isVital() || paragraph.getBattle().dependsOn(parameter)) {
						buffer.append(getID(VAR_ENEMY_PREFIX,counter,VAR_PARAMETER_PREFIX,parameter.getId()));
						buffer.append('=');
						buffer.append(val);
						buffer.append('\n');
					}
				}
				counter++;
			}
			
			//remember parameters for limits of alchemy
			for (Parameter parameter : battleAlchemyLimits) {
				buffer.append(getID(VAR_TEMP_PARAMETER_PREFIX,parameter.getId()));
				buffer.append('=');
				buffer.append(getID(VAR_PARAMETER_PREFIX,parameter.getId()));
				buffer.append('\n');
			}
			buffer.append("GS '");
			buffer.append(getID(PROC_SELECT_TARGET_AUTO));
			buffer.append("'\nEND\n");
			
			//battle rounds
			counter = 0;
			for (NPCParams npcParams : paragraph.getEnemies()) {
				NPC npc = npcParams.getNpc();
				//Hero attacks Enemies
				buffer.append("if ");
				buffer.append(getID(VAR_HERO_ATTACK_SELECTION));
				buffer.append('=');
				buffer.append(counter);
				buffer.append(" and ");
				buffer.append(getID(VAR_BATTLE_MODE));
				buffer.append(">0 : GS '");
				buffer.append(getProcBattleCaclulation(initBuffer,paragraph.getBattle(),counter,true,false));
				buffer.append("'\n");
				
				//Enemies attack Hero
				buffer.append(getID(VAR_CURRENT_ENEMY_TYPE));
				buffer.append("=0\nif ");
				buffer.append(getID(VAR_HERO_ATTACK_SELECTION));
				buffer.append(">=0 and ");
				buffer.append(getID(VAR_BATTLE_MODE));
				buffer.append(">0 : ");
				buffer.append(getID(VAR_CURRENT_ENEMY_TYPE));
				buffer.append('=');
				buffer.append(npc.getId());
				buffer.append("&GS '");
				buffer.append(getID(PROC_CHECK_ENEMY_ALIVE,counter));
				//is alive?
				buffer.append("'\nif ");
				buffer.append(getID(VAR_CURRENT_ENEMY_TYPE));
				buffer.append(">0 : GS '");
				buffer.append(getProcBattleCaclulation(initBuffer,paragraph.getBattle(),counter, false,false));
				buffer.append("'\n");
				counter++;
			}
			
		} else {
			//peace
			buffer.append(getID(VAR_BATTLE_MODE));
			buffer.append("=0&NOSAVE=0\n");
		}
		
		//select any connection by default
		//no preselected connection
		buffer.append(getID(VAR_CAN_GO));
		buffer.append("=0\n");
		if (alchemyPresent) {
			buffer.append("GS '");
			buffer.append(getID(PROC_ENABLE_ALCEMY));
			buffer.append("'\n");
			if (paragraph.getAlchemy() != null) {
				for (Alchemy alchemy : paragraph.getAlchemy().keySet()) {
					boolean value = paragraph.getAlchemy().get(alchemy);
					if (value==false) {
						//disable alchemy
						buffer.append(getID(VAR_ALCHEMY_DISABLED_PREFIX,alchemy.getId()));
						buffer.append("=1\n");
					}
				}
			}
		}
		
		StringBuilder builder = new StringBuilder();
		
		if (paragraph.getChangeParameters() != null && paragraph.getChangeParameters().size()>0) {
			for (Parameter parameter : paragraph.getChangeParameters().keySet()) {
				ParametersCalculation calculation = paragraph.getChangeParameters().get(parameter);
				addCalculationProc(builder,calculation,VAR_PARAMETER_PREFIX);
				if (model.getSettings().isOverflowControl() ^ calculation.isOverflowControl()) {
					//control limits
					if (parameter.getLimit() != null) {
						builder.append("if ");
						builder.append(getID(VAR_PARAMETER_PREFIX,parameter.getLimit().getId()));
						builder.append('<');
						builder.append(getID(VAR_CALCULATION));
						builder.append(" : ");
						builder.append(getID(VAR_CALCULATION));
						builder.append('=');
						builder.append(getID(VAR_PARAMETER_PREFIX,parameter.getLimit().getId()));
						builder.append('\n');
					}
				}
				builder.append(getID(VAR_PARAMETER_PREFIX,parameter.getId()));
				builder.append('=');
				builder.append(getID(VAR_CALCULATION));
				builder.append('\n');
				if (parameter.isNegative()==false) {
					builder.append("if ");
					builder.append(getID(VAR_PARAMETER_PREFIX,parameter.getId()));
					builder.append("<=0 : ");
					builder.append(getID(VAR_PARAMETER_PREFIX,parameter.getId()));
					builder.append("=0\n");
				}
			}
		}
		if (paragraph.getChangeModificators() != null) {
			for (Modificator modificator : paragraph.getChangeModificators().keySet()) {
				boolean value = paragraph.getChangeModificators().get(modificator);
				builder.append(getID(VAR_PARAMETER_PREFIX,modificator.getId()));
				builder.append('=');
				if (value) {
					builder.append(VALUE_MODIFICATOR_SET);
				} else {
					builder.append(VALUE_MODIFICATOR_NOT_SET);
				}
				builder.append('\n');
			}
		}
		if (paragraph==model.getStartParagraph()) {
			for (ObjectBean obj : model.getObjects()) {
				String name = objects.get(obj);
				builder.append(getID(VAR_OBJECTS));
				builder.append("['");
				builder.append(name);
				builder.append("']=0\n");
			}
		}
		if (paragraph.getGotObjects().isEmpty()==false) {
			for (ObjectBean bean : paragraph.getGotObjects()) {
				String name= objects.get(bean);
				if (paragraph!=model.getStartParagraph()) {
					builder.append("if ");
					builder.append(getID(VAR_OBJECTS));
					builder.append("['");
					builder.append(name);
					builder.append("']=0 : ");
				}
				builder.append("ADDOBJ '");
				builder.append(name);
				builder.append("'&");
				builder.append(getID(VAR_OBJECTS));
				builder.append("['");
				builder.append(name);
				builder.append("']=1\n");
			}
		}
		if (paragraph.getLostObjects().isEmpty()==false) {
			for (ObjectBean bean : paragraph.getLostObjects()) {
				String name= objects.get(bean);
				builder.append("DELOBJ '");
				builder.append(name);
				builder.append("'\n");
				builder.append(getID(VAR_OBJECTS));
				builder.append("['");
				builder.append(name);
				builder.append("']=0\n");
			}
		}
		
		if (builder.length()>0) {
			buffer.append("if ");
			buffer.append(getID(VAR_CURRENT_PARAGRAPH));
			buffer.append("<>");
			buffer.append(number);
			buffer.append(" :\n");
			buffer.append(builder.toString());
			buffer.append("END\n");
		}
		
		//add image
		if (model.getSettings().isDisableImages()==false && paragraph.hasTopImages()) {
			//show images
			buffer.append("if ");
			buffer.append(getID(VAR_MUST_GO));
			buffer.append("=0 : *P '<center>");
			buffer.append("<img src=\""); 
			buffer.append(paragraph.getNextTopImage().getUrl());
			buffer.append("\">");
			if (paragraph.hasSprites()) {
				List<Sprite> list = paragraph.getSprites();
				for (Sprite sprite : list) {
					buffer.append("<img style=\"position:absolute;left:");
					buffer.append(sprite.getX());
					buffer.append(";top:");
					buffer.append(sprite.getY());
					buffer.append("\" src=\"");
					buffer.append(sprite.getPicture().getUrl());
					buffer.append("\">");
				}
			} 
			buffer.append("</center>'\n");
		}
		//add text of paragraph
		println(text);
		//add image
		if (model.getSettings().isDisableImages()==false && paragraph.hasBottomImages()) {
			//show images
			buffer.append("if ");
			buffer.append(getID(VAR_MUST_GO));
			buffer.append("=0 : *P '<center><img src=\""); 
			buffer.append(paragraph.getNextBottomImage().getUrl());
			buffer.append("\"></center>'\n");
		}
		
//		if (model.getSettings().isDisableImages()==false && paragraph.getTopImages().isEmpty()==false) {
//			buffer.append("if ");
//			buffer.append(getID(VAR_MUST_GO));
//			buffer.append("=0 : *P '</td></tr></table>'\n");
//		}
		
		if (paragraph.getBattle() != null) {
			//battle name
			buffer.append("if ");
			buffer.append(getID(VAR_BATTLE_MODE));
			buffer.append(">0 : *NL&");
			println(paragraph.getBattle().getName());
		}
		if (showParams==false && hasParameters) {
			//show parameters for second entry
			buffer.append("if ");
			buffer.append(getID(VAR_CURRENT_PARAGRAPH));
			buffer.append('=');
			buffer.append(number);
			buffer.append(" : GS '");
			buffer.append(getID(PROC_SHOW_PARAMS));
			buffer.append("'\n");
		}
		if (paragraph.getBattle() != null) {
			//hero round calculation
			buffer.append("if ");
			buffer.append(getID(VAR_HERO_ATTACK_SELECTION));
			buffer.append(">=0 : *P '");
			buffer.append(appConstants.qspHeroAttack());
			buffer.append("<<");
			buffer.append(getID(VAR_ATTACK_VALUE));
			buffer.append(">>");
			if (paragraph.getBattle().isAttackDefense()) {
				buffer.append(' ');
				buffer.append(appConstants.qspHeroDefense());
				buffer.append("<<");
				buffer.append(getID(VAR_DEFENSE_VALUE));
				buffer.append(">>");
			}
			buffer.append("'\n");
			int counter = 0;
			for (@SuppressWarnings("unused") NPCParams npcParams : paragraph.getEnemies()) {
				NPC npc = npcParams.getNpc();
				buffer.append("if ");
				buffer.append(getID(VAR_HERO_ATTACK_SELECTION));
				buffer.append('=');
				buffer.append(counter);
				buffer.append(" : GS '");
				buffer.append(getProcBattleCaclulation(initBuffer,paragraph.getBattle(),counter, true,true));
				buffer.append("'\n");
//				addBattleCalculation(buffer, paragraph.getBattle(),counter, true,true);
				counter++;
			}
//			//end of line for hero params
//			buffer.append("if ");
//			buffer.append(getID(VAR_CURRENT_PARAGRAPH));
//			buffer.append('=');
//			buffer.append(number);
//			buffer.append(" : *PL ' '\n");
			
			counter = 0;
			for (NPCParams npcParams : paragraph.getEnemies()) {
				NPC npc = npcParams.getNpc();
				buffer.append(getID(VAR_CURRENT_ENEMY_TYPE));
				buffer.append("=0\nif ");
				buffer.append(getID(VAR_BATTLE_MODE));
				buffer.append(">0 : ");
				buffer.append(getID(VAR_CURRENT_ENEMY_TYPE));
				buffer.append('=');
				buffer.append(npc.getId());
				buffer.append("&GS '");
				buffer.append(getID(PROC_CHECK_ENEMY_ALIVE,counter));
				buffer.append("'\nif ");
				buffer.append(getID(VAR_CURRENT_ENEMY_TYPE));
				buffer.append(">0 :\n*NL&GS '");
				//alive!!!
				buffer.append(getID(PROC_SHOW_ENEMY_SELECTION,counter));
				buffer.append("'\nGS '");
				buffer.append(getID(PROC_SHOW_ENEMY_NAME));
				buffer.append("'\nGS '");
				buffer.append(getID(PROC_SHOW_ENEMY_PARAMS,counter));
				buffer.append("'\nif ");
				buffer.append(getID(VAR_HERO_ATTACK_SELECTION));
				buffer.append(">=0 : *P '");
				//print attack/defense parameters
				buffer.append(appConstants.urqHeroAttack());
				buffer.append("<<");
				buffer.append(getID(VAR_ENEMY_PREFIX,counter,VAR_ATTACK_VALUE));
				buffer.append(">> ");
				if (paragraph.getBattle().isAttackDefense()) {
					buffer.append(appConstants.urqHeroDefense());
					buffer.append("<<");
					buffer.append(getID(VAR_ENEMY_PREFIX,counter,VAR_DEFENSE_VALUE));
					buffer.append(">> ");
				}
				buffer.append("'\nif ");
				buffer.append(getID(VAR_HERO_ATTACK_SELECTION));
				buffer.append(">=0 : GS '");
				buffer.append(getProcBattleCaclulation(initBuffer,paragraph.getBattle(),counter, false,true));
				buffer.append("'\nEND\nif ");
				buffer.append(getID(VAR_CURRENT_ENEMY_TYPE));
				buffer.append(">0 and ");
				buffer.append(getID(VAR_HERO_ATTACK_SELECTION));
				buffer.append(">=0 : *NL\n");
				
//				addBattleCalculation(buffer, paragraph.getBattle(),counter, false,true);
				counter++;
			}
			//clear selected target
			buffer.append(getID(VAR_HERO_ATTACK_SELECTION));
			buffer.append("=-1\n");
			//calculate Attack-Defense-Damage for next round
			addCalculationProc(buffer, paragraph.getBattle().getAttack(), VAR_PARAMETER_PREFIX);
			buffer.append('\n');
			buffer.append(getID(VAR_ATTACK_VALUE));
			buffer.append('=');
			buffer.append(getID(VAR_CALCULATION));
			buffer.append('\n');
			if (paragraph.getBattle().getFatal() != Battle.FATAL_NONE) {
				//fatal strike
				buffer.append(getID(VAR_FATAL));
				buffer.append("=0\nif ");
				buffer.append(getID(VAR_DICE_MAX));
				buffer.append("=1 : ");
				buffer.append(getID(VAR_FATAL));
				buffer.append("=1\n");
			}
			if (paragraph.getBattle().isAttackDefense()) {
				addCalculationProc(buffer, paragraph.getBattle().getDefense(), VAR_PARAMETER_PREFIX);
				buffer.append('\n');
				buffer.append(getID(VAR_DEFENSE_VALUE));
				buffer.append('=');
				buffer.append(getID(VAR_CALCULATION));
				buffer.append('\n');
			}
			if (paragraph.getBattle().isDifferenceIsDamage()==false) {
				addCalculationProc(buffer, paragraph.getBattle().getDamage(), VAR_PARAMETER_PREFIX);
				buffer.append('\n');
				buffer.append(getID(VAR_DAMAGE_VALUE));
				buffer.append('=');
				buffer.append(getID(VAR_CALCULATION));
				buffer.append('\n');
			}
		}
		
		buffer.append(getID(VAR_CURRENT_PARAGRAPH));
		buffer.append('=');
		buffer.append(number);
		buffer.append('\n');
		
		//check vital parameters
		if (vitals.isEmpty()==false) {
			buffer.append("GS '");
			buffer.append(getID(PROC_VITAL));
			buffer.append("'\n");
		}
		
		//add connections
		ArrayList<ParagraphAction> list = paragraphActions.get(paragraph);
		
		if (list == null || list.isEmpty()) {
			//final paragraph
			if (paragraph.isSuccess()) {
				//link to our site
			} else {
				addButton(model.getStartParagraph(),appConstants.playerTitleRestartGame());
			}
		} else {
			for (ParagraphAction action : list) {
				if (action.getConnection().getStrictness()==ParagraphConnection.STRICTNESS_MUST) {
					//must go
					switch (action.getConnection().getType()) {
					case ParagraphConnection.TYPE_NORMAL:
						addNonBattleControl(paragraph);
						if (action.getConnection().isConditional()) {
							addMustGoCondition(action.getObjectVar(),null,action.getConnection().getTo().getNumber());
						} else {
							buffer.append("cls&XGT '");
							buffer.append(getID(PARAGRAPH_PREFIX,action.getConnection().getTo().getNumber()));
							buffer.append("'\n");
						}
						break;
					case ParagraphConnection.TYPE_MODIFICATOR:
						addNonBattleControl(paragraph);
						addMustGoCondition(action.getModificatorVar(),VALUE_MODIFICATOR_SET,action.getConnection().getTo().getNumber());
						break;
					case ParagraphConnection.TYPE_NO_MODIFICATOR:
						addNonBattleControl(paragraph);
						addMustGoCondition(action.getModificatorVar(),VALUE_MODIFICATOR_NOT_SET,action.getConnection().getTo().getNumber());
						break;
					case ParagraphConnection.TYPE_PARAMETER_LESS:
						addNonBattleControl(paragraph);
						addDiceValue(buffer, action.getConnection().getParameterValue(),true);
						buffer.append('\n');
						addNonBattleControl(paragraph);
						builder = new StringBuilder(action.getParameterVar());
						builder.append('<');
						builder.append(getID(VAR_DICE));
						addMustGoCondition(builder.toString(),null,action.getConnection().getTo().getNumber());
						break;
					case ParagraphConnection.TYPE_PARAMETER_MORE:
						addNonBattleControl(paragraph);
						addDiceValue(buffer, action.getConnection().getParameterValue(),true);
						buffer.append('\n');
						addNonBattleControl(paragraph);
						StringBuilder builder2 = new StringBuilder(action.getParameterVar());
						builder2.append('>');
						builder2.append(getID(VAR_DICE));
						addMustGoCondition(builder2.toString(),null,action.getConnection().getTo().getNumber());
						break;
					case ParagraphConnection.TYPE_ENEMY_VITAL_LESS:
						buffer.append("if ");
						buffer.append(getID(VAR_CAN_GO));
						buffer.append("=0 and ");
						buffer.append(getID(VAR_BATTLE_MODE));
						buffer.append(">0 : ");
						addDiceValue(buffer, action.getConnection().getParameterValue(),true);
						buffer.append("\nif ");
						buffer.append(getID(VAR_CAN_GO));
						buffer.append("=0 and ");
						buffer.append(getID(VAR_BATTLE_MODE));
						buffer.append(">0 : ");
						buffer.append(getID(VAR_ENEMY_VITAL));
						buffer.append('=');
						buffer.append(getID(VAR_DICE));
						buffer.append("&if ");
						int counter=0;
						for (@SuppressWarnings("unused") NPCParams npcParams : paragraph.getEnemies()) {
							NPC npc = npcParams.getNpc();
							if (counter>0) {
								buffer.append(" or ");
							}
							buffer.append(getID(VAR_ENEMY_PREFIX,counter,VAR_PARAMETER_PREFIX,paragraph.getBattle().getVital().getId()));
							buffer.append('<');
							buffer.append(getID(VAR_ENEMY_VITAL));
							counter++;
						}
						buffer.append(" : ");
						if (model.getSettings().isSkipMustGoParagraphs()) {
							//go immediately
							buffer.append("cls&XGT '");
							buffer.append(getID(PARAGRAPH_PREFIX,action.getConnection().getTo().getNumber()));
							buffer.append("'\n");
						} else {
							buffer.append(getID(VAR_CAN_GO));
							buffer.append('=');
							buffer.append(action.getConnection().getTo().getNumber());
							buffer.append('&');
							buffer.append(getID(VAR_MUST_GO));
							buffer.append("=1\n");
						}
						break;
					case ParagraphConnection.TYPE_VITAL_LESS:
						buffer.append("if ");
						buffer.append(getID(VAR_CAN_GO));
						buffer.append("=0 and ");
						buffer.append(getID(VAR_BATTLE_MODE));
						buffer.append(">0 : ");
						addDiceValue(buffer, action.getConnection().getParameterValue(),true);
						buffer.append("\nif ");
						buffer.append(getID(VAR_CAN_GO));
						buffer.append("=0 and ");
						buffer.append(getID(VAR_BATTLE_MODE));
						buffer.append(">0 and ");
						buffer.append(getID(VAR_PARAMETER_PREFIX,paragraph.getBattle().getVital().getId()));
						buffer.append('<');
						buffer.append(getID(VAR_DICE));
						buffer.append(" : ");
						if (model.getSettings().isSkipMustGoParagraphs()) {
							//go immediately
							buffer.append("cls&XGT '");
							buffer.append(getID(PARAGRAPH_PREFIX,action.getConnection().getTo().getNumber()));
							buffer.append("'\n");
						} else {
							buffer.append(getID(VAR_CAN_GO));
							buffer.append('=');
							buffer.append(action.getConnection().getTo().getNumber());
							buffer.append('&');
							buffer.append(getID(VAR_MUST_GO));
							buffer.append("=1\n");
						}
						break;
					}
				}
			}
			
			if (paragraph.getBattle() != null) {
				//battle actions
				int counter = 0;
				for (@SuppressWarnings("unused") NPCParams npcParams : paragraph.getEnemies()) {
					NPC npc = npcParams.getNpc();
					String prefix = new StringBuilder(VAR_ENEMY_PREFIX).append(counter).append(VAR_PARAMETER_PREFIX).toString();
					if (paragraph.getBattle().isAttackDefense()) {
						//calculate defense
						addCalculationProc(buffer, paragraph.getBattle().getDefense(), prefix);
						buffer.append('\n');
						buffer.append(getID(VAR_ENEMY_PREFIX,counter,VAR_DEFENSE_VALUE));
						buffer.append('=');
						buffer.append(getID(VAR_CALCULATION));
						buffer.append('\n');
					}
					if (paragraph.getBattle().isDifferenceIsDamage()==false) {
						addCalculationProc(buffer, paragraph.getBattle().getDamage(), prefix);
						buffer.append('\n');
						buffer.append(getID(VAR_ENEMY_PREFIX,counter,VAR_DAMAGE_VALUE));
						buffer.append('=');
						buffer.append(getID(VAR_CALCULATION));
						buffer.append('\n');
					}
					//calculate attack
					addCalculationProc(buffer, paragraph.getBattle().getAttack(), prefix);
					buffer.append('\n');
					buffer.append(getID(VAR_ENEMY_PREFIX,counter,VAR_ATTACK_VALUE));
					buffer.append('=');
					buffer.append(getID(VAR_CALCULATION));
					buffer.append('\n');
					if (paragraph.getBattle().getFatal() == Battle.FATAL_NORMAL) {
						//fatal strike for Hero
						buffer.append(getID(VAR_ENEMY_PREFIX,counter,VAR_FATAL));
						buffer.append("=0\nif ");
						buffer.append(getID(VAR_DICE_MAX));
						buffer.append("=1 : ");
						buffer.append(getID(VAR_ENEMY_PREFIX,counter,VAR_FATAL));
						buffer.append("=1");
						buffer.append('\n');
					}
					counter++;
				}
				buffer.append("if ");
				buffer.append(getID(VAR_BATTLE_MODE));
				buffer.append(">0 and ");
				buffer.append(getID(VAR_MUST_GO));
				buffer.append("=0 : ");
				addButton(buffer, appConstants.battleAttack(), getID(PROC_ATTACK));
				if (paragraph.getEnemies().size()>1) {
					//target selection
					buffer.append("if ");
					buffer.append(getID(VAR_BATTLE_MODE));
					buffer.append(">1 and ");
					buffer.append(getID(VAR_MUST_GO));
					buffer.append("=0 : ");
					addButton(buffer,appConstants.urqSelectTargetBtn(),getID(PROC_SELECT_TARGET));
				}
			}
			
			for (ParagraphAction action : list) {
				String title = action.getActionName();
				if (action.getConnection().isConditional()) {
					if (action.getConnection().getStrictness()==ParagraphConnection.STRICTNESS_MUST) {
						//must go is already checked
						addButton(action.getTo(),title,true,getID(VAR_CAN_GO),title);
					} else {
						//can or must-not 
						switch (action.getConnection().getType()) {
						case ParagraphConnection.TYPE_NORMAL:
							addNonBattleControl(paragraph);
							if (action.getConnection().getStrictness()==ParagraphConnection.STRICTNESS_MUST_NOT) {
								//must not
								addButton(action.getTo(),title,true,action.getObjectVar(),"0");
							} else {
								//can
								addButton(action.getTo(),title,true,action.getObjectVar(),null);
							}
							break;
						case ParagraphConnection.TYPE_MODIFICATOR:
							addNonBattleControl(paragraph);
							if (action.getConnection().getStrictness()==ParagraphConnection.STRICTNESS_MUST_NOT) {
								addButton(action.getTo(),title,true,action.getModificatorVar(),VALUE_MODIFICATOR_NOT_SET);
							} else {
								//can
								addButton(action.getTo(),title,true,action.getModificatorVar(),VALUE_MODIFICATOR_SET);
							}
							break;
						case ParagraphConnection.TYPE_NO_MODIFICATOR:
							addNonBattleControl(paragraph);
							if (action.getConnection().getStrictness()==ParagraphConnection.STRICTNESS_MUST_NOT) {
								
								addButton(action.getTo(),title,true,action.getModificatorVar(),VALUE_MODIFICATOR_SET);
							} else {
								//can
								addButton(action.getTo(),title,true,action.getModificatorVar(),VALUE_MODIFICATOR_NOT_SET);
							}
							break;
						case ParagraphConnection.TYPE_PARAMETER_LESS:
							addNonBattleControl(paragraph);
							addDiceValue(buffer, action.getConnection().getParameterValue(),true);
							buffer.append('\n');
							addNonBattleControl(paragraph);
							builder = new StringBuilder(action.getParameterVar());
							if (action.getConnection().getStrictness()==ParagraphConnection.STRICTNESS_MUST_NOT) {
								builder.append(">=");
							} else {
								//can
								builder.append("<");
							}
							builder.append(getID(VAR_DICE));
							addButton(action.getTo(),title,true,builder.toString(),null);
							break;
						case ParagraphConnection.TYPE_PARAMETER_MORE:
							addNonBattleControl(paragraph);
							addDiceValue(buffer, action.getConnection().getParameterValue(),true);
							buffer.append('\n');
							addNonBattleControl(paragraph);
							StringBuilder builder2 = new StringBuilder(action.getParameterVar());
							if (action.getConnection().getStrictness()==ParagraphConnection.STRICTNESS_MUST_NOT) {
								builder2.append("<=");
							} else {
								//can
								builder2.append(">");
							}
							builder2.append(getID(VAR_DICE));
							addButton(action.getTo(),title,true,builder2.toString(),null);
							break;
						case ParagraphConnection.TYPE_VITAL_LESS:
							if (action.getConnection().getStrictness()==ParagraphConnection.STRICTNESS_MUST) {
								//already checked
								addButton(action.getTo(),title,true,VAR_CAN_GO,String.valueOf(action.getTo().getNumber()));
							} else {
								buffer.append("if ");
								buffer.append(getID(VAR_MUST_GO));
								buffer.append("=0 and ");
								buffer.append(getID(VAR_BATTLE_MODE));
								buffer.append(">0 : ");
								addDiceValue(buffer, action.getConnection().getParameterValue(),true);
								buffer.append("\nif ");
								buffer.append(getID(VAR_MUST_GO));
								buffer.append("=0 and ");
								buffer.append(getID(VAR_BATTLE_MODE));
								buffer.append(">0 and ");
								buffer.append(getID(VAR_PARAMETER_PREFIX,paragraph.getBattle().getVital().getId()));
								if (action.getConnection().getStrictness()==ParagraphConnection.STRICTNESS_CAN) {
									buffer.append('<');
								} else {
									buffer.append(">=");
								}
								buffer.append(getID(VAR_DICE));
								buffer.append(" : ");
								addButton(buffer, title, getID(PARAGRAPH_PREFIX,action.getConnection().getTo().getNumber()));
							}
							break;
						case ParagraphConnection.TYPE_ENEMY_VITAL_LESS:
							if (action.getConnection().getStrictness()==ParagraphConnection.STRICTNESS_MUST) {
								//already checked
								addButton(action.getTo(),title,true,VAR_CAN_GO,String.valueOf(action.getTo().getNumber()));
							} else {
								buffer.append("if ");
								buffer.append(getID(VAR_MUST_GO));
								buffer.append("=0 and ");
								buffer.append(getID(VAR_BATTLE_MODE));
								buffer.append(">0 : ");
								addDiceValue(buffer, action.getConnection().getParameterValue(),true);
								buffer.append("\nif ");
								buffer.append(getID(VAR_MUST_GO));
								buffer.append("=0 and ");
								buffer.append(getID(VAR_BATTLE_MODE));
								buffer.append(">0 : ");
								buffer.append(getID(VAR_ENEMY_VITAL));
								buffer.append('=');
								buffer.append(getID(VAR_DICE));
								buffer.append("&if ");
								int counter=0;
								for (@SuppressWarnings("unused") NPCParams npcParams : paragraph.getEnemies()) {
									NPC npc = npcParams.getNpc();
									if (counter>0) {
										buffer.append(" or ");
									}
									buffer.append(getID(VAR_ENEMY_PREFIX,counter,VAR_PARAMETER_PREFIX,paragraph.getBattle().getVital().getId()));
									if (action.getConnection().getStrictness()==ParagraphConnection.STRICTNESS_CAN) {
										buffer.append('<');
									} else {
										buffer.append(">=");
									}
									buffer.append(getID(VAR_ENEMY_VITAL));
									counter++;
								}
								buffer.append(" : ");
								addButton(buffer, title, getID(PARAGRAPH_PREFIX,action.getConnection().getTo().getNumber()));
							}
							break;
						}
					}
				} else {
					addNonBattleControl(paragraph);
					addButton(action.getTo(),title,true);
				}
			}
		}
		if (paragraph.getAlchemy() != null) {
			for (Alchemy alchemy : paragraph.getAlchemy().keySet()) {
				if (paragraph.getAlchemy().get(alchemy)) {
					//enable this alchemy
					if (alchemy.getPlace() != Alchemy.PLACE_PEACE && paragraph.getBattle() != null) {
						//can be used in the battle
						addAlchemy(buffer, alchemy,true);
					}
					addAlchemy(buffer, alchemy,false);
				}
			}
		}
		if (alchemyBattlePresent && paragraph.getBattle() != null ) {
			//add battle alchemy
			buffer.append("if ");
			buffer.append(getID(VAR_BATTLE_MODE));
			buffer.append(">0 : GS '");
			buffer.append(getID(PROC_ADD_BATTLE_ALCHEMY));
			buffer.append("'\n");
		}
		if (alchemyPeacePresent) {
			//add peace alchemy
			if (paragraph.getBattle() != null) {
				buffer.append("if ");
				buffer.append(getID(VAR_BATTLE_MODE));
				buffer.append("=0 : ");
			}
			buffer.append("GS '");
			buffer.append(getID(PROC_ADD_PEACE_ALCHEMY));
			buffer.append("'\n");
		}
		if (showParams && hasParameters) {
			buffer.append("GS '");
			buffer.append(getID(PROC_SHOW_PARAMS));
			buffer.append("'\n");
		}
		buffer.append("-\n\n");
		if (initBuffer.length()>0) {
			buffer.append(initBuffer.toString());
		}
	}

	private String getProcBattleCaclulation(StringBuilder builder, Battle battle, int counter, boolean hero, boolean print) {
		String procId = getID(PROC_BATTLE,battle.getId(),'_',counter,hero ? 't':'f',print ? 't':'f');
		if (battleSet.contains(procId)==false) {
			battleSet.add(procId);
			builder.append('#');
			builder.append(procId);
			builder.append('\n');
			addBattleCalculation(builder, battle,counter, hero,print);
			builder.append("-\n");
		}
		return procId;
	}

	private void addNonBattleControl(Paragraph paragraph) {
		if (paragraph.getBattle() != null) {
			buffer.append("if ");
			buffer.append(getID(VAR_BATTLE_MODE));
			buffer.append("=0 : ");
		}
	}

	private void addCalculationProc(StringBuilder builder, ParametersCalculation calculation, String prefix) {
		boolean next;
		if (calculation.getConstant().isZero()) {
			builder.append(getID(VAR_CALCULATION));
			builder.append('=');
			next=false;
		} else {
			addDiceValue(builder,calculation.getConstant(),false);
			builder.append(getID(VAR_CALCULATION));
			builder.append('=');
			builder.append(getID(VAR_DICE));
			next=true;
		}
		if (calculation.getParameters().isEmpty()==false) {
			for (Parameter parameter : calculation.getParameters().keySet()) {
				int value = calculation.getParameters().get(parameter);
				if (value==1) {
					if (next) {
						builder.append('+');
					}
				} else if (value==-1){
					builder.append('-');
				} else if (value>0) {
					if (next) {
						builder.append('+');
					}
					builder.append(value);
					builder.append('*');
					
				} else {
					builder.append('-');
					builder.append(value);
					builder.append('*');
					
				}
				builder.append(getID(prefix,parameter.getId()));
				next = true;
			}
		}
		builder.append('\n');
	}
	
	private void addDiceValue(StringBuilder builder, DiceValue value,boolean inLine) {
		builder.append(getID(VAR_DICE));
		builder.append('=');
		if (value.getConstant() == 0) {
			builder.append('0');
		} else {
			builder.append(value.getConstant());
		}
		if (value.isNoDice()==false) {
			if (inLine) {
				builder.append('&');
			} else {
				builder.append('\n');
			}
			builder.append("GS '");
			builder.append(getDiceValueProc(value));
			builder.append('\'');
		}
		if (inLine==false) {
			builder.append('\n');
		}
	}

	private String getDiceValueProc(DiceValue value) {
		String key = value.getDiceStr();
		String proc = diceProces.get(key);
		if (proc!=null) {
			return proc;
		}
		proc = getID("dice"+String.valueOf(diceProces.size()));
		diceProces.put(key, proc);
		diceBuffer.append('#').append(proc).append('\n');
		diceBuffer.append(getID(VAR_DICE_MAX)).append("=0\n");
		int max = Math.abs(value.getN());
		diceBuffer.append(getID(VAR_DICE));
		diceBuffer.append('=');
		diceBuffer.append(getID(VAR_DICE));
		if (value.getN()<0) {
			diceBuffer.append('-');
		} else {
			diceBuffer.append('+');
		}
		diceBuffer.append(max);
		diceBuffer.append('\n');
		//dice + constant
		for (int i = 0; i < max; i++) {
			diceBuffer.append(getID(VAR_DICE_TEMP));
			diceBuffer.append('=');
			diceBuffer.append("RAND(1,"); 
			diceBuffer.append(value.getSize());
			diceBuffer.append(")\n"); 
			for (int j = 1; j < value.getSize(); j++) {
				diceBuffer.append("if "); 
				diceBuffer.append(getID(VAR_DICE_TEMP));
				diceBuffer.append('>'); 
				diceBuffer.append(j);
				diceBuffer.append(" : "); 
				diceBuffer.append(getID(VAR_DICE));
				diceBuffer.append('=');
				diceBuffer.append(getID(VAR_DICE));
				if (value.getN()>0) {
					diceBuffer.append('+');
				} else {
					diceBuffer.append('-');
				}
				diceBuffer.append("1\n"); 
			}
		}
		diceBuffer.append("if "); 
		diceBuffer.append(getID(VAR_DICE));
		diceBuffer.append('='); 
		diceBuffer.append(value.getSize()*max); 
		diceBuffer.append(" : "); 
		diceBuffer.append(getID(VAR_DICE_MAX));
		diceBuffer.append("=1\n-\n");
		return proc;
	}
	
	private void addMustGoCondition(String variable, String value, int go) {
		buffer.append("if ");
		buffer.append(getID(VAR_MUST_GO));
		buffer.append("=0 and ");
		buffer.append(variable);
		if (value != null) {
			buffer.append('=');
			buffer.append(value);
		}
		buffer.append(" : ");
		if (model.getSettings().isSkipMustGoParagraphs()) {
			buffer.append("cls&XGT '");
			buffer.append(getID(PARAGRAPH_PREFIX,go));
			buffer.append('\'');
		} else {
			buffer.append(getID(VAR_MUST_GO));
			buffer.append("=1&");
			buffer.append(getID(VAR_CAN_GO));
			buffer.append('=');
			buffer.append(go);
		}
		buffer.append('\n');
	}
	
	private void addButton(Paragraph paragraph,String title) {
		addButton(paragraph,title,false,null,null);
	}
	private void addButton(Paragraph paragraph,String title,boolean checkMustGo) {
		addButton(paragraph,title,checkMustGo,null,null);
	}
	
	private void addButton(Paragraph paragraph,String title,boolean checkMustGo,String variable,String value) {
		if (checkMustGo) {
			buffer.append("if ");
			if (getID(VAR_CAN_GO).equals(variable)) {
				buffer.append(getID(VAR_CAN_GO));
				buffer.append('=');
				buffer.append(paragraph.getNumber());
			} else {
				buffer.append(getID(VAR_MUST_GO));
				buffer.append("=0");
				if (variable != null) {
					buffer.append(" and ");
					buffer.append(variable);
					if (value != null) {
						buffer.append('=');
						buffer.append(value);
					}
					buffer.append(" or ");
					buffer.append(getID(VAR_CAN_GO));
					buffer.append('=');
					buffer.append(paragraph.getNumber());
				}
			}
			buffer.append(" : ");
		}
		addButton(buffer, title, getID(PARAGRAPH_PREFIX,paragraph.getNumber()));
	}
	public String decorateNumber(int number,Paragraph from,Paragraph to, ParagraphConnection connection) {
		ArrayList<ParagraphAction> list = paragraphActions.get(from);
		if (list==null) {
			list = new ArrayList<ParagraphAction>();
			paragraphActions.put(from, list);
		}
		ParagraphAction action = new ParagraphAction(from,to,connection,list.size()+1);
		list.add(action);
		return appMessages.urqButtonAction(action.getId());
	}

	public void setStartParagraph(Paragraph paragraph) {
		//already set
	}

	public void endBook() {
		//add dice calculation procedures
		buffer.append(diceBuffer.toString());
	}

	public void startBook() {
	}

	
	public byte[] toBytes() {
		try {
			return buffer.toString().replace("\r\n", "\n").replace("\n", "\r\n").getBytes(ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public void addGreeting(Greeting greeting) {
		buffer.append("*NL\n");
		println(greeting.getName());
		if (greeting.getUrl() != null && greeting.getUrl().length()>0) {
			println(greeting.getUrl());
		}
		if (greeting.getText() != null && greeting.getText().length()>0) {
			println(greeting.getText());
		}
	}

	public void endGreeting() {
		buffer.append("-\n");
	}

	public void startGreeting() {
		buffer.append('#');
		buffer.append(getID(PROC_GREETINGS));
		buffer.append('\n');
		println(appConstants.urqGreetings());
	}
	
	public boolean isPlayerMode() {
		return true;
	}
	
	public boolean isHideAbsoluteModificators() {
		return false;
	}
	
}
