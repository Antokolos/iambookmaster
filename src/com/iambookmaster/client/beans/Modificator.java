package com.iambookmaster.client.beans;

import java.util.HashMap;

import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;

public class Modificator extends AbstractParameter {

	private static final long serialVersionUID = 1L;

	private static final String JSON_ABSOLUTE = "A";

	private boolean absolute;
	
	public Modificator() {
		type = AbstractParameter.TYPE_MODIFICATOR;
	}

	@Override
	public void toJSON(JSONBuilder builder, int export) {
		super.toJSON(builder, export);
		if (absolute) {
			builder.field(JSON_ABSOLUTE, 1);
		}
	}
	
	@Override
	protected void fromJSON(Object row, JSONParser parser,HashMap<String, AbstractParameter> parametersMap,HashMap<String,Picture> pictures) {
		absolute = parser.propertyNoCheckInt(row, JSON_ABSOLUTE) !=0;
	}

	@Override
	public boolean dependsOn(AbstractParameter parameter) {
		//modificator is always independent
		return false;
	}

	public boolean isAbsolute() {
		return absolute;
	}

	public void setAbsolute(boolean absolute) {
		this.absolute = absolute;
	}

//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof Modificator) {
//			return getId().equals(((Modificator) obj).getId());
//		} else {
//			return false;
//		}
//	}

//	@Override
//	public int hashCode() {
//		return getId().hashCode();
//	}

	
}
