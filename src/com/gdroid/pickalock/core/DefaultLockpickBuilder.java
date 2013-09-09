package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.LockMechanismSystem.Type;
import com.gdroid.pickalock.core.positioning.RadialPosOperation;
import com.gdroid.pickalock.core.projection.RadialProjOperation;
import com.gdroid.pickalock.core.validation.ActiveRunningCondition;
import com.gdroid.pickalock.core.validation.RadialGrabValidator;
import com.gdroid.pickalock.drawing.GameRenderer;

import android.view.MotionEvent;

/**
 * Builder appropriate for lockpicks. Contains some useful methods simplifying
 * lockpick building.
 * 
 * @author kboom
 */
public class DefaultLockpickBuilder extends Lockpick.MyBuilder {

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
	public DefaultLockpickBuilder(GameRenderer renderer, Type type) {
		this.renderer = renderer;
		// super.attachBody(Shape.createCube());
		super.setType(type);
	}

	@Override
	public void attachPositioning(Lockpick t) {
		PositioningComponent c = new PositioningComponent(
				new ActiveRunningCondition());
		c.addOperation(new RadialPosOperation(t.getAnchor(), t.getAnchor(), t.getStickyPoint()));

		bPositioning = c;
		super.attachPositioning(t);
	}

	@Override
	public void attachRender(Lockpick t) {
		// maybe radial positioning here? Surely if radial positioning
		// will prove not to be valid to be performed on real positions

		final RadialProjOperation op = new RadialProjOperation(t.getAnchor(),
				t.getSize().getX() / 2 + 0.01f);

		RenderComponent c = new RenderComponent();
		c.add(op);
		bRenderer = c;
		super.attachRender(t);
	}

	@Override
	public void attachLockMechanism(Lockpick t) {
		PassiveMechanismComponent c = new PassiveMechanismComponent(t.getType(), t.getAnchor());
		super.getSystem().lockLogic.registerObserver(c);		
		super.bLockMechanism = c;
		super.attachLockMechanism(t);
	}

	// durability is default, don't even set it.

}
