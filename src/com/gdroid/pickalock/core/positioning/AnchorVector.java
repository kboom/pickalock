package com.gdroid.pickalock.core.positioning;

import com.gdroid.pickalock.core.Lockpick;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

/**
 * This is a vector that originates at the end of specified vector, has a
 * specified length and a direction of another vector.
 * 
 * This vector has some convenient methods both to read relative object position
 * and to modify the direction vector.
 * 
 * @author kboom
 * 
 */
public class AnchorVector implements Vector {

	private static final int did = SLog.register(AnchorVector.class);
	static {
		SLog.setTag(did, "Anchor Vector.");
		SLog.setLevel(did, Level.VERBOSE);
	}

	private final Vector origin;
	private final Vector direction;
	private float leverlen;

	protected AnchorVector(Vector ori, Vector dir) {
		origin = ori;
		direction = dir;
		leverlen = 0f;
	}

	public void setLeverLength(float len) {
		leverlen = len;
	}

	@Override
	public float getX() {
		return origin.getX() + direction.getX() * leverlen;
	}

	@Override
	public float getY() {
		return origin.getY() + direction.getY() * leverlen;
	}

	@Override
	public float getZ() {
		return origin.getZ() + direction.getZ() * leverlen;
	}

	@Override
	public float getIncAngle() {
		return direction.getIncAngle();
	}

	@Override
	public float getAziAngle() {
		return direction.getAziAngle();
	}

	@Override
	public float getR2() {
		float x = getX();
		float y = getY();
		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

	@Override
	public float getR3() {
		float x = getX();
		float y = getY();
		float z = getZ();
		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)
				+ Math.pow(z, 2));
	}

	public float getLeverLength() {
		return leverlen;
	}

	@Override
	public float getAngle2With(Vector v) {
		return direction.getAngle2With(v);
	}

	@Override
	public float getAngle3With(Vector v) {
		return direction.getAngle3With(v);
	}

	public Vector getDirection() {
		return direction;
	}

	@Override
	public AnchorVector rotate(float dinc, float dazi) {
		float inclination = getIncAngle() + dinc;
		float azimuth = getAziAngle() + dazi;

		float x = direction.getX();
		float y = direction.getY();
		float z = direction.getZ();

		inclination = (float) toRadians(inclination);
		azimuth = (float) toRadians(azimuth);

		x = (float) (Math.sin(inclination) * Math.cos(azimuth));
		y = (float) (Math.sin(inclination) * Math.sin(azimuth));
		z = (float) (Math.cos(inclination));

		direction.setCartesian(x, y, z);
		return this;
	}

	@Override
	public AnchorVector rotate(float deg, Vector about) {
		final double c = Math.cos(deg);
		final double s = Math.sin(deg);
		final double C = 1.0 - c;

		final double v2[] = { about.getX(), about.getY(), about.getZ() };

		final double Q[][] = new double[][] {
				{ v2[0] * v2[0] * C + c, v2[1] * v2[0] * C + v2[2] * s,
						v2[2] * v2[0] * C - v2[1] * s },
				{ v2[1] * v2[0] * C - v2[2] * s, v2[1] * v2[1] * C + c,
						v2[2] * v2[1] * C + v2[0] * s },
				{ v2[0] * v2[2] * C + v2[1] * s, v2[2] * v2[1] * C - v2[0] * s,
						v2[2] * v2[2] * C + c } };

		float x = direction.getX();
		float y = direction.getY();
		float z = direction.getZ();

		x = (float) (x * Q[0][0] + x * Q[0][1] + x * Q[0][2]);
		y = (float) (y * Q[1][0] + y * Q[1][1] + y * Q[1][2]);
		z = (float) (z * Q[2][0] + z * Q[2][1] + z * Q[2][2]);

		direction.setCartesian(x, y, z);

		return this;
	}

	@Override
	public String print() {
		return printSimple() + ", origin at " + origin.printSimple()
				+ ", direction " + direction.printSimple() + ", lever length "
				+ leverlen;
	}

	@Override
	public String printSimple() {
		return "(x=" + getX() + ", y=" + getY() + ", z=" + getZ() + ")";
	}

	@Override
	public void reset() {
		// do nothing
	}

	public static double toDeg(double rad) {
		return rad * 180f / Math.PI;
	}

	public static double toRadians(double deg) {
		return deg * Math.PI / 180;
	}

	@Override
	public Vector setCartesian(float x, float y, float z) {
		float nx = x - origin.getX();
		float ny = y - origin.getY();
		float nz = z - origin.getZ();
		float len = (float) Math.sqrt(Math.pow(nx, 2) + Math.pow(ny, 2)
				+ Math.pow(nz, 2));
		nx /= len;
		ny /= len;
		nz /= len;
		
		direction.setCartesian(nx, ny, nz);
		return this;
	}

	@Override
	public Vector setSpherical(float r, float inc, float azi) {
		r = 1;
		float x = (float) (r * Math.sin(inc) * Math.cos(azi));
		float y = (float) (r * Math.sin(inc) * Math.sin(azi));
		float z = (float) (r * Math.cos(inc));
		direction.setCartesian(x, y, z);
		return this;
	}

	@Override
	public Vector set(Vector v) {
		return setCartesian(v.getX(), v.getY(), v.getZ());
	}

	@Override
	public Vector add(float x, float y, float z) {
		float nx = x + getX();
		float ny = y + getY();
		float nz = z + getZ();
		return setCartesian(nx, ny, nz);
	}

	@Override
	public Vector add(Vector v, float scale) {
		float nx = v.getX() * scale + getX();
		float ny = v.getY() * scale + getY();
		float nz = v.getZ() * scale + getZ();
		return setCartesian(nx, ny, nz);
	}

	@Override
	public Vector add(Vector v) {
		return add(v, 1);
	}

	@Override
	public Vector multiply(float mx, float my, float mz) {
		float nx = direction.getX() * mx;
		float ny = direction.getY() * my;
		float nz = direction.getZ() * mz;
		float len = (float) Math.sqrt(Math.pow(nx, 2) + Math.pow(ny, 2)
				+ Math.pow(nz, 2));
		nx /= len;
		ny /= len;
		nz /= len;
		direction.setCartesian(nx, ny, nz);
		return this;
	}

	@Override
	public Vector multiply(Vector v, boolean inv) {
		float nx = direction.getX() * v.getX();
		float ny = direction.getY() * v.getY();
		float nz = direction.getZ() * v.getZ();
		if (inv) {
			nx *= -1;
			ny *= -1;
			nz *= -1;
		}
		return setCartesian(nx, ny, nz);
	}

	@Override
	public boolean isZero() {
		if (getX() == 0 && getY() == 0 && getZ() == 0)
			return true;
		else
			return false;
	}

}
