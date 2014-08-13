package com.iambookmaster.client.player;

import com.iambookmaster.client.beans.NPC;


public interface BattleListener {

	void battleNoEffort(int attack, int defense);

	void battleEffort(int attack, int defense, int damage);

	void battleFatalStrike();

	void battleEffortKill();

	void battleFatalStrikeBack();

	void heroAttack(NPC npc);

	void heroDefence(NPC npc);

	void victimAttack(NPC victim, NPC enemy);

	void victimDefence(NPC victim, NPC enemy);

	void battleEffortDied();

}
