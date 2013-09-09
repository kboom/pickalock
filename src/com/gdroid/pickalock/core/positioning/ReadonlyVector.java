package com.gdroid.pickalock.core.positioning;

public class ReadonlyVector implements Vector {

	private Vector proxiedVector;
	
	public ReadonlyVector(Vector v) {
		proxiedVector = v;
	}

	@Override
	public Vector add(float x, float y, float z) {
		throw new IllegalStateException("Trying to modify read-only vector!");
	}
	

	@Override
	public Vector add(Vector v) {
		throw new IllegalStateException("Trying to modify read-only vector!");
	}


	@Override
	public float getR2() {
		return proxiedVector.getR2();
	}

	@Override
	public float getAngle2With(Vector v) {
		return proxiedVector.getAngle2With(v);
	}

	@Override
	public float getAngle3With(Vector v) {
		return proxiedVector.getAngle3With(v);
	}

	@Override
	public float getR3() {
		return proxiedVector.getR3();
	}

	@Override
	public float getX() {
		return proxiedVector.getX();
	}

	@Override
	public float getY() {
		return proxiedVector.getY();
	}

	@Override
	public float getZ() {
		return proxiedVector.getZ();
	}
	
	@Override
	public float getIncAngle() {
		return proxiedVector.getIncAngle();
	}

	@Override
	public float getAziAngle() {
		return proxiedVector.getAziAngle();
	}

	@Override
	public Vector rotate(float dinc, float dazi) {
		throw new IllegalStateException("Trying to modify read-only vector!");
	}

	@Override
	public Vector rotate(float deg, Vector about) {
		throw new IllegalStateException("Trying to modify read-only vector!");
	}

	@Override
	public String print() {
		return proxiedVector.print();
	}

	@Override
	public String printSimple() {
		return proxiedVector.printSimple();
	}

	@Override
	public void reset() {
		throw new IllegalStateException("Trying to modify read-only vector!");
	}

	@Override
	public Vector setCartesian(float x, float y, float z) {
		throw new IllegalStateException("Trying to modify read-only vector!");
	}

	@Override
	public Vector setSpherical(float r, float inc, float azi) {
		throw new IllegalStateException("Trying to modify read-only vector!");
	}

	@Override
	public Vector set(Vector v) {
		throw new IllegalStateException("Trying to modify read-only vector!");
	}

	@Override
	public Vector add(Vector v, float scale) {
		throw new IllegalStateException("Trying to modify read-only vector!");
	}

	@Override
	public Vector multiply(float mx, float my, float mz) {
		throw new IllegalStateException("Trying to modify read-only vector!");
	}

	@Override
	public Vector multiply(Vector v, boolean inv) {
		throw new IllegalStateException("Trying to modify read-only vector!");
	}

	@Override
	public boolean isZero() {
		if(proxiedVector.isZero()) return true;
		else return false;
	}



}
