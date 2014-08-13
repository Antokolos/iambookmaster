package com.iambookmaster.client.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.exceptions.JSONException;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;

public class ParagraphConnection implements Serializable{

	private static final long serialVersionUID = 1L;

	private static final String JSON_FROM = "from";
	private static final String JSON_TO = "to";
	private static final String JSON_DIRECTIONS = "dir";
	private static final String JSON_TO_ID = "toid";
	private static final String JSON_FROM_ID = "fromid";
	private static final String JSON_OBJECT_ID = "obj";
	private static final String JSON_COLOR = "b";
	private static final String JSON_TYPE = "c";
	private static final String JSON_PARAMETER = "d";
	private static final String JSON_PARAMETER_VALUE = "e";
	private static final String JSON_STRICTNESS = "f";
	private static final String JSON_NAME_FROM = "h";
	private static final String JSON_NAME_TO = "i";
	private static final String JSON_CORRECTION_X = "g";
	private static final String JSON_CORRECTION_Y = "j";
	private static final String JSON_REVERSE_HIDDEN_USAGE = "k";

	public static final String TYPE_NORMAL_STR = "0";
	public static final String TYPE_PARAMETER_MORE_STR = "1";
	public static final String TYPE_PARAMETER_LESS_STR = "2";
	public static final String TYPE_MODIFICATOR_STR = "3";
	public static final String TYPE_NO_MODIFICATOR_STR = "4";
	public static final String TYPE_VITAL_LESS_STR = "5";
	public static final String TYPE_ENEMY_VITAL_LESS_STR = "6";
	public static final String TYPE_BATTLE_ROUND_MORE_STR = "7";
	
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_PARAMETER_MORE = 1;
	public static final int TYPE_PARAMETER_LESS = 2;
	public static final int TYPE_MODIFICATOR = 3;
	public static final int TYPE_NO_MODIFICATOR = 4;
	public static final int TYPE_VITAL_LESS = 5;
	public static final int TYPE_ENEMY_VITAL_LESS = 6;
	public static final int TYPE_BATTLE_ROUND_MORE = 7;

	public static final String STRICTNESS_CAN_STR = "0";
	public static final String STRICTNESS_MUST_STR = "1";
	public static final String STRICTNESS_MUST_NOT_STR = "2";
	public static final int STRICTNESS_CAN = 0;
	public static final int STRICTNESS_MUST = 1;
	public static final int STRICTNESS_MUST_NOT = 2;




	
	private Paragraph from;
	private Paragraph to;
	private String toId;
	private String fromId;
	private String nameFrom;
	private String nameTo;
	private boolean bothDirections;
	private ObjectBean object;
	private int color;
	private int type;
	private int strictness;
	private Parameter parameter;
	private Modificator modificator;
	private DiceValue parameterValue;
	private int correctionX;
	private int correctionY;
	private boolean reverseHiddenUsage;
	
	public ParagraphConnection(ParagraphConnection connection) {
		this.from = connection.from;
		this.to = connection.to;
		this.toId = connection.toId;
		this.fromId = connection.fromId;
		this.nameFrom = connection.nameFrom;
		this.nameTo = connection.nameTo;
		this.bothDirections = connection.bothDirections;
		this.object = connection.object;
		this.color = connection.color;
		this.type = connection.type;
		this.strictness = connection.strictness;
		this.parameter = connection.parameter;
		this.modificator = connection.modificator;
		this.parameterValue = connection.parameterValue;
		this.correctionX = connection.correctionX;
		this.correctionY = connection.correctionY;
	}

//	public ParagraphConnection() {
//		this.bothDirections = connection.bothDirections
//		private Paragraph from;
//		private Paragraph to;
//		private String toId;
//		private String fromId;
//		private String nameFrom;
//		private String nameTo;
//		private boolean bothDirections;
//		private ObjectBean object;
//		private int color;
//		private int type;
//		private int strictness;
//		private Parameter parameter;
//		private Modificator modificator;
//		private DiceValue parameterValue;
//		private int correctionX;
//		private int correctionY;
//	}

