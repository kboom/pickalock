package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.projection.OffsetProjOperation;
import com.gdroid.pickalock.core.validation.RunningCondition;
import com.gdroid.pickalock.drawing.GameRenderer;

public class DefaultBackgroundBuilder extends Background.MyBuilder {
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
	public DefaultBackgroundBuilder(GameRenderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void attachPositioning(Background t) {
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
	public void attachRender(Background t) {
		RenderComponent c = new RenderComponent();
		c.add(new OffsetProjOperation(t.getStickyPoint()));		
		bRenderer = c;
		super.attachRender(t);
	}

}
