package com.gdroid.pickalock.drawing;

import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

public class SmartColor {

	private static final int did = SLog.register(SmartColor.class);
	static {
		SLog.setTag(did, "Smart Color.");
		SLog.setLevel(did, Level.ERROR);
	}

	float r;
	float g;
	float b;
	float a;

	public SmartColor(float r, float g, float b, float a) {
		if ((r < 0 || r > 1f) || (g < 0 || g > 1f) || (b < 0 || b > 1f)
				|| (a < 0 || a > 1f))
			throw new IllegalArgumentException("Parameters must be normalized!");

		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public static SmartColor[] gradient(SmartColor startColor, SmartColor endColor, int size) {
		SLog.v(did, String.format("Filling gradient A=%s, B=%s", startColor.print(), endColor.print()));
		
		SmartColor[] result = new SmartColor[size];

		final float rstep = (endColor.r - startColor.r) / size;
		final float gstep = (endColor.g - startColor.g) / size;
		final float bstep = (endColor.b - startColor.b) / size;
		final float astep = (endColor.a - startColor.a) / size;

		
		// true
		final float rs = startColor.r;
		final float gs = startColor.g;
		final float bs = startColor.b;
		final float as = startColor.a;

		SLog.v(did,
				String.format("Steps: %f,%f,%f,%f, Initial values: %f,%f,%f,%f", rstep, gstep, bstep, astep, rs, gs, bs, as));

		for (int i = 0; i < size; i++) {
			float nr = rs + i * rstep;
			float ng = gs + i * gstep;
			float nb = bs + i * bstep;
			float na = as + i * astep;

			result[i] = new SmartColor(nr, ng, nb, na);
		}

		return result;

	}

	public float[] asFloatArray() {
		return new float[] { r, g, b, a };
	}

	public String print() {
		return String.format("(%d,%d,%d,%d)", Math.round(r * 255),
				Math.round(g * 255), Math.round(b * 255), Math.round(a * 255));
	}

}