	public ParagraphConnection() {
	}

	public boolean dependsOn(AbstractParameter abstractParameter) {
		return parameter==abstractParameter || modificator==abstractParameter;
	}

	public void toJSON(JSONBuilder builder, int export) {
		builder.newRow();
		builder.field(JSON_FROM, from.getId());
		builder.field(JSON_TO, to.getId());
		if (reverseHiddenUsage) {
			builder.field(JSON_REVERSE_HIDDEN_USAGE, 1);
		}
		if (nameFrom != null && nameFrom.length()>0) {
			builder.field(JSON_NAME_FROM,nameFrom);
		}
		if (toId != null && toId.length()>0) {
			builder.field(JSON_TO_ID, toId);
		}
		if (fromId != null && fromId.length()>0) {
			builder.field(JSON_FROM_ID, fromId);
		}
		if (bothDirections) {
			builder.field(JSON_DIRECTIONS, bothDirections);
			if (nameTo != null && nameTo.length()>0) {
				builder.field(JSON_NAME_TO,nameTo);
			}
		}
		if (object != null) {
			builder.field(JSON_OBJECT_ID, object.getId());
		}
		builder.field(JSON_TYPE, type);
		builder.field(JSON_STRICTNESS, strictness);
		switch (type) {
		case TYPE_MODIFICATOR:
		case TYPE_NO_MODIFICATOR:
			if (modificator != null) {
				builder.field(JSON_PARAMETER, modificator.getId());
			}
			break;

		case TYPE_PARAMETER_MORE:
		case TYPE_PARAMETER_LESS:
			if (parameter != null && parameterValue != null) {
				builder.field(JSON_PARAMETER, parameter.getId());
				builder.field(JSON_PARAMETER_VALUE, parameterValue.getJSON());
			}
			break;
			
		case TYPE_VITAL_LESS:
		case TYPE_ENEMY_VITAL_LESS:
			if (parameterValue != null) {
				builder.field(JSON_PARAMETER_VALUE, parameterValue.getJSON());
			}
			break;
		case TYPE_BATTLE_ROUND_MORE:
			if (parameterValue != null) {
				parameterValue.setN(0);
				builder.field(JSON_PARAMETER_VALUE, parameterValue.getJSON());
			}
			break;
		}
		if (export==Model.EXPORT_ALL) {
			if (color != 0) {
				builder.field(JSON_COLOR, color);
			}
			if (correctionX != 0) {
				builder.field(JSON_CORRECTION_X, correctionX);
			}
			if (correctionY != 0) {
				builder.field(JSON_CORRECTION_Y, correctionY);
			}
			
		}
	}
	
	public boolean isReverseHiddenUsage() {
		return reverseHiddenUsage;
	}

	public void setReverseHiddenUsage(boolean reverseHiddenUsage) {
		this.reverseHiddenUsage = reverseHiddenUsage;
	}

