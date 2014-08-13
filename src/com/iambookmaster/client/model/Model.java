package com.iambookmaster.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.Window;
import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.NPCParams;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.beans.Picture;
import com.iambookmaster.client.beans.Settings;
import com.iambookmaster.client.beans.Sound;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.exceptions.JSONException;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;

public class Model {

	protected static final int EVENT_ADD_NEW = 0;
	protected static final int EVENT_REFRESH_ALL = 1;
	protected static final int EVENT_SELECTED = 2;
	protected static final int EVENT_UNSELECT = 3;
	protected static final int EVENT_UPDATE = 4;
	protected static final int EVENT_EDIT = 5;
	protected static final int EVENT_DELETE = 6;
	protected static final int EVENT_SHOW_INFO = 7;
	
	public static final int EXPORT_ALL = 0;
	public static final int EXPORT_PLAY = 1;
	
	protected static final String JSON_PARAGRAPHS = "locations";
	protected static final String JSON_PARAGRAPH_CONNECTION = "connections";
	protected static final String JSON_NEXT_ID = "next_id";
	protected static final String JSON_START_PARAGRAPH = "start";
	protected static final String JSON_PLOT = "plot";
	protected static final String JSON_OBJECTS = "obj";
	protected static final String JSON_NEXT_OBJ_ID = "next_obj_id";
	public static final char CONNECTION_DELIMETER_FROM = '<';
	public static final char CONNECTION_DELIMETER_TO = '>';
	protected static final String CONNECTION_DELIMETER_FROM_STR = "<";
	protected static final String CONNECTION_DELIMETER_TO_STR = ">";
	protected static final String JSON_SOUNDS = "snds";
	protected static final String JSON_IMAGES = "imgs";
	protected static final String JSON_NEXT_CONTENT_ID = "next_content_id";
	protected static final String JSON_SETTINGS = "set";
	protected static final String JSON_VERSION = "vr";
	protected static final String JSON_GAME_ID = "id";
	protected static final String JSON_GAME_KEY = "k";
	protected static final String JSON_PARAMETERS = "p";
	protected static final String JSON_BOOK_RULES = "r";
	protected static final String JSON_PLAYER_RULES = "s";
	protected static final String JSON_COMMERCIAL_TEXT = "t";
	protected static final String JSON_DEMO_INFO_TEXT = "u";
	
	public static final String CONNECTION_ID_PREFIX = "c";
	public static final String OBJECT_ID_PREFIX = "o";
	public static final String ALCHEMY_PREFIX_FROM = "af";
	public static final String ALCHEMY_PREFIX_TO = "at";
	public static final String MODIFICATOR_PREFIX = "m";
	public static final String BATTLE = "b";

	public static final int STATUS_PROPOSAL = 0;
	public static final int STATUS_DRAFT = 1;
	public static final int STATUS_FINAL = 2;
	public static final int HI_VERSION=1;
	public static final int LO_VERSION=10;
	public static final int PLAYER_LIST_TYPE_ALWAYS = 0;
	public static final int PLAYER_LIST_TYPE_POPUP = 1;
	public static final int PLAYER_LIST_TYPE_NONE = 2;
	private static final int SOURCE_BOOK_PLOT = 0;
	private static final int SOURCE_BOOK_RULES = 1;
	private static final int SOURCE_PLAYER_RULES = 2;
	private static final int SOURCE_COMMERCIAL_TEXT = 3;
	private static final int SOURCE_DEMO_INFO_TEXT = 4;
	
	protected int nextId;
	private ArrayList<ParagraphListener> paragraphListeners = new ArrayList<ParagraphListener>();
	private ArrayList<ParameterListener> parameterListeners = new ArrayList<ParameterListener>();
	protected ArrayList<Paragraph> paragraphs = new ArrayList<Paragraph>();
	protected ArrayList<ObjectBean> objects = new ArrayList<ObjectBean>();
	private ArrayList<ParagraphConnectionListener> paragraphConnectionListeners = new ArrayList<ParagraphConnectionListener>();
	private ArrayList<ObjectListener> objectListeners = new ArrayList<ObjectListener>();
	private ArrayList<PlotListener> plotListeners = new ArrayList<PlotListener>();
	private ArrayList<SettingsListener> settingsListener = new ArrayList<SettingsListener>();
	protected ArrayList<ParagraphConnection> paragraphConnections = new ArrayList<ParagraphConnection>();
	protected ArrayList<Picture> pictures = new ArrayList<Picture>();
	protected ArrayList<Sound> sounds = new ArrayList<Sound>();
	private ArrayList<ContentListener> contentListeners = new ArrayList<ContentListener>();
	protected ArrayList<AbstractParameter> parameters = new ArrayList<AbstractParameter>();
	private ContentPlayer contentPlayer;
	protected Settings settings = new Settings();
	private Paragraph currentParagraph;
	protected Paragraph startParagraph;
	protected String plot = "";
	protected String bookRules = "";
	protected String playerRules = "";
	protected String commercialText="";
	protected String demoInfoText="";
	protected int nextObjId;
	protected int nextContentId;
	private int version;
	private String gameId;
	protected String gameKey;
	private FullParagraphDescriptonBuilder descriptonBuilder = new FullParagraphDescriptonBuilder(); 
	protected final AppConstants appConstants;
	protected final AppMessages appMessages;
	
	public String getDemoInfoText() {
		return demoInfoText;
	}

	public String getBookRules() {
		return bookRules;
	}

	public String getPlayerRules() {
		return playerRules;
	}

	public String getCommercialText() {
		return commercialText;
	}

	public void setCommercialText(String commercialText) {
		this.commercialText = commercialText;
	}

	public Model(AppConstants appConstants, AppMessages appMessages) {
		this.appConstants = appConstants;
		this.appMessages = appMessages;
	}

	public Paragraph addNewParagraph(ParagraphListener sender) {
		Paragraph paragraph = new Paragraph();
		paragraph.setId(getNextParagraphId());
		paragraph.setName(appConstants.modelNewParagraphName());
		paragraphs.add(paragraph);
		if (startParagraph==null) {
			startParagraph = paragraph;
			paragraph.setType(Paragraph.TYPE_START);
		}
		fireParagraphEvent(EVENT_ADD_NEW,paragraph,sender);
		return paragraph;
	}

	protected String getNextParagraphId() {
		return String.valueOf(nextId++);
		
	}

	protected void fireParametersEvent(int event, AbstractParameter parameter,ParameterListener sender) {
		for (int i = 0; i < parameterListeners.size(); i++) {
			ParameterListener listener = parameterListeners.get(i);
			if (listener==sender) {
				continue;
			}
			switch (event) {
			case EVENT_ADD_NEW:
				listener.addNewParameter(parameter);
				break;
			case EVENT_REFRESH_ALL:
				listener.refreshAll();
				break;
			case EVENT_SELECTED:
				listener.select(parameter);
				break;
			case EVENT_UNSELECT:
				listener.select(parameter);
				break;
			case EVENT_UPDATE:
				listener.update(parameter);
				break;
			case EVENT_DELETE:
				listener.remove(parameter);
				break;
			case EVENT_SHOW_INFO:
				listener.showInfo(parameter);
				break;
			}
		}
	}

	protected void fireParagraphEvent(int event, Paragraph paragraph, ParagraphListener sender) {
		for (int i = 0; i < paragraphListeners.size(); i++) {
			ParagraphListener listener = paragraphListeners.get(i);
			if (listener==sender) {
				continue;
			}
			switch (event) {
			case EVENT_ADD_NEW:
				listener.addNewParagraph(paragraph);
				break;
			case EVENT_REFRESH_ALL:
				listener.refreshAll();
				break;
			case EVENT_SELECTED:
				listener.select(paragraph);
				break;
			case EVENT_UNSELECT:
				listener.unselect(paragraph);
				break;
			case EVENT_UPDATE:
				listener.update(paragraph);
				break;
			case EVENT_EDIT:
				listener.edit(paragraph);
				break;
			case EVENT_DELETE:
				listener.remove(paragraph);
				break;
			}
		}
	}

