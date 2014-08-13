package com.iambookmaster.client.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.google.gwt.user.client.Window;
import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.DiceValue;
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
import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ParameterListener;

public class ModelPersist extends Model {
	
	public ModelPersist(AppConstants appConstants, AppMessages appMessages) {
		super(appConstants, appMessages);
	}

	public void setGameKey(String gameKey) {
		this.gameKey = gameKey;
	}

	public String toJSON(int export, JSONBuilder result) {
		result.newRow();
		
		//parameters
		JSONBuilder builder = result.getInstance();
		int o=0;
		for (AbstractParameter bean : parameters) {
			bean.setOrder(o++);
			bean.toJSON(builder,export);
		}
		result.childArray(JSON_PARAMETERS, builder);		
		
		//objects
		builder = result.getInstance();
		for (int i = 0; i < objects.size(); i++) {
			ObjectBean bean = objects.get(i);
			bean.toJSON(builder,export);
		}
		result.childArray(JSON_OBJECTS, builder);		
		
		//images
		builder = result.getInstance();
		for (int i = 0; i < pictures.size(); i++) {
			Picture bean = pictures.get(i);
			bean.toJSON(builder,export);
		}
		result.childArray(JSON_IMAGES, builder);
		
		//sounds
		builder = result.getInstance();
		for (int i = 0; i < sounds.size(); i++) {
			Sound bean = sounds.get(i);
			bean.toJSON(builder,export);
		}
		result.childArray(JSON_SOUNDS, builder);
		
		//locations
		builder = result.getInstance();
		for (int i = 0; i < paragraphs.size(); i++) {
			Paragraph location = paragraphs.get(i);
			location.toJSON(builder,export);
		}
		result.childArray(JSON_PARAGRAPHS, builder);
		
		//connections
		builder = result.getInstance();
		for (int i = 0; i < paragraphConnections.size(); i++) {
			ParagraphConnection connection = paragraphConnections.get(i);
			connection.toJSON(builder,export);
		}
		result.childArray(JSON_PARAGRAPH_CONNECTION, builder);			
		if (export==EXPORT_ALL) {
			result.field(JSON_NEXT_ID, nextId);
			result.field(JSON_NEXT_OBJ_ID, nextObjId);
			result.field(JSON_NEXT_CONTENT_ID, nextContentId);
			result.field(JSON_PLOT, plot);
			result.field(JSON_BOOK_RULES, bookRules);
		}
		result.field(JSON_PLAYER_RULES, playerRules);
		result.field(JSON_COMMERCIAL_TEXT, commercialText);
		result.field(JSON_DEMO_INFO_TEXT, demoInfoText);
		
		if (startParagraph != null) {
			result.field(JSON_START_PARAGRAPH, startParagraph.getId());
		}
		result.field(JSON_GAME_ID, getGameId());
		result.field(JSON_GAME_KEY, getGameKey());
		//settings
		builder = result.getInstance();
		settings.toJSON(builder,export);
		result.child(JSON_SETTINGS, builder);
		result.field(JSON_VERSION, getModelVersion());
		return result.toString();
	}

