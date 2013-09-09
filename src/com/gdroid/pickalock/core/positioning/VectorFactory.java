package com.gdroid.pickalock.core.positioning;

import com.gdroid.pickalock.pooling.TObjectPool;
import com.gdroid.pickalock.utils.SLog;

/**
 * A smart vector factory. It should be instantiated and set up in each
 * environment it works in. When one of normalization methods gets called, the
 * resulting vector will be fully-adjusted to that environment (eg. have offset,
 * be normalized, good axes).

 * 
 * @author kboom
 * 
 */
public final class VectorFactory {

	private static final int did = SLog.register(VectorFactory.class);
	static {
		SLog.setTag(did, "Vector factory.");
	}
	private final Vector3 normalizer = new Vector3();
	private final Vector3 offset = new Vector3();
	private AxesOrientation intAxesOrientation;
	private AxesOrientation extAxesOrientation;

	public VectorFactory() {
		normalizer.setCartesian(1,1,1);
		// standard for android
		extAxesOrientation = AxesOrientation.LEFT_TOP_FRONT;
		// standard for open GL
		intAxesOrientation = AxesOrientation.LEFT_BOTTOM_FRONT;
	}

	/**
	 * Allocates a new vector with specified coordinates. Should be used only to
	 * obtain one copy of a vector and reuse it all the time. Use with caution.
	 * Resulting vector is normalized according to current normalization values set.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Vector3 create(float x, float y, float z) {
		return create(x, y, z, false);
	}
	
	public Vector3 create(float x, float y, float z, boolean normalize) {
		Vector3 v = new Vector3();
		v.setCartesian(x, y, z);
		if(normalize) this.normalize(v);
		return v;
	}

	/**
	 * Allocates a new vector with specified coordinates. Should be used only to
	 * obtain one copy of a vector and reuse it all the time. Use with caution.
	 * Resulting vector is normalized according to current normalization values set.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector create(float x, float y) {
		return this.create(x, y, 0f);
	}
	
	/**
	 * Allocates a new vector with default coordinates. Should be used only to
	 * obtain one copy of a vector and reuse it all the time. Use with caution.
	 * The vector is normalized that is it lies in the center of coordinate
	 * system.
	 * 
	 * @return
	 */
	public Vector create() {
		return create(0, 0, 0, true);
	}
	
	public JoinedVector createJoined(Vector v, Vector ...other) {
		return this.createJoined(v, false, other);
	}
	
	public JoinedVector createJoined(Vector v, boolean normalize, Vector ...other) {
		JoinedVector result = new JoinedVector(this.create(), v, other);
		if(normalize) this.normalize(result);
		return result;
	}

	/**
	 * Normalizes given vector. It gets also properly rotated according to
	 * current axes types. Normalization should be applied only once.
	 * 
	 * @param lastMotion
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public void normalize(final Vector lastMotion) {
		this.normalize(lastMotion, lastMotion.getX(), lastMotion.getY(), lastMotion.getZ());
	}
	
	/**
	 * Allocates memory.
	 * @param v
	 * @return
	 */
	public String printNormalized(final Vector v) {
		final float x = v.getX();
		final float y = v.getY();
		final float z = v.getZ();
		
		float xp = (x + offset.getX()) / normalizer.getX();
		float yp = (y + offset.getY()) / normalizer.getY();
		float zp = (z + offset.getZ()) / normalizer.getZ();
		
		return String.format("Normalized coordinates: (%f,%f,%f)", xp, yp, zp);
	}

	/**
	 * Changes the coordinates of a given vector honoring all environment
	 * offsets, rotations. Normalization should be applied only once, after
	 * setting new, raw coordinates. The resulting vector is of course marked as
	 * normalized.
	 * 
	 * @param v
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public void normalize(final Vector v, float x, float y, float z) {
		float nx = (x + offset.getX()) / normalizer.getX();
		float ny = (y + offset.getY()) / normalizer.getY();
		float nz = (z + offset.getZ()) / normalizer.getZ();
		v.setCartesian(nx,ny,nz);
		SLog.d(did, String.format("Normalizing: (%f,%f,%f) ; effect = %s", x,
				y, z, v.print()));		
	}

	/**
	 * Sets offset vector. Relative to {@link AxesOrientation.LEFT_TOP_FRONT }
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public void setOffset(float x, float y, float z) {
		offset.setCartesian(x, y, z);
		Vector d = create();
		d.set(offset);
		rotateAxes(d);
		SLog.i(did, String.format(
				"Global offset set: (%f,%f,%f) / (%f,%f,%f)", x, y, z,
				d.getX(), d.getY(), d.getZ()));
	}

	/**
	 * Sets an offset that will be added in all normalization methods.
	 * 
	 * @param x
	 * @param y
	 */
	public void setOffset(float x, float y) {
		setOffset(x, y, 0);
	}

	public Vector3 getOffset() {
		return offset;
	}

	/**
	 * Sets normalization vector. Given parameters has no other meaning than a
	 * length. Rotation is applied due to what was set in
	 * {@link #setAxesDirections(AxesDirections) }.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Vector3 setScale(float x, float y, float z) {

		if (x <= 0 || y <= 0 || z <= 0)
			throw new IllegalArgumentException(
					"Wrong normalization values. Should all be positive.");
		normalizer.setCartesian(x, y, z);
		// now everything that gets normalized will have fine axes too
		this.rotateAxes(normalizer);

		SLog.i(did, String.format("Global scale set: (%f,%f,%f)/(%f,%f,%f)",
				x, y, z, normalizer.getX(), normalizer.getY(),
				normalizer.getZ()));

		return normalizer;
	}

	public Vector3 setScale(int x, int y) {
		return setScale(x, y, 1);
	}

	/**
	 * Set the natural orientation of axes in working environment (this will be
	 * used to recalculate).
	 */
	public void setExternalAxesOrientation(AxesOrientation type) {
		extAxesOrientation = type;
		setScale(normalizer.getX(), normalizer.getY(), normalizer.getZ());
	}

