package com.iambookmaster.client.beans;

import java.util.HashMap;

import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.exceptions.JSONException;
import com.iambookmaster.client.model.Model;

public class Parameter extends AbstractParameter{

	private static final long serialVersionUID = 1L;

	private static final String JSON_HERO_ONLY = "A";
	private static final String JSON_VITAL = "B";
	private static final String JSON_NEGATIVE = "C";
	private static final String JSON_HERO_INITIAL_VALUE = "D";
	private static final String JSON_LIMIT = "E";
	private static final String JSON_HERO_HAS_INITIAL = "F";
	private static final String JSON_INVISIBLE = "H";
	private static final String JSON_SUPRESS_ONE_VALUE = "N";

	/**
	 * Parameter only for main hero
	 */
	private boolean heroOnly;
	/**
	 * Critical parameter, 0 - dead
	 */
	private boolean vital;

	/**
	 * Value can be negative
	 */
	private boolean negative;
	
	/**
	 * Initial value for hero
	 */
	private DiceValue heroInitialValue;
	
	/**
	 * Initial value for hero exists
	 */
	private boolean heroHasInitialValue;
	
	/**
	 * Parameter invisible for end user
	 */
	private boolean invisible;
	
	/**
	 * Does not show value for =1
	 */
	private boolean suppressOneValue;
	
	private Parameter limit;
	
	private Picture icon;
	
	public Picture getIcon() {
		return icon;
	}

	public void setIcon(Picture icon) {
		this.icon = icon;
	}

	public boolean isSuppressOneValue() {
		return suppressOneValue;
	}

	public void setSuppressOneValue(boolean suppressOneValue) {
		this.suppressOneValue = suppressOneValue;
	}

	public Parameter() {
		type = AbstractParameter.TYPE_PARAMETER;
	}

	public boolean isInvisible() {
		return invisible;
	}

	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}

	@Override
	public void toJSON(JSONBuilder builder, int export) {
		super.toJSON(builder, export);
		if (heroOnly) {
			builder.field(JSON_HERO_ONLY, 1);
		}
		if (vital) {
			builder.field(JSON_VITAL, 1);
		}
		if (negative) {
			builder.field(JSON_NEGATIVE, 1);
		}
		if ((heroHasInitialValue || export==Model.EXPORT_ALL) && heroInitialValue != null) {
			builder.field(JSON_HERO_INITIAL_VALUE, heroInitialValue.getJSON());
		}
		if (heroHasInitialValue) {
			builder.field(JSON_HERO_HAS_INITIAL, 1);
		}
		
		if (limit != null) {
			builder.field(JSON_LIMIT, limit.getId());
		}

		if (invisible) {
			builder.field(JSON_INVISIBLE, 1);
		}
		if (suppressOneValue) {
			builder.field(JSON_SUPRESS_ONE_VALUE, 1);
		}
	}

	@Override
	protected void fromJSON(Object row, JSONParser parser,HashMap<String, AbstractParameter> parametersMap,HashMap<String,Picture> pictures) throws JSONException {
		heroOnly = parser.propertyNoCheckInt(row, JSON_HERO_ONLY) > 0;
		vital = parser.propertyNoCheckInt(row, JSON_VITAL) > 0;
		negative = parser.propertyNoCheckInt(row, JSON_NEGATIVE) > 0;
		invisible = parser.propertyNoCheckInt(row, JSON_INVISIBLE) > 0;
		suppressOneValue = parser.propertyNoCheckInt(row, JSON_SUPRESS_ONE_VALUE) > 0;
		heroHasInitialValue = parser.propertyNoCheckInt(row, JSON_HERO_HAS_INITIAL) > 0;
		String dice = parser.propertyNoCheckString(row,JSON_HERO_INITIAL_VALUE);
		if (dice != null) {
			heroInitialValue = new DiceValue(dice);
		}
		dice = parser.propertyNoCheckString(row, JSON_LIMIT);
		if (dice != null) {
			AbstractParameter abstractParameter = parametersMap.get(dice);
			if (abstractParameter instanceof Parameter) {
				limit = (Parameter) abstractParameter;
			} else {
				throw new JSONException("Unknown Parameter with ID="+dice);
			}
		}
	}

	public boolean isHeroOnly() {
		return heroOnly;
	}

	public void setHeroOnly(boolean heroOnly) {
		this.heroOnly = heroOnly;
	}

	public boolean isVital() {
		return vital;
	}

	public void setVital(boolean vital) {
		this.vital = vital;
	}

	public boolean isNegative() {
		return negative;
	}

	public void setNegative(boolean negative) {
		this.negative = negative;
	}

	public DiceValue getHeroInitialValue() {
		if (heroInitialValue==null) {
			heroInitialValue = new DiceValue();
		}
		return heroInitialValue;
	}

	public void setHeroInitialValue(DiceValue heroInitialValue) {
		this.heroInitialValue = heroInitialValue;
	}
	
	@Override
	public boolean dependsOn(AbstractParameter parameter) {
		return limit==parameter;
	}

	public Parameter getLimit() {
		return limit;
	}

	public void setLimit(Parameter limit) {
		this.limit = limit;
	}

	public boolean isHeroHasInitialValue() {
		return heroHasInitialValue;
	}

	public void setHeroHasInitialValue(boolean heroHasInitialValue) {
		this.heroHasInitialValue = heroHasInitialValue;
	}
	
	
}
