package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.positioning.ProgressRotationPosOperation;
import com.gdroid.pickalock.core.positioning.ProgressRotationPosOperation.Direction;
import com.gdroid.pickalock.core.projection.LocalProjOperation;
import com.gdroid.pickalock.core.projection.RotationProjOperation;
import com.gdroid.pickalock.core.validation.PassiveRunningCondition;
import com.gdroid.pickalock.drawing.GameRenderer;

public class DefaultLockBuilder extends Lock.MyBuilder {

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
	public DefaultLockBuilder(GameRenderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void attachPositioning(Lock t) {
		PositioningComponent c = new PositioningComponent(
				new PassiveRunningCondition());
		ProgressRotationPosOperation ppo = new ProgressRotationPosOperation(
				t.getRotation(), Direction.RIGHT, 0.25f);
		super.getSystem().lockLogic.registerObserver(ppo);
		c.addOperation(ppo);
		bPositioning = c;
		super.attachPositioning(t);
	}

	@Override
	public void attachRender(Lock t) {
		RenderComponent c = new RenderComponent();
		c.add(new RotationProjOperation(t.getRotation()));
		c.add(new LocalProjOperation(t.getStickyPoint()));
		bRenderer = c;
		super.attachRender(t);
	}

}
