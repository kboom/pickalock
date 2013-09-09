package com.gdroid.pickalock.curve;

/**
 * A useful part of the curve should stay in {(0,0),(1,0),(0,1),(1,1)}
 * rectangle.
 * 
 * 
 * @author kboom
 * 
 */
public abstract class MCurve {

	public float getValueX(float t) {
		return t;
	}

	public abstract float getValueY(float t);

	/**
	 * Calculates the length of this curve for specified t range.
	 * 
	 * @param start
	 * @param step
	 * @return
	 */
	public float getLength(float start, float end, float step) {
		float result = 0f;
		for (float t = start; t < end; t += step) {
			result += Math.sqrt(Math.pow(getValueX(t) - (getValueX(t - step)),
					2) + Math.pow(getValueY(t) - getValueY(t - step), 2));
		}
		return result;
	}

	
}
