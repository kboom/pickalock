package com.gdroid.pickalock.drawing;

import javax.microedition.khronos.opengles.GL10;

public interface VBOSupportable {
	public void generateHardwareBuffers(GL10 gl);
	public void invalidateHardwareBuffers();
	public void releaseHardwareBuffers(GL10 gl);
}