	public void addModel(Paragraph paragraph, Model model) {
		//add all paragraphs
		ArrayList<Paragraph> list = model.getParagraphs();
		for (int i = 0; i < list.size(); i++) {
			Paragraph par = list.get(i);
			if (par.getType()==Paragraph.TYPE_START) {
				//clear this flag
				par.setType(Paragraph.TYPE_NORMAL);
			}
			par.setX(par.getX()+paragraph.getX());
			par.setY(par.getY()+paragraph.getY());
			par.setId(getNextParagraphId());
			getParagraphs().add(par);
		}
		list = null;
		//add connections
		ArrayList<ParagraphConnection> conns = model.getParagraphConnections();
		for (int i = 0; i < conns.size(); i++) {
			getParagraphConnections().add(conns.get(i));
		}
		conns = null;
		//add objects
		ArrayList<ObjectBean> currObjs = getObjects();
		ArrayList<ObjectBean> objs = model.getObjects();
		next_obj:
		for (int i = 0; i < objs.size(); i++) {
			ObjectBean object = objs.get(i);
			String name = object.getName().toLowerCase().trim();
			for (int j = 0; j < currObjs.size(); j++) {
				ObjectBean obj = currObjs.get(j);
				if (obj.getName().toLowerCase().trim().equals(name)) {
					//the same
					continue next_obj;
				}
			}
			//not found, add
			currObjs.add(object);
		}
		currObjs = null;
		objs = null;
		//add images
		ArrayList<Picture> currPicts = getPictures();
		ArrayList<Picture> picts = model.getPictures();
		next_img:
		for (int i = 0; i < picts.size(); i++) {
			Picture picture = picts.get(i);
			String name = picture.getName().toLowerCase().trim();
			String url = picture.getUrl().trim();
			boolean byURL = url.length()>0;
			for (int j = 0; j < currPicts.size(); j++) {
				Picture pkt = currPicts.get(j);
				
				if (byURL) {
					if (pkt.getUrl().trim().equals(url)) {
						//the same
						continue next_img;
					}
				} else if (pkt.getName().toLowerCase().trim().equals(name)) {
					//the same
					continue next_img;
				}
			}
			//not found, add
			currPicts.add(picture);
		}
		currPicts = null;
		picts = null;
		//add sounds
		ArrayList<Sound> currSound = getSounds();
		ArrayList<Sound> sounds = model.getSounds();
		next_img:
		for (int i = 0; i < sounds.size(); i++) {
			Sound sound = sounds.get(i);
			String name = sound.getName().toLowerCase().trim();
			String url = sound.getUrl().trim();
			boolean byURL = url.length()>0;
			for (int j = 0; j < currSound.size(); j++) {
				Sound snd = currSound.get(j);
				
				if (byURL) {
					if (snd.getUrl().trim().equals(url)) {
						//the same
						continue next_img;
					}
				} else if (snd.getName().toLowerCase().trim().equals(name)) {
					//the same
					continue next_img;
				}
			}
			//not found, add
			currSound.add(sound);
		}
		currSound = null;
		sounds = null;
		//done
		refreshAll();
	}

	public void checkIntegrity() {
//		HashSet<String> ids = new HashSet<String>(paragraphs.size());
//		for (int i = 0; i < paragraphs.size(); i++) {
//			String id = paragraphs.get(i).getId();
//			while (true) {
//				if (ids.contains(id)) {
//					//ERROR 
//					id = String.valueOf(nextId++);
//					paragraphs.get(i).setId(id);
//				} else {
//					ids.add(id);
//					break;
//				}
//			}
//		}
	}

	public void generateConnectionNames() {
		for (ParagraphConnection connection : paragraphConnections) {
			boolean refresh=false;
			if (connection.getNameFrom().length()==0) {
				//empty
				connection.setNameFrom(connection.getTo().getName());
				refresh=true;
			}
			if (connection.isBothDirections() && connection.getNameTo().length()==0) {
				connection.setNameTo(connection.getFrom().getName());
				refresh=true;
			}
			if (refresh) {
				updateParagraphConnection(connection, null);
			}
		}
		Window.alert(appConstants.validatorDone());
	}
	