	protected void fireParagraphConnectionEvent(int event, ParagraphConnection connection, ParagraphConnectionListener sender) {
		for (int i = 0; i < paragraphConnectionListeners.size(); i++) {
			ParagraphConnectionListener listener = paragraphConnectionListeners.get(i);
			if (listener==sender) {
				continue;
			}
			switch (event) {
			case EVENT_REFRESH_ALL:
				listener.refreshAll();
				break;
			case EVENT_SELECTED:
				listener.select(connection);
				break;
			case EVENT_UNSELECT:
				listener.unselect(connection);
				break;
			case EVENT_UPDATE:
				listener.update(connection);
				break;
			case EVENT_DELETE:
				listener.remove(connection);
				break;
			case EVENT_ADD_NEW:
				listener.addNew(connection);
				break;
			}
		}
	}

	private void firePlotEvent(int event, int source, PlotListener sender) {
		for (int i = 0; i < plotListeners.size(); i++) {
			PlotListener listener = plotListeners.get(i);
			if (listener==sender) {
				continue;
			}
			switch (event) {
			case EVENT_REFRESH_ALL:
				listener.refreshAll();
				break;
			case EVENT_UPDATE:
				switch (source) {
				case SOURCE_BOOK_RULES:
					listener.updateBookRules(bookRules);
					break;
				case SOURCE_PLAYER_RULES:
					listener.updatePlayerRules(playerRules);
					break;
				case SOURCE_COMMERCIAL_TEXT:
					listener.updateCommercialText(commercialText);
					break;
				case SOURCE_DEMO_INFO_TEXT:
					listener.updateDemoInfoText(demoInfoText);
					break;
				default:
					listener.update(plot);
					break;
				}
				break;
			}
		}
	}

	protected void fireObjectEvent(int event, ObjectBean object, ObjectListener sender) {
		for (int i = 0; i < objectListeners.size(); i++) {
			ObjectListener listener = objectListeners.get(i);
			if (listener==sender) {
				continue;
			}
			switch (event) {
			case EVENT_REFRESH_ALL:
				listener.refreshAll();
				break;
			case EVENT_UPDATE:
				listener.update(object);
				break;
			case EVENT_SELECTED:
				listener.select(object);
				break;
			case EVENT_UNSELECT:
				listener.unselect(object);
				break;
			case EVENT_ADD_NEW:
				listener.addNewObject(object);
				break;
			case EVENT_DELETE:
				listener.remove(object);
				break;
			case EVENT_SHOW_INFO:
				listener.showInfo(object);
				break;
			}
		}
	}

	protected void firePictureEvent(int event, Picture picture, ContentListener sender) {
		for (int i = 0; i < contentListeners.size(); i++) {
			ContentListener listener = contentListeners.get(i);
			if (listener==sender) {
				continue;
			}
			switch (event) {
			case EVENT_REFRESH_ALL:
				listener.refreshAll();
				break;
			case EVENT_UPDATE:
				listener.update(picture);
				break;
			case EVENT_ADD_NEW:
				listener.addNew(picture);
				break;
			case EVENT_DELETE:
				listener.remove(picture);
				break;
			case EVENT_SELECTED:
				listener.select(picture);
				break;
			case EVENT_UNSELECT:
				listener.unselect(picture);
				break;
			case EVENT_SHOW_INFO:
				listener.showInfo(picture);
				break;
			}
		}
	}

	protected void fireSoundEvent(int event, Sound sound, ContentListener sender) {
		for (int i = 0; i < contentListeners.size(); i++) {
			ContentListener listener = contentListeners.get(i);
			if (listener==sender) {
				continue;
			}
			switch (event) {
			case EVENT_REFRESH_ALL:
				listener.refreshAll();
				break;
			case EVENT_UPDATE:
				listener.update(sound);
				break;
			case EVENT_ADD_NEW:
				listener.addNew(sound);
				break;
			case EVENT_DELETE:
				listener.remove(sound);
				break;
			case EVENT_SELECTED:
				listener.select(sound);
				break;
			case EVENT_UNSELECT:
				listener.unselect(sound);
				break;
			case EVENT_SHOW_INFO:
				listener.showInfo(sound);
				break;
			}
		}
	}
	
	public void addPicture(ContentListener sender) {
		Picture picture = new Picture();
		picture.setId(String.valueOf(nextContentId++));
		picture.setName(appConstants.modelNewImageName());
		pictures.add(picture);
		firePictureEvent(EVENT_ADD_NEW, picture, sender);
	}
	
	public void addSound(ContentListener sender) {
		Sound sound = new Sound();
		sound.setId(String.valueOf(nextContentId++));
		sound.setName(appConstants.modelNewSoundName());
		sounds.add(sound);
		fireSoundEvent(EVENT_ADD_NEW, sound, sender);
	}
	
	public void updatePicture(Picture picture,ContentListener sender) {
		firePictureEvent(EVENT_UPDATE, picture, sender);
	}
	
	public void updateSound(Sound sound,ContentListener sender) {
		fireSoundEvent(EVENT_UPDATE, sound, sender);
	}
	
	public boolean removePicture(Picture picture) {
		for (Paragraph paragraph : paragraphs) {
			if (paragraph.dependsOn(picture)) {
				Window.alert(appMessages.pictureIsUsedInParagraph(paragraph.getName()));
				return false;
			}
		}
		pictures.remove(picture);
		firePictureEvent(EVENT_DELETE, picture, null);
		return true;
	}
	
	public boolean removeSound(Sound sound) {
		for (Paragraph paragraph : paragraphs) {
			if (paragraph.dependsOn(sound)) {
				Window.alert(appMessages.soundIsUsedInParagraph(paragraph.getName()));
				return false;
			}
		}
		sounds.remove(sound);
		fireSoundEvent(EVENT_DELETE, sound, null);
		return true;
	}

	public void selectPicture(Picture object, ContentListener objectListener) {
		firePictureEvent(EVENT_SELECTED, object, null);
	}

	public void unselectPicture(Picture object, ContentListener objectListener) {
		firePictureEvent(EVENT_UNSELECT, object, null);
	}

	public void selectSound(Sound object, ContentListener objectListener) {
		fireSoundEvent(EVENT_SELECTED, object, null);
	}

	public void unselectSound(Sound object, ContentListener objectListener) {
		fireSoundEvent(EVENT_UNSELECT, object, null);
	}

	public void addContentListener(ContentListener listener) {
		if (contentListeners.contains(listener)==false) {
			contentListeners.add(listener);
		}
	}
	public void removeContentListener(ContentListener listener) {
		if (contentListeners.contains(listener)) {
			contentListeners.remove(listener);
		}
	}

	public void addParagraphConnection(ParagraphConnection connection,ParagraphConnectionListener sender) {
		paragraphConnections.add(connection);
		fireParagraphConnectionEvent(EVENT_ADD_NEW,connection,sender);
	}

	public void addParagraphListener(ParagraphListener listener) {
		if (paragraphListeners.contains(listener)==false) {
			paragraphListeners.add(listener);
		}
	}
	public void removeParagraphListener(ParagraphListener listener) {
		if (paragraphListeners.contains(listener)) {
			paragraphListeners.remove(listener);
		}
	}

	public void addParamaterListener(ParameterListener listener) {
		if (parameterListeners.contains(listener)==false) {
			parameterListeners.add(listener);
		}
	}
	public void removeParamaterListener(ParameterListener listener) {
		if (parameterListeners.contains(listener)) {
			parameterListeners.remove(listener);
		}
	}

	public void addParagraphConnectionListener(ParagraphConnectionListener listener) {
		if (paragraphConnectionListeners.contains(listener)==false) {
			paragraphConnectionListeners.add(listener);
		}
	}
	public void removeParagraphConnectionListener(ParagraphConnectionListener listener) {
		if (paragraphConnectionListeners.contains(listener)) {
			paragraphConnectionListeners.remove(listener);
		}
	}

