package com.gdroid.pickalock.curve;

import com.gdroid.pickalock.core.positioning.Vector;

public class ErrorCurveDecorator extends CurveDecorator {

	private float scaleFactor = 5f;
	
	public ErrorCurveDecorator(MCurve curve) {
		super(curve);
	}
	
	public ErrorCurveDecorator(MCurve curve, float factor) {
		super(curve);
		this.scaleFactor = factor;
	}
	
	public void setScaleFactor(float factor) {
		this.scaleFactor = factor;
	}

	/**
	 * Modifies error vector value according to the value of decorated function
	 * in given time.
	 * 
	 * @param time a global time
	 * @param e an error to be modified
	 */
	public void warpError(final float time, Vector e) {
		float ltime = super.getLocalTime(time);		
		float x = e.getX();
		float y = e.getY();
		super.fillValue(ltime, e);	
		/* As a function is normalized, its values never exceed 1. This means
		   that to make the error actually stronger than it is in reality
		   we must divide not multiply! */
		
		x *= scaleFactor / e.getY();
		y *= scaleFactor / e.getY();
		e.setCartesian(x, y, 0);
	}
	
	protected float getScaleFactor() {
		return scaleFactor;
	}

}
