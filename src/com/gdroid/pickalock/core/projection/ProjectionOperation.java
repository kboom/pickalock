package com.gdroid.pickalock.core.projection;

import javax.microedition.khronos.opengles.GL10;

/**
 * Class does not modify any vector but still may use ones (almost surely
 * drawing vector) to apply some operations to model view matrix.
 * 
 * This operations come in sequentially.
 * 
 * @author kboom
 * 
 */
public interface ProjectionOperation {
	void project(GL10 gl);
}
