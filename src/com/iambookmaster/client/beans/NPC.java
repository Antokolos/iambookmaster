package com.iambookmaster.client.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.exceptions.JSONException;

public class NPC extends AbstractParameter{
	
	private static final long serialVersionUID = 1L;

	private static final String JSON_ID = "A";
	private static final String JSON_VALUE = "B";
	private static final String JSON_VALUES = "C";
	private static final String JSON_NAME_POINTER = "D";

	private LinkedHashMap<Parameter,Integer> values = new LinkedHashMap<Parameter, Integer>();
	private String genitiveName;
	public NPC() {
		type = AbstractParameter.TYPE_NPC;
	}


	public String getGenitiveName() {
		return genitiveName;
	}


	public void setGenitiveName(String genitiveName) {
		this.genitiveName = genitiveName;
	}


	@Override
	public void toJSON(JSONBuilder builder, int export) {
		super.toJSON(builder, export);
		if (values.size()>0) {
			ArrayList<ParameterValue> list = new ArrayList<NPC.ParameterValue>(values.size());
			for (Parameter parameter : values.keySet()) {
				list.add(new ParameterValue(parameter,values.get(parameter)));
			}
			Collections.sort(list, new Comparator<ParameterValue>(){
				public int compare(ParameterValue o1, ParameterValue o2) {
					return o1.parameter.getOrder() - o2.parameter.getOrder(); 
				}
			});
			JSONBuilder subBuilder = builder.getInstance();
			for (ParameterValue parameter : list) {
				subBuilder.newRow();
				subBuilder.field(JSON_ID, parameter.parameter.getId());
				subBuilder.field(JSON_VALUE, parameter.value.intValue());
			}
			builder.childArray(JSON_VALUES, subBuilder);
		}
		if (genitiveName != null && genitiveName.length()>0) {
			builder.field(JSON_NAME_POINTER,genitiveName);
		}
	}
	
	@Override
	protected void fromJSON(Object row, JSONParser parser,HashMap<String, AbstractParameter> parametersMap,HashMap<String,Picture> pictures) throws JSONException {
		Object vals = parser.propertyNoCheck(row, JSON_VALUES);
		if (vals != null) {
			int len = parser.length(vals);
			for (int i=0;i<len;i++) {
				Object rw = parser.getRow(vals, i);
				String id = parser.propertyString(rw, JSON_ID);
				int val = parser.propertyNoCheckInt(rw, JSON_VALUE);
				AbstractParameter parameter = parametersMap.get(id);
				if (parameter instanceof Parameter) {
					Parameter param = (Parameter) parameter;
					values.put(param, val);
				} else {
					throw new JSONException("Unknown Parameter with ID="+id);
				}
			}
		}
		genitiveName = parser.propertyNoCheckString(row,JSON_NAME_POINTER);
	}

	@Override
	public boolean dependsOn(AbstractParameter parameter) {
		return values.containsKey(parameter);
	}

	public HashMap<Parameter, Integer> getValues() {
		return values;
	}

	public class ParameterValue {
		private Parameter parameter;
		private Integer value;
		public Parameter getParameter() {
			return parameter;
		}
		public Integer getValue() {
			return value;
		}
		public ParameterValue(Parameter parameter, Integer value) {
			this.parameter = parameter;
			this.value = value;
		}
		public ParameterValue() {
		}
		
	}
	
}
