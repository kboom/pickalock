package com.gdroid.pickalock.curve;

import java.util.ArrayList;
import java.util.HashMap;

import com.gdroid.pickalock.core.LockMechanismSystem;
import com.gdroid.pickalock.utils.SLog;

/**
 * A factory that creates a curve either from given parameter list or a
 * well-formatted string. A singleton.
 * 
 * @author kboom
 * 
 */
public final class CurveFactory {

	private static final int did = SLog.register(CurveFactory.class);
	static {
		SLog.setTag(did, "Curve factory.");
	}

	
	private Container cc;
	private static CurveFactory instance = null;
	
	private CurveFactory() { }
	
	public static CurveFactory getInstance() {
		if(instance == null)
			instance = new CurveFactory();
		
		return instance;
	}
	
	public void setSource(Container cont) {
		cc = cont;
	}	

	public MCurve createPoly(final float... coef) {
		return new MCurve() {

			@Override
			public float getValueY(float t) {
				float result = 0f;
				for (int i = 0; i < coef.length; i++) {
					result += coef[i] * Math.pow(t, coef.length - 1 - i);
				}
				return result;
			}

		};
	}

	public MCurve createPoly(final String coef) {
		return new MCurve() {

			private ArrayList<Float> y = parseToCoef(coef);

			@Override
			public float getValueY(float t) {
				float result = 0f;
				float size = y.size();
				for (int i = 0; i < size; i++) {
					result += y.get(i) * Math.pow(t, size - 1 - i);
				}
				return result;
			}

		};
	}

	public MCurve createParaPoly(final float[] x, final float[] y) {
		return new MCurve() {

			@Override
			public float getValueX(float t) {
				float result = 0f;
				for (int i = 0; i < x.length; i++) {
					result += x[i] * Math.pow(t, x.length - 1 - i);
				}
				return result;
			}

			@Override
			public float getValueY(float t) {
				float result = 0f;
				for (int i = 0; i < y.length; i++) {
					result += y[i] * Math.pow(t, y.length - 1 - i);
				}
				return result;
			}

		};
	}

	/**
	 * Creates an curve matching given equations. If no equation is found, error
	 * is being thrown. {@link #release()} should be called after all curves are
	 * loaded.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public MCurve create(String x, String y) {
		if (!cc.isOpen())
			cc.open();
		return cc.get(x, y);
	}

	/**
	 * Closes the source of functions to free memory.
	 */
	public void release() {
		if (cc.isOpen()) {
			cc.release();
		}
	}

	/**
	 * Parses string threating it as a coefficients splitted by spaces.
	 * 
	 * @param coef
	 * @return
	 */
	private ArrayList<Float> parseToCoef(String coef) {
		ArrayList<Float> result = new ArrayList<Float>();
		return result;
	}

	/**
	 * Contains definitions of non-standard curves. It could be made composite,
	 * but traversing through a tree would be expensive in such game. Makeing
	 * enum of it would be a waste of memory too. This seems a fair choice. One
	 * factory only is instantiated to get one of the values, then it is left to
	 * gc on demand, just before gameplay starts.
	 * 
	 * There is a documentation of supported type of functions in each container
	 * javadoc.
	 * 
	 * @author kboom
	 * 
	 */
	public abstract static class Container {
		private final HashMap<Desc, MCurve> functions = new HashMap<Desc, MCurve>();
		private boolean isOpened = false;

		protected final Container put(String x, String y, MCurve curve) {
			functions.put(new Desc(x, y), curve);
			return this;
		}

		public MCurve get(String x, String y) {
			for (Desc d : functions.keySet()) {
				if (d.x.equalsIgnoreCase(x) && d.y.equalsIgnoreCase(y))
					return functions.get(d);
			}
			throw new IllegalArgumentException("Could not find that curve.");
		}

		public void open() {
			isOpened = true;
		}

		public final boolean isOpen() {
			return this.isOpened;
		}

		public void release() {
			functions.clear();
			this.isOpened = false;
		}

		private static class Desc {
			String x = "t";
			String y = "t";

			Desc(String x, String y) {
				this.x = x;
				this.y = y;
			}
		}
	}
}
