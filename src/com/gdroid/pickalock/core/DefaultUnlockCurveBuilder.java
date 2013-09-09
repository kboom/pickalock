package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.LockMechanismSystem.Type;
import com.gdroid.pickalock.core.projection.OffsetProjOperation;
import com.gdroid.pickalock.drawing.GameRenderer;
import com.gdroid.pickalock.utils.SLog;

public class DefaultUnlockCurveBuilder extends UnlockCurve.MyBuilder {

	private GameRenderer renderer;

	public DefaultUnlockCurveBuilder(GameRenderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void attachPositioning(UnlockCurve t) {
		PositioningComponent c = PositioningComponent.getNeutral();

		bPositioning = c;
		super.attachPositioning(t);
	}

	@Override
	public void attachRender(UnlockCurve t) {
		RenderComponent c = new RenderComponent();
		c.add(new OffsetProjOperation(t.getStickyPoint()));
		bRenderer = c;
		super.attachRender(t);
	}

	@Override
	public void attachLockMechanism(UnlockCurve t) {
		ActiveMechanismComponent c = new ActiveMechanismComponent(
				t.getType(), t.getStickyPoint());
		super.bLockMechanism = c;
		super.attachLockMechanism(t);
	}

}
