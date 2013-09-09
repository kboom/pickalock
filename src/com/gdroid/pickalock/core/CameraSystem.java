package com.gdroid.pickalock.core;

import com.gdroid.pickalock.drawing.Camera;

/**
 * Proxy / Adapter for camera.
 * @author kboom
 *
 */
public class CameraSystem extends BaseObject implements Camera  {

	private Camera camera;
	
	public CameraSystem() {
		
	}
	
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void accomodateTo(float nearX, float farX) {
		camera.accomodateTo(nearX, farX);		
	}


	@Override
	public void move(float x, float y, float z) {
		camera.move(x, y, z);		
	}

	@Override
	public void rotate(float xAngle, float yAngle, float zAngle) {
		camera.rotate(xAngle, yAngle, zAngle);		
	}

	public void dimensionsChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lookAt(float x, float y, float z) {
		camera.lookAt(x, y, z);		
	}

	@Override
	public void getRay(float x, float y, float z, float[] ray) {
		// TODO Auto-generated method stub
		
	}


}
