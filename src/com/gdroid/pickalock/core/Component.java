package com.gdroid.pickalock.core;

import com.gdroid.pickalock.utils.SLog;

/**
 * Building blocks of each game object. Reacts to some internal or external events,
 * raises flags and changes host fields. They are independent from each other.
 * @author kboom
 *
 */
public abstract class Component extends BaseObject {
		
	private boolean isActive = true;
	
	
	public boolean isActive() {
		return isActive;
	}
	
	public void activate() {
		isActive = true;
	}
	
	public void deactivate() {
		isActive = false;
	}

	/* enforce all components to implement this method from superclass */
	protected abstract void update(float timeDelta, BaseObject parent);
	
}
