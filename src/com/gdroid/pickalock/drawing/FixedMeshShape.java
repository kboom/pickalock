package com.gdroid.pickalock.drawing;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

public class FixedMeshShape extends MeshShape {

	private IntBuffer mFixedVertexBuffer;
	private IntBuffer mFixedTexCoordBuffer;
	private IntBuffer mFixedColorBuffer;
	
	public FixedMeshShape(DrawingMode mode, int vertsAcross, int vertsDown) {
		super(mode, vertsAcross, vertsDown);
		
		final int FIXED_SIZE = 4;
		int meshSize = super.getMeshSize();
		
		
		mFixedVertexBuffer = ByteBuffer
				.allocateDirect(FIXED_SIZE * meshSize * 3)
				.order(ByteOrder.nativeOrder()).asIntBuffer();
		mFixedTexCoordBuffer = ByteBuffer
				.allocateDirect(FIXED_SIZE * meshSize * 2)
				.order(ByteOrder.nativeOrder()).asIntBuffer();
		mFixedColorBuffer = ByteBuffer
				.allocateDirect(FIXED_SIZE * meshSize * 4)
				.order(ByteOrder.nativeOrder()).asIntBuffer();

		super.linkBuffers(GL10.GL_FIXED, FIXED_SIZE, mFixedVertexBuffer, mFixedTexCoordBuffer, mFixedColorBuffer);
		
	}

	@Override
	public void set(int i, int j, float x, float y, float z, float u, float v,
			float[] color) {
		
		final int index = super.getIndex(i, j);
		final int posIndex = index * 3;
		final int texIndex = index * 2;
		final int colorIndex = index * 4;
		
		
		mFixedVertexBuffer.put(posIndex, (int) (x * (1 << 16)));
		mFixedVertexBuffer.put(posIndex + 1, (int) (y * (1 << 16)));
		mFixedVertexBuffer.put(posIndex + 2, (int) (z * (1 << 16)));

		mFixedTexCoordBuffer.put(texIndex, (int) (u * (1 << 16)));
		mFixedTexCoordBuffer.put(texIndex + 1, (int) (v * (1 << 16)));

		if (color != null) {
			mFixedColorBuffer.put(colorIndex, (int) (color[0] * (1 << 16)));
			mFixedColorBuffer.put(colorIndex + 1,
					(int) (color[1] * (1 << 16)));
			mFixedColorBuffer.put(colorIndex + 2,
					(int) (color[2] * (1 << 16)));
			mFixedColorBuffer.put(colorIndex + 3,
					(int) (color[3] * (1 << 16)));
		}
		
	}

}
