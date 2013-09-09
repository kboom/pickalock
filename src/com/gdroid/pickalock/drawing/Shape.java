package com.gdroid.pickalock.drawing;

import java.nio.Buffer;
import java.nio.CharBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

public class Shape implements Renderable, VBOSupportable {

	private static final int did = SLog.register(Shape.class);
	static {
		SLog.setTag(did, "Grid.");
		SLog.setLevel(did, Level.DEBUG);
	}

	private CharBuffer mIndexBuffer;
	private Buffer mVertexBuffer;
	private Buffer mTexCoordBuffer;
	private Buffer mColorBuffer;
	private int mVertBufferIndex;
	private int mIndexBufferIndex;
	private int mTextureCoordBufferIndex;
	private int mColorBufferIndex;

	private boolean useTexture;
	private boolean useColor;

	private int mIndexCount;
	private boolean mUseHardwareBuffers;

	private int mCoordinateSize;
	private int mCoordinateType;
	private int mDrawingMode;
	private DrawingMethod mDrawingMethod;

	public enum DrawingMethod {
		ELEMENTS, ARRAYS
	}

	private int mVertCoordsPerPoint;
	private int mTexCoordsPerPoint;
	private int mColorValuesPerPoint;

	public Shape(DrawingMethod drawingType, int drawingMethod, int vcPerPoint,
			int tcPerPoint, int cvPerPoint) {		
		if (vcPerPoint > 4 || vcPerPoint < 2)
			throw new IllegalArgumentException(
					"Number of vertice coordinates can only be {2,3,4}!");

		// test other values
		mDrawingMethod = drawingType;
		mDrawingMode = drawingMethod;
		mVertCoordsPerPoint = vcPerPoint;
		mTexCoordsPerPoint = tcPerPoint;
		mColorValuesPerPoint = cvPerPoint;

		mUseHardwareBuffers = false;
	}

	protected void linkBuffers(int type, int size, Buffer vertexBuffer,
			Buffer texCoordBuffer, Buffer colorBuffer) {

		mVertexBuffer = vertexBuffer;
		mTexCoordBuffer = texCoordBuffer;
		mColorBuffer = colorBuffer;
		mCoordinateSize = size;
		mCoordinateType = type;
	}

	protected final void prepareIndexes(int indexCount, CharBuffer indexBuffer) {
		mIndexBuffer = indexBuffer;
		mIndexCount = indexCount;
		mVertBufferIndex = 0;
	}

	public void useTexture() {
		useTexture = true;
	}

	public void useColor() {
		useColor = true;
	}

	public final int getVertexBuffer() {
		return mVertBufferIndex;
	}

	public final int getTextureBuffer() {
		return mTextureCoordBufferIndex;
	}

	public final int getIndexBuffer() {
		return mIndexBufferIndex;
	}

	public final int getColorBuffer() {
		return mColorBufferIndex;
	}

	public final int getIndexCount() {
		return mIndexCount;
	}

	public boolean usingHardwareBuffers() {
		return mUseHardwareBuffers;
	}

	public void invalidateHardwareBuffers() {
		mVertBufferIndex = 0;
		mIndexBufferIndex = 0;
		mTextureCoordBufferIndex = 0;
		mColorBufferIndex = 0;
		mUseHardwareBuffers = false;
	}

	// może być błąd z indexbuffer, bo trochę oszukujemy
	// nie alokowaliśmy niczego (null check był)
	public void releaseHardwareBuffers(GL10 gl) {
		if (mUseHardwareBuffers) {
			if (gl instanceof GL11) {
				SLog.d(did, "Releasing hardware buffers...");
				GL11 gl11 = (GL11) gl;
				int[] buffer = new int[1];
				buffer[0] = mVertBufferIndex;
				gl11.glDeleteBuffers(1, buffer, 0);

				buffer[0] = mTextureCoordBufferIndex;
				gl11.glDeleteBuffers(1, buffer, 0);

				buffer[0] = mColorBufferIndex;
				gl11.glDeleteBuffers(1, buffer, 0);

				if (mIndexBuffer == null) {
					buffer[0] = mIndexBufferIndex;
					gl11.glDeleteBuffers(1, buffer, 0);
				}
			}

			invalidateHardwareBuffers();
		}
	}

