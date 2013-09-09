package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.LockMechanismSystem.MoveResult;
import com.gdroid.pickalock.core.LockMechanismSystem.Type;
import com.gdroid.pickalock.core.LockMechanismSystem.MoveResult.State;
import com.gdroid.pickalock.core.positioning.AnchorVector;
import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.core.positioning.Vector3;
import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

/**
 * A component that is able to influence host positioning (without direct
 * modifying it) due to decision made by central lock mechanism it has been set
 * on.
 * 
 * @author kboom
 * 
 */
public class ActiveMechanismComponent extends LockMechanismComponent {

	private static final int did = SLog.register(ActiveMechanismComponent.class);
	static {
		SLog.setTag(did, "Active Mechanism Component.");
		SLog.setLevel(did, Level.VERBOSE);
	}
	
	private Vector mSource;
	
	public ActiveMechanismComponent(Type type, Vector source) {
		super(type);
		mSource = source;
	}
	
	@Override
	protected void update(float timeDelta, BaseObject parent) {
		
		GameObject host = (GameObject) parent;
		
		// When MOVE flag disappears we should hide those.
		if(host.checkFlag(GameObjectStatusCodes.RELEASED)) {
			SLog.d(did, "Released flag detected, hidding all flags maintained.");
			host.hideFlag(GameObjectStatusCodes.MOVE_REJECTED);
			host.hideFlag(GameObjectStatusCodes.MOVE_ACCEPTED);
			return;
		}
		
		// Lock runs all the time, lockpicks don't. Leave subclasses to decide.
		if (!checkRunConditions(host))
			return;

		
		system.lockLogic.move(getType(), mSource);
		
		super.update(timeDelta, parent);

	}

	@Override
	public void onMoveAccepted(MoveResult result) {
		
	}

	@Override
	public void onMoveRejected(MoveResult result) {
		
	}

	protected boolean checkRunConditions(GameObject host) {
		if (host.checkFlag(GameObjectStatusCodes.MOVED))
			return true;
		else {
			return false;
		}
	}

}
