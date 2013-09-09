package com.gdroid.pickalock.core.projection;

import javax.microedition.khronos.opengles.GL10;

import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.core.positioning.Vector3;
import com.gdroid.pickalock.utils.SLog;

public class LocalProjOperation implements ProjectionOperation {

	private Vector point;

	public LocalProjOperation(Vector point) {
		this.point = point;
	}

	@Override
	public void project(GL10 gl) {
		float inclination = point.getIncAngle();
		if (inclination != 0)
			gl.glRotatef(inclination, 0f, -1f, 0f);
		gl.glRotatef(point.getAziAngle(), 0f, 0f, 1f);
	}
}
