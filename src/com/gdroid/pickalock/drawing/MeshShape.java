package com.gdroid.pickalock.drawing;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;

import javax.microedition.khronos.opengles.GL10;

public abstract class MeshShape extends Shape {

	private final int size;
	private int mW;
	private int mH;

	private final DrawingMode drawingMode;

	public enum DrawingMode {
		SHARED, // points are shared among quads
		INDEPENDENT // points are independent for each quad
	}

	public MeshShape(DrawingMode mode, int vertsAcross, int vertsDown) {
		super(DrawingMethod.ELEMENTS, GL10.GL_TRIANGLES, 3, 2, 4);

		if (vertsAcross < 0 || vertsAcross >= 65536) {
			throw new IllegalArgumentException("vertsAcross");
		}
		if (vertsDown < 0 || vertsDown >= 65536) {
			throw new IllegalArgumentException("vertsDown");
		}
		if (vertsAcross * vertsDown >= 65536) {
			throw new IllegalArgumentException(
					"vertsAcross * vertsDown >= 65536");
		}

		drawingMode = mode;
		size = vertsAcross * vertsDown;

		mW = vertsAcross;
		mH = vertsDown;

		final int CHAR_SIZE = 2;

		int quadW = mW - 1;
		int quadH = mH - 1;
		int quadCount = quadW * quadH;
		int indexCount = quadCount * 6;

		CharBuffer indexBuffer = ByteBuffer
				.allocateDirect(CHAR_SIZE * indexCount)
				.order(ByteOrder.nativeOrder()).asCharBuffer();

		/**
		 * Initialize triangle list mesh.
		 * 
		 * Ay[y*W+x]     ----   By[y*W+(x+1)]  
		 *  |  						|
		 *  |						|
		 *  |						|
		 *  |						|
		 * Cy[(y+1)*W+x] ----   Dy[(y+1)*W+(x+1)] 
		 * 
		 * 2x triangle: AyByCy, ByCyDy
		 */
		
		int m;
		switch (drawingMode) {
		case INDEPENDENT:
			m = 2;
		case SHARED:
			m = 1;
		 {
			int i = 0;
			for (int y = 0; y < quadH; y++) {
				for (int x = 0; x < quadW; x++) {
					char a = (char) (m*y * mW + x);
					char b = (char) (m*y * mW + x + 1);
					char c = (char) ((m*y + 1) * mW + x);
					char d = (char) ((m*y + 1) * mW + x + 1);

					// upper triangle
					indexBuffer.put(i++, a);
					indexBuffer.put(i++, b);
					indexBuffer.put(i++, c);

					// lower triangle
					indexBuffer.put(i++, b);
					indexBuffer.put(i++, c);
					indexBuffer.put(i++, d);
				}
			}
		}
			break;
			
		default:
			break;

		}

		super.prepareIndexes(indexCount, indexBuffer);

	}
	
	public final DrawingMode getDrawingMode() {
		return drawingMode;
	}

	public int getMeshSize() {
		return size;
	}

	public int getIndex(int i, int j) {
		if (i < 0 || i >= mW) {
			throw new IllegalArgumentException("i");
		}
		if (j < 0 || j >= mH) {
			throw new IllegalArgumentException("j");
		}

		final int index = mW * j + i;
		return index;
	}

	public abstract void set(int i, int j, float x, float y, float z, float u,
			float v, float[] color);
}
