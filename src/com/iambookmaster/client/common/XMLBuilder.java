package com.iambookmaster.client.common;

import java.sql.Timestamp;
import java.util.Date;

public class XMLBuilder extends JSONBuilder {

	public static final String FIELD_OBJECT = "item";
	private static final String FIELD_OBJECT_END = "</item>";

	public static XMLBuilder getStartInstance() {
		return new XMLBuilder();
	}
	
	public XMLBuilder getInstance() {
		return new XMLBuilder();
	}
	
	protected XMLBuilder() {
		buffer = new StringBuffer();
	}

	public void reset() {
		rows = 0;
		buffer.delete(0, buffer.length()-1);
		newField = true;
	}
	
	protected void appendFieldName(String name) {
		buffer.append('<');
		buffer.append(name);
		buffer.append(">");
	}
	protected void endField(String name) {
		buffer.append("</");
		buffer.append(name);
		buffer.append(">");
	}
	
	protected void endChildArray() {
		buffer.append(FIELD_OBJECT_END);
	}
	
	public void child(String name,JSONBuilder value) {
		appendFieldName(name);
		buffer.append(value.toString());
		endField(name);
	}
	
	public void childArray(String name,JSONBuilder value) {
		appendFieldName(name);
		buffer.append(value.toString());
		endField(name);
	}
	
	public void childArray(String name, String childArray) {
		appendFieldName(name);
		buffer.append(childArray);
		endField(name);
	}
	
	public void field(String name,Object value) {
		appendFieldName(name);
		if (value instanceof Date) {
			buffer.append(((Date)value).getTime());
			
		} else if (value instanceof Timestamp) {
			buffer.append(((Timestamp)value).getTime());
		} else {
			buffer.append(encodeUTF2XML(String.valueOf(value)));
		}
		endField(name);
	}

	public static String encodeUTF2XML(String textString) {
		if (textString==null || textString.length()==0) {
			return textString;
		}
		return textString.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&apos;").replace("\"", "&quot;");
	}

	public void newRow() {
		if (rows>0) {
			endField(FIELD_OBJECT);
		}
		rows++;
		newField = true;
		appendFieldName(FIELD_OBJECT);
	}

	public String toString() {
		return buffer.toString()+(rows > 0 ? FIELD_OBJECT_END:"");
	}
	public String toXML() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+buffer.toString()+(rows > 0 ? FIELD_OBJECT_END:"");
	}

}
