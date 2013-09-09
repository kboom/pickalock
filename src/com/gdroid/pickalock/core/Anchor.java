package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.LockMechanismSystem.Type;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

public class Anchor extends GameObject {

	private static final int did = SLog.register(Anchor.class);
	static {
		SLog.setTag(did, "Anchor.");
		SLog.setLevel(did, Level.DEBUG);
	}

	private Type type;
	private PositioningComponent positioning;
	private MotionComponent motion;
	private RenderComponent renderer;
	private LockMechanismComponent lockMechanism;

	@Override
	protected void update(float timeDelta, BaseObject parent) {
		super.update(timeDelta, parent);

		motion.update(timeDelta, this);
		lockMechanism.update(timeDelta, this);
		// Update position. This is was the user sees.
		positioning.update(timeDelta, this);
		renderer.update(timeDelta, this); // finally schedule for rendering
	}

	@Override
	public void reset() {
		super.reset();
		
		positioning.reset();
		renderer.reset();
		lockMechanism.reset();
		SLog.e(did, "ANCHOR initial: " + super.getAnchor().print());
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
	public abstract static class MyBuilder extends
			Builder<MyBuilder, Anchor> {

		private static final int did = SLog
				.register(Anchor.MyBuilder.class);
		static {
			SLog.setTag(did, "StaticObject builder.");
		}

		protected PositioningComponent bPositioning;
		protected MotionComponent bMotion;
		protected RenderComponent bRenderer;
		protected LockMechanismComponent bLockMechanism;

		private Type type;

		@Override
		protected final Anchor create() {
			return new Anchor();
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

		protected void attachLockMechanism(Anchor t) {
			t.lockMechanism = bLockMechanism;
		}

		protected void attachMotion(Anchor t) {
			t.motion = bMotion;
		}

		// common methods		
		
		@Override
		protected void attachPositioning(Anchor t) {
			t.positioning = bPositioning;
		}

		@Override
		protected void attachRender(Anchor t) {
			t.renderer = bRenderer;
		}
		
		@Override
		protected void onPreAttachment(Anchor t) {
			t.type = type;
			attachMotion(t);
			attachLockMechanism(t);
		}

		@Override
		protected void onPostAttachment(Anchor t) {			
			if (t.positioning == null) {
				SLog.d(did, "Using default positioning.");
				t.positioning = PositioningComponent.getNeutral();
			}
			if (t.motion == null) {
				SLog.d(did, "Using default motion.");
				t.motion = MotionComponent.getNeutral();
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
