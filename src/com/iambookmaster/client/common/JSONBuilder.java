package com.iambookmaster.client.common;

import java.sql.Timestamp;
import java.util.Date;

public class JSONBuilder {
	
	public static final String NULL = "null";

	protected StringBuffer buffer;
	protected boolean newField = true;
	protected int rows;
	protected boolean ignoreLenght;

	protected static final char[] hexequiv = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	
	public static JSONBuilder getStartInstance() {
		return new JSONBuilder();
	}
	
	public JSONBuilder getInstance() {
		return new JSONBuilder();
	}
	
	protected JSONBuilder() {
		buffer = new StringBuffer();
	}

	public void reset() {
		rows = 0;
		buffer.delete(0, buffer.length()-1);
		newField = true;
	}
	
	protected void appendFieldName(String name) {
		if (newField==false) {
			buffer.append(',');
		} else {
			newField=false;
		}
		buffer.append('"');
		buffer.append(name);
		buffer.append("\":");
	}
	
	private int oldLengtht;
	
	protected void endField(String name) {
		int l = buffer.length();
		if (l-oldLengtht>128) {
			buffer.append('\n');
		}
		oldLengtht = l;
	}
	
	protected void endChildArray() {
		buffer.append('}');
	}
	public void child(String name,JSONBuilder value) {
		appendFieldName(name);
		buffer.append(value.buffer.toString());
		endChildArray();
		endField(name);
	}
	
	public void childArray(String name,JSONBuilder value) {
		appendFieldName(name);
		buffer.append('[');
		String subJSON = ((JSONBuilder) value).buffer.toString();
		if (subJSON.length()>0) {
			buffer.append(subJSON);
			buffer.append("}]");
		} else {
			buffer.append(']');
		}
		endField(name);
	}
	
	public void childArray(String name, String childArray) {
		appendFieldName(name);
		buffer.append('[');
		buffer.append(childArray);
		buffer.append(']');
		endField(name);
	}
	
	public void child(String name, String child) {
		appendFieldName(name);
		buffer.append(child);
		endField(name);
	}
	
	public void field(String name,Object value) {
		appendFieldName(name);
		if (value instanceof Date) {
			buffer.append("new Date(");
			buffer.append(((Date)value).getTime());
			buffer.append(')');
			
		} else if (value instanceof Timestamp) {
			buffer.append("new Date(");
			buffer.append(((Timestamp)value).getTime());
			buffer.append(')');
			
		} else if (value instanceof Integer) {
			buffer.append(((Integer)value).intValue());
			
		} else if (value instanceof Boolean) {
			buffer.append(((Boolean)value).booleanValue());
			
		} else {
			buffer.append('"');
			buffer.append(encodeUTF2Esc(String.valueOf(value)));
			buffer.append('"');
		}
		endField(name);
	}

	public static String encodeUTF2Esc(String textString) {
		if (textString==null || textString.length()==0) {
			return textString;
		}
		StringBuffer buffer = new StringBuffer();
		char[] sym = textString.toCharArray();
		for (int i = 0; i < sym.length; i++) {
			char code = sym[i];
			switch (code) {
			case 0: buffer.append("\\0"); break;
			case 8: buffer.append("\\b"); break;
			case 9: buffer.append("\\t"); break;
			case 10: buffer.append("\\n"); break;
			case 13: buffer.append("\\r"); break;
			case 11: buffer.append("\\v"); break;
			case 12: buffer.append("\\f"); break;
			case 34: buffer.append("\\\""); break;
			case 39: buffer.append("\\\'"); break;
			case 92: buffer.append("\\\\"); break;
			default: 
				if (code > 0x1f && code < 0x7F) { 
					buffer.append(code); 
				} else { 
					buffer.append("\\u");
					buffer.append(hexequiv[(code >> 12) & 0xF]);
					buffer.append(hexequiv[(code >> 8) & 0xF]);
					buffer.append(hexequiv[(code >> 4) & 0xF]);
					buffer.append(hexequiv[code & 0xF]);
				}
			}
		}
		return buffer.toString();
	}
	/**
	 * It is for Java 1.4 compatibility
	 */
	public void field(String name,int value) {
		appendFieldName(name);
		buffer.append(value);
		endField(name);
	}

	/**
	 * It is for Java 1.4 compatibility
	 */
	public void field(String name,boolean value) {
		appendFieldName(name);
		buffer.append(value);
		endField(name);
		
	}

	/**
	 * It is for Java 1.4 compatibility
	 */
	public void field(String name,float value) {
		appendFieldName(name);
		buffer.append(value);
		endField(name);
		
	}

	/**
	 * It is for Java 1.4 compatibility
	 */
	public void field(String name,double value) {
		appendFieldName(name);
		buffer.append(value);
		endField(name);
		
	}

	public void newRow() {
		if (rows>0) {
			//close previous
			buffer.append('}');
		}
		if (buffer.length()>0) {
			if (ignoreLenght) {
				ignoreLenght = false;
			} else {
				buffer.append(',');
			}
		}
		rows++;
		buffer.append('{');
		newField = true;
	}

	public String toString() {
		return buffer.toString()+(rows > 0 ? "}":"");
	}
	public boolean isEmpty() {
		return buffer.length()==0;
	}

}
