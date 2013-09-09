package com.gdroid.pickalock.drawing;

public interface Camera {
	public void accomodateTo(float nearX, float farX);

	// rotates too
	public void move(float x, float y, float z);

	public void lookAt(float x, float y, float z);

	public void rotate(float xAngle, float yAngle, float zAngle);

	/**
	 * Gets coordinates of ray which will pass through eye point and touch
	 * coordinates.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void getRay(float x, float y, float z, float [] ray);
}
