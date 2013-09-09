package com.gdroid.pickalock.drawing;

import javax.microedition.khronos.opengles.GL10;

import com.gdroid.pickalock.utils.SLog;

public class TexturedAvatar implements ObjectAvatar {

	private static final int did = SLog.register(TexturedAvatar.class);
	static {
		SLog.setTag(did, "Texture avatar.");
	}

	private Shape mShape;
	private Texture mTexture;

	public TexturedAvatar(Shape shape, Texture texture) {
		if (texture == null)
			throw new IllegalArgumentException(
					"Textured avatar must use a texture!");
		mShape = shape;
		mTexture = texture;
		mShape.useTexture();
	}

	@Override
	public void generateHardwareBuffers(GL10 gl) {
		mShape.generateHardwareBuffers(gl);
	}

	@Override
	public void invalidateHardwareBuffers() {
		mShape.invalidateHardwareBuffers();
	}

	@Override
	public void releaseHardwareBuffers(GL10 gl) {
		mShape.releaseHardwareBuffers(gl);
	}

	@Override
	public void beginDrawing(GL10 gl) {
		
	}

	@Override
	public void draw(GL10 gl) {
		//mTexture.beginDrawing(gl);
		
		//gl.glLoadIdentity();
		//gl.glScalef(0.5f, 0.5f, 0.5f);
		
		mShape.beginDrawing(gl);
		mShape.draw(gl);
		mShape.endDrawing(gl);

		//mTexture.endDrawing(gl);
	}

	@Override
	public void endDrawing(GL10 gl) {
		// TODO Auto-generated method stub
		
	}

}
