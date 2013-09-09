package com.gdroid.pickalock.drawing;

import javax.microedition.khronos.opengles.GL10;

public interface Renderable {
	public void beginDrawing(GL10 gl);
	public void draw(GL10 gl);
	public void endDrawing(GL10 gl);
}
