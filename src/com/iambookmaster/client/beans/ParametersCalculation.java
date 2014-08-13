package com.iambookmaster.client.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.exceptions.JSONException;
import com.iambookmaster.client.locale.AppMessages;

public class ParametersCalculation implements Serializable{

	private static final long serialVersionUID = 1L;

	private static final String JSON_CONSTANT = "a";
	private static final String JSON_PARAMETERS = "b";
	private static final String JSON_OVERFLOW_CONTROL = "c";
	public static final String JSON_ACCEPTOR = "z";
	
	private LinkedHashMap<Parameter, Integer> parameters = new LinkedHashMap<Parameter, Integer>();
	private DiceValue constant = new DiceValue(6,0,1);
	private boolean overflowControl;
	
	public void toJSON(JSONBuilder builder,int export) {
		builder.newRow();
		builder.field(JSON_CONSTANT, constant.getJSON());
		if (overflowControl) {
			builder.field(JSON_OVERFLOW_CONTROL, 1);
		}
		if (parameters.size()>0) {
			StringBuffer buffer = new StringBuffer();
			for (Parameter parameter : parameters.keySet()) {
				Integer value = parameters.get(parameter);
				if (value !=0) { 
					if (buffer.length()>0) {
						buffer.append(',');
					}
					buffer.append(value);
					buffer.append('+');
					buffer.append(parameter.getId());
				}
			}
			builder.field(JSON_PARAMETERS, buffer.toString());
		}
		
	}

	public void fromJSON(Object att, JSONParser parser,	HashMap<String, AbstractParameter> parametersMap) throws JSONException{
		String dice = parser.propertyString(att, JSON_CONSTANT);
		constant = new DiceValue(dice);
		overflowControl = parser.propertyNoCheckInt(att, JSON_OVERFLOW_CONTROL) == 1;
		dice = parser.propertyNoCheckString(att, JSON_PARAMETERS);
		if (dice != null) {
			String[]ids = dice.split(",");
			for (String id : ids) {
				int i = id.indexOf('+');
				int val;
				try {
					val = Integer.parseInt(id.substring(0,i));
					id = id.substring(i+1);
				} catch (Exception e) {
					throw new JSONException("Incorrect definition of parameter in calculation: ID="+id);
				}
				AbstractParameter abstractParameter = parametersMap.get(id);
				if (abstractParameter instanceof Parameter) {
					parameters.put((Parameter) abstractParameter, val);
				} else {
					throw new JSONException("Parameter does not exist ID="+id);
				}
			}
		}
	}

	public boolean dependsOn(AbstractParameter parameter) {
		return parameters.containsKey(parameter);
	}

	public LinkedHashMap<Parameter, Integer> getParameters() {
		return parameters;
	}

	public void setParameters(LinkedHashMap<Parameter, Integer> parameters) {
		this.parameters = parameters;
	}

	public DiceValue getConstant() {
		return constant;
	}

	public void setConstant(DiceValue constant) {
		this.constant = constant;
	}

	public boolean isOverflowControl() {
		return overflowControl;
	}

	public void setOverflowControl(boolean overflowControl) {
		this.overflowControl = overflowControl;
	}

	public int calculate(Map<Parameter, Integer> values){
		int value=constant.calculate();
		for (Parameter parameter : parameters.keySet()) {
			if (values.containsKey(parameter)) {
				int sign = parameters.get(parameter);
				value = value + sign*values.get(parameter);
			} else {
				//skip
			}
		}
		return value;
	}

	public boolean isFatal() {
		return constant != null && constant.isFatal();
	}
	
	public String toString(Parameter parameter, AppMessages appMessages) {
		if (parameters==null || parameters.size()==0) {
			//just constant Hits=12
			return appMessages.calculationSetParameter(parameter.getName(),constant.toString());
		} else if (parameters.containsKey(parameter) && parameters.size()==1 && parameters.get(parameter)==1) {
			//relative change
			if (constant.getN()==0 || constant.getSize()==0) {
				if (constant.getConstant()>0) {
					//A+2
					return appMessages.calculationIncParameter(parameter.getName(),constant.toString());
				} else {
					//A-2
					return appMessages.calculationDecParameter(parameter.getName(),constant.toAbsString());
				}
			} else if (constant.isPlus()){
				//A+2+1D6
				return appMessages.calculationAddParameter(parameter.getName(),constant.toString());
			} else {
				//A-2-1D6
				return appMessages.calculationSubParameter(parameter.getName(),constant.toAbsString());
			}
		} else if (constant.isZero() && parameter.getLimit() != null && parameters.size()==1 && parameters.containsKey(parameter.getLimit()) && parameters.get(parameter.getLimit())==1) {
			//restore to max
			//A=Amax
			return appMessages.calculationRestoreToMax(parameter.getName());
		} else {
			//unknown calculation
			StringBuilder builder = new StringBuilder(parameter.getName());
			builder.append('=');
			boolean first=true;
			for (Parameter param : parameters.keySet()) {
				int val = parameters.get(param);
				if (first) {
					first = false;
					if (val<0) {
						builder.append('-');
					}
				} else if (val>=0){
					builder.append('+');
				} else {
					builder.append('-');
				}
				builder.append(param.getName());
			}
			if (constant.isZero()==false) {
				if (constant.isPlus()) {
					builder.append('+');
				}
				builder.append(constant.toString());
			}
			return builder.toString();
		}
	}

	public String toString(AppMessages appMessages) {
		if (parameters==null || parameters.size()==0) {
			//just constant Hits=12
			return constant.toString();
		} else {
			//unknown calculation
			StringBuilder builder = new StringBuilder();
			boolean first=true;
			for (Parameter param : parameters.keySet()) {
				int val = parameters.get(param);
				if (first) {
					first = false;
					if (val<0) {
						builder.append('-');
					}
				} else if (val>=0){
					builder.append('+');
				} else {
					builder.append('-');
				}
				builder.append(param.getName());
			}
			if (constant.isZero()==false) {
				if (constant.isPlus()) {
					builder.append('+');
				}
				builder.append(constant.toString());
			}
			return builder.toString();
		}
	}
}
