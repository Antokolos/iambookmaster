package com.iambookmaster.client.beans;

import java.util.HashMap;

import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.exceptions.JSONException;

public class NPCParams {
	
	private static final String JSON_NPC = "a";
	private static final String JSON_ROUND = "b";
	private static final String JSON_FRIEND = "c";
	
	private NPC npc;
	private int round;
	private boolean friend;

	public NPC getNpc() {
		return npc;
	}

	public void setNpc(NPC npc) {
		this.npc = npc;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public boolean isFriend() {
		return friend;
	}

	public void setFriend(boolean friend) {
		this.friend = friend;
	}

	public void toJSON(JSONBuilder builder, int export) {
		builder.newRow();
		builder.field(JSON_NPC, npc.getId());
		if (round != 0) {
			builder.field(JSON_ROUND, round);
		}
		if (friend) {
			builder.field(JSON_FRIEND, 1);
		}
	}

	public static NPCParams fromJS(Object row, JSONParser parser, HashMap<String, AbstractParameter> parametersMap) throws JSONException {
		String objectId = parser.propertyString(row, JSON_NPC);
		AbstractParameter parameter = parametersMap.get(objectId);
		NPCParams result = new NPCParams();
		if (parameter instanceof NPC) {
			//found
			result.npc = (NPC)parameter;
		} else {
			throw new JSONException("Unknown NPC with ID="+objectId);
		}
		result.round = parser.propertyNoCheckInt(row, JSON_ROUND);
		result.friend = parser.propertyNoCheckInt(row, JSON_FRIEND)>0;
		return result;
	}

	public NPCParams(NPC npc) {
		this.npc = npc;
	}

	public NPCParams() {
	}

	public HashMap<Parameter, Integer> getValues() {
		return npc.getValues();
	}

}
