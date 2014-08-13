package com.iambookmaster.client.player;

import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.NPC;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.beans.Parameter;

public interface PlayerStateListener {

	void addObject(ObjectBean object);

	void removeObject(ObjectBean object);

	void useObject(ObjectBean object, boolean success);

	void reset();

	void finish();

	void lostObject(ObjectBean object);
	
	void changeParameter(Parameter parameter, int value);

	void changeModificator(Modificator parameter, boolean value);

	void battle(Battle parameter, boolean start);

	void enemy(NPC npc, boolean add);

	void enableConnection(ParagraphConnection connection);

	void disableConnection(ParagraphConnection connection);

}
