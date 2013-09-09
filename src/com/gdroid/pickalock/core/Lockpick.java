package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.LockMechanismSystem.Type;
import com.gdroid.pickalock.core.positioning.AnchorVector;
import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

/**
 * Already assembled object.
 * 
 * @author kboom
 * 
 */
public class Lockpick extends GameObject {

	private static final int did = SLog.register(Lockpick.class);
	static {
		SLog.setTag(did, "Lockpick.");
		SLog.setLevel(did, Level.VERBOSE);
	}

	
	private Type type;
	private DurabilityComponent durability;
	// maybe some state pattern?
	private PositioningComponent positioning;
	private RenderComponent renderer;
	private LockMechanismComponent lockMechanism;

	private Lockpick() {
	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		super.update(timeDelta, parent);

		lockMechanism.update(timeDelta, this);
		durability.update(timeDelta, this);
		// Update position. This is was the user sees.
		positioning.update(timeDelta, this);
		renderer.update(timeDelta, this); // finally schedule for rendering
	}

	@Override
	public void reset() {
		super.reset();

		positioning.reset();
		durability.reset();
		renderer.reset();
		lockMechanism.reset();
		SLog.v(did, "LOCKPICK initial: " + super.getAnchor().printSimple());
		positioning.applyOperations();
		

	}

	/**
	 * As this class is not only static, but also abstract, its methods and
	 * fields still cannot be accessed without external builder that extends
	 * this class. Concrete class should override create method. In order not to
	 * make it dependent from concrete components, there is no instantation
	 * here. External class can extend this builder and do it.
	 * 
	 * @author kboom
	 * 
	 */
	public abstract static class MyBuilder extends Builder<MyBuilder, Lockpick> {

		private static final int did = SLog.register(Lockpick.MyBuilder.class);
		static {
			SLog.setTag(did, "Lockpick builder.");
		}

		protected DurabilityComponent bDurability;
		protected PositioningComponent bPositioning;
		protected RenderComponent bRenderer;
		protected LockMechanismComponent bLockMechanism;

		private Type type;

		@Override
		protected final Lockpick create() {
			return new Lockpick();
		}

		public final void setType(Type t) {
			SLog.d(did, "Type set: " + t.ordinal());
			this.type = t;
		}

		/*
		 * Allow subclass to modify internal values through calling super
		 * method. It's kind of tricky, that a subclass will modify this builder
		 * fields and then use this builder functions to acctaully put it into
		 * created object.
		 */

		protected void attachDurability(Lockpick t) {
			t.durability = bDurability;
		}

		protected void attachLockMechanism(Lockpick t) {
			t.lockMechanism = bLockMechanism;
		}

		// common methods		
		
		@Override
		protected void attachPositioning(Lockpick t) {
			t.positioning = bPositioning;
		}

		@Override
		protected void attachRender(Lockpick t) {
			t.renderer = bRenderer;
		}
		
		@Override
		protected void onPreAttachment(Lockpick t) {
			t.type = type;
			attachDurability(t);
			attachLockMechanism(t);
		}

		@Override
		protected void onPostAttachment(Lockpick t) {			
			if (t.durability == null) {
				SLog.d(did, "Using default durability.");
				t.durability = DurabilityComponent.getNeutral();
			}
			if (t.positioning == null) {
				SLog.d(did, "Using default positioning.");
				t.positioning = PositioningComponent.getNeutral();
			}
			if (t.lockMechanism == null) {
				SLog.d(did, "Using default lock mechanism.");
				t.lockMechanism = LockMechanismComponent.getNetural(type);
			}
			
			
		}

	}

	public Type getType() {
		return type;
	}

	

}
