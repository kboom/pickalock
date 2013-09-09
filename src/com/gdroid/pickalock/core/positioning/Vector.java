package com.gdroid.pickalock.core.positioning;

public interface Vector {

	public float getX();

	public float getY();

	/**
	 * Zero for 2D vector.
	 * 
	 * @return
	 */
	public float getZ();

	/**
	 * The angle between this vector and XY plane. Subtract from 180 to get
	 * elevation angle.
	 * 
	 * @return
	 */
	public float getIncAngle();

	/**
	 * The angle between this vector and OX axis at XY plane. Covers whole 360
	 * degrees.
	 * 
	 * @return angle in degrees.
	 */
	public float getAziAngle();

	/**
	 * 
	 * @return
	 */
	public float getR2();

	/**
	 * 
	 * @return
	 */
	public float getR3();

	public float getAngle2With(final Vector v);

	public float getAngle3With(final Vector v);

	/**
	 * Use to rotate this vector by spherical angles around its origin. The
	 * length of the vector is not changed.
	 * 
	 * @param dinc
	 * @param dazi
	 * @return
	 */
	public Vector rotate(float dinc, float dazi);

	/**
	 * Use to rotate this vector about another by a given angle. The length of
	 * the vector is not changed.
	 * 
	 * @param deg
	 * @param about
	 * @return
	 */
	public Vector rotate(float deg, Vector about);

	public String print();

	public String printSimple();

	public void reset();
	
	/**
	 * Null value will preserve old one.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Vector setCartesian(float x, float y, float z);

	public Vector setSpherical(float r, float inc, float azi);

	public Vector set(Vector v);

	public Vector add(float x, float y, float z);

	public Vector add(Vector v, float scale);
	
	public Vector add(Vector v);

	public Vector multiply(float mx, float my, float mz);

	public Vector multiply(Vector v, boolean inv);
	

	/**
	 * Checks if this vector is zero valued.
	 * 
	 * @return
	 */
	public boolean isZero();

}
