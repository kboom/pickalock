package com.gdroid.pickalock.drawing;

import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;

import com.gdroid.pickalock.core.RenderComponent;
import com.gdroid.pickalock.pooling.FixedSizeArray;
import com.gdroid.pickalock.pooling.FixedSizeArray.StaticIterator;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

public class RenderScheduler {

	private static final int did = SLog.register(RenderScheduler.class);
	static {
		SLog.setTag(did, "Render Scheduler.");
		SLog.setLevel(did, Level.DEBUG);
	}
	
	private Iterator<ObjectAvatar> it;
	private final BufferLibrary bufferLibrary = new BufferLibrary(true);
	private final FixedSizeArray<Renderable> renderList = new FixedSizeArray<Renderable>(
			20);

	public void schedule(Renderable component, ObjectAvatar obj) {
		SLog.d(did, "Schedule call for rendering...");
		renderList.add(component);
		bufferLibrary.add(obj);
	}
	
	public void unschedule(Renderable component, ObjectAvatar obj) {
		SLog.d(did, "Unschedule call for rendering...");
		renderList.remove(component);
		bufferLibrary.remove(obj);
	}

	public Iterable<Renderable> getRenderList(GL10 gl) {
		bufferLibrary.releaseHardwareBuffers(gl);
		bufferLibrary.generateHardwareBuffers(gl);	
		return renderList.iterator();
	}

	public int queuedCount() {
		return renderList.getCount();
	}

}
