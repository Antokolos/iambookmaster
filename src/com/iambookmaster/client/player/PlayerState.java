package com.iambookmaster.client.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.Battle.BattleRound;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.NPC;
import com.iambookmaster.client.beans.NPCParams;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.beans.ParametersCalculation;
import com.iambookmaster.client.beans.Picture;
import com.iambookmaster.client.beans.Sound;
import com.iambookmaster.client.common.Base64Coder;
import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.exceptions.JSONException;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.Model.FullParagraphDescriptonBuilder;
import com.iambookmaster.client.model.ParagraphParsingHandler;

public class PlayerState {

	private static final int EVENT_ADD_OBJECT = 0;
	private static final int EVENT_REMOVE_OBJECT = 1;
	private static final int EVENT_USE_OBJECT = 2;
	private static final int EVENT_RESET = 3;
	private static final int EVENT_FINISH = 4;
	private static final int EVENT_LOST_OBJECT = 5;
	private static final int EVENT_CHANGE_PARAMETER = 6;
	private static final int EVENT_CHANGE_MODIFICATOR = 7;
	private static final int EVENT_BATTLE = 8;
	private static final int EVENT_ENEMY = 9;
	private static final int EVENT_ENABLE = 10;
	private static final int EVENT_DISABLE = 11;
	
	private static final String FIELD_PARAGRAPH_ID = "a";
	private static final String FIELD_OBJECT_ID = "b";
	private static final String FIELD_BAG = "c";
	private static final String FIELD_GAME_ID = "d";
	private static final String FIELD_AUDIO = "e";
	private static final String FIELD_IMAGES = "f";
	private static final String FIELD_BACKGROUND = "i";
	private static final String FIELD_BACKGROUND_SOUND = "k";
	private static final String FIELD_PARAMETERS = "l";
	private static final String FIELD_PARAMETER_ID = "m";
	private static final String FIELD_PARAMETER_VALUE = "n";
	private static final String FIELD_MODIFICATORS = "o";
	private static final String FIELD_FIGHTERS = "p";
	private static final String FIELD_ALCEMY_WAS_USED = "r";
	private static final String FIELD_BATTLE_LIMITS = "s";
	private static final String FIELD_OLD_STATE = "t";
	private static final String FIELD_NPC = "u";
	private static final String FIELD_BATTLE_ROUND = "x";
	private static final String FIELD_METADATA = "j";
	
	private LinkedHashSet<ObjectBean> bag;
	private FullParagraphDescriptonBuilder paragraphDescriptonBuilder;
	private ArrayList<PlayerStateListener> listeners;
	private Paragraph currentParagraph;
	private Model model;
	private boolean allowAudio;
	private boolean allowImages;
	private Picture background;
	private Sound backgroundSound;
	private LinkedHashMap<Parameter, Integer> parameters;
	private HashSet<Modificator> modificators;
	private boolean finished;
	private AppConstants appConstants;
	private AppMessages appMessages;
	private ArrayList<FighterData> fighters;
	private LinkedHashMap<Parameter, Integer> heroBatleLimits;
	private boolean alchemyWasUsed;
	private HashMap<ParagraphConnection,Boolean> vitalConnections;
	private HashMap<ParagraphConnection,Integer> escapeBattleConnections;
	private Battle currentBattle;
	private int currentBattleRound;
	private FighterData currentBattleTarget;
	private PlayerStateMetadata metadata;
	private final String[] history = new String[5];
	
	public PlayerStateMetadata getMetadata() {
		if (metadata==null) {
			metadata = new PlayerStateMetadata();
		}
		return metadata;
	}

	public void setMetadata(PlayerStateMetadata metadata) {
		this.metadata = metadata;
	}

	public PlayerState(Model mod,AppConstants constants,AppMessages messages) {
		model = mod;
		this.appConstants = constants;
		this.appMessages = messages;
		listeners = new ArrayList<PlayerStateListener>();
		bag=new LinkedHashSet<ObjectBean>();
		paragraphDescriptonBuilder = model.getFullParagraphDescriptonBuilder();
		paragraphDescriptonBuilder.setObjects(bag);
		paragraphDescriptonBuilder.setPlayerMode(true);
		parameters = new LinkedHashMap<Parameter, Integer>();
		modificators = new HashSet<Modificator>();
		fighters = new ArrayList<PlayerState.FighterData>();
		heroBatleLimits = new LinkedHashMap<Parameter, Integer>();
		vitalConnections = new HashMap<ParagraphConnection,Boolean>();
		escapeBattleConnections = new HashMap<ParagraphConnection, Integer>();
	}
	
	public void reset() {
		for (int i = 0; i < history.length; i++) {
			history[i] = null;
		}
		bag.clear();
		parameters.clear();
		modificators.clear();
		alchemyWasUsed = false;
		ArrayList<AbstractParameter> list = model.getParameters();
		//initialize all pre-defined parameters
		for (AbstractParameter abstractParameter : list) {
			if (abstractParameter instanceof Parameter) {
				Parameter parameter = (Parameter) abstractParameter;
				if (parameter.isHeroHasInitialValue()) {
					//has initial value
					parameters.put(parameter, parameter.getHeroInitialValue().calculate());
				}
			}
		}
		
		//initialize all parameters with limits
		for (AbstractParameter abstractParameter : list) {
			if (abstractParameter instanceof Parameter) {
				Parameter parameter = (Parameter) abstractParameter;
				if (parameter.getLimit() != null && parameters.containsKey(parameter)==false && parameters.containsKey(parameter.getLimit())) {
					//limit is initialized
					parameters.put(parameter, parameters.get(parameter.getLimit()));
				}
			}
		}
		
		//check that limits are after his parameters
		HashMap<Parameter,Integer> limits = null;
		HashSet<Parameter> used = new HashSet<Parameter>();
		for (Parameter parameter : parameters.keySet()) {
			if (parameter.getLimit() != null && used.contains(parameter.getLimit())) {
				//limit before main parameter
				if (limits == null) {
					limits = new HashMap<Parameter,Integer>();
				}
				limits.put(parameter.getLimit(),parameters.get(parameter.getLimit()));
			}
			used.add(parameter);
		}
		if (limits != null) {
			//reorder this parameters, remove first
			for (Parameter parameter : limits.keySet()) {
				parameters.remove(parameter);
			}
			//and add again to the tail
			for (Parameter parameter : limits.keySet()) {
				parameters.put(parameter, limits.get(parameter));
			}
			limits.clear();
		}
		
		//check that all limits are initialized
		for (Parameter parameter : parameters.keySet()) {
			if (parameter.getLimit() != null && parameters.containsKey(parameter.getLimit())==false) {
				//initialize limit for this parameter
				if (limits==null) {
					limits = new HashMap<Parameter, Integer>();
				}
				limits.put(parameter.getLimit(), parameters.get(parameter));
			}
		}
		if (limits != null && limits.size()>0) {
			for (Parameter parameter : limits.keySet()) {
				parameters.put(parameter, limits.get(parameter));
			}
		}
		background = null;
		fireEvent(EVENT_RESET);
		currentParagraph = model.getStartParagraph();
		finished = false;
	}
	
