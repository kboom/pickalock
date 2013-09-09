package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.AmbientObject.MyBuilder;
import com.gdroid.pickalock.core.GameObject.Builder;
import com.gdroid.pickalock.drawing.ShapeFactory;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

/**
 * An object that does not interact. Mainly used to decorate environment.
 * 
 * @author kboom
 * 
 */
public class AmbientObject extends GameObject {
	private static final int did = SLog.register(ShapeFactory.class);
	static {
		SLog.setTag(did, "StaticObject.");
		SLog.setLevel(did, Level.ERROR);
	}

	private PositioningComponent positioning;
	private RenderComponent renderer;

	public AmbientObject() {

	}

	@Override
	protected void update(float timeDelta, BaseObject parent) {
		super.update(timeDelta, parent);
		positioning.update(timeDelta, this);
		renderer.update(timeDelta, this);
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		super.reset();
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
			Builder<MyBuilder, AmbientObject> {

		private static final int did = SLog
				.register(AmbientObject.MyBuilder.class);
		static {
			SLog.setTag(did, "StaticObject builder.");
		}

		protected PositioningComponent bPositioning;
		protected RenderComponent bRenderer;

		@Override
		protected final AmbientObject create() {
			return new AmbientObject();
		}

		// common methods

		@Override
		protected void attachPositioning(AmbientObject t) {
			t.positioning = bPositioning;
		}

		@Override
		protected void attachRender(AmbientObject t) {
			t.renderer = bRenderer;
		}

		@Override
		protected void onPostAttachment(AmbientObject t) {
			// fill empty parameters if they exist
			if (t.positioning == null)
				t.positioning = PositioningComponent.getNeutral();

			// initial things to do
			t.positioning.applyOperations();
		}


	}
}
