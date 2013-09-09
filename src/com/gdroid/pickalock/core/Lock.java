package com.gdroid.pickalock.core;

import com.gdroid.pickalock.utils.SLog;

public class Lock extends GameObject {

	private static final int did = SLog.register(Lock.class);
	static {
		SLog.setTag(did, "Lock.");
	}

	private PositioningComponent positioning;
	private RenderComponent renderer;

	private Lock() {
	}

	@Override
	protected void update(float timeDelta, BaseObject parent) {
		super.update(timeDelta, parent);
		positioning.update(timeDelta, this);
		renderer.update(timeDelta, this);
	}

	@Override
	public void reset() {
		super.reset();
		positioning.reset();
		renderer.reset();
		positioning.applyOperations();
	}

	public abstract static class MyBuilder extends Builder<MyBuilder, Lock> {
		protected PositioningComponent bPositioning;
		protected RenderComponent bRenderer;

		@Override
		protected final Lock create() {
			return new Lock();
		}

		/*
		 * Allow subclass to modify internal values through calling super
		 * method. It's kind of tricky, that a subclass will modify this builder
		 * fields and then use this builder functions to acctaully put it into
		 * created object.
		 */

		// common methods

		@Override
		protected void attachPositioning(Lock t) {
			t.positioning = bPositioning;
		}

		@Override
		protected void attachRender(Lock t) {
			t.renderer = bRenderer;
		}

		@Override
		protected void onPostAttachment(Lock t) {
			if (t.positioning == null)
				t.positioning = PositioningComponent.getNeutral();
		}

	}

}