	public void setCurrentParagraph(Paragraph currentParagraph) {
		this.currentParagraph = currentParagraph;
	}

	public void apply(Paragraph paragraph) {
		alchemyWasUsed = false;
		currentParagraph = paragraph;
		for (ObjectBean bean : paragraph.getGotObjects()) {
			if (bag.contains(bean)==false) {
				bag.add(bean);
				fireEvent(EVENT_ADD_OBJECT,bean);
			}
		}
		for (ObjectBean bean : paragraph.getLostObjects()) {
			if (bag.contains(bean)) {
				bag.remove(bean);
				fireEvent(EVENT_LOST_OBJECT,bean);
			}
		}
		//update parameters
		LinkedHashMap<Parameter, ParametersCalculation> changes = paragraph.getChangeParameters();
		if (changes != null && changes.size()>0) {
			for (Parameter parameter : changes.keySet()) {
				ParametersCalculation calculation = changes.get(parameter);
				int value = calculateNewValue(calculation,parameter);
				fireEvent(EVENT_CHANGE_PARAMETER, parameter, value);
				if (parameter.isVital() && value<=0) {
					//death
					fireEvent(EVENT_FINISH);
				}				
			}
		}
		//update modificators
		LinkedHashMap<Modificator, Boolean> mods = paragraph.getChangeModificators();
		if (mods != null && mods.size()>0) {
			for (Modificator modificator : mods.keySet()) {
				boolean value = mods.get(modificator);
				if (value) {
					modificators.add(modificator);
				} else {
					modificators.remove(modificator);
				}
				fireEvent(EVENT_CHANGE_MODIFICATOR, modificator, value ? 1:0);
			}
		}
		
		if (paragraph.isFail() || paragraph.isSuccess()) {
			finished = true;
			fireEvent(EVENT_FINISH);
		}

		//battle
		if (currentBattle !=null) {
			fighters.clear();
			heroBatleLimits.clear();
			vitalConnections.clear();
			escapeBattleConnections.clear();
		}
		currentBattleTarget = null;
		if (paragraph.getBattle() == null) {
			currentBattle = null;
			currentBattleRound=0;
		} else {
			currentBattleRound=1;
			currentBattle = paragraph.getBattle();
			//start battle
			for ( NPCParams npc : paragraph.getEnemies()) {
				fighters.add(new FighterData(npc.getValues(), paragraph.getBattle(), npc));
			}
			selectTarget();
			updateBattleConnections(paragraph);

			
			//save limits
			ArrayList<AbstractParameter> listAlchemy = model.getParameters();
			for (AbstractParameter parameter : listAlchemy) {
				if (parameter instanceof Alchemy) {
					Alchemy alchemy = (Alchemy) parameter;
					if (alchemy.getBattleLimit() == null || heroBatleLimits.containsKey(alchemy.getBattleLimit())) {
						continue;
					}
					Integer value = parameters.get(alchemy.getBattleLimit());
					if (value != null && value>0) {
						heroBatleLimits.put(alchemy.getBattleLimit(),value);
					}
				}
			}
			
//			heroBatleLimits = new FighterData(parameters, paragraph.getBattle(), null);
			fireEvent(EVENT_BATTLE, paragraph.getBattle(), 1);
			for ( NPCParams npc : paragraph.getEnemies()) {
				fireEvent(EVENT_ENEMY, npc.getNpc(), 1);
			}
		} 
		
	}

	private int calculateNewValue(ParametersCalculation calculation,Parameter parameter) {
		boolean hasValue = parameters.containsKey(parameter);
		int value = calculation.calculate(parameters);
		if (!parameter.isNegative() && value<0) {
			//cannot be negative
			value=0;
		}
		if (model.getSettings().isOverflowControl() ^ calculation.isOverflowControl()) {
			if (parameter.getLimit() != null && parameters.containsKey(parameter.getLimit())) {
				//control limits
				int max = parameters.get(parameter.getLimit());
				int val = hasValue ? parameters.get(parameter) : 0;
				if (val>max) {
					if (value>val) {
						//it was a useless action
						value = val;
					}
				} else if (value>max) { 
					value=max;
				}
			}
		}
		if (hasValue || parameter.getLimit() == null || parameters.containsKey(parameter.getLimit())==false) {
			parameters.put(parameter, value);
		} else {
			//initial insert, has max parameter
			int max = parameters.get(parameter.getLimit());
			parameters.remove(parameter.getLimit());
			parameters.put(parameter, value);
			parameters.put(parameter.getLimit(), max);
		}
		return value;
	}

	private void updateBattleConnections(Paragraph paragraph) {
		List<ParagraphConnection> list = model.getOutputParagraphConnections(paragraph);
		vitalConnections.clear();
		escapeBattleConnections.clear();
		for (ParagraphConnection connection : list) {
			if (connection.getType()==ParagraphConnection.TYPE_VITAL_LESS || connection.getType()==ParagraphConnection.TYPE_ENEMY_VITAL_LESS) {
				vitalConnections.put(connection,Boolean.FALSE);
			}
			if (connection.getType()==ParagraphConnection.TYPE_BATTLE_ROUND_MORE) {
				escapeBattleConnections.put(connection,connection.getParameterValue().getConstant());
			}
		}	
	}

	public LinkedHashMap<Parameter, Integer> getHeroBatleLimits() {
		return heroBatleLimits;
	}

	private void fireEvent(int event, AbstractParameter parameter,int value) {
		for (int i = 0; i < listeners.size(); i++) {
			PlayerStateListener listener = listeners.get(i);
			switch (event) {
			case EVENT_CHANGE_PARAMETER:
				listener.changeParameter((Parameter)parameter,value);
				break;
			case EVENT_CHANGE_MODIFICATOR:
				listener.changeModificator((Modificator)parameter,value==1);
				break;
			case EVENT_BATTLE:
				if (value==0) {
					//end of battle - clean target
					currentBattleTarget=null;
				}
				listener.battle((Battle)parameter,value==1);
				break;
			case EVENT_ENEMY:
				listener.enemy((NPC)parameter,value==1);
				break;
			default:
				throw new IllegalArgumentException("Unsupported Enent "+event);
			} 
		}
	}

