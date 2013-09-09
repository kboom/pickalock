package com.gdroid.pickalock.core.positioning;

/**
 * Vector that joins other vectors. It is fully dynamic that is change in any of
 * joined vectors immidiately changes this vector values.
 * 
 * @author kboom
 * 
 */
public class JoinedVector implements Vector {

	private final Vector joinOffset;
	private final Vector[] joinedArr;

	/**
	 * Offset is normalized by default. Initial normalisation depends on
	 * normalization state of its contents.
	 * 
	 * @param joinOff
	 * @param v
	 * @param other
	 */
	public JoinedVector(Vector joinOff, Vector v, Vector... other) {
		joinOffset = joinOff;
		joinedArr = new Vector[1 + other.length];
		joinedArr[0] = v;
		for (int i = 0; i < other.length; i++) {
			joinedArr[1 + i] = other[i];
		}
	}

	@Override
	public float getX() {
		float sumx = joinOffset.getX();

		for (int i = 0; i < joinedArr.length; i++) {
			sumx += joinedArr[i].getX();
		}

		return sumx;
	}

	@Override
	public float getY() {
		float sumy = joinOffset.getX();

		for (int i = 0; i < joinedArr.length; i++) {
			sumy += joinedArr[i].getY();
		}

		return sumy;
	}

	@Override
	public float getZ() {
		float sumz = joinOffset.getZ();

		for (int i = 0; i < joinedArr.length; i++) {
			sumz += joinedArr[i].getZ();
		}

		return sumz;
	}

	public final Vector getOffset() {
		return joinOffset;
	}

	@Override
	public Vector add(Vector v, float scale) {
		return add(scale * v.getX(), scale * v.getY(), scale * v.getZ());
	}

	@Override
	public JoinedVector add(float x, float y, float z) {
		joinOffset.add(x, y, z);
		return this;
	}

	@Override
	public JoinedVector add(Vector v) {
		joinOffset.add(v);
		return this;
	}

	@Override
	public float getR2() {
		float x = getX();
		float y = getY();

		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

	@Override
	public float getR3() {
		return (float) Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2)
				+ Math.pow(getZ(), 2));
	}

	@Override
	public float getAngle2With(Vector v) {
		float x = getX();
		float y = getY();

		float r2 = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		return (float) Math.acos((x * v.getX() + y * v.getY())
				/ (r2 * v.getR2()));
	}

	@Override
	public float getAngle3With(Vector v) {
		float x = getX();
		float y = getY();
		float z = getZ();

		float r3 = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)
				+ Math.pow(z, 2));
		return (float) Math.acos((x * v.getX() + y * v.getY() + z * v.getZ())
				/ (r3 * v.getR3()));
	}

	@Override
	public JoinedVector multiply(Vector v, boolean inv) {
		if (inv)
			return multiply(1 / v.getX(), 1 / v.getY(), 1 / v.getZ());
		else
			return multiply(v.getX(), v.getY(), v.getZ());
	}

	@Override
	public JoinedVector multiply(float dx, float dy, float dz) {
		float x = joinOffset.getX() * dx;
		float y = joinOffset.getY() * dy;
		float z = joinOffset.getZ() * dz;
		joinOffset.setCartesian(x, y, z);
		return this;
	}

	@Override
	public JoinedVector setCartesian(float x, float y, float z) {
		joinOffset.setCartesian(x, y, z);
		return this;
	}

	@Override
	public JoinedVector set(Vector v) {
		joinOffset.set(v);
		return this;
	}

	@Override
	public float getIncAngle() {
		return (float) toDeg(Math.asin(getZ() / getR3()));
	}

	@Override
	public float getAziAngle() {
		float x = getX();
		float y = getY();
		float result = (float) toDeg(Math.atan2(y, x));
		return y > 0 ? result : result + 360;
	}

	@Override
	public void reset() {
		joinOffset.reset();
	}

	@Override
	public String print() {
		String result = "There are " + (joinedArr.length + 1)
				+ " vectors joined:";
		for (int i = 0; i < joinedArr.length; i++)
			result += "\nno. " + i + ": " + joinedArr[i].printSimple();

		result += "\nwith added offset: " + joinOffset.printSimple();
		result += "\nRepresentation: " + this.printSimple();

		return result;
	}

	@Override
	public String printSimple() {
		return "(x=" + getX() + ", y=" + getY() + ", z=" + getZ() + ")";
	}

	@Override
	public boolean isZero() {
		if (getX() == 0 && getY() == 0 && getZ() == 0)
			return true;
		else
			return false;
	}

	public static double toDeg(double rad) {
		return rad * 180f / Math.PI;
	}

	@Override
	public JoinedVector setSpherical(float r, float inc, float azi) {
		this.joinOffset.setSpherical(r, inc, azi);
		return this;
	}

	@Override
	public JoinedVector rotate(float dinc, float dazi) {
		this.joinOffset.rotate(dinc, dazi);
		return this;
	}

	@Override
	public JoinedVector rotate(float deg, Vector about) {
		joinOffset.rotate(deg, about);
		return this;
	}

}
