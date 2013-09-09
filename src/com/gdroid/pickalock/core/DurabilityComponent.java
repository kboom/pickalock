package com.gdroid.pickalock.core;

import com.gdroid.pickalock.utils.SLog;

/**
 * Stores and manages durability of a component. If durability reaches zero,
 * this component sends
 * {@link com.gdroid.pickalock.core.EventCodes.ZERO_DURABILITY } event code with
 * its parent. Object is being damaged if its current position does not match
 * target one.
 * 
 * @author kboom
 * 
 */
public abstract class DurabilityComponent extends Component {

	private static final int did = SLog.register(DurabilityComponent.class);
	static {
		SLog.setTag(did, "Durability component.");
	}
	
	private float durability;
	private float damageTime;
	private float damageDelay;

	public DurabilityComponent() {
		durability = 1f;
		damageTime = 0f;
		damageDelay = 0f;
	}
	
	public void setDelay(float time) {
		damageDelay = time;
	}

	public float getDurability() {
		return durability;
	}

	@Override
	public final void update(float timeDelta, BaseObject parent) {
		if (durability <= 0) {
			system.event.trigger(EventCodes.ZERO_DURABILITY, parent);
			return;
		}

		GameObject host = (GameObject) parent;
		if (!host.checkFlag(GameObjectStatusCodes.GRABBED))
			return; // temp!

		if (!host.needsRefreshing())
			return;

		if (host.checkFlag(GameObjectStatusCodes.MOVE_REJECTED)) {
			if (damageTime > damageDelay) {
				/*
				damage(system.vector.diffLength(host.getGlobalPosition(),
						host.getTargetGlobalPosition()), durability);
				*/
				damageTime = 0f;
			} else
				damageTime += timeDelta;
		}
	}

	@Override
	public void reset() {
		SLog.d(did, "Reset.");
		durability = 1f;
		damageTime = 0f;
	}

	protected abstract void damage(float distance, float durability);

	public static DurabilityComponent getNeutral() {
		return new DurabilityComponent() {

			@Override
			protected void damage(float distance, float durability) {
				// no harm
			}

		};
	}

}
