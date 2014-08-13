package com.iambookmaster.client.beans;

import java.io.Serializable;
import java.util.ArrayList;

import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;

public class Greeting implements Serializable{

	private static final long serialVersionUID = 1L;

	private static final String JSON_NAME = "a";
	private static final String JSON_URL = "b";
	private static final String JSON_IMAGE = "c";
	private static final String JSON_TEXT = "d";
	
	private String name;
	private String url;
	private String imageUrl;
	private String text;
	public String getImageUrl() {
		return imageUrl == null ? "":imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getName() {
		return name == null ? "":name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getText() {
		return text==null?"":text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getUrl() {
		return url==null? "":url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void toJSON(JSONBuilder builder) {
		builder.newRow();
		builder.field(JSON_NAME, name);
		if (url != null && url.length()>0) {
			builder.field(JSON_URL, url);
		}
		if (imageUrl != null && imageUrl.length()>0) {
			builder.field(JSON_IMAGE, imageUrl);
		}
		if (text != null && text.length()>0) {
			builder.field(JSON_TEXT, text);
		}
	}

	public static ArrayList<Greeting> fromJSArray(Object object,JSONParser parser){
		int l = parser.length(object);
		ArrayList<Greeting> list = new ArrayList<Greeting>();
		for (int i = 0; i < l; i++) {
			Object row = parser.getRow(object, i);
			list.add(fromJS(row,parser));
		}
		return list;
	}
	
	public static Greeting fromJS(Object obj,JSONParser parser) {
		Greeting object = new Greeting();
		object.name = parser.propertyString(obj, JSON_NAME);
		object.url = parser.propertyNoCheckString(obj, JSON_URL);
		object.imageUrl = parser.propertyNoCheckString(obj, JSON_IMAGE);
		object.text = parser.propertyNoCheckString(obj, JSON_TEXT);
		return object;
	}
	

}
