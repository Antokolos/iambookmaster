package com.iambookmaster.client.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.exceptions.JSONException;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;

public class Paragraph implements Serializable{

	private static final long serialVersionUID = 1L;

	private static final String JSON_BACKGROUND_IMAGES = "imgs";
	private static final String JSON_BACKGROUND_SOUNDS = "snds";
	private static final String JSON_SOUNDS = "snd";
	private static final String JSON_TOP_IMAGES = "tp";
	private static final String JSON_BOTTOM_IMAGES = "bt";
	private static final String JSON_BATTLE = "a";
	@Deprecated
	private static final String JSON_ENEMIES = "b";
	private static final String JSON_MODIFICATORS = "c";
	private static final String JSON_PARAMETERS = "d";
	private static final String JSON_ALCHEMY = "e";
	private static final String JSON_SPRITES = "f";
	private static final String JSON_NUMBER = "n";
	private static final String JSON_ENEMIES_EXT = "k";
	private static final String JSON_OBJECT_LOST_ID = "l";
	private static final String JSON_FIGHT_TOGETHER = "m";
	private static final String JSON_X = "x";
	private static final String JSON_Y = "y";
	private static final String JSON_ID = "id";
	private static final String JSON_NAME = "name";
	private static final String JSON_TYPE = "type";
	private static final String JSON_DESCRIPTION = "text";
	private static final String JSON_OBJECT_ID = "obj";
	private static final String JSON_STATUS = "stat";
	private static final String JSON_COMMERCIAL = "j";
	
	public static final char CHAR_ID_SUBJECT = '#';
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_FAIL = 1;
	public static final int TYPE_SUCCESS = 2;
	public static final int TYPE_START = 3;
	public static final int TYPE_COMMERCIAL = 4;



	private String id;
	
	private String name;

	private Paragraph owner;
	
	private int x;
	
	private int y;

	private int type;
	
	private String description;
	
	private int status;
	
	private int number;
	
	private HashSet<ObjectBean> gotObjects;
	private HashSet<ObjectBean> lostObjects;
	
	private ArrayList<Picture> backgroundImages;
	private ArrayList<Picture> topImages;
	private ArrayList<Picture> bottomImages;
	private ArrayList<Sound> backgroundSounds;
	private ArrayList<Sound> sounds;
	private ArrayList<NPCParams> enemies;
	private Battle battle;
	
	private LinkedHashMap<Parameter,ParametersCalculation> changeParameters;
	private LinkedHashMap<Modificator,Boolean> changeModificators;
	private LinkedHashMap<Alchemy,Boolean> alchemy;
	private boolean commercial;

	public boolean isCommercial() {
		return commercial;
	}
	public void setCommercial(boolean commercial) {
		this.commercial = commercial;
	}
	
//	public ArrayList<Integer> getEnemyRounds() {
//		if (enemies == null) {
//			enemyRounds = null;
//		} else if (enemyRounds == null) {
//			enemyRounds = new ArrayList<Integer>();
//			for (int i = 0; i < enemies.size(); i++) {
//				enemyRounds.add(0);
//			}
//		} else if (enemies.size() > enemyRounds.size()) {
//			while (enemies.size() > enemyRounds.size()) {
//				enemyRounds.add(0);
//			}
//		} else if (enemies.size() < enemyRounds.size()) {
//			while (enemies.size() < enemyRounds.size()) {
//				enemyRounds.remove(enemyRounds.size()-1);
//			}
//		}
//		return enemyRounds;
//	}
	
	public boolean dependsOn(AbstractParameter parameter) {
		if (changeParameters != null && changeParameters.containsKey(parameter)) {
			return true;
		}
		if (changeModificators != null && changeModificators.containsKey(parameter)) {
			return true;
		}
		if (alchemy != null && alchemy.containsKey(parameter)) {
			return true;
		}
		if (enemies != null) {
			for (NPCParams enemy : enemies) {
				if (enemy.getNpc().equals(parameter)) {
					return true;
				}
			}
		}
		if (battle==parameter) {
			return true;
		}
		return false;
	}
	