	public void addPlotListener(PlotListener listener) {
		if (plotListeners.contains(listener)==false) {
			plotListeners.add(listener);
		}
	}
	public void removePlotListener(PlotListener listener) {
		if (plotListeners.contains(listener)) {
			plotListeners.remove(listener);
		}
	}

	public void addObjectsListener(ObjectListener listener) {
		if (objectListeners.contains(listener)==false) {
			objectListeners.add(listener);
		}
	}
	public void removeObjectsListener(ObjectListener listener) {
		if (objectListeners.contains(listener)==false) {
			objectListeners.remove(listener);
		}
	}

	public void updateSettings(SettingsListener sender) {
		for (int i = 0; i < settingsListener.size(); i++) {
			SettingsListener listener = settingsListener.get(i);
			if (listener!=sender) {
				listener.settingsWereUpated();
			}
			
		}
	}
	
	public void addSettingsListener(SettingsListener listener) {
		if (settingsListener.contains(listener)==false) {
			settingsListener.add(listener);
		}
	}
	public void removeSettingsListener(SettingsListener listener) {
		if (settingsListener.contains(listener)) {
			settingsListener.remove(listener);
		}
	}

	public boolean fromJSON(String model) {
		try {
			restore(JSONParser.eval((String)model),JSONParser.getInstance());
			return true;
		} catch (Exception e) {
			String message = e.getMessage();
			if (message.length()>300) {
				Window.alert(appMessages.modelErrorParsingData(message.substring(0,300)+"\n..."));
			} else {
				Window.alert(appMessages.modelErrorParsingData(message));
			}
			e.printStackTrace();
			return false;
		}
	}
	
	public void restore(Object object,JSONParser parser) throws JSONException {
		//restore sounds
		Object obj = parser.property(object, JSON_SOUNDS);
		if (obj==null) {
			sounds.clear();
		} else {
			sounds = Sound.fromJSArray(obj,parser);
		}
		
		//restore images
		obj = parser.property(object, JSON_IMAGES);
		if (obj==null) {
			pictures.clear();
		} else {
			pictures = Picture.fromJSArray(obj,parser);
		}
		
		//create map for fast searching
		HashMap<String, Picture> pct = new HashMap<String, Picture>(pictures.size());
		for (int i = 0; i < pictures.size(); i++) {
			Picture picture = pictures.get(i);
			pct.put(picture.getId(), picture);
		}
		HashMap<String, Sound> snd = new HashMap<String, Sound>(sounds.size());
		for (int i = 0; i < sounds.size(); i++) {
			Sound sound = sounds.get(i);
			snd.put(sound.getId(), sound);
		}
		
		//restore parameters
		obj = parser.propertyNoCheck(object, JSON_PARAMETERS);
		if (obj == null) {
			parameters = new ArrayList<AbstractParameter>();
		} else {
			parameters = AbstractParameter.fromJSArray(obj,parser,pct);
		}
		HashMap<String, AbstractParameter> parametersMap = new HashMap<String, AbstractParameter>();
		for (AbstractParameter parameter : parameters) {
			parametersMap.put(parameter.getId(), parameter);
		}
		
		//restore objects
		obj = parser.propertyNoCheck(object, JSON_OBJECTS);
		if (obj == null) {
			objects = new ArrayList<ObjectBean>();
		} else {
			objects = ObjectBean.fromJSArray(obj,parser,pct);
		}
		
		//restore paragraphs
		obj = parser.property(object, JSON_PARAGRAPHS);
		if (obj==null) {
			paragraphs.clear();
		} else {
			paragraphs = Paragraph.fromJSArray(obj,objects,pct,snd,parser,appMessages,parametersMap);
		}
		
		//restore connections
		obj = parser.property(object, JSON_PARAGRAPH_CONNECTION);
		if (obj==null) {
			paragraphConnections.clear();
		} else {
			paragraphConnections = ParagraphConnection.fromJSArray(obj,paragraphs,objects,parser,appMessages,parametersMap);
		}
		nextId =  parser.propertyNoCheckInt(object, JSON_NEXT_ID);
		nextObjId = parser.propertyNoCheckInt(object, JSON_NEXT_OBJ_ID);
		nextContentId = parser.propertyNoCheckInt(object, JSON_NEXT_CONTENT_ID);
		String startId = parser.propertyNoCheckString(object, JSON_START_PARAGRAPH);
		startParagraph = null;
		for (int i = 0; i < paragraphs.size(); i++) {
			Paragraph paragraph = paragraphs.get(i);
			if (paragraph.getType()==Paragraph.TYPE_START) {
				if (startId == null) {
					startParagraph = paragraph;
					continue;
				} else if (startId.equals(paragraph.getId())) {
					//the same
					startParagraph = paragraph;
					continue;
				}
				//error, correct it
				paragraph.setType(Paragraph.TYPE_NORMAL);
			} else if (paragraph.getId().equals(startId) && startParagraph==null) {
				startParagraph = paragraph;
			}
		}
		String plot = parser.propertyNoCheckString(object, JSON_PLOT);
		if (plot == null) {
			this.plot = "";
		} else {
			this.plot = plot;
		}
		plot = parser.propertyNoCheckString(object, JSON_BOOK_RULES);
		if (plot == null) {
			this.bookRules = "";
		} else {
			this.bookRules = plot;
		}
		plot = parser.propertyNoCheckString(object, JSON_PLAYER_RULES);
		if (plot == null) {
			this.playerRules = "";
		} else {
			this.playerRules = plot;
		}
		plot = parser.propertyNoCheckString(object, JSON_COMMERCIAL_TEXT);
		if (plot == null) {
			this.commercialText = "";
		} else {
			this.commercialText = plot;
		}
		plot = parser.propertyNoCheckString(object, JSON_DEMO_INFO_TEXT);
		if (plot == null) {
			this.demoInfoText = "";
		} else {
			this.demoInfoText = plot;
		}
		
		obj = parser.property(object, JSON_SETTINGS);
		if (obj != null) {
			settings = Settings.fromJS(obj,parser);
		} else {
			settings = new Settings();
		}

		version = parser.propertyNoCheckInt(object, JSON_VERSION);
		gameId = parser.propertyNoCheckString(object, JSON_GAME_ID);
		gameKey = parser.propertyNoCheckString(object, JSON_GAME_KEY);
		firePlotEvent(EVENT_REFRESH_ALL, SOURCE_BOOK_PLOT, null);
		fireSoundEvent(EVENT_REFRESH_ALL, null, null);
		firePictureEvent(EVENT_REFRESH_ALL, null, null);
		fireParagraphEvent(EVENT_REFRESH_ALL,null,null);
		fireObjectEvent(EVENT_REFRESH_ALL, null, null);
		fireParametersEvent(EVENT_REFRESH_ALL, null, null);
		updateSettings(null);
	}

	public void updateParagraph(Paragraph paragraph,ParagraphListener sender) {
		fireParagraphEvent(EVENT_UPDATE,paragraph,sender);
	}

	public ArrayList<Paragraph> getParagraphs() {
		return paragraphs;
	}

	public ArrayList<ParagraphConnection> getParagraphConnections() {
		return paragraphConnections;
	}

	public ArrayList<ObjectBean> getObjects() {
		return objects;
	}

	public void apply(Model model) {
		paragraphConnections = model.paragraphConnections;
		objects = model.objects;
		paragraphs = model.paragraphs;
		nextId = model.nextId;
		nextObjId = model.nextObjId;
		nextContentId = model.nextContentId;
		startParagraph = model.startParagraph;
		plot = model.plot;
		bookRules = model.bookRules;
		playerRules = model.playerRules;
		settings = model.settings;
		sounds = model.sounds;
		pictures = model.pictures;
		version = model.version;
		gameId = model.gameId;
		parameters = model.parameters;
		commercialText = model.commercialText;
		demoInfoText = model.demoInfoText;
		bookRules = model.bookRules;
		playerRules = model.playerRules;
		refreshAll();
	}
	
