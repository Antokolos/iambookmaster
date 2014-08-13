package com.iambookmaster.client.beans;

import java.util.HashMap;
import java.util.Map;

import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.exceptions.JSONException;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.player.BattleListener;

public class Battle extends AbstractParameter{

	private static final long serialVersionUID = 1L;

	private static final String JSON_ONE_TURN = "A";
	private static final String JSON_ATTACK_DEFENCE = "B";
	private static final String JSON_ATTACK = "C";
	private static final String JSON_DEFENSE = "D";
	private static final String JSON_VITAL = "E";
	private static final String JSON_DAMAGE = "F";
	private static final String JSON_DEFFERENCE_IS_DAMAGE = "H";
	private static final String JSON_FATAL = "I";
	
	public static final String FATAL_NONE_STR = "0";
	public static final String FATAL_DEAD_STR = "1";
	public static final String FATAL_NORMAL_STR = "2";
	public static final int FATAL_NONE = 0;
	public static final int FATAL_DEAD = 1;
	public static final int FATAL_NORMAL = 2;
	
	private boolean oneTurnBattle;
	private boolean attackDefense;
	private ParametersCalculation attack=new ParametersCalculation();
	private ParametersCalculation defense=new ParametersCalculation();
	private Parameter vital;
	private ParametersCalculation damage=new ParametersCalculation();
	private boolean differenceIsDamage;
	private int fatal;
	
	public Battle() {
		type = AbstractParameter.TYPE_BATTLE;
	}

	@Override
	public void toJSON(JSONBuilder builder, int export) {
		super.toJSON(builder, export);
		if (oneTurnBattle) {
			builder.field(JSON_ONE_TURN, 1);
		}
		if (vital != null){
			builder.field(JSON_VITAL, vital.getId());
		}
		if (attackDefense) {
			builder.field(JSON_ATTACK_DEFENCE, 1);
		} 
		if (attackDefense || export==Model.EXPORT_ALL) {
			JSONBuilder subBuilder = builder.getInstance();
			defense.toJSON(subBuilder,export);
			builder.child(JSON_DEFENSE, subBuilder);
		}
		if (differenceIsDamage) {
			builder.field(JSON_DEFFERENCE_IS_DAMAGE, 1);
		} 
		if (!differenceIsDamage || export==Model.EXPORT_ALL) {
			JSONBuilder subBuilder = builder.getInstance();
			damage.toJSON(subBuilder,export);
			builder.child(JSON_DAMAGE, subBuilder);
		}
		builder.field(JSON_FATAL, fatal);
		JSONBuilder subBuilder = builder.getInstance();
		attack.toJSON(subBuilder,export);
		builder.child(JSON_ATTACK, subBuilder);
	}
	
	@Override
	protected void fromJSON(Object row, JSONParser parser,HashMap<String, AbstractParameter> parametersMap,HashMap<String,Picture> pictures) throws JSONException {
		oneTurnBattle = parser.propertyNoCheckInt(row, JSON_ONE_TURN) != 0;
		attackDefense = parser.propertyNoCheckInt(row, JSON_ATTACK_DEFENCE) != 0;
		differenceIsDamage = parser.propertyNoCheckInt(row, JSON_DEFFERENCE_IS_DAMAGE) != 0; 
		fatal = parser.propertyInt(row, JSON_FATAL); 
		String key = parser.propertyNoCheckString(row, JSON_VITAL);
		if (key != null){
			AbstractParameter param = parametersMap.get(key);
			if (param instanceof Parameter) {
				vital = (Parameter) param;
			} else {
				throw new JSONException("Does not exists Parameter with ID="+key);
			}
		}
		//power of attack
		Object att = parser.propertyDirect(row, JSON_ATTACK);
		attack.fromJSON(att,parser,parametersMap);
		//power of damage
		att = parser.propertyDirectNoCheck(row, JSON_DAMAGE);
		if (att != null) {
			damage.fromJSON(att,parser,parametersMap);
		}
		//power of defense
		att = parser.propertyDirectNoCheck(row, JSON_DEFENSE);
		if (att != null) {
			defense.fromJSON(att,parser,parametersMap);
		}
	}

	@Override
	public boolean dependsOn(AbstractParameter parameter) {
		return parameter==vital || attack.dependsOn(parameter) || defense.dependsOn(parameter) || damage.dependsOn(parameter);
	}
	
	public boolean isOneTurnBattle() {
		return oneTurnBattle;
	}

	public void setOneTurnBattle(boolean oneTurnBattle) {
		this.oneTurnBattle = oneTurnBattle;
	}