	private void fireEvent(int event, ParagraphConnection connection) {
		for (int i = 0; i < listeners.size(); i++) {
			PlayerStateListener listener = listeners.get(i);
			switch (event) {
			case EVENT_ENABLE:
				listener.enableConnection(connection);
				break;
			case EVENT_DISABLE:
				listener.disableConnection(connection);
				break;
			}
		}
	}
	
	private void fireEvent(int event) {
		finished = true;
		for (int i = 0; i < listeners.size(); i++) {
			PlayerStateListener listener = listeners.get(i);
			switch (event) {
			case EVENT_RESET:
				listener.reset();
				break;
			case EVENT_FINISH:
				listener.finish();
				break;
			}
		}
	}
	
	private void fireEvent(int event, ObjectBean object,boolean success) {
		for (int i = 0; i < listeners.size(); i++) {
			PlayerStateListener listener = listeners.get(i);
			switch (event) {
			case EVENT_USE_OBJECT:
				listener.useObject(object,success);
				break;
			default:
				throw new IllegalArgumentException("Unsupported Enent "+event);
			}
		}
	}

	private void fireEvent(int event, ObjectBean object) {
		for (int i = 0; i < listeners.size(); i++) {
			PlayerStateListener listener = listeners.get(i);
			switch (event) {
			case EVENT_ADD_OBJECT:
				listener.addObject(object);
				break;
			case EVENT_LOST_OBJECT:
				listener.lostObject(object);
				break;
			case EVENT_REMOVE_OBJECT:
				listener.removeObject(object);
				break;
			default:
				throw new IllegalArgumentException("Unsupported Enent "+event);
			}
		}
	}

	public String getFullParagraphDescripton(Paragraph paragraph, ArrayList<Paragraph> ids, ArrayList<String> errors, ParagraphParsingHandler parsingHandler) {
		paragraphDescriptonBuilder.setParagraphParsingHandler(parsingHandler);
		paragraphDescriptonBuilder.setParameters(parameters);
		paragraphDescriptonBuilder.setModificators(modificators);
		return paragraphDescriptonBuilder.getFullParagraphDescripton(paragraph, ids,errors,null);
	}

	public String getFullParagraphDescripton(Paragraph paragraph, ArrayList<Paragraph> ids, ParagraphParsingHandler parsingHandler, ArrayList<ParagraphConnection> connections) {
		paragraphDescriptonBuilder.setParagraphParsingHandler(parsingHandler);
		paragraphDescriptonBuilder.setParameters(parameters);
		paragraphDescriptonBuilder.setModificators(modificators);
		return paragraphDescriptonBuilder.getFullParagraphDescripton(paragraph, ids,null,null,connections);
	}
	
	public boolean selectObject(ObjectBean object) {
		if (isFinished()) {
			return false;
		}
		if (currentParagraph != null) {
			ArrayList<ParagraphConnection> connections = model.getOutputParagraphConnections(currentParagraph);
			for (int i = 0; i < connections.size(); i++) {
				ParagraphConnection connection = connections.get(i);
				if (connection.getObject()==object && connection.getStrictness() != ParagraphConnection.STRICTNESS_MUST_NOT) {
					//has output connection from here
					currentParagraph = connection.getTo();
					fireEvent(EVENT_USE_OBJECT,object,true);
					return true;
				}
			}
		}
		fireEvent(EVENT_USE_OBJECT,object,false);
		return false;
	}
	
