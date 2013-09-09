package com.gdroid.pickalock.core.projection;

import javax.microedition.khronos.opengles.GL10;

import com.gdroid.pickalock.core.Game;
import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.core.positioning.Vector3;
import com.gdroid.pickalock.utils.SLog;

public class OffsetProjOperation implements ProjectionOperation {

	private static final int did = SLog.register(OffsetProjOperation.class);
	static {
		SLog.setTag(did, "Global Projection Operation.");
	}
	
	private Vector point;

	public OffsetProjOperation(Vector point) {
		this.point = point;
	}
	
	@Override
	public void project(GL10 gl) {
		gl.glTranslatef(point.getX(), point.getY(), point.getZ());
	}

}
