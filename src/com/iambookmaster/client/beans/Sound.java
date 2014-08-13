package com.iambookmaster.client.beans;

import java.io.Serializable;
import java.util.ArrayList;

import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.model.Model;

public class Sound implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String JSON_ID = "a";
	private static final String JSON_NAME = "b";
	private static final String JSON_URL = "c";
	private static final String JSON_TYPE = "d";

	private String id;
	private String name;
	private String url;
	private int type;
	
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void toJSON(JSONBuilder builder, int export) {
		builder.newRow();
		builder.field(JSON_ID, id);
		if (export==Model.EXPORT_ALL) {
			builder.field(JSON_NAME, name);
		}
		builder.field(JSON_URL, url);
		builder.field(JSON_TYPE, type);
	}
	public static ArrayList<Sound> fromJSArray(Object obj,JSONParser parser) {
		int l = parser.length(obj);
		ArrayList<Sound> list = new ArrayList<Sound>();
		for (int i = 0; i < l; i++) {
			Object row = parser.getRow(obj, i);
			list.add(fromJS(row,parser));
		}
		return list;
	}
	
	public static Sound fromJS(Object object,JSONParser parser){
		Sound sound = new Sound();
		sound.id = parser.propertyString(object, JSON_ID);
		sound.url = parser.propertyString(object, JSON_URL);
		sound.type = parser.propertyInt(object, JSON_TYPE);
		sound.name = parser.propertyNoCheckString(object, JSON_NAME);
		return sound;
	}
}
