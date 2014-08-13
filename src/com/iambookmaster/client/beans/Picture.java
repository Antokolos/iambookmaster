package com.iambookmaster.client.beans;

import java.io.Serializable;
import java.util.ArrayList;

import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.model.Model;

public class Picture implements Serializable{

	private static final long serialVersionUID = 1L;

	private static final String JSON_ID = "a";
	private static final String JSON_NAME = "b";
	private static final String JSON_URL = "c";
	private static final String JSON_NO_REPEAT = "e";
	private static final String JSON_WIDTH = "d";
	private static final String JSON_HEIGHT = "h";
	private static final String JSON_ROLE = "f";
	private static final String JSON_BIG_URL = "g"; 
	private static final String JSON_BIG_WIDTH = "k";
	private static final String JSON_BIG_HEIGHT = "l";
	
	public static final int ROLE_FILLER=1; 
	public static final int ROLE_ICON=2;

	private String id;
	private String name;
	private String url;
	private int widht;
	private int height;
	private int bigWidht;
	private int bigHeight;
	private String bigUrl;
	private int role;
	private boolean noRepeat;
	
	public boolean isFiller() {
		return role==ROLE_FILLER;
	}
	
	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public String getBigUrl() {
		return bigUrl==null ? "" : null;
	}
	public void setBigUrl(String smallUrl) {
		this.bigUrl = smallUrl;
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
	public String getUrl() {
		return url==null ? "" : url;
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
		builder.field(JSON_WIDTH, widht);
		builder.field(JSON_HEIGHT, height);
		
		if (bigUrl != null && bigUrl.isEmpty()==false) {
			builder.field(JSON_BIG_URL, bigUrl);
			builder.field(JSON_BIG_HEIGHT, bigHeight);
			builder.field(JSON_BIG_WIDTH, bigWidht);
		}
		if (noRepeat) {
			builder.field(JSON_NO_REPEAT, 1);
		}
		if (role != 0) {
			builder.field(JSON_ROLE, role);
		}
	}
	public static ArrayList<Picture> fromJSArray(Object obj,JSONParser parser) {
		int l = parser.length(obj);
		ArrayList<Picture> list = new ArrayList<Picture>();
		for (int i = 0; i < l; i++) {
			Object row = parser.getRow(obj, i);
			list.add(fromJS(row,parser));
		}
		return list;
	}
	
	public static Picture fromJS(Object object,JSONParser parser){
		Picture picture = new Picture();
		picture.id = parser.propertyString(object, JSON_ID);
		picture.url = parser.propertyString(object, JSON_URL);
		picture.widht = parser.propertyNoCheckInt(object, JSON_WIDTH);
		picture.height = parser.propertyNoCheckInt(object, JSON_HEIGHT);
		picture.bigWidht = parser.propertyNoCheckInt(object, JSON_BIG_WIDTH);
		picture.bigHeight = parser.propertyNoCheckInt(object, JSON_BIG_HEIGHT);
		picture.name = parser.propertyNoCheckString(object, JSON_NAME);
		picture.noRepeat = parser.propertyNoCheckInt(object, JSON_NO_REPEAT)>0;
		picture.role = parser.propertyNoCheckInt(object, JSON_ROLE);
		picture.bigUrl = parser.propertyNoCheckString(object, JSON_BIG_URL);
		return picture;
	}
	public boolean isNoRepeat() {
		return noRepeat;
	}
	public void setNoRepeat(boolean noRepeat) {
		this.noRepeat = noRepeat;
	}
	public int getWidht() {
		return widht;
	}
	public void setWidht(int widht) {
		this.widht = widht;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}

	public int getBigWidht() {
		return bigWidht;
	}

	public void setBigWidht(int bigWidht) {
		this.bigWidht = bigWidht;
	}

	public int getBigHeight() {
		return bigHeight;
	}

	public void setBigHeight(int bigHeight) {
		this.bigHeight = bigHeight;
	}
	
}
