package com.gdroid.pickalock.core.validation;

import com.gdroid.pickalock.core.GameObject;
import com.gdroid.pickalock.core.GameObjectStatusCodes;

public class ActiveRunningCondition implements RunningCondition {

	@Override
	public boolean check(GameObject host) {
		if (!host.checkFlag(GameObjectStatusCodes.MOVE_ACCEPTED)
				|| host.checkFlag(GameObjectStatusCodes.MOVE_REJECTED))
			return false;
		else return true;
	}

}