	public void splitParagraphConnection(ParagraphConnection connection) {
		Paragraph paragraph = addNewParagraph(null);
		if (connection.getFrom().getX()>0 && connection.getFrom().getY()>0 && connection.getTo().getX()>0 && connection.getTo().getY()>0) {
			int x = Math.min(connection.getFrom().getX(), connection.getTo().getX())+(Math.abs(connection.getFrom().getX()-connection.getTo().getX()))/2;
			int y = Math.min(connection.getFrom().getY(), connection.getTo().getY())+(Math.abs(connection.getFrom().getY()-connection.getTo().getY()))/2;
			paragraph.setX(x);
			paragraph.setY(y);
		}
		ParagraphConnection connection2 = new ParagraphConnection();
		ParagraphConnection connection3 = new ParagraphConnection();
		
		connection3.setFrom(connection.getFrom());
		connection3.setTo(paragraph);
		connection2.setFrom(paragraph);
		connection2.setTo(connection.getTo());
		connection2.setBothDirections(connection.isBothDirections());
		
		connection3.setBothDirections(connection.isBothDirections());
		connection3.setType(connection.getType());
		connection3.setColor(connection.getColor());
		connection3.setCorrectionX(connection.getCorrectionX());
		connection3.setCorrectionY(connection.getCorrectionY());
		connection3.setModificator(connection.getModificator());
		connection3.setObject(connection.getObject());
		connection3.setParameter(connection.getParameter());
		connection3.setParameterValue(connection.getParameterValue());
		connection3.setStrictness(connection.getStrictness());
		/*
		 * replace IDs in text
		 */
		
		String toId = CONNECTION_DELIMETER_FROM+connection.getToId()+CONNECTION_DELIMETER_TO_STR;
		String toIdNew = CONNECTION_DELIMETER_FROM+connection3.getToId()+CONNECTION_DELIMETER_TO_STR;
		String desc = connection3.getFrom().getDescription().replace(toId,toIdNew);
		connection3.getFrom().setDescription(desc);
		
		if (connection.isBothDirections()) {
			String fromId = CONNECTION_DELIMETER_FROM_STR+connection.getFromId()+CONNECTION_DELIMETER_TO;
			String fromIdNew = CONNECTION_DELIMETER_FROM_STR+connection2.getFromId()+CONNECTION_DELIMETER_TO;
			desc = connection2.getTo().getDescription().replace(fromId,fromIdNew);
			connection2.getTo().setDescription(desc);
		}
		
		removeParagraphConnection(connection);
		addParagraphConnection(connection2,null);
		addParagraphConnection(connection3,null);
		fireParagraphEvent(EVENT_UPDATE, connection3.getFrom(), null);
		fireParagraphEvent(EVENT_UPDATE, connection2.getTo(), null);
		fireParagraphEvent(EVENT_UPDATE, paragraph, null);
		selectParagraph(paragraph,null);
	}
	
//	private void addConnection(HashMap<Paragraph, ArrayList<ParagraphConnection>> connections,  ParagraphConnection connection, Paragraph from) {
//		ArrayList<ParagraphConnection> list = connections.get(from);
//		if (list==null) {
//			list = new ArrayList<ParagraphConnection>();
//			connections.put(from, list);
//		}
//		list.add(connection);
//	}
	
