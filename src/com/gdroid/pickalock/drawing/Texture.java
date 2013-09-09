package com.gdroid.pickalock.drawing;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

public class Texture implements Renderable {

	private final TextureMapping mapping;

	private boolean done = false;

	/**
	 * All bitmaps should be recycled.
	 * 
	 * @param gl
	 * @param b
	 * @param bitmaps
	 */
	public Texture(TextureMapping mapping) {
		this.mapping = mapping;
	}

	/*
	 * public void popTexture(GL10 gl, int i) { if (i >= textureHandles.length)
	 * throw new IllegalArgumentException("Texture not found in set.");
	 * gl.glClientActiveTexture(GL10.GL_TEXTURE0 + i); }
	 */

	@Override
	public void beginDrawing(GL10 gl) {
		if(!mapping.loaded) return;
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				GL10.GL_MODULATE);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, mapping.name);
		gl.glClientActiveTexture(GL10.GL_TEXTURE0);
	}

	@Override
	public void draw(GL10 gl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endDrawing(GL10 gl) {
		if(!mapping.loaded) return;
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}

}
