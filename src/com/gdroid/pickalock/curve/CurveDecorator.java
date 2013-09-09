package com.gdroid.pickalock.curve;

import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.utils.SLog;

public abstract class CurveDecorator {

	private static final int did = SLog.register(CurveDecorator.class);
	static {
		SLog.setTag(did, "Curve decorator.");
	}
	
	private MCurve curve;
	private float totalLength;
	private float timeScale;

	/* for prototype purposes */
	protected CurveDecorator() {
	}
	
	public CurveDecorator(MCurve curve) {
		this.curve = curve;
		totalLength = computeCurveLength();
		timeScale = computeTimeScale();
		SLog.d(did, String.format("Curve is %f long with %f time scale.",
				totalLength, timeScale));
	}

	public final void fillValue(float time, Vector v) {
		float ltime = getLocalTime(time);
		float x = curve.getValueX(ltime);
		float y = curve.getValueY(ltime);
		v.setCartesian(x, y, 0);
	}

	public final float getLength() {
		return totalLength;
	}
	

	public float getTimeScale() {
		return timeScale;
	}

	public float getLocalTime(float time) {
		return time * timeScale;
	}


	/**
	 * Calculates the length of this curve between start parameter and end which
	 * is the first point to be outside the useful region.
	 * 
	 * @param start
	 * @param step
	 * @return
	 */
	public float computeCurveLength() {
		float result = 0f;
		float t = 0f;
		float oxval = curve.getValueX(t);
		float oyval = curve.getValueY(t);
		float nxval = 0f;
		float nyval = 0f;

		while (oxval <= 1f && oyval <= 1f && oxval >= 0f && oyval >= 0f) {
			t += 0.0001f;
			nxval = curve.getValueX(t);
			nyval = curve.getValueY(t);

			result += Math.sqrt(Math.pow(nxval - oxval, 2)
					+ Math.pow(nyval - oyval, 2));

			oxval = nxval;
			oyval = nyval;

		}

		return result;
	}

	public float computeTimeScale() {
		float time = 0f;
		float x = 0f;
		float y = 0f;
		while (x >= 0f && y >= 0f && x <= 1f && y <= 1f) {
			x = curve.getValueX(time);
			y = curve.getValueY(time);
			time += 0.00001f;
		}
		return time - 0.00001f;
	}
	
	/**
	 * Fills an error vector that is the difference between given vector and the
	 * one computed using given time and decorated curve.
	 * 
	 * @param time a global time
	 * @param m a vector used for comparison
	 * @param e a vector that will be filled
	 */
	public void fillError(final float time, final Vector m, Vector e) {
		fillValue(time, e);
		float xdiff = m.getX() - e.getX();
		float ydiff = m.getY() - e.getY();
		e.setCartesian(xdiff, ydiff, 0);
	}
}
