package com.iambookmaster.client.player;

import java.io.Serializable;

import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;

public class PlayerStateMetadata implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private static final String FIELD_AUTORUN = "a";
	
	private boolean autorun;

	public void fromJS(JSONParser parser, Object data) {
		autorun = parser.propertyNoCheckInt(data, FIELD_AUTORUN) > 0;
	}

	public void toJSON(JSONBuilder builder) {
		builder.newRow();
		if (autorun) {
			builder.field(FIELD_AUTORUN, 1);
		}
	}

	public boolean isAutorun() {
		return autorun;
	}

	public void setAutorun(boolean autorun) {
		this.autorun = autorun;
	}
	
	

}
