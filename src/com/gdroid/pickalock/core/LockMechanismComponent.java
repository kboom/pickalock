package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.LockMechanismSystem.MoveResult;
import com.gdroid.pickalock.core.LockMechanismSystem.MoveResult.State;
import com.gdroid.pickalock.core.LockMechanismSystem.Type;
import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.core.positioning.Vector3;
import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

/**
 * A component capable of communicating with LockMechanismSystem. It uses it as
 * a kind of "proxy", not being able to use Lock Mechanism directly. This
 * becomes clear when looking into Lock Mechanism System itself.
 * 
 * One host can possess multiple lock mechanisms (especially a lock - reacting
 * on each other differently, eg. sound).
 * 
 * @author kboom
 * 
 */
public abstract class LockMechanismComponent extends Component {

	private static final int did = SLog.register(LockMechanismComponent.class);
	static {
		SLog.setTag(did, "Lock mechanism component.");
		SLog.setLevel(did, Level.VERBOSE);
	}

	private final Type type;

	/**
	 * Creates an component bound to specified lock mechanism.
	 * 
	 * @param t
	 */
	public LockMechanismComponent(Type type) {
		if(type == null)
			throw new IllegalArgumentException("Type cannot be null.");
		this.type = type;
	}
	
	@Override
	protected void update(float timeDelta, BaseObject parent) {
		GameObject host = (GameObject) parent;
		
		MoveResult result = system.lockLogic.getResult(type);

		if (result.getState() == State.ACCEPTED) {
			host.hideFlag(GameObjectStatusCodes.MOVE_REJECTED);
			host.raiseFlag(GameObjectStatusCodes.MOVE_ACCEPTED);
			onMoveAccepted(result);
		} else if(result.getState() == State.REJECTED) {
			host.hideFlag(GameObjectStatusCodes.MOVE_ACCEPTED);
			host.raiseFlag(GameObjectStatusCodes.MOVE_REJECTED);
			onMoveRejected(result);
		}
	}
	
	public abstract void onMoveAccepted(MoveResult result);
	public abstract void onMoveRejected(MoveResult result);
	
	

	protected final float getProgress() {
		return system.lockLogic.getProgress();
	}
	
	public Type getType() {
		return type;
	}

	@Override
	public void reset() {

	}

	public static LockMechanismComponent getNetural(Type t) {
		return new LockMechanismComponent(t) {

			@Override
			protected void update(float timeDelta, BaseObject parent) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onMoveAccepted(MoveResult result) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onMoveRejected(MoveResult result) {
				// TODO Auto-generated method stub
				
			}

		};
	}

}
