package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.Lock.MyBuilder;
import com.gdroid.pickalock.core.LockMechanismSystem.Type;
import com.gdroid.pickalock.drawing.GameRenderer;
import com.gdroid.pickalock.dressing.Outfit;

/**
 * A factory that creates fully functional game objects capable of being used in
 * a game of specified type. This object will become default, raw apperance that
 * can be changed in skin factory. An object should be passed to skin manager to
 * get its skin afterwards. Once manufactured, an object is not capable of
 * changing its internal state (logic). This factory is an enum factory because
 * it is to be changed, not subclassed. It can use strings to identify objects.
 * 
 * @author kboom
 */
public abstract class GameObjectFactory {

	private GameRenderer renderer;

	public GameObjectFactory(GameRenderer renderer) {
		this.renderer = renderer;
	}

	public final GameRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Returns a pre-set builder of some type which needs to have some fields
	 * set before creating the lockpick.
	 * 
	 * @return
	 */
	public abstract Lockpick.MyBuilder getLockpickBuilder(Type t, Outfit outfit);

	public abstract Anchor.MyBuilder getAnchorBuilder(Type t, Outfit outfit);

	public abstract UnlockCurve.MyBuilder getUnlockCurveBuilder(Outfit outfit);

	public abstract Lock.MyBuilder getLockBuilder(Outfit outfit);

	public abstract Background.MyBuilder getBackgroundBuilder(Outfit outfit);

	public abstract AmbientObject.MyBuilder getAmbientObjectBuilder(
			Outfit outfit);

	/**
	 * Creates a game object. An object should be built using this method
	 * because it is then well maintainted.
	 * 
	 * @param builder
	 * @return
	 */
	protected final GameObject construct(GameObject.Builder<?, ?> builder) {
		return builder.construct();
	}

}