	public void addPlayerStateListener(PlayerStateListener listener) {
		if (listeners.contains(listener)==false) {
			listeners.add(listener);
		}
	}
	public void removePlayerStateListener(PlayerStateListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	public Iterator<ObjectBean> getObjectIterator() {
		return bag.iterator();
	}

	public boolean isBagEmpty() {
		return bag.isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see com.iambookmaster.client.player.PlayerGameState#saveState(boolean)
	 */
	public String saveState(boolean addToHistory) {
		JSONBuilder json = _toJSON(); 
		String old = null;
		//find the oldest state
		for (int i = 0; i < history.length; i++) {
			if (history[i] != null) {
				old = history[i];
				break;
			}
		}
		
		if (addToHistory) {
			//scroll history
			for (int i = 1; i < history.length; i++) {
				history[i-1] = history[i]; 
			}
			//save the latest state
			history[history.length-1] = json.toString();
		}
		if (old != null) {
			//add the oldest state
			json.field(FIELD_OLD_STATE,Base64Coder.encodeString(old));
		}
		return Base64Coder.encodeString(json.toString());

	}
	public String toJSON() {
		return _toJSON().toString();
	}
	private JSONBuilder _toJSON() {
		JSONBuilder builder = JSONBuilder.getStartInstance();
		builder.newRow();
		builder.field(FIELD_PARAGRAPH_ID, currentParagraph.getId());
		
		//bag
		JSONBuilder builderBag = builder.getInstance();
		Iterator<ObjectBean> iterator = bag.iterator();
		while (iterator.hasNext()) {
			ObjectBean bean = iterator.next();
			builderBag.newRow();
			builderBag.field(FIELD_OBJECT_ID,bean.getId());
		}
		builder.childArray(FIELD_BAG, builderBag);
		
		if (parameters.size()>0) {
			//parameters
			builderBag = builder.getInstance();
			for (Parameter parameter : parameters.keySet()) {
				int value = parameters.get(parameter);
				builderBag.newRow();
				builderBag.field(FIELD_PARAMETER_ID,parameter.getId());
				builderBag.field(FIELD_PARAMETER_VALUE,value);
			}
			builder.childArray(FIELD_PARAMETERS, builderBag);
		}
		
		if (modificators.size()>0) {
			StringBuffer buffer = new StringBuffer();
			for (Modificator modificator : modificators) {
				if (buffer.length()>0) {
					buffer.append(',');
				}
				buffer.append(modificator.getId());
			}
			builder.field(FIELD_MODIFICATORS, buffer.toString());
		}
		
		builder.field(FIELD_GAME_ID, model.getGameId());
		builder.field(FIELD_AUDIO, allowAudio ? 1:0);
		builder.field(FIELD_IMAGES, allowImages ? 1:0);
		if (background != null) {
			builder.field(FIELD_BACKGROUND, background.getId());
		}
		if (backgroundSound != null) {
			builder.field(FIELD_BACKGROUND_SOUND, backgroundSound.getId());
		}
		if (currentBattle != null && !fighters.isEmpty()) {
			//battle in the process
			builderBag = builder.getInstance();
			for (FighterData data : fighters) {
				builderBag.newRow();
				JSONBuilder jsonBuilder = builder.getInstance();
				data.npc.toJSON(jsonBuilder, Model.EXPORT_PLAY);
				builderBag.child(FIELD_NPC,jsonBuilder);
				if (currentBattleTarget==data) {
					builderBag.field(FIELD_PARAMETER_VALUE,1);
				}
				JSONBuilder builderSub = builder.getInstance();
				for (Parameter parameter : data.parameters.keySet()) {
					int value = data.parameters.get(parameter);
					builderSub.newRow();
					builderSub.field(FIELD_PARAMETER_ID, parameter.getId());
					builderSub.field(FIELD_PARAMETER_VALUE,value);
				}
				builderBag.childArray(FIELD_PARAMETERS,builderSub);
			}
			builder.childArray(FIELD_FIGHTERS,builderBag);
			builder.field(FIELD_BATTLE_ROUND,currentBattleRound);
			if (alchemyWasUsed) {
				builder.field(FIELD_ALCEMY_WAS_USED,1);
			}
			if (heroBatleLimits.isEmpty()==false) {
				builderBag = builder.getInstance();
				for (Parameter parameter : heroBatleLimits.keySet()) {
					int value = heroBatleLimits.get(parameter);
					builderBag.newRow();
					builderBag.field(FIELD_PARAMETER_ID, parameter.getId());
					builderBag.field(FIELD_PARAMETER_VALUE,value);
				}
				builderBag.childArray(FIELD_BATTLE_LIMITS,builderBag);
			}
		}
		if (metadata != null) {
			builderBag = builder.getInstance();
			metadata.toJSON(builderBag);
			builder.child(FIELD_METADATA,builderBag);
		}
		return builder;
	}
	
	public void fromJS(JavaScriptObject data) {
		JSONParser parser = JSONParser.getInstance();
		String gameId = parser.propertyString(data, FIELD_GAME_ID);
		if (model.getGameId().equals(gameId)==false) {
			throw new IllegalArgumentException(AppLocale.getAppConstants().playerDifferentVersionsOfGame());
		}
		String id = parser.propertyString(data, FIELD_PARAGRAPH_ID);
		Paragraph paragraph = model.getParagrapByID(id);
		if (paragraph==null) {
			throw new IllegalArgumentException("Error code 1");
		}
		//bag
		Object bg = parser.property(data, FIELD_BAG);
		int len = parser.length(bg);
		LinkedHashSet<ObjectBean> bag = new LinkedHashSet<ObjectBean>();
		for (int i = 0; i < len; i++) {
			id = parser.propertyString(parser.getRow(bg, i),FIELD_OBJECT_ID);
			ObjectBean bean = model.getObjectById(id);
			if (bean==null) {
				throw new IllegalArgumentException("Error code 2");
			}
			bag.add(bean);
		}
		
		LinkedHashMap<Parameter, Integer> parameters = new LinkedHashMap<Parameter, Integer>();
		HashSet<Modificator> modificators = new HashSet<Modificator>();
		HashMap<String,AbstractParameter> params = new HashMap<String, AbstractParameter>(model.getParameters().size());
		if (model.getParameters().size()>0) {
			ArrayList<AbstractParameter> list = model.getParameters();
			for (AbstractParameter abstractParameter : list) {
				params.put(abstractParameter.getId(), abstractParameter);
			}
			
			//parameters
			bg = parser.propertyNoCheck(data, FIELD_PARAMETERS);
			if (bg != null) {
				len = parser.length(bg);
				for (int i = 0; i < len; i++) {
					Object row = parser.getRow(bg, i);
					id = parser.propertyString(row,FIELD_PARAMETER_ID);
					AbstractParameter abstractParameter = params.get(id);
					if (abstractParameter instanceof Parameter) {
						Parameter parameter = (Parameter) abstractParameter;
						int value = parser.propertyInt(row, FIELD_PARAMETER_VALUE);
						parameters.put(parameter, value);
					} else {
						throw new IllegalArgumentException("Error code 5");
					}
				}
			}
			
			//modificators
			id =parser.propertyNoCheckString(data, FIELD_MODIFICATORS);
			if (id != null) {
				String[] mods = id.split(",");
				for (String key : mods) {
					AbstractParameter abstractParameter = params.get(key);
					if (abstractParameter instanceof Modificator) {
						modificators.add((Modificator) abstractParameter);
					} else {
						throw new IllegalArgumentException("Error code 6");
					}
				}
			}
		}
		
		//Background picture
		Picture picture=null;
		id = parser.propertyNoCheckString(data, FIELD_BACKGROUND);
		if (id != null) {
			picture = model.getPictureByID(id);
			if (picture==null) {
				throw new IllegalArgumentException("Error code 3");
			}
		}
		//Background sound
		Sound sound=null;
		id = parser.propertyNoCheckString(data, FIELD_BACKGROUND_SOUND);
		if (id != null) {
			sound = model.getSoundByID(id);
			if (sound==null) {
				throw new IllegalArgumentException("Error code 4");
			}
		}

		bg = parser.property(data, FIELD_METADATA);
		PlayerStateMetadata metadata;
		if (bg == null) {
			metadata = null;
		} else {
			metadata = new PlayerStateMetadata();
			metadata.fromJS(parser,bg);
		}
		//battle
		Battle battle = null;
		bg = parser.property(data, FIELD_FIGHTERS);
		FighterData target = null;
		ArrayList<FighterData> fighters=null;
		currentBattleRound = 0;	
		if (bg != null && paragraph.getBattle() != null) {
			//restore battle
			currentBattleRound = parser.propertyNoCheckInt(data, FIELD_BATTLE_ROUND);
			len = parser.length(bg);
			battle = paragraph.getBattle();
			fighters = new ArrayList<PlayerState.FighterData>(len);
			for (int i = 0; i < len; i++) {
				Object row = parser.getRow(bg, i);
				NPCParams npc;
				try {
					npc = NPCParams.fromJS(parser.propertyNoCheck(row, FIELD_NPC), parser, params);
				} catch (JSONException e) {
					throw new IllegalArgumentException(e.getMessage());
				}
				Object prs = parser.property(row,FIELD_PARAMETERS);
				int m = parser.length(prs);
				LinkedHashMap<Parameter, Integer> values = new LinkedHashMap<Parameter, Integer>(m); 
				for (int j = 0; j < m; j++) {
					Object pair = parser.getRow(prs, j);
					id = parser.propertyString(pair,FIELD_PARAMETER_ID);
					AbstractParameter abstractParameter = params.get(id);;
					if (abstractParameter instanceof Parameter) {
						Parameter parameter = (Parameter) abstractParameter;
						int value = parser.propertyInt(pair, FIELD_PARAMETER_VALUE);
						values.put(parameter, value);
					} else {
						throw new IllegalArgumentException("Error code 8");
					}
				}
				FighterData fighter = new FighterData(values,paragraph.getBattle(),npc);
				
				if (parser.propertyNoCheckInt(row,FIELD_PARAMETER_VALUE)>0) {
					target = fighter;
				}
				fighters.add(fighter);
			}
		}
		LinkedHashMap<Parameter, Integer> heroBatleLimits=null;
		bg = parser.property(data, FIELD_BATTLE_LIMITS);
		if (bg != null) {
			int m = parser.length(bg);
			heroBatleLimits = new LinkedHashMap<Parameter, Integer>(m); 
			for (int j = 0; j < m; j++) {
				Object pair = parser.getRow(bg, j);
				id = parser.propertyString(pair,FIELD_PARAMETER_ID);
				AbstractParameter abstractParameter = params.get(id);;
				if (abstractParameter instanceof Parameter) {
					Parameter parameter = (Parameter) abstractParameter;
					int value = parser.propertyInt(pair, FIELD_PARAMETER_VALUE);
					heroBatleLimits.put(parameter, value);
				} else {
					throw new IllegalArgumentException("Error code 9");
				}
			}
		}
		
		//success
		this.metadata = metadata; 
		currentBattle = battle;
		this.fighters.clear();
		if (battle != null) {
			this.fighters.addAll(fighters);
			alchemyWasUsed = parser.propertyNoCheckInt(data, FIELD_ALCEMY_WAS_USED)>0;
			if (heroBatleLimits != null) {
				this.heroBatleLimits = heroBatleLimits;
			}
			updateBattleConnections(paragraph);
		}
		background = picture;
		backgroundSound = sound;
		allowAudio = (parser.propertyInt(data,FIELD_AUDIO)>0);
		allowImages = (parser.propertyInt(data,FIELD_IMAGES)>0);
		this.bag = bag;
		paragraphDescriptonBuilder.setObjects(bag);
		this.currentParagraph = paragraph;
		this.parameters = parameters;
		this.modificators = modificators;
		fireEvent(EVENT_RESET);
		if (currentBattle != null) {
			//start battle
			currentBattleTarget = target;
			fireEvent(EVENT_BATTLE,battle,1);
			for ( FighterData npc : fighters) {
				fireEvent(EVENT_ENEMY, npc.getNpc().getNpc(), 1);
			}
			selectTarget();
		}
		for (int i = 0; i < history.length; i++) {
			history[i] = null;
		}
		String old = parser.propertyNoCheckString(data, FIELD_OLD_STATE);
		if (old != null) {
			//there is an old state
			history[history.length-1] = Base64Coder.decodeString(old);
		}
	}

	/* (non-Javadoc)
	 * @see com.iambookmaster.client.player.PlayerGameState#restoreState(java.lang.String)
	 */
	public void restoreState(String data) {
		String json = Base64Coder.decodeString(data);
		reset();
		fromJS(JSONParser.eval(json));
		finished = false;
		if (currentParagraph != null) {
			if (currentParagraph.isFail() || currentParagraph.isSuccess()) {
				finished = true;
			}
		}
	}

	public Paragraph getCurrentParagraph() {
		return currentParagraph;
	}

	public boolean isAllowAudio() {
		return allowAudio;
	}

	public void setAllowAudio(boolean allowAudio) {
		this.allowAudio = allowAudio;
	}

	public boolean isAllowImages() {
		return allowImages;
	}

	public void setAllowImages(boolean allowImages) {
		this.allowImages = allowImages;
	}

	public Picture getBackground() {
		return background;
	}

	public void setBackground(Picture background) {
		this.background = background;
	}

	public void setBackgroundSound(Sound sound) {
		backgroundSound = sound;
	}

	public Sound getBackgroundSound() {
		return backgroundSound;
	}

	public LinkedHashMap<Parameter, Integer> getParameters() {
		return parameters;
	}

	public HashSet<Modificator> getModificators() {
		return modificators;
	}

	public void heroIsDeadInBattle() {
		fireEvent(EVENT_FINISH);
	}

	public void heroWonBattle() {
		fireEvent(EVENT_BATTLE, currentBattle,0);
	}

	public void update(Parameter parameter, int value) {
		parameters.put(parameter, value);
		fireEvent(EVENT_CHANGE_PARAMETER, parameter, value);
	}

	public boolean meetsCondition(Alchemy alchemy,boolean battle) {
		if (currentParagraph.getAlchemy() != null && currentParagraph.getAlchemy().containsKey(alchemy)) {
			boolean value = currentParagraph.getAlchemy().get(alchemy);
			if (value==false) {
				//disable it
				return false;
			}
		} else if (alchemy.isOnDemand()) {
			//not available
			return false;
		}
		if (battle) {
			if (alchemy.getPlace()==Alchemy.PLACE_PEACE){
				//available in peaceful time only
				return false;
			}
			if (alchemy.getBattleLimit() != null) {
				//battle and Alchemy has battle limit
				Integer value = heroBatleLimits.get(alchemy.getBattleLimit());
				if (value == null || value==0) {
					return false;
				}
			}
		} else if (alchemy.getPlace()==Alchemy.PLACE_BATTLE){
			//available in battle only
			return false;
		}
		if (parameters.containsKey(alchemy.getFrom())==false) {
			return false;
		}
		int value = parameters.get(alchemy.getFrom());
		if (value <alchemy.getFromValue()) {
			return false; 
		}
		if (alchemy.isWeapon()==false && alchemy.getTo().getLimit() != null && (model.getSettings().isOverflowControl() ^ alchemy.isOverflowControl())) {
			//overflow control for peaceful alchemy
			if (parameters.containsKey(alchemy.getTo().getLimit())==false) {
				//limit is not set at all
				return false;
			}
			value = parameters.containsKey(alchemy.getTo()) ? parameters.get(alchemy.getTo()) : 0;
			int max = parameters.get(alchemy.getTo().getLimit());
			if (value>=max) {
				//no reason to use this alchemy now
				return false;
			}
		}
		//can be used
		return true;
	}

	/**
	 * Check that the connection is visible 
	 * @param connection
	 * @return
	 */
	public boolean alwaysVisible(ParagraphConnection connection) {
		if (connection.isConditional()==false) {
			return true;
		} else if (connection.getType()==ParagraphConnection.TYPE_NORMAL) {
			return model.getSettings().isHiddenUsingObjects()==connection.isReverseHiddenUsage();
		} else {
			return model.getSettings().isHideNonMatchedParameterConnections()==connection.isReverseHiddenUsage();
		}
	}
	
	public boolean meetsCondition(ParagraphConnection connection) {
		boolean xor = connection.getStrictness()==ParagraphConnection.STRICTNESS_MUST_NOT;
		if (currentBattle != null && isBattleActive()) {
			//battle mode
			switch (connection.getType()) {
			case ParagraphConnection.TYPE_ENEMY_VITAL_LESS:
			case ParagraphConnection.TYPE_VITAL_LESS:
				if (currentBattleTarget != null) {
					if (connection.getStrictness()==ParagraphConnection.STRICTNESS_MUST_NOT) {
						return Boolean.FALSE.equals(vitalConnections.get(connection));
					} else {
						return Boolean.TRUE.equals(vitalConnections.get(connection));
					}
				}
			case ParagraphConnection.TYPE_BATTLE_ROUND_MORE:
				Integer res = escapeBattleConnections.get(connection);
				if (res != null && res.intValue() <= currentBattleRound) {
					if (currentBattleTarget == null) {
						//battle end, only must-go conditions are actual
						return connection.getStrictness()==ParagraphConnection.STRICTNESS_MUST;
					} else {
						//in battle
						return connection.getStrictness()!=ParagraphConnection.STRICTNESS_MUST_NOT;
					}
				}
			}
			return false;
		} else {
			//peace mode
			switch (connection.getType()) {
			case ParagraphConnection.TYPE_NORMAL:
				return (connection.getObject()==null || bag.contains(connection.getObject())) ^ xor;
			case ParagraphConnection.TYPE_MODIFICATOR:
				if (connection.getModificator()==null) {
					return xor;
				} else {
					return modificators.contains(connection.getModificator())  ^ xor;
				}
			case ParagraphConnection.TYPE_NO_MODIFICATOR:
				if (connection.getModificator()==null) {
					return xor;
				} else {
					return (modificators.contains(connection.getModificator())==false) ^ xor;
				}
			case ParagraphConnection.TYPE_PARAMETER_LESS:
				if (connection.getParameter()==null|| parameters.containsKey(connection.getParameter())==false) {
					return xor;
				} else {
					return (parameters.get(connection.getParameter()) < connection.getParameterValue().calculate()) ^ xor;
				}
			case ParagraphConnection.TYPE_PARAMETER_MORE:
				if (connection.getParameter()==null|| parameters.containsKey(connection.getParameter())==false) {
					return xor;
				} else {
					return (parameters.get(connection.getParameter()) > connection.getParameterValue().calculate()) ^ xor;
				}
			}
		}
		return xor;
	}

	public boolean apply(Alchemy alchemy) {
		if (parameters.containsKey(alchemy.getFrom())) {
			int value = parameters.get(alchemy.getFrom());
			if (value >=alchemy.getFromValue()) {
				value = value - alchemy.getFromValue();
				parameters.put(alchemy.getFrom(),value);
				fireEvent(EVENT_CHANGE_PARAMETER, alchemy.getFrom(),value);
				if (alchemy.getFrom().isVital() && value<=0 && !finished ) {
					//hero is dead
					fireEvent(EVENT_FINISH);
				}
				if (alchemy.isWeapon()==false){
					if (parameters.containsKey(alchemy.getTo())) {
						value = parameters.get(alchemy.getTo());
					} else {
						value = 0; 
					}
					value = value + alchemy.getToValue().calculate();
					if (model.getSettings().isOverflowControl() ^ alchemy.isOverflowControl()) {
						if (alchemy.getTo().getLimit() != null && parameters.containsKey(alchemy.getTo().getLimit())) {
							//control limits
							int max = parameters.get(alchemy.getTo().getLimit());
							int val = parameters.containsKey(alchemy.getTo()) ? parameters.get(alchemy.getTo()) : 0;
							if (val>max) {
								if (value>val) {
									//it was a useless action
									value = val;
								}
							} else if (value>max) { 
								value=max;
							}
						}
					}
					parameters.put(alchemy.getTo(),value);
					fireEvent(EVENT_CHANGE_PARAMETER, alchemy.getTo(),value);
					if (alchemy.getTo().isVital() && value<=0 && !finished ) {
						//hero is dead
						fireEvent(EVENT_FINISH);
					}
				}
				return true;
			}
		}
		return false;
	}

	private void enableVitalConnection(ParagraphConnection connection) {
		vitalConnections.put(connection, Boolean.TRUE);
		fireEvent(EVENT_ENABLE, connection);
	}


	private void disableVitalConnection(ParagraphConnection connection) {
		vitalConnections.put(connection, Boolean.FALSE);
		fireEvent(EVENT_DISABLE, connection);
	}

	public Model getModel() {
		return model;
	}

	public boolean isFinished() {
		return finished;
	}

	public boolean hasObjects() {
		return bag.isEmpty()==false;
	}

	public ArrayList<Alchemy> getAlchemy(boolean battle,boolean currentParagraph) {
		ArrayList<AbstractParameter> list = model.getParameters();
		ArrayList<Alchemy> result = null;
		for (AbstractParameter parameter : list) {
			if (parameter instanceof Alchemy) {
				Alchemy alchemy = (Alchemy) parameter;
				if (battle) {
					if (alchemyWasUsed && alchemy.isOneTimePerRound()) {
						//this alchemy can be used just one time per round
						continue;
					}
				} else {
					//peas
					if (alchemy.isOnDemand() && currentParagraph==false) {
						//this alchemy is used in paragraph test
						continue;
					}
				}
				if (meetsCondition(alchemy, battle)) {
					if (result==null) {
						result = new ArrayList<Alchemy>();
					}
					result.add(alchemy);
				}
			}
		}
		return result;
	}

	public boolean nextBattleRound(BattleListener playerListener) {
		FighterData localTaget=selectTarget();
		if (localTaget==null) {
			heroWonBattle();
			return false;
		}
		
		FighterData victim = selectVictim();
		int oldVital = parameters.get(currentBattle.getVital());
		localTaget.round = currentBattle.calculateBattleRound(localTaget.getParameters());
		if (victim != null) {
			victim.round = currentBattle.calculateBattleRound(victim.getParameters());
			playerListener.victimAttack(victim.getNpc().getNpc(),localTaget.getNpc().getNpc());
			currentBattle.attack(victim.getParameters(),victim.round,localTaget.getParameters(),localTaget.round,playerListener,true);
			if (victim.isAlive()==false) {
				//victim is dead
				victim = null;
			}
			if (localTaget.isAlive()==false) {
				localTaget = selectTarget();
				if (localTaget==null) {
					heroWonBattle();
					return false;
				} else {
					//select next target
					localTaget.round = currentBattle.calculateBattleRound(localTaget.getParameters());					
				}
			}
		}
		
		boolean heroFight = victim==null || currentParagraph.isFightTogether(); 
		BattleRound heroRound = currentBattle.calculateBattleRound(parameters);
		if (heroFight) {
			alchemyWasUsed = false;
			playerListener.heroAttack(localTaget.getNpc().getNpc());
			currentBattle.attack(parameters,heroRound,localTaget.getParameters(),localTaget.round,playerListener,true);
			if (victim != null && localTaget.isAlive()==false) {
				//target is dead, select next target for victim
				localTaget = selectTarget();
				if (localTaget==null) {
					heroWonBattle();
					return false;
				} else {
					//select next target
					localTaget.round = currentBattle.calculateBattleRound(localTaget.getParameters());					
				}
			}
		}
		if (isHeroAlive()) {
			//NPC attack
			for (FighterData widget : fighters) {
				if (widget.isAlive()) {
					if (widget.isFriend() && widget.getRound()<currentBattleRound) {
						//active friend in battle
						if (widget != victim) {
							//this friend is not fought yet
							widget.round = currentBattle.calculateBattleRound(widget.getParameters());
							playerListener.victimAttack(victim.getNpc().getNpc(),localTaget.getNpc().getNpc());
							currentBattle.attack(victim.getParameters(),widget.round,localTaget.getParameters(),localTaget.round,playerListener,false);
							if (victim.isAlive()==false) {
								//victim is dead
								victim = null;
							}
							if (localTaget.isAlive()==false) {
								localTaget = selectTarget();
								if (localTaget==null) {
									heroWonBattle();
									return false;
								} else if (localTaget.round==null){
									//select next target
									localTaget.round = currentBattle.calculateBattleRound(localTaget.getParameters());					
								}
							}
						}
					} else if (currentBattle.isAttackDefense() || localTaget != widget) {
						//Enemy, for attack vs. attack battle Hero already fought with localTarget
						if (widget.round==null) {
							widget.round = currentBattle.calculateBattleRound(widget.getParameters());
						}
						if (victim == null) {
							//fight with hero
							playerListener.heroDefence(widget.getNpc().getNpc());
							currentBattle.attack(widget.getParameters(),widget.round,parameters,heroRound,playerListener,false);
						} else {
							//fight with victim
							playerListener.victimDefence(victim.npc.getNpc(),widget.getNpc().getNpc());
							currentBattle.attack(widget.getParameters(),widget.round,victim.getParameters(),victim.round,playerListener,false);
							if (victim.isAlive()==false) {
								//victim is dead
								victim = null;
							}
						}
					}
				}
			}
		}
		if (localTaget.isAlive()==false) {
			localTaget = null;
		}
		for (FighterData npcWidget : fighters) {
			if (npcWidget.isAlive()) {
				//at least one enemy is alive 
				if (localTaget==null) {
					currentBattleTarget = npcWidget; 
					localTaget = npcWidget;
				}
				break;
			}
		}
		
		for (Parameter parameter : parameters.keySet()) {
			int val = parameters.get(parameter);
			if (parameter.isVital() || currentBattle.getVital()==parameter) {
				if (val<=0) {
					//hero is dead
					for (ParagraphConnection connection : vitalConnections.keySet()) {
						if (connection.getType()==ParagraphConnection.TYPE_VITAL_LESS) {
							if (connection.getParameterValue() != null) {
								if (connection.getParameterValue().calculate() ==0 ) {
									//enable this vital connection, it is "Hero is dead"
									enableVitalConnection(connection);
									return true;
								}
							}
						}
					}
					fireEvent(EVENT_CHANGE_PARAMETER, parameter, val);
					finished = true;
					heroIsDeadInBattle();
					return false;
				}
			}
		}
		
		//check vital connections
		if (vitalConnections.isEmpty() == false) {
			for (ParagraphConnection connection : vitalConnections.keySet()) {
				int vital=10000000;
				if (connection.getType()==ParagraphConnection.TYPE_ENEMY_VITAL_LESS) {
					for (FighterData npcWidget : fighters) {
						if (npcWidget.isFriend() || npcWidget.getRound()>currentBattleRound) {
							//friend or join later
							continue;
						}
						int val = npcWidget.getParameters().get(currentBattle.getVital());
						if (val<vital) {
							vital = val;
						}
					}
				} else if (connection.getType()==ParagraphConnection.TYPE_VITAL_LESS) {
					vital = parameters.get(currentBattle.getVital());
				}
				if (connection.getParameterValue() != null) {
					if (connection.getParameterValue().calculate() > vital && vital>0) {
						//enable this vital connections
						enableVitalConnection(connection);
						if (connection.getStrictness()==ParagraphConnection.STRICTNESS_MUST) {
							//stop battle
							return false;
						}
					} else {
						//disable it
						disableVitalConnection(connection);
					}
				}
			}
		}
		
		int value = parameters.get(currentBattle.getVital());
		if (oldVital !=value) {
			fireEvent(EVENT_CHANGE_PARAMETER, currentBattle.getVital(), value);
		}
		
		if (escapeBattleConnections.size()>0) {
			//there are some escape connections
			for (ParagraphConnection connection : escapeBattleConnections.keySet()) {
				if (currentBattleRound==connection.getParameterValue().getConstant()) {
					//this connection has to be enable on this round
					if (connection.getStrictness()==ParagraphConnection.STRICTNESS_MUST_NOT) {
						disableVitalConnection(connection);
					} else {
						//we can cancel battle
						enableVitalConnection(connection);
						if (connection.getStrictness()==ParagraphConnection.STRICTNESS_MUST) {
							//disable all other connections
//							for (ParagraphConnection connection2 : escapeBattleConnections.keySet()) {
//								if (connection2 != connection) {
//									disableVitalConnection(connection2);
//								}
//							}
							//we must cancel the battle
							fireEvent(EVENT_BATTLE, currentBattle,0);
							return false;
						}
						
					}
				}
			}
		}
		if (localTaget==null) {
			//hero is winner
			heroWonBattle();
			return false;
		} else {
			currentBattleRound++;
			return true;
		}
	}

	private FighterData selectVictim() {
		boolean friends=false;
		for (FighterData npcWidget : fighters) {
			if (npcWidget.isFriend() && npcWidget.isAlive()) {
				//at least one friend is alive
				friends = true;
				if (npcWidget.getRound()<currentBattleRound) {
					//found victim
					return npcWidget;
				}
			}
		}
		if (currentParagraph.isFightTogether()) {
			//fight with Hero, NPC friend(s) joins later
			return null;
		}
		if (friends) {
			//move battle round counter
			for (FighterData npcWidget : fighters) {
				if (npcWidget.isFriend() && npcWidget.isAlive()) {
					currentBattleRound = npcWidget.getRound();
					return npcWidget;
				}
			}
		}
		//all friends are dead or non-exist
		return null;
	}

	public boolean isHeroAlive() {
		for (Parameter parameter : parameters.keySet()) {
			int value = parameters.get(parameter);
			if (value < 1 && parameter.isVital()) {
				return false;
			}
		}
		return true;
	}

	public boolean doAlchemyInBattle(Alchemy alchemy) {
		alchemyWasUsed = true;
		boolean updateHeroParameters=false;
		if (alchemy.isWeapon()) {
			if (currentBattleTarget == null || !currentBattleTarget.getParameters().containsKey(alchemy.getTo())) {
				return false;
			}
			//update Hero
			if (apply(alchemy)==false) {
				//hm..error
				return false;
			}
			//update NPC
			int value = currentBattleTarget.getParameters().get(alchemy.getTo());
			value = value - alchemy.getToValue().calculate();
			if (value<0 && !alchemy.getTo().isNegative()) {
				value = 0;
			}
			currentBattleTarget.getParameters().put(alchemy.getTo(), value);
			selectTarget();
			updateHeroParameters=true;
		} else {
			apply(alchemy);
			if (parameters.containsKey(alchemy.getTo())) {
				parameters.put(alchemy.getTo(),getParameters().get(alchemy.getTo()));
				updateHeroParameters=true;
			}
		}
//		if (parameters.containsKey(alchemy.getFrom())) {
//			parameters.put(alchemy.getFrom(),getParameters().get(alchemy.getFrom()));
//			updateHeroParameters=true;
//		}
		if (alchemy.getBattleLimit() != null) {
			//battle limit
			int value = heroBatleLimits.get(alchemy.getBattleLimit());
			heroBatleLimits.put(alchemy.getBattleLimit(), value-1);
			updateHeroParameters=true;
		}
		return updateHeroParameters;
	}
	
	public class FighterData {
		public BattleRound round;
		private Map<Parameter,Integer> parameters;
		private NPCParams npc;
		private boolean dead;

		public boolean isAlive() {
			if (dead) {
				return false;
			}
			for (Parameter parameter : parameters.keySet()) {
				int value = parameters.get(parameter);
				if (value < 1 && parameter.isVital()) {
					dead = true;
					return false;
				}
			}
			return true;
		}

		public NPCParams getNpc() {
			return npc;
		}

		public Map<Parameter, Integer> getParameters() {
			return parameters;
		}

		public FighterData() {
		}
		public FighterData(Map<Parameter,Integer> initialParameters,Battle battle,NPCParams npc) {
			this.npc = npc;
			parameters = new LinkedHashMap<Parameter, Integer>(initialParameters.size());
			//make a copy of parameters
			for (Parameter parameter : initialParameters.keySet()) {
				if (battle.dependsOn(parameter)) {
					parameters.put(parameter,new Integer(initialParameters.get(parameter)));
				}
			}
		}

		public int getVital() {
			return parameters.get(currentBattle.getVital());
		}

		public boolean isFriend() {
			return npc.isFriend();
		}

		public int getRound() {
			return npc.getRound();
		}

		public boolean isCanBeTarget() {
			if (npc.isFriend() || npc.getRound()>currentBattleRound) {
				//cannot be attacked now
				return false;
			} else {
				return isAlive();
			}
		}
	}

	public ArrayList<FighterData> getFighters() {
		return fighters;
	}

	public HashMap<Parameter, Integer> getHeroBattleLimits() {
		return heroBatleLimits;
	}

	public Battle getCurrentBattle() {
		return currentBattle;
	}

//	public boolean isAvailableInBattle(ParagraphConnection connection) {
//		if (connection.isBothDirections()) {
//			return false;
//		} else if (connection.getType()==ParagraphConnection.TYPE_ENEMY_VITAL_LESS || connection.getType()==ParagraphConnection.TYPE_VITAL_LESS){
//			Boolean res = vitalConnections.get(connection);
//			if (res==null) {
//				return false;
//			} else {
//				return res.booleanValue();
//			}
//		} else if (connection.getType()==ParagraphConnection.TYPE_BATTLE_ROUND_MORE){
//			return connection.getParameterValue().getConstant() < currentBattleRound;
//		} else {
//			return false;
//		}
//	}

	private FighterData selectTarget() {
		if (currentBattleTarget != null && currentBattleTarget.isCanBeTarget()) {
			return currentBattleTarget;
		}
		FighterData localTaget = null;
		boolean skipRounds=false;
		for (FighterData npc : fighters) {
			if (npc.isFriend()) {
				continue;
			}
			if (npc.isAlive()) {
				if (npc.getRound()>currentBattleRound) {
					//this NPC joins later
					skipRounds=true;
					continue;
				}
				//still in battle
				localTaget = npc;
				break;
			}
		}
		if (localTaget==null && skipRounds) {
			//no enemies now but there are something later
			for (FighterData npc : fighters) {
				if (npc.isFriend()) {
					continue;
				}
				if (npc.isAlive()) {
					//still in battle
					currentBattleRound = npc.getRound();
					localTaget = npc;
					break;
				}
			}			
		}
		if (localTaget==null) {
			heroWonBattle();
		} else {
			currentBattleTarget = localTaget;
		}
		return localTaget;
	}


	public boolean isTarget(FighterData target) {
		return target==currentBattleTarget;
	}

	public void selectTarget(FighterData target) {
		currentBattleTarget = target;
	}

	public boolean isBattleActive() {
		return currentBattle != null && currentBattleTarget != null;
	}

	public String getStateFromHistory() {
		for (int i = 0; i < history.length; i++) {
			if (history[i] != null) {
				history[history.length-1] = history[i];
				return Base64Coder.encodeString(history[i]);
			}
		}
		return null;
	}

	public boolean isCanRestoreFromHistory() {
		for (int i = 0; i < history.length; i++) {
			if (history[i] != null) {
				return true;
			}
		}
		return false;
	}

	public boolean isHeroFighting() {
		return currentParagraph.isFightTogether() || selectVictim()==null;
	}

	public boolean isHasBackState() {
		return history[history.length-2] != null;
	}

	public void goBack() {
		restoreState(Base64Coder.encodeString((history[history.length-2])));
	}

	public void killAllOpponents() {
		if (isBattleActive()) {
			for (FighterData data : fighters) {
				if (data.isCanBeTarget()) {
					data.dead = true;
				}
			}
		}
	}

}
