package com.gdroid.pickalock.core.projection;

import javax.microedition.khronos.opengles.GL10;

import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.core.positioning.Vector3;
import com.gdroid.pickalock.utils.SLog;

public class RotationProjOperation implements ProjectionOperation {

	private final Vector source;
	
	public RotationProjOperation(Vector source) {
		this.source = source;
	}

	@Override
	public void project(GL10 gl) {
		gl.glRotatef(source.getAziAngle(), 0f, 0f, 1f);	
		// gl.glRotatef(source.getElevAngle(), 0f,1f,0f);
	}
	

}
