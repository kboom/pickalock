package com.gdroid.pickalock.core.projection;

import javax.microedition.khronos.opengles.GL10;

import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.core.positioning.Vector3;
import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

public class RadialProjOperation implements ProjectionOperation {
	
	private float radius;
	private Vector point;
	
	public RadialProjOperation(Vector point, float radius) {
		this.radius = radius;
		this.point = point;
	}
	
	@Override
	public void project(GL10 gl) {
		float inclination = point.getIncAngle();
		float azimuth = point.getAziAngle();
		
		if(point.getX() > 0) {
			gl.glRotatef(inclination, 0f, -1f, 0f);
			gl.glRotatef(azimuth, 0f, 0f, 1f);
		} else {			
			gl.glRotatef(inclination, 0f, 1f, 0f);
			gl.glRotatef(azimuth, 0f, 0f, 1f);
		}
		gl.glTranslatef(radius, 0f, 0f);			
	}

}
