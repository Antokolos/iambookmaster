package com.iambookmaster.client.common;

import com.google.gwt.core.client.JavaScriptObject;

public class JSONParser {

	private static JSONParser parser = new JSONParser();
	
	public static JSONParser getInstance() {
		return parser;
	}
	
	public native static JavaScriptObject eval(String s)/*-{
		return eval('('+s+')'); 
	}-*/;
    
    public native static JavaScriptObject evalArray(String s)/*-{
		return eval('(['+s+'])'); 
	}-*/; 
	public native static String escape(String f)/*-{
		return escape(f);
	}-*/;

	public native static String mailToEncode(String sValue)/*-{
	   var   text = "", Ucode, ExitValue, s;
	   for (var i = 0; i < sValue.length; i++) {
	      s = sValue.charAt(i);
	      Ucode = s.charCodeAt(0);
	      var Acode = Ucode;
	      if (Ucode > 1039 && Ucode < 1104) {
	         Acode -= 848;
	         ExitValue = "%" + Acode.toString(16);
	      }
	      else if (Ucode == 1025) {
	         Acode = 168;
	         ExitValue = "%" + Acode.toString(16);
	      }
	      else if (Ucode == 1105) {
	         Acode = 184;
	         ExitValue = "%" + Acode.toString(16);         
	      }
	      else if (Ucode == 32) {
	         Acode = 32;
	         ExitValue = "%" + Acode.toString(16);         
	      }
	      else if (Ucode == 10){
	         Acode = 10;
	         ExitValue = "%0A";
	      }
	      else {
	         ExitValue = s;         
	      }
	      text = text + ExitValue;
	   }     
	   return text;
	}-*/;

    /**
     * Logger for debugging pure JS in host-mode. Useless in web-mode
     * @param out
     * Example: @com.gga.gwt.common.JSONProxy::log(Ljava/lang/String;)('YES ');
     */
    public static void log(String out) {
    	System.out.println(out);
    }

	/**
	 * Return lenght of JSON arrayt
	 * @param object
	 * @return
	 */
    public native int length(Object a)/*-{
		return a.length; 
	}-*/;

	/**
	 * Return row of JS Array
	 * @param o JS array
	 * @param r index of row
	 * @return
	 */
    public native Object getRow(Object o, int r)/*-{
		return o[r]; 
	}-*/; 
    
	/**
	 * Get field from JS object by name
	 * @param p
	 * @param f
	 * @return
	 */
	public native Object property(Object o, String f) /*-{
		return o[f];
	}-*/;
	
	/**
	 * Get field from JS object by name
	 * @param p
	 * @param f
	 * @return
	 */
	public native Object propertyNoCheck(Object o, String f) /*-{
		return (o[f] == undefined) ? null : o[f];
	}-*/;
	
	/**
	 * Get field from JS object by name
	 * @param p
	 * @param f
	 * @return
	 */
	public native String propertyNoCheckString(Object o, String f) /*-{
		return (o[f] == undefined) ? null : o[f];
	}-*/;
	
	/**
	 * Get field from JS object by name
	 * @param p
	 * @param f
	 * @return
	 */
	public native boolean propertyBoolean(Object o, String f) /*-{
		return o[f];
	}-*/;
	
	/**
	 * Get field from JS object by name
	 * @param p
	 * @param f
	 * @return false if field does not exists
	 */
	public native boolean propertyNoCheckBoolean(Object o, String f) /*-{
		return (o[f] == undefined) ? false : o[f];
	}-*/;
	/**
	 * Extract String value from JS object propertie
	 * @param o Object
	 * @param n Propery name
	 * @return String value
	 */
	public native String propertyString(Object o, String f) /*-{
		return o[f];
	}-*/;

	public native int propertyInt(Object o, String f) /*-{
		return o[f];
	}-*/;

	public native int propertyNoCheckInt(Object o, String f)/*-{
		return (o[f] == undefined) ? 0 : o[f];
	}-*/;

	public Object propertyDirect(Object o, String f) {
		return property(o, f);
	}

	public Object propertyDirectNoCheck(Object o, String f) {
		return propertyNoCheck(o, f);
	}



	

}
