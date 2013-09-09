package com.gdroid.pickalock.drawing;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

public interface GameRenderer extends Renderer {
	public void renderFrame();
	public void setSurface(GLSurface surface);
	public GLSurface getSurface();
	public Camera getCamera();
}