	public ModelPersist getLightMode() {
		ModelPersist result = new ModelPersist(appConstants, appMessages);
		result.apply(this);
		ArrayList<ParagraphConnection> connections =new ArrayList<ParagraphConnection>(paragraphConnections.size());
		
		HashSet<ObjectBean> objects = new HashSet<ObjectBean>(this.objects.size());
		HashSet<AbstractParameter> parameters = new HashSet<AbstractParameter>(this.parameters.size());
		HashSet<Picture> pictures = new HashSet<Picture>(this.pictures.size());
		HashSet<Sound> sounds = new HashSet<Sound>(this.sounds.size());
		
		HashMap<Paragraph, Paragraph> fakeParargaprh = new HashMap<Paragraph, Paragraph>();
		for (ParagraphConnection connection : getParagraphConnections()) {
			boolean fake=false;
			if (connection.isBothDirections()) {
				if (connection.getFrom().isCommercial() && connection.getTo().isCommercial()) {
					//connection between commercial paragraphs
					continue;
				}
				if (connection.getFrom().isCommercial()) {
					//make a copy and reverse
					fake = true;
					ParagraphConnection connection2 = new ParagraphConnection(connection);
					connection2.setFrom(connection.getTo());
					connection2.setTo(connection.getFrom());
					connection2.setBothDirections(false);
					connection = connection2;
				} else if (connection.getTo().isCommercial()) {
					//make a copy and make a one way
					fake = true;
					connection = new ParagraphConnection(connection);
					connection.setBothDirections(false);
				}
			} else if (connection.getFrom().isCommercial()) {
				//connection from commercial paragraph
				continue;
			}
			if (connection.getTo().isCommercial()) {
				//replace to a fake paragraph
				if (fake==false) {
					connection = new ParagraphConnection(connection);
				}
				Paragraph paragraph = fakeParargaprh.get(connection.getTo());
				if (paragraph==null) {
					paragraph = new Paragraph();
					paragraph.setId(connection.getTo().getId());
					paragraph.setName(connection.getTo().getName());
					paragraph.setNumber(connection.getTo().getNumber());
					paragraph.setX(connection.getTo().getX());
					paragraph.setY(connection.getTo().getY());
					paragraph.setStatus(connection.getTo().getStatus());
					paragraph.setDescription("");
					paragraph.setType(Paragraph.TYPE_SUCCESS);
					paragraph.setCommercial(true);
					fakeParargaprh.put(connection.getTo(),paragraph);
				}
				connection.setTo(paragraph);
			}
			connections.add(connection);
			if (connection.getFrom().isCommercial()==false) {
				addNonCommercialContent(connection.getFrom(),objects,parameters,pictures,sounds);
			}
			if (connection.isBothDirections() && connection.getTo().isCommercial()==false) {
				addNonCommercialContent(connection.getFrom(),objects,parameters,pictures,sounds);
			}
		}

		HashSet<Paragraph> paragraphs = new HashSet<Paragraph>(this.paragraphs.size());
		for (ParagraphConnection connection : connections) {
			paragraphs.add(connection.getFrom());
			paragraphs.add(connection.getTo());
		}
		int counter=0;
		for (Paragraph paragraph : paragraphs) {
			if (paragraph.isCommercial()==false) {
				counter++;
			}
		}
//		for (Iterator<ParagraphConnection> iterator = connections.iterator(); iterator.hasNext();) {
//			ParagraphConnection connection = iterator.next();
//			if (connection.isBothDirections()==false && connection.isConditional()) {
//				switch (connection.getType()) {
//				case ParagraphConnection.TYPE_MODIFICATOR:
//					if (parameters.contains(connection.getModificator())==false) {
//						//cannot be passed
//						iterator.remove();
//						continue;
//					}
//					break;
//				case ParagraphConnection.TYPE_NORMAL:
//					if (objects.contains(connection.getObject())==false) {
//						//cannot be passed
//						iterator.remove();
//						continue;
//					}
//					break;
//				}
//			}
//			paragraphs.add(connection.getFrom());
//			paragraphs.add(connection.getTo());
//		}
		
		//add all connection-releated stuff
		for (ParagraphConnection connection : connections) {
			addNonCommercialContent(connection,objects,parameters);
		}
		for (Picture picture : this.pictures) {
			if (picture.isFiller()) {
				pictures.add(picture);
			}
		}
		result.paragraphs = new ArrayList<Paragraph>(paragraphs);
		result.paragraphConnections = connections;
		result.pictures = new ArrayList<Picture>(pictures);
		result.sounds = new ArrayList<Sound>(sounds);
		result.objects = new ArrayList<ObjectBean>(objects);
		paragraphs = null;
		connections = null;
		pictures = null;
		sounds = null;
		
		//add all related parameters (NPC, battles, etc.)
		ArrayList<AbstractParameter> list = new ArrayList<AbstractParameter>(this.parameters);
		list.removeAll(parameters);
		otter:
		while (true) {
			for (AbstractParameter parameter : list) {
				for (AbstractParameter parameter2 : parameters) {
					if (parameter2.dependsOn(parameter)) {
						//add this parameter and check again
						parameters.add(parameter);
						list.remove(parameter);
						continue otter;
					}
				}
			}
			break;
		}
		result.parameters = new ArrayList<AbstractParameter>(parameters);
		result.demoInfoText = appMessages.demoInfoTextDefault(counter,this.paragraphs.size());
		return result;
	}

