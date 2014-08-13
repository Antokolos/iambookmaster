package com.iambookmaster.client.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.exceptions.JSONException;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.model.Model;

public class ObjectBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private static final String JSON_ID = "id";
	private static final String JSON_NAME = "name";
	private static final String JSON_COMMENTS = "a";
	private static final String JSON_DESCRIPTION = "b";
	private static final String JSON_KEY = "c";
	private static final String JSON_UNCOUNTABLE = "d";
	private static final String JSON_ICON = "e";

	private String id;
	private String name;
	private String misuse;
	private String masterComments;
	private int key;
	private boolean uncountable;
	private Picture icon;
	
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name==null ? "":name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isUncountable() {
		return uncountable;
	}
	public void setUncountable(boolean uncountable) {
		this.uncountable = uncountable;
	}
	public void toJSON(JSONBuilder builder,int export) {
		builder.newRow();
		builder.field(JSON_ID, id);
		builder.field(JSON_NAME, name);
		if (getDescription().length()>0) {
			builder.field(JSON_DESCRIPTION, getDescription());
		}
		if (icon != null) {
			builder.field(JSON_ICON, icon.getId());
		}
		if (export==Model.EXPORT_ALL) {
			if (getMasterComments().length()>0) {
				builder.field(JSON_COMMENTS, getMasterComments());
			}
			if (key != 0) {
				builder.field(JSON_KEY, key);
			}
			if (uncountable) {
				builder.field(JSON_UNCOUNTABLE, 1);
			}
		}
	}

	public static ArrayList<ObjectBean> fromJSArray(Object object,JSONParser parser, HashMap<String, Picture> imagesMap) throws JSONException {
		int l = parser.length(object);
		ArrayList<ObjectBean> list = new ArrayList<ObjectBean>();
		for (int i = 0; i < l; i++) {
			Object row = parser.getRow(object, i);
			list.add(fromJS(row,parser,imagesMap));
		}
		return list;
	}
	
	public static ObjectBean fromJS(Object obj,JSONParser parser, HashMap<String, Picture> imagesMap)throws JSONException  {
		ObjectBean object = new ObjectBean();
		object.id = parser.propertyString(obj, JSON_ID);
		object.name = parser.propertyNoCheckString(obj, JSON_NAME);
		object.masterComments = parser.propertyNoCheckString(obj, JSON_COMMENTS);
		object.misuse = parser.propertyNoCheckString(obj, JSON_DESCRIPTION);
		object.key = parser.propertyNoCheckInt(obj, JSON_KEY);
		object.uncountable = parser.propertyNoCheckInt(obj, JSON_UNCOUNTABLE)==1;
		String id = parser.propertyNoCheckString(obj, JSON_ICON);
		if (id != null) {
			object.icon = imagesMap.get(id);
			if (object.icon==null) {
				throw new JSONException("Image "+id+" does not exist");
			}
		}
		return object;
	}
	public String getDescription() {
		return misuse==null ? "":misuse;
	}
	public void setDescription(String description) {
		misuseMessages = null;
		this.misuse = description;
	}
	public String getMasterComments() {
		return masterComments==null ? "":masterComments;
	}
	public void setMasterComments(String comments) {
		this.masterComments = comments;
	}
	
	private transient String[] misuseMessages;
	private transient int misuseMessageCounter;
	public String getNextMissusedMessage(AppConstants appConstants) {
		if (misuseMessages != null) {
			//already initialized
			if (misuseMessageCounter>=misuseMessages.length) {
				misuseMessageCounter = 0;
			}
			int i = misuseMessageCounter;
			while (true) {
				if (misuseMessages[i].trim().length()!=0) {
					misuseMessageCounter = i + 1;
					return misuseMessages[i];
				}
				i++;
				if (i>=misuseMessages.length) {
					i = 0;
				}
				if (i==misuseMessageCounter) {
					return appConstants.getDefaumtMissuseMessage();
				}
			}
		}
		if (misuse==null || misuse.length()==0) {
			return appConstants.getDefaumtMissuseMessage();
		}
		//has messages
		misuseMessages = misuse.split("\n");
		return getNextMissusedMessage(appConstants);
	}
	public Picture getIcon() {
		return icon;
	}
	public void setIcon(Picture icon) {
		this.icon = icon;
	}
	
}
