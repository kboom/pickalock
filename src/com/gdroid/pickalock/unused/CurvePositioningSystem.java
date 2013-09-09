package com.gdroid.pickalock.unused;

import java.util.ArrayList;

import com.gdroid.pickalock.core.BaseObject;
import com.gdroid.pickalock.core.LockMechanismSystem;
import com.gdroid.pickalock.core.LockMechanismSystem.Type;
import com.gdroid.pickalock.core.positioning.Vector3;
import com.gdroid.pickalock.core.positioning.VectorFactory;
import com.gdroid.pickalock.core.positioning.VectorFactory.Pool;
import com.gdroid.pickalock.curve.CurveDecorator;
import com.gdroid.pickalock.curve.MCurve;
import com.gdroid.pickalock.pooling.TObjectPool;
import com.gdroid.pickalock.utils.SLog;

/**
 * 
 * 
 * @author kboom
 * 
 */


public class CurvePositioningSystem extends BaseObject {

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

/*	
	static {
		SLog.register(CurvePositioningSystem.class);
		SLog.setTag(CurvePositioningSystem.class, "Curve positioning system.");
		// SLog.setLevel(CurvePositioningSystem.class, Level.NONE);
	}

	private final ArrayList<PositionedCurve> curveList;

	private final TObjectPool<Vector> vectorPool;
	private int pdens;

	public CurvePositioningSystem() {
		curveList = new ArrayList<PositionedCurve>();
		vectorPool = system.vector.new Pool(10);
		pdens = 100; // 500 points in 1 period
	}

	public int addCurve(PositionedCurve curve) {
		int key = curveList.size();
		curveList.add(curve);
		return key;
	}

	
	 * Returns full path scaled to match shape coordinate system (-1,-1)-(1,1).
	 * This means that it can be directly used as a vertex buffer array source,
	 * and the location of the resulting shape is determined by
	 * {@link #getPlaneCenter(Type t) }
	 * 
	 * @param t
	 * @return
	 
	public float[] getFullPath(int id) {
		float[] result = new float[(int) Math.floor(2 * pdens)];
		PositionedCurve pcurve = curveList.get(id);
		Vector v = vectorPool.allocate();
		for (int i = 0; i < pdens; i++) {
			pcurve.curve.fillValue((float) i / pdens, v);
			result[2 * i] = (v.getX() - 0.5f) * 2;
			result[2 * i + 1] = (v.getY() - 0.5f) * 2;

			SLog.d(this, result[2 * i] + "," + result[2 * i + 1]);
		}

		vectorPool.release(v);
		return result;
	}
	
	
	 * Use to obtain single, normalized part of a specified curve.
	 * 
	 * @param id
	 * @return
	
	public PathCut getPath(int id, float start, float end) {
		float period = end - start;
		int pointnum = (int) (pdens * period);

		float[] verts = new float[2 * pointnum];

		PositionedCurve pcurve = curveList.get(id);
		Vector v = vectorPool.allocate();

		float lowestX = 1, highestX = -1;
		float lowestY = 1, highestY = -1;

		SLog.d(this, "Computing non-normalized path part...");
		for (int i = 0; i < pointnum; i++) {
			float arg = start + ((float) i / pointnum) * period;
			pcurve.curve.fillValue(arg, v);
			float x = (v.getX() - 0.5f) * 2;
			float y = (v.getY() - 0.5f) * 2;

			if (lowestX > x)
				lowestX = x;
			else if (highestX < x)
				highestX = x;

			if (lowestY > y)
				lowestY = y;
			else if (highestY < y)
				highestY = y;

			verts[2 * i] = x;
			verts[2 * i + 1] = y;

			SLog.d(this, String.format("Arg: %f, val: (%f,%f)", arg, x, y));
		}

		SLog.d(this, String.format(
				"Lowest/Highest values: x=(%f,%f), y=(%f,%f)", lowestX,
				highestX, lowestY, highestY));

		vectorPool.release(v);

		// now normalize it
		float centerX = (lowestX + highestX) / 2;
		float centerY = (lowestY + highestY) / 2;

		// dx/Dx = lengthX / (2*1)
		float xscale = (highestX - lowestX) / 2;
		float yscale = (highestY - lowestY) / 2;

		SLog.d(this, String.format(
				"Normalizing it with offset (%f,%f) and scale (%f,%f)...", centerX,
				centerY, xscale, yscale));

		for (int i = 0; i < pointnum; i++) {
			verts[2 * i] = (verts[2 * i] - centerX) / xscale;
			verts[2 * i + 1] = (verts[2 * i + 1] - centerY) / yscale;

			SLog.d(this, verts[2 * i] + "," + verts[2 * i + 1]);
		}

		PathCut result = new PathCut();
		result.centerX = centerX;
		result.centerY = centerY;
		result.scaleX = xscale;
		result.scaleY = yscale;
		result.verts = verts;		
		return result;
	}

	public Vector getPathPartCenter(int id, float start, float end) {
		PositionedCurve curve = curveList.get(id);
		return curve.translateBack((start + end) / 2);
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}
	
	public static class PathCut {
		private float[] verts;
		private float scaleX;
		private float scaleY;
		private float centerX;
		private float centerY;
		
		private PathCut() { }
		
		public float [] getVertices() {
			return verts;
		}
		
		public float getScaleX() {
			return scaleX;
		}
		
		public float getScaleY() {
			return scaleY;
		}
		
		public float getCenterX() {
			return centerX;
		}
		
		public float getCenterY() {
			return centerY;
		}
	}

	public static class PositionedCurve {
		private MCurve scalingCurve;
		private final CurveDecorator curve;
		private final Vector size = system.vector.create(0.5f, 1f);
		private final Vector offset = system.vector.create();

		public PositionedCurve(CurveDecorator prototype, MCurve curve) {
			this(prototype, curve, new MCurve() {

				@Override
				public float getValueX(float t) {
					return 1f; // no scaling
				}
				
				@Override
				public float getValueY(float t) {
					return 1f; // no scaling
				}

			});
		}

		public PositionedCurve(CurveDecorator cprot, MCurve mcurve, MCurve scurve) {			
			this.curve = cprot.clone(mcurve);
			this.scalingCurve = scurve;
		}

		
		 * Translates a vector into local coordinate system.
		 * 
		 * @param p
		
		public void translate(Vector p) {
			SLog.d(this, "translating " + p.printSimple() + " by offset "
					+ offset.printSimple() + " and size " + size.printSimple());

			float nx = (p.getX() - offset.getX()) / size.getX() + 0.5f;
			float ny = (p.getY() - offset.getY()) / size.getY() + 0.5f;

			// enable assertions
			assert (nx > 0 && nx < 1) : "Position wrongly translated: x = "
					+ nx;
			assert (ny > 0 && ny < 1) : "Position wrongly translated: y = "
					+ ny;

			p.set(nx, ny);
		}

		
		 * This is to translate <strong>single points only</strong> from
		 * non-symmetric local math plane (0,0)-(1,1) to global symmetric plane
		 * (-1,-1)-(1,1).
		 * 
		 * @param t
		 * @return
		 
		public Vector translateBack(float arg) {
			Vector ret = system.vector.create();
			Vector val = system.vector.create();
			float x, y;

			curve.fillValue(arg, val);

			x = offset.getX() - size.getX() / 2 + val.getX() * size.getX();
			y = offset.getY() - size.getY() / 2 + val.getY() * size.getY();

			ret.set(x, y);
			ret.normalized();
			return ret;
		}

		/
		 * This is to locate the center of the curve symmetric plane
		 * (-1,-1)-(1,1). This curve is an effect of calling
		 * {@link #getFullPath(Type) } which translates every point from math
		 * coordinate system (only positive) to drawing coordinate system
		 * (-1,-1)-(1,1) - like shapes.
		 * 
		 * 
		 * @param t
		 * @return
		 
		public Vector getCenter() {
			Vector ret = system.vector.create();
			ret.set(offset.getX(), offset.getY());
			ret.normalized();
			return ret;
		}

		public void setOffset(float x, float y) {
			offset.set(x, y);
		}

		public void setSize(float w, float h) {
			assert (w > 0 && h > 0) : "Size cannot be less than zero!";
			size.set(w, h);
		}

		public void setOffset(Vector v) {
			this.setOffset(v.getX(), v.getY());
		}

		public void setSize(Vector v) {
			this.setSize(v.getX(), v.getY());
		}

	}
	
	*/

}
