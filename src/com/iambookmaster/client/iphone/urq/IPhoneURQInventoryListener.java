package com.iambookmaster.client.iphone.urq;

import com.iambookmaster.client.iurq.logic.InvVar;
import com.iambookmaster.client.iurq.logic.InvVar.Action;

public interface IPhoneURQInventoryListener {

	void useInventory(String name, InvVar var);

	void useInventory();

	void forward();

	void back();

	void doAction(Action action);

}
