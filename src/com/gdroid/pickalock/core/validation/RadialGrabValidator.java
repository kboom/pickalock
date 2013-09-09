package com.gdroid.pickalock.core.validation;

import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

public class RadialGrabValidator implements GrabValidator {

	private static final int did = SLog.register(RadialGrabValidator.class);
	static {
		SLog.setTag(did, "Radial grab validator.");
		SLog.setLevel(did, Level.VERBOSE);
	}
	
	private float radius;

	public RadialGrabValidator() {
		this(0.2f);
	}

	public RadialGrabValidator(float radius) {
		this.radius = radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	@Override
	public boolean isGrabbed(Vector handle, Vector grip) {
		float trange = (float) (Math.pow(handle.getX() - grip.getX(), 2) + Math
				.pow(handle.getY() - grip.getY(), 2));
		float mrange = (float) Math.pow(radius, 2);
		float error = 1-(mrange - trange) / (mrange);
		boolean result = trange <= mrange ? true : false;
		SLog.v(did,
				"Checked in " + radius + " range: handle = " + handle.print()
						+ ", grip = " + grip.print() + "; match=" + result
						+ " with normalized error=" + error);
		return result;
	}

}
