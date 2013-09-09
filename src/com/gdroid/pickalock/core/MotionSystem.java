package com.gdroid.pickalock.core;


import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.core.positioning.Vector3;

import android.view.MotionEvent;

public class MotionSystem extends BaseObject {

	private final Vector lastTouchPoint;
	
	public MotionSystem() {
		lastTouchPoint = BaseObject.system.vector.create();
	}
	
	@Override
	protected void update(float timeDelta, BaseObject parent) {
		super.update(timeDelta, parent);
		
	}
	
	public Vector lastPosition() {
		return lastTouchPoint;
	}

	@Override
	public void reset() {
		lastTouchPoint.reset();		
	}

}
