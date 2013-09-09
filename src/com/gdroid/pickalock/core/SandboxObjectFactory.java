package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.LockMechanismSystem.Type;
import com.gdroid.pickalock.core.AmbientObject.MyBuilder;
import com.gdroid.pickalock.drawing.GameRenderer;
import com.gdroid.pickalock.dressing.Outfit;

/**
 * This object factory sets up game objects making them totally invurnelable and
 * easier to grab. Not all of provided parameters will be used.
 * 
 * @return
 */
public class SandboxObjectFactory extends GameObjectFactory {

	public SandboxObjectFactory(GameRenderer renderer) {
		super(renderer);
	}

	// jeśli to się różni tylko że jest blocking, po co dwie funkcje...
	// po prostu getLockpickBuilder()!
	@Override
	public DefaultLockpickBuilder getLockpickBuilder(Type t, Outfit outfit) {
		DefaultLockpickBuilder builder = new DefaultLockpickBuilder(
				getRenderer(), t);		
		builder.attachBody(outfit);
		return builder;
	}

	@Override
	public DefaultLockBuilder getLockBuilder(Outfit outfit) {
		DefaultLockBuilder builder = new DefaultLockBuilder(getRenderer());
		builder.attachBody(outfit);
		return builder;		
	}

	@Override
	public DefaultUnlockCurveBuilder getUnlockCurveBuilder(Outfit outfit) {
		DefaultUnlockCurveBuilder builder = new DefaultUnlockCurveBuilder(getRenderer());
		builder.attachBody(outfit);
		return builder;
	}

	@Override
	public DefaultBackgroundBuilder getBackgroundBuilder(Outfit outfit) {
		DefaultBackgroundBuilder builder = new DefaultBackgroundBuilder(getRenderer());
		builder.attachBody(outfit);
		return builder;
	}

	@Override
	public MyBuilder getAmbientObjectBuilder(Outfit outfit) {
		DefaultAmbientObjectBuilder builder = new DefaultAmbientObjectBuilder(getRenderer());
		builder.attachBody(outfit);
		return builder;
	}

	@Override
	public Anchor.MyBuilder getAnchorBuilder(Type t, 
			Outfit outfit) {
		DefaultAnchorBuilder builder = new DefaultAnchorBuilder(getRenderer(), t);
		builder.attachBody(outfit);
		return builder;
	}

}
