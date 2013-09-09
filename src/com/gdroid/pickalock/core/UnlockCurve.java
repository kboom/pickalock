package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.LockMechanismSystem.Type;
import com.gdroid.pickalock.utils.SLog;

public class UnlockCurve extends GameObject {
	
	private static final int did = SLog.register(UnlockCurve.class);
	static {
		SLog.setTag(did, "Unlock curve.");
	}
	
	private Type type;
	private PositioningComponent positioning;
	private RenderComponent renderer;	
	private LockMechanismComponent lockMechanism;
	
	private UnlockCurve() { }

	@Override
	public void update(float timeDelta, BaseObject parent) {
		super.update(timeDelta, parent); 
		// Check if move valid according to algorithm. If not, raise a flag.
		lockMechanism.update(timeDelta, this);
		// Update position. This is was the user sees. 
		positioning.update(timeDelta, this);
		renderer.update(timeDelta, this); // finally schedule for rendering


	}
	
	public Type getType() {
		return type;
	}

	@Override
	public void reset() {
		super.reset();
		positioning.reset();
		renderer.reset();
		lockMechanism.reset();
		
		positioning.applyOperations();
	}
	
	/**
	 * As this class is not only static, but also abstract, its methods
	 * and fields still cannot be accessed without external builder
	 * that extends this class. Concrete class should override create
	 * method. In order not to make it dependent from concrete components,
	 * there is no instantation here. External class can extend this
	 * builder and do it.
	 * @author kboom
	 *
	 */
	public abstract static class MyBuilder extends Builder<MyBuilder,UnlockCurve> {
		
		private static final int did = SLog.register(UnlockCurve.MyBuilder.class);
		static {
			SLog.setTag(did, "Unlock curve bulider.");
		}
		
		protected PositioningComponent bPositioning;
		protected RenderComponent bRenderer;	
		protected LockMechanismComponent bLockMechanism;
		
		private Type type;
		
		@Override
		protected final UnlockCurve create() {
			return new UnlockCurve();
		}
		
		public final void setType(Type t) {
			this.type = t;
		}
		
		/*
		 * Allow subclass to modify internal values through calling
		 * super method. It's kind of tricky, that a subclass will
		 * modify this builder fields and then use this builder
		 * functions to acctaully put it into created object.
		 */
		
		protected void attachLockMechanism(UnlockCurve t) {
			t.lockMechanism = bLockMechanism;
		}
		
		// common methods		

		@Override
		protected void attachPositioning(UnlockCurve t) {
			t.positioning = bPositioning;
		}

		@Override
		protected void attachRender(UnlockCurve t) {
			t.renderer = bRenderer;
		}
		
		@Override
		protected void onPreAttachment(UnlockCurve t) {
			t.type = type;
			attachLockMechanism(t);
		}

		@Override
		protected void onPostAttachment(UnlockCurve t) {
			if (t.positioning == null)
				t.positioning = PositioningComponent.getNeutral();
			if (t.lockMechanism == null)
				t.lockMechanism = LockMechanismComponent.getNetural(type);
		}
		
		
	}

}
