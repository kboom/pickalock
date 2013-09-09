package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.LockMechanismSystem.Type;
import com.gdroid.pickalock.drawing.GameRenderer;

/**
 * Creates a volatile lockpick.
 * @author kboom
 *
 */
public class VolatileLockpickBuilder extends DefaultLockpickBuilder {

	public VolatileLockpickBuilder(GameRenderer renderer, Type type) {
		super(renderer, type);
	}

	@Override
	public void attachDurability(Lockpick t) {
		DurabilityComponent c = new LinearDurability();
		c.setDelay(10f);
		super.bDurability = c;
		super.attachDurability(t);
	}	
	
}
