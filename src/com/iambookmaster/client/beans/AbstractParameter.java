package com.iambookmaster.client.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.exceptions.JSONException;
import com.iambookmaster.client.model.Model;

@SuppressWarnings("serial")
public abstract class AbstractParameter implements Serializable{

	public static final int TYPE_NPC = 0;
	public static final int TYPE_PARAMETER = 1;
	public static final int TYPE_BATTLE = 2;
	public static final int TYPE_MODIFICATOR = 3;
	public static final int TYPE_ALCHEMY = 4;
	
	private static final String JSON_ID = "a";
	private static final String JSON_NAME = "b";
	private static final String JSON_TYPE = "c";
	private static final String JSON_DESCRIPTION = "d";
	private static final String JSON_ICON = "e";
	
	private String id;
	private String name;
	private String description;
	private Picture icon;
	protected int type;
	transient private int order; 
	
	public Picture getIcon() {
		return icon;
	}
	public void setIcon(Picture icon) {
		this.icon = icon;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
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
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public static ArrayList<AbstractParameter> fromJSArray(Object object, JSONParser parser,HashMap<String,Picture> pictures) throws JSONException {
		int l = parser.length(object);
		ArrayList<AbstractParameter> list = new ArrayList<AbstractParameter>();
		for (int i = 0; i < l; i++) {
			Object row = parser.getRow(object, i);
			list.add(fromJS(row,parser,pictures));
		}
		if (list.size()>0) {
			//populate linked data
			HashMap<String, AbstractParameter> parametersMap = new HashMap<String, AbstractParameter>(list.size()); 
			for (AbstractParameter parameter : list) {
				parametersMap.put(parameter.getId(), parameter);
			}
			
			for (int i = 0; i < l; i++) {
				Object row = parser.getRow(object, i);
				list.get(i).fromJSON(row,parser,parametersMap,pictures);
			}
		}
		return list;
	}
	
	public abstract boolean dependsOn(AbstractParameter parameter);
	
	protected abstract void fromJSON(Object row, JSONParser parser, HashMap<String, AbstractParameter> parametersMap,HashMap<String,Picture> pictures)  throws JSONException;
	
	public static AbstractParameter fromJS(Object row, JSONParser parser,HashMap<String,Picture> pictures) throws JSONException {
		int type = parser.propertyInt(row, JSON_TYPE);
		AbstractParameter res;
		switch (type) {
		case TYPE_BATTLE:
			res = new Battle();
			break;
		case TYPE_NPC:
			res = new NPC();
			break;
		case TYPE_PARAMETER:
			res = new Parameter();
			break;
		case TYPE_MODIFICATOR:
			res = new Modificator();
			break;
		case TYPE_ALCHEMY:
			res = new Alchemy();
			break;
		default:
			throw new JSONException("Unknown type of parameter "+type);
		}
		res.id = parser.propertyString(row, JSON_ID);
		res.name = parser.propertyString(row, JSON_NAME);
		res.name = parser.propertyString(row, JSON_NAME);
		res.description = parser.propertyNoCheckString(row, JSON_DESCRIPTION);
		String id = parser.propertyNoCheckString(row, JSON_ICON);
		if (id != null) {
			res.icon = pictures.get(id);
		}
		return res;
	}
	
	public void toJSON(JSONBuilder builder, int export) {
		builder.newRow();
		builder.field(JSON_ID, id);
		builder.field(JSON_NAME, name);
		builder.field(JSON_TYPE, type);
		if (export==Model.EXPORT_ALL && description != null) {
			builder.field(JSON_DESCRIPTION, description);
		}
		if (icon != null) {
			builder.field(JSON_ICON, icon.getId());
		}
	}
	
	public int getType() {
		return type;
	}

}