	protected void refreshAll() {
		firePlotEvent(EVENT_REFRESH_ALL, SOURCE_BOOK_PLOT, null);
		fireSoundEvent(EVENT_REFRESH_ALL, null, null);
		firePictureEvent(EVENT_REFRESH_ALL, null, null);
		fireObjectEvent(EVENT_REFRESH_ALL, null, null);
		fireParagraphEvent(EVENT_REFRESH_ALL,null,null);
		fireParagraphConnectionEvent(EVENT_REFRESH_ALL,null,null);
		fireParametersEvent(EVENT_REFRESH_ALL,null,null);
		updateSettings(null);
	}


	public void selectParagraph(Paragraph paragraph, ParagraphListener sender) {
		if (currentParagraph!=paragraph) {
			if (currentParagraph != null) {
				fireParagraphEvent(EVENT_UNSELECT,currentParagraph,sender);
			}
			currentParagraph = paragraph;
			fireParagraphEvent(EVENT_SELECTED,paragraph,sender);
		}
	}

	public void makeParagraphAsStart(Paragraph paragraph) {
		if (paragraph==startParagraph) {
			//already start 
			paragraph.setType(Paragraph.TYPE_START);
			return;
		}
		if (paragraph.isFail()) {
			Window.alert(appConstants.modelParagraphIsFailMakeNormal());
			return;
		}
		if (startParagraph != null) {
			makeParagraphAsNormal(startParagraph);
		}
		startParagraph = paragraph;
		paragraph.setType(Paragraph.TYPE_START);
		updateParagraph(paragraph, null);
	}

	public void makeParagraphAsNormal(Paragraph paragraph) {
		if (startParagraph==paragraph) {
			startParagraph = null;
		}
		paragraph.setType(Paragraph.TYPE_NORMAL);
		updateParagraph(paragraph, null);
	}

	public void makeParagraphAsFail(Paragraph paragraph) {
		if (startParagraph==paragraph) {
			Window.alert(appConstants.modelParagraphIsStartCannotBeFail());
			return;
		}
		paragraph.setType(Paragraph.TYPE_FAIL);
		updateParagraph(paragraph, null);
	}

	public void makeParagraphAsCommercial(Paragraph paragraph) {
		if (startParagraph==paragraph) {
			Window.alert(appConstants.modelParagraphIsStartCannotBeCommercial());
			return;
		}
		paragraph.setType(Paragraph.TYPE_COMMERCIAL);
		updateParagraph(paragraph, null);
	}

	public void makeParagraphAsSuccess(Paragraph paragraph) {
		if (startParagraph==paragraph) {
			Window.alert(appConstants.modelParagraphIsStartCannotBeSuccess());
			return;
		}
		paragraph.setType(Paragraph.TYPE_SUCCESS);
		updateParagraph(paragraph, null);
	}

	public Paragraph getStartParagraph() {
		return startParagraph;
	}

	public ArrayList<ParagraphConnection> getOutputParagraphConnections(Paragraph paragraph) {
		ArrayList<ParagraphConnection> result = new ArrayList<ParagraphConnection>();
		int l = paragraphConnections.size();
		for (int i = 0; i < l; i++) {
			ParagraphConnection connection = paragraphConnections.get(i);
			if (connection.getFrom()==paragraph || (connection.isBothDirections() && connection.getTo()==paragraph)) {
				result.add(connection);
			}
		}
		return result;
	}

	public ArrayList<ParagraphConnection> getInputParagraphConnections(Paragraph paragraph) {
		ArrayList<ParagraphConnection> result = new ArrayList<ParagraphConnection>();
		int l = paragraphConnections.size();
		for (int i = 0; i < l; i++) {
			ParagraphConnection connection = paragraphConnections.get(i);
			if (connection.getTo()==paragraph || (connection.isBothDirections() && connection.getFrom()==paragraph)) {
				result.add(connection);
			}
		}
		return result;
	}

	public void editParagraph(Paragraph paragraph, ParagraphListener sender) {
		fireParagraphEvent(EVENT_EDIT, paragraph, sender);
	}

	public void updateParagraphConnection(ParagraphConnection connection, ParagraphConnectionListener sender) {
		fireParagraphConnectionEvent(EVENT_UPDATE, connection, sender);
	}

	public void selectParagraphConnection(ParagraphConnection connection, ParagraphConnectionListener sender) {
		fireParagraphConnectionEvent(EVENT_SELECTED, connection, sender);
	}

	public void unselectParagraphConnection(ParagraphConnection connection, ParagraphConnectionListener sender) {
		fireParagraphConnectionEvent(EVENT_UNSELECT, connection, sender);
	}

	public void unselectParagraph(Paragraph paragraph, ParagraphListener sender) {
		fireParagraphEvent(EVENT_UNSELECT, paragraph, sender);
		if (currentParagraph==paragraph) {
			currentParagraph = null;
		}
	}

	public String getPlot() {
		return plot;
	}
	public void updateBookRules(String rules, PlotListener sender) {
		bookRules = rules;
		firePlotEvent(EVENT_UPDATE,SOURCE_BOOK_RULES,sender);
	}

	public void updatePlayerRules(String rules, PlotListener sender) {
		playerRules = rules;
		firePlotEvent(EVENT_UPDATE,SOURCE_PLAYER_RULES,sender);
	}

	public void updatePlot(String plot,PlotListener sender) {
		this.plot = plot;
		firePlotEvent(EVENT_UPDATE,SOURCE_BOOK_PLOT,sender);
	}

	public void selectObject(ObjectBean object, ObjectListener sender) {
		fireObjectEvent(EVENT_SELECTED, object, sender);
	}

	public void unselectObject(ObjectBean object, ObjectListener sender) {
		fireObjectEvent(EVENT_UNSELECT, object, sender);
	}

	public ObjectBean addNewObject(ObjectListener sender) {
		ObjectBean objectBean = new ObjectBean();
		objectBean.setId(String.valueOf(nextObjId++));
		objectBean.setName(appConstants.newObjectName());
		objects.add(objectBean);
		fireObjectEvent(EVENT_ADD_NEW, objectBean, sender);
		return objectBean;
	}

	public void updateObject(ObjectBean object, ObjectListener sender) {
		fireObjectEvent(EVENT_UPDATE, object, sender);
	}

	public void removeParagraphConnection(ParagraphConnection connection) {
		paragraphConnections.remove(connection);
		fireParagraphConnectionEvent(EVENT_DELETE, connection,null);
	}

	public void removeParagraph(Paragraph paragraph) {
		if (startParagraph==paragraph) {
			Window.alert(appConstants.modelParagraphIsStartCannotBeDeleted());
			return;
		}
		ArrayList<ParagraphConnection> list = getAllParagraphConnections(paragraph);
		if (list.size()>0) {
			Window.alert(appMessages.modelParagraphStillHasConnections(paragraph.getName()));
			return;
		}
		paragraphs.remove(paragraph);
		fireParagraphEvent(EVENT_DELETE, paragraph,null);
	}

	public ArrayList<Paragraph> getAllSuccessParagraphs() {
		ArrayList<Paragraph> result = new ArrayList<Paragraph>();
		int l = paragraphs.size();
		for (int i = 0; i < l; i++) {
			Paragraph paragraph = paragraphs.get(i);
			if (paragraph.isSuccess()) {
				result.add(paragraph);
			}
		}
		return result;
	}

	public ArrayList<ParagraphConnection> getAllParagraphConnections(Paragraph paragraph) {
		ArrayList<ParagraphConnection> result = new ArrayList<ParagraphConnection>();
		int l = paragraphConnections.size();
		for (int i = 0; i < l; i++) {
			ParagraphConnection connection = paragraphConnections.get(i);
			if (connection.getFrom()==paragraph || connection.getTo()==paragraph) {
				result.add(connection);
			}
		}
		return result;
	}