	/**
	 * Sets axes orientation of this factory. Make sure
	 * {@link #setExternalAxesOrientation() } is set.
	 * 
	 * @param type
	 */
	public void setAxesOrientation(AxesOrientation type) {
		intAxesOrientation = type;
		setScale(normalizer.getX(), normalizer.getY(), normalizer.getZ());
	}

	/**
	 * Rotates the axes corresponding to what has been set in
	 * {@link #setAxesOrientation(AxesDirections) and}
	 * {@link #setExternalAxesOrientation(AxesDirections) }. Calling more than
	 * once on same raw data will compromise the result.
	 * 
	 * @param v
	 *            vector to perform axes rotation on
	 */
	public Vector rotateAxes(Vector v) {
		float x = v.getX();
		float y = v.getY();
		float z = v.getZ();

		x *= AxesOrientation.getXMultiplier(intAxesOrientation,
				extAxesOrientation);
		y *= AxesOrientation.getYMultiplier(intAxesOrientation,
				extAxesOrientation);
		z *= AxesOrientation.getZMultiplier(intAxesOrientation,
				extAxesOrientation);

		v.setCartesian(x, y, z);
		return v;
	}

	/**
	 * Directions of the axes. Every vector created with external coordinates
	 * should be normalized. Normalization takes care of chosing right direction
	 * as well.
	 * 
	 * @author kboom
	 * 
	 */
	public enum AxesOrientation {

		// Identifiaction is based on position relative to BOTTOM_LEFT_FRONT
		// 1 on match, -1 otherwise. Only local meaning. Moving ccw starting
		// from netural position.

		/**
		 * x direction face right, y direction face up, z direction faces you
		 */
		LEFT_BOTTOM_FRONT(1, 1, 1),

		/**
		 * x direction face right, y direction face down, z direction faces you
		 */
		LEFT_TOP_FRONT(1, -1, 1),

		/**
		 * x direction face left, y direction face down, z direction faces you
		 */
		RIGHT_TOP_FRONT(-1, -1, 1),

		/**
		 * x direction face left, y direction face up, z direction faces you
		 */
		RIGHT_BOTTOM_FRONT(-1, 1, 1),

		// flip

		/**
		 * x direction face right, y direction face up, z direction face behind
		 * the screen
		 */
		LEFT_BOTTOM_BACK(1, 1, -1),

		/**
		 * x direction face right, y direction face down, z direction face
		 * behind the screen
		 */
		LEFT_TOP_BACK(1, -1, -1),

		/**
		 * x direction face left, y direction face down, z direction face behind
		 * the screen
		 */
		RIGHT_TOP_BACK(-1, -1, -1),

		/**
		 * x direction face left, y direction face up, z direction face behind
		 * the screen
		 */
		RIGHT_BOTTOM_BACK(-1, 1, -1);

		private int x, y, z;

		AxesOrientation(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		static int getXMultiplier(AxesOrientation source, AxesOrientation target) {
			if (source.x == target.x)
				return 1;
			else
				return -1;
		}

		static int getYMultiplier(AxesOrientation source, AxesOrientation target) {
			if (source.y == target.y)
				return 1;
			else
				return -1;
		}

		static int getZMultiplier(AxesOrientation source, AxesOrientation target) {
			if (source.z == target.z)
				return 1;
			else
				return -1;
		}

	}

	/**
	 * Simple vector pool for general purpose operations. If operation is being
	 * performed every frame it should not be pooled but the vector should be
	 * private field of using class instead. Vectors in this pool cannot be
	 * garbage collected before the factory itself is.
	 * 
	 * @author kboom
	 * 
	 */
	public class Pool extends TObjectPool<Vector3> {

		public Pool(int size) {
			super(size);
			SLog.d(did, "Vector pool created.");
		}

		@Override
		protected void fill() {
			for (int x = 0; x < getSize(); x++) {
				// getAvailable().add(VectorFactory.this.create());
				getAvailable().add(new Vector3() {
					@Override
					public void finalize() {
						// it cannot be gc so throw an error if it does!
						throw new IllegalStateException("Vector from the pool "
								+ Pool.class.getSimpleName() + " was gc!");
					}
				});
			}
		}

		@Override
		protected void clean(Vector3 t) {
			t.reset();
		}

	}

	/**
	 * Changes the coordinates of a given vector honoring all environment
	 * offsets, rotations. Normalization should be applied only once, after
	 * setting new, raw coordinates. A user should check if a vector had been
	 * normalized before.
	 * 
	 * @param v
	 *            a vector to be changed
	 * @param x
	 *            new x coordinate
	 * @param y
	 *            new y coordinate
	 */
	public void normalize(final Vector3 v, float x, float y) {
		normalize(v, x, y, 0f);
	}

	public static double toDeg(float rad) {
		return rad * 180f / Math.PI;
	}

	public Vector createProxied(Vector cglopos) {
		return new ReadonlyVector(cglopos);
	}

	public AnchorVector createAnchor(Vector origin, Vector dir) {
		return new AnchorVector(origin, dir);
	}

}
