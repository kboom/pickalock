package com.gdroid.pickalock.core.validation;

import com.gdroid.pickalock.core.GameObject;

public class PassiveRunningCondition implements RunningCondition {

	@Override
	public boolean check(GameObject host) {
		return true;
	}

}