	private void addNonCommercialContent(ParagraphConnection connection, HashSet<ObjectBean> objects, HashSet<AbstractParameter> parameters) {
		for (ObjectBean bean : this.objects) {
			if (connection.dependsOn(bean)) {
				objects.add(bean);
			}
		}
		for (AbstractParameter parameter : this.parameters) {
			if (connection.dependsOn(parameter)) {
				parameters.add(parameter);
			}
		}
	}

	private void addNonCommercialContent(Paragraph paragraph, HashSet<ObjectBean> objects, HashSet<AbstractParameter> parameters, HashSet<Picture> pictures, HashSet<Sound> sounds) {
		for (Sound sound : this.sounds) {
			if (paragraph.dependsOn(sound)) {
				sounds.add(sound);
			}
		}
		for (Picture picture : this.pictures) {
			if (paragraph.dependsOn(picture)) {
				pictures.add(picture);
			}
		}
		for (ObjectBean bean : this.objects) {
			if (paragraph.dependsOn(bean)) {
				objects.add(bean);
			}
		}
		for (AbstractParameter parameter : this.parameters) {
			if (paragraph.dependsOn(parameter)) {
				parameters.add(parameter);
			}
		}
	}

	public NPC addNewNPC(ParameterListener sender) {
		return (NPC)addAbstractParameter(new NPC(),sender,appConstants.modelNewNPCName());
	}

	public Parameter addNewParameter(ParameterListener sender) {
		return (Parameter)addAbstractParameter(new Parameter(),sender,appConstants.modelNewParameterName());
	}

	public Battle addNewBattle(ParameterListener sender) {
		return (Battle)addAbstractParameter(new Battle(),sender,appConstants.modelNewBattleName());
	}

	public Modificator addNewModificator(ParameterListener sender) {
		return (Modificator)addAbstractParameter(new Modificator(),sender,appConstants.modelNewModificatorName());
	}

	public Alchemy addNewAlchemy(ParameterListener sender) {
		Alchemy alchemy = new Alchemy();
		alchemy.setFromValue(1);
		alchemy.setToValue(new DiceValue(6,0,3));
		return (Alchemy)addAbstractParameter(alchemy,sender,appConstants.modelNewAlchemyName());
	}

	private AbstractParameter addAbstractParameter(AbstractParameter parameter,	ParameterListener sender,String name) {
		parameter.setId(String.valueOf(nextObjId++));
		parameter.setName(name);
		parameters.add(parameter);
		fireParametersEvent(EVENT_ADD_NEW,parameter,sender);
		return parameter;
	}

	public void sortNPC() {
		sortAbastractParameters(AbstractParameter.TYPE_NPC);
	}

