package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.LockMechanismSystem.MoveResult;
import com.gdroid.pickalock.core.LockMechanismSystem.MoveResult.State;
import com.gdroid.pickalock.core.LockMechanismSystem.Type;
import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

public class PassiveMechanismComponent extends LockMechanismComponent implements LockMechanismSystem.ProgressObserver {

	private static final int did = SLog
			.register(PassiveMechanismComponent.class);
	static {
		SLog.setTag(did, "Passive Mechanism Component.");
		SLog.setLevel(did, Level.VERBOSE);
	}

	private Vector target;
	private boolean progressed;

	public PassiveMechanismComponent(Type type, Vector target) {
		super(type);
		this.target = target;
	}
	
	@Override
	protected void update(float timeDelta, BaseObject parent) {
		if(progressed) {
			super.update(timeDelta, parent);
			progressed = false;
		}
	}

	@Override
	public void onMoveAccepted(MoveResult result) {
		target.set(result.lastpos);
	}

	@Override
	public void onMoveRejected(MoveResult result) {
		// do nothing
	}

	@Override
	public void onProgress(float progress) {
		progressed = true;
	}

	@Override
	public void onSuccess() {

	}

	@Override
	public void onFailure() {

	}

}