	public void generateHardwareBuffers(GL10 gl) {
		if (!mUseHardwareBuffers) {
			if (gl instanceof GL11) {
				SLog.d(did, "Filling hardware buffers...");
				GL11 gl11 = (GL11) gl;
				int[] buffer = new int[1];

				// Allocate and fill the vertex buffer.
				gl11.glGenBuffers(1, buffer, 0);
				mVertBufferIndex = buffer[0];
				gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertBufferIndex);
				final int vertexSize = mVertexBuffer.capacity()
						* mCoordinateSize;
				gl11.glBufferData(GL11.GL_ARRAY_BUFFER, vertexSize,
						mVertexBuffer, GL11.GL_STATIC_DRAW);

				// Allocate and fill the texture coordinate buffer.
				gl11.glGenBuffers(1, buffer, 0);
				mTextureCoordBufferIndex = buffer[0];
				gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER,
						mTextureCoordBufferIndex);
				final int texCoordSize = mTexCoordBuffer.capacity()
						* mCoordinateSize;
				gl11.glBufferData(GL11.GL_ARRAY_BUFFER, texCoordSize,
						mTexCoordBuffer, GL11.GL_STATIC_DRAW);

				// Allocate and fill the color buffer.
				gl11.glGenBuffers(1, buffer, 0);
				mColorBufferIndex = buffer[0];
				gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mColorBufferIndex);
				final int colorSize = mColorBuffer.capacity() * mCoordinateSize;
				gl11.glBufferData(GL11.GL_ARRAY_BUFFER, colorSize,
						mColorBuffer, GL11.GL_STATIC_DRAW);

				// Unbind the array buffer.
				gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

				// Allocate and fill the index buffer.
				gl11.glGenBuffers(1, buffer, 0);
				mIndexBufferIndex = buffer[0];
				gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER,
						mIndexBufferIndex);
				// A char is 2 bytes.
				if (mIndexBuffer != null) {
					final int indexSize = mIndexBuffer.capacity() * 2;
					gl11.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, indexSize,
							mIndexBuffer, GL11.GL_STATIC_DRAW);
				}

				// Unbind the element array buffer.
				gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);

				mUseHardwareBuffers = true;

				assert mVertBufferIndex != 0;
				assert mTextureCoordBufferIndex != 0;
				assert mIndexBufferIndex != 0;
				assert gl11.glGetError() == 0;

			} else
				SLog.d(did, "Could not fill hardware buffers.");
		}
	}

	@Override
	public void beginDrawing(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		if (useTexture) {
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		} else {
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}

		if (useColor) {
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		} else {
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}
	}

	@Override
	public final void draw(GL10 gl) {		
		if (!mUseHardwareBuffers) {
			gl.glVertexPointer(mVertCoordsPerPoint, mCoordinateType, 0,
					mVertexBuffer);

			if (useTexture) {
				gl.glTexCoordPointer(mTexCoordsPerPoint, mCoordinateType, 0,
						mTexCoordBuffer);
			}

			if (useColor) {
				gl.glColorPointer(mColorValuesPerPoint, mCoordinateType, 0,
						mColorBuffer);
			}

			if (mDrawingMethod == DrawingMethod.ELEMENTS)
				gl.glDrawElements(mDrawingMode, mIndexCount,
						GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
			else
				gl.glDrawArrays(mDrawingMode, 0, mIndexCount);

		} else {	
			GL11 gl11 = (GL11) gl;
			// draw using hardware buffers
			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertBufferIndex);
			gl11.glVertexPointer(mVertCoordsPerPoint, mCoordinateType, 0, 0);

			if (useTexture) {
				gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER,
						mTextureCoordBufferIndex);
				gl11.glTexCoordPointer(mTexCoordsPerPoint, mCoordinateType, 0,
						0);
			}

			if (useColor) {
				gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mColorBufferIndex);
				gl11.glColorPointer(mColorValuesPerPoint, mCoordinateType, 0, 0);
			}

			gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferIndex);

			if (mDrawingMethod == DrawingMethod.ELEMENTS)
				gl11.glDrawElements(mDrawingMode, mIndexCount,
						GL11.GL_UNSIGNED_SHORT, 0);
			else
				gl11.glDrawArrays(mDrawingMode, 0, mIndexCount);

			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
			gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);

		}
	}

	@Override
	public void endDrawing(GL10 gl) {
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		if (useTexture) {
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
		if (useColor) {
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}
	}

}