	private void sortAbastractParameters(int type) {
		ArrayList<AbstractParameter> list = new ArrayList<AbstractParameter>(parameters.size());
		for (Iterator<AbstractParameter> iterator = parameters.iterator(); iterator.hasNext();) {
			AbstractParameter parameter = iterator.next();
			if (parameter.getType()==type) {
				//remove for sorting
				list.add(parameter);
				iterator.remove();
			}
		}
		Collections.sort(list, new Comparator<AbstractParameter>() {
			public int compare(AbstractParameter o1, AbstractParameter o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		parameters.addAll(list);
		fireParametersEvent(EVENT_REFRESH_ALL,null,null);
	}

	public void sortParameters() {
		sortAbastractParameters(AbstractParameter.TYPE_PARAMETER);
	}

	public void sortBattles() {
		sortAbastractParameters(AbstractParameter.TYPE_BATTLE);
	}

	public void sortModificators() {
		sortAbastractParameters(AbstractParameter.TYPE_MODIFICATOR);
	}

	public void sortAlchemy() {
		sortAbastractParameters(AbstractParameter.TYPE_ALCHEMY);
	}

	/**
	 * Generate text for paragraph
	 * @param paragraph
	 * @param export
	 */
	public void regenerateText(Paragraph paragraph,int export) {
		ArrayList<ParagraphConnection> connections = getOutputParagraphConnections(paragraph);
		
		StringBuffer buffer = new StringBuffer();
		
		for (ParagraphConnection connection : connections) {
			if (connection.getFrom()==paragraph && connection.isHiddenUsage(settings)) {
				buffer.append(CONNECTION_DELIMETER_FROM);
				buffer.append(connection.getToId());
				buffer.append(CONNECTION_DELIMETER_TO);
			}
			
		}
		buffer.append(paragraph.getName());
		buffer.append('\n');
		if (paragraph.getGotObjects().size()>0) {
			for (ObjectBean bean : paragraph.getGotObjects()) {
				buffer.append('+');
				buffer.append(bean.getName());
				buffer.append(' ');
				buffer.append(CONNECTION_DELIMETER_FROM);
				buffer.append(OBJECT_ID_PREFIX);
				buffer.append(bean.getId());
				buffer.append(CONNECTION_DELIMETER_TO);
				buffer.append('\n');
			}
		}
		if (paragraph.getLostObjects().size()>0) {
			for (ObjectBean bean : paragraph.getLostObjects()) {
				buffer.append('-');
				buffer.append(bean.getName());
//				buffer.append(' ');
//				buffer.append(CONNECTION_DELIMETER_FROM);
//				buffer.append(OBJECT_ID_PREFIX);
//				buffer.append(bean.getId());
//				buffer.append(CONNECTION_DELIMETER_TO);
				buffer.append('\n');
			}
		}
		buffer.append('\n');
		for (int i = 0; i < connections.size(); i++) {
			ParagraphConnection connection = connections.get(i);
			if (connection.getFrom()==paragraph) {
				//normal
				if (connection.isHiddenUsage(settings)) {
					//already added
					continue;
				}
				switch (connection.getType()) {
				case ParagraphConnection.TYPE_NORMAL:
					if (connection.getObject() != null) {
						//conditional
//						buffer.append(CONNECTION_DELIMETER_FROM);
//						buffer.append(CONNECTION_DELIMETER_TO);
						buffer.append(connection.getObject().getName());
						buffer.append(" - ");
					}
					break;
				case ParagraphConnection.TYPE_MODIFICATOR:
					if (connection.getModificator() != null) {
						String id;
						if (settings.isAddModificatorNamesToText()) {
							id = "<m"+connection.getModificator().getId()+">";
						} else {
							id = connection.getModificator().getName();
						}
						switch (connection.getStrictness()) {
						case ParagraphConnection.STRICTNESS_MUST:
							if (connection.getModificator().isAbsolute()) {
								buffer.append(appMessages.paragraphTemplateModificatorPresentMustAbs(id));
							} else {
								buffer.append(appMessages.paragraphTemplateModificatorPresentMust(id));
							} 
							break;
						case ParagraphConnection.STRICTNESS_MUST_NOT:
							buffer.append(appMessages.paragraphTemplateModificatorPresentMustNot(id));
							break;
						default:
							if (connection.getModificator().isAbsolute()) {
								buffer.append(appMessages.paragraphTemplateModificatorPresentAbs(id));
							} else {
								buffer.append(appMessages.paragraphTemplateModificatorPresent(id));
							}
						}
						buffer.append(' ');
//						buffer.append(CONNECTION_DELIMETER_FROM);
//						buffer.append(CONNECTION_DELIMETER_TO);
					}
					break;
				case ParagraphConnection.TYPE_NO_MODIFICATOR:
					if (connection.getModificator() != null) {
//						buffer.append(CONNECTION_DELIMETER_FROM);
//						buffer.append(CONNECTION_DELIMETER_TO);
						String id;
						if (settings.isAddModificatorNamesToText()) {
							id = "<m"+connection.getModificator().getId()+">";
						} else {
							id = connection.getModificator().getName();
						}
						switch (connection.getStrictness()) {
						case ParagraphConnection.STRICTNESS_MUST:
							buffer.append(appMessages.paragraphTemplateModificatorNotPresentMust(id));
							break;
						case ParagraphConnection.STRICTNESS_MUST_NOT:
							buffer.append(appMessages.paragraphTemplateModificatorNotPresentMustNot(id));
							break;
						default:
							buffer.append(appMessages.paragraphTemplateModificatorNotPresent(id));
						}
						buffer.append(' ');
					}
					break;
				case ParagraphConnection.TYPE_PARAMETER_LESS:
					if (connection.getParameter() != null && connection.getParameterValue() !=null) {
//						buffer.append(CONNECTION_DELIMETER_FROM);
//						buffer.append(CONNECTION_DELIMETER_TO);
						switch (connection.getStrictness()) {
						case ParagraphConnection.STRICTNESS_MUST:
							buffer.append(appMessages.paragraphTemplateParameterLessMust(connection.getParameter().getName(),connection.getParameterValue().toString()));
							break;
						case ParagraphConnection.STRICTNESS_MUST_NOT:
							buffer.append(appMessages.paragraphTemplateParameterLessMustNot(connection.getParameter().getName(),connection.getParameterValue().toString()));
							break;
						default:
							buffer.append(appMessages.paragraphTemplateParameterLess(connection.getParameter().getName(),connection.getParameterValue().toString()));
						}
						buffer.append(' ');
					}
					break;
				case ParagraphConnection.TYPE_PARAMETER_MORE:
					if (connection.getParameter() != null && connection.getParameterValue() !=null) {
//						buffer.append(CONNECTION_DELIMETER_FROM);
//						buffer.append(CONNECTION_DELIMETER_TO);
						switch (connection.getStrictness()) {
						case ParagraphConnection.STRICTNESS_MUST:
							buffer.append(appMessages.paragraphTemplateParameterMoreMust(connection.getParameter().getName(),connection.getParameterValue().toString()));
							break;
						case ParagraphConnection.STRICTNESS_MUST_NOT:
							buffer.append(appMessages.paragraphTemplateParameterMoreMustNot(connection.getParameter().getName(),connection.getParameterValue().toString()));
							break;
						default:
							buffer.append(appMessages.paragraphTemplateParameterMore(connection.getParameter().getName(),connection.getParameterValue().toString()));
						}
						buffer.append(' ');
					}
					break;
				case ParagraphConnection.TYPE_VITAL_LESS:
					if (connection.getFrom().getBattle() != null && connection.getFrom().getBattle().getVital() != null) {
						buffer.append(appMessages.paragraphTemplateVitalLess(connection.getFrom().getBattle().getVital().getName(),connection.getParameterValue().toString()));
//						buffer.append(CONNECTION_DELIMETER_FROM);
//						buffer.append(CONNECTION_DELIMETER_TO);
					}
					break;
				case ParagraphConnection.TYPE_ENEMY_VITAL_LESS:
					if (connection.getFrom().getBattle() != null && connection.getFrom().getBattle().getVital() != null) {
						buffer.append(appMessages.paragraphTemplateNPCVitalLess(connection.getFrom().getBattle().getVital().getName(),connection.getParameterValue().toString()));
//						buffer.append(CONNECTION_DELIMETER_FROM);
//						buffer.append(CONNECTION_DELIMETER_TO);
					}
					break;
				case ParagraphConnection.TYPE_BATTLE_ROUND_MORE:
					switch (connection.getStrictness()) {
					case ParagraphConnection.STRICTNESS_MUST:
						buffer.append(appMessages.paragraphTemplateBattleRoundMoreMust(connection.getParameterValue().getConstant()));
						break;
					case ParagraphConnection.STRICTNESS_MUST_NOT:
						buffer.append(appMessages.paragraphTemplateBattleRoundMoreMustNot(connection.getParameterValue().getConstant()));
						break;
					default:
						buffer.append(appMessages.paragraphTemplateBattleRoundMore(connection.getParameterValue().getConstant()));
					}
					break;
				}
				buffer.append(connection.getTo().getName());
				buffer.append(CONNECTION_DELIMETER_FROM);
				buffer.append(connection.getToId());
				buffer.append(CONNECTION_DELIMETER_TO);
			} else {
				//reverse
				buffer.append(connection.getFrom().getName());
				buffer.append(CONNECTION_DELIMETER_FROM);
				buffer.append(connection.getFromId());
				buffer.append(CONNECTION_DELIMETER_TO);
			}
			buffer.append('\n');
		}
		
		//change modificators
		if (paragraph.getChangeModificators() != null && paragraph.getChangeModificators().size()>0) {
			for (Modificator modificator : paragraph.getChangeModificators().keySet()) {
				boolean value = paragraph.getChangeModificators().get(modificator);
				if (settings.isAddModificatorNamesToText()){
					if (value) {
						buffer.append(appMessages.paragraphTemplateSetModificatorID(modificator.getId()));
					} else {
						buffer.append(appMessages.paragraphTemplateClearModificatorID(modificator.getId()));
					}
				} else {
					if (value) {
						if (modificator.isAbsolute()) {
							buffer.append(appMessages.paragraphTemplateSetAbsoluteModificator(modificator.getName(),modificator.getId()));
						} else {
							buffer.append(appMessages.paragraphTemplateSetModificator(modificator.getName()));
						}
					} else {
						buffer.append(appMessages.paragraphTemplateClearModificator(modificator.getName()));
					}
				}
			}
		}
		
		//change parameters
		if (paragraph.getChangeParameters() != null && paragraph.getChangeParameters().size()>0) {
			for (Parameter parameter : paragraph.getChangeParameters().keySet()) {
				ParametersCalculation calculation = paragraph.getChangeParameters().get(parameter);
				buffer.append('\n');
				buffer.append(calculation.toString(parameter,appMessages));
			}
		}
		
		//add battle description
		if (paragraph.getBattle() != null) {
			buffer.append(appMessages.paragraphTemplateBattle(paragraph.getBattle().getName()));
			if (paragraph.getEnemies() != null && paragraph.getEnemies().size()>0) {
				boolean add=false;
				int round=0;
				for (NPCParams npc : paragraph.getEnemies()) {
					if (npc.getRound()>round) {
						round = npc.getRound();
						buffer.append(appMessages.paragraphTemplateJoinAfterRound(round));
					}
					if (add) {
						buffer.append(',');
					} else {
						add = true;
					}
					if (npc.isFriend()) {
						buffer.append(appMessages.paragraphTemplateBattleFriend(npc.getNpc().getName()));
					} else {
						buffer.append(npc.getNpc().getName());
					}
				}
				buffer.append('\n');
			}
		}
		
		if (paragraph.getAlchemy() != null) {
			for (Alchemy alchemy : paragraph.getAlchemy().keySet()) {
				boolean value = paragraph.getAlchemy().get(alchemy);
				if (value) {
					if (settings.isAddAlchemyToText()) {
						buffer.append(appMessages.paragraphTemplateAlchemyEnabledInText(alchemy.getName(),alchemy.getId(),alchemy.getFrom().getName(),alchemy.getTo().getName()));
					} else {
						buffer.append(appMessages.paragraphTemplateAlchemyEnabled(alchemy.getName(),String.valueOf(alchemy.getFromValue()),alchemy.getFrom().getName(),alchemy.getToValue().toString(),alchemy.getTo().getName()));
					}
				} else {
					buffer.append(appMessages.paragraphTemplateAlchemyDisabled(alchemy.getName()));
				}
				buffer.append('\n');
			}
		}
		paragraph.setDescription(buffer.toString());
		fireParagraphEvent(EVENT_UPDATE, paragraph,null);
	}
}
