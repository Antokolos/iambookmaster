/*
 * Copyright 2009 Bart Guijt and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.iambookmaster.client.iphone.data;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author bguijt
 */
public class IPhoneSaveGameBean extends JavaScriptObject {

	protected IPhoneSaveGameBean() {}
  
  	/* (non-Javadoc)
	 * @see com.iambookmaster.client.iphone.data.IPhoneGameBean#getWhen()
	 */
  	public final Date getWhen() {
  		long time = Math.round(this.getDate());
  		return new Date(time);
  	}

  	/**
  	 * @return the 'clicked' property (an integer) as a Java Date (SQLite does not support DATE types).
  	 */
  	private final native double getDate() /*-{
    	return this.stamp;
	}-*/;

  	/* (non-Javadoc)
	 * @see com.iambookmaster.client.iphone.data.IPhoneGameBean#getId()
	 */
  	public final native int getId() /*-{
		return this.id;
	}-*/;
  
  	/* (non-Javadoc)
	 * @see com.iambookmaster.client.iphone.data.IPhoneGameBean#getGameId()
	 */
  	public final native String getGameId() /*-{
  		return this.gameId;
	}-*/;
  	
  	/* (non-Javadoc)
	 * @see com.iambookmaster.client.iphone.data.IPhoneGameBean#getName()
	 */
  	public final native String getName() /*-{
		return this.name;
	}-*/;
	
  	/* (non-Javadoc)
	 * @see com.iambookmaster.client.iphone.data.IPhoneGameBean#getData()
	 */
  	public final native String getData() /*-{
		return this.data;
	}-*/;

  	/* (non-Javadoc)
	 * @see com.iambookmaster.client.iphone.data.IPhoneGameBean#getLocation()
	 */
  	public final native String getLocation() /*-{
		return this.location;
	}-*/;
  	
}
