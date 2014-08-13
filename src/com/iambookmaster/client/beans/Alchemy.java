package com.iambookmaster.client.beans;

import java.util.HashMap;

import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.exceptions.JSONException;

public class Alchemy extends AbstractParameter {

	private static final long serialVersionUID = 1L;

	private static final String JSON_AVAILABLE_IN_BATTLE = "A";
	private static final String JSON_FROM = "B";
	private static final String JSON_TO = "C";
	private static final String JSON_TO_VALUE = "E";
	private static final String JSON_FROM_VALUE = "F";
	private static final String JSON_ON_DEMAND = "G";
	private static final String JSON_WEAPON = "H";
	private static final String JSON_ONE_TIME_PER_ROUND = "M";
	private static final String JSON_BATTLE_LIMIT = "N";
	private static final String JSON_OVERFLOW_CONTROL = "L";

	public static final int PLACE_PEACE=0;
	public static final int PLACE_BATTLE=1;
	public static final int PLACE_BOTH=2;


	
	private int place;
	private boolean onDemand;
	private boolean weapon;
	private Parameter from;
	private Parameter to;
	private DiceValue fromValue;
	private DiceValue toValue;
	private boolean oneTimePerRound;
	private Parameter battleLimit;
	private boolean overflowControl;
	
	public Alchemy() {
		type = AbstractParameter.TYPE_ALCHEMY;
	}

	@Override
	public void toJSON(JSONBuilder builder, int export) {
		super.toJSON(builder, export);
		builder.field(JSON_AVAILABLE_IN_BATTLE, place);
		if (from != null) {
			builder.field(JSON_FROM, from.getId());
		}
		if (to != null) {
			builder.field(JSON_TO, to.getId());
		}
		builder.field(JSON_FROM_VALUE, fromValue.getJSON());
		builder.field(JSON_TO_VALUE, toValue.getJSON());
		if (weapon) {
			builder.field(JSON_WEAPON, 1);
		}
		if (onDemand) {
			builder.field(JSON_ON_DEMAND, 1);
		}
		if (oneTimePerRound) {
			builder.field(JSON_ONE_TIME_PER_ROUND, 1);
		}
		if (battleLimit != null) {
			builder.field(JSON_BATTLE_LIMIT, battleLimit.getId());
		}
		if (overflowControl) {
			builder.field(JSON_OVERFLOW_CONTROL, 1);
		}
	}
	
	@Override
	protected void fromJSON(Object row, JSONParser parser,HashMap<String, AbstractParameter> parametersMap,HashMap<String,Picture> pictures) throws JSONException {
		place = parser.propertyNoCheckInt(row, JSON_AVAILABLE_IN_BATTLE);
		//from
		String id = parser.propertyNoCheckString(row, JSON_FROM);
		if (id != null) {
			AbstractParameter parameter = parametersMap.get(id);
			if (parameter instanceof Parameter) {
				from = (Parameter) parameter;
			} else {
				throw new JSONException("Unknown Parameter with ID="+id);
			}
		}
		//to
		id = parser.propertyNoCheckString(row, JSON_TO);
		if (id != null) {
			AbstractParameter parameter = parametersMap.get(id);
			if (parameter instanceof Parameter) {
				to = (Parameter) parameter;
			} else {
				throw new JSONException("Unknown Parameter with ID="+id);
			}
		}
		id = parser.propertyNoCheckString(row, JSON_BATTLE_LIMIT);
		if (id != null) {
			AbstractParameter parameter = parametersMap.get(id);
			if (parameter instanceof Parameter) {
				battleLimit = (Parameter) parameter;
			} else {
				throw new JSONException("Unknown Parameter with ID="+id);
			}
		}
		String dice = parser.propertyString(row, JSON_FROM_VALUE);
		fromValue = new DiceValue(dice);
		dice = parser.propertyString(row, JSON_TO_VALUE);
		toValue = new DiceValue(dice);
		weapon = parser.propertyNoCheckInt(row, JSON_WEAPON)==1;
		onDemand = parser.propertyNoCheckInt(row, JSON_ON_DEMAND)==1;
		oneTimePerRound = parser.propertyNoCheckInt(row, JSON_ONE_TIME_PER_ROUND)==1;
		overflowControl = parser.propertyNoCheckInt(row, JSON_OVERFLOW_CONTROL)==1;
	}

	@Override
	public boolean dependsOn(AbstractParameter parameter) {
		return from==parameter || to==parameter || battleLimit==parameter;
	}

	public Parameter getFrom() {
		return from;
	}

	public void setFrom(Parameter from) {
		this.from = from;
	}

	public Parameter getTo() {
		return to;
	}

	public void setTo(Parameter to) {
		this.to = to;
	}

	public int getPlace() {
		return place;
	}

	public void setPlace(int place) {
		this.place = place;
	}

	public boolean isOnDemand() {
		return onDemand;
	}

	public void setOnDemand(boolean onDemand) {
		this.onDemand = onDemand;
	}

	public int getFromValue() {
		return fromValue.getConstant();
	}

	public void setFromValue(int value) {
		if (fromValue==null) {
			fromValue = new DiceValue(6,0,value);
		} else {
			fromValue.setConstant(value);
		}
	}

	public DiceValue getToValue() {
		return toValue;
	}

	public void setToValue(DiceValue toValue) {
		this.toValue = toValue;
	}

	public boolean isWeapon() {
		return weapon;
	}

	public void setWeapon(boolean weapon) {
		this.weapon = weapon;
	}

	public boolean isOneTimePerRound() {
		return oneTimePerRound;
	}

	public void setOneTimePerRound(boolean oneTimePerRound) {
		this.oneTimePerRound = oneTimePerRound;
	}

	public Parameter getBattleLimit() {
		return battleLimit;
	}

	public void setBattleLimit(Parameter battleLimit) {
		this.battleLimit = battleLimit;
	}

	public boolean isOverflowControl() {
		return overflowControl;
	}

	public void setOverflowControl(boolean overflowControl) {
		this.overflowControl = overflowControl;
	}



}
