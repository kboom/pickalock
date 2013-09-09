package com.gdroid.pickalock.core.positioning;

import com.gdroid.pickalock.core.LockMechanismSystem;
import com.gdroid.pickalock.utils.SLog;

public class ProgressRotationPosOperation extends
		PositioningOperation implements LockMechanismSystem.ProgressObserver {

	private final Direction direction;
	private final float period;
	private float oldProgress;
	private float progressDelta;
	private boolean wasUpdated;

	public ProgressRotationPosOperation(Vector pos, Direction dir, float period) {
		this(pos, pos, Direction.RIGHT, period);
	}
	
	public ProgressRotationPosOperation(Vector target, Vector source, Direction dir, float period) {
		super(target, source);
		oldProgress = 0f;
		progressDelta = 0f;
		direction = dir;
		wasUpdated = false;
		this.period = period;
	}

	@Override
	public void perform() {
		if(!wasUpdated) return;
		wasUpdated = false;
		
		float angle = 0f;
		switch (direction) {
		case LEFT:
			angle = (float) (2*Math.PI * progressDelta * period);
			super.target.rotate(0, angle);
			break;
		case RIGHT:
			angle = -(float) (2*Math.PI * progressDelta * period);
			super.target.rotate(0, angle);
			break;
		case TOP:
			// something
			super.target.rotate(angle, 0);
			break;
		case BOTTOM:
			// something
			super.target.rotate(angle, 0);
			break;
		}
		
	}

	@Override
	public void onProgress(float progress) {
		this.progressDelta = progress - oldProgress;
		oldProgress = progress;
		wasUpdated = true;
	}

	@Override
	public void onSuccess() {
		
	}

	@Override
	public void onFailure() {
		// TODO Auto-generated method stub

	}

	public enum Direction {
		LEFT, RIGHT, TOP, BOTTOM
	}

}