	private transient int nextBackgroundImageCounter;
	private transient int nextTopImageCounter;
	private transient int nextBottomImageCounter;
	private transient int nextSoundCounter;
	private transient int nextBackgroundSoundCounter;

	private ArrayList<Sprite> sprites;

	private boolean fightTogether;
	
	public boolean dependsOn(Sound sound) {
		return (sounds != null && sounds.contains(sound)) || 
			   (backgroundSounds != null && backgroundSounds.contains(sound));
	}

	public boolean dependsOn(Picture picture) {
		return (backgroundImages != null && backgroundImages.contains(picture)) || 
			   (topImages != null && topImages.contains(picture)) ||
			   (bottomImages != null && bottomImages.contains(picture));
	}

	public Set<ObjectBean> getGotObjects() {
		if (gotObjects==null) {
			gotObjects = new HashSet<ObjectBean>();
		}
		return gotObjects;
	}

	public void setGotObjects(HashSet<ObjectBean> gotObjects) {
		this.gotObjects = gotObjects;
	}

	public void setLostObjects(HashSet<ObjectBean> lostObjects) {
		this.lostObjects = lostObjects;
	}

	public Set<ObjectBean> getLostObjects() {
		if (lostObjects==null) {
			lostObjects = new HashSet<ObjectBean>();
		}
		return lostObjects;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Paragraph getOwner() {
		return owner;
	}

	public void setOwner(Paragraph owner) {
		this.owner = owner;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		if (x==0) {
			x = 0;
		}
		this.x = x;
	}

	public boolean isFail() {
		return type==TYPE_FAIL;
	}
	public boolean isSuccess() {
		return type==TYPE_SUCCESS;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void toJSON(JSONBuilder builder, int export) {
		builder.newRow();
		builder.field(JSON_ID, id);
		if (gotObjects != null && gotObjects.size()>0) {
			if (gotObjects.size()==1) {
				builder.field(JSON_OBJECT_ID, gotObjects.iterator().next().getId());
			} else {
				StringBuilder buffer = new StringBuilder();
				for (ObjectBean object : gotObjects) {
					if (buffer.length()>0) {
						buffer.append(',');
					}
					buffer.append(object.getId());
				}
				builder.field(JSON_OBJECT_ID, buffer.toString());
			}
		}
		if (lostObjects != null && lostObjects.size()>0) {
			if (lostObjects.size()==1) {
				builder.field(JSON_OBJECT_LOST_ID, lostObjects.iterator().next().getId());
			} else {
				StringBuilder buffer = new StringBuilder();
				for (ObjectBean object : lostObjects) {
					if (buffer.length()>0) {
						buffer.append(',');
					}
					buffer.append(object.getId());
				}
				builder.field(JSON_OBJECT_LOST_ID, buffer.toString());
			}
		}
		if (type != TYPE_NORMAL) {
			builder.field(JSON_TYPE, type);
		}
		if (export==Model.EXPORT_ALL) {
			builder.field(JSON_NAME, name);
			builder.field(JSON_X, x);
			builder.field(JSON_Y, y);
			builder.field(JSON_NUMBER, number);
			builder.field(JSON_STATUS, status);
		}
		if (hasBackgroundImages()) {
			if (backgroundImages.size()==1) {
				builder.field(JSON_BACKGROUND_IMAGES, backgroundImages.get(0).getId());
			} else {
				StringBuilder buffer = new StringBuilder();
				for (int i = 0; i < backgroundImages.size(); i++) {
					if (buffer.length()>0) {
						buffer.append(',');
					}
					buffer.append(backgroundImages.get(i).getId());
				}
				builder.field(JSON_BACKGROUND_IMAGES, buffer.toString());
			}
		}
		if (hasTopImages()) {
			if (topImages.size()==1) {
				builder.field(JSON_TOP_IMAGES, topImages.get(0).getId());
			} else {
				StringBuilder buffer = new StringBuilder();
				for (int i = 0; i < topImages.size(); i++) {
					if (buffer.length()>0) {
						buffer.append(',');
					}
					buffer.append(topImages.get(i).getId());
				}
				builder.field(JSON_TOP_IMAGES, buffer.toString());
			}
		}
		if (hasBottomImages()) {
			if (bottomImages.size()==1) {
				builder.field(JSON_BOTTOM_IMAGES, bottomImages.get(0).getId());
			} else {
				StringBuilder buffer = new StringBuilder();
				for (int i = 0; i < bottomImages.size(); i++) {
					if (buffer.length()>0) {
						buffer.append(',');
					}
					buffer.append(bottomImages.get(i).getId());
				}
				builder.field(JSON_BOTTOM_IMAGES, buffer.toString());
			}
		}
		if (backgroundSounds != null && backgroundSounds.size()>0) {
			if (backgroundSounds.size()==1) {
				builder.field(JSON_BACKGROUND_SOUNDS, backgroundSounds.get(0).getId());
			} else {
				StringBuilder buffer = new StringBuilder();
				for (int i = 0; i < backgroundSounds.size(); i++) {
					if (buffer.length()>0) {
						buffer.append(',');
					}
					buffer.append(backgroundSounds.get(i).getId());
				}
				builder.field(JSON_BACKGROUND_SOUNDS, buffer.toString());
			}
		}
		if (sounds != null && sounds.size()>0) {
			if (sounds.size()==1) {
				builder.field(JSON_SOUNDS, sounds	.get(0).getId());
			} else {
				StringBuilder buffer = new StringBuilder();
				for (int i = 0; i < sounds.size(); i++) {
					if (buffer.length()>0) {
						buffer.append(',');
					}
					buffer.append(sounds.get(i).getId());
				}
				builder.field(JSON_SOUNDS, buffer.toString());
			}
		}
		if (battle != null) {
			builder.field(JSON_BATTLE, battle.getId());
		}
		if (enemies != null && enemies.size()>0) {
			JSONBuilder jsonBuilder = builder.getInstance();
			for (int i = 0; i < enemies.size(); i++) {
				enemies.get(i).toJSON(jsonBuilder,export);
			}
			builder.childArray(JSON_ENEMIES_EXT, jsonBuilder);
			if (fightTogether) {
				builder.field(JSON_FIGHT_TOGETHER, 1);
			}
		}
		if (changeModificators != null && changeModificators.size() > 0) {
			//save Modificators
			StringBuilder buffer = new StringBuilder();
			for (Modificator modificator : changeModificators.keySet()) {
				if (buffer.length()>0) {
					buffer.append(',');
				}
				boolean value = changeModificators.get(modificator);
				buffer.append(value ? '+':'-'); 
				buffer.append(modificator.getId());
			}
			builder.field(JSON_MODIFICATORS, buffer.toString());
		}
		
		if (alchemy != null && alchemy.size()>0) {
			//save Alchemy
			StringBuilder buffer = new StringBuilder();
			for (Alchemy alc : alchemy.keySet()) {
				if (buffer.length()>0) {
					buffer.append(',');
				}
				boolean value = alchemy.get(alc);
				buffer.append(value ? '+':'-'); 
				buffer.append(alc.getId());
			}
			builder.field(JSON_ALCHEMY, buffer.toString());
			
		}
		
		if (sprites != null && sprites.size()>0) {
			//save Sprites
			JSONBuilder subBuilder = builder.getInstance();
			for (Sprite sprite : sprites) {
				sprite.toJSON(subBuilder, export);
			}
			builder.childArray(JSON_SPRITES, subBuilder);
		}
		
		if (changeParameters != null && changeParameters.size() > 0) {
			//save Parameters
			JSONBuilder subBuilder = builder.getInstance();
			for (Parameter parameter : changeParameters.keySet()) {
				ParametersCalculation calculation = changeParameters.get(parameter);
				calculation.toJSON(subBuilder, export);
				subBuilder.field(ParametersCalculation.JSON_ACCEPTOR, parameter.getId()); 
			}
			builder.childArray(JSON_PARAMETERS, subBuilder);
		}
		builder.field(JSON_DESCRIPTION, getDescription());
		if (commercial) {
			builder.field(JSON_COMMERCIAL, 1);
		}
	}

	public static ArrayList<Paragraph> fromJSArray(Object object,ArrayList<ObjectBean> objects,HashMap<String,Picture> pictures,HashMap<String,Sound> sounds,JSONParser parser,AppMessages appMessages, HashMap<String, AbstractParameter> parametersMap) throws JSONException{
		int l = parser.length(object);
		ArrayList<Paragraph> list = new ArrayList<Paragraph>();
		for (int i = 0; i < l; i++) {
			Object row = parser.getRow(object, i);
			list.add(fromJS(row,objects,pictures,sounds,parser,appMessages,parametersMap));
		}
		return list;
	}

	public static Paragraph fromJS(Object object,ArrayList<ObjectBean> objects,HashMap<String,Picture> pictures,HashMap<String,Sound> sounds,JSONParser parser, AppMessages appMessages, HashMap<String, AbstractParameter> parametersMap) throws JSONException{
		Paragraph paragraph = new Paragraph();
		paragraph.id = parser.propertyString(object, JSON_ID);
		paragraph.name = parser.propertyNoCheckString(object, JSON_NAME);
		paragraph.x = parser.propertyNoCheckInt(object, JSON_X);
		paragraph.number = parser.propertyNoCheckInt(object, JSON_NUMBER);
		paragraph.y = parser.propertyNoCheckInt(object, JSON_Y);
		paragraph.type = parser.propertyNoCheckInt(object, JSON_TYPE);
		paragraph.status = parser.propertyNoCheckInt(object, JSON_STATUS);
		paragraph.description = parser.propertyNoCheckString(object, JSON_DESCRIPTION);
		paragraph.commercial = parser.propertyNoCheckInt(object, JSON_COMMERCIAL)>0;
		if (paragraph.description==null) {
			paragraph.description = "";
		}
		String objectId = parser.propertyNoCheckString(object, JSON_OBJECT_ID);
		if (objectId != null) {
			String ids[] = objectId.split(",");
			scan_obj1:
			for (ObjectBean bean:objects) {
				for (String id : ids) {
					if (bean.getId().equals(id)) {
						//found
						if (paragraph.gotObjects==null) {
							paragraph.gotObjects = new HashSet<ObjectBean>(ids.length);
						}
						paragraph.gotObjects.add(bean);
						if (paragraph.gotObjects.size()==ids.length) {
							//found all
							break scan_obj1;
						}
						break;
					}
				}
			}
			if (paragraph.gotObjects==null || paragraph.gotObjects.size()<ids.length) {
				throw new JSONException(appMessages.modelUnknownObjectsIDs(objectId));
			}
		}
		objectId = parser.propertyNoCheckString(object, JSON_OBJECT_LOST_ID);
		if (objectId != null) {
			String ids[] = objectId.split(",");
			scan_obj1:
			for (ObjectBean bean:objects) {
				for (String id : ids) {
					if (bean.getId().equals(id)) {
						//found
						if (paragraph.lostObjects==null) {
							paragraph.lostObjects = new HashSet<ObjectBean>(ids.length);
						}
						paragraph.lostObjects.add(bean);
						if (paragraph.lostObjects.size()==ids.length) {
							//found all
							break scan_obj1;
						}
						break;
					}
				}
			}
			if (paragraph.lostObjects==null || paragraph.lostObjects.size()<ids.length) {
				throw new JSONException(appMessages.modelUnknownObjectsIDs(objectId));
			}
		}
		//restore background images
		objectId = parser.propertyNoCheckString(object, JSON_BACKGROUND_IMAGES);
		if (objectId != null) {
			paragraph.backgroundImages = new ArrayList<Picture>();
			String[] ids = objectId.split(",");
			for (String id : ids) {
				Picture bean = pictures.get(id);
				if (bean == null) {
					throw new JSONException("Unknown Image with ID="+id);
				} else {
					//found
					paragraph.backgroundImages.add(bean);
				}
			}
		}
		//restore top images
		objectId = parser.propertyNoCheckString(object, JSON_TOP_IMAGES);
		if (objectId != null) {
			paragraph.topImages = new ArrayList<Picture>();
			String[] ids = objectId.split(",");
			for (String id : ids) {
				Picture bean = pictures.get(id);
				if (bean == null) {
					throw new JSONException("Unknown Image with ID="+id);
				} else {
					//found
					paragraph.topImages.add(bean);
				}
			}
		}
		//restore bottom images
		objectId = parser.propertyNoCheckString(object, JSON_BOTTOM_IMAGES);
		if (objectId != null) {
			paragraph.bottomImages = new ArrayList<Picture>();
			String[] ids = objectId.split(",");
			for (String id : ids) {
				Picture bean = pictures.get(id);
				if (bean == null) {
					throw new JSONException("Unknown Image with ID="+id);
				} else {
					//found
					paragraph.bottomImages.add(bean);
				}
			}
		}
		
		//restore sprites
		Object rows = parser.propertyNoCheck(object, JSON_SPRITES);
		if (rows != null) {
			int l = parser.length(rows);
			paragraph.sprites = new ArrayList<Sprite>(l);
			for (int i = 0; i < l; i++) {
				Object row = parser.getRow(rows, i);
				Sprite sprite = new Sprite();
				sprite.fromJSON(row, parser, pictures);
				paragraph.sprites.add(sprite);
			}
		}
		
		//restore sounds
		objectId = parser.propertyNoCheckString(object, JSON_BACKGROUND_SOUNDS);
		if (objectId != null) {
			paragraph.backgroundSounds = new ArrayList<Sound>();
			String[] ids = objectId.split(",");
			for (String id : ids) {
				Sound bean = sounds.get(id);
				if (bean == null) {
					throw new JSONException("Unknown Sound with ID="+id);
				} else {
					//found
					paragraph.backgroundSounds.add(bean);
				}
			}
		}
		//restore effects
		objectId = parser.propertyNoCheckString(object, JSON_SOUNDS);
		if (objectId != null) {
			paragraph.sounds = new ArrayList<Sound>();
			String[] ids = objectId.split(",");
			for (String id : ids) {
				Sound bean = sounds.get(id);
				if (bean != null) {
					//found
					paragraph.sounds.add(bean);
				}
			}
		}
		
		//battle
		objectId = parser.propertyNoCheckString(object, JSON_BATTLE);
		if (objectId != null) {
			AbstractParameter parameter = parametersMap.get(objectId);
			if (parameter instanceof Battle) {
				paragraph.battle = (Battle) parameter;
			} else {
				throw new JSONException("Does not exist Battle with ID="+objectId);
			}
		}
		//restore enemies
		Object data = parser.propertyNoCheck(object, JSON_ENEMIES_EXT);
		if (data==null) {
			//old version
			objectId = parser.propertyNoCheckString(object, JSON_ENEMIES);
			if (objectId != null) {
				paragraph.enemies = new ArrayList<NPCParams>();
				String[] ids = objectId.split(",");
				for (String id : ids) {
					AbstractParameter parameter = parametersMap.get(id);
					if (parameter instanceof NPC) {
						//found
						paragraph.enemies.add(new NPCParams((NPC)parameter));
					} else {
						throw new JSONException("Unknown NPC with ID="+id);
					}
				}
			}
		} else {
			//new version
			int l = parser.length(data);
			paragraph.enemies = new ArrayList<NPCParams>(l);
			for (int i = 0; i < l; i++) {
				Object row = parser.getRow(data, i);
				paragraph.enemies.add(NPCParams.fromJS(row,parser,parametersMap));
			}
		}
		paragraph.fightTogether = parser.propertyNoCheckInt(object,JSON_FIGHT_TOGETHER) > 0;
		
		//restore modificators
		objectId = parser.propertyNoCheckString(object, JSON_MODIFICATORS);
		if (objectId != null) {
			paragraph.changeModificators = new LinkedHashMap<Modificator, Boolean>();
			String[] ids = objectId.split(",");
			for (String id : ids) {
				char val = id.charAt(0);
				id = id.substring(1);
				AbstractParameter parameter = parametersMap.get(id);
				if (parameter instanceof Modificator) {
					//found
					paragraph.changeModificators.put((Modificator) parameter,val=='+');
				} else {
					throw new JSONException("Unknown Modificator with ID="+id);
				}
			}
		}
		
		//restore alchemy
		objectId = parser.propertyNoCheckString(object, JSON_ALCHEMY);
		if (objectId != null) {
			paragraph.alchemy = new LinkedHashMap<Alchemy, Boolean>();
			String[] ids = objectId.split(",");
			for (String id : ids) {
				char val = id.charAt(0);
				id = id.substring(1);
				AbstractParameter parameter = parametersMap.get(id);
				if (parameter instanceof Alchemy) {
					//found
					paragraph.alchemy.put((Alchemy) parameter,val=='+');
				} else {
					throw new JSONException("Unknown Alchemy with ID="+id);
				}
			}
		}
		
		//restore parameters
		rows = parser.propertyNoCheck(object, JSON_PARAMETERS);
		if (rows != null) {
			int l = parser.length(rows);
			paragraph.changeParameters = new LinkedHashMap<Parameter, ParametersCalculation>(l);
			for (int i = 0; i < l; i++) {
				Object row = parser.getRow(rows, i);
				ParametersCalculation calculation = new ParametersCalculation();
				calculation.fromJSON(row, parser, parametersMap);
				objectId = parser.propertyString(row, ParametersCalculation.JSON_ACCEPTOR);
				AbstractParameter parameter = parametersMap.get(objectId);
				if (parameter instanceof Parameter) {
					//found
					paragraph.changeParameters.put((Parameter) parameter,calculation);
				} else {
					throw new JSONException("Unknown Parameter with ID="+objectId);
				}
			}
		}
		
		return paragraph;
	}

	public int getType() {
		return type;
	}

	public String getDescription() {
		if (description==null) {
			return "";
		} else {
			return description;
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public ArrayList<Picture> getBackgroundImages() {
		if (backgroundImages==null) {
			backgroundImages = new ArrayList<Picture>();
		}
		return backgroundImages;
	}

	public void setBackgroundImages(ArrayList<Picture> backgroundImages) {
		this.backgroundImages = backgroundImages;
	}

	public ArrayList<Sound> getBackgroundSounds() {
		if (backgroundSounds==null) {
			backgroundSounds = new ArrayList<Sound>();
		}
		return backgroundSounds;
	}

	public void setBackgroundSounds(ArrayList<Sound> backgroundSounds) {
		this.backgroundSounds = backgroundSounds;
	}

	public ArrayList<Sound> getSounds() {
		if (sounds==null) {
			sounds = new ArrayList<Sound>();
		}
		return sounds;
	}

	public void setSounds(ArrayList<Sound> sounds) {
		this.sounds = sounds;
	}

	public boolean hasBackgroundImages() {
		return backgroundImages != null && backgroundImages.size()>0;
	}
	public boolean hasSounds() {
		return sounds != null && sounds.size()>0;
	}

	public boolean hasBottomImages() {
		return bottomImages != null && bottomImages.size()>0;
	}
	
	public ArrayList<Picture> getBottomImages() {
		if (bottomImages == null) {
			bottomImages = new ArrayList<Picture>();
		}
		return bottomImages;
	}

	public void setBottomImages(ArrayList<Picture> bottomImages) {
		this.bottomImages = bottomImages;
	}
	
	public ArrayList<Sprite> getSprites() {
		if (sprites == null) {
			sprites = new ArrayList<Sprite>();
		}
		return sprites;
	}

	public void setSprites(ArrayList<Sprite> sprites) {
		this.sprites = sprites;
	}
	
	public boolean hasTopImages() {
		return topImages != null && topImages.size()>0;
	}

	public ArrayList<Picture> getTopImages() {
		if (topImages == null) {
			topImages = new ArrayList<Picture>();
		}
		return topImages;
	}

	public void setTopImages(ArrayList<Picture> topImages) {
		this.topImages = topImages;
	}

	public boolean hasBackgroundSounds() {
		return backgroundSounds != null && backgroundSounds.size()>0;
	}

	public Picture getNextBackgroundImage() {
		int s = backgroundImages.size();
		if (s==1) {
			return backgroundImages.get(0);
		}
		if (nextBackgroundImageCounter>=s) {
			nextBackgroundImageCounter=0;
		}
		return backgroundImages.get(nextBackgroundImageCounter++);
	}

	public Picture getNextTopImage() {
		int s = topImages.size();
		if (s==1) {
			return topImages.get(0);
		}
		if (nextTopImageCounter>=s) {
			nextTopImageCounter=0;
		}
		return topImages.get(nextTopImageCounter++);
	}

	public Picture getNextBottomImage() {
		int s = bottomImages.size();
		if (s==1) {
			return bottomImages.get(0);
		}
		if (nextBottomImageCounter>=s) {
			nextBottomImageCounter=0;
		}
		return bottomImages.get(nextBottomImageCounter++);
	}

	public Sound getNextSound() {
		int s = sounds.size();
		if (s==1) {
			return sounds.get(0);
		}
		if (nextSoundCounter>=s) {
			nextSoundCounter=0;
		}
		return sounds.get(nextSoundCounter++);
	}

	public Sound getNextBackgroundSound() {
		int s = backgroundSounds.size();
		if (s==1) {
			return backgroundSounds.get(0);
		}
		if (nextBackgroundSoundCounter>=s) {
			nextBackgroundSoundCounter=0;
		}
		return backgroundSounds.get(nextBackgroundSoundCounter++);
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Battle getBattle() {
		return battle;
	}

	public void setBattle(Battle battle) {
		this.battle = battle;
	}

	public ArrayList<NPCParams> getEnemies() {
		return enemies;
	}

	public void setEnemies(ArrayList<NPCParams> enemies) {
		this.enemies = enemies;
	}

	public LinkedHashMap<Parameter, ParametersCalculation> getChangeParameters() {
		return changeParameters;
	}

	public void setChangeParameters(LinkedHashMap<Parameter, ParametersCalculation> changeParameters) {
		this.changeParameters = changeParameters;
	}

	public LinkedHashMap<Modificator, Boolean> getChangeModificators() {
		return changeModificators;
	}

	public LinkedHashMap<Alchemy, Boolean> getAlchemy() {
		return alchemy;
	}

	public void setChangeModificators(LinkedHashMap<Modificator, Boolean> changeModificators) {
		this.changeModificators = changeModificators;
	}

	public void addChangeModificator(Modificator modificator, boolean value) {
		if (changeModificators==null) {
			changeModificators = new LinkedHashMap<Modificator, Boolean>();
		}
		changeModificators.put(modificator,value);
	}

	public void addAlchemy(Alchemy alc, boolean value) {
		if (alchemy==null) {
			alchemy = new LinkedHashMap<Alchemy, Boolean>();
		}
		alchemy.put(alc,value);
	}

	public void addChangeParameter(Parameter parameter, ParametersCalculation calculation) {
		if (changeParameters==null) {
			changeParameters = new LinkedHashMap<Parameter, ParametersCalculation>();
		}
		changeParameters.put(parameter,calculation);
	}

	public boolean hasChangeModificator(Modificator modificator) {
		return changeModificators != null && changeModificators.containsKey(modificator);
	}

	public boolean hasChangeParameter(Parameter parameter) {
		return changeParameters != null && changeParameters.containsKey(parameter);
	}

	public boolean hasAlchemy(Alchemy alc) {
		return alchemy != null && alchemy.containsKey(alc);
	}

	public void setAlchemy(LinkedHashMap<Alchemy, Boolean> alchemy) {
		this.alchemy = alchemy;
	}

	public boolean hasSprites() {
		return sprites != null && sprites.size()>0;
	}
	public boolean dependsOn(ObjectBean object) {
		return (gotObjects != null && gotObjects.contains(object)) || (lostObjects != null && lostObjects.contains(object));
	}
	
	public void setFightTogether(boolean fightTogether) {
		this.fightTogether = fightTogether;
	}
	public boolean isFightTogether() {
		return fightTogether;
	}
	
}