	public boolean isAttackDefense() {
		return attackDefense;
	}

	public void setAttackDefense(boolean attackDefense) {
		this.attackDefense = attackDefense;
	}

	public ParametersCalculation getAttack() {
		return attack;
	}

	public void setAttack(ParametersCalculation attack) {
		this.attack = attack;
	}

	public ParametersCalculation getDefense() {
		return defense;
	}

	public void setDefense(ParametersCalculation defense) {
		this.defense = defense;
	}

	public Parameter getVital() {
		return vital;
	}

	public void setVital(Parameter vital) {
		this.vital = vital;
	}

	public ParametersCalculation getDamage() {
		return damage;
	}

	public void setDamage(ParametersCalculation damage) {
		this.damage = damage;
	}

	public boolean isDifferenceIsDamage() {
		return differenceIsDamage;
	}

	public void setDifferenceIsDamage(boolean differenceIsDamage) {
		this.differenceIsDamage = differenceIsDamage;
	}

	public int getFatal() {
		return fatal;
	}

	public void setFatal(int fatal) {
		this.fatal = fatal;
	}

	public BattleRound calculateBattleRound(Map<Parameter, Integer> parameters) {
		BattleRound round = new BattleRound();
		round.attack = attack.calculate(parameters);
		if (fatal != FATAL_NONE) {
			round.fatal = attack.isFatal();
		}
		if (attackDefense) {
			//attacke vs. defence
			round.defense = defense.calculate(parameters);
		}
		return round;
	}
	
	public void attack(Map<Parameter, Integer> attackParameters,	BattleRound attackRound,
					   Map<Parameter, Integer> defenceParameters,	BattleRound defenceRound, 
					   BattleListener playerListener,boolean heroAttack) {
		if (((fatal == FATAL_DEAD && heroAttack)|| fatal==FATAL_NORMAL) && attackRound.fatal) {
			//fatal strike
			playerListener.battleFatalStrike();
			if (fatal==FATAL_DEAD) {
				//kill 
				playerListener.battleEffortKill();
				defenceParameters.put(vital,0);
			} else {
				//max damage
				attackHit(defenceParameters,attackRound.attack,0,attackParameters,playerListener,true);
			}
		} else if (oneTurnBattle) {
			//one turn battle
			if (attackRound.attack > defenceRound.attack) {
				playerListener.battleFatalStrike();
				defenceParameters.put(vital,0);
			} else if (attackRound.attack < defenceRound.attack) {
				playerListener.battleFatalStrikeBack();
				attackParameters.put(vital,0);
			} else {
				playerListener.battleNoEffort(attackRound.attack,defenceRound.attack);
			}
		} else if (attackDefense) {
			//two turn battle
			if (attackRound.attack > defenceRound.defense) {
				//damage
				attackHit(defenceParameters,attackRound.attack,defenceRound.defense,attackParameters,playerListener,true);
			} else {
				playerListener.battleNoEffort(attackRound.attack,defenceRound.defense);
			}
		} else {
			//attack to attack
			if (attackRound.attack > defenceRound.attack) {
				attackHit(defenceParameters,attackRound.attack,defenceRound.attack,attackParameters,playerListener,true);
			} else if (heroAttack && attackRound.attack < defenceRound.attack && defenceRound.canAttack) {
				attackHit(attackParameters,defenceRound.attack,attackRound.attack,defenceParameters,playerListener,false);
			} else {
				playerListener.battleNoEffort(attackRound.attack,defenceRound.attack);
			}
			defenceRound.canAttack = false;
		}
	}

	private void attackHit(Map<Parameter, Integer> parameters, int attack, int defense,Map<Parameter, Integer> attackeParameters, BattleListener playerListener, boolean direction) {
		int val = parameters.get(vital);
		int dmg;
		if (differenceIsDamage) {
			dmg= attack - defense;
		} else {
			dmg = damage.calculate(attackeParameters);
		}
		if (direction) {
			playerListener.battleEffort(attack,defense,dmg);
		} else {
			playerListener.battleEffort(defense,attack,dmg);
		}
		val = val - dmg;
		if (val<=0) {
			//kill
			if (direction) {
				playerListener.battleEffortKill();
			} else {
				playerListener.battleEffortDied();
			}
			val = 0;
		}
		parameters.put(vital,val);
	}

	public class BattleRound{
		private int attack;
		private int defense;
		private boolean fatal;
		private boolean canAttack=true;//can kontr-attack
		
	}

}
