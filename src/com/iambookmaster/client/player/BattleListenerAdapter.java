package com.iambookmaster.client.player;

import com.iambookmaster.client.beans.NPC;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;

public abstract class BattleListenerAdapter implements BattleListener {

	private static final int ROUND_HERO=0; 
	private static final int ROUND_FRIEND=1; 
	private static final int ROUND_ENEMY=2;
	
	private int hero; 
	private AppConstants appConstants;
	private AppMessages appMessages;
	private PlayerState playerState;
	

	public BattleListenerAdapter(AppConstants appConstants,AppMessages appMessages, PlayerState playerState) {
		this.appConstants = appConstants;
		this.appMessages = appMessages;
		this.playerState = playerState;
	}

	public void battleEffort(int attack, int defense, int damage) {
		if (playerState.getCurrentBattle().isAttackDefense()) {
			addMessage(appMessages.battleEffortAD(attack,defense,damage));
		} else {
			String whom;
			switch (hero) {
			case ROUND_ENEMY:
				if (attack>defense) {
					whom = appConstants.battleDamageToVictim();
				} else {
					whom = appConstants.battleDamageToNPC();
				}
				break;
			case ROUND_FRIEND:
				if (attack>defense) {
					whom = appConstants.battleDamageToNPCFromVictim();
				} else {
					whom = appConstants.battleDamageToVictimFromNPC();
				}
				break;
			default://hero
				if (attack>defense) {
					whom = appConstants.battleDamageToNPC();
				} else {
					whom = appConstants.battleDamageToHero();
				}
			}
			addMessage(appMessages.battleEffortAA(attack,defense,damage,whom));
		}
	}

	public void battleEffortDied() {
		addMessage(appConstants.battleDied());
	}
	
	public void battleEffortKill() {
		addMessage(appConstants.battleKill());
	}

	public void battleFatalStrike() {
		addMessage(appConstants.battleFatalStrike());
	}

	public void battleNoEffort(int attack, int defense) {
		if (playerState.getCurrentBattle().isAttackDefense()) {
			addMessage(appMessages.battleNoEffortAD(attack,defense));
		} else if (hero==ROUND_HERO){
			addMessage(appMessages.battleNoEffortAA(attack,defense));
		} else {
			//hero can defense only
			addMessage(appMessages.battleNoEffortAD(attack,defense));
		}
	}

	public void battleFatalStrikeBack() {
		addMessage(appConstants.battleFatalStrikeBack());
	}

	public void heroAttack(NPC npc) {
		hero=ROUND_HERO;
		addMessage(appMessages.battleHeroAttack(getNPCName(npc,true)));
	}

	public void heroDefence(NPC npc) {
		hero=ROUND_ENEMY;
		addMessage(appMessages.battleHeroDefense(getNPCName(npc,false)));
	}
	
	private String getNPCName(NPC npc,boolean genitive) {
		if (genitive && npc.getGenitiveName() != null) {
			return npc.getGenitiveName().replace('<', ' ');
		} else {
			return npc.getName().replace('<', ' ');
		}
	}
	
	public void victimAttack(NPC victim, NPC enemy) {
		hero=ROUND_FRIEND;
		addMessage(appMessages.battleVictimAtack(getNPCName(victim,false),getNPCName(enemy,true)));
	}

	public void victimDefence(NPC victim, NPC enemy) {
		hero=ROUND_ENEMY;
		addMessage(appMessages.battleVictimDefence(getNPCName(victim,true),getNPCName(enemy,false)));
	}

	protected abstract void addMessage(String message);
}