	public void removeObjects(ObjectBean objectBean) {
		int l = paragraphConnections.size();
		for (int i = 0; i < l; i++) {
			ParagraphConnection connection = paragraphConnections.get(i);
			if (connection.getObject()==objectBean) {
				Window.alert(appMessages.modelObjectCannotBeRemoved(objectBean.getName(),connection.getFrom().getName(),connection.getTo().getName()));
				return;
			}
		}
		l = paragraphs.size();
		for (int i = 0; i < l; i++) {
			Paragraph paragraph = paragraphs.get(i);
			if (paragraph.getGotObjects().contains(objectBean)) {
				Window.alert(appMessages.modelObjectCannotBeRemoved2(objectBean.getName(),paragraph.getName()));
				return;
			}
		}
		objects.remove(objectBean);
		fireObjectEvent(EVENT_DELETE, objectBean,null);
	}

	public Settings getSettings() {
		return settings;
	}

	
	
	public String getFullParagraphDescripton(Paragraph paragraph,ArrayList<Paragraph> ids,ArrayList<String> errors) throws IllegalArgumentException {
		return descriptonBuilder.getFullParagraphDescripton(paragraph, ids, errors, null);
	}
	
	public String getFullParagraphDescripton(Paragraph paragraph,ArrayList<Paragraph> ids,ArrayList<String> errors,Paragraph preSelect) throws IllegalArgumentException {
		return descriptonBuilder.getFullParagraphDescripton(paragraph, ids, errors, preSelect);
	}

	public ArrayList<Picture> getPictures() {
		return pictures;
	}

	public ArrayList<Sound> getSounds() {
		return sounds;
	}

	public void previewURL(String url) {
		if (contentPlayer != null) {
			contentPlayer.openURL(url);
		}
	}

	public void playSound(String url,boolean loop) {
		if (contentPlayer != null) {
			contentPlayer.playSound(url,loop);
		}
	}

	public ContentPlayer getContentPlayer() {
		return contentPlayer;
	}

	public void setContentPlayer(ContentPlayer contentPlayer) {
		this.contentPlayer = contentPlayer;
	}

	public void previewImage(Picture picture) {
		previewURL(picture.getUrl());
	}

	public void playSound(Sound sound) {
		playSound(sound.getUrl(),false);
	}

	public class FullParagraphDescriptonBuilder {
		private String connectionPattern;
		private String connectionMarkedPattern;
		private ParagraphDescriptionLinkProvider linkProvider;
		private SecretKeyDescriptionProvider secretKeyProvider;
		private ParagraphParsingHandler paragraphParsingHandler;
		private Set<ObjectBean> objects;
		private Map<Parameter,Integer> parameters;
		private Set<Modificator> modificators;
		private boolean hiddenUsingObjects;
		private boolean emptyConditionIsError=true;
		private boolean playerMode;
		private boolean checkSecretKeys;

		public boolean isCheckSecretKeys() {
			return checkSecretKeys;
		}

		public void setCheckSecretKeys(boolean checkSecretKeys) {
			this.checkSecretKeys = checkSecretKeys;
		}

		public boolean isPlayerMode() {
			return playerMode;
		}

		public void setPlayerMode(boolean playerMode) {
			this.playerMode = playerMode;
		}

		public boolean isEmptyConditionIsError() {
			return emptyConditionIsError;
		}

		public void setEmptyConditionIsError(boolean emptyConditionIsError) {
			this.emptyConditionIsError = emptyConditionIsError;
		}

		public FullParagraphDescriptonBuilder() {
			connectionPattern = "<span></span>";
			connectionMarkedPattern = "<span></span>";
			hiddenUsingObjects = settings.isHiddenUsingObjects();
		}
		
		public ParagraphParsingHandler getParagraphParsingHandler() {
			return paragraphParsingHandler;
		}

		public void setParagraphParsingHandler(
				ParagraphParsingHandler paragraphParsingHandler) {
			this.paragraphParsingHandler = paragraphParsingHandler;
		}
		
		public String getConnectionMarkedPattern() {
			return connectionMarkedPattern;
		}

		public void setConnectionMarkedPattern(String connectionMarkedPattern) {
			this.connectionMarkedPattern = connectionMarkedPattern;
		}

		public String getConnectionPattern() {
			return connectionPattern;
		}

		public void setConnectionPattern(String connectionPattern) {
			this.connectionPattern = connectionPattern;
		}
		
		public String getFullParagraphDescripton(Paragraph paragraph,ArrayList<Paragraph> ids) throws IllegalArgumentException {
			return getFullParagraphDescripton(paragraph,ids,null,null,null);
		}
		
		public String getFullParagraphDescripton(Paragraph paragraph,ArrayList<Paragraph> ids,ArrayList<ParagraphConnection> connections) throws IllegalArgumentException {
			return getFullParagraphDescripton(paragraph,ids,null,null,connections);
		}
		
