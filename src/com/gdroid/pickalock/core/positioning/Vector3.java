package com.gdroid.pickalock.core.positioning;

public class Vector3 implements Vector {

	private float x, y, z;

	protected Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	protected Vector3(Vector v) {
		this(v.getX(), v.getY(), v.getZ());
	}

	protected Vector3(float x, float y) {
		this(x, y, 0f);
	}

	protected Vector3() {
		this(0f, 0f, 0f);
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public float getZ() {
		return z;
	}

	@Override
	public Vector3 add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	@Override
	public Vector3 add(Vector v, float scale) {
		return add(scale * v.getX(), scale * v.getY(), scale * v.getZ());
	}

	@Override
	public Vector add(Vector v) {
		return add(v, 1);
	}

	@Override
	public float getR2() {
		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

	@Override
	public float getR3() {
		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)
				+ Math.pow(z, 2));
	}

	@Override
	public float getAngle2With(final Vector v) {
		return (float) Math.acos((this.x * v.getX() + this.y * v.getY())
				/ (this.getR2() * v.getR2()));
	}

	@Override
	public float getAngle3With(final Vector v) {
		return (float) Math
				.acos((this.x * v.getX() + this.y * v.getY() + this.z
						* v.getZ())
						/ (this.getR3() * v.getR3()));
	}

	@Override
	public Vector3 multiply(Vector v, boolean inv) {
		if (inv)
			return this.multiply(1 / v.getX(), 1 / v.getY(), 1 / v.getZ());
		else
			return this.multiply(v.getX(), v.getY(), v.getZ());
	}

	@Override
	public Vector3 multiply(float dx, float dy, float dz) {
		this.x *= dx;
		this.y *= dy;
		this.z *= dz;
		return this;
	}

	@Override
	public Vector3 setCartesian(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	@Override
	public Vector setSpherical(float r, float inc, float azi) {
		x = (float) (r * Math.sin(inc) * Math.cos(azi));
		y = (float) (r * Math.sin(inc) * Math.sin(azi));
		z = (float) (r * Math.cos(inc));
		return this;
	}

	@Override
	public Vector set(Vector v) {
		x = v.getX();
		y = v.getY();
		z = v.getZ();
		return this;
	}

	@Override
	public float getIncAngle() {
		return (float) toDeg(Math.acos(getZ() / getR3())); // był sin i działało?
	}

	@Override
	public float getAziAngle() {
		float result = (float) toDeg(Math.atan2(y, x));
		return y > 0 ? result : result + 360;
	}

	public Vector3 rotate(float dinc, float dazi) {
		float r = this.getR3();
		x += (float) (r * Math.sin(dinc) * Math.cos(dazi));
		y += (float) (r * Math.sin(dinc) * Math.sin(dazi));
		z += (float) (r * Math.cos(dinc));
		return this;
	}

	public Vector3 rotate(float deg, Vector about) {
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

		x = (float) (x * Q[0][0] + x * Q[0][1] + x * Q[0][2]);
		y = (float) (y * Q[1][0] + y * Q[1][1] + y * Q[1][2]);
		z = (float) (z * Q[2][0] + z * Q[2][1] + z * Q[2][2]);

		return this;
	}

	@Override
	public String print() {
		return "(x=" + this.x + ",y=" + this.y + ",z=" + this.z + ", R2="
				+ getR2() + ", R3=" + getR3() + ",Inclination=" + getIncAngle()
				+ ",Azimuth=" + getAziAngle() + ")";
	}

	@Override
	public String printSimple() {
		return "(x=" + this.x + ", y=" + this.y + ", z=" + this.z + ")";
	}

	@Override
	public boolean isZero() {
		if (this.x == 0 && this.y == 0 && this.z == 0)
			return true;
		else
			return false;
	}

	@Override
	public void reset() {
		x = 0;
		y = 0;
		z = 0;
	}

	public static double toDeg(double rad) {
		return rad * 180f / Math.PI;
	}

}
