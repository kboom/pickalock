package com.gdroid.pickalock.drawing;

import javax.microedition.khronos.opengles.GL10;

import com.gdroid.pickalock.pooling.FixedSizeArray;

/**
 * Class managing buffers of all shapes. Introduces some order and queueing
 * affecting performance. For instance objects that are assign high level
 * of occurance will not release buffers instantly after being removed from
 * rendering queue.
 * 
 * Holds objects that are currently in memory. 
 * 
 * @author kboom
 * 
 */
public class BufferLibrary implements VBOSupportable {
	private final boolean supportsVBO;
	private static final int GRID_LIST_SIZE = 32;
	private FixedSizeArray<VBOSupportable> mGridList;
	private FixedSizeArray<VBOSupportable> pendingAdditions;
	private FixedSizeArray<VBOSupportable> pendingRemovals;

	public BufferLibrary(boolean supportsVBO) {
		super();
		this.supportsVBO = supportsVBO;
		
		if (!supportsVBO) return;
		
		mGridList = new FixedSizeArray<VBOSupportable>(GRID_LIST_SIZE);
		pendingAdditions = new FixedSizeArray<VBOSupportable>(GRID_LIST_SIZE);
		pendingRemovals = new FixedSizeArray<VBOSupportable>(GRID_LIST_SIZE);
	}

	public void add(VBOSupportable obj) {
		if (!supportsVBO) return;
		pendingAdditions.add(obj);
	}
	
	public void remove(VBOSupportable obj) {
		if (!supportsVBO) return;
		pendingRemovals.add(obj);
	}
	
	public void generateHardwareBuffers(GL10 gl) {
		if (!supportsVBO) return;
		else if(pendingAdditions.getCount() == 0) return;
		
		for(VBOSupportable obj : pendingAdditions) {
			if(!mGridList.contains(obj)) {
				obj.generateHardwareBuffers(gl);
				mGridList.add(obj);
			}
		}
		
		pendingAdditions.clear();
	}

	public void releaseHardwareBuffers(GL10 gl) {
		if (!supportsVBO) return;
		else if(pendingRemovals.getCount() == 0) return;
		
		for(VBOSupportable obj : pendingRemovals) {
			if(mGridList.contains(obj)) {
				obj.releaseHardwareBuffers(gl);
				obj.invalidateHardwareBuffers();
				mGridList.remove(obj);
			}
		}
		
		pendingRemovals.clear();
	}

	public void invalidateHardwareBuffers() {
		if (!supportsVBO) return;
		
		for(VBOSupportable obj : mGridList) {
			if(mGridList.contains(obj)) {
				obj.invalidateHardwareBuffers();
			}
		}
		
		mGridList.clear();
	}

}
