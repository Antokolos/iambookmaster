package com.iambookmaster.client.beans;

import java.io.Serializable;
import java.util.HashMap;

import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.exceptions.JSONException;

public class Sprite implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String FILED_IMAGE = "a";
	private static final String FILED_X = "b";
	private static final String FILED_Y = "c";
	
	private Picture picture;
	
	private int x;
	
	private int y;

	public Picture getPicture() {
		return picture;
	}

	public void setPicture(Picture picture) {
		this.picture = picture;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void toJSON(JSONBuilder builder, int export) {
		builder.newRow();
		builder.field(FILED_IMAGE, picture.getId());
		builder.field(FILED_X, x);
		builder.field(FILED_Y, y);
		
	}

	public void fromJSON(Object obj, JSONParser parser,	HashMap<String, Picture> pictures) throws JSONException {
		String id = parser.propertyString(obj, FILED_IMAGE);
		picture = pictures.get(id);
		if (picture==null) {
			throw new JSONException("Picture ID"+id+" does not exist");
		}
		x = parser.propertyInt(obj, FILED_X);
		y = parser.propertyInt(obj, FILED_Y);
	}
	

}