	public static ArrayList<ParagraphConnection> fromJSArray(Object object, ArrayList<Paragraph> locations,ArrayList<ObjectBean> objects,JSONParser parser,AppMessages appMessages,HashMap<String,AbstractParameter> parametersMap) throws JSONException {
		int l = parser.length(object);
		ArrayList<ParagraphConnection> list = new ArrayList<ParagraphConnection>();
		HashMap<String, Paragraph> map = new HashMap<String, Paragraph>(locations.size());
		for (int i = 0; i < locations.size(); i++) {
			Paragraph location = locations.get(i);
			map.put(location.getId(), location);
		}
		for (int connIndex = 0; connIndex < l; connIndex++) {
			Object row = parser.getRow(object, connIndex);
			String id1 = parser.propertyString(row, JSON_FROM);
			String id2 = parser.propertyString(row, JSON_TO);
			Paragraph from = map.get(id1);
			Paragraph to = map.get(id2);
			if (from == null) {
				throw new JSONException(appMessages.modelUnknownParagraphId(id1));
			}
			if (to == null) {
				throw new JSONException(appMessages.modelUnknownParagraphId(id2));
			}
			id1 = parser.propertyNoCheckString(row, JSON_OBJECT_ID);
			ParagraphConnection connection = new ParagraphConnection();
			if (id1 != null) {
				for (int j = 0; j < objects.size(); j++) {
					ObjectBean bean = objects.get(j);
					if (bean.getId().equals(id1)) {
						//found
						connection.setObject(bean);
						break;
					}
				}
				if (connection.getObject()==null) {
					throw new JSONException(appMessages.modelUnknownObjectId(id1));
				}
			}
			connection.setFrom(from);
			connection.setTo(to);
			connection.setBothDirections(parser.propertyNoCheckBoolean(row, JSON_DIRECTIONS));
			connection.toId=parser.propertyNoCheckString(row, JSON_TO_ID);
			connection.fromId =parser.propertyNoCheckString(row, JSON_FROM_ID);
			connection.setColor(parser.propertyNoCheckInt(row, JSON_COLOR));
			connection.strictness = parser.propertyNoCheckInt(row, JSON_STRICTNESS);
			connection.correctionX = parser.propertyNoCheckInt(row, JSON_CORRECTION_X);
			connection.correctionY = parser.propertyNoCheckInt(row, JSON_CORRECTION_Y);
			connection.nameFrom=parser.propertyNoCheckString(row, JSON_NAME_FROM);
			connection.reverseHiddenUsage  = parser.propertyNoCheckInt(row, JSON_REVERSE_HIDDEN_USAGE)>0;
			if (connection.bothDirections) {
				connection.nameTo=parser.propertyNoCheckString(row, JSON_NAME_TO);
				connection.setType(TYPE_NORMAL);
			} else {
				connection.setType(parser.propertyNoCheckInt(row, JSON_TYPE));
				switch (connection.type) {
				case TYPE_MODIFICATOR:
				case TYPE_NO_MODIFICATOR:
					String idMod = parser.propertyString(row, JSON_PARAMETER);
					if (idMod != null) {
						AbstractParameter abstractParameter = parametersMap.get(idMod);
						if (abstractParameter instanceof Modificator) {
							connection.setModificator((Modificator) abstractParameter);
						} else {
							throw new JSONException("Unknown Modificator with ID="+idMod);
						}
					} else {
						connection.setType(TYPE_NORMAL);
					}
					break;
	
				case TYPE_PARAMETER_MORE:
				case TYPE_PARAMETER_LESS:
					String idParam = parser.propertyNoCheckString(row, JSON_PARAMETER);
					if (idParam != null) {
						AbstractParameter abstractParameter2 = parametersMap.get(idParam);
						if (abstractParameter2 instanceof Parameter) {
							connection.setParameter((Parameter) abstractParameter2);
						} else {
							throw new JSONException("Unknown Parameter with ID="+idParam);
						}
					}
					idParam = parser.propertyNoCheckString(row, JSON_PARAMETER_VALUE);
					if (idParam != null) {
						connection.setParameterValue(new DiceValue(idParam));
					}
					break;
					
				case TYPE_VITAL_LESS:
				case TYPE_ENEMY_VITAL_LESS:
					String val = parser.propertyNoCheckString(row, JSON_PARAMETER_VALUE);
					if (val == null) {
						connection.setParameterValue(new DiceValue());
					} else {
						connection.setParameterValue(new DiceValue(val));
					}
					break;
				case TYPE_BATTLE_ROUND_MORE:
					String val2 = parser.propertyNoCheckString(row, JSON_PARAMETER_VALUE);
					if (val2 == null) {
						connection.setParameterValue(new DiceValue());
					} else {
						connection.setParameterValue(new DiceValue(val2));
					}
					break;
				}
			}
			list.add(connection);
		}
		return list;
	}
	
	public int getCorrectionX() {
		return correctionX;
	}

	public void setCorrectionX(int correctionX) {
		this.correctionX = correctionX;
	}

	public int getCorrectionY() {
		return correctionY;
	}

