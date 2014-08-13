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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author bguijt
 */
public class IPhoneFileBean extends JavaScriptObject {

	protected IPhoneFileBean() {}
  
  	public final native String getName() /*-{
		return this.name;
	}-*/;
  	
  	public final native void setName(String name) /*-{
		this.name = name;
	}-*/;
  	
  	public final native String getPath() /*-{
		return this.path;
	}-*/;
	
	public final native void setPath(String path) /*-{
		this.path = path;
	}-*/;
	
  	
	
}
