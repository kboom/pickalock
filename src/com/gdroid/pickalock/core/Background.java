package com.gdroid.pickalock.core;

import com.gdroid.pickalock.drawing.ShapeFactory;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

/**
 * If background won't get anything extraordinary this class will be deleted and
 * StaticObject will get this.
 * 
 * @author kboom
 * 
 */
public class Background extends GameObject {

	private static final int did = SLog.register(ShapeFactory.class);
	static {
		SLog.setTag(did, "Background.");
		SLog.setLevel(did, Level.ERROR);
	}

	private PositioningComponent positioning;
	private RenderComponent renderer;

	public Background() {

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
			Builder<MyBuilder, Background> {

		private static final int did = SLog
				.register(Background.MyBuilder.class);
		static {
			SLog.setTag(did, "Background builder.");
		}

		protected PositioningComponent bPositioning;
		protected RenderComponent bRenderer;

		@Override
		protected final Background create() {
			return new Background();
		}

		// common methods

		@Override
		protected void attachPositioning(Background t) {
			t.positioning = bPositioning;
		}

		@Override
		protected void attachRender(Background t) {
			t.renderer = bRenderer;
		}

		@Override
		protected void onPostAttachment(Background t) {
			if (t.positioning == null)
				t.positioning = PositioningComponent.getNeutral();
		}


	}
}