	public void setCorrectionY(int correctionY) {
		this.correctionY = correctionY;
	}

	public String getNameFrom() {
		return nameFrom==null ? "":nameFrom;
	}

	public void setNameFrom(String name) {
		this.nameFrom = name;
	}

	public String getNameTo() {
		return nameTo==null ? "":nameTo;
	}

	public void setNameTo(String name) {
		this.nameTo = name;
	}

	public boolean isBothDirections() {
		return bothDirections;
	}
	public void setBothDirections(boolean bothDirections) {
		this.bothDirections = bothDirections;
	}
	public ObjectBean getObject() {
		if (bothDirections) {
			//two way connection cannot have condition
			return null;
		} else {
			return object;
		}
	}
	public void setObject(ObjectBean object) {
		this.object = object;
	}
	public String getToId() {
		if (toId==null || toId.trim().length()==0) {
			toId = Model.CONNECTION_ID_PREFIX+to.getId();
		}
		return toId;
	}
	public String getFromId() {
		if (fromId==null || fromId.trim().length()==0) {
			fromId = Model.CONNECTION_ID_PREFIX+from.getId();
		}
		return fromId;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public void setFromId(String fromId) {
		this.fromId = fromId;
	}
	public void setToId(String toId) {
		this.toId = toId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Parameter getParameter() {
		return parameter;
	}
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}
	public Modificator getModificator() {
		return modificator;
	}
	public void setModificator(Modificator modificator) {
		this.modificator = modificator;
	}
	public DiceValue getParameterValue() {
		return parameterValue;
	}
	public void setParameterValue(DiceValue parameterValue) {
		this.parameterValue = parameterValue;
	}
	public int getStrictness() {
		return strictness;
	}
	public void setStrictness(int strictness) {
		this.strictness = strictness;
	}

	public Paragraph getFrom() {
		return from;
	}
	public void setFrom(Paragraph from) {
		this.from = from;
	}
	public Paragraph getTo() {
		return to;
	}
	public void setTo(Paragraph to) {
		this.to = to;
	}

	public boolean isConditional() {
		return object != null || type != TYPE_NORMAL;
	}

	public boolean isHiddenUsage(Settings settings) {
		if (bothDirections) {
			return false;
		}
		if (type==TYPE_NORMAL) {
			return settings.isHiddenUsingObjects() && object != null;
		} else {
			return false;
//			return strictness==STRICTNESS_MUST && settings.isSkipMustGoParagraphs();
		}
	}

	public boolean dependsOn(ObjectBean bean) {
		return bean==object;
	}

//	public boolean isReverceCondition(ParagraphConnection connection) {
//		switch (type) {
//		case TYPE_PARAMETER_LESS:
//			if (parameterValue.isNoDice() && connection.parameterValue.isNoDice()) {
//				if (connection.type==ParagraphConnection.TYPE_PARAMETER_MORE && strictness==connection.strictness) {
//					
//				}
//				if (connection.type==ParagraphConnection.TYPE_PARAMETER_LESS && 
//						(strictness==STRICTNESS_MUST_NOT && connection.strictness
//							) {
//					
//				}
//				parameterValue.getConstant() == connection.parameterValue.getConstant()+1;
//			}
//		case TYPE_PARAMETER_MORE:
//			return connection.type==ParagraphConnection.TYPE_PARAMETER_MORE &&
//					parameterValue.isNoDice() && connection.parameterValue.isNoDice() && 
//					parameterValue.getConstant() == connection.parameterValue.getConstant()-1;
//		case TYPE_MODIFICATOR:
//			return connection.type==ParagraphConnection.TYPE_NO_MODIFICATOR &&
//					modificator==connection.modificator;
//		case TYPE_NO_MODIFICATOR:
//			return connection.type==ParagraphConnection.TYPE_MODIFICATOR &&
//					modificator==connection.modificator;
//		case TYPE_NORMAL:
//			return connection.type==ParagraphConnection.TYPE_NORMAL &&
//					object==connection.object;
//		}
//		return false;
//	}

}
