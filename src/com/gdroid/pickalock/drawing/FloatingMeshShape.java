package com.gdroid.pickalock.drawing;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class FloatingMeshShape extends MeshShape {

	private FloatBuffer mFloatVertexBuffer;
	private FloatBuffer mFloatTexCoordBuffer;
	private FloatBuffer mFloatColorBuffer;

	public FloatingMeshShape(DrawingMode mode, int vertsAcross, int vertsDown) {
		super(mode, vertsAcross, vertsDown);
		final int FLOAT_SIZE = 4;

		int meshSize = super.getMeshSize();

		mFloatVertexBuffer = ByteBuffer
				.allocateDirect(FLOAT_SIZE * meshSize * 3)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mFloatTexCoordBuffer = ByteBuffer
				.allocateDirect(FLOAT_SIZE * meshSize * 2)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mFloatColorBuffer = ByteBuffer
				.allocateDirect(FLOAT_SIZE * meshSize * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();

		super.linkBuffers(GL10.GL_FLOAT, FLOAT_SIZE, mFloatVertexBuffer,
				mFloatTexCoordBuffer, mFloatColorBuffer);
	}

	@Override
	public void set(int i, int j, float x, float y, float z, float u, float v,
			float[] color) {

		final int index = super.getIndex(i, j);
		final int posIndex = index * 3;
		final int texIndex = index * 2;
		final int colorIndex = index * 4;

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
