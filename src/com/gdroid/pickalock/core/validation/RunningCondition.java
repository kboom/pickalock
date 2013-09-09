package com.gdroid.pickalock.core.validation;

import com.gdroid.pickalock.core.GameObject;

public interface RunningCondition {
	public boolean check(GameObject host);
}