		public String getFullParagraphDescripton(Paragraph paragraph,ArrayList<Paragraph> ids,ArrayList<String> errors,Paragraph preSelect) throws IllegalArgumentException {
			return getFullParagraphDescripton(paragraph,ids,errors,preSelect,null);
		}
		public String getFullParagraphDescripton(Paragraph paragraph,ArrayList<Paragraph> ids,ArrayList<String> errors,Paragraph preSelect,ArrayList<ParagraphConnection> connections) throws IllegalArgumentException {
			if (connections==null) {
				connections = getOutputParagraphConnections(paragraph);
			} else {
				//make a copy
				ArrayList<ParagraphConnection> conn = new ArrayList<ParagraphConnection>(connections.size());
				conn.addAll(connections);
				connections = conn;
			}
			if (errors != null && paragraph.getType()==Paragraph.TYPE_START && paragraph.isCommercial()) {
				errors.add(appConstants.modelParagraphIsStartCannotBeCommercial());
			}
			StringBuffer buffer = new StringBuffer();
			StringBuffer postBuffer= null;
			buffer.append(paragraph.getDescription());
			int pos=0;
			int condition=-1;
			int objectDetected=0;
			boolean scanAchemy=true;
			boolean battleFound=false;
			ArrayList<Modificator> mustUseModificators=null;
			if (paragraph.getChangeModificators() != null && paragraph.getChangeModificators().size()>0) {
				mustUseModificators = new ArrayList<Modificator>();
				for (Modificator modificator : paragraph.getChangeModificators().keySet()) {
					if (modificator.isAbsolute() || settings.isAddModificatorNamesToText()) {
						mustUseModificators.add(modificator);
					}
				}
			}
			ArrayList<Alchemy> mustFromAlchemy=null;
			ArrayList<Alchemy> mustToAlchemy=null;
			if (paragraph.getAlchemy() != null) {
				mustToAlchemy = new ArrayList<Alchemy>(paragraph.getAlchemy().size());
				if (settings.isAddAlchemyToText()) {
					mustFromAlchemy = new ArrayList<Alchemy>(paragraph.getAlchemy().size());
				}
				for (Alchemy alchemy : paragraph.getAlchemy().keySet()) {
					boolean value = paragraph.getAlchemy().get(alchemy);
					if (value) {
						if (settings.isAddAlchemyToText()) {
							mustFromAlchemy.add(alchemy);
						}
						mustToAlchemy.add(alchemy);
					}
				}
			}
			if (settings.isAddModificatorNamesToText()) {
				for (ParagraphConnection connection : connections) {
					if (connection.getType()==ParagraphConnection.TYPE_MODIFICATOR || connection.getType()==ParagraphConnection.TYPE_NO_MODIFICATOR) {
						if (connection.getFrom()==paragraph)  {
							if (mustUseModificators==null) {
								mustUseModificators = new ArrayList<Modificator>();
							}
							if (connection.getModificator()==null) {
								if (errors != null) {
									errors.add(appMessages.modelParagraphHasConnectionWithoutModificator(connection.getFrom().getName(),connection.getTo().getName()));
								}
							} else {
								mustUseModificators.add(connection.getModificator());
							}
						}
					}
				}
			}
			
			next_step:
			while (true) {
				int start = buffer.indexOf(CONNECTION_DELIMETER_FROM_STR,pos);
				if (start<0) {
					if (paragraphParsingHandler !=null) {
						if (pos==0) {
							paragraphParsingHandler.addText(paragraph,buffer.toString());
						} else if (pos<buffer.length()) {
							paragraphParsingHandler.addText(paragraph,buffer.substring(pos));
						}
					}
					break;
				}
				if (start>0 && condition<0 && paragraphParsingHandler !=null) {
					paragraphParsingHandler.addText(paragraph,buffer.substring(pos, start));
				}
				pos = start;
				int end = buffer.indexOf(CONNECTION_DELIMETER_TO_STR,start);
				if (end<0) {
					if (errors != null) {
						errors.add(appConstants.modelWrongParagraphStructure());
					}
				}
				String id = buffer.substring(start+1, end);
				buffer.replace(start, end+1, "");
				if (id.length()==0) {
					//marker - <>
					if (condition>=0) {
						//second marker started - wrong!
						if (errors != null) {
							errors.add(appConstants.modelParagraphHave2DelimSiquence());
						}
					} else {
						condition = start;
					}
					continue;
				}
				if (id.startsWith(OBJECT_ID_PREFIX)) {
					//object
					if (condition>=0 && errors != null) {
						errors.add(appConstants.modelParagraphObjectWithConditionPrefix());
					}
					ObjectBean objectBean=null;
					if (paragraph.getGotObjects().size()==0) {
						if (errors != null) {
							errors.add(appConstants.modelParagraphUnknownReferenceItemText());
						}
					} else {
						//check it
						for (ObjectBean bean : paragraph.getGotObjects()) {
							if (id.substring(1).equals(bean.getId())){
								objectBean = bean;
								break;
							}
						}
//						if (objectBean==null) {
//							for (ObjectBean bean : paragraph.getLostObjects()) {
//								if (id.substring(1).equals(bean.getId())){
//									objectBean = bean;
//									break;
//								}
//							}
//						}
						if (objectBean == null && errors != null) {
							errors.add(appConstants.modelParagraphHasReferenceToOtherItemText());
						}
					}
					if (objectBean != null) {
						objectDetected++;
						if (hiddenUsingObjects) {
							//use secret keys
							if (objectBean.getKey() == 0) {
								//key not set
								if (checkSecretKeys && errors != null) {
									errors.add(appMessages.modelSecretKeyIsNotSet(objectBean.getName()));
								}
							} else {
								String key;
								if (secretKeyProvider == null) { 
									key = "("+(objectBean.getKey()>0 ? "+":"")+objectBean.getKey()+")";
								} else {
									key = secretKeyProvider.getObjectSecretKey(objectBean);
								}
								if (paragraphParsingHandler == null) {
									if (playerMode==false) {
										buffer.insert(start,key);
									}
								} else {
									paragraphParsingHandler.addObject(paragraph,objectBean,key);
								}
							}
						}
					}
					continue;
				}
				if (id.startsWith(MODIFICATOR_PREFIX)) {
					//modificator
					if (condition>=0 && errors != null) {
						errors.add(appConstants.modelParagraphObjectWithConditionPrefix());
					}
					if (mustUseModificators==null || mustUseModificators.size()==0) {
						if (errors != null) {
							errors.add(appConstants.modelParagraphMustNotHaveModificatorsInText());
						}
					} else {
						String idM = id.substring(1);
						for (Iterator<Modificator> iterator = mustUseModificators.iterator(); iterator.hasNext();) {
							Modificator modificator = (Modificator) iterator.next();
							if (modificator.getId().equals(idM)) {
								//found
								if (paragraph.getChangeModificators()!= null && paragraph.getChangeModificators().containsKey(modificator)) {
									if (modificator.isAbsolute() && paragraph.getChangeModificators().get(modificator)) {
										//set absolute modificator
										if (playerMode==false) {
											//only for book mode
											String num=null;
											if (linkProvider == null) {
												for (ParagraphConnection connection : paragraphConnections) {
													if (connection.getType()==ParagraphConnection.TYPE_MODIFICATOR && connection.getModificator()==modificator) {
														//found
														num = String.valueOf(connection.getTo().getNumber());
														break;
													}
												}
											} else {
												num = linkProvider.getModificatorValue(modificator);
											}
											if (num == null) {
												if (errors != null) {
													errors.add(appMessages.modelParagraphHasUnusedAbsModificator(modificator.getName()));
												}
											} else if (num.length()>0){
												//we got number
												buffer.insert(start,num);
												if (settings.isAddModificatorNamesToText()) {
													buffer.insert(start,'=');
												}
											}
										}
									}
								} 
								if (settings.isAddModificatorNamesToText()) {
									if (paragraphParsingHandler == null) {
										buffer.insert(start,modificator.getName());
									} else {
										paragraphParsingHandler.addText(paragraph, modificator.getName());
									}
								}
								iterator.remove();
								continue next_step;
							}
						}
						//not found
						if (errors != null) {
							errors.add(appMessages.modelParagraphHasUnknownModificator(idM));
						}
						
					}
					
				}
				if (id.startsWith(ALCHEMY_PREFIX_FROM) || id.startsWith(ALCHEMY_PREFIX_TO)) {
					//alchemy
					if (condition>=0 && errors != null) {
						errors.add(appConstants.modelParagraphObjectWithConditionPrefix());
					}
					if (paragraph.getAlchemy()==null) {
						if (errors != null) {
							errors.add(appConstants.modelParagraphUnknownReferenceAlchemyText());
						}
					} else {
						String alId = id.substring(2);
						Alchemy found=null;
						for (Alchemy alchemy : paragraph.getAlchemy().keySet()) {
							boolean value = paragraph.getAlchemy().get(alchemy);
							if (scanAchemy && errors != null) {
								//validate all alchemy in the paragraph
								if (value) {
									if (alchemy.isOnDemand()==false) {
										errors.add(appMessages.modelParagraphHasNonDemandAlchemy(alchemy.getName()));
									}
								} else {
									if (alchemy.isOnDemand()) {
										errors.add(appMessages.modelParagraphHasDisabledOnDemandAlchemy(alchemy.getName()));
									}
								}
								if (alchemy.getPlace()==Alchemy.PLACE_BATTLE) {
									if (paragraph.getBattle()==null) {
										errors.add(appMessages.modelParagraphPeasfulHasBattleAlchemy(alchemy.getName()));
									}
								} else if (alchemy.isWeapon()) {
									errors.add(appMessages.modelParagraphHasNonBattleWeaponAlchemy(alchemy.getName()));
								}
							}
							if (value && alchemy.getId().equals(alId)) {
								//found
								found = alchemy;
								if (scanAchemy==false) {
									break;
								}
							}
						}
						scanAchemy = false;
						if (found==null) {
							if (errors != null) {
								errors.add(appConstants.modelParagraphUnknownReferenceAlchemyText());
							}
						} else if (found.isOnDemand()){
							//for found.isOnDemand()=false error is already reported
							if (id.startsWith(ALCHEMY_PREFIX_FROM)) {
								//from
								if (settings.isAddAlchemyToText()) {
									String from = String.valueOf(found.getFromValue());
									if (paragraphParsingHandler == null) {
										buffer.insert(start,from);
									} else {
										//add just value
										paragraphParsingHandler.addAlchemyFromValue(paragraph,from);
									}
									if (mustFromAlchemy != null) {
										if (mustFromAlchemy.contains(found)) {
											mustFromAlchemy.remove(found);
										} else {
											if (errors != null) {
												errors.add(appMessages.modelParagraphMultyReferenceAlchemyFrom(found.getName()));
											}
										}
									}
								} else {
									if (errors != null) {
										errors.add(appConstants.modelParagraphReferenceAlchemyDisabled());
									}
								}
							} else {
								//to
								String to = settings.isAddAlchemyToText() ? found.getToValue().toString() : "";
								if (paragraphParsingHandler == null) {
									buffer.insert(start,to);
								} else {
									//add value and link
									paragraphParsingHandler.addAlchemy(paragraph,to,found);
								}
								if (mustToAlchemy != null) {
									if (mustToAlchemy.contains(found)) {
										mustToAlchemy.remove(found);
									} else {
										if (errors != null) {
											errors.add(appMessages.modelParagraphMultyReferenceToAlchemyTo(found.getName()));
										}
									}
								}
							}
						}
					}
					continue;
				}
				if (BATTLE.equals(id)) {
					//insert battle description in the test and skip it
					if (condition>=0 && errors != null) {
						errors.add(appConstants.modelParagraphObjectWithConditionPrefix());
					}
					if (paragraph.getBattle()==null) {
						if (errors != null) {
							errors.add(appConstants.modelParagraphNoBattleSet());
						}
					} else if (battleFound){
						if (errors != null) {
							errors.add(appConstants.modelParagraphDoubleBattleRef());
						}
					} else {
						//normal
						Battle battle = paragraph.getBattle();
						if (paragraph.getEnemies()==null || paragraph.getEnemies().size()==0) {
							if (errors != null) {
								errors.add(appConstants.modelParagraphBattleNoEnemies());
							}
						} else if (playerMode) {
							paragraphParsingHandler.addBattle(battle,paragraph);
						} else {
							StringBuilder builder = new StringBuilder('\n');
							builder.append(battle.getName());
							builder.append('\n');
							for (NPCParams npc : paragraph.getEnemies()) {
								//TODO Rounds and Friends
								builder.append(npc.getNpc().getName());
								builder.append(' ');
								HashMap<Parameter,Integer> values = npc.getValues();
								boolean add=false;
								for (Parameter parameter : values.keySet()) {
									if (battle.dependsOn(parameter)) {
										if (add) {
											builder.append(", ");
										}
										add = true;
										builder.append(parameter.getName());
										builder.append(':');
										builder.append(values.get(parameter));
									}
								}
								builder.append('\n');
							}
							buffer.insert(start,builder.toString());
							pos = start + builder.length();
						}
						battleFound = true;
					}
					continue;
				}
				//paragraph number
				for (int i = 0; i < connections.size(); i++) {
					ParagraphConnection connection = connections.get(i);
					String url;
					Paragraph next;
					if (paragraph==connection.getFrom() && connection.getToId().equals(id)) {
						next = connection.getTo();
					} else if (paragraph==connection.getTo() && connection.getFromId().equals(id) && connection.isBothDirections()) {
						//to it, bi-direction
						next = connection.getFrom();
					} else {
						continue;
					}
					if (connection.isConditional()) {
						//connection with condition
						if (condition<0 && settings.isHiddenUsingObjects()==false) {
							if (errors != null) {
								errors.add(appMessages.modelConnectionNoCoditionDescriptor(id));
							}
							connections.remove(i);
							continue next_step;
						} else if (connection.getType() != ParagraphConnection.TYPE_NORMAL) {
							//model condition
							boolean draw=!settings.isHideNonMatchedParameterConnections();
							switch (connection.getType()) {
							case ParagraphConnection.TYPE_MODIFICATOR:
								if (connection.getModificator()==null) {
									if (errors != null) {
										errors.add(appConstants.modificatorNotSetInParagraph());
									}
								} else if (modificators == null || modificators.contains(connection.getModificator())) {
									//draw it
									draw = true;
								} else if (playerMode==false && connection.getModificator().isAbsolute()==false) {
									draw = true;
								}
								break;
							case ParagraphConnection.TYPE_NO_MODIFICATOR:
								if (connection.getModificator()==null) {
									if (errors != null) {
										errors.add(appConstants.modificatorNotSetInParagraph());
									}
								} else if (modificators == null || !modificators.contains(connection.getModificator())) {
									//draw it
									draw = true;
								}
								break;
							case ParagraphConnection.TYPE_PARAMETER_LESS:
								if (connection.getParameter()==null) {
									if (errors != null) {
										errors.add(appConstants.parameterNotSetInParagraph());
									}
								} else if (connection.getParameterValue()==null){
									if (errors != null) {
										errors.add(appConstants.parameterValueNotSetInParagraph());
									}
								} else if (parameters == null) {
									draw = true;
								} else if (parameters.containsKey(connection.getParameter()) && 
											parameters.get(connection.getParameter())<connection.getParameterValue().calculate()) {
									draw = true;
								}
								break;
							case ParagraphConnection.TYPE_PARAMETER_MORE:
								if (connection.getParameter()==null) {
									if (errors != null) {
										errors.add(appConstants.parameterNotSetInParagraph());
									}
								} else if (connection.getParameterValue()==null){
									if (errors != null) {
										errors.add(appConstants.parameterValueNotSetInParagraph());
									}
								} else if (parameters == null) {
									draw = true;
								} else if (parameters.containsKey(connection.getParameter()) && 
									parameters.get(connection.getParameter())>connection.getParameterValue().calculate()) {
									//draw it
									draw = true;
								}
								break;
							case ParagraphConnection.TYPE_VITAL_LESS:
								draw = true;
								if (connection.getStrictness()==ParagraphConnection.STRICTNESS_MUST_NOT && errors != null) {
									errors.add(appConstants.modelMustNotVitalConnectionMakesNoSense());
								}
								break;
							case ParagraphConnection.TYPE_ENEMY_VITAL_LESS:
								draw = true;
								if (connection.getStrictness()==ParagraphConnection.STRICTNESS_MUST_NOT && errors != null) {
									errors.add(appConstants.modelMustNotVitalConnectionMakesNoSense());
								}
								break;
							case ParagraphConnection.TYPE_BATTLE_ROUND_MORE:
								draw = true;
								break;
							default:
								if (errors != null) {
									errors.add("Unsupported type of connection "+connection.getType());
								}
								break;
							}
							if (draw==false && connection.getStrictness()==ParagraphConnection.STRICTNESS_MUST_NOT) {
								//reverse condition
								draw = true;
							}
							if (draw) {
								if (paragraphParsingHandler !=null) {
									if (condition>=0) {
										paragraphParsingHandler.addText(paragraph,buffer.substring(condition, start));
									}
								}									
								condition = -1;
							} else {
								if (condition>=0) {
									buffer.replace(condition, start, "");
									pos = pos - start + condition;
									condition = -1;
								}
								connections.remove(i);
								continue next_step;
							}
						} else if (hiddenUsingObjects && connection.getStrictness()!=ParagraphConnection.STRICTNESS_MUST_NOT) {
							//just remove description
							if (condition>=0) {
								buffer.replace(condition, start, "");
								pos = pos - start + condition;
								condition = -1;
							}
							connections.remove(i);
							if (preSelect != null && next==preSelect) {
								//mark it at the end
								if (postBuffer ==null) {
									postBuffer = new StringBuffer();
								}
								postBuffer.append(connection.getObject().getName());
								postBuffer.append(' ');
								if (linkProvider==null) {
									postBuffer.append(connectionMarkedPattern);
								} else {
									postBuffer.append(linkProvider.getLinkTo(paragraph,next,connection));
								}
								postBuffer.append('\n');
								if (objects != null && !objects.contains(connection.getObject()) && errors != null) {
									errors.add(appMessages.modelPlayerDoesNotHaveRequieredObject(connection.getObject().getName(),preSelect.getName()));
								}
							}
							continue next_step;
						} else if (objects != null){
							//add link only of necessary object exists
							boolean reverce = connection.getStrictness() != ParagraphConnection.STRICTNESS_MUST_NOT;
							if (objects.contains(connection.getObject())==reverce) {
								//player has this object
								if (paragraphParsingHandler !=null) {
									if (condition>=0) { 
										paragraphParsingHandler.addText(paragraph,buffer.substring(condition, start));
									} else if (pos>start){
										paragraphParsingHandler.addText(paragraph,buffer.substring(start, pos));
									}
								}									
								condition = -1;
							} else {
								//does not have
								if (condition>=0) {
									buffer.replace(condition, start, "");
									pos = pos - start + condition;
									condition = -1;
								}
								connections.remove(i);
								continue next_step;
							}
						} else {
							//just add link
//							String link = buffer.substring(condition,start).trim();
//							if (emptyConditionIsError && link.length()==0 && errors != null) {
//								errors.add(appConstants.modelTextOfUsingIsEmpty());
//							}
							condition = -1;
						}
					} else if (condition>=0 && errors != null) {
						errors.add(appMessages.modelConnectionNonConditionalHasCondtionDescriptor(id));
					}
					if (ids != null) {
						ids.add(next);
					}
					if (linkProvider==null) {
						if (next==preSelect) {
							url = connectionMarkedPattern;
						} else {
							url = connectionPattern ;
						}
					} else {
						url = linkProvider.getLinkTo(paragraph,next,connection);
					}
					if (paragraphParsingHandler == null) {
						pos = start+url.length();
						buffer.insert(start,url);
					} else {
						paragraphParsingHandler.addLinkTo(paragraph,next,connection);
					}
					connections.remove(i);
					continue next_step;
				}
				if (errors != null) {
					errors.add(appMessages.modelUnknownConnectionId(id));
				}
			}
			for (int i = 0; i < connections.size(); i++) {
				//not used connections
				ParagraphConnection connection = connections.get(i);
				if (connection.getObject()==null) {
					//missed link
					if (errors != null) {
						errors.add(appMessages.modelConnectionMissedInText(connection.getFrom().getName(),connection.getTo().getName()));
					}
				} else if (settings.isHiddenUsingObjects()) {
					//condition, leave "as is"
				} else {
					//missed link
					if (errors != null) {
						errors.add(appMessages.modelConnectionMissedInText(connection.getFrom().getName(),connection.getTo().getName()));
					}
				}
			}
			if (errors != null) {
				if (paragraph.getBattle() != null && battleFound==false) {
					errors.add(appConstants.modelParagraphNoBattleRef());
				}
				if (mustUseModificators != null && mustUseModificators.size()>0) {
					errors.add(appConstants.modelParagraphUnreferredModificators());
				}
			}
			if (postBuffer != null) {
				buffer.append('\n');
				buffer.append('\n');
				buffer.append(postBuffer);
			}
			if (errors != null && (paragraph.getGotObjects().size() != objectDetected)) {
				errors.add(appConstants.modelDiffentQuantitiesItemsInTextAndData());
			}
			return buffer.toString();//.replace("\n","<br/>");
		}

