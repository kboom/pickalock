package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.LockMechanismSystem.Type;
import com.gdroid.pickalock.core.positioning.RadialPosOperation;
import com.gdroid.pickalock.core.projection.OffsetProjOperation;
import com.gdroid.pickalock.core.projection.RadialProjOperation;
import com.gdroid.pickalock.core.validation.ActiveRunningCondition;
import com.gdroid.pickalock.core.validation.RadialGrabValidator;
import com.gdroid.pickalock.core.validation.RunningCondition;
import com.gdroid.pickalock.drawing.GameRenderer;

public class DefaultAnchorBuilder extends Anchor.MyBuilder {
	// private static Shape rawShape; (put it this way when implementing
	// Dresser!)
	private GameRenderer renderer;

	/**
	 * This is where all internal params are set. They should be multiple
	 * builders with different parameters (one less specific calls builder
	 * containing everything with some default params).
	 * 
	 * @param renderer
	 */
	public DefaultAnchorBuilder(GameRenderer renderer, Type type) {
		this.renderer = renderer;
		super.setType(type);
	}

	@Override
	public void attachMotion(Anchor t) {
		// we do not modify z in local position so it seems ok
		GrabbedMotionComponent c = new GrabbedMotionComponent(t.getMotion(),
				t.getAnchor(), new RadialGrabValidator());
		renderer.getSurface().registerMotionObserver(c);

		bMotion = c;
		super.attachMotion(t);
	}

	@Override
	public void attachPositioning(Anchor t) {
		
		// no positioning!
		
		
		
		PositioningComponent c = new PositioningComponent(
				new RunningCondition() {

					@Override
					public boolean check(GameObject host) {
						return true;
					}

				});
		bPositioning = c;
		super.attachPositioning(t);
	}

	@Override
	public void attachRender(Anchor t) {
		RenderComponent c = new RenderComponent();
		c.add(new OffsetProjOperation(t.getAnchor()));
		bRenderer = c;
		super.attachRender(t);
	}

	@Override
	public void attachLockMechanism(Anchor t) {
		ActiveMechanismComponent c = new ActiveMechanismComponent(t.getType(), t.getStickyPoint());
		super.bLockMechanism = c;
		super.attachLockMechanism(t);
	}

	// durability is default, don't even set it.

}
