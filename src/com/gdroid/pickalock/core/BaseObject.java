package com.gdroid.pickalock.core;

import com.gdroid.pickalock.utils.AllocationGuard;
import com.gdroid.pickalock.utils.SLog;

public abstract class BaseObject extends AllocationGuard {
	
	private static final int did = SLog.register(BaseObject.class);
	static {
		SLog.setTag(did, "Base object.");
	}
	
	static SystemRegistry system;
	
	private static int nextUniqueID = 0; 
	private int uniqueID;
	
	
	public BaseObject() {
		SLog.v(did, String.format("Creating with id: %d", nextUniqueID));
		uniqueID = nextUniqueID;
		nextUniqueID++;		
	}
	
	protected void update(float timeDelta, BaseObject parent) {
		// does nothing
	}
	
	public final int getIdentifier() {
		return uniqueID;
	}
	
	public abstract void reset();
}