		public ParagraphDescriptionLinkProvider getLinkProvider() {
			return linkProvider;
		}

		public void setLinkProvider(ParagraphDescriptionLinkProvider linkProvider) {
			this.linkProvider = linkProvider;
		}

		public Set<ObjectBean> getObjects() {
			return objects;
		}

		public void setObjects(Set<ObjectBean> objects) {
			this.objects = objects;
		}

		public boolean isHiddenUsingObjects() {
			return hiddenUsingObjects;
		}

		public void setHiddenUsingObjects(boolean hiddenUsingObjects) {
			this.hiddenUsingObjects = hiddenUsingObjects;
		}

		public Map<Parameter, Integer> getParameters() {
			return parameters;
		}

		public void setParameters(Map<Parameter, Integer> parameters) {
			this.parameters = parameters;
		}

		public Set<Modificator> getModificators() {
			return modificators;
		}

		public void setModificators(Set<Modificator> modificators) {
			this.modificators = modificators;
		}
		
	}

	public FullParagraphDescriptonBuilder getFullParagraphDescriptonBuilder() {
		return new FullParagraphDescriptonBuilder();
	}

	public int getVersion() {
		return version==0 ? 100 : version;
	}

	public int getModelVersion() {
		return HI_VERSION*100+LO_VERSION;
	}

