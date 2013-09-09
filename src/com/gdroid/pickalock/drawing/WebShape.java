package com.gdroid.pickalock.drawing;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class WebShape extends Shape {

	private FloatBuffer mFloatVertexBuffer;
	private FloatBuffer mFloatTexCoordBuffer;
	private FloatBuffer mFloatColorBuffer;
	
	public WebShape(int size) {
		super(DrawingMethod.ARRAYS, GL10.GL_TRIANGLE_FAN, 3, 2, 4);
		
		// dummy buffer
		CharBuffer indexBuffer = ByteBuffer
				.allocateDirect(0).order(ByteOrder.nativeOrder()).asCharBuffer();
		
		super.prepareIndexes(size, null);
		
		final int FLOAT_SIZE = 4;

		mFloatVertexBuffer = ByteBuffer
				.allocateDirect(FLOAT_SIZE * size * 3)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mFloatTexCoordBuffer = ByteBuffer
				.allocateDirect(FLOAT_SIZE * size * 2)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mFloatColorBuffer = ByteBuffer
				.allocateDirect(FLOAT_SIZE * size * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();

		super.linkBuffers(GL10.GL_FLOAT, FLOAT_SIZE, mFloatVertexBuffer,
				mFloatTexCoordBuffer, mFloatColorBuffer);
	}
	
	public void set(int i, float x, float y, float z, float u, float v, float[] color) {
		final int posIndex = i * 3;
		final int texIndex = i * 2;
		final int colorIndex = i * 4;

		mFloatVertexBuffer.put(posIndex, x);
		mFloatVertexBuffer.put(posIndex + 1, y);
		mFloatVertexBuffer.put(posIndex + 2, z);

		mFloatTexCoordBuffer.put(texIndex, u);
		mFloatTexCoordBuffer.put(texIndex + 1, v);

		if (color != null) {
			mFloatColorBuffer.put(colorIndex, color[0]);
			mFloatColorBuffer.put(colorIndex + 1, color[1]);
			mFloatColorBuffer.put(colorIndex + 2, color[2]);
			mFloatColorBuffer.put(colorIndex + 3, color[3]);
		}
	}

}