	public int getVersionHi() {
		return (int)(getVersion()/100);
	}

	public int getVersionLo() {
		return getVersion() % 100;
	}

	public String getGameId() {
		if (gameId==null) {
			gameId = String.valueOf(new Date().getTime());
		}
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public Paragraph getParagrapByID(String id) {
		for (int i = 0; i < paragraphs.size(); i++) {
			Paragraph paragraph = paragraphs.get(i);
			if (paragraph.getId().equals(id)) {
				return paragraph;
			}
		}
		return null;
	}

	public ObjectBean getObjectById(String id) {
		for (int i = 0; i < objects.size(); i++) {
			ObjectBean bean = objects.get(i);
			if (bean.getId().equals(id)) {
				return bean;
			}
		}
		return null;
	}

	public Picture getPictureByID(String id) {
		for (int i = 0; i < pictures.size(); i++) {
			Picture bean = pictures.get(i);
			if (bean.getId().equals(id)) {
				return bean;
			}
		}
		return null;
	}

	public void stopSound() {
		if (contentPlayer != null) {
			contentPlayer.stopSound();
		}
	}

	public void stopBackgroundSound() {
		if (contentPlayer != null) {
			contentPlayer.stopBackgroundSound();
		}
	}

	public void playBackground(Sound sound) {
		if (contentPlayer != null) {
			contentPlayer.playBackgroundSound(sound.getUrl());
		}
	}

	public Sound getSoundByID(String id) {
		for (int i = 0; i < sounds.size(); i++) {
			Sound bean = sounds.get(i);
			if (bean.getId().equals(id)) {
				return bean;
			}
		}
		return null;
	}

	public Paragraph getCurrentParagraph() {
		return currentParagraph;
	}

	public void refreshParagraphs() {
		fireParagraphEvent(EVENT_REFRESH_ALL, null, null);
	}

	public String getGameKey() {
		return gameKey;
	}

	public void showInfo(AbstractParameter parameter) {
		fireParametersEvent(EVENT_SHOW_INFO, parameter, null);
	}
	
	public void showInfo(ObjectBean object) {
		fireObjectEvent(EVENT_SHOW_INFO, object, null);
	}

	public void showInfo(Picture object) {
		firePictureEvent(EVENT_SHOW_INFO, object, null);
	}

	public void showInfo(Sound object) {
		fireSoundEvent(EVENT_SHOW_INFO, object, null);
	}
	
	public ArrayList<AbstractParameter> getParameters() {
		return parameters;
	}

	public boolean removeParameter(AbstractParameter parameter, ParameterListener sender) {
		for (AbstractParameter par : parameters) {
			if (par !=parameter && par.dependsOn(parameter)) {
				Window.alert(appMessages.cannotRemoveParameter(parameter.getName(),par.getName()));
				return false;
			}
		}
		for (Paragraph paragraph : paragraphs) {
			if (paragraph.dependsOn(parameter)) {
				Window.alert(appMessages.parameterIsUsedInParagraph(paragraph.getName()));
				return false;
			}
		}
		for (ParagraphConnection connection : paragraphConnections) {
			if (connection.dependsOn(parameter)) {
				Window.alert(appMessages.parameterIsUsedInParagraphConnection(connection.getFrom().getName(),connection.getTo().getName()));
				return false;
			}
		}
		parameters.remove(parameter);
		fireParametersEvent(EVENT_DELETE, parameter, sender);
		return true;
	}

	public void updateParameter(AbstractParameter parameter, ParameterListener sender) {
		fireParametersEvent(EVENT_UPDATE, parameter, sender);
	}

	public void selectParameter(AbstractParameter parameter, ParameterListener sender) {
		fireParametersEvent(EVENT_SELECTED, parameter, sender);
	}

	public void refreshParameters() {
		fireParametersEvent(EVENT_REFRESH_ALL, null, null);
	}

	public void updateCommercialText(String text, PlotListener listener) {
		commercialText = text;
		firePlotEvent(EVENT_UPDATE,SOURCE_COMMERCIAL_TEXT,listener);
	}

	public void updateFirstPageDemoInfoText(String text, PlotListener listener) {
		demoInfoText = text;
		firePlotEvent(EVENT_UPDATE,SOURCE_DEMO_INFO_TEXT,listener);
	}

	public void refreshObjects() {
		fireObjectEvent(EVENT_REFRESH_ALL, null, null);
	}

	public void refreshPictures() {
		firePictureEvent(EVENT_REFRESH_ALL, null, null);
	}

	public void refreshSounds() {
		fireSoundEvent(EVENT_REFRESH_ALL, null, null);
	}


}
